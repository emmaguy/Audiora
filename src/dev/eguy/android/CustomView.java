package dev.eguy.android;

import android.content.Context;
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
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);

		// create the thread which will contain and execute the game logic
		m_gThread = new GameThread(context, holder);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		if (m_gThread != null)
		{
			m_gThread.SetRunningState(true);
			m_gThread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		if (m_gThread != null)
		{
			m_gThread.SetRunningState(false);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// fire - create a new fire event
		if (event.getAction() == MotionEvent.ACTION_DOWN)
		{
			m_gThread.Fire();
		}
		return true;
	}

}
