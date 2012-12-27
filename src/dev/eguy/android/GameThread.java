package dev.eguy.android;

import java.util.LinkedList;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class GameThread extends Thread
{
	class Projectile
	{
		public int XLocation;
		public int YLocation;
	}

	class Asteroid
	{
		public int XLocation;
		public int YLocation;
		public int Speed;
	}

	private Resources m_resource = null;

	private int m_shipX = 30, m_shipY = 30;
	private Bitmap m_shipBitmap = null, m_projectile = null, m_asteroid = null, m_particle = null;
	private boolean m_gameRunning = false;
	private SurfaceHolder m_holder = null;
	private Context m_context = null;
	private LinkedList<Projectile> m_projectiles = new LinkedList<Projectile>();
	private LinkedList<Asteroid> m_asteroids = new LinkedList<Asteroid>();

	public GameThread(Context context, SurfaceHolder holder)
	{
		m_holder = holder;
		m_context = context;
		m_resource = context.getResources();

		m_shipBitmap = BitmapFactory.decodeResource(m_resource, R.drawable.ship);
		m_projectile = BitmapFactory.decodeResource(m_resource, R.drawable.pewpew);
		m_asteroid = BitmapFactory.decodeResource(m_resource,R.drawable.asteroid);
		m_particle = BitmapFactory.decodeResource(m_resource,R.drawable.particle);
	}

	public void SetRunningState(boolean state)
	{
		m_gameRunning = state;
	}

	public void Fire()
	{
		Projectile pj = new Projectile();
		pj.XLocation = m_shipX + m_shipBitmap.getWidth();
		pj.YLocation = (int) (m_shipY + 0.5 * m_shipBitmap.getHeight());

		m_projectiles.add(pj);
	}
	
	private static int GetRandomNumber(int max)
	{
		Random ran = new Random();
		return ran.nextInt(max);
	}

	public void run()
	{
		Canvas canvas = null;

		if (m_shipBitmap == null || m_projectile == null || m_asteroid == null || m_particle == null)
		{
			// not a lot we can do with noting to draw
			return;
		}

		ParticleSys pt = new ParticleSys(150, 150);
		
		while (m_gameRunning)
		{
			try
			{
				canvas = m_holder.lockCanvas(null);

				// draw background
				canvas.drawARGB(255, 0, 0, 0);

				// draw the ship
				canvas.drawBitmap(m_shipBitmap, m_shipX, m_shipY, null);

				// draw the projectiles (missiles fired by ship)
				for (int i = 0; i < m_projectiles.size(); i++)
				{
					Projectile prj = m_projectiles.get(i);
					canvas.drawBitmap(m_projectile, prj.XLocation, prj.YLocation, null);
				}

				// update projectiles (as they are moving_
				for (int i = 0; i < m_projectiles.size(); i++)
				{
					Projectile prj = m_projectiles.get(i);
					prj.XLocation += 5;

					// if off screen, destroy
					if (prj.XLocation > canvas.getWidth())
					{
						m_projectiles.remove(i);
					}
				}

				// see if we want to draw any new asteroids
				if(m_asteroids.size() < 7 || GetRandomNumber(10) == 0)
				{
					Asteroid a = new Asteroid();
					a.XLocation = 250 + GetRandomNumber(canvas.getWidth());
					a.YLocation = GetRandomNumber(canvas.getHeight());
					a.Speed = 1 + GetRandomNumber(2);
					m_asteroids.add(a);
				}

				// draw asteroids
				for(int i = 0; i < m_asteroids.size(); i++)
				{
					Asteroid a = m_asteroids.get(i);
					canvas.drawBitmap(m_asteroid, a.XLocation, a.YLocation, null);
				}
				
				// update asteroids
				for(int i = 0; i < m_asteroids.size(); i++)
				{
					Asteroid a = m_asteroids.get(i);
					a.XLocation -= a.Speed;
					
					// once the asteroid is well off the screen
					if (a.XLocation < -10 || a.YLocation < -10 || a.XLocation > canvas.getWidth() || a.YLocation > canvas.getHeight())
					{
						m_asteroids.remove(i);
					}
				}
				
				// draw particles
				for (ParticleSys.Particle x : pt.GetParticles())
				{
					canvas.drawBitmap(m_particle, x.XVal, x.YVal, null);
				}
				
				// move for nxt time
				pt.MoveParticles();
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
}
