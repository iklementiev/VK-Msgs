package com.vkmsgs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.view.View;

import com.vkmsgs.api.LongPollServer;
import com.vkmsgs.api.Message;
import com.vkmsgs.api.User;
import com.vkmsgs.api.VKApi;

public class LongPoll extends Application
{
	/*+1 	UNREAD 	сообщение не прочитано
	+2 	OUTBOX 	исходящее сообщение
	+4 	REPLIED 	на сообщение был создан ответ
	+8 	IMPORTANT 	помеченное сообщение
	+16 	CHAT 	сообщение отправлено через чат
	+32 	FRIENDS 	сообщение отправлено другом
	+64 	SPAM 	сообщение помечено как "Спам"
	+128 	DELЕTЕD 	сообщение удалено (в корзине)
	+256 	FIXED 	сообщение проверено пользователем на спам
	+512 	MEDIA 	сообщение содержит медиаконтент */
	
	
	
	
	private static final ScheduledExecutorService worker1 = Executors.newSingleThreadScheduledExecutor();
	
	public static void LongPollTask() {
		
	}
	
	public static Context context = MainActivity.context;
	
	public static LongPollTask lpt = new LongPollTask();
	
	public static void Start(){
		helper.WriteDebug("Start LongPollTask");
		lpt.execute();
	}
	
	public static void Stop(){
		helper.WriteDebug("Stop LongPollTask");
		lpt.cancel(true);
	}
	
	public static class LongPollTask extends AsyncTask<Void, String, Void> {	    
	    @Override
	    protected void onPreExecute() {
	      super.onPreExecute();	      
	    }

		@Override
		protected Void doInBackground(Void... params) {			
			LongPollServer lps = VKApi.messagesGetLongPollServer();
			
			while (true) {
				String request = "http://" + lps.server + "?act=a_check&key=" + lps.key +"&ts="+ lps.ts + "&wait=25&mode=2";
				helper.WriteInfo("DO=" + request);
				
				String root = VKApi.sendRequestInternal(request);
				helper.WriteInfo("resp=" + root);
				try {
					JSONObject response = new JSONObject(root);
					String ts = response.getString("ts");
					JSONArray updates = response.getJSONArray("updates");
					
					if (updates.length() == 0) {
						lps.ts = ts;
						continue;
					}
					else {						
						publishProgress(updates.toString());						
						lps.ts = ts;
						continue;
					}
				} catch (JSONException e) {
					continue;
				}
			}
		}
		
		@Override
	    protected void onProgressUpdate(String... values) {
	    	super.onProgressUpdate(values);
	      
	    	helper.WriteDebug("values size= " + values.length);
	    	helper.WriteDebug("values[0]= " + values[0]);
	    	
	    	//Toast.makeText(MainActivity.context, values[0], Toast.LENGTH_SHORT).show();
	      
	    	JSONArray array = null;
			try {
				array = new JSONArray(values[0]);
				processResponse(array);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}					      
		}

	    @Override
	    protected void onPostExecute(Void result) {
	      super.onPostExecute(result);
	    }
    }
	
