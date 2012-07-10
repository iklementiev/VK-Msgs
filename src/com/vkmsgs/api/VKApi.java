package com.vkmsgs.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.text.Html;

import com.vkmsgs.Const;
import com.vkmsgs.Params;
import com.vkmsgs.helper;

public class VKApi {

	public VKApi(String access_token){
        this.access_token=access_token;
        this.api_id=Const.API_ID;
    }
    
    static String access_token;
    String api_id;
    
	public static String Auth(String login, String password) {
			//http://vk.com/developers.php?oid=-1&p=%D0%9F%D1%80%D0%B0%D0%B2%D0%B0_%D0%B4%D0%BE%D1%81%D1%82%D1%83%D0%BF%D0%B0_%D0%BF%D1%80%D0%B8%D0%BB%D0%BE%D0%B6%D0%B5%D0%BD%D0%B8%D0%B9
	        //+1      Пользователь разрешил отправлять ему уведомления.
	        //+2      Доступ к друзьям.
	        //+4      Доступ к фотографиям.
	        //+8      Доступ к аудиозаписям.
	        //+16     Доступ к видеозаписям.
	        //+32     Доступ к предложениям.
	        //+64     Доступ к вопросам.
	        //+128    Доступ к wiki-страницам.
	        //+256    Добавление ссылки на приложение в меню слева.
	        //+512    Добавление ссылки на приложение для быстрой публикации на стенах пользователей.
	        //+1024   Доступ к статусам пользователя.
	        //+2048   Доступ заметкам пользователя.
	        //+4096   (для Desktop-приложений) Доступ к расширенным методам работы с сообщениями.
	        //+8192   Доступ к обычным и расширенным методам работы со стеной.
	        //+65536  offline
	        //+131072 Доступ к документам пользователя.
	        //+262144 Доступ к группам пользователя.
		
		int settings=1+2+4+8+16+32+64+128+1024+2048+4096+8192+65536+131072+262144;
			
		String url = "https://api.vk.com/oauth/token?grant_type=password&client_id=" + Const.API_ID 
				+ "&client_secret=" + Const.API_Key
				+ "&username=" + login
				+ "&password=" + password
				+ "&scope=" + settings;
		
		return helper.DoRequest(url);		
	}
	
	private final static int MAX_TRIES=3;
	static boolean enable_compression=true;
	public static final String BASE_URL="https://api.vk.com/method/";
	
	public static JSONObject sendUnSignRequest(Params params)
	{
		String url = getUrl(params);
        helper.WriteDebug("UnSignRequest URL= "+url);
        
        String response="";
        for(int i=1;i<=MAX_TRIES;++i){
            if(i!=1)
                helper.WriteDebug("UnSignRequest attempt #" + i);
            response = sendRequestInternal(url);
            break;            
        }
        helper.WriteLog("UnSignRequest Response=" + response);
        JSONObject root = null;
        if (response != null) {
        	try {
    			root = new JSONObject(response);
    		} catch (JSONException e) {
    			helper.WriteError("не могу распарсить респонс");
    			helper.WriteError(e.getMessage());
    		}
    		checkError(root);
            return root;
		} else {
			helper.WriteWarn("респонс = нулл");
			return null;
		}
	}	
	
    private static JSONObject sendRequest(Params params)  {
        String url = getSignedUrl(params);
        helper.WriteLog("URL= "+url);
        
        String response="";
        for(int i=1;i<=MAX_TRIES;++i){
            if(i!=1)
                helper.WriteDebug("sendRequestInternal attempt #" + i);
            response = sendRequestInternal(url);
            break;            
        }
        helper.WriteLog("Response=" + response);
        JSONObject root = null;
        if (response != null) {
        	try {
    			root = new JSONObject(response);
    		} catch (JSONException e) {
    			helper.WriteError("не могу распарсить респонс");
    			helper.WriteError(e.getMessage());
    		}
    		checkError(root);
            return root;
		} else {
			helper.WriteWarn("респонс = нулл");
			return null;
		}
    }
	
    private static String getSignedUrl(Params params) {
        String args = params.getParamsString();
        
        //add access_token
        if(args.length()!=0)
            args+="&";
        args+="access_token=" + access_token;
        
        return BASE_URL+params.method_name+"?"+args;
    }
    
