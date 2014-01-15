package com.servicedemo;

import com.servicedemo.entity.RiverUser;
import com.servicedemo.service.SocketService;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

public class BActivity extends SocketBaseActivity implements SocketBaseActivity.ServiceCallBack{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_b);
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sayHello();
			}
		});
		Button button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(BActivity.this,CActivity.class));
			}
		});
		System.out.println("Activity B starting");
		
		setCallback(this);
	}

	public void sayHello() {
		RiverUser user = new RiverUser();
		user.userid = 10000;
		user.username = "River Candy";
		Message message = new Message();
		message.what = 4;
		
		Bundle data = new Bundle();
		data.putSerializable("user", user);
		
		message.setData(data);
		if (!mBound) return;
		try {
			mService.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void callback(Message msg) {
		switch (msg.what) {
		case SocketService.MSG_SET_VALUE:
			Bundle bd = msg.getData();
			RiverUser user = (RiverUser)bd.get("user");
			Toast.makeText(BActivity.this, "Service respond B "+user.userid+"   username:   "+user.username, Toast.LENGTH_SHORT).show();
			System.out.println("Service respond B  "+user.userid+"   username:   "+user.username);
			break;
		default:
			break;
		}
	}

}
