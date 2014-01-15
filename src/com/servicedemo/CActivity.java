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

public class CActivity extends SocketBaseActivity implements SocketBaseActivity.ServiceCallBack{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_c);
		Button button = (Button)findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sayHello();
			}
		});
		System.out.println("Activity C starting");
		setCallback(this);
	}

	public void sayHello() {
		RiverUser user = new RiverUser();
		user.userid = 10098;
		user.username = "River";
		Message message = new Message();
		message.what = 5;
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
			Toast.makeText(CActivity.this, "Service respond C "+user.userid+"   username:   "+user.username, Toast.LENGTH_SHORT).show();
			System.out.println("Service respond C "+user.userid+"   username:   "+user.username);
			break;
		default:
			break;
		}
	}

}
