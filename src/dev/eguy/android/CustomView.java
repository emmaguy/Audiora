package dev.eguy.android;

import android.content.Context;
import android.hardware.SensorEvent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CustomView extends SurfaceView implements SurfaceHolder.Callback
{
	private GameThread m_gThread = null;
	
	public CustomView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	
		// register interest in hearing about changes to our surface
		getHolder().addCallback(this);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		// TODO

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// fire - create a new fire event
		if (m_gThread != null && event.getAction() == MotionEvent.ACTION_DOWN)
		{
			m_gThread.OnTouchEvent();
		}
		return true;
	}

	public void OnSensorChanged(SensorEvent event)
	{
		if(m_gThread != null)
		{
			m_gThread.OnSensorChanged(event);
		}
	}

	public void SetGameThread(GameThread gThread)
	{
		m_gThread = gThread;
	}
}
