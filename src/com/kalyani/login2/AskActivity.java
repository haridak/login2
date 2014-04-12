package com.kalyani.login2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphLocation;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;


public class AskActivity extends Activity  {

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
	private static final int PICK_FRIENDS_ACTIVITY = 1;

	private Button pickFriendsButton;
	View v;
	SharedPreferences sp;
	ProgressDialog progress;
	private TextView resultsTextView;
	String results = "";
	String post_id;
	ArrayList<String> prev_post_ids = new ArrayList<String>();
	private EditText text;
	int index4=0;
	String respondant_firstname;
	private List<String> tags = new ArrayList<String>();
	private GraphUser user2;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private PendingAction pendingAction = PendingAction.NONE;
	private UiLifecycleHelper lifecycleHelper;
	boolean pickFriendsWhenSessionOpened;
	private boolean canPresentShareDialog=true;
	ArrayList<String> prev_post_ids3;
	String[] friends_firstnames = new String[300];
	String[] friends_uids=  new String[300];
	ArrayList<String> stringArrayList = new ArrayList<String>();
	ArrayList<String> postMessages = new ArrayList<String>();
	ArrayList<String> status_ids = new ArrayList<String>();
	ArrayList<String> processing_post_ids_temp = new ArrayList<String>();
	ArrayList<String> processing_post_ids = new ArrayList<String>();
	ArrayList<String> postIDs_temp = new ArrayList<String>();
	ArrayList<String> stringArrayList_responses = new ArrayList<String>();
	ArrayList<String> stringArrayList_names = new ArrayList<String>();
	private static final String PERMISSION = "publish_actions";
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
	private enum PendingAction {
		NONE,
		POST_STATUS_UPDATE
	}
	//	private void handleGraphApiAnnounce() {
	//		Session session = Session.getActiveSession();
	//		Log.i("TAG","In handle graph api annouce");
	//		List<String> permissions = session.getPermissions();
	//		if (!permissions.contains(ALL_PERMISSIONS)) {
	//			requestPublishPermissions(session, PERMISSION);
	//			return;
	//		}
	//
	//	}

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
						user2 = user;
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

			//			Session.NewPermissionsRequest newPermissionsRequest2 = new Session.NewPermissionsRequest(this, PERMISSION)
			//			// demonstrate how to set an audience for the publish permissions,
			//			// if none are set, this defaults to FRIENDS
			//			.setDefaultAudience(SessionDefaultAudience.FRIENDS)
			//			.setRequestCode(REAUTH_ACTIVITY_CODE);
			//			session.requestNewPublishPermissions(newPermissionsRequest2);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("TAG", "in onCreate method");
		getPostIDs(Session.getActiveSession());
		//	List<String> tags = new ArrayList<String>();
		//tags.add("1035192085");
		getFirstName(Session.getActiveSession()); 
		makeMeRequest(Session.getActiveSession());
		getCurrentUserLocation(Session.getActiveSession()); 
		//userLikes(Session.getActiveSession()); 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ask);
		//ViewMesssagesFromPostID(prev_post_ids);
		//ViewResponses(prev_post_ids);	
		//Log.i("shared_preferences",sp.getAll().toString());
		//processing_post_ids_temp.addAll((Collection<? extends String>) sp);
		//for(String s: processing_post_ids_temp)
		//{
		//	int pos = s.lastIndexOf("=");
		//	String substring =  s.substring(pos+1,s.length());
		//	
		//	processing_post_ids.add(substring);
		//	Log.i("got the postid from sp", substring);
		//}
		//Button loginButton = (Button)findViewById(R.id.login_button);

		text = (EditText) findViewById(R.id.editText1);


