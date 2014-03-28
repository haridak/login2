package com.kalyani.login2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;

// adding a line to check checking on 3/21

public class AskActivity extends Activity  {
	//
	//	@Override
	//	protected void onCreate(Bundle savedInstanceState) {
	//		// TODO Auto-generated method stub
	//		super.onCreate(savedInstanceState);
	//		setContentView(R.layout.ask);
	//	}
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
	private TextView resultsTextView;
	private EditText text;
	private GraphUser user;
	private List<String> tags;
	//= Arrays.asList("100008034453600", "");
	private ArrayList<String> ids = new ArrayList<String>();
	private GraphUser user2;
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private boolean pendingAnnounce;
	private PendingAction pendingAction = PendingAction.NONE;
	private UiLifecycleHelper lifecycleHelper;
	boolean pickFriendsWhenSessionOpened;
	private boolean canPresentShareDialog=true;
	private static final String PERMISSION = "publish_actions";
	private static final String PERMISSION2 = "friends_location";
	public static final List<String> ALL_PERMISSIONS = Arrays.asList(       
			"read_friendlists",
			"publish_stream",
			"offline_access",
			"email",
			"read_stream",
			"user_location",
			"friends_location");

	 
	String place ="111856692159256";
	GraphPlace gp =null;
	String message;
	private enum PendingAction {
		NONE,
		POST_PHOTO,
		POST_STATUS_UPDATE
	}
	private UiLifecycleHelper uiHelper;

	private void handleGraphApiAnnounce() {
		Session session = Session.getActiveSession();
		Log.i("TAG","In handlegraphapiannouce");
		List<String> permissions = session.getPermissions();
		if (!permissions.contains(ALL_PERMISSIONS)) {
			pendingAnnounce = true;
			requestPublishPermissions(session, PERMISSION);
			return;
		}

	}
	
