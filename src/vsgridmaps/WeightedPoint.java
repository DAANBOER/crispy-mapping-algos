package vsgridmaps;

public class WeightedPoint {

    private double x, y;
    private int weight;
    private double assigned_x, assigned_y;
    private int index;

    public WeightedPoint(double x, double y, int weight, int index) {
        this.x = x;
        this.y = y;
        this.weight = weight;
        this.index = index;
        this.assigned_x = Double.NaN;
        this.assigned_y = Double.NaN;
    }

    public void set(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setAssigned(double assigned_x, double assigned_y) {
        this.assigned_x = assigned_x;
        this.assigned_y = assigned_y;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public int getIndex() {
    	return index;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getAssigned_x() {
        return assigned_x;
    }

    public void setAssigned_x(double assigned_x) {
        this.assigned_x = assigned_x;
    }

    public double getAssigned_y() {
        return assigned_y;
    }

    public void setAssigned_y(double assigned_y) {
        this.assigned_y = assigned_y;
    }

    public static boolean areDisjoint(WeightedPoint a, WeightedPoint b) {
        double dx = Math.abs(a.getAssigned_x() - b.getAssigned_x());
        double dy = Math.abs(a.getAssigned_y() - b.getAssigned_y());
        return Math.max(dx, dy) > (a.getWeight() + b.getWeight()) / 2.0 - 0.1;
    }
    

}
