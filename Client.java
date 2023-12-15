package network_project;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    static final String Message = "message";
    static final String Request = "request";
    static String serverName = "localhost";

    public static void main(String[] args) {
        System.out.println("\u001B[34mWelcome to 2023_group_36's network system\u001B[0m");
        System.out.println("\r\n\r\n");
        Scanner scanner = new Scanner(System.in);
        boolean suc = true;
        int port = 1234;
        try {
            Socket client = new Socket(serverName, port);
            DataOutputStream out = new DataOutputStream(client.getOutputStream());
            DataInputStream in = new DataInputStream(client.getInputStream());
            int connect_count = 0;
            System.out.println("�µ������ѽ���: " + connect_count++);
            FileList fileList = FileList.getFileList();
            boolean log = false;
            while (!log) {
                System.out.println("��¼��1 ע�᣺2 �˳���3");
                String command = scanner.nextLine();
                if (command.equals("3")) {
                    out.writeUTF("end");
                    System.out.println("�ͻ��˹ر�");
                    client.close();
                    suc = false;
                    break;
                } else if (!(command.equals("1") || command.equals("2"))) {
                    System.out.println("�������������");
                } else {
                    System.out.println("�������û���");
                    String name = scanner.nextLine();
                    System.out.println("����������");
                    String password = scanner.nextLine();
                    if (command.equals("1"))
                        out.writeUTF("login");
                    else
                        out.writeUTF("register");
                    out.writeUTF(name);
                    out.writeUTF(password);
                    Segment segment = new Segment(in);
                    segment.print_Response_segment();
                    if (command.equals("1") && segment.getStatus().equals("200"))
                        suc = log = true;
                    segment.print_content();
                }
            }
            boolean conn;
            while (true) {
                System.out.println("�Ƿ�Ҫ���������ӣ� Y/N");
                String str = scanner.nextLine();
                if (str.equals("Y")) {
                    out.writeUTF("K");
                    conn = true;
                    break;
                } else if (str.equals("N")) {
                    out.writeUTF("C");
                    conn = false;
                    break;
                } else System.out.println("����������");
            }
            boolean reGet = false;
            String lastFile = "";
            while (suc) {
                if (!conn) {
                    client.close();
                    client = new Socket(serverName, port);
                    out = new DataOutputStream(client.getOutputStream());
                    in = new DataInputStream(client.getInputStream());
                    System.out.println("\u001B[32m�µ������ѽ���: " + connect_count++ + "\u001B[0m");
                }
                String command, file_name;
                if (!reGet) {
                    System.out.println("�ϴ��ļ���p �����ļ���g ������e");
                    command = scanner.nextLine();
                } else
                    command = "g";

                if (command.equals("e")) {
                    out.writeUTF("end");
                    suc = false;
                    client.close();
                    System.out.println("�ͻ��˹ر�");
                } else if (command.equals("ERROR")) {
                    out.writeUTF(command);
                    Segment segment = new Segment(in);
                    segment.print_Response_segment();
                    System.out.println("\u001B[31m����������δ֪����\u001B[0m");
                    client.close();
                    break;
                } else {
                    if (!reGet) {
                        System.out.println("�������ļ�����");
                        file_name = scanner.nextLine();
                    } else {
                        file_name = lastFile;
                        reGet = false;
                    }
                    if (command.equals("p")) {
                        try {
                            File file = new File(file_name);
                            FileInputStream fileInputStream = new FileInputStream(file);
                            long len = file.length();
                            byte[] fileData = new byte[(int) len];
                            fileInputStream.read(fileData);
                            out.writeUTF("Post");
                            out.writeUTF(file_name);
                            out.writeUTF(String.valueOf(len));
                            out.write(fileData);
                            Segment segment = new Segment(in);
                            segment.print_Response_segment();


                        } catch (FileNotFoundException e) {
                            System.out.println("�ļ��ڿͻ��˲�����");
                        }
                    } else if (command.equals("g")) {
                        out.writeUTF("Get");
                        out.writeUTF(file_name);
                        Segment segment = new Segment(in);
                        if (segment.getStatus().equals("200") || segment.getStatus().equals("304")) {
                            int len = Integer.valueOf(in.readUTF());
                            segment.setFile_length(len);
                            segment.print_Response_segment();
                            if (segment.getStatus().equals("304"))
                                System.out.println("\u001B[32m�ļ�δ���޸ģ������ӻ����ж�ȡ\u001B[0m");
                            fileList.getFile(file_name, in, -1, len);
                        } else if (segment.getStatus().equals("301") || segment.getStatus().equals("302")) {
                            reGet = true;
                            lastFile = segment.getLocation();
                            segment.print_Response_segment();
                            System.out.println("\u001B[31m�㽫��" + segment.getStatus() + "�ض�����" + lastFile + "\u001B[0m");
                        } else {
                            segment.print_Response_segment();
                            System.out.println("\u001B[31m�ļ��ڷ������в�����\u001B[0m");
                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
