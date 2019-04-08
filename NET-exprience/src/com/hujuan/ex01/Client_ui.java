/**
 * 
 */
package com.hujuan.ex01;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 * @author hujuan
 *
 */
public class Client_ui {

	private JFrame frame;
	private JTextField iptextField;
	private JLabel lblPort;
	private JTextField textField_port;
	private JTextField textField_send;
	private JButton btnSend;
	private DataInputStream dis;
	private DataOutputStream dos;
	private JTextArea textArea_record;
	private boolean isConnect = false;

	private String host = "localhost";
	private int port = 8000;
	private Socket socket;
	private Client ct;

	public Client_ui() throws UnknownHostException, IOException {
		try {
			socket = new Socket(host, port);
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());

			ct = new Client();
			ct.start();

			if (isConnect == false) {
				dos.writeUTF("<" + socket.getLocalAddress() + ":" + socket.getLocalPort() + ">" + " 进入了聊天室");
				isConnect = true;
			}

			isConnect = true;
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "服务器未开启", "错误", JOptionPane.ERROR_MESSAGE);
			e1.printStackTrace();
		}

		initialize();

		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		textField_send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

	}

	/**
	 * Launch the application.
	 * 
	 * @throws IOException
	 * @throws UnknownHostException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client_ui window = new Client_ui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		// Client_ui window = new Client_ui();
		// window.frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("client");
		frame.setBounds(100, 100, 450, 350);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lblNewLabel = new JLabel("IPAddress");
		lblNewLabel.setBounds(22, 12, 63, 13);

		iptextField = new JTextField();
		iptextField.setBounds(103, 6, 97, 25);
		iptextField.setColumns(10);
		try {
			iptextField.setText((InetAddress.getLocalHost()).getHostAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
		iptextField.setEditable(false);
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(lblNewLabel);

		frame.getContentPane().add(iptextField);

		lblPort = new JLabel("port");
		lblPort.setBounds(24, 49, 63, 13);
		frame.getContentPane().add(lblPort);

		textField_port = new JTextField();
		textField_port.setBounds(105, 43, 95, 25);
		textField_port.setEditable(false);
		textField_port.setText((socket.getLocalPort()) + "");
		frame.getContentPane().add(textField_port);
		textField_port.setColumns(10);

		btnSend = new JButton("send");
		btnSend.setBounds(129, 240, 104, 23);

		frame.getContentPane().add(btnSend);

		textArea_record = new JTextArea();
		textArea_record.setBounds(243, 30, 185, 233);
		textArea_record.append("");
		textArea_record.setEditable(false);
		frame.getContentPane().add(textArea_record);

		textField_send = new JTextField();
		textField_send.setBounds(22, 94, 211, 143);
		frame.getContentPane().add(textField_send);

		JLabel label = new JLabel("message");
		label.setBounds(259, 12, 110, 13);
		frame.getContentPane().add(label);

		JLabel label_1 = new JLabel("发送消息");
		label_1.setBounds(22, 74, 63, 13);
		frame.getContentPane().add(label_1);
		closing();
	}

	public class Client extends Thread {

		@Override
		public void run() {
			String message = null;
			while (true) {
				try {
					message = dis.readUTF();
					textArea_record.append("\n");
					textArea_record.append(message);
					textArea_record.setCaretPosition(textArea_record.getDocument().getLength());
					
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					if (isConnect == true)
					{
						JOptionPane.showMessageDialog(null, "服务器断开连接", "错误", JOptionPane.ERROR_MESSAGE);
					}
					isConnect = false;
					try {
						dis.close();
						dos.close();
						socket.close();
						ct.stop();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					e.printStackTrace();
				} catch (NullPointerException e) {
					System.out.println("客户端收不到消息");
					e.printStackTrace();
				}
			}
		}
	}

	public void send() {
		if (isConnect == false) {
			JOptionPane.showMessageDialog(null, "未连接服务器，无法发送消息", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (textField_send.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "消息不能为空", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String str = textField_send.getText();
		sendMessage(str);
	}

	public void sendMessage(String str) {
		try {
			dos.writeUTF("<" + socket.getLocalAddress() + ":" + socket.getLocalPort() + ">:" + str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		textArea_record.append("\n");
		textArea_record.append("我:" + str);
		textArea_record.setCaretPosition(textArea_record.getDocument().getLength());
		textField_send.setText("");
	}

	public void closing() {
		this.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (isConnect == true) {
					try {
						dos.writeUTF("<" + socket.getLocalAddress() + ":" + socket.getLocalPort() + ">" + " 离开了聊天室");
						dis.close();
						dos.close();
						socket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}
}