package pbartz.examples.verlet;

import java.util.Random;
import android.graphics.Canvas;

public class VerletTest {

	Bodies bodies;
	Renderer renderer;
	Physics physics;
	VerletSurface surface;
	private long lastTimeStamp = 0;
	
	public VerletTest(VerletSurface tSurface) {
		surface = tSurface;
		initialize();
	}
	
	private void initialize() {
		bodies = new Bodies();
		renderer = new Renderer(bodies);
		physics = new Physics(bodies);
		
		Body body = new Body();
		
		Random r = new Random();
		
		for(int i = 0 ; i < 3 ; i++) {
			Vector2D x = new Vector2D(r.nextDouble() * 480, r.nextDouble() * 800);
			Vector2D ox = new Vector2D(r.nextDouble() * 480, r.nextDouble() * 800);
			Vector2D a = new Vector2D(0, 0.2);
			
			Vertex v = new Vertex(x, ox, a);
			body.addVertex(v);
			
			int next = i + 1;
			if (next >= 3) {
				next = 0;
			}
			
			body.addEdge(new Edge(body, i, next));
		}
		
		bodies.add(body);
	}

	public void draw(Canvas canvas) {
		renderer.draw(canvas);		
	}

	public void TimeStep() {
		physics.Update();
		
		long timeDiff = System.currentTimeMillis() - lastTimeStamp ;
		
		if (timeDiff > 500) {			
			physics.vGravity.x = surface.mOrientation[1] / 5; 
			physics.vGravity.y = surface.mOrientation[2] / 5;
			lastTimeStamp += timeDiff;		
		}
	}
	
	
	public void rebuild(float startX, float startY, float endX, float endY) {
		float dx = (endX - startX) / 10;
		float dy = (endY - startY) / 10;
		
		Body body = new Body();
		
		body.addVertex(new Vertex(new Vector2D(startX, startY), new Vector2D(startX - dx, startY - dy), new Vector2D(0, 0.2)));
		body.addVertex(new Vertex(new Vector2D(endX, endY), new Vector2D(endX - dx, endY - dy), new Vector2D(0, 0.2)));
		body.addVertex(new Vertex(new Vector2D(startX + ((endX - startX) / 2), endY + ((endY - startY) / 2)), new Vector2D(startX + ((endX - startX) / 2) - dx, endY + ((endY - startY) / 2) - dy), new Vector2D(0, 0.2)));
		
		body.addEdge(new Edge(body, 0, 1));
		body.addEdge(new Edge(body, 1, 2));
		body.addEdge(new Edge(body, 2, 0));

		bodies.add(body);
	}
	
	
}