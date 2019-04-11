package com.hujuan.ex02;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ChatRoomClientFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7909370607161820239L;

	private JPanel panel;
	private JPanel northPanel;
	private JPanel southPanel;
	private JTextField port;
	private JTextField IP;
	private JTextField userName;
	private JList userlist;
	private JTextArea chatRecords;
	private JScrollPane leftPanel;
	private JScrollPane rightPanel;
	private JSplitPane centerPanel;
	private JTextField message;
	private JButton link;
	private JButton discon;
	private JButton send;
	private JPanel Jpanel;

	private boolean isConnect = false;
	private boolean isconnect = false;
	private Socket socket;
	private DataInputStream dis;
	private DataOutputStream dos;
	private DataOutputStream ddos;
	private ClientThread ct;

	private Vector<String> users = new Vector<>();
	private int usersLength = 0;

	// 用作监听的服务端口
	private ServerSocket serverSocket;
	private ServerThread st;
	private boolean isStart = false;
	private JTextField textField_serverport;
	private JLabel label;
	private UserList ul;
	private Vector<Socket> llist = new Vector<>();
	private Vector<Integer> portlists = new Vector<Integer>();
	private Vector<Integer> selected_portlist;

	public ChatRoomClientFrame() {

		initServer();

		link.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (userName.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "用户名不能为空", "错误", JOptionPane.ERROR_MESSAGE);
				}
				if (textField_serverport.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "服务端口不能为空", "错误", JOptionPane.ERROR_MESSAGE);
				} else {

					try {
						socket = new Socket("localhost", 8088);
						dos = new DataOutputStream(socket.getOutputStream());
						dis = new DataInputStream(socket.getInputStream());

						if (socket != null) {
							port.setText(socket.getLocalPort() + "");

							try {
								IP.setText((InetAddress.getLocalHost()).getHostAddress());
							} catch (Exception ex) {
								ex.printStackTrace();
							}

							userName.setEditable(false);
							link.setEnabled(false);
							discon.setEnabled(true);

							ct = new ClientThread(socket);
							ct.start();

							if (isConnect == false) {
								dos.writeUTF(textField_serverport.getText() + " 服务端口");
								dos.writeUTF(userName.getText() + " 进入了聊天室");
								// 把服务端口传给服务器

								ul = new UserList();
								ul.start();

								isConnect = true;
							}

							if (isStart == false) {
								serverSocket = new ServerSocket(Integer.parseInt(textField_serverport.getText()));

								st = new ServerThread(serverSocket);
								st.start();

								isStart = true;
							}

						}
					} catch (NumberFormatException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (UnknownHostException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(null, "服务器未开启", "错误", JOptionPane.ERROR_MESSAGE);
						e1.printStackTrace();
					}

				}
			}
		});

		userlist.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// 获取所有被选中的选项索引
				int[] indices = userlist.getSelectedIndices();
				// 获取选项数据的 ListModel
				// ListModel<Socket> listModel = userlist.getModel();
				// 输出选中的选项
				selected_portlist = new Vector<Integer>();
				for (int index : indices) {
					selected_portlist.add(portlists.get(index));
				}
			}
		});

		discon.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (isConnect == true) {
						isConnect = false;
						dos.writeUTF(userName.getText() + " 离开了聊天室");
						// userlist.setText("");
						// userstr = null;
						usersLength = 0;
						dos.close();
						dis.close();
						socket.close();
						chatRecords.append("\n");
						chatRecords.append("你断开了服务器");
						chatRecords.setCaretPosition(chatRecords.getDocument().getLength());
						port.setEditable(true);
						IP.setEditable(true);
						userName.setEditable(true);
						link.setEnabled(true);
						discon.setEnabled(false);
						ct.stop();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		message.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				send();
			}
		});

		initFrame();
	}

	private void initServer() {
		Jpanel = new JPanel();
		Jpanel.setLayout(new BorderLayout());
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		northPanel = new JPanel();
		northPanel.setBorder(new TitledBorder("连接信息"));

		port = new JTextField(8);
		// port.setText("8088");
		IP = new JTextField(8);
		// IP.setText("172.16.13.68");
		port.setEditable(false);
		IP.setEditable(false);
		userName = new JTextField(8);
		link = new JButton("启动");
		discon = new JButton("停止");
		discon.setEnabled(false);

		label = new JLabel("服务端口");
		northPanel.add(label);

		textField_serverport = new JTextField();
		northPanel.add(textField_serverport);
		textField_serverport.setColumns(10);
		northPanel.add(new JLabel("端口号"));
		northPanel.add(port);
		northPanel.add(new JLabel("服务器IP"));
		northPanel.add(IP);
		northPanel.add(new JLabel("用户名"));
		northPanel.add(userName);
		northPanel.add(link);
		northPanel.add(discon);
		panel.add(northPanel, BorderLayout.NORTH);

		userlist = new JList();
		leftPanel = new JScrollPane(userlist);
		leftPanel.setBorder(new TitledBorder("在线用户"));
		panel.add(leftPanel, BorderLayout.EAST);

		chatRecords = new JTextArea();
		chatRecords.setLineWrap(true);
		chatRecords.setEditable(false);
		rightPanel = new JScrollPane(chatRecords);
		rightPanel.setBorder(new TitledBorder("聊天信息"));
		centerPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		centerPanel.setDividerLocation(100);
		panel.add(centerPanel, BorderLayout.CENTER);

		southPanel = new JPanel();
		southPanel.setBorder(new TitledBorder("写消息"));
		message = new JTextField(50);
		send = new JButton("发送");
		southPanel.add(message);
		southPanel.add(send);
		panel.add(southPanel, BorderLayout.SOUTH);
		Jpanel.add(panel, BorderLayout.CENTER);
		getContentPane().add(Jpanel);
	}

	private void initFrame() {
		this.setTitle("客户端");
		this.setSize(640, 480);
		this.setLocationRelativeTo(null);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.getImage("img/logo.jpg");
		this.setIconImage(img);
		this.setResizable(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		closing();
	}

	public class ClientThread extends Thread {
		private Socket soc;
		private DataInputStream dis;
		private DataOutputStream dos;
		
		
		public ClientThread(Socket socket){
			this.soc = socket;
			try{
			dis = new DataInputStream(this.soc.getInputStream());
			dos = new DataOutputStream(this.soc.getOutputStream());
			}
			catch(IOException ex){
				ex.printStackTrace();
			}
		}
		@Override
		public void run() {
			String message = null;
			while (true) {
				try {
					message = dis.readUTF();
					if (message.contains("usersNames") && !users.contains(message.split(" ")[0])) {
						users.add(message.split(" ")[0]);
					}
					if (message.contains("userServerPorts")
							&& !portlists.contains(Integer.parseInt(message.split(" ")[0]))) {
						portlists.add(Integer.parseInt(message.split(" ")[0]));
					} else if (!message.contains("usersNames") && !message.contains("userServerPorts")) {
						chatRecords.append("\n");
						chatRecords.append(message);
						chatRecords.setCaretPosition(chatRecords.getDocument().getLength());
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					if (isConnect == true)
						JOptionPane.showMessageDialog(null, "服务器断开连接", "错误", JOptionPane.ERROR_MESSAGE);
					link.setEnabled(true);
					discon.setEnabled(false);
					isConnect = false;
					port.setEditable(true);
					IP.setEditable(true);
					userName.setEditable(true);
					usersLength = 0;
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
				}
			}
		}
	}

	public void send() {
		// if (selected_portlist == null) {
		// JOptionPane.showMessageDialog(null, "没有选择发送用户", "错误",
		// JOptionPane.ERROR_MESSAGE);
		// }
		
		if (isStart == false) {
			JOptionPane.showMessageDialog(null, "没有开启自己的服务器，无法发送消息", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (message.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(null, "消息不能为空", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String str = message.getText();
		if (selected_portlist == null) {
			for (int i = 0; i < llist.size(); i++) {
				System.out.println(llist.get(0));
				try {
					dos = new DataOutputStream(llist.get(i).getOutputStream());
					sendMessage(str);
					// dos.writeUTF(userName.getText() + "： " + str);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		} else {
			for (int i = 0; i < selected_portlist.size(); i++) {
				Socket si;
				try {
					si = new Socket(InetAddress.getLocalHost(), selected_portlist.get(i));
					dos = new DataOutputStream(si.getOutputStream());
					dis = new DataInputStream(si.getInputStream());
					sendMessage(str);
					new ClientThread(si).start();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		// sendMessage(str);
	}

	public void sendMessage(String str) {
		// if (selected_portlist == null) {
		// JOptionPane.showMessageDialog(null, "没有选择发送用户", "错误",
		// JOptionPane.ERROR_MESSAGE);
		// }
		// for (int i = 0; i < selected_portlist.size(); i++) {
		// System.out.println(users.get(i));
		try {
			// dos = new DataOutputStream(
			// (new Socket(InetAddress.getLocalHost(),
			// selected_portlist.get(i))).getOutputStream());
			dos.writeUTF(userName.getText() + "： " + str);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		// }
		// 显示自己发的消息
		// if (!str.equals("")) {
		chatRecords.append("\n");
		chatRecords.append(userName.getText() + "： " + str);
		chatRecords.setCaretPosition(chatRecords.getDocument().getLength());
		// }
		message.setText("");

	}

	public void closing() {
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (isConnect == true) {
					try {
						dos.writeUTF(userName.getText() + " 离开了聊天室");
						dis.close();
						dos.close();
						socket.close();
						ct.stop();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		});
	}

	public class UserList extends Thread {

		@Override
		public void run() {
			while (true) {
				if (users.size() != usersLength) {
					userlist.setListData(users);
					usersLength = users.size();
				}
			}
		}

	}

	private class ServerThread extends Thread {
		private ServerSocket serverSocket;

		public ServerThread(ServerSocket serverSocket) {
			this.serverSocket = serverSocket;
		}

		@Override
		public void run() {
			while (true) {
				Socket socket = null;
				try {
					socket = serverSocket.accept();
					new Thread(new MSThread(socket)).start();
					llist.add(socket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class MSThread implements Runnable {

		private Socket socket;
		private DataInputStream dis;
		private DataOutputStream dos;

		public MSThread(Socket socket) {

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
					if (!message.equals("")) {
						chatRecords.append("\n");
						chatRecords.append(message);
						chatRecords.setCaretPosition(chatRecords.getDocument().getLength());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}