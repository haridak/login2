package com.kalyani.login2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NotificationsActivity extends Activity {
	String[] prev_post_ids;
	public static final List<String> ALL_PERMISSIONS = Arrays.asList(       
			"read_friendlists",
			"offline_access",
			"email",
			"read_stream",
			"user_location",
			"friends_likes",
			"friends_location"); 

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(com.kalyani.login2.R.layout.notifications);
	          Intent myintent = getIntent();
        String[] Array = myintent.getStringArrayExtra("string-array");
        
        List<String> stringList = new ArrayList<String>(Arrays.asList(Array)); 
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        android.R.layout.activity_list_item, android.R.id.text1,stringList);
	    
        ListView answerslist = (ListView)findViewById(com.kalyani.login2.R.id.answers_listview);
        answerslist.setAdapter(adapter);
	}
	public void onBackPressed()  
	{  
	    //do whatever you want the 'Back' button to do  
	    //as an example the 'Back' button is set to start a new Activity named 'NewActivity'  
	    this.startActivity(new Intent(NotificationsActivity.this,AskActivity.class));  

	    return;  
	}
}



//select author_uid,message  from checkin where author_uid IN (select uid2 from friend where uid1 = me())
