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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListModel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JTextArea;

/**
 * @author hujuan
 *
 */
public class Server_ui {

	private JFrame frame;
	private JTextField textField_ip;
	private JTextField textField_port;
	private JTextField textField_send;
	private JTextArea textArea;
	private JButton btnSend;
	private JList userlist;

	private Vector<Socket> lists = new Vector<Socket>();
	private Vector<Socket> selected_list;
	private Server st;
	private DataOutputStream dos;
	private DataInputStream dis;
	private Vector<String> users = new Vector<String>();

	private int usersLength = 0;
	private UserList ul;
	private boolean isStart = false;
	private int port = 8000;
	private ServerSocket serverSocket;

	/**
	 * Create the application.
	 */
	public Server_ui() {

		initialize();
		try {
			if (isStart == false) {
				serverSocket = new ServerSocket(port);
				textArea.append("\n");
				textArea.append("服务器已开启");

				st = new Server(serverSocket);
				st.start();

				isStart = true;
				
//				userlist.setListData(lists);
				ul = new UserList();
				ul.start();
			}
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		// 设置send按钮事件
		btnSend.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (lists.size() == 0) {
					JOptionPane.showMessageDialog(null, "当前没有用户在线", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (textField_send.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "消息不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String line = "服务器：" + textField_send.getText();
				sendSelcetedMessage(line);

				textField_send.setText("");
			}
		});

		// userlist.addListSelectionListener(arg0);
		userlist.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// 获取所有被选中的选项索引
				int[] indices = userlist.getSelectedIndices();
				// 获取选项数据的 ListModel
				ListModel<Socket> listModel = userlist.getModel();
				// 输出选中的选项
				selected_list = new Vector<Socket>();
				for (int index : indices) {
					selected_list.add(listModel.getElementAt(index));
				}
				System.out.println();
			}
		});

		textField_send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (lists.size() == 0) {
					JOptionPane.showMessageDialog(null, "当前没有用户在线", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (textField_send.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "消息不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String line = "服务器：" + textField_send.getText();
				sendSelcetedMessage(line);

				textField_send.setText("");

			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("server");
		frame.setBounds(500, 500, 450, 360);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JLabel lblIpaddress = new JLabel("IPAddress");

		textField_ip = new JTextField();
		textField_ip.setColumns(10);
		try {
			textField_ip.setText((InetAddress.getLocalHost()).getHostAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
		textField_ip.setEditable(false);

		JLabel lblPort = new JLabel("Port");

		textField_port = new JTextField();
		textField_port.setColumns(10);
		textField_port.setText(port + "");
		textField_port.setEditable(false);

		userlist = new JList(lists);

		textField_send = new JTextField();
		textField_send.setColumns(10);

		JLabel lblChooseClient = new JLabel("choose Client");

		JLabel lblSendMessage = new JLabel("send message");

		btnSend = new JButton("send");

		textArea = new JTextArea();

		JLabel lblMessage = new JLabel("message");
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblChooseClient)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(lblIpaddress)
										.addComponent(lblPort))
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
										.addComponent(textField_port)
										.addComponent(textField_ip, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)))
								.addComponent(btnSend, Alignment.TRAILING)
								.addComponent(userlist, GroupLayout.PREFERRED_SIZE, 260, GroupLayout.PREFERRED_SIZE)
								.addComponent(textField_send, GroupLayout.PREFERRED_SIZE, 266, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(lblMessage)
								.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 197, GroupLayout.PREFERRED_SIZE)))
						.addComponent(lblSendMessage))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblIpaddress)
								.addComponent(textField_ip, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblPort)
								.addComponent(textField_port, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGap(9)
							.addComponent(lblChooseClient)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(userlist, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(lblSendMessage)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textField_send, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(lblMessage)
							.addGap(2)
							.addComponent(textArea, GroupLayout.PREFERRED_SIZE, 201, GroupLayout.PREFERRED_SIZE)))
					.addPreferredGap(ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
					.addComponent(btnSend)
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
	}

	/* ！！！！这里是群发，，后期需要改正 */
	public void sendMessage(String str) {
		try {
			for (int i = 0; i < lists.size(); i++) {
				dos = new DataOutputStream(lists.get(i).getOutputStream());
				dos.writeUTF(str);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void sendSelcetedMessage(String str) {
		try {
			if(selected_list==null){
				JOptionPane.showMessageDialog(null, "没有选择发送用户", "错误", JOptionPane.ERROR_MESSAGE);
			}
			for (int i = 0; i < selected_list.size(); i++) {
				dos = new DataOutputStream(selected_list.get(i).getOutputStream());
				dos.writeUTF(str);
			}
			textArea.append("\n");
			textArea.append(str);
			textArea.setCaretPosition(textArea.getDocument().getLength());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}

	public class Server extends Thread {
		private ServerSocket serverSocket;

		public Server(ServerSocket serverSocket) {
			this.serverSocket = serverSocket;
		}

		@Override
		public void run() {
			while (true) {
				Socket socket = null;
				try {
					socket = serverSocket.accept();
					new Thread(new Server_Handler(socket)).start();//接收到一个客户端的Socket就分配一个线程
					lists.add(socket); 
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class Server_Handler implements Runnable {

		private Socket socket;
		private DataInputStream dis;

		public Server_Handler(Socket socket) {

			try {
				this.socket = socket;
				dis = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String message = null;
			try {
				while (true) {
					message = dis.readUTF();
					
					textArea.append("\n");
					textArea.append(message);
					textArea.setCaretPosition(textArea.getDocument().getLength());
					
					if (message.contains("进入了聊天室") && !message.contains("：")) {
						users.add(message.split(" ")[0]);
					}
					if (message.contains("离开了聊天室") && !message.contains("：")) {
						users.remove(message.split(" ")[0]);
//						sendMessage(message);
						dis.close();
						socket.close();
						lists.remove(socket);
					} else {
//						sendMessage(message);
					}
				}
			} catch (IOException e) {
				lists.remove(socket);
				e.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	public class UserList extends Thread {

		@Override
		public void run() {
			while (true) {
				if (users.size() != usersLength) {	/////!不能删掉这个if
					userlist.setListData(lists);
					usersLength = users.size();

				}
			}
		}
	}

	public void closing() {
		this.frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					if (isStart == true) {
						if (dis != null) {
							dis.close();
						}
						if (dos != null) {
							dos.close();
						}
						serverSocket.close();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server_ui window = new Server_ui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}