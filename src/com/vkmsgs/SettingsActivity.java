package com.vkmsgs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

public class SettingsActivity extends Activity  implements OnClickListener{
	
	Button btnExit;
	Button btnNotify;
	Button btnClearFiles;
	Button btnClearAvaCache;
	
	final int DIALOG_ITEMS = 1;
	String data[] = { "Звук", "Вибро", "Звук и вибро", "Отключено" };
	String data2[] = { "По имени", "По фамилии", "По userId" };
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings);

        btnExit = (Button)findViewById(R.id.btnExit);
        btnExit.setOnClickListener(this);   
        
        btnClearFiles = (Button)findViewById(R.id.btnClearFiles);
        btnClearFiles.setOnClickListener(this); 
        
        btnClearAvaCache = (Button)findViewById(R.id.btnClearAvaCache);
        btnClearAvaCache.setOnClickListener(this);
        
        
//        btnNotify = (Button)findViewById(R.id.btnNotify);
//        btnNotify.setOnClickListener(this);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt("Title");
        // выделяем элемент 
        //spinner.setSelection(2);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		      @Override
		      public void onItemSelected(AdapterView<?> parent, View view,
		          int position, long id) {
		        // показываем позиция нажатого элемента
		        Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
		      }
		      @Override
		      public void onNothingSelected(AdapterView<?> arg0) {
		    	  Toast.makeText(getBaseContext(), "Надо выделить", Toast.LENGTH_SHORT).show();  
		      }
       });
        
        
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner.setAdapter(adapter2);
        // заголовок
        spinner2.setPrompt("Title");
        // выделяем элемент 
        //spinner.setSelection(2);
        // устанавливаем обработчик нажатия
        spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
		      @Override
		      public void onItemSelected(AdapterView<?> parent, View view,
		          int position, long id) {
		        // показываем позиция нажатого элемента
		        Toast.makeText(getBaseContext(), "Position = " + position, Toast.LENGTH_SHORT).show();
		      }
		      @Override
		      public void onNothingSelected(AdapterView<?> arg0) {
		    	  Toast.makeText(getBaseContext(), "Надо выделить", Toast.LENGTH_SHORT).show();  
		      }
       });
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
	    
	    case R.id.btnExit:
        	
        	SettingsManager.setSetting(this, "access_token", null);
        	SettingsManager.setSetting(this, "user_id", 0);
        	
        	/*
        	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            Editor editor=prefs.edit();
            editor.putString("access_token", null);
            editor.putLong("user_id", 0);
            editor.commit();
            */
        	
            helper.WriteDebug("очистил токен");
            
            this.finish();
            
            Intent intentLogin = new Intent(this, LoginActivity.class);  
            startActivity(intentLogin);            
        	break;
	    case R.id.btnClearFiles:
	    	//FileCache.clear();
	    	break;
	    case R.id.btnClearAvaCache:
	    	ImageLoader imageLoader = new ImageLoader(this);
	    	imageLoader.clearCache();
	    	Toast.makeText(getBaseContext(), "Кеш аватаров очищен", Toast.LENGTH_SHORT).show();
	    	break;
	    	
	  }
	}	
}
