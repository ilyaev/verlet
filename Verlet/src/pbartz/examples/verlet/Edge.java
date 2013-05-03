package pbartz.examples.verlet;

import java.util.Random;

public class Edge {
	
	public int v1Index = -1;
	public int v2Index = -1;
	
	public Body body;
	public float length = 100;
	public double elastic = 0.5;
	
	public Edge(Body tBody, int v1, int v2) {
		body = tBody;
		v1Index = v1;
		v2Index = v2;
		
		//Random r = new Random();
		//length = r.nextInt(80) + 50;
		
	}
	
}