package network_project;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Segment {
    static final Map<String, String> StatusSet = new HashMap<String, String>() {{
        put("200", "200 OK");
        put("301", "301 Moved Permanently");
        put("302", "302 Found");
        put("304", "304 Not Modified");
        put("404", "404 Not Found");
        put("405", "405 Method Not Allowed");
        put("500", "500 Internal Server Error");
    }};
    static final Map<String, String> FileType = new HashMap<String, String>() {{
        put(".txt", "text/plain");
        put(".csv", "text/plain");
        put(".html", "text/html");
        put(".png", "image/png");
        put("login", "application/x-www-form-urlencoded");
        put("register", "application/x-www-form-urlencoded");
    }};
    static final Map<String, String> ContentType = new HashMap<String, String>() {{
        put("200", "登录成功");
        put("201", "注册成功");
        put("202", "用户名已被注册");
        put("203", "用户名错误");
        put("204", "密码错误");
    }};


    Segment(DataInputStream in) throws IOException {
        String[] strings = in.readUTF().split(newline);
        status = strings[0];
        location = strings[1];
        content_Type = strings[2];
        lastModified = Integer.valueOf(strings[3]);
        conn_Type = strings[4];
        content = strings[5];
        content_length=content.length();
    }

    Segment(String Content_Type, String Conn_Type, String Status, String Location, String Content) {
        content_Type = FileType.get(Content_Type);
        conn_Type = Conn_Type;
        status = StatusSet.get(Status);
        location = Location;
        content = Content;
        content_length=Content.length();
    }

    public static String Post = "POST /";
    public static String Get = "GET  /";
    public static String Keep = "Keep-Alive";
    public static String Close = "Close";
    public static String Null = "null";
    private static int time = 0;

    private int content_length = 0;
    private int file_length=0;


    private String content_Type;
    private String conn_Type;
    private String status;
    private String location;
    private int lastModified = -1;
    private String content;
    String newline = System.lineSeparator();



    public void print_Response_segment() {
        System.out.println("\u001B[34m====>> RECEIVING MASSAGE <<====");
        System.out.println("---->> header <<----\u001B[0m");
        System.out.println("HTTP/1.1 " + status);
        System.out.println("Server:2023-Group36-HttpServer");
        if (!location.equals(Null)) System.out.println("Location:" + location);
        content_length = content.length()+file_length;
        System.out.println("Content-Length:" + content_length);
        System.out.println("Content-Type:" + content_Type);
        if (lastModified > -1) System.out.println("Last-Modified:" + lastModified);
        System.out.println("Connection:" + conn_Type);
        System.out.println();
    }

    public void print_content() {
        System.out.println(ContentType.get(content.substring(0, 3)));
    }

    public void print_Request_segment(String com) {
        System.out.println("\u001B[34m====>> send response <<====");
        System.out.println("---->> response sended <<----\u001B[0m");
        System.out.println("request is:");
        System.out.println(com + " HTTP/1.1");
        System.out.println("Accept:*/*");
        System.out.println("Accept-language:zh-cn");
        System.out.println("User-Agent:2023-Group36-HttpClient");
        System.out.println("Host:192.168.1.1:1234");
        System.out.println("Connection:" + conn_Type);
        if (conn_Type.equals("Keep-Alive")) System.out.println("Keep-Alive:timeout=120");
        content_length = content.length()+file_length;
        if (com.substring(0, 6).equals(Post)) {
            System.out.println("Content-Type:" + content_Type);
            System.out.println("Content-Length:" + content_length);
        }
        System.out.println();
    }

    public void send(DataOutputStream out) throws IOException {
        String[] strings = {status, location, content_Type, String.valueOf(lastModified), conn_Type, content};
        String result = String.join(newline, strings);
        out.writeUTF(result);
    }
    public void send(int len,DataOutputStream out,byte[]data) throws IOException {
        out.writeUTF(String.valueOf(len));
        out.write(data);
    }

    public void setContent(String s) {
        content = s;
    }

    public void setStatus(String s) {
        status = StatusSet.get(s);
    }

    public void setFile_length(int len) {
        file_length = len;
    }
    public void setLastModified(int n){lastModified=n;}
    String getStatus() {
        return status.substring(0,3);
    }

    String getContent() {
        return content;
    }
    String getLocation(){return location;}

}
