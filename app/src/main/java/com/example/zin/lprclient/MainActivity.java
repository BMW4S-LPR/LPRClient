package com.example.zin.lprclient;

import java.io.IOException;
import java.net.Socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
	static final int getFromLogin = 500;
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
	static String logtext = "日志记录:";
	
	public ArrayList<String> decoder(String code) {
		ArrayList<String> re = new ArrayList<>();
		while (code.indexOf(SEGKEY) != -1) {
			re.add(code.substring(0, code.indexOf(SEGKEY)));
			code = code.substring(code.indexOf(SEGKEY) + SEGKEY.length(), code.length());
		}
		re.add(code);
		return re;
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String ctext = "";
			
			SimpleDateFormat myFmt1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
			Date now = new Date();
			String rq = myFmt1.format(now);
			
			switch (msg.what) {
				case CONNECTFAIL:
					ctext = "连接超时";
					Toast.makeText(MainActivity.this, "连接超时，详细信息请查看日志。", Toast.LENGTH_SHORT).show();
					break;
				
				case STARTCREATESOCKET:
					ctext = "开始创建Socket: " + newconnect.ipn + " " + newconnect.por;
					break;
				
				case CREATESOCKETSUCCESS:
					ctext = "Socket创建成功";
					break;
				
				case GETIPSUCCESS:
					ctext = "IP地址获取成功: " + newconnect.localname + " " + newconnect.localip;
					break;
				case CONNECTSUCCESS:
//					tv.append("完成连接"); // 文本域中提示信息
					ctext = "完成连接";
					break;
				case CREATESOCKETFAIL:
//					tv.append("Socket创建失败"); // 文本域中提示信息
					ctext = "Socket创建失败";
					break;
				case CREATEREADERSUCCESS:
//					tv.append("Reader创建成功");
					ctext = "Reader创建成功";
					break;
				case CREATEREADERFAIL:
//					tv.append(); // 文本域中提示信息
					ctext = "Reader创建失败";
					break;
				case RECEIVE:
					ctext = newconnect.recetext;
					ArrayList<String> re = decoder(ctext);
					if (re.get(0).equals("1001")) {
						RECtextview.setText(re.get(1));
						if (re.get(1).substring(0, 1).equals("未")) {
							RESULTimageview.setImageBitmap(null);
						}
					}
					if (re.get(0).equals("1002")) {
						STATEtextview.setText("当前状态：" + re.get(1));
					}
					if (re.get(0).equals("1003")) {
						POStextview.setText("X: " + re.get(1) + "   Y: " + re.get(2) + "   Width: " + re.get(3) + "   Height: " + re.get(4));
					}
					if (re.get(0).equals("1004")) {
						Bitmap tmp = stringtoBitmap(re.get(1));
						RAWimageview.setImageBitmap(tmp);
					}
					if (re.get(0).equals("1005")) {
						Bitmap tmp = stringtoBitmap(re.get(1));
						RESULTimageview.setImageBitmap(tmp);
					}
//					boolean op = false;
//
//					if (re.get(0).equals("1000")) {
//						op = true;
//						Toast.makeText(MainActivity.this, "服务器无法识别此指令，详情查看日志。", Toast.LENGTH_SHORT).show();
//					}
//					if (re.get(0).equals("1001")) {
//						op = true;
//						Toast.makeText(MainActivity.this, re.get(1), Toast.LENGTH_SHORT).show();
//						if (re.get(1).equals("登陆成功")) {
////							loginfo.decodestr(ctext);
////							rerangelist();
//						}
//					}
//					if (re.get(0).equals("1002")) {
//						op = true;
//						Toast.makeText(MainActivity.this, re.get(1), Toast.LENGTH_SHORT).show();
//
//					}
//
//					if (re.get(0).equals("1003") || re.get(0).equals("1004")) {
//						op = true;
//						Toast.makeText(MainActivity.this, re.get(1), Toast.LENGTH_SHORT).show();
//
//					}
//					if (op == false) {
//						Toast.makeText(MainActivity.this, "从服务器接收了一个无法识别的指令，详情查看日志。", Toast.LENGTH_SHORT).show();
//
//					}
					break;
				case CREATESOCKETOUTOFTIME:
//					tv.append();
					ctext = "Socket建立超时！请检查服务器是否打开，以及设备是否接入网络。";
					break;
				case CREATINGSOCKET:
					ctext = "正在创建socket，地址:" + newconnect.ipn + " 端口:" + newconnect.por;
					break;
			}
			if (ctext != "") {
				if (ctext.length() > 200) {
					logtext = "超长字符: " + ctext.substring(0, 200) + "......    " + rq + '\n' + logtext;
				} else {
					logtext = ctext + "    " + rq + '\n' + logtext;
				}
				logtext = logtext.substring(0, Math.min(3000, logtext.length()));
				LOGtextview.setText(logtext);
			}
		}
	};
	static EditText IPtext, PORTtext, TRYtext;
	static TextView RECtextview, LOGtextview, STATEtextview, POStextview;
	static Button ConnectButton, SS;
	static ImageView RAWimageview, RESULTimageview;
	Connect newconnect;
	
	public static Bitmap stringtoBitmap(String string) {
		//将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return bitmap;
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		IPtext = findViewById(R.id.IPtext);
		PORTtext = findViewById(R.id.PORTtext);
		RECtextview = findViewById(R.id.RECtextview);
		LOGtextview = findViewById(R.id.LOGtextview);
		ConnectButton = findViewById(R.id.ConnectButton);
		STATEtextview = findViewById(R.id.STATEtextview);
		TRYtext = findViewById(R.id.TRYtext);
		POStextview = findViewById(R.id.POStextview);
		SS = findViewById(R.id.shit);
		RAWimageview = findViewById(R.id.RAWimageview);
		RESULTimageview = findViewById(R.id.RESULTimageview);
		SS.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				newconnect.new sendmsg(TRYtext.getText().toString()).start();
				
			}
		});
		ConnectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				newconnect = new Connect(handler, IPtext.getText().toString(), Integer.valueOf(PORTtext.getText().toString()));
				newconnect.new connect().start();
				
			}
		});
	}
}
