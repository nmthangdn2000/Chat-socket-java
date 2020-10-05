package server;

import java.awt.BorderLayout;


import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;



import javax.swing.JLabel;
import java.awt.Font;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Server extends JFrame {

	private JPanel contentPane;
	private JButton close;
	public JTextArea user;
	private ServerSocket server;
	public Hashtable<String, serverthread> listUser = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		new Server().go();
	}
	private void go() {
		try {
			listUser = new Hashtable<String, serverthread>();
			server = new ServerSocket(2019);
			user.append("Máy chủ bắt đầu phục vụ \n");
			while (true) {
				Socket client = server.accept();
				new serverthread(this,client);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			user.append("Máy chủ không hoạt động \n");
		}
	}
	
	public void sendAll(String from, String msg, int type) {
		Enumeration e = listUser.keys();
		String name = null;
		while(e.hasMoreElements()) {//kiểm tra nếu có đối tượng hay ko
			name = (String) e.nextElement();
			if(name.compareTo(from)!=0) {
				if(type == 1)
					listUser.get(name).sendMegThread("3", msg); // gửi tin nhắn qua clien tương ứng với case = 3
				if(type == 2)
					listUser.get(name).sendMegThread("6", msg);
				if(type == 3)
					listUser.get(name).sendMegThread("7", msg);
			}
		}
	}
	
	public void sendAllUpdate(String from) {
		Enumeration e = listUser.keys();
		String name = null;
		while(e.hasMoreElements()) {
			name = (String) e.nextElement();
			if(name.compareTo(from)!=0) 
					listUser.get(name).sendMegThread("4", getAllName());;
		}
	}
	public String getAllName() {
		Enumeration e = listUser.keys();
		String name = "";
		while(e.hasMoreElements()) {
			name+=e.nextElement()+"\n";//nextElement trả về phần tử kế tiếp trong Enumeration
		}
		return name;
	}
	/**
	 * Create the frame.
	 */
	public Server() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 322, 444);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblTrngThiServer = new JLabel("Tr\u1EA1ng th\u00E1i server");
		lblTrngThiServer.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblTrngThiServer.setBounds(10, 11, 128, 22);
		contentPane.add(lblTrngThiServer);
		
		user = new JTextArea();
		user.setBounds(10, 44, 286, 290);
		user.setEditable(false);
		contentPane.add(user);
		user.append("Máy chủ đã được mở.\n");
		
		close = new JButton("Close server");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					server.close();
					System.exit(0);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		close.setBounds(96, 358, 112, 36);
		contentPane.add(close);
		setVisible(true);
	}
}
