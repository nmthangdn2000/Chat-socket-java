/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;

/**
 *
 * @author binkyoa
 */
public class serverthread extends Thread{
    public Socket client;
	public Server server;
	private String nickName;
	private DataOutputStream dos;
	private DataInputStream dis;
	private boolean run;
	// time 
	private Calendar c = Calendar.getInstance();
	private int hour = c.get(Calendar.HOUR_OF_DAY);
	private int minute = c.get(Calendar.MINUTE);
	private int second = c.get(Calendar.SECOND);
	private int millis = c.get(Calendar.MILLISECOND);
        

	public serverthread(Server server, Socket client){
		try {
			this.server=server;
			this.client=client;
			dos= new DataOutputStream(client.getOutputStream());
			dis= new DataInputStream(client.getInputStream());
			run=true;
			this.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void run(){
		// xữ lý đăng nhập
		String msg=null;
		
		while(run){
			nickName = getMSG();// lấy giá trị bên client
            System.out.println("Nick Name: "+  nickName);
			if(nickName.compareTo("0")==0){
				logout();
			}
			else {
				if(checkNick(nickName)){
					sendMSG("0");
				}
				else{
					server.user.append(" ( "+hour+":"+minute+":"+second+" ) "+nickName+" đã kết nối với room\n");
					server.sendAll(nickName,nickName+" đã vào room với anh em\n", 1);
					server.listUser.put(nickName, this);
					server.sendAllUpdate(nickName);
					sendMSG("1");
					diplayAllUser();
					while(run){
						int stt = Integer.parseInt(getMSG());// lấy giá trị bên client gán thành số
						switch(stt){
							case 0:
								run=false;
								server.listUser.remove(this.nickName);
								exit(1);
								break;
							case 1:
								msg = getMSG();// lấy giá trị bên client
								server.sendAll(nickName,msg+" : "+nickName+"\n", 1);// 1 là gửi tin nhắn
								break;
							case 2:
								System.out.println("có vào đây nhá");
								msg = getMSG();// lấy giá trị bên client
								server.sendAll(nickName,msg+"_"+" : "+nickName+"\n", 2);// 2 là gửi file
								break;
							case 3: // kicl vô văn hóa
								run=false;
								server.listUser.remove(this.nickName);
								exit(2);
								break;
							case 4: // kich treo máy
								run=false;
								server.listUser.remove(this.nickName);
								exit(3);
								break;
							case 5: // vote kich 
								msg = getMSG();// lấy giá trị bên client
								server.sendAll(nickName,nickName+" muốn kích "+ msg + " ra khỏi phòng", 3);// 2 là gửi file
								break;
						}
					}
				}
			}
		}
	}
	private void logout() {
		try {
			dos.close();
			dis.close();
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void exit(int type){
		try {
			server.sendAllUpdate(nickName);
			dos.close();
			dis.close();
			client.close();
			server.user.append(" ( "+hour+":"+minute+":"+second+" ) "+nickName+" đã thoát\n");
			String notifi = "";
			if(type == 1) notifi = " đã thoát\n";
			else if(type == 2) notifi = " vô văn hóa\n";
			else if(type == 3) notifi = " bị kích vì treo máy\n";
			server.sendAll(nickName,nickName+notifi, 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private boolean checkNick(String nick){
		return server.listUser.containsKey(nick);
	}
	private void sendMSG(String data){
		try {
			dos.writeUTF(data);
			dos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	public void sendMegThread(String msg1,String msg2){
		sendMSG(msg1);
		sendMSG(msg2);
	}
	private String getMSG(){
		String data=null;
		try {
			data=dis.readUTF();      
		} catch (IOException e) {
			e.printStackTrace();
		}
		return data;
	}
	private void diplayAllUser(){
		String name = server.getAllName();
		sendMSG("4");
		sendMSG(name);
	}
        
}
