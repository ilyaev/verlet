package pbartz.examples.verlet;

public class Vertex {

	public Vector2D position;
	public Vector2D oldPosition;
	public Vector2D acceleration;
	
	
	public Vertex(Vector2D tPosition, Vector2D tOldPosition, Vector2D tAcceleration) {
		position = tPosition;
		oldPosition = tOldPosition;
		acceleration = tAcceleration;
	}
	
}