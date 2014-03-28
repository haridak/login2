package com.kalyani.login2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MainActivity extends FragmentActivity {

	//added for auth
	private static final int SPLASH = 0;
	private static final int SELECTION = 1;
	private static final int SETTINGS = 2;
	private static final int FRAGMENT_COUNT = SETTINGS +1;
	private static final String PERMISSION = "publish_actions";

	private MenuItem settings; //private variable to represent the menu item for the settings fragment. You'll use this to trigger the UserSettingsFragment display
	
	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

	private boolean isResumed = false; //flag that indicates a visible activity. This flag is used to enable session state change checks.
	

	//hides the fragments in the beginning
	//the UiLifecycleHelper activity lifecycle methods that are needed to properly keep track of the session:
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("TAG", "in oncreate");
	    super.onCreate(savedInstanceState);
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);

	    FragmentManager fm = getSupportFragmentManager();
	    fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
	    fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
	    fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

	    FragmentTransaction transaction = fm.beginTransaction();
	    for(int i = 0; i < fragments.length; i++) {
	        transaction.hide(fragments[i]);
	    }
	    transaction.commit();
	}
	@Override
	public void onResume() {
		Log.i("TAG", "in onresume");

	    super.onResume();
	    uiHelper.onResume();
	    isResumed = true;
	}

	@Override
	public void onPause() {
		Log.i("TAG", "in onpause");

	    super.onPause();
	    uiHelper.onPause();
	    isResumed = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("TAG", "in onactivityresult");

	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onDestroy() {
		Log.i("TAG", "in ondestroy");

	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.i("TAG", "in onsaveinstancestate");

	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	//session management related code
	private void showFragment(int fragmentIndex, boolean addToBackStack) {
		Log.i("TAG", "in showfragment");

	    FragmentManager fm = getSupportFragmentManager();
	    FragmentTransaction transaction = fm.beginTransaction();
	    for (int i = 0; i < fragments.length; i++) {
	        if (i == fragmentIndex) {
	            transaction.show(fragments[i]);
	        } else {
	            transaction.hide(fragments[i]);
	        }
	    }
	    if (addToBackStack) {
	        transaction.addToBackStack(null);
	    }
	    transaction.commit();
	}
	
	
	//this code shows relevant fragment based on the user's authentication status
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.i("TAG", "in onSessionStateChange");

	    // Only make changes if the activity is visible
	    if (isResumed) {
	        FragmentManager manager = getSupportFragmentManager();
	        // Get the number of entries in the back stack
	        int backStackSize = manager.getBackStackEntryCount();
	        // Clear the back stack
	        for (int i = 0; i < backStackSize; i++) {
	            manager.popBackStack();
	        }
	        if (state.isOpened()) {
	        	//session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSION));
	            // If the session state is open:
	            // Show the authenticated fragment
	            showFragment(SELECTION, false);
	        } else if (state.isClosed()) {
	            // If the session state is closed:
	            // Show the login fragment
	            showFragment(SPLASH, false);
	        }
	    }
	}
	
	//method to handle session changes
	@Override
	protected void onResumeFragments() {
		Log.i("TAG", "in onResumeFragments");
	    super.onResumeFragments();
	    Session session = Session.getActiveSession();

	    if (session != null && session.isOpened()) {
	        // if the session is already open,
	        // try to show the selection fragment
	        showFragment(SELECTION, false);
	    } else {
	        // otherwise present the splash screen
	        // and ask the person to login.
	        showFragment(SPLASH, false);
	    }
	}
//the UiLifecycleHelper to track the session and trigger a session state change listener
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = 
	    new Session.StatusCallback() {
		
	    @Override
	    public void call(Session session, 
	            SessionState state, Exception exception)
	    {Log.i("TAG", "in call");
	        onSessionStateChange(session, state, exception);
	    }
	};
	//method to prepare the options menu display
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.i("TAG", "in onPrepareOptionsMenu");
	    // only add the menu when the selection fragment is showing
	    if (fragments[SELECTION].isVisible()) {
	        if (menu.size() == 0) {
	            settings = menu.add(R.string.settings);
	        }
	        return true;
	    } else {
	        menu.clear();
	        settings = null;
	    }
	    return false;
	}

	//method to display the UserSettingsFragment when the settings menu is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("TAG", "in onOptionsItemSelected");
	    if (item.equals(settings)) {
	        showFragment(SETTINGS, true);
	        return true;
	    }
	    return false;
	}
}
//added for auth ends here