	public static String sendReqToLongPoll(LongPollServer lps){
		String request = "http://" + lps.server + "?act=a_check&key=" + lps.key +"&ts="+ lps.ts + "&wait=25&mode=2";
		helper.WriteInfo("DO=" + request);
		
		String root = VKApi.sendRequestInternal(request);
		helper.WriteInfo("resp=" + root);
		try {
			JSONObject response = new JSONObject(root);
			String ts = response.getString("ts");
			JSONArray updates = response.getJSONArray("updates");
			
			if (updates.length() == 0) {
				lps.ts = ts;
				sendReqToLongPoll(lps);
			}
			else {
				processResponse(updates);
				lps.ts = ts;
				sendReqToLongPoll(lps);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	public static void processResponse(JSONArray array) {	
		for(int i = 0; i <  array.length(); ++i) {        	 
        	try {
				JSONArray arrayItem =  (JSONArray)array.get(i);
				int type = (Integer) arrayItem.get(0);					

/*				    0,$message_id,0 -- удаление сообщения с указанным local_id
				    1,$message_id,$flags -- замена флагов сообщения (FLAGS:=$flags)
				    2,$message_id,$mask[,$user_id] -- установка флагов сообщения (FLAGS|=$mask)
				    3,$message_id,$mask[,$user_id] -- сброс флагов сообщения (FLAGS&=~$mask)
				    4,$message_id,$flags,$from_id,$timestamp,$subject,$text,$attachments -- добавление нового сообщения
				    8,-$user_id,0 -- друг $user_id стал онлайн
				    9,-$user_id,$flags -- друг $user_id стал оффлайн ($flags равен 0, если пользователь покинул сайт (например, нажал выход) и 1, если оффлайн по таймауту (например, статус away))


				    51,$chat_id,$self -- один из параметров (состав, тема) беседы $chat_id были изменены. $self - были ли изменения вызываны самим пользователем
				    61,$user_id,$flags -- пользователь $user_id начал набирать текст в диалоге. событие должно приходить раз в ~5 секунд при постоянном наборе текста. $flags = 1
				    62,$user_id,$chat_id -- пользователь $user_id начал набирать текст в беседе $chat_id.
				    70,$user_id,$call_id -- пользователь $user_id совершил звонок имеющий идентификатор $call_id, дополнительную информацию о звонке можно получить используя метод voip.getCallInfo.*/
			    
				switch(type) {
		        case 0:
		        	break;
		        case 1:
		        	break;
		        case 2:
		        	break;
		        case 3:
		        	break;
		        case 4:
		        	Long message_id = Long.parseLong(arrayItem.get(1).toString());
		        	int flags = Integer.parseInt(arrayItem.get(2).toString());
		        	Long from_id = Long.parseLong(arrayItem.get(3).toString());
		        	Long timestamp = Long.parseLong(arrayItem.get(4).toString());
		        	String subject = arrayItem.get(5).toString();
		        	String text = arrayItem.get(6).toString();
		        	
		        	/*int x = flags;
		        	while (x > 1) {
		        		helper.WriteDebug("x=" + Integer.toString(flags));
		        		int n = NOD(x, 2);
		        		helper.WriteDebug("nod=" + Integer.toString(n));
						x = x - n;
						helper.WriteDebug("x=" + Integer.toString(n));
					}*/
		        	
		        	addInboxMessage(message_id, flags, from_id, timestamp, subject, text);
		        	break;
		        case 8:
		        	setUserOnline(Long.parseLong(arrayItem.get(1).toString()));
		        	break;
		        case 9:
		        	setUserOffline(Long.parseLong(arrayItem.get(1).toString()));
		        	break;
		        case 51:
		        	break;
		        case 61:
		        	//юзер начал набирать текст в диалоге
		        	Long uid = Long.parseLong(arrayItem.get(1).toString());
		        	setUserTyping(uid);
		        	break;
		        case 62:
		        	//юзер начал набирать текст в беседе
		        	Long uid1 = Long.parseLong(arrayItem.get(1).toString());
		        	break;
		        case 70:
		        	break;
				}
			} catch (JSONException e) {
				helper.WriteError(e.getMessage().toString());
			}
        }
	}
	
	private static void setUserTyping(Long uid) {		
		long a = ConversationActivity.uid;
        long b = uid;
        
		if (ConversationActivity.uid != 0 && a == b) {
			ConversationActivity.userStatusText.setText("Набирает сообщение...");
			
			Runnable clearStatus = new Runnable() {
			    public void run() {
			    	ConversationActivity.userStatusText.setText("");
			    }
			};
   		   	worker1.schedule(clearStatus, 1, TimeUnit.SECONDS);	
		} else {
			helper.WriteWarn(" опять не равны (typing...)");
		}
	}
	
	private static void addInboxMessage(Long message_id, int flags, Long from_id, Long timestamp, String subject, String text) {
		int icon = R.drawable.attach_pressed;
		
		String userName = VKApi.getUserName(from_id);
		
        CharSequence tickerText = "Сообщение от " + userName + ". " + text; 
        
        long when = System.currentTimeMillis(); 
        
        Notification notification = new Notification(icon, tickerText, when);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        
        Context context = MainActivity.context; 
        CharSequence contentTitle = "Сообщение от " + userName; 
        CharSequence contentText = text;
        
        Intent notificationIntent = new Intent( 
        		context, ConversationActivity.class); 
        notificationIntent.putExtra("uid", from_id);
        notificationIntent.putExtra("title", subject);
		
        PendingIntent contentIntent = PendingIntent.getActivity( 
        		context, 0, notificationIntent, 0); 
        
                
        notification.setLatestEventInfo(context, contentTitle, 
                contentText, contentIntent); 
        
        MainActivity.mNotifyMgr.notify(101, notification);
        
        Vibrator v = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);         
        v.vibrate(300);
        
        helper.WriteDebug("ConversationActivity.uid= '" + ConversationActivity.uid + "'");
        helper.WriteDebug("from_id= '" + from_id + "'");
        
        long a = ConversationActivity.uid;
        long b = from_id;
        
        if (ConversationActivity.uid != 0 && a == b) {
        	helper.WriteWarn(ConversationActivity.uid + "==" + from_id);
        	
        	Message m = new Message();
        	m.body = text;
        	m.date = System.currentTimeMillis();
        	m.is_out = false;        	
        	
        	ConversationActivity.messages.add(m);
        	
        	
        } else {        	
        	helper.WriteWarn(ConversationActivity.uid + "!=" + from_id); 
        }
        
        int i =0;
        for (Message msg : DialogsActivity.dialogs) {
			if (msg.uid == from_id) {
				helper.WriteDebug("msg.uid " + msg.uid);
				DialogsActivity.dialogs.get(i).body = text;
				DialogsActivity.dialogs.get(i).read_state = 0;
				DialogsActivity.dialogs.get(i).date = timestamp;				
			}
			i++;
		}
        
        Collections.sort(DialogsActivity.dialogs);
        
        ConversationActivity.adapter.notifyDataSetChanged();
        DialogsActivity.adapter.notifyDataSetChanged();
	}

	private static void setUserOffline(long uid) {
		helper.WriteDebug("user " + uid + " offline");
		
	}

	private static void setUserOnline(long uid) {
		helper.WriteDebug("user " + uid + " online");
	}
}

