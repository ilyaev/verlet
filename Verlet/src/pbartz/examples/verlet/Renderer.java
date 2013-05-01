package pbartz.examples.verlet;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class Renderer {
	
	Bodies bodies;
	
	Paint dotPaint;
	Paint linePaint;
	
	float dotRadius = 3;
	
	public Renderer(Bodies tBodies) {
		bodies = tBodies;
		
		dotPaint = new Paint();
		dotPaint.setARGB(255, 255, 0, 0);
		
		linePaint = new Paint();
		linePaint.setARGB(255, 119, 221, 119);
	}
	
	public void draw(Canvas canvas) {
		
		Path path = new Path();
		
		for(int i = 0 ; i < bodies.count ; i++) {
			Body body = bodies.get(i);
			
			path.reset();
			for(int j = 0 ; j < body.vertexCount ; j++) {
				Vertex vertex = body.vertex.get(j);
				if (j == 0) {
					path.moveTo((float)vertex.position.x, (float)vertex.position.y);
				} else {
					path.lineTo((float)vertex.position.x, (float)vertex.position.y);
				}
			}
			path.close();
			
			canvas.drawPath(path, linePaint);
			
			for(int j = 0 ; j < body.vertexCount ; j++) {
				Vertex vertex = body.vertex.get(j);
				canvas.drawCircle((float)vertex.position.x, (float)vertex.position.y, dotRadius, dotPaint);
			}
		}
	}
	
}
