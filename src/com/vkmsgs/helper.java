package com.vkmsgs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class helper {

	public static void WriteLog(String text)
	{
		if (Const.IsLogEnabled) {
			Log.d(Const.LogTag, text);
		}		
	}
	
	public static void WriteDebug(String text)
	{
		if (Const.IsLogEnabled) {
			Log.d(Const.LogTag, text);
		}		
	}
	
	public static void WriteInfo(String text)
	{
		if (Const.IsLogEnabled) {
			Log.i(Const.LogTag, text);
		}		
	}
	
	public static void WriteError(String text)
	{
		if (Const.IsLogEnabled) {
			Log.e(Const.LogTag, text);
		}		
	}

	public static void WriteWarn(String text)
	{
		if (Const.IsLogEnabled) {
			Log.w(Const.LogTag, text);
		}		
	}
	
	
	public static String DoRequest(String url)
	{	
		try {
			WriteDebug("Start request " + url);
			
			HttpGet httpGet = new HttpGet(url);
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used. 
			int timeoutConnection = 5000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			
	        HttpResponse response = httpclient.execute(httpGet);
	        StatusLine statusLine = response.getStatusLine();
	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            response.getEntity().writeTo(out);
	            out.close();
	            String responseString = out.toString();
	            
	            WriteDebug("End request " + url);
	            WriteInfo("responseString: " + responseString);
	            
	            return responseString;
	        } else {
	            response.getEntity().getContent().close();
	            
	            WriteLog("Error request " + url);
	            WriteLog("IOException: " + statusLine.getReasonPhrase());
	            WriteLog("statusLine: " + statusLine.getStatusCode());
	            
	            return Integer.toString(statusLine.getStatusCode());
	        }
		} catch (Exception e) {
			helper.WriteError("connect error: " + e.getMessage().toString());			
			return e.getMessage().toString();
		}
	}
	
	public static String extractPattern(String string, String pattern){
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(string);
        if (!m.find())
            return null;
        return m.toMatchResult().group(1);
    }
    
    public static String convertStreamToString(InputStream is) throws IOException {
        InputStreamReader r = new InputStreamReader(is);
        StringWriter sw = new StringWriter();
        char[] buffer = new char[1024];
        try {
            for (int n; (n = r.read(buffer)) != -1;)
                sw.write(buffer, 0, n);
        }
        finally{
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sw.toString();
    }
    
    public static boolean isNullOrEmpty(final String str) {
        if (str == null)
          return true;
        if (str.trim().equals(""))
          return true;
        return false;
      }
    
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
    
    public static CharSequence getDate(Long epochString) {
    	
    	//long epoch = Long.parseLong( epochString );
        Date dialogDate = new Date( epochString * 1000 );
    	
    	Date today = new Date();	    	
    	
    	Calendar calendar = Calendar.getInstance();  
        calendar.setTime(today);  
  
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
  
        Date today2 = calendar.getTime();  
    	
    	//helper.WriteInfo("dialogDate="  + dialogDate);
    	//helper.WriteInfo("today="  + today2);
    	
    	//TODO: сделать "вчера"
    	
		if (dialogDate.compareTo(today2) > 0) {
			//helper.WriteInfo(dialogDate + ">" + today2);
			return dialogDate.getHours() +":" + dialogDate.getMinutes();
		} else {				
			//helper.WriteInfo(dialogDate + "<=" + today2);
			
			String month = "";
			if (dialogDate.getMonth()<10)
				month = "0" + dialogDate.getMonth();
			else 
				month = "" + dialogDate.getMonth();
			return dialogDate.getDate() + "." + month;
		}			
	}
}
