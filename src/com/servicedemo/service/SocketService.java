package com.servicedemo.service;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.servicedemo.db.SQLiteHelper;
import com.servicedemo.entity.RiverUser;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.Toast;

public class SocketService extends Service {

	/** Command to the service to register a client,receiving callbacks 
	 *  from the servicve.the Message's replyTo field must be a Messenger of
	 *  the client where callbacks should be sent.*/
	public static final int MSG_REGISTER_CLIENT = 1;

	/** Command to the service to unregister a client,to stop receiving callbacks
	 *  from the service.The message's replyTo field must be a Messenger of 
	 *  the client as previously given with MSG_UNREGISTERED_CLIENT*/
	public  static final int MSG_UNREGISTERED_CLIENT = 2;

	/** Command to service to set a new value.this can be send to the 
	 *  service to supply a new value,and will be sent by the service to 
	 *  any registered clients with the new value*/
	public static final int MSG_SET_VALUE = 3;

	/** Keeps track of all current registered clients */
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();

	int mValue = 0;

	/** Handler of incoming messages from clients*/
	class IncomingHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				mClients.add(msg.replyTo);
				break;
			case MSG_UNREGISTERED_CLIENT:
				mClients.remove(msg.replyTo);
				break;
			case MSG_SET_VALUE:
				if(mClients.size() < 1) return;
				for (int i = mClients.size()-1; i >=0; i--) {
					try {
						RiverUser rv = new RiverUser();
						rv.userid = 100000000;
						rv.username = "This is RiverUser";

						Bundle bundle = new Bundle();
						bundle.putSerializable("user", rv);

						Message message = new Message();
						message.setData(bundle);
						message.what = SocketService.MSG_SET_VALUE;

						mClients.get(i).send(message);
					} catch (RemoteException e) {
						/* The Client is dead.Remove it from the list;
						 * we are going through the list from back to front
						 * so this is safe to do inside the loop*/
						mClients.remove(i);
					}
				}
				break;
			case 4:
				Bundle bd = msg.getData();
				RiverUser user = (RiverUser)bd.get("user");
				Toast.makeText(SocketService.this, "The data from Activity B "+user.userid+"   username:   "+user.username, Toast.LENGTH_SHORT).show();
				System.out.println("The data from Activity B "+user.userid+"   username:   "+user.username);
				new ThreadTest().start();
				
				SQLiteHelper sql = new SQLiteHelper(SocketService.this,"",null,0);
				sql.addData(sql.getWritableDatabase());
				break;
			case 5:
				Bundle bd1 = msg.getData();
				RiverUser user1 = (RiverUser)bd1.get("user");
				Toast.makeText(SocketService.this, "The data from Activity C "+user1.userid+"   username:   "+user1.username, Toast.LENGTH_SHORT).show();
				System.out.println("The data from Activity C "+user1.userid+"   username:   "+user1.username);
				new ThreadTest().start();
				
				SQLiteHelper sql1 = new SQLiteHelper(SocketService.this,"",null,0);
				sql1.selectData(sql1.getWritableDatabase());
				break;
			default:
				super.handleMessage(msg);
				break;
			}
		}

	}

	Timer timer;
	private class ThreadTest extends Thread{

		@Override
		public void run(){
			try {
				mMessenger.send(Message.obtain(null, MSG_SET_VALUE));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onCreate() {
		System.out.println("Restart service ......");
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("keep printing``````");
			}
		}, 1000, 1000);
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	/** Target we publish for clients to send messages to IncomingHandler*/
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	/** When binding to the service,we return an interface to our messenger
	 *  for sending messages to the service*/
	@Override
	public IBinder onBind(Intent intent) {
		Toast.makeText(this, "bind service", Toast.LENGTH_SHORT).show();
		System.out.println("binding service...");
		return mMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		timer = null;
		Toast.makeText(this, "exit service...", Toast.LENGTH_SHORT).show();
		System.out.println("exit service...");
		super.onDestroy();
	}
}
