package vsgridmaps;

public class ProblemInstance {

    private int instancenumber = -1;
    private WeightedPointSet pointset;
    private int minx, maxx, miny, maxy;

    public int getMinx() {
        return minx;
    }

    public void setMinx(int minx) {
        this.minx = minx;
    }

    public int getMaxx() {
        return maxx;
    }

    public void setMaxx(int maxx) {
        this.maxx = maxx;
    }

    public int getMiny() {
        return miny;
    }

    public void setMiny(int miny) {
        this.miny = miny;
    }

    public int getMaxy() {
        return maxy;
    }

    public void setMaxy(int maxy) {
        this.maxy = maxy;
    }

    public int getInstancenumber() {
        return instancenumber;
    }

    public void setInstancenumber(int instancenumber) {
        this.instancenumber = instancenumber;
    }

    public WeightedPointSet getPointset() {
        return pointset;
    }

    public void setPointset(WeightedPointSet pointset) {
        this.pointset = pointset;
    }
    
    public double computeScore() {
        double sum = 0;
        for (WeightedPoint wp : pointset) {
            double dx = wp.getX() - wp.getAssigned_x();
            double dy = wp.getY() - wp.getAssigned_y();
            sum += dx * dx + dy * dy;
        }
        return sum;
    }

    public boolean isValid() {

        for (int i = 0; i < pointset.size(); i++) {
            WeightedPoint wp = pointset.get(i);

            double dx, dy;

            if (wp.getWeight() % 2 == 0) {
                // should have integer coords
                dx = Math.round(wp.getAssigned_x()) - wp.getAssigned_x();
                dy = Math.round(wp.getAssigned_y()) - wp.getAssigned_y();
            } else {
                // should have integer coords plus a half
                dx = Math.round(wp.getAssigned_x() - 0.5) - (wp.getAssigned_x() - 0.5);
                dy = Math.round(wp.getAssigned_y() - 0.5) - (wp.getAssigned_y() - 0.5);
            }

            if (Math.abs(dx) >= 0.01) {
                return false;
            }
            if (Math.abs(dy) >= 0.01) {
                return false;
            }

            for (int j = 0; j < i; j++) {
                WeightedPoint wp2 = pointset.get(j);
                if (!WeightedPoint.areDisjoint(wp, wp2)) {
                    return false;
                }
            }
        }
        return true;
    }
}
