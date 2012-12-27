package dev.eguy.android;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class ParticleSys
{
	// particles in the explosion
	private Particle[] m_particles;						
	private int m_numDeadParticles = 0;
	
	public ParticleSys(int particleNr, float xLocation, float yLocation) 
	{
		this.m_particles = new Particle[particleNr];
		int colour = Color.argb(255, GlobalHelpers.GetNextRand(255), GlobalHelpers.GetNextRand(255), GlobalHelpers.GetNextRand(255));
	 	for (int i = 0; i < this.m_particles.length; i++) 
	 	{
			Particle p = new Particle(xLocation, yLocation, colour);
			this.m_particles[i] = p;
		}
	}
	
	public void DrawAndUpdateParticles(Canvas canvas)
	{
		for (ParticleSys.Particle x : this.m_particles)
		{
			x.Draw(canvas);
			if (x.Update())
				m_numDeadParticles++;
		}
	}
	
	// are all the particles dead
	public boolean IsDeceased()
	{
		return m_numDeadParticles >= m_particles.length;
	}

	enum State { Alive, Dead };
	
	public class Particle 
	{	
		private static final int DEFAULT_LIFETIME 	= 150;
		
		// amount to fade by
		private static final int NUM_FADE = 10;
		
		// max width/height
		private static final int MAX_DIMENSION		= 3;
		
		// maximum speed (per update)
		private static final int MAX_SPEED			= 4;	

		private State m_state;		// particle is alive or dead
		private float m_width;		// width of the particle
		private float m_height;		// height of the particle
		private float m_xLoc, m_yLoc;			// horizontal and vertical position
		private double m_xVel, m_yVel;		// vertical and horizontal velocity
		private int m_age;			// current age of the particle
		private int m_lifetime;		// particle dies when it reaches this value
		private int m_colour;			// the colour of the particle
		private Paint m_paint;		// internal use to avoid instantiation
		
		public Particle(float xLocation, float yLocation, int colour) 
		{		
			this.m_xLoc = xLocation;
			this.m_yLoc = yLocation;
			this.m_state = State.Alive;
			this.m_width = GlobalHelpers.GetNextRand(MAX_DIMENSION) + 1;
			this.m_height = this.m_width;
			this.m_lifetime = DEFAULT_LIFETIME;
			this.m_age = 0;
			this.m_xVel= GlobalHelpers.GetNextRandDbl(MAX_SPEED * 2) - MAX_SPEED;
			this.m_yVel = GlobalHelpers.GetNextRandDbl(MAX_SPEED * 2) - MAX_SPEED;
			
			// smoothing out the diagonal speed
			if (m_xVel * m_xVel + m_yVel * m_yVel > MAX_SPEED * MAX_SPEED) 
			{
				m_xVel *= 0.7;
				m_yVel *= 0.7;
			}
			this.m_colour = colour;
			this.m_paint = new Paint(this.m_colour);
			this.m_paint.setColor(this.m_colour);
		}

		public boolean Update() 
		{
			if (this.m_state != State.Dead) 
			{
				this.m_xLoc += this.m_xVel;
				this.m_yLoc += this.m_yVel;

				// extract alpha
				int a = this.m_colour >>> 24;
				a -= NUM_FADE;
				
				if (a <= 0) 
				{ 
					// if reached transparency kill the particle
					this.m_state = State.Dead;
					return true;
				} 
				else 
				{
					// set the new alpha
					this.m_colour = (this.m_colour & 0x00ffffff) + (a << 24);		
					this.m_paint.setAlpha(a);
					
					// increase the age of the particle
					this.m_age++; 
				}
				// reached the end if its life
				if (this.m_age >= this.m_lifetime) 
				{	
					this.m_state = State.Dead;
					return true;
				}
			}
			
			return false;
		}

		public void Draw(Canvas canvas) 
		{
			canvas.drawRect(this.m_xLoc, this.m_yLoc, this.m_xLoc + this.m_width, this.m_yLoc + this.m_height, m_paint);
		}
	}
}
