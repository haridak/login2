package com.kalyani.login2;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
	
		Button Search =(Button) findViewById(R.id.buttons1);
		Search.setOnClickListener(new View.OnClickListener() {
			    @Override
			    public void onClick(View v) {
			    	
			        String fqlQuery = 
	   "SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me())";
			        Bundle params = new Bundle();
			        params.putString("q", fqlQuery);
			        Session session = Session.getActiveSession();
			        Request request = new Request(session,
			            "/fql",                         
			            params,                         
			            HttpMethod.GET,                 
			            new Request.Callback(){         
			                public void onCompleted(Response response) {
			                    Log.i("TAG", "Result: " + response.toString());
			                   // parseFQLResponse(response);
			                }                  
			        }); 
			        Request.executeBatchAsync(request);                 
			    }
			});
	}
}
