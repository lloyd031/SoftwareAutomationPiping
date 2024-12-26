package application;

public class PathNode{
	private double x,y,z;
	private PathNode parent;
	public PathNode(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
	}
	
	public double getX() {
		return this.x;
	}
	public double getY() {
		return this.y;
	}
	public double getZ() {
		return this.z;
	}
	public void setParent(PathNode parent) {
		this.parent=parent;
	}
	public PathNode getParent() {
		return this.parent;
	}
}
