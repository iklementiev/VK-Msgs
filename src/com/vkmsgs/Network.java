package com.vkmsgs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.vkmsgs.api.LongPollServer;
import com.vkmsgs.api.VKApi;

public class Network {
	/*public static String sendReqToLongPoll(LongPollServer lps){
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
				LongPoll.processResponse(updates);
				lps.ts = ts;
				sendReqToLongPoll(lps);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}*/
}
