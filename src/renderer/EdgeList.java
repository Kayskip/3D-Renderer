package renderer;

import java.util.HashMap;
import java.util.Map;

public class EdgeList {
	
	private int startY;
	private int endY;
	public Map<Integer, Row> edges = new HashMap<Integer, Row>();

	public EdgeList(int startY, int endY) {
		this.startY = startY;
		this.endY = endY;
		for (int i = startY; i <= endY; i++) edges.put(i, new Row());
	}
	
	public int getStartY() {
		return startY;
	}

	public int getEndY() {
		return endY;
	}
	
	public float getLeftX(int i) {
		return (float) edges.get(i).leftX;
	}

	public float getRightX(int i) {
		return (float) edges.get(i).rightX;
	}

	public float getLeftZ(int i) {
		return (float) edges.get(i).leftZ;
	}

	public float getRightZ(int i) {
		return (float) edges.get(i).rightZ;
	}
	
	public void setRightZ(int i, float z){
		edges.get(i).rightZ = z;
	}
	
    public void setRightX(int i, float x){
    	edges.get(i).rightX = x;
	}
    
    public void setLeftZ(int i, float z){
    	edges.get(i).leftZ = z;
    }
    
    public void setLeftX(int i, float x){
    	edges.get(i).leftX = x;
    }
    
    public static class Row {
    	private float leftX;
    	private float rightX;
    	private float leftZ;
    	private float rightZ;
    	}
    
}

