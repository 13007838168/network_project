package network_project;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileList {
    public static Map FileMap = new HashMap<String, MyFile>();
    private static int time = 1;

    public static int getTime() {
        return time++;
    }

    static String get_File_Type(String name) {
        String[] dot = name.split("\\.");
        return "." + dot[dot.length - 1];
    }

    public void getFile(String filename, DataInputStream in, int time, int len) {
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            int sum = 0;
            byte[] bytes = new byte[len];
            while (sum < len)
                sum += in.read(bytes, sum, len - sum);
            outputStream.write(bytes);
            outputStream.close();
            if (time > 0) {
                MyFile myFile = new MyFile();
                myFile.type = get_File_Type(filename);
                myFile.segment_type = "200";
                myFile.last_modified = time;
                myFile.is_modified = true;
                FileMap.put(filename, myFile);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No such File");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    FileList() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File("information.csv")));
            String content = reader.readLine();
            while ((content = reader.readLine()) != null) {
                String[] list = content.split(",");
                MyFile myFile = new MyFile();
                myFile.type = list[1];
                myFile.location = list[2];
                myFile.segment_type = list[3];
                FileMap.put(list[0], myFile);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No such file");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static FileList fileList = new FileList();

    public static FileList getFileList() {
        return fileList;
    }

    public MyFile getFileList(String filename) {
        return (MyFile) FileMap.get(filename);
    }

    public void setFileValue(String filename) {
        MyFile myFile = getFileList(filename);
        myFile.is_modified = false;
        myFile.segment_type = "304";
        FileMap.put(filename, myFile);
        getTime();
    }
}
