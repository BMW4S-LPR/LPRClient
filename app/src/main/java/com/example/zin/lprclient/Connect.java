/**
 * Created by Rear82 on 2018/8/12.
 */
package com.example.zin.lprclient;

import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Rear82 on 2017/12/8.
 */

public class Connect {
	static final int GETIPSUCCESS = 1;
	static final int CREATESOCKETSUCCESS = 2;
	static final int CREATESOCKETFAIL = 4;
	static final int CONNECTSUCCESS = 3;
	static final int CONNECTFAIL = 5;
	static final int CREATEREADERSUCCESS = 6;
	static final int CREATEREADERFAIL = 7;
	static final int RECEIVE = 8;
	static final int CREATESOCKETOUTOFTIME = 9;
	static final int CREATINGSOCKET = 10;
	static final int STARTCREATESOCKET = 11;
	
	static final String SEGKEY = "---";
	
	public PrintWriter writer;
	Handler handler;
	Socket socket[] = new Socket[5000]; // 声明Socket对象
	BufferedReader reader;
	boolean con = false;
	InetAddress ip;
	String localname;
	String localip;
	String recetext = "";
	String ipn;
	int por;
	int soNum = 0;
	
	public Connect(Handler hd, String ipadd, int port) {
		handler = hd;
		ipn = ipadd;
		por = port;
	}
	
	public void addtext(int a) {
		Message msg = new Message();
		msg.what = a;
		handler.sendMessage(msg);
	}
	public class sendmsg extends Thread {
		String str = "";
		public sendmsg(String sendstr) {
			str = sendstr;
		}
		@Override
		public void run() {
			if(con) {
				writer.println(str);
			}
		}
	}
	public class connect extends Thread { // 连接套接字方法
		@Override
		public void run() {
			
			try { // try语句块捕捉可能出现的异常
				ip = InetAddress.getLocalHost(); // 实例化对象
				localname = ip.getHostName(); // 获取本机名
				localip = ip.getHostAddress(); // 获取本IP地址
				addtext(GETIPSUCCESS);
			} catch (UnknownHostException e) {
				e.printStackTrace(); // 输出异常信息
			}
			try { // 捕捉异常
				con = false;
				new setthread().start();
				addtext(STARTCREATESOCKET);
				soNum++;
				socket[soNum] = new Socket(ipn, por);// 实例化Socket对象
				addtext(CREATESOCKETSUCCESS);
				writer = new PrintWriter(socket[soNum].getOutputStream(), true);
				con = true;
				addtext(CONNECTSUCCESS);
				if (soNum > 1) {
					socket[soNum - 1].close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				addtext(CREATESOCKETFAIL);
			}
			try {
				if (con == true) {
					reader = new BufferedReader(new InputStreamReader(socket[soNum]
							.getInputStream())); // 实例化BufferedReader对象
					addtext(CREATEREADERSUCCESS); // 文本域中提示信息
					readthread rdt = new readthread();
					rdt.start();
				}
			} catch (IOException e) {
				e.printStackTrace();
				addtext(CREATEREADERFAIL);
			}
		}
	}
	
	public class setthread extends Thread {
		@Override
		public void run() {
			try {
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (con == false) {
				addtext(CONNECTFAIL);
			}
			
		}
	}
	
	public class readthread extends Thread {
		
		@Override
		public void run() {
			try {
				while (true) { // 如果套接字是连接状态
					if (reader.ready()) {
						// 获得客户端信息
						recetext = reader.readLine();
						addtext(RECEIVE);
					}
				}
			} catch (Exception e) {
				System.out.println(e.toString());
//				tv2.setText(tv2.getText() + "\n" + e.toString()); // 输出异常信息
			
			}

//			try {
//				if (reader != null) {
//					reader.close(); // 关闭流
//					tv2.append("\n客户端断开连接");
//				}
//				if (socket != null) {
//					socket.close(); // 关闭套接字
//					tv2.append("\n客户端断开连接");
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}
}