	private void makeMeRequest(final Session session) {
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {

	        @Override
	        public void onCompleted(GraphUser user, Response response) {
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
		
		//session.openForPublish(new Session.OpenRequest(this).setPermissions(ALL_PERMISSIONS));
		if (session != null) {
			Log.i("TAG", "session != null");
			//session.openForPublish(new Session.OpenRequest(this).setPermissions(ALL_PERMISSIONS));
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, string)
			.setDefaultAudience(SessionDefaultAudience.FRIENDS)
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
		//List<String> tags = new ArrayList<String>();
		//tags.add("1035192085");
		makeMeRequest(Session.getActiveSession());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ask);
		//Button loginButton = (Button)findViewById(R.id.login_button);
		
		text = (EditText) findViewById(R.id.editText1);
		//ListView listView1 = (ListView) findViewById(R.id.listView1);
		resultsTextView = (TextView) findViewById(R.id.resultsTextView);
		Button friendsloc =(Button) findViewById(R.id.friendsloc);
		friendsloc.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				requestPublishPermissions(Session.getActiveSession(),PERMISSION2);
				getLocation();
			}
		});
		
		Button post  = (Button) findViewById(R.id.buttonPost);
		post.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				message = text.getText().toString();
				Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
				Log.i("TAG", "in onclick");
				// canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
				//      FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
				requestPublishPermissions(Session.getActiveSession(),PERMISSION);
				onClickPostStatusUpdate();

			}
		});
		pickFriendsButton = (Button) findViewById(R.id.pickFriendsButton);
		pickFriendsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onClickPickFriends();
			}
		});

		lifecycleHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {
			@Override
			public void call(Session session, SessionState state, Exception exception) {
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
		super.onStart();

		// Update the display every time we are started.
		displaySelectedFriends(RESULT_OK);
		//getfriendsids(RESULT_OK);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//displaySelectedFriends(RESULT_OK);
		//getfriendsids(RESULT_OK);
		// Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
		// the onResume methods of the primary Activities that an app may be launched into.
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
		if (pickFriendsWhenSessionOpened && state.isOpened()) {
			pickFriendsWhenSessionOpened = false;

			startPickFriendsActivity();
		}
	}

	private void displaySelectedFriends(int resultCode) {
		
		Log.i("TAG","in displaySelectedFriends");
		String results = "";
		ArrayList<String> tags2;
		FriendPickerApplication application = (FriendPickerApplication) getApplication();
		Collection<GraphUser> selection = application.getSelectedUsers();
		if (selection != null && selection.size() > 0) {
			ArrayList<String> names = new ArrayList<String>();
			//ArrayList<String> tags = new ArrayList<String>();
			for (GraphUser user : selection) {
				//names.add(user.getName());
				tags.add(user.getId());
				
			}
			//tags2 = TextUtils.join(", ", tags);
			
			//tags = results;
		} else {
			results = "Choose friends and enter your question here";
		}
		text.setText(results);
	}

	private void getfriendsids(int resultCode) {
		Log.i("TAG","in getfriendsids");
		FriendPickerApplication application = (FriendPickerApplication) getApplication();
		Collection<GraphUser> selection = application.getSelectedUsers();
		if (selection != null && selection.size() > 0) {
			for (GraphUser user : selection) {
				ids.add(user.getId());
				Log.i(""+ids, "");
			}
		//tags.addAll(ids);
	}
		return;
	}
	private void onClickPickFriends() {
		startPickFriendsActivity();
	}

	private void startPickFriendsActivity() {
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
	private void makeFriendsRequest()
	{
		Session.openActiveSession(this, true, new Session.StatusCallback() {

		      // callback when session changes state
		      @SuppressWarnings("deprecation")
			@Override
		      public void call(Session session, SessionState state, Exception exception) {
		        if (session.isOpened()) {

		          // make request to the 
		            Request.executeMyFriendsRequestAsync(session, new Request.GraphUserListCallback() {

		                @Override
		                public void onCompleted(List<GraphUser> users, Response response) {
		                    //Log.d("AL",""+users.size() + response.toString());
//		                    for (int i=0;i<users.size();i++){
//		                    	GraphUser user = users.get(i);
//		                    	String Location = user.getLocation().toString();
//		                        Log.d("AL",""+users.get(i).toString());
//		                       // welcome.setText("Done");
//		                    }
		                    for(int i=0;i<users.size();i++)
		                    		{
		                    Log.d("AL",""+users.get(i).toString());
		                    		}
		                }
		            });
		        }
		      }
		    });
		//Log.i("TAG","In makeFriendsRequest();");
		//getLocation();
		
	}
	
	private void getLocation() {
	    Request myFriendsRequest = Request.newMyFriendsRequest(Session.getActiveSession(), 
	            new Request.GraphUserListCallback() {

	        @SuppressWarnings("unused")
			@Override
	        public void onCompleted(List<GraphUser> users, Response response) {
	            if (response.getError() == null) {
	            	List<GraphUser> friends = showFriendsandLocation(users, response);
	            	 for (int i=0;i<friends.size();i++){
	            		 GraphUser user1 = friends.get(i);
	                    	//String Location = user.getLocation().toString();
	                        Log.d("AL",""+users.get(i).toString());
	                       // welcome.setText("Done");
	                    }
	            	//listView1.
	            	//Toast.makeText(getApplicationContext(), (CharSequence) friends, Toast.LENGTH_SHORT).show();
	            	
	            }

	        }

	    });
	    // Add location to the list of info to get.
	    Bundle requestParams = myFriendsRequest.getParameters();
	    requestParams.putString("fields", "location");
	    myFriendsRequest.setParameters(requestParams);
	    myFriendsRequest.executeAsync();
	}

	private List<GraphUser> showFriendsandLocation(List<GraphUser> users, Response response) {
		Log.i("TAG", "In showPublishResult");
		String title = null;
		String alertMessage = null;
		FacebookRequestError error = response.getError();
		if (error == null) {
			title = getString(R.string.success);
			//String id = result.cast(GraphObjectWithId.class).getId();
			//alertMessage = getString(R.string.successfully_posted_post, message, id);
		} else {
			title = getString(R.string.error);
			alertMessage = error.getErrorMessage();
		}

		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(alertMessage)
		.setPositiveButton(R.string.ok, null)
		.show();
		GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
	    GraphObjectList<GraphObject> data = multiResult.getData();
	    return data.castToListOf(GraphUser.class); 
	}
	private void performPublish(PendingAction action, boolean allowNoSession) {
		Session session = Session.getActiveSession();
		if (session != null) {
			pendingAction = action;
			Log.i("TAG", "session != null");
			if (hasPublishPermission()) {
				// We can do the action right away.
				Log.i("TAG", "In hasPublishPermission()");
				handlePendingAction();
				return;
			} else if (session.isOpened()) {
				Log.i("TAG", "session.isOpened");
				// We need to get new permissions, then complete the action when we get called back.
				//session.removeCallback(sessionStatusCallback);          // Remove callback from old session.
				//session = Session.openActiveSessionFromCache(context);  // Create new session by re-opening from cache.
				//session.addCallback(sessionStatusCallback);             // Add callback to new session.
				//  session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
				//  handlePendingAction();
				Request request = Request
						.newStatusUpdateRequest(Session.getActiveSession(),message,place, tags, new Request.Callback()
						{
							//Request request = Request
							//.newStatusUpdateRequest(Session.getActiveSession(), message, new Request.Callback() {
							@Override
							public void onCompleted(Response response) {
								showPublishResult(message, response.getGraphObject(), response.getError());
							}
						});
				request.executeAsync();
				return;
			}
			else
				Log.i("TAG", "no session.isOpened()");
		}
		else
			Log.i("TAG", "no session performPublish");

		if (allowNoSession) {
			Log.i("TAG", "In performPublish");
			pendingAction = action;
			handlePendingAction();
		}
	}
	private boolean hasPublishPermission() {
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
		case POST_PHOTO:
			// postPhoto();
			break;
		case POST_STATUS_UPDATE:
			postStatusUpdate();
			break;
		}
	}
	private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
		return new FacebookDialog.ShareDialogBuilder(this)
		.setName("AskMyNetwork")
		.setDescription("The 'AskMyNetwork' application lets you choose friends and ask them question")
		.setLink("");
	}
	private void postStatusUpdate() {
		Log.i("TAG", "In postStatusUpdate");
		canPresentShareDialog=true;
		if (user2 != null && hasPublishPermission()) {
			Log.d(""+user2, "user2's value");
			Log.i("TAG", "In user != null && hasPublishPermission()");
			// final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
			Request request = Request
					.newStatusUpdateRequest(Session.getActiveSession(), message, place ,tags, new Request.Callback() {
						@Override
						public void onCompleted(Response response) {
							Log.i("TAG", "In oncompleted of poststatusupdate");
							showPublishResult(message, response.getGraphObject(), response.getError());
						}
					});
			request.executeAsync();
		}
		else if(user2==null)
		{
			Log.d(""+user2, "user2's value");
			Log.i("TAG","USER2 IS NULL");
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
		Log.i("TAG", "In showPublishResult");
		String title = null;
		String alertMessage = null;
		if (error == null) {
			title = getString(R.string.success);
			String id = result.cast(GraphObjectWithId.class).getId();
			alertMessage = getString(R.string.successfully_posted_post, message, id);
		} else {
			title = getString(R.string.error);
			alertMessage = error.getErrorMessage();
		}

		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(alertMessage)
		.setPositiveButton(R.string.ok, null)
		.show();
	}
	private interface GraphObjectWithId extends GraphObject {
		String getId();
	}
	
		
}
