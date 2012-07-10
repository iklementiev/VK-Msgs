package com.vkmsgs;

import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.vkmsgs.LongPoll.LongPollTask;
import com.vkmsgs.api.LongPollServer;
import com.vkmsgs.api.VKApi;

public class MainActivity  extends TabActivity  {
	public account account=new account();
	static NotificationManager mNotifyMgr; 
	public static Context context;
	//LongPoll  lp = new LongPoll();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        context = this.getApplicationContext();
        account.restore(this);        
        setTabs();  

        mNotifyMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        LongPoll.Start();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        LongPoll.Stop();
    }
   
    
    private void setTabs()
	{
		addTab("Messages", R.drawable.tab_messages, DialogsActivity.class);
		addTab("Contacts", R.drawable.tab_contacts, ContactsActivity.class);		
		addTab("Search", R.drawable.tab_search, SearchActivity.class);
		addTab("Settings", R.drawable.tab_settigns, SettingsActivity.class);
	}
	
	private void addTab(String labelId, int drawableId, Class<?> c)
	{
		TabHost tabHost = getTabHost();
		Intent intent = new Intent(this, c);
		TabHost.TabSpec spec = tabHost.newTabSpec("tab" + labelId);	
		
		View tabIndicator = LayoutInflater.from(this).inflate(R.layout.tabs, getTabWidget(), false);
		
		TextView title = (TextView) tabIndicator.findViewById(R.id.title);
		title.setText(labelId);
		
		ImageView icon = (ImageView) tabIndicator.findViewById(R.id.icon);
		icon.setImageResource(drawableId);
		
		spec.setIndicator(tabIndicator);
		spec.setContent(intent);
		tabHost.addTab(spec);
	}
}
