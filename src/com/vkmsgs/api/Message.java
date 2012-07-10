package com.vkmsgs.api;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vkmsgs.helper;

public class Message implements Comparable {
	//http://vk.com/pages?oid=-1&p=%D0%A4%D0%BE%D1%80%D0%BC%D0%B0%D1%82_%D0%BE%D0%BF%D0%B8%D1%81%D0%B0%D0%BD%D0%B8%D1%8F_%D0%BB%D0%B8%D1%87%D0%BD%D1%8B%D1%85_%D1%81%D0%BE%D0%BE%D0%B1%D1%89%D0%B5%D0%BD%D0%B8%D0%B9
	
    public long mid;
    public long uid;
    public Long date;
    public int read_state;
    public int out;
    
    public boolean isChecked;
    
    public String title;
    public String body;
   
    public ArrayList<Attachment> attachments=new ArrayList<Attachment>();
	
    //fwd_messages
		//массив пересланных сообщений (если есть)
	
    public long chat_id = -1;
    public ArrayList<Long> chat_active; 
    public int users_count; 
    public long admin_id;
    public boolean is_out;
		
    public static Message parse(JSONArray a) throws JSONException {
        Message m = new Message();
        m.mid = a.getLong(1);
        m.uid = a.getLong(3);
        m.date = a.getLong(4);
        m.title = VKApi.unescape(a.getString(5));
        m.body = VKApi.unescape(a.getString(6));
        int flag = a.getInt(2);
        /*m.read_state = ((flag & UNREAD) != 0)?"0":"1";
        m.is_out = (flag & OUTBOX) != 0;
        if ((flag & BESEDA) != 0) {
            m.chat_id = a.getLong(3) & 63;//cut 6 last digits
            JSONObject o= a.getJSONObject(7);
            m.uid = o.getLong("from");
        }*/
        //m.attachment = a.getJSONArray(7); TODO
        return m;
    }
    
    
    
    public static Message parse(JSONObject o, boolean from_history, long history_uid, boolean from_chat, long me) throws NumberFormatException, JSONException{
        Message m = new Message();
        if(from_chat){
            long from_id=o.getLong("from_id");
            m.uid = from_id;
            m.is_out=(from_id==me);
        }else if(from_history){
            m.uid=history_uid;
            Long from_id = o.getLong("from_id");
            m.is_out=!(from_id==history_uid);
            
            if (o.getInt("out") == 1)
            	m.is_out = true;
            else
            	m.is_out = false;
        }else{
            //тут не очень, потому что при получении списка диалогов если есть моё сообщение, 
        	//которое я написал в беседу, то в нём uid будет мой. Хотя в других случайх uid всегда собеседника.
            m.uid = o.getLong("uid");            
            
        }
        m.mid = o.getLong("mid");
        m.date = o.getLong("date");
        if(!from_history && !from_chat)
            m.title = VKApi.unescape(o.getString("title"));
        m.body = VKApi.unescape(o.getString("body"));
        m.read_state = o.getInt("read_state");
        if(o.has("chat_id")) {
            m.chat_id=o.getLong("chat_id");
            String[] values = o.getString("chat_active").split(",");             
            ArrayList<Long> ar = new ArrayList<Long>();
                        
            for (String string : values) {
            	helper.WriteDebug("string="+string);
            	ar.add(Long.parseLong(string));            	
			}
            m.chat_active = ar;
        }

        JSONArray attachments=o.optJSONArray("attachments");
        JSONObject geo_json=o.optJSONObject("geo");
        m.attachments=Attachment.parseAttachments(attachments, 0, 0, geo_json);
        return m;
    }
    
    public void setChecked(boolean paramBoolean)
    {
      this.isChecked = paramBoolean;
    }
    /*
    
    public void toggle()
    {
      this.message.toggle();
    }
    
    public boolean isChecked()
    {
      return this.message.isChecked();
    }*/

    @Override
    public int compareTo(Object otherMessage){    
    	
        if(otherMessage instanceof Message){
            //throw new ClassCastException("Not a valid Message object!!");
        }
       
        Message tempCar = (Message)otherMessage;
       
        if(this.date > tempCar.date){
            return -1;
        }else if(this.date < tempCar.date){
            return 1;
        }else{
            return 0;
        }
    }
}
