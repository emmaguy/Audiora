package dev.eguy.android;

import java.util.LinkedList;
import dev.eguy.android.AsteroidManager.Asteroid;
import dev.eguy.android.GlobalHelpers.RunningState;
import dev.eguy.android.ShipManager.Projectile;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.hardware.SensorEvent;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class GameThread extends Thread
{
	private static final int NUM_PARTICLES = 300;
	
	private int m_score = 0;
	private Context m_context = null;
	private TextView m_activity = null;
	private Resources m_resource = null;
	private ShipManager m_shipMgr = null;
	private SurfaceHolder m_holder = null;
	private AsteroidManager m_asteroids = null;
	private LinkedList<ParticleSys> m_explosions = new LinkedList<ParticleSys>();
	private GlobalHelpers.RunningState m_state = RunningState.Stopped;

	public GameThread(Context context, SurfaceHolder holder, TextView a)
	{
		Display disp = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		m_resource = context.getResources();
		
		m_holder = holder;
		m_context = context;
		m_shipMgr = new ShipManager(m_resource, disp.getWidth(), disp.getHeight());
		m_asteroids = new AsteroidManager(m_resource);
		m_activity = a;
	}
	
	public void SetRunningState(RunningState state)
	{
		m_state = state;
	}
	
	public void UpdateScore()
	{
		m_activity.post(new Runnable()
		{	
			@Override
			public void run()
			{
				m_activity.setText("Score: " + m_score);
			}
		});
	}
	
	public void run()
	{
		Canvas canvas = null;

		if (m_shipMgr == null ||  m_asteroids == null)
		{
			// not a lot we can do with not ship and no asteroids
			return;
		}
		
		while (m_state == RunningState.Running)
		{
			try
			{
				UpdateScore();
				
				canvas = m_holder.lockCanvas(null);

				if(canvas != null)
				{
					// draw background
					canvas.drawARGB(255, 0, 0, 0);
	
					// draw and update the action
					m_shipMgr.DrawAndUpdate(canvas);
					m_asteroids.DrawAndUpdate(canvas);
					
					for(int i = 0; i < this.m_explosions.size(); i++)
					{
						ParticleSys s = this.m_explosions.get(i);
						
						// draw and update all the particle systems (explosions) for next loop
						s.DrawAndUpdateParticles(canvas);
						if(s.IsDeceased())
						{
							this.m_explosions.remove(s);							
						}
					}
					
					DetectForCollisions();
				}
				
				Thread.yield();
			} 
			catch(Exception e)
			{
				StackTraceElement[] ele = e.getStackTrace();
				
				String trace = "";
				for(StackTraceElement element : ele)
				{
					trace += element.getMethodName() + " " + element.getLineNumber() + System.getProperty("line.separator");					
				}
				
				Toast.makeText(m_context, e.getMessage() + trace, 5);
			}
			finally
			{
				if (canvas != null)
				{
					m_holder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}
	
	public void DetectForCollisions()
	{	
		LinkedList<Asteroid> astsToDelete = new LinkedList<Asteroid>();
		LinkedList<Projectile> projsToDelete = new LinkedList<Projectile>();
		
		LinkedList<Asteroid> asteroids = m_asteroids.GetAsteroids();
		LinkedList<Projectile> projectiles = m_shipMgr.GetProjectiles(); 
		
		// iteratre through projectiles first - if there aren't any there won't be any collisions
		for (Projectile p : projectiles) 
		{
			for (Asteroid a : asteroids) 	
			{
				if(a != null && p != null && p.XLocation >= a.XLocation && p.XLocation < a.XLocation + a.Width
					&& p.YLocation >= a.YLocation && p.YLocation < a.YLocation + a.Height)
				{
					// kaboom
					m_explosions.add(new ParticleSys(NUM_PARTICLES, p.XLocation, p.YLocation));
					m_score += a.Speed * 10;
					
					// destroy asteroid and particle
					astsToDelete.add(a);
					projsToDelete.add(p);
				}
			}		
		}	
		
		// delete the colliding asteroids/particles
		for(Asteroid a : astsToDelete)
			m_asteroids.DestroyAsteroid(a);
		
		for(Projectile p: projsToDelete)
			m_shipMgr.DestroyProjectile(p);
	}

	public void OnSensorChanged(SensorEvent event)
	{
		// update the x and y values of the ship
		// values[1]: Pitch, rotation around X axis (-180 to 180), with positive values when the z-axis moves toward the y-axis.
		// values[2]: Roll, rotation around Y axis (-90 to 90), with positive values when the x-axis moves toward the z-axis.
		m_shipMgr.UpdateLocation(m_context, event.values[1], event.values[2]);
	}
	
	public void OnTouchEvent()
	{
		m_shipMgr.Fire();
	}
}
