package pbartz.examples.verlet;

import java.util.ArrayList;
import java.util.List;

public class Bodies {
	
	public List<Body> items;
	public int count = 0;
	public int maxCount = 10;
	
	public Bodies() {
		items = new ArrayList<Body>();
	}
	
	public Body get(int i) {
		return items.get(i);
	}
	
	public void set(int i, Body b) {
		items.set(i, b);
	}
	
	public void add(Body b) {
		items.add(b);
		count += 1;
		if (count > maxCount) {
			count = maxCount;
			items.remove(0);
		}
	}
	
}
