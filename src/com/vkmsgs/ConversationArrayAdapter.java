package com.vkmsgs;

import java.util.HashSet;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vkmsgs.api.Message;

public class ConversationArrayAdapter extends ArrayAdapter<Message> {
	private final List<Message> list;
    public static List<Message> original;

    ConversationActivity activity;
    private final Activity context;
    HashSet<Long> checkedItems = new HashSet();

    public ConversationArrayAdapter(Activity context, List<Message> list) {
        super(context, R.layout.item_message_list, list);
        this.context = context;
        this.list = list;
        //this.original = new ArrayList<Message>(list);
    }
    
    public void clearCheckedItems()
    {
      this.checkedItems.clear();
      for (int i = 0; ; i++)
      {
        if (i >= this.list.size())
        {
          notifyDataSetChanged();
          this.activity.onItemsCheckChanged();
          return;
        }
        //((Message)this.list.get(i)).setChecked(false);
      }
    }
    
    public HashSet<Long> getCheckedItems()
    {
      return this.checkedItems;
    }
    
	static class ViewHolder {
    	//protected TextView name;
    	protected TextView date;
        protected TextView textIn;
        protected TextView textOut;
        /*protected ImageView image;
        protected ImageView online;*/
    }
	
	 @Override
	    public View getView(int position, final View convertView, ViewGroup parent) {
	       View view = null;

	       LayoutInflater localLayoutInflater;
	       MessageWrapper localMessageWrapper;
	       if (convertView == null) {
	    	    localLayoutInflater = ((Activity)getContext()).getLayoutInflater();
	            //LayoutInflater inflator = context.getLayoutInflater();
	            view = localLayoutInflater.inflate(R.layout.item_message_list, parent, false);
	            /*view.setOnClickListener(new View.OnClickListener()
	            {
	              public void onClick(View paramView)
	              {
	                ((Message)convertView.getTag()).toggle();
	                if (((Message)convertView.getTag()).isChecked())
	                	ConversationArrayAdapter.this.checkedItems.add(Long.valueOf(((Message)convertView.getTag()).mid));
	                while (true)
	                {
	                  ConversationArrayAdapter.this.activity.onItemsCheckChanged();
	                  ConversationArrayAdapter.this.notifyDataSetChanged();
	                  return;
	                  ConversationArrayAdapter.this.checkedItems.remove(Long.valueOf(((Message)convertView.getTag()).mid));
	                }
	              }
	            });	   */         
	            //view.setMinimumHeight(100);
	            //view.setBackgroundResource(R.drawable.dialogs);	          

	            localMessageWrapper = new MessageWrapper(view, getContext());
	            view.setTag(localMessageWrapper);

	        } else {
	            view = convertView;
	        }	       
	       
	        //ViewHolder holder = (ViewHolder) view.getTag();	        
	        	         
	        localMessageWrapper = (MessageWrapper) view.getTag();
	        localMessageWrapper.populateFrom((Message)this.list.get(position));
	        

	        	        
	        return view;
	    }
    
    
}
