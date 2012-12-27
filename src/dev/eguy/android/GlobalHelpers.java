package dev.eguy.android;

import java.util.Random;

public class GlobalHelpers
{
	public enum RunningState { Running, Stopped, Paused };
	static Random r = new Random();
	
	public static int GetNextRand(int max)
	{
		return r.nextInt(max);
	}
	
	public static double GetNextRandDbl(int max)
	{
		return r.nextDouble() * max;
	}
}
