package com.vkmsgs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.vkmsgs.api.User;

public class ContactsArrayAdapter extends ArrayAdapter<User> implements Filterable, SectionIndexer {
		private Activity activity;
	    private final List<User> list;
        public static List<User> original;
	    private Filter filter;
	    
	    //TODO:
	    //private long nameFormat = SettingsManager.getLongSetting(this.context, "nameFormat");
	    private long nameFormat = 1;
	    
	    HashMap<String, Integer> alphaIndexer;
	    String[] sections;
	    
	    private static LayoutInflater inflater=null;
	    public ImageLoader imageLoader; 
	    
	    public ContactsArrayAdapter(Activity context, List<User> list) {
	        super(context, R.layout.item_list_contacts, list);
	        this.activity = context;
	        this.list = list;
	        this.original = new ArrayList<User>(list);
	        
	        alphaIndexer = new HashMap<String, Integer>();
	        int size = list.size();
	        
	        for (int x = 0; x < size; x++) {
	        	User s = list.get(x);	            
	            String ch =  s.first_name.substring(0, 1);	            
	            ch = ch.toUpperCase();
	            alphaIndexer.put(ch, x);
	        }
	        Set<String> sectionLetters = alphaIndexer.keySet();
            ArrayList<String> sectionList = new ArrayList<String>(sectionLetters); 
            Collections.sort(sectionList);
            sections = new String[sectionList.size()];
            sectionList.toArray(sections);
            
            
            inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            imageLoader=new ImageLoader(activity.getApplicationContext());
	    }

	    static class ViewHolder {
	    	protected TextView name;
	        protected ImageView image;
	        protected ImageView online;
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	       View view = null;
	        
	       if (convertView == null) {
	            LayoutInflater inflator = activity.getLayoutInflater();
	            view = inflator.inflate(R.layout.item_list_contacts, null, false);
	            view.setMinimumHeight(70);
	            view.setBackgroundResource(R.drawable.dialogs);	          

	            final ViewHolder viewHolder = new ViewHolder();	            
	            viewHolder.name = (TextView) view.findViewById(R.id.name);
	            viewHolder.image = (ImageView) view.findViewById(R.id.image);
	            viewHolder.online = (ImageView) view.findViewById(R.id.online);
	            view.setTag(viewHolder);
	        } else {
	            view = convertView;
	        }
	        
	       ViewHolder holder = (ViewHolder) view.getTag();
	        
	        final User usr = list.get(position);
	        
	        nameFormat = 1;
	         
	        if (nameFormat == 1) {
	        	holder.name.setText(usr.first_name + " " + usr.last_name);
			} else {
				holder.name.setText(usr.last_name + " " + usr.first_name);
			}
		    
	        
		    if (usr.online)
			    holder.online.setVisibility(ImageView.VISIBLE);
		    
		    imageLoader.DisplayImage(usr.photo, holder.image);
		    		    
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
            	
            	if (constraint == "@all"){
            		result.values = original;
	                result.count = original.size();
            	} else if (constraint == "@online") {
            		List<User> founded = new ArrayList<User>();
            		
            		for (User u : original) {                    	
	                    if (u.online)
	                        founded.add(u);                        
	                }
	                result.values = founded;
	                result.count = founded.size();
            		
				} else if (constraint != null && constraint.toString().length() > 0) {
	                List<User> founded = new ArrayList<User>();
	                for (User u : original) {                    	
	                    if (u.first_name.toLowerCase().contains(constraint) || u.last_name.toLowerCase().contains(constraint))
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
                for (User u : (List<User>) filterResults.values) {
                    add(u);
                }
                notifyDataSetChanged(); 
            }
        }

		@Override
		public int getPositionForSection(int section) {
			return alphaIndexer.get(sections[section]);
		}


		@Override
		public int getSectionForPosition(int position) {
			return 1;
		}


		@Override
		public Object[] getSections() {
			return sections;
		}
	}

