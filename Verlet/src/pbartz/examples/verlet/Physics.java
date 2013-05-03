package pbartz.examples.verlet;

import android.util.Log;

public class Physics {
	
	public Bodies bodies;
	public Vector2D vGravity;
	public int fTimeStep = 1;
	
	float collisionDepth = 0;
	Vector2D collisionNormal = null;
	Edge collisionEdge = null;
	Body collisionBody = null;
	Vertex collisionVertex = null;
	private Body collisionVertexBody;
	private int collisionVertexIndex;
	private int collisionVertexBodyIndex;
	private int collisionBodyIndex;
	
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
	
	public boolean DetectCollision(int bIndex1, int bIndex2) {
		Body b1 = bodies.get(bIndex1);
		Body b2 = bodies.get(bIndex2);
		Body b = null;		
		
		float minDistance = 10000;
		
		for(int i = 0 ; i < (b1.edgeCount + b2.edgeCount) ; i++) {
			
			Edge e;
			
			if (i < b1.edgeCount) {
				e = b1.edges.get(i);
				b = b1;
				collisionBodyIndex = bIndex1;
			} else {
				e = b2.edges.get(i - b1.edgeCount);
				b = b2;
				collisionBodyIndex = bIndex2;
			}
			
			Vector2D axis = new Vector2D(
					b.vertex.get(e.v1Index).position.y - b.vertex.get(e.v2Index).position.y,
					b.vertex.get(e.v1Index).position.x - b.vertex.get(e.v2Index).position.x
			);
			
			axis.normalizeThis();
			
			float minA, minB, maxA, maxB;
			
			float p1[] = b1.projectToAxis(axis);
			minA = p1[0];
			maxA = p1[1];
			
			float p2[] = b2.projectToAxis(axis);
			minB = p2[0];
			maxB = p2[1];
			
			float distance = IntervalDistance(minA, maxA, minB, maxB);
			
			if (distance > 0) {
				return false;
			} else if (Math.abs(distance)  < minDistance) {
				minDistance = Math.abs(distance);
				collisionNormal = axis;
				collisionEdge = e;
				collisionBody = b;
			}			
		}
		
		collisionDepth = minDistance;
		
		if (collisionBodyIndex != bIndex2) {
			Body temp = b2;
			b2 = b1;
			b1 = temp;
		}
		
		Vector2D center1 = b1.center();
		Vector2D center2 = b2.center();
		
		int sign = (int)Math.signum(collisionNormal.dot(center1.subtract(center2)));
		
		if (sign != 1) {
			collisionNormal.scale(-1);
		}
		
		float smallestD = 10000;
		
		for(int i = 0 ; i < b1.vertexCount ; i++) {
			float distance = (float)collisionNormal.dot(b1.vertex.get(i).position.subtract(center2));
			if(distance < smallestD) {
				smallestD = distance;
				collisionVertex = b1.vertex.get(i);
				collisionVertexBodyIndex = bIndex1;
				collisionVertexIndex = i;
			}
		}
		
		return true;
	}
	
	public void processCollision() {
		Vector2D collisionVector = collisionNormal.multiply(collisionDepth);
		
		collisionVertex.position.addThis(collisionVector.multiply(0.5));
		bodies.get(collisionVertexBodyIndex).vertex.set(collisionVertexIndex, collisionVertex);
		
		
		
//		Vertex e1 = collisionBody.vertex.get(collisionEdge.v1Index);
//		Vertex e2 = collisionBody.vertex.get(collisionEdge.v2Index);
//		
//		float t;
//		
//		if (Math.abs(e1.position.x - e2.position.x) > Math.abs(e1.position.y - e2.position.y)) {
//			t = (float)((collisionVertex.position.x - collisionVector.x - e1.position.x) / (e2.position.x - e1.position.x) ); 
//		} else {
//			t = (float)((collisionVertex.position.y - collisionVector.y - e1.position.y) / (e2.position.y - e1.position.y) );
//		}
//		
//		float lambda = 1 / (t*t + (1-t) * (1-t));
//		
//		e1.position.sub(collisionVector.multiply((1-t)*0.5*lambda));
//		e2.position.sub(collisionVector.multiply(t*0.5*lambda));
//		
//		collisionVertex.position.addThis(collisionVector.multiply(0.5));
//		
//		bodies.get(collisionVertexBodyIndex).vertex.set(collisionVertexIndex, collisionVertex);
//		bodies.get(collisionBodyIndex).vertex.set(collisionEdge.v1Index, e1);
//		bodies.get(collisionBodyIndex).vertex.set(collisionEdge.v2Index, e2);
	}
	
	public float IntervalDistance(float minA, float maxA, float minB, float maxB) {
		if (minA < minB) {
			return minB - maxA;
		}
		return minA - maxB;
	}
	
	public void IterateCollisions() {
		if (bodies.count > 1) {			
			for(int i = 0 ; i < bodies.count ; i++) {
				for(int j = 0 ; j < bodies.count ; j++) {
					if (j != i) {
						if (DetectCollision(i, j)) {
							processCollision();
						}
					}
				}				
			}
		}
	}
	
	public void Update() {
		UpdateForces();
		UpdateVerlet();
		IterateCollisions();
		SatisfyConstraints();
	}
	
	
}