package network_project;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class Server {


    public static void main(String[] args) throws IOException {
        System.out.println("\u001B[34mWelcome to 2023_group_36's network system\u001B[0m");
        System.out.println("\r\n\r\n");
        String conn = "Keep-Alive";
        boolean suc = true;
        int port = 1234, time = 0;
        Map<String, String> dic = new HashMap<String, String>();
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket server = serverSocket.accept();
            DataInputStream in = new DataInputStream(server.getInputStream());
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            System.out.println("连接已建立");
            boolean log = false;
            while (!log) {
                FileList.getTime();
                String command = in.readUTF();
                if (command.equals("end")) {
                    System.out.println("");
                    server.close();
                    serverSocket.close();
                    suc = false;
                    System.out.println("服务器关闭");
                    break;
                } else {
                    String name = in.readUTF(), password = in.readUTF();
                    Segment segment = new Segment(".html", conn, "405", Segment.Null, Segment.Null);
                    String cont = command + name + password;
                    if (command.equals("register")) {
                        if (dic.containsKey(name))
                            segment.setContent("202" + cont);
                        else {
                            dic.put(name, password);
                            segment.setStatus("200");
                            segment.setContent("201" + cont);
                        }
                    } else if (dic.containsKey(name)) {
                        if (dic.get(name).equals(password)) {
                            segment.setStatus("200");
                            segment.setContent("200" + cont);
                            suc = log = true;
                        } else
                            segment.setContent("204" + cont);
                    } else
                        segment.setContent("203" + cont);
                    segment.send(out);
                    segment = new Segment(command, conn, Segment.Null, Segment.Null, cont);
                    segment.print_Request_segment(Segment.Post + command);
                }
            }
            String str = in.readUTF();
            conn = str.equals("K") ? Segment.Keep : Segment.Close;
            while (suc) {
                time++;
                if (conn.equals(Segment.Close)) {
                    serverSocket.close();
                    serverSocket = new ServerSocket(port);
                    server = serverSocket.accept();
                    in = new DataInputStream(server.getInputStream());
                    out = new DataOutputStream(server.getOutputStream());
                    System.out.println("新的连接已建立");
                }
                FileList fileList = FileList.getFileList();
                String command = in.readUTF();
                if (command.equals("end")) {
                    server.close();
                    serverSocket.close();
                    System.out.println("服务器关闭");
                    suc = false;
                } else if (command.equals("ERROR")) {
                    Segment segment = new Segment("", conn, "500", Segment.Null, Segment.Null);
                    segment.send(out);
                    server.close();
                    serverSocket.close();
                    suc = false;
                    System.out.println("系统崩溃");
                } else {
                    String file_name = "S" + in.readUTF();
                    if (command.equals("Post")) {
                        int len = Integer.valueOf(in.readUTF());
                        fileList.getFile(file_name, in, FileList.getTime(), len);
                        Segment segment = new Segment(FileList.get_File_Type(file_name), conn, "200", Segment.Null, Segment.Null);
                        segment.send(out);
                        segment = new Segment(FileList.get_File_Type(file_name), conn, Segment.Null, Segment.Null, Segment.Null);
                        segment.setFile_length(len);
                        segment.print_Request_segment(Segment.Post + file_name);

                    } else if (command.equals("Get")) {
                        MyFile myFile = fileList.getFileList(file_name);
                        Segment segment;
                        if (myFile == null) {
                            {
                                segment = new Segment(FileList.get_File_Type(file_name), conn, "404", Segment.Null, Segment.Null);
                                segment.send(out);
                            }
                        } else if (myFile.segment_type.equals("301") || myFile.segment_type.equals("302")) {
                            {
                                segment = new Segment(FileList.get_File_Type(file_name), conn, myFile.segment_type, myFile.location, Segment.Null);
                                segment.send(out);
                            }
                        } else {
                            try {
                                File file = new File(file_name);
                                FileInputStream fileInputStream = new FileInputStream(file);
                                long len = file.length();
                                byte[] fileData = new byte[(int) len];
                                fileInputStream.read(fileData);
                                String status = myFile.is_modified ? "200" : "304";
                                fileList.setFileValue(file_name);
                                segment = new Segment(FileList.get_File_Type(file_name), conn, status, Segment.Null, Segment.Null);
                                segment.setLastModified(myFile.last_modified);
                                segment.send(out);
                                segment.send((int) len, out, fileData);
                            } catch (FileNotFoundException e) {
                                System.out.println("文件不存在");
                            }
                        }
                        segment = new Segment(FileList.get_File_Type(file_name), conn, Segment.Null, Segment.Null, Segment.Null);
                        segment.print_Request_segment(Segment.Get + file_name);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}