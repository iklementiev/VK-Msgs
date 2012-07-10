package com.vkmsgs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.vkmsgs.LoginActivity.TransitionType;
import com.vkmsgs.api.User;
import com.vkmsgs.api.VKApi;

public class ContactsActivity extends Activity implements OnClickListener {
	public static TransitionType transitionType;
	
	ListView listView;
	EditText contactsSearch;
	ToggleButton btnOnline;
	ToggleButton btnFriends;
	ToggleButton btnPhoneContacts;
	
	ArrayAdapter<User> adapter;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
		setContentView(R.layout.contacts);

		btnOnline = (ToggleButton)findViewById(R.id.btnOnline);		
		btnOnline.setOnClickListener(this);
    	
		btnFriends = (ToggleButton)findViewById(R.id.btnFriends);
		btnFriends.setOnClickListener(this);
		btnFriends.setSelected(true);
		
		btnPhoneContacts = (ToggleButton)findViewById(R.id.btnPhoneContacts);
		btnPhoneContacts.setOnClickListener(this);
		
		String code = "";
		
		code += "var a=API.friends.get({\"fields\":\"photo,online\",\"order\":\"hints\"});";
		//code += "var b=a@.uid;";
		//code += "var c=API.getProfiles({\"uids\":a});";
		//code += "var n=c@.first_name + \" \" + c@.last_name;";
		code += "return a;";
		ArrayList<User> users = null;
		try {
			users = VKApi.getFriendsExec(code);					
		} catch (Exception e) {
			helper.WriteError("не могу получить список контактов " + e.getMessage());
		}		
		
		if (users != null) {
			listView = (ListView) findViewById(R.id.lvContacts);
			
			adapter = new ContactsArrayAdapter(this, users);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					User us = (User) listView.getItemAtPosition(position);
					
					helper.WriteDebug("ОТКРЫВАЕМ Контакт " + us.uid);
		
					Intent intent = new Intent(ContactsActivity.this, ConversationActivity.class);
					intent.putExtra("uid", us.uid);
					intent.putExtra("name", us.first_name + " " + us.last_name);
					startActivity(intent);	
					
					transitionType = TransitionType.SlideLeft;  
		            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
				}
			});
			
			contactsSearch = (EditText)findViewById(R.id.contactsSearch);
			contactsSearch.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					helper.WriteInfo("Ищу: " + s.toString());
					adapter.getFilter().filter(s.toString());
				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					// TODO Auto-generated method stub
					
				}
			});	
		}
		else {
			helper.WriteWarn("Не удалось получить список контактов");
			Toast.makeText(this, "Не удалось получить список контактов", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void onClick(View v) {        
        switch(v.getId()) {
        case R.id.btnOnline:
        	//btnOnline.setChecked(true);
        	btnFriends.setChecked(false);
        	btnPhoneContacts.setChecked(false);
        	adapter.getFilter().filter("@online");
        	break;
        case R.id.btnFriends:
        	btnOnline.setChecked(false);
        	//btnFriends.setSelected(true);
        	btnPhoneContacts.setChecked(false);
	    	adapter.getFilter().filter("@all");
	    	break;
        case R.id.btnPhoneContacts:
        	btnOnline.setChecked(false);
        	btnFriends.setChecked(false);
        	//btnPhoneContacts.setSelected(false);
	    	adapter.getFilter().filter("@all");
	    	break;
        }
	}
}
