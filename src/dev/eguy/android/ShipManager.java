package dev.eguy.android;

import java.util.LinkedList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class ShipManager
{
	class Projectile
	{
		public float XLocation, YLocation;
	}

	private static final int ANGLE_SMALL = 1;
	private static final int ANGLE_LARGE = 179;
	private static final float SCALE_SHIP_SPEED = 4.0f;
	
	private float m_maxX = 0, m_maxY = 0;
	private float m_shipX = 0, m_shipY = 0;
	
	private Bitmap m_shipBitmap = null, m_projectile = null;
	private LinkedList<Projectile> m_projectiles = new LinkedList<Projectile>();
	private LinkedList<Projectile> m_newProjectiles = new LinkedList<Projectile>();
		
	public ShipManager(Resources r, int maxX, int maxY)
	{
		m_maxX = maxX;
		m_maxY = maxY;
		m_shipX = 30;
		m_shipY = maxY / 2;
		m_projectile = BitmapFactory.decodeResource(r, R.drawable.bullet);
		m_shipBitmap = BitmapFactory.decodeResource(r, R.drawable.firepowergrey);
	}
		
	public void DrawAndUpdate(Canvas c)
	{
		// draw the ship
		c.drawBitmap(m_shipBitmap, m_shipX, m_shipY, null);

		// draw the projectiles (missiles fired by ship)
		for (int i = 0; i < m_projectiles.size(); i++)
		{
			Projectile prj = m_projectiles.get(i);
			c.drawBitmap(m_projectile, prj.XLocation, prj.YLocation, null);
			
			prj.XLocation += 5;
	
			// if off screen, destroy
			if (prj.XLocation > c.getWidth())
			{
				DestroyProjectile(prj);
			}
		}
		
		AddNewProjectiles();
	}
	
	public void UpdateLocation(Context context, float x, float y)
	{	
		// scale the speed of the ships x/y movement
		if (x < - ANGLE_SMALL && x > -ANGLE_LARGE && m_shipX < m_maxX - m_shipBitmap.getWidth())
		{ 
			// we know x is going to be -ve but we want a +ve number 
			m_shipX += (x / - SCALE_SHIP_SPEED);
        }
		else if (x > ANGLE_SMALL && x < ANGLE_LARGE && m_shipX > 0) 
		{
        	m_shipX -= (x / SCALE_SHIP_SPEED);
        } 
		
		if (y > ANGLE_SMALL && m_shipY < m_maxY - m_shipBitmap.getHeight()) 
		{
        	m_shipY += (y / SCALE_SHIP_SPEED);   
        } 
		else if (y < -ANGLE_SMALL && m_shipY > 0) 
		{
			// we know y is going to be -ve but we want a +ve number to subtract from the ship's position
        	m_shipY -= (y / - SCALE_SHIP_SPEED);
        }
	}
	
	public void Fire()
	{
		// called based on touch event so add to 'new projectiles collection'
		// which will be added to the main m_projectiles LinkedList
		// in the main game loop
		Projectile pj = new Projectile();
		pj.XLocation = m_shipX + m_shipBitmap.getWidth();
		pj.YLocation = (int) (m_shipY + 0.5 * m_shipBitmap.getHeight());

		m_newProjectiles.add(pj);
	}
	
	private void AddNewProjectiles()
	{
		// add them all to the main list of projectiles
		m_projectiles.addAll(m_newProjectiles);
		
		// clear for next time
		m_newProjectiles.clear();
	}
	
	public LinkedList<Projectile> GetProjectiles()
	{
		return m_projectiles;
	}

	public void DestroyProjectile(Projectile p)
	{
		m_projectiles.remove(p);	
	}
}
