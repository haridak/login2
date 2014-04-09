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
	 
	    Button askButton=(Button) view.findViewById(R.id.button1);
	    searchButton.setOnClickListener(new OnClickListener()
	    {
            @Override
            public void onClick(View v)
            {
            	Intent intent = new Intent(v.getContext(),SearchActivity.class);
				Log.i("TAG", "In onClick of Search My Network button");
				startActivityForResult(intent,0);
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
	

}
