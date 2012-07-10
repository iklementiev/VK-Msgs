package com.vkmsgs;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vkmsgs.api.Message;

public class MessageWrapper {
	
	private View row = null;
	private Context context;
	//private final int type;
	//private final boolean chat;
	  
	private ImageView senderImage;
	private TextView timeLeft = null;
	private TextView timeRight = null;
	private TextView body = null;
	private ImageView pendingSen = null;
	private boolean isChecked;
	private Message message;
	private LinearLayout bubbleLayout;
	
	public MessageWrapper(View paramView, Context paramContext)
	{
	    this.row = paramView;
	    this.context = paramContext;
	}
	
	public void populateFrom(Message msg)
	{
		View localView1 = this.row;
		timeLeft = getTimeLeft();
		timeRight = getTimeRight();
		
		if (msg.is_out == true) {
			//helper.WriteDebug("исходящее " + msg.mid);
			LinearLayout localLinearLayout2 = getBubbleLayout();
			localLinearLayout2.setBackgroundResource(R.drawable.bg_msg_out);
			
			timeRight.setVisibility(View.GONE);
			timeLeft.setVisibility(View.VISIBLE);
			timeLeft.setText(helper.getDate(msg.date));
		} else {
			//helper.WriteDebug("входящее " + msg.mid);
			LinearLayout localLinearLayout3 = getBubbleLayout();
			localLinearLayout3.setBackgroundResource(R.drawable.bg_msg_in);			
			
			timeLeft.setVisibility(View.GONE);
			timeRight.setVisibility(View.VISIBLE);
			timeRight.setText(helper.getDate(msg.date));
		}		
		
		TextView localTextView1 = getBody();
		localTextView1.setText(msg.body);
	}
	public void setChecked(boolean paramBoolean)
	{
	    this.message.setChecked(paramBoolean);
	}
	
	private ImageView getPendingSend()
	{
	    if (this.pendingSen == null)
	      this.pendingSen = ((ImageView)this.row.findViewById(R.id.pendingSend));
	    return this.pendingSen;
	}
	
	private TextView getBody()
	  {
	    if (this.body == null)
	      this.body = ((TextView)this.row.findViewById(R.id.messageText));
	    return this.body;
	  }

	  private LinearLayout getBubbleLayout()
	  {
	    if (this.bubbleLayout == null)
	      this.bubbleLayout = ((LinearLayout)this.row.findViewById(R.id.bubbleLayout));
	    return this.bubbleLayout;
	  }
	  
	  private TextView getTimeLeft()
	  {
	    if (this.timeLeft == null)
	      this.timeLeft = ((TextView)this.row.findViewById(R.id.timeLeft));
	    return this.timeLeft;
	  }

	  private TextView getTimeRight()
	  {
	    if (this.timeRight == null)
	      this.timeRight = ((TextView)this.row.findViewById(R.id.timeRight));
	    return this.timeRight;
	  }
}
