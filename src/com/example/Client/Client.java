package com.example.Client;

import com.example.utils.Protocol;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private static final int SERVER_PORT = 30000;
    private Socket socket;
    private PrintStream ps;
    private BufferedReader brServer;
    private BufferedReader keyIn;
    public void init() {
        try {
            // 初始化键盘输入流
            keyIn = new BufferedReader(new InputStreamReader(System.in));
            // 连接到服务器
            socket = new Socket("127.0.0.1", SERVER_PORT);
            // 获取该服务器对应的输出流和输入流
            ps = new PrintStream(socket.getOutputStream());
            brServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String tip = "";
            // 采用循环不断的弹出对话框要求输入用户名
            while (true) {
                String userName = JOptionPane.showInputDialog(tip + "输入用户名");
                // 在输入用户名之后在其后增加协议字符串之后发送
                ps.println(Protocol.USER_ROUND + userName + Protocol.USER_ROUND);
                // 读取服务器的响应
                String result = brServer.readLine();
                // 如果用户名重复则开始下次循环
                if (result.equals(Protocol.NAME_REP)) {
                    tip = "用户名重复，请重新输入";
                    continue;
                }
                // 如果服务器返回登录成功，则结束循环
                if (result.equals(Protocol.LOGIN_SUCCESS)) {
                    ps.println(Protocol.MSG_ROUND + userName + "上线了！" + Protocol.MSG_ROUND);
                    break;
                }
            }
        }
        catch (UnknownHostException uhe) {
            System.out.println("找不到服务器，请确定服务器已经启动！");
            closeRs();
            System.exit(1);
        }
        catch (IOException e) {
            System.out.println("网络异常！请重新登录！");
            closeRs();
            System.exit(1);
        }
        new CilentThread(brServer).start();
    }

    // 定义一个读取键盘输入，并向网络发送的方法
    private void readAndSend() {
        try {
            // 不断的从键盘输入
            String line = null;
            while ((line = keyIn.readLine()) != null) {
                // 如果发送的消息中有冒号，且以// 开头，则认为向发送私聊消息
                if (line.indexOf(":") > 0 && line.startsWith("//")) {
                    line = line.substring(2);
                    ps.println(Protocol.PRIVATE_ROUND + line.split(":")[0]
                            + Protocol.SPLIT_SIGN + line.split(":")[1]
                            + Protocol.PRIVATE_ROUND);
                }
                else if (line.equals("好友列表") || line == "好友列表") {
                    ps.println(Protocol.LIST_ONLINE + line + Protocol.LIST_ONLINE);
                }
                else{
                    ps.println(Protocol.MSG_ROUND + line + Protocol.MSG_ROUND);
                }
            }
        }
        catch (IOException ioe) {
            System.out.println("网络通信出现了异常！请重新登录！");
            closeRs();
            System.exit(1);
        }
    }

    // 关闭Socket、输出流、输入流方法
    private void closeRs() {
        try {
            if (keyIn != null) {
                keyIn.close();
            }
            if (brServer != null) {
                brServer.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.init();
        client.readAndSend();
    }
}
