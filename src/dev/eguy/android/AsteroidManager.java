package dev.eguy.android;

import java.util.LinkedList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class AsteroidManager
{
	class Asteroid
	{
		public int XLocation;
		public int YLocation;
		public int Speed;
		public int Width, Height;
	}
	
	private Bitmap m_asteroid = null;
	private final int MIN_NUM_ASTEROIDS = 10; // min number of asteroids on screen (ish)
	private LinkedList<Asteroid> m_asteroids = new LinkedList<Asteroid>();
	
	public AsteroidManager(Resources resource)
	{
		m_asteroid = BitmapFactory.decodeResource(resource, R.drawable.asteroid);
	}
	
	public void DrawAndUpdate(Canvas canvas)
	{
		// see if we want to draw any new asteroids
		if(m_asteroids.size() < MIN_NUM_ASTEROIDS || GlobalHelpers.GetNextRand(10) == 0)
		{
			Asteroid a = new Asteroid();
			a.Width = m_asteroid.getWidth();
			a.Height = m_asteroid.getHeight();
			a.XLocation = 250 + GlobalHelpers.GetNextRand(canvas.getWidth());
			a.YLocation = GlobalHelpers.GetNextRand(canvas.getHeight());
			a.Speed = 1 + GlobalHelpers.GetNextRand(2);
			m_asteroids.add(a);
		}
		
		// draw asteroids
		for(int i = 0; i < m_asteroids.size(); i++)
		{
			Asteroid a = m_asteroids.get(i);
			canvas.drawBitmap(m_asteroid, a.XLocation, a.YLocation, null);
			
			a.XLocation -= a.Speed;
			
			// once the asteroid is well off the screen
			if (a.XLocation < -10 || a.YLocation < -10 || a.XLocation > canvas.getWidth() || a.YLocation > canvas.getHeight())
			{
				m_asteroids.remove(i);
			}
		}
	}

	public LinkedList<Asteroid> GetAsteroids()
	{
		return m_asteroids;
	}

	public void DestroyAsteroid(Asteroid a)
	{
		m_asteroids.remove(a);
	}
}
