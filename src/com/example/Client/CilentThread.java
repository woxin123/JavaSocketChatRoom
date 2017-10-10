package com.example.Client;

import java.io.BufferedReader;
import java.io.IOException;

public class CilentThread extends Thread {

    // 该客户端程序负责处理的输入流
    BufferedReader br = null;

    public CilentThread(BufferedReader br) {
        this.br = br;
    }

    @Override
    public void run() {
        try {
            String content = null;
            while ((content = br.readLine()) != null) {
                System.out.println(content);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
