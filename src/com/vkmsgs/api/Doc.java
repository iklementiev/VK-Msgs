package com.vkmsgs.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Doc {
    public String title;
    public String url;
    public long size;
    
    public static Doc parse(JSONObject o) throws JSONException {
        Doc d = new Doc();
        d.title = o.getString("title");
        d.url = o.getString("url");
        d.size = o.getLong("size");
        return d;
    }

}
