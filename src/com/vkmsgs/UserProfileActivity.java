package com.vkmsgs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vkmsgs.api.User;
import com.vkmsgs.api.VKApi;

public class UserProfileActivity extends Activity implements OnClickListener {
	Button btnBack;
	Button btnAddToFriend;
	Button btnDiscard;
	ImageView ivPhoto;
	ImageView ivOnline;
	TextView tvName;
	
	Long uid;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                
		setContentView(R.layout.userprofile);
		
		tvName = (TextView)findViewById(R.id.tvName);
		ivPhoto  = (ImageView)findViewById(R.id.ivPhoto);
		ivOnline = (ImageView)findViewById(R.id.ivOnline);
				
		btnBack = (Button)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		
		btnAddToFriend = (Button)findViewById(R.id.btnAddToFriend);
		btnAddToFriend.setOnClickListener(this);
		
		btnDiscard = (Button)findViewById(R.id.btnDiscard);
		btnDiscard.setOnClickListener(this);		
		
		Bundle extras = getIntent().getExtras(); 
		if(extras !=null) {		
			uid = extras.getLong("uid");
						
			tvName.setText(Long.toString(uid));			
			
			User user = VKApi.getUser(uid);		
			
			tvName.setText(user.first_name + " " + user.last_name);
			
			if (user.online) {
				ivOnline.setVisibility(View.VISIBLE);
			}
			
			ImageLoader imageLoader=new ImageLoader(this.getApplicationContext());
			imageLoader.DisplayImage(user.photo_big, ivPhoto);
		} else {
			Toast.makeText(this, "Параметры не переданы", Toast.LENGTH_SHORT).show();
		}		
	}
	
	public void onClick(View v) {        
        switch(v.getId()) {
        case R.id.btnBack:
        	finish();
        	break;
        case R.id.btnAddToFriend:
        	finish();
        	break;
        case R.id.btnDiscard:
        	finish();
        	break;
        }
	}
}