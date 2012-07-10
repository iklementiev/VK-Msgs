package com.vkmsgs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.vkmsgs.LoginActivity.TransitionType;
import com.vkmsgs.api.Message;
import com.vkmsgs.api.User;
import com.vkmsgs.api.VKApi;

public class DialogsActivity extends Activity implements OnClickListener {
	
	public static TransitionType transitionType;
	public static Map<Long, User> users = new HashMap<Long, User>();
	
	Button msgComposeBtn;
	public static ArrayList<Message> dialogs = null;
	public static DialogsArrayAdapter adapter = null;
	//ArrayList<User> users = null;	

	ListView listView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogs);

		msgComposeBtn = (Button)findViewById(R.id.msgComposeBtn);
		msgComposeBtn.setOnClickListener(this);
		
		ProgressBar progressBar1 = (ProgressBar)findViewById(R.id.progressBar1);
		progressBar1.setVisibility(View.VISIBLE);
		
		MyTask mt = new MyTask();
	    mt.execute();
	}
	
	protected void onPause() {
	    super.onPause();
	    helper.WriteDebug("dialogs Paused");
	}
	
	protected void onResume() {
	    super.onPause();
	    helper.WriteDebug("dialogs Resume");
	}	
	
	class MyTask extends AsyncTask<Void, Void, Void> {	    
	    @Override
	    protected void onPreExecute() {
	      super.onPreExecute();	      
	    }

	    @Override
		protected Void doInBackground(Void... params) {		    
		    try {
		    	dialogs = VKApi.getDialogs(0, 0);
			} catch (MalformedURLException e) {
				helper.WriteError(e.getMessage());
			} catch (IOException e) {
				helper.WriteError(e.getMessage());
			} catch (JSONException e) {
				helper.WriteError(e.getMessage());
			}
	    	
	      return null;
	    }
   
	    @Override
	    protected void onPostExecute(Void result) {
	      super.onPostExecute(result);	      
	      
	      if (dialogs != null) {	    	 
	    	ArrayList<Long> uids = new ArrayList<Long>(); 
	    	for (Message msg : dialogs) {
				if (msg.chat_id > 0) {
					for (Long id : msg.chat_active) {
						if (!uids.contains(id))
							uids.add(id);
					}
				} else {
					if (!uids.contains(msg.uid))
						uids.add(msg.uid);
				}
			}    	
	    	
	    	ArrayList<User> us = VKApi.getUsers(uids);
	    	for (User user : us) {
				users.put(user.uid, user);
			}	    	
	    	
		    listView = (ListView) findViewById(R.id.lvDialogs);
			
			adapter = new DialogsArrayAdapter(DialogsActivity.this, dialogs);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Message mesg = (Message) listView.getItemAtPosition(position);
					
					helper.WriteDebug("ОТКРЫВАЕМ ДИАЛОГ " + mesg.uid);
					
					Intent intent = new Intent(DialogsActivity.this, ConversationActivity.class);
					intent.putExtra("uid", mesg.uid);
					intent.putExtra("title", mesg.title);
					startActivity(intent);	
					
					transitionType = TransitionType.SlideLeft;  
		            overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);						
				}
			});
			
			EditText dialogsSearch = (EditText)findViewById(R.id.dialogsSearch);
			dialogsSearch.addTextChangedListener(new TextWatcher() {
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
			helper.WriteWarn("Не удалось получить список диалогов");
			//Toast.makeText(this, "Не удалось получить список диалогов", Toast.LENGTH_SHORT).show();
		}
	      
	      FrameLayout fl = (FrameLayout)findViewById(R.id.frame4bar);
	      fl.setVisibility(View.GONE);
	      
	      ListView lv = (ListView)findViewById(R.id.lvDialogs);
	      lv.setVisibility(View.VISIBLE);
	    }		
	  }
	
	public void onClick(View v) {        
        switch(v.getId()) {
        case R.id.msgComposeBtn:
        	//Intent intent = new Intent(this, WriteMessage.class);  
            //startActivity(intent);
        	break;
        }
	}
}
