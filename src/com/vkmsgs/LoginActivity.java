package com.vkmsgs;


import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.vkmsgs.api.VKApi;

public class LoginActivity extends Activity implements OnClickListener {	
	
	public static enum TransitionType {  
        Zoom, SlideLeft, Diagonal  
	}
	
	public static TransitionType transitionType;
	account account=new account();
    VKApi api;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.login);
				
		Button loginBtn = (Button)findViewById(R.id.loginBtn);  
		loginBtn.setOnClickListener(this); 	
		
		FrameLayout frameReg = (FrameLayout)findViewById(R.id.frameReg);
		frameReg.setOnClickListener(this);
				
		helper.WriteInfo("Я работаю на Api level=" + Build.VERSION.SDK_INT);
		
		if (Build.VERSION.SDK_INT < 8) {
			helper.WriteInfo("Надо юзать лонг пул");
		} else {
			helper.WriteInfo("Уже можно юзать пуш");
		}
		
		account.restore(this);
        
        if(account.access_token!=null) {
            api=new VKApi(account.access_token);
            
            finish();
            
            Intent intent = new Intent(this, MainActivity.class);  
            startActivity(intent);
        }		
    }
    
    public void onClick(View v) {        
        switch(v.getId()) {
        case R.id.loginBtn:        	
        	EditText phoneNumber   = (EditText)findViewById(R.id.etPhoneNumber);
        	EditText password   = (EditText)findViewById(R.id.etPassword);
        	
        	String login = phoneNumber.getText().toString();
        	String passwd = password.getText().toString();
        	        	
        	if (helper.isNullOrEmpty(login) && helper.isNullOrEmpty(passwd)) {
        		Toast.makeText(this, "Введите номер телефона и пароль", Toast.LENGTH_SHORT).show();
			} else if (helper.isNullOrEmpty(login)) {
				Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
			} else if (helper.isNullOrEmpty(passwd)) {
				Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
			} else {   		
        		String result = "";
				
				result = VKApi.Auth(login, passwd);				
        		            		            		
        		if (result.equals("401")) {
        			Toast.makeText(this, "401: Неверные логин или пароль", Toast.LENGTH_SHORT).show();	
				} else if (result.equals("404")) {
					Toast.makeText(this, "404: адрес найдено", Toast.LENGTH_SHORT).show();
				} else if (result.equals("Socket is not connected")) {
					Toast.makeText(this, "Нет соединения", Toast.LENGTH_SHORT).show();
				}
        		else {						
					try {
						JSONObject jToken = new JSONObject(result);
						
						String access_token = jToken.getString("access_token");
						long user_id = jToken.getLong("user_id");
						
						account.access_token= access_token;
		                account.user_id=user_id;
								                
						account.save(LoginActivity.this);		
						
						if (access_token != null) {
							finish();

				            Intent intent = new Intent(this, MainActivity.class);  
				            startActivity(intent);
						}
						
					} catch (JSONException e) {
						helper.WriteError(e.getMessage());
					} 
        		}
        	}
            break;
        case R.id.frameReg:
        	Intent intent = new Intent(this, RegistrationActivity.class);  
            startActivity(intent);
        	
        	transitionType = TransitionType.SlideLeft;  
            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
        	
        	break;
        }
    }
}