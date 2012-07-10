package com.vkmsgs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vkmsgs.api.Attachment;
import com.vkmsgs.api.Message;
import com.vkmsgs.api.User;
import com.vkmsgs.api.VKApi;

@SuppressLint("ParserError")
public class ConversationActivity extends Activity implements OnClickListener {
	Button btnBack;
	TextView tvName;
	Button btnSend;
	
	ImageView attachImage;
	
	Button btnDelete;
	Button btnForward;
	View attachesList;
	LinearLayout attachMenu;
	EditText etText;
	ListView lvMessages;
	ImageView ivOnline;
	ImageView ivPhoto;
	View attachPopupContainer;
	ArrayList<Attachment> sendAttaches = new ArrayList<Attachment>();
	
	TextView attachTakePhoto;
	TextView attachChoosePhoto;
	TextView attachLocation;
	public static TextView userStatusText;
	View messageOptions;
	
	private int messageLatitude;
	private int messageLongitude;
    public ImageLoader imageLoader; 
	String photoDir;
	private long lastTypeNotification = 0L;

	private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();

	public static long uid;
	public static String title;
	public static ConversationArrayAdapter adapter = null;
	public static ArrayList<Message> messages = new ArrayList<Message>();
	ArrayList<Long> unReadedMessages = new ArrayList<Long>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.conversation);
		
		imageLoader=new ImageLoader(getApplicationContext());
		
		tvName = (TextView)findViewById(R.id.tvName);
		ivOnline = (ImageView)findViewById(R.id.ivOnline);
		ivPhoto = (ImageView)findViewById(R.id.ivPhoto);
		attachMenu = ((LinearLayout)findViewById(R.id.attachesMenu));
		userStatusText = (TextView)findViewById(R.id.userStatusText);
		//attachPopupContainer = (View)findViewById(R.id.attachesMenu);
		attachesList = findViewById(R.id.attachesList);
		
		etText = (EditText)findViewById(R.id.etText);		
		etText.addTextChangedListener(new TextWatcher()
	    {
	      public void afterTextChanged(Editable paramEditable)
	      {
	        StartTyping();
	      }

	      public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
	      {
	      }

	      public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
	      {
	      }
	    });
		
		btnBack = (Button)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		
		attachImage = (ImageView)findViewById(R.id.attachImage);
		attachImage.setOnClickListener(this);
		
		btnSend = (Button)findViewById(R.id.btnSend);
		btnSend.setOnClickListener(this);
		
		attachTakePhoto = (TextView)findViewById(R.id.attachTakePhoto);
		attachTakePhoto.setOnClickListener(this);
		
		attachChoosePhoto = (TextView)findViewById(R.id.attachChoosePhoto);
		attachChoosePhoto.setOnClickListener(this);
		
		attachLocation = (TextView)findViewById(R.id.attachLocation);
		attachLocation.setOnClickListener(this);
		
		lvMessages = (ListView)findViewById(R.id.lvMessages);
		
		Bundle extras = getIntent().getExtras(); 
		if(extras !=null) {		
			uid = extras.getLong("uid");
			title =  extras.getString("title");
			tvName.setText(title);	
			
			/*
			if (condition) {
				//беседа больше 2х человек
			} else {
				//беседа 2 человека
				*/
				User user = VKApi.getUser(uid);
				tvName.setText(user.first_name + " " + user.last_name);	
				if (user.online)
					ivOnline.setVisibility(View.VISIBLE);
				
				imageLoader.DisplayImage(user.photo, ivPhoto);
				userStatusText.setText("Был в сети " + helper.getDate(user.last_seen));
			//}			
			
			messages = VKApi.getHistory(uid);			
			Collections.reverse(messages);
			
			for (Message msg : messages) {
				if (msg.read_state == 0) {
					unReadedMessages.add(msg.mid);										
				}				
			}
			helper.WriteDebug("Непрочитанных сообщений: " + unReadedMessages.size());
			
			Runnable markAsReaded = new Runnable() {
			    public void run() {
			    	int response = VKApi.messagesMarkAsRead(unReadedMessages);
			    	helper.WriteDebug("пометил сообщения как прочитанные: " + unReadedMessages.size());
			    } 
			};
   		   	worker.schedule(markAsReaded, 5, TimeUnit.SECONDS);			
			
			if (messages == null ){
				Toast.makeText(this, "Нет сообщений", Toast.LENGTH_SHORT).show();
			} else {
				adapter = new ConversationArrayAdapter(this, messages);
				lvMessages.setAdapter(adapter);
			}
		} else {
			Toast.makeText(this, "Параметры не переданы", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
    public void onDestroy() {
        super.onDestroy();
        
        uid = 0;
    }
	
	public void onClick(View v) {        
        switch(v.getId()) {
        case R.id.btnBack:
        	finish();
        	break;
        case R.id.btnSend:
        	String text = etText.getText().toString();
        	
        	helper.WriteDebug("text for send: '" + text + "'");
        	
        	if (text != "") {
            	etText.setText("");
            	
            	Message m = new Message();
            	m.body = text;
            	m.date = System.currentTimeMillis();
            	m.is_out = true;
            	messages.add(m);
            	adapter.notifyDataSetChanged();
            	
            	ImageView pendingSend = (ImageView)findViewById(R.id.pendingSend);
            	pendingSend.setVisibility(View.VISIBLE);
            	
            	VKApi.messagesSend(uid, text);
            	
            	pendingSend.setVisibility(View.GONE);	
			} else {
				Toast.makeText(this, "Введите текст", Toast.LENGTH_SHORT).show();
			}
        	
        	if (sendAttaches.size() > 0) {
        		helper.WriteInfo("try to send photo to vk");
        		String upload_url = VKApi.photosGetMessagesUploadServer();
        		if (upload_url == null) {
        			helper.WriteInfo("upload_url is null");
        		}
        		else { 
        			helper.WriteInfo("upload_url=" + upload_url);
        			
        			HttpClient httpclient = new DefaultHttpClient();
        		    HttpPost httppost = new HttpPost(upload_url);

        		    
        		    //Network.postPhoto(upload_url, photoDir);
        		    
        		   /* try {
        		        // Add your data
        		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        		        nameValuePairs.add(new BasicNameValuePair("photo", new File(photoDir)));        		        
        		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        		        // Execute HTTP Post Request
        		        HttpResponse response = httpclient.execute(httppost);
        		        
        		    } catch (ClientProtocolException e) {
        		        // TODO Auto-generated catch block
        		    } catch (IOException e) {
        		        // TODO Auto-generated catch block
        		    }
*/
        		}
        		
        		
        		
        		//VKApi.messagesSend(uid,sendAttaches);
			}
			
        	break;
        case R.id.attachImage:
        	int i = 4;
            int j = 0;
            
            helper.WriteDebug("Открыть меню атачей");
            
            if (ConversationActivity.this.sendAttaches == null)
            {
            	helper.WriteDebug("еще нет атачей");
            	
            	LinearLayout localLinearLayout = ConversationActivity.this.attachMenu;
			  	if (ConversationActivity.this.attachMenu.getVisibility() == 0)
			  	{
			  		localLinearLayout.setVisibility(i);
			    	ImageView localImageView = ConversationActivity.this.attachImage;
			    	localImageView.setImageResource(R.drawable.attach_pressed);
			  	}
            }
            
            /*
            i = 0;
            View localView = ConversationActivity.this.attachesList;
            if (ConversationActivity.this.attachesList.getVisibility() == 0)
            	j = 8;
            localView.setVisibility(j);*/
            attachMenu.setVisibility(View.VISIBLE);
            attachImage.setImageResource(R.drawable.attach_pressed);
			
            
        	break;
        case R.id.attachTakePhoto:
        	attachMenu.setVisibility(4);
        	attachImage.setImageResource(R.drawable.attach);
        	
            //Intent localIntent = new Intent("android.media.action.IMAGE_CAPTURE");
            
            //photoDir = Const.getPhotosDir() + "/" + System.currentTimeMillis() + ".jpg"; //(VKApplication.getInstance().getCameraDir() + "/" + System.currentTimeMillis() + ".jpg");
            photoDir = Environment.getExternalStorageDirectory() + "/images/make_machine_example.jpg";
        	
            startCameraActivity();
            /*File localFile = new File(photoDir);
            if (!localFile.getParentFile().exists())
              localFile.getParentFile().mkdirs();
            localIntent.putExtra("output", Uri.fromFile(new File(photoDir)));
            ConversationActivity.this.startActivityForResult(localIntent, 0);*/
            
        	break;
        case R.id.attachChoosePhoto:
        	attachMenu.setVisibility(4);
        	attachImage.setImageResource(R.drawable.attach);
        	
        	startGalleryActivity();
        	
            //ConversationActivity.this.startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
        	break;
        case R.id.attachLocation:
        	Toast.makeText(this, "Еще не робит", Toast.LENGTH_SHORT).show();
        	break;
        }
	}
	
	private void StartTyping()
	{
	    if (System.currentTimeMillis() - this.lastTypeNotification > 5000L)
	    {
	    	this.lastTypeNotification = System.currentTimeMillis();
	    		    	
	    	VKApi.messagesSetActivity(uid, 0L);
	        /*if (ConversationActivity.this.dialogDescriptor.isPrivate())
	        {
	          VKApplication localVKApplication2 = (VKApplication)ConversationActivity.this.application;
	          String[] arrayOfString2 = new String[4];
	          arrayOfString2[0] = "uid";
	          arrayOfString2[1] = (ConversationActivity.access$400(ConversationActivity.this).getId() + "");
	          arrayOfString2[2] = "type";
	          arrayOfString2[3] = "typing";
	          localVKApplication2.doRequest("messages.setActivity", arrayOfString2);
	        }
	        else
	        {
	          VKApplication localVKApplication1 = (VKApplication)ConversationActivity.this.application;
	          String[] arrayOfString1 = new String[4];
	          arrayOfString1[0] = "chat_id";
	          arrayOfString1[1] = (ConversationActivity.access$400(ConversationActivity.this).getId() + "");
	          arrayOfString1[2] = "type";
	          arrayOfString1[3] = "typing";
	          localVKApplication1.doRequest("messages.setActivity", arrayOfString1);
	        }*/	        
	    }
	}
	
	public void onItemsCheckChanged()
	{
	    if (this.adapter.getCheckedItems().size() > 0)
	    {
	      findViewById(R.id.infoFrame).setVisibility(4);
	      findViewById(R.id.messageOptions).setVisibility(0);
	      this.btnForward.setText(getString(R.string.forward) + " (" + this.adapter.getCheckedItems().size() + ")");
	      this.btnDelete.setText(getString(R.string.delete) + " (" + this.adapter.getCheckedItems().size() + ")");
	    }
	    while (true)
	    {
	      return;
	      //findViewById(R.id.infoFrame).setVisibility(0);
	      //findViewById(R.id.messageOptions).setVisibility(4);
	    }
	}
	
	private void addAttach(Attachment att)
	{
	    synchronized (this.sendAttaches)
	    {
	    	this.sendAttaches.add(att);
	    	//updateAttaches();
	    	if (attachesCount() > 0)
	    		this.attachesList.setVisibility(0);
	    	return;
	    }
	}
	
	private int attachesCount()
	{
	    int i = this.sendAttaches.size();
	    /*if (((VKApplication)this.application).getForwardingMessages().length != 0)
	      i++;*/
	    if ((this.messageLatitude != 0) && (this.messageLongitude != 0))
	      i++;
	    return i;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data); 
		
		helper.WriteDebug("requestCode: " + requestCode);
		helper.WriteDebug("resultCode: " + resultCode);
	    /*switch( resultCode )
	    {
	    	case 0:
	    		helper.WriteDebug("User cancelled");
	    		break;
	    			
	    	case -1:
	    		onPhotoTaken();
	    		break;
	    }*/
	    
	    /*switch(requestCode) { 
	    case REQ_CODE_PICK_IMAGE:*/
	        if(resultCode == RESULT_OK){  
	            Uri selectedImage = data.getData();
	            String[] filePathColumn = {MediaStore.Images.Media.DATA};

	            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	            cursor.moveToFirst();

	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String filePath = cursor.getString(columnIndex);
	            cursor.close();

	            helper.WriteDebug("filePath: " + filePath);
	            Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
	            
	            
	            int h = 48; // height in pixels
	            int w = 48; // width in pixels    
	            Bitmap scaled = Bitmap.createScaledBitmap(yourSelectedImage, h, w, true);

	            
	            attachImage.setImageBitmap(scaled);
	            
	            PhotoAttach pa = new PhotoAttach();
	            pa.photo_src = filePath;
	            
	            addAttach(pa);
	        }
	}
	
	protected void startCameraActivity()
	{
	    File file = new File( photoDir );
	    Uri outputFileUri = Uri.fromFile( file );
	    	
	    Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
	    intent.putExtra( MediaStore.EXTRA_OUTPUT, outputFileUri );
	    	
	    startActivityForResult( intent, 0 );
	}
	
	protected void onPhotoTaken()
	{
	    //_taken = true;
	    	
	    BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inSampleSize = 4;
	    	
	    Bitmap bitmap = BitmapFactory.decodeFile( photoDir, options );
	    attachImage.setImageBitmap(bitmap);
	    //_image.setImageBitmap(bitmap);
	    	
	    //_field.setVisibility( View.GONE );
	}
	protected void startGalleryActivity()
	{
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, 1); 
	}
	
}