		//		 String fqlQuery = "SELECT current_location FROM user WHERE uid IN " +
		//	                "(SELECT uid2 FROM friend WHERE uid1 = me())";
		//	        Bundle params = new Bundle();
		//	        params.putString("q", fqlQuery);
		//	        Session session = Session.getActiveSession();
		//	        Request request = new Request(session,
		//	            "/fql",                         
		//	            params,                         
		//	            HttpMethod.GET,                 
		//	            new Request.Callback(){         
		//	                public void onCompleted(Response response) {
		//	                    Log.i("TAG", "Result: " + response.toString());
		//	                    parseFQLResponse(response);
		//	                }                  
		//	        }); 
		//	        Request.executeBatchAsync(request); 
		//displayFriendsLocation();
		// HashSet hs = new HashSet();
		//hs.addAll(stringArrayList);
		//stringArrayList.clear();
		//stringArrayList.addAll(hs);
		//Collections.sort(stringArrayList);
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
		//      android.R.layout.activity_list_item, android.R.id.text1,stringArrayList );
		this.listView1 = (ListView)findViewById(R.id.listView2);
		//  listView1.setAdapter(adapter);

		listView1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				listView1.getItemAtPosition(position);

				if ((listView1.getItemAtPosition(position)) == null) {
					Log.v("TextView", "Null");
				}
				else
				{
					String s = listView1.getItemAtPosition(position).toString();
					// int _listViewPostion = position;
					Log.i(s, s);
					Toast toast=Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
					toast.show();
					getFriends(s);
				}
			} } );
		Button clear =(Button) findViewById(R.id.button_clear);
		clear.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				tags.clear();
				text.setText("");
				resultsTextView.setText("");

			}
		});

		Button previousAnswer = (Button)findViewById(R.id.previous_answers);

		previousAnswer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				progress = ProgressDialog.show(AskActivity.this, "Searching", "Please Wait");
				ViewMesssagesFromPostID(prev_post_ids);
				//ViewResponses(prev_post_ids,v);
				//				if(!(stringArrayList_responses.isEmpty()))
				//				{
				//					String[] stockArr = new String[stringArrayList_responses.size()];
				//					stockArr = stringArrayList_responses.toArray(stockArr);
				//					Log.i("TAG", "In onClick of previousanswer button");
				//					Intent intent = new Intent(v.getContext(),NotificationsActivity.class);
				//					intent.putExtra("string-array",stockArr);
				//					startActivityForResult(intent,0);
				//					Log.i("TAG", "In startActivityForResult of previousanswer button");
				//				}
				//				else
				//				{
				//					Toast.makeText(getApplicationContext(), "No responses available.", Toast.LENGTH_SHORT).show();
				//					Log.i("TAG", "didnt start the notificationsactivity");
				//				}

			}

		});

		resultsTextView = (TextView) findViewById(R.id.resultsTextView);
		Button friendsloc =(Button) findViewById(R.id.friendsloc);
		friendsloc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i("TAG", "onClick of friendsloc");
				String fqlQuery = "SELECT current_location FROM user WHERE uid IN " +
						"(SELECT uid2 FROM friend WHERE uid1 = me())";
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
						parseFQLResponse(response);
					}                  
				}); 
				Request.executeBatchAsync(request);                 
			}
		});


		Button post  = (Button) findViewById(R.id.buttonPost);
		post.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "Posting message...", Toast.LENGTH_SHORT).show();
				message = text.getText().toString();
				Log.i("TAG", "in onclick of post button");
				requestPublishPermissions(Session.getActiveSession(),PERMISSION);
				onClickPostStatusUpdate();

			}
		});
		pickFriendsButton = (Button) findViewById(R.id.pickFriendsButton);
		pickFriendsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Log.i("TAG", "in onclick of pickfriendsbutton");
				startPickFriendsActivity();
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
		canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
	}

	@Override
	protected void onStart() {
		Log.i("TAG", "in onStart");
		super.onStart();
		// Update the display every time we are started.
		displaySelectedFriends(RESULT_OK);
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
		case PICK_FRIENDS_ACTIVITY:
			displaySelectedFriends(resultCode);
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
			pickFriendsWhenSessionOpened = false;

			startPickFriendsActivity();
		}
	}

	private void displaySelectedFriends(int resultCode) {

		Log.i("TAG","in displaySelectedFriends");
		String results = "";
		FriendPickerApplication application = (FriendPickerApplication) getApplication();
		Collection<GraphUser> selection = application.getSelectedUsers();
		Collection<GraphUser> selection2 = selection;
		if (selection2 != null && selection2.size() > 0) {
			ArrayList<String> names = new ArrayList<String>();
			for (GraphUser user : selection2) {
				names.add(user.getName());
				tags.add(user.getId());

			}
			results = TextUtils.join(", ", names);

		} else {
			results = "";
		}
		resultsTextView.setText(results);
	}

	//	private void onClickPickFriends() {
	//		Log.i("TAG","in onClickPickFriends");
	//		startPickFriendsActivity();
	//	}

	private void startPickFriendsActivity() {
		Log.i("TAG","in startPickFriendsActivity");
		if (ensureOpenSession()) {
			Intent intent = new Intent(this, PickFriendsActivity.class);
			// Note: The following line is optional, as multi-select behavior is the default for
			// FriendPickerFragment. It is here to demonstrate how parameters could be passed to the
			// friend picker if single-select functionality was desired, or if a different user ID was
			// desired (for instance, to see friends of a friend).
			PickFriendsActivity.populateParameters(intent, null, true, true);
			startActivityForResult(intent, PICK_FRIENDS_ACTIVITY);
		} else {
			pickFriendsWhenSessionOpened = true;
		}
	}
	private void onClickPostStatusUpdate() {
		Log.i("TAG", "In onClickPostStatusUpdate");
		performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
	}
	private void performPublish(PendingAction action, boolean allowNoSession) {
		Log.i("TAG", "In performPublish");
		Session session = Session.getActiveSession();
		if (session != null) {
			pendingAction = action;
			Log.i("TAG", "session != null of performPublish");
			if (hasPublishPermission()) {
				// We can do the action right away.
				Log.i("TAG", "In hasPublishPermission() of performPublish");
				handlePendingAction();
				return;
			} else if (session.isOpened()) {
				Log.i("TAG", "session.isOpened of performPublish");
				// We need to get new permissions, then complete the action when we get called back.
				//session.removeCallback(sessionStatusCallback);          // Remove callback from old session.
				//session = Session.openActiveSessionFromCache(context);  // Create new session by re-opening from cache.
				//session.addCallback(sessionStatusCallback);             // Add callback to new session.
				//  session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
				//  handlePendingAction();
				if(tags.toString().isEmpty() || message.toString()=="")
				{
					Toast.makeText(getApplicationContext(), "Some details are missing. Please re-enter", Toast.LENGTH_LONG).show();
				}
				else

				{
					Log.i("tags to string", tags.toString());
					Log.i("message to string", message.toString());
					Request request = Request
							.newStatusUpdateRequest(Session.getActiveSession(),message,place, tags, new Request.Callback()
							{
								@Override
								public void onCompleted(Response response) {
									Log.i("TAG", "onCompleted of performPublish");
									showPublishResult(message, response.getGraphObject(), response.getError());
								}
							});
					request.executeAsync();
					return;
				}
			}
			else
				Log.i("TAG", "no session.isOpened() of performPublish");
		}
		else
			Log.i("TAG", "no session performPublish of performPublish");

		if (allowNoSession) {
			Log.i("TAG", "In allowNoSession of performPublish");
			pendingAction = action;
			handlePendingAction();
		}
	}
	private boolean hasPublishPermission() {
		Log.i("TAG", "In hasPublishPermission");
		Session session = Session.getActiveSession();
		return session != null && session.getPermissions().contains("publish_actions");
	}
	@SuppressWarnings("incomplete-switch")
	private void handlePendingAction() {
		Log.i("TAG", "In handlePendingAction");
		PendingAction previouslyPendingAction = pendingAction;
		// These actions may re-set pendingAction if they are still pending, but we assume they
		// will succeed.
		pendingAction = PendingAction.NONE;

		switch (previouslyPendingAction) {
		case POST_STATUS_UPDATE:
			postStatusUpdate();
			break;
		}
	}
	//	private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
	//		return new FacebookDialog.ShareDialogBuilder(this)
	//		.setName("AskMyNetwork")
	//		.setDescription("The 'AskMyNetwork' application lets you choose friends and ask them question")
	//		.setLink("");
	//	}
	private void postStatusUpdate() {
		Log.i("TAG", "In postStatusUpdate");
		canPresentShareDialog=true;
		if (user2 != null && hasPublishPermission()) {
			Log.d(""+user2, "user2's value");
			Log.i("TAG", "In user != null && hasPublishPermission() of postStatusUpdate ");
			if((tags.toString()=="") || (message.toString()==""))
			{
				Toast.makeText(getApplicationContext(), "Some details are missing. Please re-enter", Toast.LENGTH_LONG).show();
			}
			else

			{
				Log.i("tags to string", tags.toString());
				Log.i("message to string", message.toString());
				Request request = Request
						.newStatusUpdateRequest(Session.getActiveSession(), message, place ,tags, new Request.Callback() {
							@Override
							public void onCompleted(Response response) {
								Log.i("TAG", "In oncompleted of postStatusUpdate");
								showPublishResult(message, response.getGraphObject(), response.getError());
							}

						});
				request.executeAsync();
			}
		}
		else if(user2==null)
		{
			Log.d(""+user2, "user2's value: ");
			Log.i("TAG","USER2 IS NULL postStatusUpdate");
		}

		//        if (canPresentShareDialog) {
		//        	Log.i("TAG", "In canPresentShareDialog");
		//            FacebookDialog shareDialog = createShareDialogBuilder().build();
		//            uiHelper.trackPendingDialogCall(shareDialog.present());
		//        } else if (user != null && hasPublishPermission()) {
		//        	Log.i("TAG", "In user != null && hasPublishPermission()");
		//           // final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
		//            Request request = Request
		//                    .newStatusUpdateRequest(Session.getActiveSession(), message, new Request.Callback() {
		//                        @Override
		//                        public void onCompleted(Response response) {
		//                            showPublishResult(message, response.getGraphObject(), response.getError());
		//                        }
		//                    });
		//            request.executeAsync();
		//        } else {
		//        	
		//            pendingAction = PendingAction.POST_STATUS_UPDATE;
		//        }
	}
	private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
		String substring = "";
		Log.i("TAG", "In showPublishResult");
		String title = null;
		String alertMessage = null;
		text.setText("");
		if (error == null) {
			title = getString(R.string.success);
			post_id = result.cast(GraphObjectWithId.class).getId();
			int pos = post_id.lastIndexOf("_");
			substring =  post_id.substring(pos+1,post_id.length());
			alertMessage = getString(R.string.successfully_posted_post,"",substring);
		} else {
			title = getString(R.string.error);
			alertMessage = error.getErrorMessage();
		}
		prev_post_ids.add(substring);
		sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(substring, substring);
		Log.i("post_ids_added_to_sp", substring);
		editor.commit();
		new AlertDialog.Builder(this)

		.setTitle(title)
		.setMessage(alertMessage)
		.setPositiveButton(R.string.ok, null)
		.show();
		Log.i("post_id", substring);
		//ViewResponses(prev_post_ids);
	}
	private interface GraphObjectWithId extends GraphObject {
		String getId();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final void parseFQLResponse( Response response )
	{
		JSONObject fbInfo = new JSONObject();

		try
		{
			GraphObject go  = response.getGraphObject();
			JSONObject  jso = go.getInnerJSONObject();
			JSONArray   arr = jso.getJSONArray( "data" );
			for(int i = 0;i < arr.length(); i++){
				// Log.i("TAG", "inside 1st for loop");

				if((arr.getJSONObject(i).isNull("current_location")))
				{
					Log.i("tag","value is null");
				}
				else
				{
					fbInfo = arr.getJSONObject(i).getJSONObject("current_location");
					String temp= fbInfo.getString("city").toString();
					stringArrayList.add(temp);

				}

			}
		}

		catch ( Throwable t )
		{
			t.printStackTrace();
		}

		//	    HashSet hs = new HashSet();
		//	    hs.addAll(stringArrayList);
		//	    stringArrayList.clear();
		//	    stringArrayList.addAll(hs);
		//	    Collections.sort(stringArrayList);
		//	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
		//	            android.R.layout.activity_list_item, android.R.id.text1,stringArrayList );
		//	    listView1.setAdapter(adapter);
		displayFriendsLocation();
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

		listView1.setAdapter(adapter);

	}

	public void getFriends(String city) {
		Log.i("TAG", "onClick of friendsloc");
		String fqlQuery = "SELECT uid, name FROM user WHERE uid IN (SELECT uid2 FROM friend WHERE uid1 = me()) AND current_location.city='"+city+"'"; ;
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
				parseLocationFQLResponse(response);
			}                  
		}); 
		Request.executeBatchAsync(request);                 
	}

	public final void parseLocationFQLResponse( Response response )
	{
		String fbInfo;
		String fbInfo_UID;

		try
		{
			GraphObject go  = response.getGraphObject();
			JSONObject  jso = go.getInnerJSONObject();
			JSONArray   arr = jso.getJSONArray( "data" );
			for(int i = 0;i < arr.length(); i++){
				// Log.i("TAG", "inside 1st for loop");

				//if((arr.getJSONObject(i).isNull("uid")))
				//	{
				//Log.i("tag","value is null");
				//}
				//else
				//{
				fbInfo_UID = arr.getJSONObject(i).getString("uid");
				fbInfo = arr.getJSONObject(i).getString("name");
				Log.i("fbinfo", fbInfo);
				//String temp= fbInfo.substring("name").toString();
				stringArrayList_names.add(fbInfo);
				results = TextUtils.join(", ", stringArrayList_names);
				resultsTextView.setText(results);
				tags.add(fbInfo_UID);


				//              		}


			}
		}

		catch ( Throwable t )
		{
			t.printStackTrace();
		}

	}



	public void ViewResponses(ArrayList<String> prev_post_ids2)
	{
		prev_post_ids2.add("10202533463151228");
		prev_post_ids2.add("10202533466631315");

		//ViewMesssagesFromPostID(prev_post_ids);
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for( String s : prev_post_ids2) {
			String fqlQuery = 
					"SELECT fromid, post_id, text FROM comment where post_id  = " + "'"+s +"';";
			Log.i("fql query", fqlQuery);
			Bundle params = new Bundle();
			params.putString("q", fqlQuery);
			Session session = Session.getActiveSession();
			Request request = new Request(session,
					"/fql",                         
					params,                         
					HttpMethod.GET,                 
					new Request.Callback(){         
				public void onCompleted(Response response) {
					Log.i("responses_data", "Result: " + response.toString());
					//parseAnswersFQLResponse(response);
					try
					{
						GraphObject go  = response.getGraphObject();
						JSONObject  jso = go.getInnerJSONObject();
						JSONArray   arr = jso.getJSONArray( "data" );
						if(postIDs_temp.containsAll(prev_post_ids))
						{
							Log.i("tag","the post id already exists");
						}
						else
						{
							String s = postMessages.get(index4);
							stringArrayList_responses.add(s);
							for(int i = 0;i < arr.length(); i++){
								// Log.i("TAG", "inside 1st for loop");

								if((arr.getJSONObject(i).isNull("text")))
								{
									Log.i("tag","value is null");
								}
								else
								{
									int index_condition=friends_uids.length;
									String fbInfo1 = arr.getJSONObject(i).getString("fromid");
									for(int index2=0;index2<index_condition;index2++)
									{
										Log.i("uid",friends_uids[index2]);
										Log.i("fromid",fbInfo1);
										if(friends_uids[index2].contentEquals(fbInfo1))

										{
											Log.i("checking uid fromid uid:", friends_uids[index2]);
											Log.i("checking uid fromid fromid:", fbInfo1);
											index_condition=index2;
											String fbInfo3 = arr.getJSONObject(i).getString("post_id");
											String fbInfo2 = arr.getJSONObject(i).getString("text");
											String temp1 = friends_firstnames[index2] + " says: " +fbInfo2;
											stringArrayList_responses.add(temp1);
											postIDs_temp.add(fbInfo3);
										}
										else
											Log.i("tag", "fromid and uid dont match");
									}
								}  

							}


						}       

					}

					catch ( Throwable t )
					{
						t.printStackTrace();
					}

					index4++;
					if(!(stringArrayList_responses.isEmpty()))
					{
						progress.dismiss();

						String[] stockArr = new String[stringArrayList_responses.size()];
						stockArr = stringArrayList_responses.toArray(stockArr);
						Log.i("TAG", "In onClick of previousanswer button");
						//Intent intent2 = new Intent()
						Intent intent = new Intent(getBaseContext(),NotificationsActivity.class);
						intent.putExtra("string-array",stockArr);
						startActivityForResult(intent,0);
						Log.i("TAG", "In startActivityForResult of previousanswer button");
					}
					else
					{
						progress.dismiss();
						Toast.makeText(getApplicationContext(), "No responses available.", Toast.LENGTH_SHORT).show();
						Log.i("TAG", "didnt start the notificationsactivity");
					}
				}

			}); 
			Request.executeBatchAsync(request);  
		}


	}
	public void ViewMesssagesFromPostID(ArrayList<String> prev_post_ids4)
	{


		prev_post_ids4.add("10202533463151228");
		prev_post_ids4.add("10202533466631315");

		for( String s : prev_post_ids4) {

			if(s.isEmpty())
			{
				Log.i("tag", " the post id in viewresponses is empty");

			}
			else
			{
				String fqlQuery = 
						"SELECT status_id, message FROM status where status_id  = " + "'"+s +"';";
				Log.i("fql query", fqlQuery);
				Bundle params = new Bundle();
				params.putString("q", fqlQuery);
				Session session = Session.getActiveSession();
				Request request = new Request(session,
						"/fql",                         
						params,                         
						HttpMethod.GET,                 
						new Request.Callback(){         
					public void onCompleted(Response response) {
						Log.i("ViewMesssagesFromPostID", "Result: " + response.toString());
						//parseViewMesssagesFromPostID(response);
						String fbInfo3;
						String statusid;

						try
						{
							GraphObject go  = response.getGraphObject();
							JSONObject  jso = go.getInnerJSONObject();
							JSONArray   arr = jso.getJSONArray( "data" );
							for(int i = 0;i < arr.length(); i++){
								// Log.i("TAG", "inside 1st for loop");

								if((arr.getJSONObject(i).isNull("message")))
								{
									Log.i("tag","value is null");
								}
								else
								{
									fbInfo3 = arr.getJSONObject(i).getString("message");
									statusid =arr.getJSONObject(i).getString("status_id");
									Log.i("fbinfo", fbInfo3);
									//String temp= fbInfo.substring("name").toString();
									postMessages.add(fbInfo3);
									status_ids.add(statusid);
								}

							}       
						}

						catch ( Throwable t )
						{
							t.printStackTrace();
						}
						ViewResponses(prev_post_ids);
					}                  
				}); 
				Request.executeBatchAsync(request);  
			}

		}
	}


	public void parseViewMesssagesFromPostID(Response response)
	{

		String fbInfo3;

		try
		{
			GraphObject go  = response.getGraphObject();
			JSONObject  jso = go.getInnerJSONObject();
			JSONArray   arr = jso.getJSONArray( "data" );
			for(int i = 0;i < arr.length(); i++){
				// Log.i("TAG", "inside 1st for loop");

				if((arr.getJSONObject(i).isNull("message")))
				{
					Log.i("tag","value is null");
				}
				else
				{
					fbInfo3 = arr.getJSONObject(i).getString("message");

					Log.i("fbinfo", fbInfo3);
					//String temp= fbInfo.substring("name").toString();
					postMessages.add(fbInfo3);


				}
			}       
		}

		catch ( Throwable t )
		{
			t.printStackTrace();
		}

	}


	public ArrayList<String>  parseAnswersFQLResponse(Response response)
	{
		String fbInfo1;
		String fbInfo2;
		String fbInfo3;

		try
		{
			GraphObject go  = response.getGraphObject();
			JSONObject  jso = go.getInnerJSONObject();
			JSONArray   arr = jso.getJSONArray( "data" );
			if(postIDs_temp.containsAll(prev_post_ids))
			{
				Log.i("tag","the post id already exists");
			}
			else
			{
				String current_post_id ="abc";

				for(int index3 = 0;index3 < arr.length(); index3++){
					// Log.i("TAG", "inside 1st for loop");

					if((arr.getJSONObject(index3).isNull("text")))
					{
						Log.i("tag","value is null");
					}
					else
					{
						fbInfo1 = arr.getJSONObject(index3).getString("fromid");
						fbInfo3 = arr.getJSONObject(index3).getString("post_id");
						for(int index5=0;index5<=status_ids.size();index5++)
						{
							if(status_ids.get(index5).contentEquals(fbInfo1) && prev_post_ids.get(index5).contentEquals(fbInfo1))
							{
								stringArrayList_responses.add(postMessages.get(index5));
								current_post_id=status_ids.get(index5);
							}
							else
							{

							}

							
							fbInfo2 = arr.getJSONObject(index3).getString("text");
							Log.i("fbinfo", fbInfo2);
							//String temp= fbInfo.substring("name").toString();
							for(int index2=0;index2<=friends_uids.length;index2++)
							{
								
								if(friends_uids[index2].contentEquals(fbInfo3) && current_post_id.contentEquals(fbInfo3))
								{
									
									String temp1 = friends_firstnames[index2] + " says: " +fbInfo2;
									stringArrayList_responses.add(temp1);
									postIDs_temp.add(fbInfo3);
								}
								else
									Log.i("tag", "fromid and uid dont match");
							}

						}
					}

				}       

			}

		}
		catch ( Throwable t )
		{
			t.printStackTrace();
		}
		return stringArrayList_responses;

	}


	public void getFirstName(final Session session) 
	{
		Log.i("TAG", "getFirstName");
		String fqlQuery = "SELECT uid, first_name FROM user WHERE uid IN (SELECT uid1,uid2 FROM friend where uid1=me())";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Request request = new Request(session,
				"/fql",                         
				params,                         
				HttpMethod.GET,                 
				new Request.Callback(){         
			public void onCompleted(Response response) {
				//JSONObject fbInfo = new JSONObject();
				Log.i("TAG", "friends of user " + response.toString());
				try
				{
					GraphObject go  = response.getGraphObject();
					JSONObject  jso = go.getInnerJSONObject();
					JSONArray   arr = jso.getJSONArray( "data" );

					for(int index=0;index<arr.length();index++)
					{
						friends_uids[index]= (String) arr.getJSONObject(index).get("uid").toString();
						friends_firstnames[index]= (String) arr.getJSONObject(index).get("first_name").toString();

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
	//https://graph.facebook.com/fql?q=SELECT current_location FROM user WHERE uid=me()& access_token=xxxxx

	public void getCurrentUserLocation(final Session session) {

		Log.i("TAG", "onClick of friendsloc");
		String fqlQuery = "SELECT current_location.id FROM user WHERE uid = me()";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Request request = new Request(session,
				"/fql",                         
				params,                         
				HttpMethod.GET,                 
				new Request.Callback(){         
			public void onCompleted(Response response) {
				JSONObject fbInfo = new JSONObject();
				Log.i("TAG", "current_location: " + response.toString());
				try
				{
					GraphObject go  = response.getGraphObject();
					JSONObject  jso = go.getInnerJSONObject();
					JSONArray   arr = jso.getJSONArray( "data" );

					fbInfo = arr.getJSONObject(0).getJSONObject("current_location");

					if((fbInfo.getString("id").isEmpty()))
					{
						Log.i("tag","value is null");
					}
					else
					{

						place =fbInfo.getString("id");
						Log.i(" the place id", place);

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

	//	public void userLikes(final Session session) {
	//
	//		Log.i("TAG", "userlikes");
	//		String fqlQuery = "SELECT reviewee_id,rating FROM review WHERE reviewee_id IN (SELECT page_id, name from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me())) AND type IN ('FOOD/BEVERAGES','BAKERY','RESTAURANT/CAFE'))";
	//		Bundle params = new Bundle();
	//		params.putString("q", fqlQuery);
	//		Request request = new Request(session,
	//				"/fql",                         
	//				params,                         
	//				HttpMethod.GET,                 
	//				new Request.Callback(){         
	//			public void onCompleted(Response response) {
	//				JSONObject fbInfo = new JSONObject();
	//				int avg = 0;
	//				ArrayList<Integer> reviewee_ids = new ArrayList<Integer>();
	//				ArrayList<Integer> ratings = new ArrayList<Integer>();
	//				Log.i("userlikes", response.toString());
	//				try
	//				{
	//				
	//					GraphObject go  = response.getGraphObject();
	//					JSONObject  jso = go.getInnerJSONObject();
	//					JSONArray   arr = jso.getJSONArray( "data" );
	//					for(int k = 1;k < arr.length(); k++)
	//					{
	//						//for(int j=0;j<=1;j++)
	//						//{
	//					int c =arr.getJSONObject(k).getInt("reviewee_id");
	//					int a = arr.getJSONObject(k).getInt("rating");
	//				if(c>5)
	//				{
	//					c=c+arr.getJSONObject(k-1).getInt("reviewee_id")/2;
	//				}
	//				else
	//					a= a+arr.getJSONObject(k).getInt("rating")/2;
	//					
	//					Log.i("rating", Integer.toString(c));
	//					Log.i("rating", Integer.toString(a));
	//					//markers.add(c);
	//					
	//			
	//				}     
	//					}
	//				//}
	//				catch( Throwable t )
	//				{
	//					t.printStackTrace();
	//			}
	//			}  
	//		}); 
	//		Request.executeBatchAsync(request);                 
	//	}
	//final , gets only restaurant pages
	//SELECT name, categories.name FROM page WHERE page_id IN  (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()) AND type IN ('FOOD/BEVERAGES','BAKERY','RESTAURANT/CAFE'));
	//SELECT page_id, type FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()); 
	//SELECT name, categories.name FROM page WHERE page_id IN  (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me())) AND strpos(categories.name,"restaurant")>0;
	//
	//	public void userLikes(final Session session) {
	//
	//		Log.i("TAG", "userlikes");
	//		new Request(
	//		    session,
	//		    "/{review-id}",
	//		    null,
	//		    HttpMethod.GET,
	//		    new Request.Callback() {
	//		        public void onCompleted(Response response) {
	//		           Log.i("new_user_like" ,response.toString());
	//		        }
	//		    }
	//		).executeAsync(); 
	//	}

	//SELECT type, name,location.city from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me()) AND type IN ( "RESTAURANT/CAFE", "BAR", "HOTEL", "MEXICAN RESTAURANT"));

	//SELECT name,location from page where page_id IN (SELECT reviewee_id,rating FROM review WHERE reviewee_id IN (SELECT page_id, name from page WHERE page_id IN (SELECT page_id FROM page_fan WHERE uid IN (SELECT uid1, uid2 FROM friend WHERE uid1 = me())) AND type IN ('FOOD/BEVERAGES','BAKERY','RESTAURANT/CAFE')));

	public void onBackPressed()  
	{  
		//do whatever you want the 'Back' button to do  
		//as an example the 'Back' button is set to start a new Activity named 'NewActivity'  
		this.startActivity(new Intent(AskActivity.this,MainActivity.class));  

		return;  
	}

	public void getPostIDs(Session session)
	{


		Log.i("TAG", "onClick of friendsloc");
		String fqlQuery = "select status_id from status where source ='491327477645562' AND uid=me()";
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Request request = new Request(session,
				"/fql",                         
				params,                         
				HttpMethod.GET,                 
				new Request.Callback(){         
			public void onCompleted(Response response) {
				//Log.i("TAG", "post_ids_from_app: " + response.toString());
				try
				{
					GraphObject go  = response.getGraphObject();
					JSONObject  jso = go.getInnerJSONObject();
					JSONArray   arr = jso.getJSONArray( "data" );
					for(int index8=0;index8<arr.length();index8++)
					{
						String post_id1 = arr.getJSONObject(index8).get("status_id").toString();
						Log.i("got postid", post_id1);
						processing_post_ids.add(post_id1);
					}
					prev_post_ids.addAll(processing_post_ids);
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
