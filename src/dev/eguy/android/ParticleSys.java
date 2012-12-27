package dev.eguy.android;

import java.util.LinkedList;
import java.util.Random;

public class ParticleSys
{
	enum Direction { N, NE, E, SE, S, SW, W, NW };

	class Particle
	{
		public int XVal;
		public int YVal;
		public Direction Direction;
	}
	
	private final int NUM_PARTICLES = 100;
	private int m_timeToLive = 25;
	private LinkedList<Particle> m_particles = new LinkedList<Particle>();
	
	public ParticleSys(int x, int y)
	{
		Direction[] directions = Direction.values();
		int numDirections = directions.length;
		int currentDir = 0;
		int currentDirIndex = 0;
		int numParticlesPerDirection = NUM_PARTICLES / numDirections;
		
		for (int i = 0; i < NUM_PARTICLES; i++)
		{
			Particle newParticle = new Particle();
			newParticle.XVal = x;
			newParticle.YVal = y;
			newParticle.Direction = directions[currentDirIndex];
			m_particles.add(newParticle);

			currentDir++;

			if (currentDir >= numParticlesPerDirection)
			{
				currentDir = 0;
				currentDirIndex++;
				
				if (currentDirIndex >= numDirections)
				{
					// wrap back to 0 so we're not out of range
					currentDirIndex = 0;
				}
			}
		}
	}
	
	private int GetDistanceToMove(Random ran)
	{
		 return 1 + ran.nextInt(3);
	}
	
	public void MoveParticles()
	{
		Random ran = new Random();
		
		for(int i = 0; i < m_particles.size(); i++)
		{
			Particle p = m_particles.get(i);
			
			if(p.Direction == Direction.N || p.Direction == Direction.NE || p.Direction == Direction.NW)
			{
				p.XVal += GetDistanceToMove(ran);
			}
			if(p.Direction == Direction.S || p.Direction == Direction.SW || p.Direction == Direction.SE)
			{
				p.XVal -= GetDistanceToMove(ran);
			}
			if(p.Direction == Direction.E || p.Direction == Direction.NE || p.Direction == Direction.SE)
			{
				p.YVal += GetDistanceToMove(ran);;
			}
			if(p.Direction == Direction.W || p.Direction == Direction.NW || p.Direction == Direction.SW)
			{
				p.YVal -= GetDistanceToMove(ran);;
			}
		}
		
		m_timeToLive--;
		if(m_timeToLive <= 0)
		{
			m_particles.clear();
		}
	}
	
	public LinkedList<Particle> GetParticles()
	{
		return m_particles;
	}
}
