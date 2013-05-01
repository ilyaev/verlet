package pbartz.examples.verlet;

public class Edge {
	
	public int v1Index = -1;
	public int v2Index = -1;
	
	public Body body;
	public float length = 100;
	public double elastic = 0.09;
	
	public Edge(Body tBody, int v1, int v2) {
		body = tBody;
		v1Index = v1;
		v2Index = v2;
	}
	
}
