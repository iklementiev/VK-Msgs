package com.vkmsgs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.vkmsgs.DialogsActivity.MyTask;
import com.vkmsgs.api.Message;
import com.vkmsgs.api.User;
import com.vkmsgs.api.VKApi;

public class DialogsArrayAdapter extends ArrayAdapter<Message> implements Filterable {
	    private final List<Message> list;
	    public static List<Message> original;
	    private final Activity activity;
	    private Filter filter;
	    public ImageLoader imageLoader; 

	    private static LayoutInflater inflater=null;
	    
	    public DialogsArrayAdapter(Activity context, List<Message> list) {
	        super(context, R.layout.item_list_dialogs, list);
	        this.activity = context;
	        this.list = list;
	        this.original = new ArrayList<Message>(list);
	        
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader=new ImageLoader(activity.getApplicationContext());            
	    }
	    
		static class ViewHolder {
	    	protected TextView name;
	    	protected TextView date;
	        protected TextView text;
	        protected ImageView image;
	        protected ImageView online;
	    }

	    @Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
	       View view = null;
	        
	       if (convertView == null) {
	            LayoutInflater inflator = activity.getLayoutInflater();
	            view = inflator.inflate(R.layout.item_list_dialogs, null, false);
	            view.setMinimumHeight(100);
	            view.setBackgroundResource(R.drawable.dialogs);	          

	            final ViewHolder viewHolder = new ViewHolder();	            
	            viewHolder.name = (TextView) view.findViewById(R.id.name);
	            viewHolder.text = (TextView) view.findViewById(R.id.text);
	            viewHolder.image = (ImageView) view.findViewById(R.id.image);
	            viewHolder.online = (ImageView) view.findViewById(R.id.ivOnline);
	            viewHolder.date = (TextView) view.findViewById(R.id.date);
	            view.setTag(viewHolder);
	        } else {
	            view = convertView;
	        }
	        final ViewHolder holder = (ViewHolder) view.getTag();
	        	 
	        Message msg = list.get(position);
	        holder.text.setText(Html.fromHtml(msg.body).toString());
	        holder.date.setText(helper.getDate( msg.date));
	        
	        //helper.WriteDebug("в диалогсјррейјдаптере: " + msg.mid + " read_state=" + msg.read_state);
	        
	        if (msg.read_state == 0) 
	        	holder.text.setBackgroundColor(R.drawable.attach);	        			
	        
	        if (DialogsActivity.users.containsKey(msg.uid)){
	        	User user = DialogsActivity.users.get(msg.uid);
	        
	        	holder.name.setText(user.first_name + " " + user.last_name);	

	        	if (user.online) 
	        	holder.online.setVisibility(ImageView.VISIBLE);		
	        
		        if (msg.chat_id == 1 && msg.chat_active != null) {
		        	String uids = "";
		        	for (Long chatUser : msg.chat_active) {
		        		 uids += chatUser + ",";
					}
		        	ArrayList<User> chatUsers = VKApi.getUsers(uids.substring(0, uids.length() - 1));
		        	
	        		//imageLoader.DisplayImage(us.photo, holder.image);
				} else {
					imageLoader.DisplayImage(user.photo, holder.image);
				}
	        }
	        
	        return view;
	    }

		@Override
		public Filter getFilter() {
			if (filter == null){
				filter = new ContactsFilter();
			}
			return filter;
		}
	    
	    private class ContactsFilter extends  Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();

                FilterResults result = new FilterResults();
                if (constraint != null && constraint.toString().length() > 0) {
                    List<Message> founded = new ArrayList<Message>();
                    for (Message u : original) {                    	
                        if (u.title.toLowerCase().contains(constraint) || u.body.toLowerCase().contains(constraint))
                            founded.add(u);                        
                    }
                    
                    result.values = founded;
                    result.count = founded.size();
                } else {
                    result.values = original;
                    result.count = original.size();
                }
                return result;
            } 
 
            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                clear();
                for (Message u : (List<Message>) filterResults.values) {
                    add(u);
                }
                notifyDataSetChanged();
            }
        }
	}