    private static String getUrl(Params params) {
        String args = params.getParamsString();
        
        return BASE_URL+params.method_name+"?"+args;
    }
    
//    private static void processNetworkException(int i, IOException ex) throws IOException {
//        ex.printStackTrace();
//        if(i==MAX_TRIES)
//            throw ex;
//    }
    
    private static void checkError(JSONObject root) {
        if(!root.isNull("error")){
            JSONObject error;
			try {
				error = root.getJSONObject("error");
				int code=error.getInt("error_code");
	            String message=error.getString("error_msg");
			} catch (JSONException e) {
				helper.WriteError(e.getMessage());
			}                        
        }
    }
    
    public static String sendRequestInternal(String url) {
        HttpURLConnection connection=null;
        try{
            connection = (HttpURLConnection)new URL(url).openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setUseCaches(false);
            connection.setDoOutput(false);
            connection.setDoInput(true);           
            
            if(enable_compression)
                connection.setRequestProperty("Accept-Encoding", "gzip");
            int code=connection.getResponseCode();
            helper.WriteLog("code="+code);
            
            disableConnectionReuseIfNecessary();
            //It may happen due to keep-alive problem http://stackoverflow.com/questions/1440957/httpurlconnection-getresponsecode-returns-1-on-second-invocation
            if (code==-1) {
                helper.WriteError("response code=-1");
                return null;
            }
            InputStream is = new BufferedInputStream(connection.getInputStream(), 8192);
            String enc=connection.getHeaderField("Content-Encoding");
            if(enc!=null && enc.equalsIgnoreCase("gzip"))
                is = new GZIPInputStream(is);
            String response=helper.convertStreamToString(is);
            return response;
        } catch (MalformedURLException e) {
			helper.WriteError("sendRequestInternal MalformedURLException: " + e.getMessage());
			return null;
		} catch (IOException e) {
			helper.WriteError("sendRequestInternal IOException: " + e.getMessage());
			return null;
		}
        finally{
            if(connection!=null)
                connection.disconnect();
        }
    }
    
