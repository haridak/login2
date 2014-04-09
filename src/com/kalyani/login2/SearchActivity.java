package com.kalyani.login2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.facebook.AppEventsLogger;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;


public class SearchActivity extends Activity {

	final Session.StatusCallback sessionStatusCallback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, SessionState state, Exception exception) {
			// If there is an exception...
			if(exception != null)
			{
				// Handle fail case here.
				return;
			}

			// If session is just opened...
			if(state == SessionState.OPENED)
			{
				// Handle success case here.
				return;
			}
		};
	};
	 ProgressDialog progress;
	String results = "";
	String post_id;
	String s="abc";
String fqlQuery = null;
	ArrayList<String> prev_post_ids = new ArrayList<String>();
	int i=0;
	ArrayList<String> stringArrayList_searchresults = new ArrayList<String>();
	private List<String> tags = new ArrayList<String>();
	String query;
	ListView Searchlocation;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private UiLifecycleHelper lifecycleHelper;
	boolean pickFriendsWhenSessionOpened;
	ArrayList<String> prev_post_ids3;
	ArrayAdapter<String> adapter;
	ArrayList<String> stringArrayList = new ArrayList<String>();
	ArrayList<String> postMessages = new ArrayList<String>();
	ArrayList<String> postIDs_temp = new ArrayList<String>();
	ArrayList<String> stringArrayList_responses = new ArrayList<String>();
	ArrayList<String> stringArrayList_names = new ArrayList<String>();
	public static final List<String> ALL_PERMISSIONS = Arrays.asList(       
			"read_friendlists",
			"offline_access",
			"email",
			"read_stream",
			"user_location",
			"friends_likes",
			"friends_location"); 
	String place ="111856692159256";
	GraphPlace gp =null;
	String message;
	ListView listView1;
	private void makeMeRequest(final Session session) {
		Log.i("TAG", "In makeMeRequest method");
		Request request = Request.newMeRequest(session, 
				new Request.GraphUserCallback() {

			@Override
			public void onCompleted(GraphUser user, Response response) {
				Log.i("TAG", "In onCompleted of  makeMeRequest method");
				// If the response is successful
				if (session == Session.getActiveSession()) {
					if (user != null) {
						return;
					}
				}
				if (response.getError() != null) {
					// Handle error
				}
			}
		});
		request.executeAsync();
	} 

	private void requestPublishPermissions(Session session, String string) {
		Log.i("TAG", "in requestPublishPermissions ");

		//	session.openForPublish(new Session.OpenRequest(this).setPermissions(ALL_PERMISSIONS));
		if (session != null) {
			Log.i("TAG", "session != null of requestPublishPermissions");
			//session.openForPublish(new Session.OpenRequest(this).setPermissions(ALL_PERMISSIONS));
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, string)
			.setDefaultAudience(SessionDefaultAudience.ONLY_ME)
			.setRequestCode(REAUTH_ACTIVITY_CODE);
			session.requestNewPublishPermissions(newPermissionsRequest);
		}
	}


	@Override
	protected void onStart() {
		Log.i("TAG", "in onStart");
		super.onStart();
		// Update the display every time we are started.

	}

	@Override
	protected void onResume() {
		Log.i("TAG", "in onResume");
		super.onResume();
		AppEventsLogger.activateApp(this);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("TAG","in ActivityResult next line is switch(requestcode)");
		switch (requestCode) {
		case 1 :
			break;
		default:
			Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
			break;
		}
	}

	private boolean ensureOpenSession() {
		Log.i("TAG","in ensureOpenSession");
		if (Session.getActiveSession() == null ||
				!Session.getActiveSession().isOpened()) {
			Session.openActiveSession(this, true, new Session.StatusCallback() {
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					onSessionStateChanged(session, state, exception);

				}
			});
			return false;
		}
		return true;
	}

	private void onSessionStateChanged(Session session, SessionState state, Exception exception) {
		Log.i("TAG","in onSessionStateChanged");
		if (pickFriendsWhenSessionOpened && state.isOpened()) {
			//pickFriendsWhenSessionOpened = false;

		}
	}


	public final void displayFriendsLocation()
	{
		HashSet hs = new HashSet();
		hs.addAll(stringArrayList);
		stringArrayList.clear();
		stringArrayList.addAll(hs);
		Collections.sort(stringArrayList);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
				android.R.layout.activity_list_item, android.R.id.text1,stringArrayList );
		Searchlocation.setAdapter(adapter);

	}

	public boolean userLikes(final Session session) {
		if(ensureOpenSession())
		{
		Log.i("TAG", "userlikes");
		stringArrayList_searchresults.clear();
		Bundle params = new Bundle();
	//	String fqlQuery = null;
		Log.i("location value", s.toString());
if(s.toString().contentEquals("abc"))
{
	fqlQuery = "SELECT name from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()) AND type IN " +query + ");";
	Log.i("fql query", fqlQuery);
	//Bundle params = new Bundle();
	params.putString("q", fqlQuery);
}
else
{
	fqlQuery = "SELECT name from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()) AND type IN " +query + ") AND location.city = " + s;
		Log.i("fql query", fqlQuery);
		//Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		s="abc";
	
}
		//Bundle params = new Bundle();
		//params.putString("q", fqlQuery);
		Request request = new Request(session,
				"/fql",                         
				params,                         
				HttpMethod.GET,                 
				new Request.Callback(){         
			public void onCompleted(Response response) {
				//String location;
				String name;
				Log.i("userlikes", response.toString());
				fqlQuery ="";
				try
				{

					GraphObject go  = response.getGraphObject();
					JSONObject  jso = go.getInnerJSONObject();
					JSONArray   arr = jso.getJSONArray( "data" );
					for(int k = 0;k < arr.length(); k++)
					{
						name =arr.getJSONObject(k).getString("name");
						Log.i("name", name);
						if((name.contains("000")) || (name.contains("Smile")))
						{
							Log.i("tag","invalid page");
						}
						else
						{
						stringArrayList_searchresults.add(name);
						}

					}     
				}
				//}
				catch( Throwable t )
				{
					t.printStackTrace();
				}
				HashSet hs = new HashSet();
				hs.addAll(stringArrayList_searchresults);
				stringArrayList_searchresults.clear();
				stringArrayList_searchresults.addAll(hs);
				Collections.sort(stringArrayList_searchresults);
				
			}  
		}); 
		Request.executeBatchAsync(request);   
		return true;
	}
		else
		{
			Log.i("tag", "session not active");
					return false;
		}
	
	}


	public void FriendsLikesLocation(final Session session) {
		//final ProgressDialog progress;

		Log.i("TAG", "FriendsLikesLocation");
	String fqlQuery = "SELECT location from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()))";
		//String fqlQuery = "SELECT location from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()) AND type IN ('SHOPPING AND RETAIL','ELECTRONICS','HOSPITAL','BOOK','RESTAURANT/CAFE','HOTEL','BAR'))";
		Log.i("fql query", fqlQuery);
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Request request = new Request(session,
				"/fql",                         
				params,                         
				HttpMethod.GET,                 
				new Request.Callback(){         
			public void onCompleted(Response response) {
				Log.i("locations", response.toString());
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
				DisplaySearchLocations();
			} 
			
		}); 
		Request.executeBatchAsync(request); 
		
	}
	//final , gets only restaurant pages
	//SELECT name, categories.name FROM page WHERE page_id IN  (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()) AND type IN ('FOOD/BEVERAGES','BAKERY','RESTAURANT/CAFE'));
	//SELECT page_id, type FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()); 
	//SELECT name, categories.name FROM page WHERE page_id IN  (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me())) AND strpos(categories.name,"restaurant")>0;

	//SELECT type, name,location.city from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()) AND type IN ( "RESTAURANT/CAFE", "BAR", "HOTEL", "MEXICAN RESTAURANT"));

	//SELECT name,location from page where page_id IN (SELECT reviewee_id,rating FROM review WHERE reviewee_id IN (SELECT page_id, name from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me())) AND type IN ('FOOD/BEVERAGES','BAKERY','RESTAURANT/CAFE')));
	//

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		FriendsLikesLocation(Session.getActiveSession());
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		// Intent myintent = getIntent();
	      //  String[] Array = myintent.getStringArrayExtra("string-array");
	        
	       // List<String> stringList = new ArrayList<String>(Arrays.asList(Array)); 
		    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
	        //android.R.layout.activity_list_item, android.R.id.text1,stringList);
		Searchlocation = (ListView)findViewById(R.id.Searchlocation);
		Searchlocation.setVisibility(View.INVISIBLE);
	
			//Searchlocation.setAdapter(adapter);
			Searchlocation.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					Searchlocation.getItemAtPosition(position);

					if ((Searchlocation.getItemAtPosition(position)) == null) {
						Log.v("Searchlocation", "Null");
					}
					else
					{
						 s = Searchlocation.getItemAtPosition(position).toString();
						// int _listViewPostion = position;
						Log.i(s, s);
						Toast toast=Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
						toast.show();
						
					}
				} } );
		Button Search =(Button) findViewById(R.id.search_button);

		
		Search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(final View v)
			{	
				
				progress = ProgressDialog.show(SearchActivity.this, "Searching", "Please Wait");
			
				if(ensureOpenSession())
				{
				Log.i("TAG", "userlikes");
				stringArrayList_searchresults.clear();
				Bundle params = new Bundle();
			//	String fqlQuery = null;
				Log.i("location value", s.toString());
		if(s.toString().contentEquals("abc"))
		{
			fqlQuery = "SELECT name from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()) AND type IN " +query + ");";
			Log.i("fql query", fqlQuery);
			//Bundle params = new Bundle();
			params.putString("q", fqlQuery);
		}
		else
		{
			fqlQuery = "SELECT name from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()) AND type IN " +query + ") " +
					" AND location.city = " +"'" + s + "'";
				Log.i("fql query", fqlQuery);
				//Bundle params = new Bundle();
				params.putString("q", fqlQuery);
				s="abc";
			
		}
				//Bundle params = new Bundle();
				//params.putString("q", fqlQuery);
				Request request = new Request(Session.getActiveSession(),
						"/fql",                         
						params,                         
						HttpMethod.GET,                 
						new Request.Callback(){         
					public void onCompleted(Response response) {
						//String location;
						String name;
						Log.i("userlikes", response.toString());
						fqlQuery ="";
						try
						{

							GraphObject go  = response.getGraphObject();
							JSONObject  jso = go.getInnerJSONObject();
							JSONArray   arr = jso.getJSONArray( "data" );
							for(int k = 0;k < arr.length(); k++)
							{
								name =arr.getJSONObject(k).getString("name");
								Log.i("name", name);
								if((name.contains("000")) || (name.contains("Smile")))
								{
									Log.i("tag","invalid page");
								}
								else
								{
								stringArrayList_searchresults.add(name);
								}

							}     
						}
						//}
						catch( Throwable t )
						{
							t.printStackTrace();
						}
						HashSet hs = new HashSet();
						hs.addAll(stringArrayList_searchresults);
						stringArrayList_searchresults.clear();
						stringArrayList_searchresults.addAll(hs);
						Collections.sort(stringArrayList_searchresults);

						if(!(stringArrayList_searchresults.isEmpty()))
						{
							progress.dismiss();
							String[] stockArr = new String[stringArrayList_searchresults.size()];
							stockArr = stringArrayList_searchresults.toArray(stockArr);
							Log.i("TAG", "In onClick of search results button");
							Intent intent = new Intent(v.getContext(),NotificationsActivity.class);
							intent.putExtra("string-array",stockArr);
							startActivityForResult(intent,0);
							Log.i("TAG", "In startActivityForResult of search results button");
						}
						else
						{
							progress.dismiss();
							Toast.makeText(getApplicationContext(), "No search results found.", Toast.LENGTH_LONG).show();
							Log.i("TAG", "didnt start the search results activity");
						}
						
					}  
				}); 
				Request.executeBatchAsync(request);   
			
		

			}

			
			else
			{
				Log.i("tag", "session not active");
						
			}
			}
		});
		
		lifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				Log.i("TAG", "in call below lifecyclehelper ");
				onSessionStateChanged(session, state, exception);
			}
		});
		lifecycleHelper.onCreate(savedInstanceState);

		ensureOpenSession();
		makeMeRequest(Session.getActiveSession());
	}
	
	

	public void onCheckboxClicked(View view) {
		// Is the view now checked?
		boolean checked = ((CheckBox) view).isChecked();
	

		// Check which checkbox was clicked
		switch(view.getId()) {
		case R.id.restaurant:
			if (checked)
				Log.i("tag", "restaurant checked");
			query = "( 'CAFE','RESTAURANT','INDIAN RESTAURANT','RESTAURANT/CAFE', 'BAR', 'HOTEL','BUFFET RESTAURANT', 'ITALIAN RESTAURANT','MEXICAN RESTAURANT', 'CHINESE RESTAURANT','ASIAN FUSION RESTAURANT','ASIAN RESTAURANT','THAI RESTAURANT')";
		
		
			break;
		case R.id.shopping:
			if (checked)
				Log.i("tag", "shopping checked");
			query = "( 'SHOPPING MALL','SHOPPING/RETAIL')";
		
		
			break;
		case R.id.selectloc:
			if (checked)
			{
				Log.i("tag", "location checked");
				if(stringArrayList.isEmpty())
				{
					Searchlocation.setVisibility(View.VISIBLE);
				progress = ProgressDialog.show(SearchActivity.this, "Loading Locations", "Please Wait");
				}
				else
				{
					Searchlocation.setVisibility(View.VISIBLE);
				}
			break;
			}
			else
				Searchlocation.setVisibility(View.INVISIBLE);
			
			break;
		
		case R.id.hospitals:
			if (checked)
				Log.i("tag", "hospitals checked");
			query = "( 'HOSPITAL', 'CLINIC', 'HOSPITAL/CLINIC')";
		
		
			break;
		case R.id.attractions:
			if (checked)
				Log.i("tag", "things to do checked");
			query = "( 'AMUSEMENT', 'AMUSEMENT PARK RIDE', 'ART GALLERY', 'BOWLING ALLEY', 'CASINO','CIRCUS','CONCERT VENUE','HIGHWAY','HISTORICAL PLACE','LANDMARK','MONUMENT','MUSEUM', 'ORCHESTRA', 'PERFORMANCE VENUE', 'PUBLIC SQUARE', 'RACE TRACK', 'RODEO', 'SOCIAL CLUB', 'SPORTS VENUE & STADIUM', 'STREET', 'SYMPHONY', 'THEATRE', 'THEME PARK', 'TICKET SALES', 'TOURIST ATTRACTION', 'PUBLIC PLACES & ATTRACTIONS','ATTRACTIONS/THINGS TO DO')";
		
		
			break;
		case R.id.books:
			if (checked)
				Log.i("tag", "books checked");
			query = "( 'BOOK STORE', 'BOOK', 'BOOK SERIES')";
		
		
			break;

		}

	}

	public final void DisplaySearchLocations()
	{
		HashSet hs = new HashSet();
		hs.addAll(stringArrayList);
		stringArrayList.clear();
		stringArrayList.addAll(hs);
		Collections.sort(stringArrayList);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.mylist, android.R.id.text1,stringArrayList );
		progress.dismiss();
		Searchlocation.setAdapter(adapter);
	

	}
	
	
}
