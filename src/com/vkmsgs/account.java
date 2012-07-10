package com.vkmsgs;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class account {
    public String access_token;
    public long user_id;
    
    public void save(Context context){
        SettingsManager.setSetting(context, "access_token", access_token);
        SettingsManager.setSetting(context, "user_id", user_id);
    }
    
    public void restore(Context context){
    	access_token = SettingsManager.getStringSetting(context, "access_token");
    	user_id = SettingsManager.getLongSetting(context, "user_id");
    	       
        helper.WriteLog("восстанавливаю аккаунт: access_token=" +access_token + ", user_id=" + user_id);
    }
}
