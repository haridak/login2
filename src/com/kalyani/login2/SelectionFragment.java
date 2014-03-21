package com.kalyani.login2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class SelectionFragment extends Fragment {
	//added for auth
	private static final String TAG = "SelectionFragment";
	@Override
	public View onCreateView(LayoutInflater inflater, 
	        ViewGroup container, Bundle savedInstanceState) {
	    super.onCreateView(inflater, container, savedInstanceState);
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
            	startActivityForResult(intent,0);
            	
            	//Toast.makeText(getActivity(), "clicked search button",Toast.LENGTH_SHORT).show();
            } 
  }); 
	    askButton.setOnClickListener(new OnClickListener()
	    {
            @Override
            public void onClick(View v)
            {
            	Intent intent = new Intent(v.getContext(),AskActivity.class);
            	startActivityForResult(intent,0);
            } 
  }); 
	    return view;
	}
	//added for auth ends here

}
