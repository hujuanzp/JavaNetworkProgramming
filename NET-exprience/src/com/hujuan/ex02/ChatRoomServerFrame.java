package com.hujuan.ex02;

import java.awt.BorderLayout;
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
import java.net.UnknownHostException;
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
//import javax.swing.ListModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ChatRoomServerFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6582389471265865971L;

	private JPanel panel;
	private JPanel northPanel;
	private JPanel southPanel;
	private JTextField IP;
	private JTextField port;
	private JList<String> userlist;
	private JTextArea chatRecords;
	private JScrollPane leftPanel;
	private JScrollPane rightPanel;
	private JSplitPane centerPanel;
	private JTextField message;
	private JButton start;
	private JButton stop;
	private JButton send;
	private JPanel Jpanel;
	private Vector<Socket> lists = new Vector<Socket>();
	private Vector<Socket> selected_list;

	private ServerSocket serverSocket;
	private ServerThread st;
	private DataOutputStream dos;
	private DataInputStream dis;
	private Vector<String> users = new Vector<String>();
	private int usersLength = 0;
	private UserList ul;
	// private String usermessage = null;

	private boolean isStart = false;

	// 为了输出客户端的服务端口
	private Vector<Integer> serverPorts = new Vector<>();

	public ChatRoomServerFrame() {

		initServer();

		start.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					if (port.getText().trim().equals("")) {
						JOptionPane.showMessageDialog(null, "端口号不能为空", "错误", JOptionPane.ERROR_MESSAGE);
						return;
					}
					if (isStart == false) {
						serverSocket = new ServerSocket(Integer.parseInt(port.getText()));
						chatRecords.append("\n");
						chatRecords.append("服务器已开启");
						st = new ServerThread(serverSocket);
						st.start();
						port.setEditable(false);
						start.setEnabled(false);
						stop.setEnabled(true);
						isStart = true;
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
				selected_list = new Vector<Socket>();
				for (int index : indices) {
					selected_list.add(lists.get(index));
				}
				System.out.println();
			}
		});

		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					chatRecords.append("\n");
					chatRecords.append("服务器已断开");
					chatRecords.setCaretPosition(chatRecords.getDocument().getLength());
					if (dis != null) {
						dis.close();
					}
					if (dos != null) {
						dos.close();
					}
					users.removeAll(users);
					usersLength = 0;
					serverSocket.close();
					port.setEditable(true);
					start.setEnabled(true);
					stop.setEnabled(false);
					isStart = false;
					ul.stop();
					st.stop();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (lists.size() == 0) {
					JOptionPane.showMessageDialog(null, "当前没有用户在线", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (message.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "消息不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String line = "服务器：" + message.getText();
				sendSelcetedMessage(line);

				message.setText("");
			}
		});

		message.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (lists.size() == 0) {
					JOptionPane.showMessageDialog(null, "当前没有用户在线", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (message.getText().trim().equals("")) {
					JOptionPane.showMessageDialog(null, "消息不能为空", "错误", JOptionPane.ERROR_MESSAGE);
					return;
				}
				String line = "服务器：" + message.getText();
				sendSelcetedMessage(line);

				message.setText("");

			}
		});

		initFrame();
	}

	private void initServer() {
		Jpanel = new JPanel();
		Jpanel.setBounds(1000, 1000, 1000, 1000);
		Jpanel.setLayout(new BorderLayout());
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		northPanel = new JPanel();
		northPanel.setBorder(new TitledBorder("配置信息"));
		IP = new JTextField(15);
		String ipAddress = null;
		try {
			ipAddress = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IP.setText(ipAddress);
		IP.setEditable(false);
		port = new JTextField(15);
		port.setText("8088");
		start = new JButton("启动");
		stop = new JButton("停止");
		stop.setEnabled(false);
		northPanel.add(new JLabel("本地地址"));
		northPanel.add(IP);
		northPanel.add(new JLabel("端口号"));
		northPanel.add(port);
		northPanel.add(start);
		northPanel.add(stop);
		panel.add(northPanel, BorderLayout.NORTH);

		userlist = new JList<String>();
		leftPanel = new JScrollPane(userlist);
		leftPanel.setBorder(new TitledBorder("在线用户"));
		panel.add(leftPanel, BorderLayout.EAST);

		chatRecords = new JTextArea();
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

		this.setTitle("服务器");
		this.setSize(640, 480);
		this.setLocationRelativeTo(null);
		closing();
	}

	private class MSThread implements Runnable {

		private Socket socket;
		private DataInputStream dis;
		private DataOutputStream dos;

		public MSThread(Socket socket) {

			try {
				this.socket = socket;
				dis = new DataInputStream(socket.getInputStream());
				dos = new DataOutputStream(socket.getOutputStream());
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
					dos = new DataOutputStream(socket.getOutputStream());
					if (message.contains("服务端口") && !message.contains("：")) {
						serverPorts.add(Integer.parseInt(message.split(" ")[0]));
						// dos.writeUTF(message.split(" ")[0] + "
						// userServerPorts");
						if (serverPorts != null) {
							for (Integer i : serverPorts) {
								// dos.writeUTF(i + " userServerPorts");
								for (Socket s : lists) {
									dos = new DataOutputStream(s.getOutputStream());
									dos.writeUTF(i + " userServerPorts");
								}
							}
						}
					}
					if (message.contains("进入了聊天室") && !message.contains("：")) {
						users.add(message.split(" ")[0]);
						// dos.writeUTF(message.split(" ")[0] + " usersNames");
						if (users != null) {
							for (String str : users) {
								for (Socket s : lists) {
									dos = new DataOutputStream(s.getOutputStream());
									dos.writeUTF(str + " usersNames");
								}
							}
						}
					}

					if (message.contains("离开了聊天室") && !message.contains("：")) {
						users.remove(message.split(" ")[0]);
						// sendMessage(message);
						dis.close();
						socket.close();
						lists.remove(socket);
					} else {
						// sendMessage(message);
					}
					chatRecords.append("\n");
					chatRecords.append(message);
					chatRecords.setCaretPosition(chatRecords.getDocument().getLength());
				}
			} catch (IOException e) {
				lists.remove(socket);
				e.printStackTrace();
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
					lists.add(socket);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sendSelcetedMessage(String str) {
		try {
			if (selected_list == null) {
				JOptionPane.showMessageDialog(null, "没有选择发送用户", "错误", JOptionPane.ERROR_MESSAGE);
			}
			for (int i = 0; i < selected_list.size(); i++) {
				dos = new DataOutputStream(selected_list.get(i).getOutputStream());
				dos.writeUTF(str);
			}
			chatRecords.append("\n");
			chatRecords.append(str);
			chatRecords.setCaretPosition(chatRecords.getDocument().getLength());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void closing() {
		this.addWindowListener(new WindowAdapter() {
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
						ul.stop();
						st.stop();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
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
					// try {
					// if (serverPorts != null) {
					// for (int i = 0; i < serverPorts.size() - 1; i++) {
					// dos = new DataOutputStream(lists.get(lists.size() -
					// 1).getOutputStream());
					// dos.writeUTF(serverPorts.get(i) + " userServerPorts");
					// }
					// for (Socket s : lists) {
					// dos = new DataOutputStream(s.getOutputStream());
					// dos.writeUTF(serverPorts.get(serverPorts.size() - 1) + "
					// userServerPorts");
					// }
					//
					// }
					// if (users != null) {
					// for (int i = 0; i < users.size() - 1; i++) {
					// dos = new DataOutputStream(lists.get(lists.size() -
					// 1).getOutputStream());
					// dos.writeUTF(users.get(i) + " usersNames");
					// }
					// for (Socket s : lists) {
					// dos = new DataOutputStream(s.getOutputStream());
					// dos.writeUTF(users.get(users.size() - 1) + "
					// usersNames");
					// }
					//
					// }
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					usersLength = users.size();
				}
			}
		}

	}
}