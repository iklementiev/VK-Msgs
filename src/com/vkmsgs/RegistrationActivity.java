package com.vkmsgs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.vkmsgs.api.VKApi;

public class RegistrationActivity extends Activity implements OnClickListener, View.OnFocusChangeListener  {
	Button regBackBtn;
	Button regBtn;
	EditText etPhone;
	EditText etFirstName;
	EditText etLastName;
	Boolean checkPhone = false;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		setContentView(R.layout.registration);
		
		regBackBtn = (Button)findViewById(R.id.regBackBtn);
		regBackBtn.setOnClickListener(this);
		
		regBtn = (Button)findViewById(R.id.regBtn);
		regBtn.setOnClickListener(this);
		
		etPhone = (EditText)findViewById(R.id.etPhoneNumber);
		etPhone.setOnFocusChangeListener(this);
		
		etFirstName = (EditText)findViewById(R.id.etName);
		etFirstName.setOnFocusChangeListener(this);
		
		etLastName = (EditText)findViewById(R.id.etSurname);
		etLastName.setOnFocusChangeListener(this);	

	}
	public void onFocusChange(View v, boolean hasFocus) {
		 switch(v.getId()) {
		 case R.id.etPhoneNumber:
			 if(!hasFocus){              
				//etPhone = (EditText)findViewById(R.id.etPhoneNumber);             	
         		if (checkPhone(etPhone.getText().toString())) {
         			drawOk(R.id.ivCheckPhone);
         			checkPhone = true;
         		}
				else
					drawWarn(R.id.ivCheckPhone);             	
            }      
        	break;
		 case R.id.etName:
			 if(!hasFocus){ 
				 //etFirstName = (EditText)findViewById(R.id.etName);
				 if (checkName(etFirstName.getText().toString()))
					 drawOk(R.id.ivCheckName);
				 else
					 drawWarn(R.id.ivCheckName);
			 }
			 break;
		 case R.id.etSurname:
			 if(!hasFocus){ 
				 //etLastName = (EditText)findViewById(R.id.etSurname);
				 if (checkSurname(etLastName.getText().toString()))
					 drawOk(R.id.ivCheckSurname);
				 else
					 drawWarn(R.id.ivCheckSurname);
			 }
			 break;
		 }
	}
	
	private void drawOk(int id) {
		ImageView imageView1 = (ImageView)findViewById(id);
		imageView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.ok));
		imageView1.setVisibility(View.VISIBLE);
	}
	
	private void drawWarn(int id) {
		ImageView imageView1 = (ImageView)findViewById(id);
		imageView1.setBackgroundDrawable(getResources().getDrawable(R.drawable.error));
		imageView1.setVisibility(View.VISIBLE);
	}
	
	public void onClick(View v) {        
        switch(v.getId()) {
        case R.id.regBackBtn:
        	finish();
        	break;
        case R.id.regBtn:
        	//etPhone = (EditText)findViewById(R.id.etPhoneNumber);
        	String phone = etPhone.getText().toString();
        	
        	//EditText etName = (EditText)findViewById(R.id.etName);
        	String firstName = etFirstName.getText().toString();
        	
        	//EditText etSurname = (EditText)findViewById(R.id.etSurname);
        	String lastName = etLastName.getText().toString();
        	
        	if (checkPhone == false) {
        		if (!checkPhone(phone)) {
            		Toast.makeText(this, "Введите телефон", Toast.LENGTH_SHORT).show();
        		}
        	}
        	
        	if (checkPhone == false) {
        		Toast.makeText(this, "Введите телефон", Toast.LENGTH_SHORT).show();
			}
        	else if (!checkName(firstName)) {
				Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
			} else if (!checkSurname(lastName)) {
				Toast.makeText(this, "Введите фамилию", Toast.LENGTH_SHORT).show();
			} else {
				VKApi.authSignup(phone,firstName,lastName);
			}
        	
        	break;
        }
	}

	protected boolean checkPhone(String phone) {		
		if (!helper.isNullOrEmpty(phone)) {
			return VKApi.checkPhone(phone);
		}
		return false;
	}
	
	protected boolean checkName(String name) {		
		if (helper.isNullOrEmpty(name))
			return false;		
		if (checkNameFormat(name))
			return true;		
		return false;
	}
	

	protected boolean checkSurname(String surname) {
		if (helper.isNullOrEmpty(surname))
			return false;		
		if (checkNameFormat(surname))
			return true;		
		return false;
	}
	
	private Pattern pattern = Pattern.compile("[a-zA-Zа-яА-Я]+");
	
	private boolean checkNameFormat(String name) {
		Matcher matcher = pattern.matcher(name);
        if (matcher.matches()) {
        	helper.WriteInfo(name + " прошел проверку");
            return true;
        }
        else {
        	helper.WriteWarn(name + " не прошел проверку");
            return false;
        }
	}
}
