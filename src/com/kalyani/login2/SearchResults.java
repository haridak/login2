package com.kalyani.login2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class SearchResults extends Activity {

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
		setContentView(com.kalyani.login2.R.layout.searchresults);
	          Intent myintent = getIntent();
        String[] Array = myintent.getStringArrayExtra("string-array");
        
        List<String> stringList = new ArrayList<String>(Arrays.asList(Array)); 
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
        R.layout.mylist, android.R.id.text1,stringList);
	    
        ListView answerslist = (ListView)findViewById(com.kalyani.login2.R.id.search_results_listview);
        answerslist.setAdapter(adapter);
	}

	private boolean doubleBackToExitPressedOnce = false;

	@Override
	protected void onResume() {
	    super.onResume();
	    // .... other stuff in my onResume ....
	    this.doubleBackToExitPressedOnce = false;
	}

	@Override
	public void onBackPressed() {
	    if (doubleBackToExitPressedOnce) {
	        super.onBackPressed();
	        return;
	    }

	    this.doubleBackToExitPressedOnce = true;
	    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

	    new Handler().postDelayed(new Runnable() {

	        @Override
	        public void run() {
	            doubleBackToExitPressedOnce=false;                       
	        }
	    }, 2000);
	}
}
