package com.vkmsgs;

import java.text.MessageFormat;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class SettingsManager {

	public static void setSetting(Context context, String key, String value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor=prefs.edit();
        editor.putString(key, value);	        
        editor.commit();
        
        helper.WriteDebug("Сохранение настройки: " + key + "=" + value);
	}	
	
	public static String getStringSetting(Context context, String key) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        
		String value=prefs.getString(key, null);
        
        helper.WriteDebug("Чтение настройки: " + key + "=" + value);
		
		return value;
	}
	
	
	public static void setSetting(Context context, String key, long value) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Editor editor=prefs.edit();
        editor.putLong(key, value);	        
        editor.commit();
        
        helper.WriteDebug("Сохранение настройки: " + key + "=" + value);
	}
	
	public static long getLongSetting(Context context, String key) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        
		long value=prefs.getLong(key, 0);
        
        helper.WriteDebug("Чтение настройки: " + key + "=" + value);
		
		return value;
	}
}
