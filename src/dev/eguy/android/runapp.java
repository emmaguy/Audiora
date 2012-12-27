package dev.eguy.android;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class runapp extends Activity {

	private TextView m_scoreView;
	private CustomView m_custView;
	private GameThread m_gThread = null;
	
	private Sensor m_accelerometerSensor;
	private SensorManager m_sensorManager;
	private SensorEventListener m_accelerationListener = new SensorEventListener()
	{
    	@Override
		public void onAccuracyChanged(Sensor sensor, int acc) 
    	{
		}
 
		@Override
		public void onSensorChanged(SensorEvent event) 
		{
			m_custView.OnSensorChanged(event);
		}
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
		
	    setContentView(R.layout.main);	    
	    
	    m_custView = (CustomView)findViewById(R.id.cView);
	    m_scoreView = (TextView)findViewById(R.id.ScoreTextView);
		m_sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Sensor> accelerometers = m_sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
		
		if(accelerometers.size() > 0)
			m_accelerometerSensor = accelerometers.get(0);
		
		// create the thread which will contain and execute the game logic
		m_gThread = new GameThread(m_custView.getContext(), m_custView.getHolder(), m_scoreView);
		
		m_custView.SetGameThread(m_gThread);
	}
	
	@Override
	protected void onPause()
	{	
//		m_gThread.suspend();
		m_gThread.SetRunningState(GlobalHelpers.RunningState.Paused);
		m_sensorManager.unregisterListener(m_accelerationListener);
		super.onPause();
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();

		m_sensorManager.registerListener(m_accelerationListener, m_accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
		if (m_gThread != null)
		{			
			m_gThread.SetRunningState(GlobalHelpers.RunningState.Running);
			m_gThread.start();
		}
	}
 
	@Override
	protected void onStop() 
	{
		m_gThread.SetRunningState(GlobalHelpers.RunningState.Stopped);
		super.onStop();
	}
}
