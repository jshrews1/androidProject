package com.estimote.examples.demos;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Data;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		Button submit = (Button) findViewById(R.id.regButton);
		
		submit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EditText username = (EditText) findViewById(R.id.username);

				
				String myId = username.getText().toString();

				
				Bundle userInfo = new Bundle();
				userInfo.putString("myId", myId);
				
				Intent a = new Intent(Login.this, ListBeaconsActivity.class);
				a.putExtra(ListBeaconsActivity.EXTRAS_TARGET_ACTIVITY, NotifyDemoActivity.class.getName());
				a.putExtras(userInfo);
				startActivity(a);
			}
		});
		
	}

	
}
