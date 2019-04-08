/**
 * 
 */
package com.hujuan.chapter2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author hujuan
 *
 */
public class SocketHandler_Test implements Runnable {
	private Socket socket;

	public SocketHandler_Test(Socket socket) {
		this.socket = socket;
	}

	public String echo(String msg) {
		return "server:" + msg;
	}

	private PrintWriter getWriter(Socket socket) throws IOException {
		OutputStream socketOut = socket.getOutputStream();
		return new PrintWriter(socketOut, true);
	}

	private BufferedReader getReader(Socket socket) throws IOException {
		InputStream socketIn = socket.getInputStream();
		return new BufferedReader(new InputStreamReader(socketIn));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.out.println("New connection accepted" + socket.getInetAddress() + ":" + socket.getPort());
			BufferedReader br = getReader(socket);
			PrintWriter pw = getWriter(socket);

			String msg = null;
			while ((msg = br.readLine()) != null) {
				System.out.println("<" + socket.getInetAddress() + ":" + socket.getPort()+ ">:" + msg);
				pw.println(echo(msg));
				if (msg.equals("bye")){
					break;
				}
				
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
