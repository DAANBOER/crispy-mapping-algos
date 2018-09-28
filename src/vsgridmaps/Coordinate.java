package vsgridmaps;

public class Coordinate {
	private double x,y;
	
	public Coordinate(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public void set(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}

}
