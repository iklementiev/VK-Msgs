package com.vkmsgs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.vkmsgs.LoginActivity.TransitionType;
import com.vkmsgs.api.User;
import com.vkmsgs.api.VKApi;

public class SearchActivity extends Activity implements OnClickListener {
	public static TransitionType transitionType;
	
	ListView listView;
	EditText contactsSearch;
	
	ArrayAdapter<User> adapter;
	
	
	public final static String ITEM_TITLE = "title";  
    public final static String ITEM_CAPTION = "caption";  
      
    public Map<String,?> createItem(String title, String caption) {  
        Map<String,String> item = new HashMap<String,String>();  
        item.put(ITEM_TITLE, title);  
        item.put(ITEM_CAPTION, caption);  
        return item;  
    }  
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);

        ArrayList<User> userRequests = VKApi.friendsGetRequestsExec();
        
        ArrayList<User> userSuggestions = VKApi.friendsGetSuggestionsExec();
        
        SeparatedListAdapter adapter = new SeparatedListAdapter(this);        
        
        if (userRequests != null) {
        	ContactsArrayAdapter adapter2 = new ContactsArrayAdapter(this, userRequests);
        	adapter.addSection("Заявки в друзья", adapter2);
        }
        
        if (userSuggestions != null) {
        	ContactsArrayAdapter adapter3 = new ContactsArrayAdapter(this, userSuggestions);
        	adapter.addSection("Возможные друзья", adapter3);
        }
        
        listView = (ListView) findViewById(R.id.lvContacts); 
        listView.setAdapter(adapter); 
        
        listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				User us = (User) listView.getItemAtPosition(position);
				
				Intent intent = new Intent(SearchActivity.this, UserProfileActivity.class);
				intent.putExtra("uid", us.uid);
				intent.putExtra("name", us.first_name + " " + us.last_name);
				startActivity(intent);	
				
				transitionType = TransitionType.SlideLeft;  
	            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
			}
		});
        
        
        /*
        
        for (User user : userSuggestions) {
        	userRequests.add(user);
		}        
        
                
        
        if (userRequests != null) {
        	listView = (ListView) findViewById(R.id.lvContacts);
			
			adapter = new ContactsArrayAdapter(this, userRequests);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					User us = (User) listView.getItemAtPosition(position);
					
					helper.WriteDebug("ОТКРЫВАЕМ Контакт " + us.uid);
		
					Intent intent = new Intent(SearchActivity.this, UserProfileActivity.class);
					intent.putExtra("uid", us.uid);
					intent.putExtra("name", us.first_name + " " + us.last_name);
					startActivity(intent);	
					
					transitionType = TransitionType.SlideLeft;  
		            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
				}
			});
		}*/
	}
	
	public void onClick(View v) {        
        switch(v.getId()) {
        case R.id.regBackBtn:
        	//finish();
        	break;
        }
	}
}
