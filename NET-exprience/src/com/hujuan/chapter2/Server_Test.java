/**
 * 
 */
package com.hujuan.chapter2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author hujuan
 *
 */
public class Server_Test {
	private int port = 8000;
	private ServerSocket serverSocket;

	public Server_Test() throws IOException {
		serverSocket = new ServerSocket(port);
		System.out.println("服务器启动");
	}

	public void service() {
		while (true) {
			Socket socket = null;
			try {
				socket = serverSocket.accept();
				Thread workThread = new Thread(new SocketHandler_Test(socket));
				workThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		new Server_Test().service();
	}

}
