package pbartz.examples.verlet;

public class Physics {
	
	public Bodies bodies;
	public Vector2D vGravity;
	public int fTimeStep = 1;
	
	public Physics(Bodies tBodies) {
		bodies = tBodies;
		vGravity = new Vector2D(0, 0.2);
	}

	public void UpdateForces() {
		for(int i = 0 ; i < bodies.count ; i++) {
			Body body = bodies.get(i);
			for(int j = 0 ; j < body.vertexCount ; j++) {
				body.vertex.get(j).acceleration = vGravity;
			}
			bodies.set(i, body);
		}
	}
	
	public void UpdateVerlet() {
		for(int i = 0 ; i < bodies.count ; i++) {
			Body body = bodies.get(i);
			for(int j = 0 ; j < body.vertexCount ; j++) {
				
				Vertex vertex = body.vertex.get(j);
				
				Vector2D x = vertex.position;
				Vector2D temp = x.clone();
				Vector2D temp2 = x.clone();
				
				Vector2D oldx = vertex.oldPosition;
				Vector2D a = vertex.acceleration;
				
				
				Vector2D tmp1 = x.subtract(oldx);
				Vector2D tmp2 = a.scale(fTimeStep  * fTimeStep);
				
				Vector2D tmp3 = tmp2.add(tmp1);
				Vector2D tmp4 = tmp3.add(temp2);
				
				vertex.oldPosition = temp;
				vertex.position = tmp4;
				
				body.vertex.set(j, vertex);
			}
			bodies.set(i, body);
		}
	}
	
	public void SatisfyConstraints() {
		for(int i = 0 ; i < bodies.count ; i++) {
			Body body = bodies.get(i);
			
			// Borders Restriction
			for(int j = 0 ; j < body.vertexCount ; j++) {				
				
				Vertex vertex = body.vertex.get(j);
				Vector2D vmax = vertex.position.max(new Vector2D(5,5));
				Vector2D vmin = vmax.min(new Vector2D(480 - 10, 800 - 10));
				vertex.position = vmin;
				body.vertex.set(j, vertex);

			}
			
			//Edges restriction
			
			for(int j = 0 ; j < body.edgeCount ; j++) {				
				Edge edge = body.edges.get(j);
				Vertex v1 = body.vertex.get(edge.v1Index);
				Vertex v2 = body.vertex.get(edge.v2Index);
				
				Vector2D delta = v2.position.subtract(v1.position);
				
				float deltalength = (float) Math.sqrt(delta.dotProduct(delta));
				float diff = (deltalength - edge.length) / deltalength;				
				
				Vector2D deltadif = delta.multiply(edge.elastic * diff);
				
				Vector2D x1 = v1.position.add(deltadif);
				Vector2D x2 = v2.position.subtract(deltadif);
				
				v1.position = x1;
				v2.position = x2;
				
				body.vertex.set(edge.v1Index, v1);
				body.vertex.set(edge.v2Index, v2);
			}
		}
	}
	
	public void Update() {
		UpdateForces();
		UpdateVerlet();
		SatisfyConstraints();
	}
	
	public void IterateCollisions() {
		
	}
	
	
}