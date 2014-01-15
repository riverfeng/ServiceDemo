package com.servicedemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import com.servicedemo.service.SocketService;

/*
 * if you want to communicate with the service,extend it.
 * */
public class SocketBaseActivity extends BaseActivity {

	/** Messenger for communicating with the service*/
	Messenger mService = null;

	/** Flag indicating whether we have called bind on the service*/
	boolean mBound;
	
	class IncomingHandler extends Handler{
		
		@Override
		public void handleMessage(Message msg){
			switch (msg.what) {
			case SocketService.MSG_SET_VALUE:
				serviceCallback.callback(msg);
				break;
			default:
				break;
			}
		}
		
	}

	private ServiceCallBack serviceCallback;
	
	public void setCallback(ServiceCallBack callback){
		this.serviceCallback = callback;
	}
	
	public interface ServiceCallBack{
		public void callback(Message msg);
	}
	
	/** Target we publish for clients to send messages to Incominghandler*/
	final Messenger mMessenger = new Messenger(new IncomingHandler());
	
	/** Class for interacting with the main interface of the service*/
	private ServiceConnection mConnection = new ServiceConnection(){

		/** This is called when the connection with the service has been established,
		 *  giving us the object we can use to interact with the service.We are communicating with
		 *  the service using a Messenger,so here we get a client-side repre sentation of that from 
		 *  the raw IBinder object.*/
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			try {
				/*
				 * we want to monitor the service for as log as we are connected to it
				 * */
				mService = new Messenger(service);
				Message msg = Message.obtain(null, SocketService.MSG_REGISTER_CLIENT);
				msg.replyTo = mMessenger;
				mService.send(msg);
				
			} catch (RemoteException e) {
				/*
				 * In this case the service has crashed before we could even 
				 * do anything with it;we can count on soon being disconnected.
				 * (and then reconnected if it can be restarted)
				 * so there is no need to do anything here.
				 * */
			}
		}

		/** This is called when the connection with the service has been unexpectedly disconnected --
		 *  that is,its process crashed.*/
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			mBound = false;
		}

	};
	
	@Override
	protected void onStart(){
		super.onStart();
		System.out.println("bind to Service");
		// Bind to service
		bindService(new Intent(this.getApplicationContext(),SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
		mBound = true;
	}
	
	@Override
	public void onStop() {
		super.onStop();
		// Unbind from the service
		if(mBound){
			if(mService != null){
				try {
					Message msg = Message.obtain(null, SocketService.MSG_UNREGISTERED_CLIENT);
					msg.replyTo = mMessenger;
					mService.send(msg);
				} catch (RemoteException e) {
					/* There is nothing special we need to do if the service has crashed.*/
				}
			}
			unbindService(mConnection);
			mBound = false;
		}
	}
}