    private static void disableConnectionReuseIfNecessary() {
        if (Integer.parseInt(Build.VERSION.SDK) < Build.VERSION_CODES.ECLAIR_MR1) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    
    //http://vkontakte.ru/developers.php?o=-1&p=messages.getDialogs
    public static ArrayList<Message> getDialogs(long time_offset, int count) throws MalformedURLException, IOException, JSONException {
        Params params = new Params("messages.getDialogs");
        if (time_offset != 0)
            params.put("time_offset", time_offset);
        if (count != 0)
            params.put("count", count);
        params.put("preview_length","10");
        params.put("fields","first_name,last_name,online,photo,last_seen");
        
        Object xx = sendRequest(params);
        
        if (xx != null) {
        	JSONObject root = (JSONObject) xx;
        	JSONArray array = root.optJSONArray("response");
            ArrayList<Message> messages = parseMessages(array, false, 0, false ,0);
            return messages;
		} else {
			return null;
		}
    }
    
    private static ArrayList<Message> parseMessages(JSONArray array, boolean from_history, long history_uid, boolean from_chat, long me) throws JSONException {
        ArrayList<Message> messages = new ArrayList<Message>();
        if (array != null) {
            int category_count = array.length();
            for(int i = 1; i < category_count; ++i) {
                JSONObject o = (JSONObject)array.get(i);
                Message m = Message.parse(o, from_history, history_uid, from_chat, me);
                messages.add(m);
            }
        }
        return messages;
    }
    
    public static String getUserName(Long uid){
    	Params params = new Params("users.get");
    	params.put("uid", uid);
    	params.put("fields","first_name, last_name");
    	JSONObject root = sendRequest(params);
    	JSONArray arr = root.optJSONArray("response");
    	try {
			JSONObject child = arr.getJSONObject(0);
			//helper.WriteInfo(child.getString("first_name"));
			
	    	String fname = child.getString("first_name");
	    	String lname = child.getString("last_name");
			return fname + " " + lname;
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			//e2.printStackTrace();
		}   	   	
    	return null;
    }
    
    public static User getUser(Long uid){
    	Params params = new Params("users.get");
    	params.put("uid", uid);
    	params.put("fields","first_name,last_name,online,photo,photo_big,last_seen");
    	
    	User user = null;
    	JSONObject root = sendRequest(params);
    	JSONArray arr = root.optJSONArray("response");
    	try {
			JSONObject child = arr.getJSONObject(0);
			helper.WriteInfo(child.getString("first_name"));
			
	    	return user.parse(child);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			//e2.printStackTrace();
		}   	   	
    	return null;
    }
    
    public static ArrayList<User> getUsers(String uids){
    	Params params = new Params("users.get");
    	params.put("uid", uids);
    	params.put("fields","first_name, last_name, online, photo, photo_big");
    	
    	JSONObject root = sendRequest(params);
    	JSONArray array = root.optJSONArray("response");
    	   	
    	ArrayList<User> users = new ArrayList<User>();
    	
    	if (array != null) {
    		//int category_count = 50;
            int category_count = array.length();
            for(int i = 0; i < category_count; ++i) {
            	 
                JSONObject o;
				try {
					o = (JSONObject)array.get(i);
				} catch (JSONException e) {
					helper.WriteError("не могу получить объект " + e.getMessage());
					continue;
				}
                User m;
				try {
					m = User.parse(o);
				} catch (JSONException e) {
					helper.WriteError("не могу распарсить юзера " + o);
					continue;
				}
				//helper.WriteInfo("processed " + i + " of " + category_count + ": " + m.first_name + " " + m.last_name); 
                users.add(m);                
            }
            helper.WriteInfo("Всего загружено контактов " + users.size());
        }        	
    	return users;
    }
    
    public static ArrayList<User> getUsers(ArrayList<Long> uids) {
    	String u = "";    	
    	for (Long l : uids) {
			u += l +",";
		}
    	u = u.substring(0, u.length() -1);
    	
    	Params params = new Params("users.get");
    	params.put("uids", u);
    	params.put("fields","first_name,last_name,online,photo,photo_big,last_seen");
    	
    	JSONObject root = sendRequest(params);
    	JSONArray array = root.optJSONArray("response");
    	   	
    	ArrayList<User> users = new ArrayList<User>();
    	
    	if (array != null) {
    		//int category_count = 50;
            int category_count = array.length();
            for(int i = 0; i < category_count; ++i) {            	 
                JSONObject o;
				try {
					o = (JSONObject)array.get(i);
				} catch (JSONException e) {
					helper.WriteError("не могу получить объект " + e.getMessage());
					continue;
				}
                User m;
				try {
					m = User.parse(o);
				} catch (JSONException e) {
					helper.WriteError("не могу распарсить юзера " + o);
					continue;
				}
				//helper.WriteInfo("processed " + i + " of " + category_count + ": " + m.first_name + " " + m.last_name); 
                users.add(m);                
            }
            helper.WriteInfo("Всего загружено контактов " + users.size());
        }        	
    	return users;
    }
    
    public static String unescape(String text){
        return Html.fromHtml(text).toString();
    }

    //http://vk.com/pages?oid=-1&p=auth.checkPhone
    public static boolean checkPhone(String phone) {
    	/*Params params = new Params("auth.checkPhone");
    	params.put("phone", phone);
    	params.put("client_id", Const.API_ID);
    	params.put("client_secret", Const.API_Key);
    	*/
    	String url = "https://api.vk.com/method/auth.checkPhone" 
				+ "?client_secret=" + Const.API_Key
				+ "&phone=" + phone
				+ "&client_id=" + Const.API_ID;
		
		String str = helper.DoRequest(url);	
    	
    	helper.WriteInfo("пришло: " + str);
		
    	try {
			JSONObject root = new JSONObject(str);
			int resp = root.getInt("response");
			if (resp == 1) {
				return true;
			}		
		} catch (JSONException e) {
			helper.WriteError("вернулся не json");
			return false;
		}
    	return false;
    }
    
    //http://vk.com/pages?oid=-1&p=auth.checkPhone
    public static ArrayList<User> getFriends() {
    	Params params = new Params("friends.get");
    	
    	JSONObject root = sendRequest(params);
    	JSONArray arr = root.optJSONArray("response");
    	
    	return new ArrayList<User>();
    }
    
    //http://vk.com/pages?oid=-1&p=auth.checkPhone
    public static ArrayList<User> getFriendsExec(String code) {
    	Params params = new Params("execute");
    	params.put("code", code);
    	
    	String fields="first_name,last_name,photo,online";
    	
        params.put("fields",fields);
        params.put("order","hints");
        

    	JSONObject root = sendRequest(params);
    	JSONArray array = root.optJSONArray("response");
    	   	
    	ArrayList<User> users = new ArrayList<User>();
    	
    	if (array != null) {
    		//int category_count = 50;
            int category_count = array.length();
            for(int i = 0; i < category_count; ++i) {
            	 
                JSONObject o;
				try {
					o = (JSONObject)array.get(i);
				} catch (JSONException e) {
					helper.WriteError("не могу получить объект " + e.getMessage());
					continue;
				}
                User m;
				try {
					m = User.parse(o);
				} catch (JSONException e) {
					helper.WriteError("не могу распарсить юзера " + o);
					continue;
				}
				//helper.WriteInfo("processed " + i + " of " + category_count + ": " + m.first_name + " " + m.last_name); 
                users.add(m);                
            }
            helper.WriteInfo("Всего загружено контактов " + users.size());
        }
        	
    	return users;
    }
    
    
    //http://vk.com/pages?oid=-1&p=messages.getHistory
    public static ArrayList<Message> getHistory(Long uid) {
    	Params params = new Params("messages.getHistory");
    	params.put("uid", uid);

    	JSONObject root = sendRequest(params);
    	JSONArray array = root.optJSONArray("response");
	   	
    	ArrayList<Message> messages = new ArrayList<Message>();
    	
    	if (array != null) {
            int category_count = array.length();
            for(int i = 1; i < category_count; ++i) {            	 
                JSONObject o;
				try {
					o = (JSONObject)array.get(i);
				} catch (JSONException e) {
					helper.WriteError("не могу получить объект " + e.getMessage());
					continue;
				}
                Message m;
				try {
					m = Message.parse(o,true,0,false,1);
				} catch (JSONException e) {
					helper.WriteError("не могу распарсить юзера " + o);
					continue;
				} 
				
				/*if (m.is_out == true) {
					helper.WriteInfo("сообщение '" + m.body + "' исходящее");
				} else {
					helper.WriteInfo("сообщение '" + m.body + "' входящее");
				}*/
				
				messages.add(m);                
            }
            helper.WriteInfo("Всего загружено сообщений " + messages.size());
        }
    	return messages;
    }
    
    
    //http://vk.com/pages?oid=-1&p=friends.getRequests
    //возвращает список заявок в друзья у текущего пользователя. 
    public static ArrayList<User> getRequests() {
    	Params params = new Params("friends.getRequests");
    	
    	JSONObject root = sendRequest(params);
    	
    	return null;
    }
    
    
    //http://vk.com/pages?oid=-1&p=auth.signup
    //регистрирует нового пользователя по номеру телефона. 
    public static String authSignup(String phone, String firstName, String lastName) {
    	Params params = new Params("auth.signup");
    	params.put("phone", phone);
    	params.put("first_name", firstName);
    	params.put("last_name", lastName);
    	params.put("client_id", Const.API_ID);
    	params.put("client_secret", Const.API_Key);
    	params.put("test_mode", "1");
    	
    	JSONObject root = sendUnSignRequest(params);
    	
    	return null;
    }
    
    //http://vk.com/pages?oid=-1&p=messages.send
    //посылает сообщение.
    public static String messagesSend(long uid, String message) {
    	Params params = new Params("messages.send");
    	params.put("uid", uid);
    	params.put("message", message);
    	
    	JSONObject root = sendRequest(params);
    	
    	return null;
    }
    
    //http://vk.com/developers.php?oid=-1&p=photos.getMessagesUploadServer
    public static String photosGetMessagesUploadServer() {
    	Params params = new Params("photos.getMessagesUploadServer");    	
    	
    	JSONObject root = sendRequest(params);
    	JSONArray array = root.optJSONArray("response");
    	
    	try {
			JSONObject obj = root.getJSONObject("response");
			String upload_url = obj.getString("upload_url");
			return upload_url;
		} catch (JSONException e) {
			helper.WriteError("не могу получить upload_url " + e.getMessage());
		}
    	
    	return null;
    }
    
    //http://vk.com/pages?oid=-1&p=messages.setActivity
    //изменяет статус набора текста пользователем в диалоге.
    public static boolean messagesSetActivity(Long uid, Long chat_id){    	
    	Params params = new Params("messages.setActivity");
    	params.put("type", "typing");
    	
    	if (uid != 0) 
    		params.put("uid", uid);
    	if (chat_id != 0) 
    		params.put("chat_id", chat_id);
    	
    	JSONObject root = sendRequest(params);
    	
    	return true;
    }
    
    public static boolean messagesGetLastActivity(Long uid){    	
    	Params params = new Params("messages.getLastActivity");    	
    	params.put("uid", uid);    	
    	
    	JSONObject root = sendRequest(params);
    	JSONArray array = root.optJSONArray("response");
    	    	
    	return true;
    }

    
    public static ArrayList<User> friendsGetRequestsExec(){
    	String code = "";
    	code += "var a=API.friends.getRequests();";
        code += "var c=API.getProfiles({\"uids\":a, \"fields\":\"photo,online\"});";
        code += "return c;";
        
        Params params = new Params("execute");
    	params.put("code", code);    	
    	
    	ArrayList<User> userRequests = new ArrayList<User>();
    	
    	JSONObject root = sendRequest(params);
    	JSONArray array = root.optJSONArray("response");    	    	
    	
    	if (array != null) {
            int category_count = array.length();
            for(int i = 0; i < category_count; ++i) {  
            	JSONObject o;
				try {
					o = (JSONObject)array.get(i);
				} catch (JSONException e) {
					helper.WriteError("не могу получить объект " + e.getMessage());
					continue;
				}
                User u;
				try {
					u = User.parse(o);
				} catch (JSONException e) {
					helper.WriteError("не могу распарсить юзера " + o);
					continue;
				} 
				userRequests.add(u);            
            }
            helper.WriteInfo("Всего заявок в друзья " + userRequests.size());
    	}
    	
    	return userRequests;
    }
    
    //http://vk.com/pages?oid=-1&p=friends.getSuggestions
    //Возвращает список профилей пользователей, которые могут быть друзьями текущего пользователя.
    public static ArrayList<User> friendsGetSuggestionsExec(){
    	String code = "";
    	code += "var a=API.friends.getSuggestions();";
        code += "var c=API.getProfiles({\"uids\":a, \"fields\":\"photo,online\"});";
        code += "return c;";
        
        Params params = new Params("execute");
    	params.put("code", code);    	
    	
    	ArrayList<User> userRequests = new ArrayList<User>();
    	
    	JSONObject root = sendRequest(params);
    	JSONArray array = root.optJSONArray("response");    	    	
    	
    	if (array != null) {
            int category_count = array.length();
            for(int i = 0; i < category_count; ++i) {  
            	JSONObject o;
				try {
					o = (JSONObject)array.get(i);
				} catch (JSONException e) {
					helper.WriteError("не могу получить объект " + e.getMessage());
					continue;
				}
                User u;
				try {
					u = User.parse(o);
				} catch (JSONException e) {
					helper.WriteError("не могу распарсить юзера " + o);
					continue;
				} 
				userRequests.add(u);            
            }
            helper.WriteInfo("Всего заявок в друзья " + userRequests.size());
    	}
    	
    	return userRequests;
    }
    
    //http://vk.com/pages?oid=-1&p=messages.markAsRead
    public static int messagesMarkAsRead(ArrayList<Long> unReadedMessages){
    	String mids = "";
    	for (Long mid : unReadedMessages) {
			mids += mid + ",";
		}
    	mids = mids.substring(0,mids.length() -1);
    	
    	Params params = new Params("messages.markAsRead");
    	params.put("mids", mids);

    	
    	JSONObject root = sendRequest(params);
    	
    	return 1;
    }

	public static LongPollServer messagesGetLongPollServer() {
		Params params = new Params("messages.getLongPollServer");    	
		
    	JSONObject root = sendRequest(params);
    	JSONArray array = root.optJSONArray("response");
    	
    	
    	try {
    		LongPollServer lps = new LongPollServer(); 
			JSONObject obj = root.getJSONObject("response");
			lps.key = obj.getString("key");
			lps.server = obj.getString("server");
			lps.ts = obj.getString("ts"); 
			return lps;
		} catch (JSONException e) {
			helper.WriteError("не могу получить upload_url " + e.getMessage());
		}
    	return null;
    	
	}
}

