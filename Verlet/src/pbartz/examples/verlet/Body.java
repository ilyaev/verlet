package pbartz.examples.verlet;

import java.util.ArrayList;
import java.util.List;

public class Body {

	public List<Vertex> vertex;
	public int vertexCount = 0;
	public int edgeCount = 0;
	public List<Edge> edges;
	
	public Body() {
		vertex = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
	}
	
	public void addVertex(Vertex v) {
		vertex.add(v);
		vertexCount += 1;
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
		edgeCount += 1;
	}
	
	public Vector2D center() {
		
		if (vertexCount == 0) {
			return new Vector2D(0,0);
		}
		
		double sumX = 0;
		double sumY = 0;
		
		for(int i = 0 ; i < vertexCount ; i++) {
			sumX += vertex.get(i).position.x;
			sumY += vertex.get(i).position.y;
		}
		
		return new Vector2D(sumX / vertexCount, sumY / vertexCount);
	}
	
	public float[] projectToAxis(Vector2D axis) {
		float DotP = (float)axis.dotProduct(vertex.get(0).position);
		
		float Min = DotP;
		float Max = DotP;
		
		for(int i = 1 ; i < vertexCount ; i++) {
			DotP = (float)axis.dotProduct(vertex.get(i).position);
			
			Min = Math.min(DotP, Min);
			Max = Math.max(DotP, Max);
		}
		
		float result[] = {Min, Max};
		return result;
	}
	
}
