
package clien;

import java.io.DataInputStream;
import java.io.IOException;

import javax.swing.text.BadLocationException;


public class threadclien extends Thread{
  private boolean run;
  
	private DataInputStream dis;
	private client client;

	public threadclien(client client,DataInputStream dis){
		run=true;
		this.client=client;
		this.dis=dis;

		this.start();
	}
	public void run(){
		String msg1,msg2;
		while(run){
			try {
				msg1=dis.readUTF();
				msg2=dis.readUTF();
				try {
					client.getMESS(msg1,msg2);
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			dis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void stopThread(){
		this.run=false;
	}  
}
