package com.example.Server;



import com.example.utils.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ServerThread extends Thread {

    private Socket socket;
    BufferedReader br = null;
    PrintStream ps = null;
    // 定义一个构造器，用于接受一个Socket来创建ServerThread线程
    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // 获取Socket对应的输入流
            br = new BufferedReader(new
                    InputStreamReader(socket.getInputStream()));
            // 获取Socket对应的输出流
            ps = new PrintStream(socket.getOutputStream());
            String line = null;
            while ((line = br.readLine()) != null) {
                // 如果读到的行是以Protocol.USER_ROUND开始，并以其结束
                // 则确定的读到的是用户登录的用户名
                if (line.startsWith(Protocol.USER_ROUND) &&
                        line.endsWith(Protocol.USER_ROUND)) {
                    // 得到真实消息
                    String userName = getRealMsg(line);
                    // 如果用户名重复
                    if (Server.cilent.map.containsKey(userName)) {
                        System.out.println("用户名重复");
                        ps.println(Protocol.NAME_REP);
                    }
                    else {
                        System.out.println("成功");
                        ps.println(Protocol.LOGIN_SUCCESS);
                        Server.cilent.put(userName, ps);
                    }
                }
                // 如果读到的行以Protocol.PRIVATE_ROUND开始并以其结束，
                // 则可以确定是私聊信息，私聊信息只向定向的输出流发送
                else if (line.startsWith(Protocol.PRIVATE_ROUND)
                        && line.endsWith(Protocol.PRIVATE_ROUND)) {
                    // 得到真实消息
                    String userAnduser = getRealMsg(line);
                    // 以SPLIT_SIGN分割字符串，前半是私聊用户，后半是聊天信息
                    String user = userAnduser.split(Protocol.SPLIT_SIGN)[0];
                    String msg = userAnduser.split(Protocol.SPLIT_SIGN)[1];
                    // 获取私聊用户的对应的输出流，并发送私聊消息
                    Server.cilent.map.get(user).println(Server.cilent.getKeyByValue(ps)
                            + "悄悄地对你说：" + msg);
                }
                else if (line.startsWith(Protocol.LIST_ONLINE)
                        && line.endsWith(Protocol.LIST_ONLINE)) {
                    ps.println("所有在线的好友为：");
                    for (String name : Server.cilent.keySet()) {
                        ps.println(name);
                    }
                }
                else {
                    // 得到真实的消息
                    String msg = getRealMsg(line);
                    // 遍历clients中的每个输出流
                    for (PrintStream cilentsPs : Server.cilent.valueSet()) {
                        if (cilentsPs.equals(ps)) {
                            continue;
                        }
                        cilentsPs.println(Server.cilent.getKeyByValue(ps) + "说：" + msg);
                    }
                }
            }
        }
        catch (IOException e) {
            for (PrintStream cilentsPs : Server.cilent.valueSet()) {
                if (cilentsPs.equals(ps)) {
                    break;
                }
                cilentsPs.println(Server.cilent.getKeyByValue(ps) + "说：" + "我下线了");
            }
            Server.cilent.removeByValue(ps);
            System.out.println(Server.cilent.map.size());
            // 关闭网络，IO流
            try {
                if (br != null) {
                    br.close();
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
    }

    // 将得到的数据内容去掉前后的协议字符，恢复成真实数据
    private String getRealMsg(String line) {
        return line.substring(Protocol.PROTOCOL_LEN,
                line.length() - Protocol.PROTOCOL_LEN);
    }
}
