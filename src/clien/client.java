package clien;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import javax.print.attribute.standard.Compression;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.awt.event.ActionEvent;
import java.awt.Scrollbar;
import javax.swing.JLayeredPane;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.List;
import javax.swing.SwingConstants;

public class client extends JFrame {

	private JPanel contentPane,login,connect,chat;
	private JTextField IP;
	private JTextField name;
	private JTextField name1;
	private JTextField message;
	private JTextArea DSonline;
	
	private Socket client;
	private threadclien threadclien;
	private DataOutputStream Output;
	private DataInputStream Input;
	private JTextPane msg, msg2;
	
	private JFileChooser fileChooser;
	private BufferedImage img;
	private int imgMSG = 0, imgMSG2 = 0;
	private int check = 0;
	
	private int count = 1;
	private Timer timer;
	//key vô văn hóa :v
	private ArrayList<String> keyVVH ;
	//jTextArea.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					client frame = new client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	// Socket
	private void go(String IP) {
		try {          
			client = new Socket(IP,2019);
			Output=new DataOutputStream(client.getOutputStream());
			Input=new DataInputStream(client.getInputStream());

			//client.close();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this,"Kết nối thât bại, xin hãy kiểm tra lại ip.","Message Dialog",JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
	}
	// Socket
	private void sendMess(String data) {
		try {
			Output.writeUTF(data);
			Output.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private String getMess() {
		String data = null;
		try {
			data = Input.readUTF();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}
	public void getMESS(String mess1, String mess2) throws BadLocationException {
		int s = Integer.parseInt(mess1);
		String text = "";
		switch(s) {
		// update tin nhắn người khác
		case 3 :
			text = checkLineJTextPane(this.msg2, this.msg, imgMSG - imgMSG2);
			appendString(text+mess2, this.msg2);
			break;
			//update danh sách online
		case 4:
			this.DSonline.setText(mess2);
			break;
			// server đóng cửa
		case 5 :
			threadclien.stopThread();
			break;
		case 6:
			String [] split = mess2.split("_");
			base64ToImage(split[0], split[1], this.msg2);
			text = checkLineJTextPane(this.msg2, this.msg, imgMSG - imgMSG2);
			appendString(split[1], this.msg2);
			imgMSG2++;
			break;
		case 7: 
			
			break;
		default:
			break;
		}
	}
	// kiểm tra đăng nhập
	//if(txtStaffName.getText().equals("Quang")||txtStaffName.getText().equals("Binh"))
	private boolean checklogin(String nick) {
		if(!nick.equals("")) {
			sendMess(nick);
			int stt = Integer.parseInt(getMess());
			if(stt == 0) return false;
			return true;
		}else
			return false;
	}
	//kiểm tra đăng nhập
	//----------------------------------------------------
	
	//kiểm tra tin nhắn 1
	private void checkSend(String msg) throws BadLocationException{
		if(msg.compareTo("\n")!=0){
			String text = checkLineJTextPane(this.msg, this.msg2, imgMSG2- imgMSG);
		    appendString(text+"\nTôi : "+msg+"", this.msg);
		    
//			this.msg.setText();
			sendMess("1");
			sendMess(msg);
		}
	}
	//kiểm tra tin nhắn
	//----------------------------------------------------0
	private void exit(String type){
		try {
			sendMess(type);
			Output.close();
			Input.close();
			client.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.exit(0);
	}
	// thêm văn bản mới v
	private void appendString(String str, JTextPane jTextPane) throws BadLocationException
	{
	     StyledDocument document = (StyledDocument) jTextPane.getDocument();
	     document.insertString(document.getLength(), str, null);
	     // ^ or your style attribute  
	}
	private int lineJtextPane( JTextPane jTextPane) {
		int totalCharacters = jTextPane.getDocument().getLength(); 
		int lineCount = (totalCharacters == 0) ? 1 : 0;

		try {
		   int offset = totalCharacters; 
		   while (offset > 0) {
		      offset = Utilities.getRowStart(jTextPane, offset) - 1;
		      lineCount++;
		   }
		} catch (BadLocationException e) {
		    e.printStackTrace();
		}
		System.out.println("bbbb "+lineCount);
		return lineCount;
	}
	// thêm văn bản mới tăng khoảng cách với cái còn lại
	private String checkLineJTextPane(JTextPane jTextPane, JTextPane jTextPane2, int img) {
		
		String line = "";
		int linejTextPane = lineJtextPane(jTextPane);
		int linejTextPane2 = lineJtextPane(jTextPane2);
		System.out.println(linejTextPane);
		System.out.println(linejTextPane2);
		if(linejTextPane < linejTextPane2) {
			int numberLine = linejTextPane2 - linejTextPane;
			System.out.println("numberLine : "+numberLine);
			for(int i = 0 ; i < numberLine; i++) line = line + "\n"; 
		}
//		System.out.println(img);
		if(img > 0) {
			for(int i = 0 ; i < img; i++) {
				line = line + "\n\n\n\n\n"; 
			}
			imgMSG = 0;
			imgMSG2 = 0;
		}
		return line;
	}
	// 
	public static String imageToBase64(File file){
		 String encodedString="";
		 byte[] fileContent;
		try {
			fileContent = FileUtils.readFileToByteArray(file);
			encodedString = Base64.getEncoder().encodeToString(fileContent);
			System.err.println(encodedString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return encodedString;
	}
	private void base64ToImage(String encodedString, String str, JTextPane jTextPane){
		byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
		try {
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(decodedBytes));
			Image imgFile = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); 

//			Icon icon = new ImageIcon(imgFile);
//			jTextPane.insertIcon(icon);
			
			StyledDocument document = (StyledDocument) jTextPane.getDocument();
		    try {
				Style style = document.addStyle("StyleName", null);
		        StyleConstants.setIcon(style, new ImageIcon(imgFile));
		        document.insertString(document.getLength(), str, style);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    img = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private boolean checkVoVanHoa(String text) {
		boolean check = true;
		for(int i = 0; i < keyVVH.size(); i++) {
			if ( text.toLowerCase().indexOf(keyVVH.get(i).toLowerCase()) != -1 ) {
				 System.out.println("I found the keyword");
				 JOptionPane.showMessageDialog(this,"Cái thằng vô văn hóa","Message Dialog",JOptionPane.WARNING_MESSAGE);
				 threadclien.stopThread();
				 exit("3");
				 System.exit(0);
				 check = false;
				 break;
			} else {
			   System.out.println("not found");
			}	
		}
			
		return check;
	}
	private void addKeyVoVanHoa() {
		keyVVH = new ArrayList<String>();
		keyVVH.add("ngu");
		keyVVH.add("óc chó");
		keyVVH.add("mẹ mày");
		keyVVH.add("cha mày");
		keyVVH.add("ông nội mày");
		keyVVH.add("bà nội mày");
		keyVVH.add("ông cố nội mày");
	}
	private void timeout() {
		count = 1;
		timer = new Timer(1000, new ActionListener(){
            public void actionPerformed(ActionEvent e) {  
            	System.out.println(count);
            	if(count == 30) displayMessageDialog();
                count++;
            }
        }); 
	    timer.start();
	}
	private void displayMessageDialog() {
		timer.stop();
		JOptionPane.showMessageDialog(this,"Treo máy","Message Dialog",JOptionPane.WARNING_MESSAGE);
		 threadclien.stopThread();
		 exit("4");
		 System.exit(0);
	}
	/**
	 * Create the frame.
	 */
	public client() {
		addKeyVoVanHoa();
		
		setTitle("CHAT");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 687, 588);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		chat = new JPanel();
		chat.setBounds(0, 0, 661, 538);
		contentPane.add(chat);
		chat.setLayout(null);
		
		JLabel lblTnChat_1 = new JLabel("Tên chat :");
		lblTnChat_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTnChat_1.setBounds(28, 11, 69, 14);
		chat.add(lblTnChat_1);
		
		name1 = new JTextField();
		name1.setBounds(107, 10, 185, 20);
		chat.add(name1);
		name1.setColumns(10);
		
		
		DSonline = new JTextArea();
		DSonline.setBounds(502, 77, 149, 318);
		chat.add(DSonline);
		
		JLabel lblTinNhn = new JLabel("Tin nhắn");
		lblTinNhn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTinNhn.setBounds(10, 500, 59, 14);
		chat.add(lblTinNhn);
		
		message = new JTextField();
		message.setBounds(72, 485, 287, 48);
		chat.add(message);
		message.setColumns(10);
		
		JButton btnGoi = new JButton("Gởi");
		btnGoi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println(message.getText());
					if(checkVoVanHoa(message.getText())) {
						checkSend(message.getText());// *************************************************
						timer.stop();
						timeout();
					}
					message.setText("");
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		});
		btnGoi.setBounds(369, 485, 62, 49);
		chat.add(btnGoi);
		
		JButton btnXoa = new JButton("Xóa");
		btnXoa.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				message.setText("");
			}
		});
		btnXoa.setBounds(431, 485, 59, 49);
		chat.add(btnXoa);
		
		JLabel lblDanhSchOnline = new JLabel("Danh sách online");
		lblDanhSchOnline.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblDanhSchOnline.setBounds(502, 42, 113, 17);
		chat.add(lblDanhSchOnline);
		
		JButton btnThot = new JButton("Thoát");
		btnThot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					threadclien.stopThread();
					exit("0");
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		});
		btnThot.setFont(new Font("Tahoma", Font.PLAIN, 12));
		btnThot.setBounds(302, 9, 89, 23);
		chat.add(btnThot);
		
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(10, 42, 448, 432);
		chat.add(layeredPane);
		layeredPane.setLayout(new GridLayout(1, 0, 0, 0));
	    
	    msg = new JTextPane();
	    msg.setEditable(false);
	    layeredPane.add(msg);
	    
	    msg2 = new JTextPane();
	    msg2.setEditable(false);
	    layeredPane.add(msg2);
	    
	    SimpleAttributeSet attribs = new SimpleAttributeSet();
	    StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_RIGHT);
	    msg2.setParagraphAttributes(attribs, true);
	    
