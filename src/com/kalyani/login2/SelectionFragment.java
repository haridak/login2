package com.kalyani.login2;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SelectionFragment extends Fragment {
	//added for auth
	ArrayList<String> stringArrayList = new ArrayList<String>();
	private static final String TAG = "SelectionFragment";
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
	    Log.i("TAG", "In onCreateView");
	    View view = inflater.inflate(R.layout.selection, 
	            container, false);
	    Button searchButton =(Button) view.findViewById(R.id.button2);
	    FriendsLikesLocation(Session.getActiveSession());
	 
	    Button askButton=(Button) view.findViewById(R.id.button1);
	    searchButton.setOnClickListener(new OnClickListener()
	    {
            @Override
            public void onClick(View v)
            {
            	Intent intent = new Intent(v.getContext(),SearchActivity.class);
            	//String[] stockArr = new String[stringArrayList.size()];
				//stockArr = stringArrayList.toArray(stockArr);
				Log.i("TAG", "In onClick of Search My Network button");
			//	intent.putExtra("string-array",stockArr);
				startActivityForResult(intent,0);
            	Log.i("TAG", "In startActivityForResult of Search My Network button");
            
            } 
  }); 
	    askButton.setOnClickListener(new OnClickListener()
	    {
            @Override
            public void onClick(View v)
            {
            	Log.i("TAG", "In onClick of ask button");
            	Intent intent = new Intent(v.getContext(),AskActivity.class);
            	startActivityForResult(intent,0);
            	Log.i("TAG", "In startActivityForResult of ask button");
            } 
  }); 
	    return view;
	}
	
	public void FriendsLikesLocation(final Session session) {

		Log.i("TAG", "userlikes");
		String fqlQuery = "SELECT location from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()));";
		Log.i("fql query", fqlQuery);
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Request request = new Request(session,
				"/fql",                         
				params,                         
				HttpMethod.GET,                 
				new Request.Callback(){         
			public void onCompleted(Response response) {
				//Log.i("locations", response.toString());
				try
				{
					GraphObject go  = response.getGraphObject();
					JSONObject  jso = go.getInnerJSONObject();
					JSONArray   arr = jso.getJSONArray( "data" );
					for(int i = 0;i < arr.length(); i++){
						if(arr.getJSONObject(i).getJSONObject("location").isNull("city"))
						{
							//Log.i("tag","value is null");
						}
						else
						{
							String temp = arr.getJSONObject(i).getJSONObject("location").getString("city").toString();
							Log.i("location", temp);
							stringArrayList.add(temp);
						}

					}     
				}
				catch( Throwable t )
				{
					t.printStackTrace();
				}
			
			}  
		}); 
		Request.executeBatchAsync(request); 
		
	}

}
