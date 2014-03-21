package com.kalyani.login2;

import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;

import com.facebook.widget.FacebookDialog;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

public class PostQuestion extends FragmentActivity {
	
	 private static final String PERMISSION = "publish_actions";
	    private static final Location SEATTLE_LOCATION = new Location("") {
	        {
	            setLatitude(47.6097);
	            setLongitude(-122.3331);
	        }
	    };
		//private static final String "TAG" = "POST QUESTION";

	    private final String PENDING_ACTION_BUNDLE_KEY = "com.kalyani.login2.PostQuestion:PendingAction";

	    private Button postStatusUpdateButton;
	    private Button postPhotoButton;
	    private Button pickFriendsButton;
	    private Button pickPlaceButton;
	    private LoginButton loginButton;
	    private ProfilePictureView profilePictureView;
	    private TextView greeting;
	    private PendingAction pendingAction = PendingAction.NONE;
	    private ViewGroup controlsContainer;
	    private GraphUser user;
	    private GraphPlace place;
	    private List<GraphUser> tags;
	    private boolean canPresentShareDialog;

	    private enum PendingAction {
	        NONE,
	        POST_PHOTO,
	        POST_STATUS_UPDATE
	        
	    }
	    private UiLifecycleHelper uiHelper;

	    private Session.StatusCallback callback = new Session.StatusCallback() {
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
	            onSessionStateChange(session, state, exception);
	        }
	    };
	
	private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
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
	
	  private void postStatusUpdate() {
	        if (canPresentShareDialog) {
	            FacebookDialog shareDialog = createShareDialogBuilder().build();
	            uiHelper.trackPendingDialogCall(shareDialog.present());
	        } else if (user != null && hasPublishPermission()) {
	            final String message = getString(R.string.status_update, user.getFirstName(), (new Date().toString()));
	            Request request = Request
	                    .newStatusUpdateRequest(Session.getActiveSession(), message, place, tags, new Request.Callback() {
	                        @Override
	                        public void onCompleted(Response response) {
	                            showPublishResult(message, response.getGraphObject(), response.getError());
	                        }
	                    });
	            request.executeAsync();
	        } else {
	            pendingAction = PendingAction.POST_STATUS_UPDATE;
	        }
	    }

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	    	Log.i("TAG","IN ONCREATE OF POST QUESTION");
	        super.onCreate(savedInstanceState);
	        uiHelper = new UiLifecycleHelper(this, callback);
	        uiHelper.onCreate(savedInstanceState);
	        Session.getActiveSession();
	        if (savedInstanceState != null) {
	            String name = savedInstanceState.getString(PENDING_ACTION_BUNDLE_KEY);
	            pendingAction = PendingAction.valueOf(name);
	        }

	        setContentView(R.layout.postquestion);
	        onClickPostStatusUpdate();
}
	    private void onClickPostStatusUpdate() {
	        performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
	    	Log.i("TAG","IN onClickPostStatusUpdate");
	    }
	    private void performPublish(PendingAction action, boolean allowNoSession) {
	    	
	    	Log.i("TAG","IN performPublish");
	    	
	        Session session = Session.getActiveSession();
	        if (session != null) {
	            pendingAction = action;
	            if (hasPublishPermission()) {
	                // We can do the action right away.
	                handlePendingAction();
	                return;
	            } else if (session.isOpened()) {
	                // We need to get new permissions, then complete the action when we get called back.
	                session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
	                return;
	            }
	        }

	        if (allowNoSession) {
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
	        PendingAction previouslyPendingAction = pendingAction;
	       Log.i("TAG",pendingAction.toString());
	        // These actions may re-set pendingAction if they are still pending, but we assume they
	        // will succeed.
	        pendingAction = PendingAction.NONE;
	        Log.i("TAG",previouslyPendingAction.toString());
	        switch (previouslyPendingAction) {
	        
	            case POST_STATUS_UPDATE:
	                postStatusUpdate();
	                break;
	        }
	    }
	    private interface GraphObjectWithId extends GraphObject {
	        String getId();
	    }
	    private FacebookDialog.ShareDialogBuilder createShareDialogBuilder() {
	        return new FacebookDialog.ShareDialogBuilder(this)
	                .setName("Hello Facebook")
	                .setDescription("The 'Hello Facebook' sample application showcases simple Facebook integration")
	                .setLink("http://developers.facebook.com/android");
	    }
	    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	        if (pendingAction != PendingAction.NONE &&
	                (exception instanceof FacebookOperationCanceledException ||
	                exception instanceof FacebookAuthorizationException)) {
	                new AlertDialog.Builder(PostQuestion.this)
	                    .setTitle(R.string.cancelled)
	                    .setMessage(R.string.permission_not_granted)
	                    .setPositiveButton(R.string.ok, null)
	                    .show();
	            pendingAction = PendingAction.NONE;
	        } else if (state == SessionState.OPENED_TOKEN_UPDATED) {
	            handlePendingAction();
	        }
	        updateUI();
	    }

	    private void updateUI() {
	        Session session = Session.getActiveSession();
	        boolean enableButtons = (session != null && session.isOpened());

	        postStatusUpdateButton.setEnabled(enableButtons || canPresentShareDialog);
	        postPhotoButton.setEnabled(enableButtons);
	        pickFriendsButton.setEnabled(enableButtons);
	        pickPlaceButton.setEnabled(enableButtons);

	        if (enableButtons && user != null) {
	            profilePictureView.setProfileId(user.getId());
	            greeting.setText(getString(R.string.hello_user, user.getFirstName()));
	        } else {
	            profilePictureView.setProfileId(null);
	            greeting.setText(null);
	        }
	    }
	    private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	            Log.d("HelloFacebook", "Success!");
	        }
	    };
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	    }

	    @Override
	    public void onPause() {
	        super.onPause();
	        uiHelper.onPause();
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        uiHelper.onDestroy();
	    }
	    @Override
	    protected void onResume() {
	        super.onResume();
	        uiHelper.onResume();

	        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
	        // the onResume methods of the primary Activities that an app may be launched into.
	        AppEventsLogger.activateApp(this);

	        updateUI();
	    }

	    @Override
	    protected void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	        uiHelper.onSaveInstanceState(outState);

	        outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
	    }

}