	    JButton btnImage = new JButton("Chọn ảnh");
	    btnImage.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		fileChooser = new JFileChooser();
	    		fileChooser.setCurrentDirectory(new File("C:\\Users\\ADMIN\\Desktop\\kiem_tien_of_me\\theme"));
	    		int response = fileChooser.showOpenDialog(null);
	    		if(response == JFileChooser.APPROVE_OPTION) {
	    			File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
	    			System.err.println(file);
					try {
						img = ImageIO.read(file);
						Image imgFile = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH); 

		    			Icon icon = new ImageIcon(imgFile);
		    			String text = checkLineJTextPane(msg, msg2, imgMSG2 - imgMSG);
		    		    appendString(text+"\nTôi : ", msg);
		    			msg.insertIcon(icon);
		    			imgMSG ++;
		    			timer.stop();
						timeout();
		    			sendMess("2");
		    			sendMess(imageToBase64(file));
		    			img = null;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BadLocationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
	    		}
	    	} 
	    });
	    btnImage.setBounds(492, 485, 89, 48);
	    chat.add(btnImage);
	    
	    JButton btnKich = new JButton("Kich");
	    btnKich.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		showdialog();
	    	}
	    });
	    btnKich.setBounds(584, 485, 67, 48);
	    chat.add(btnKich);
		
		login = new JPanel();
		login.setBounds(0, 0, 587, 49);
		contentPane.add(login);
		login.setLayout(null);
		
		JLabel lblTnChat = new JLabel("Tên chat :");
		lblTnChat.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblTnChat.setBounds(76, 11, 64, 16);
		login.add(lblTnChat);
		
		name = new JTextField();
		name.setBounds(150, 10, 202, 23);
		login.add(name);
		name.setColumns(10);
		
		JButton btnDangNhap = new JButton("Đăn nhập");
		btnDangNhap.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				if(checklogin(name.getText())) {
					login.setVisible(false);
					chat.setVisible(true);
					
					name1.setText(name.getText());
					name1.setEditable(false);
					msg.setText("Đã đăng nhập thành công \n");
					a();
					timeout();
				}
				else {
					JOptionPane.showMessageDialog(null,"Đã tồn tại níck này trong room, bạn vui lòng nhập lại.","Message Dialog",JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		btnDangNhap.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnDangNhap.setBounds(362, 10, 101, 23);
		login.add(btnDangNhap);
		
		connect = new JPanel();
		connect.setBounds(0, 0, 587, 49);
		contentPane.add(connect);
		connect.setLayout(null);
		
		JLabel lblIpServer = new JLabel("IP Server :");
		lblIpServer.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblIpServer.setBounds(55, 11, 65, 16);
		connect.add(lblIpServer);
		
		IP = new JTextField();
		IP.setBounds(130, 10, 206, 23);
		connect.add(IP);
		IP.setColumns(10);
		
		JButton btnKetNoi = new JButton("Kết nối");
		btnKetNoi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					go(IP.getText());
                    login.setVisible(true);
                    connect.setVisible(false);
			}
		});
		btnKetNoi.setBounds(346, 10, 89, 23);
		connect.add(btnKetNoi);
		
		login.setVisible(false);
		chat.setVisible(false);
	}
	private void showdialog() {
//			String result = (String)JOptionPane.showInputDialog(
//		               this,
//		               "nhập tên người muốn kich khỏi phòng", 
//		               "Swing Tester",            
//		               JOptionPane.PLAIN_MESSAGE,
//		               null,            
//		               null, 
//		              "abc "
//		            );
//			if(result != null && result.length() > 0){
//	            System.out.println(result);
//	         }else {
//	         	System.out.println(result);
//	         }  	
	}
	public void a() {
		threadclien = new threadclien(this ,this.Input);
	}
}
