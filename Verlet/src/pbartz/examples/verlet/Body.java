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
	
}
