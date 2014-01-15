package com.servicedemo;

import android.app.Activity;
import android.widget.Toast;


/* BaseActivity includes fileds and methods that each activity needs.
 * such as,showToast()*/
public class BaseActivity extends Activity {

	public void show(String msg)
	{
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
	
}
