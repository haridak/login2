package com.kalyani.login2;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class NotificationsActivity extends Activity {
	String[] prev_post_ids;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		prev_post_ids = getIntent().getStringArrayExtra("mStringArray");
		super.onCreate(savedInstanceState);
		ViewResponses(prev_post_ids);
	}

	public void ViewResponses(String[] stringArray )
	{
		String fqlQuery = 
				   "SELECT * FROM COMMENT WHERE POST_ID IN" +prev_post_ids ;
						        Bundle params = new Bundle();
						        params.putString("q", fqlQuery);
						        Session session = Session.getActiveSession();
						        Request request = new Request(session,
						            "/fql",                         
						            params,                         
						            HttpMethod.GET,                 
						            new Request.Callback(){         
						                public void onCompleted(Response response) {
						                    Log.i("pages_info", "Result: " + response.toString());
						                   // parseFQLResponse(response);
						                }                  
						        }); 
						        Request.executeBatchAsync(request);  
	}
	
}
