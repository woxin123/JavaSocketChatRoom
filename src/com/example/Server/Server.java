package com.example.Server;



import com.example.utils.MyMap;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final int SERVER_PORT = 30000;
    // 使用MyMap对象来保存每个客户端名字和对应输出流之间的关系
    public static MyMap<String, PrintStream> cilent = new MyMap<>();
    public void init() {
        try (
                // 建立监听的ServerSocket
                ServerSocket ss = new ServerSocket(SERVER_PORT)) {
            while (true) {
                Socket s = ss.accept();
                new Thread(new ServerThread(s)).start();
            }
        }
        catch (IOException e) {
            System.out.println("服务器启动失败，是否端口" + SERVER_PORT + "已被占用？");
        }
    }
    public static void main(String[] args) {
        Server server = new Server();
        server.init();
    }
}
