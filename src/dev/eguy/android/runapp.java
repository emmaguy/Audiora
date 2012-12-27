package dev.eguy.android;

import android.app.Activity;
import android.os.Bundle;

public class runapp extends Activity {

	private CustomView m_custView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.main);	    
	    m_custView = (CustomView)findViewById(R.id.cView);
	    //m_custView.Initialise();
	    
	    //Toast.makeText(this,"cheese",Toast.LENGTH_SHORT).show();
	}
}
