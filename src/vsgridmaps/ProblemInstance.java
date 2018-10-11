package vsgridmaps;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.tools.ArrayUtils;

import static java.lang.Math.floor;

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

    public void solveLP() {
        Model model = new Model("whatever");

        int sum_of_weights = 0;
        for (WeightedPoint p: pointset) {
            sum_of_weights += p.getWeight();
        }

        IntVar[] i = model.intVarArray(pointset.size(), minx - sum_of_weights, maxx + sum_of_weights);
        IntVar[] j = model.intVarArray(pointset.size(), miny - sum_of_weights, maxy + sum_of_weights);

        // Set solver

        // Loop every pair of points
        for (int k=0; k<pointset.size(); k++) {
            // Add non-overlap constraints
            WeightedPoint pk = pointset.get(k);
            for (int h=k+1; h<pointset.size(); h++) {

                WeightedPoint ph = pointset.get(h);
                int half_k_size = pk.getWeight()/2;
                int half_h_size = ph.getWeight()/2;

                if (pk.getWeight() % 2 != 0) { half_k_size += 1; }
                if (ph.getWeight() % 2 != 0) { half_h_size += 1; }

                System.out.println("Pair " + k + ", " + h);

                //model.arithm(i[k], "!=", i[h]).post();
                //model.arithm(j[k], "!=", j[h]).post();


                // ik + sk/2 ≤ ih - sh/2
                // ik ≤ ih -sk/2 -sh/2
                BoolVar c1 = model.arithm(
                        i[k],
                        "<=",
                        i[h],
                        "+",
                        -half_k_size -half_h_size
                ).reify();

                // ik - sk/2 ≥ ih + sh/2
                // ik ≥ ih +sk/2 -sh/2
                BoolVar c2 = model.arithm(
                        i[k],
                        ">=",
                        i[h],
                        "+",
                        +half_k_size +half_h_size
                ).reify();

                // jk + sk/2 ≤ jh - sh/2
                // jk ≤ jh -sk/2 -sh/2
                BoolVar c3 = model.arithm(
                        j[k],
                        "<=",
                        j[h],
                        "+",
                        -half_k_size -half_h_size
                ).reify();

                // jk - sk/2 ≥ jh + sh/2
                // jk ≥ jh +sk/2 -sh/2
                BoolVar c4 = model.arithm(
                        j[k],
                        ">=",
                        j[h],
                        "+",
                        +half_k_size +half_h_size
                ).reify();

                model.or(c1, c2, c3, c4).post();

            }

        }

        // Minimization goal

        // This here needs the choco-ibex module. I haven't been able to install that yet
        // http://www.ibex-lib.org/doc/java-install.html
        // Maybe we can try some other goal, this seemed to be the only one accepting real numbers
        // but I'm not entirely sure.

        RealVar goal = model.realVar("goal", 0, 100000, 0.01);
        StringBuilder ibexconstraint = new StringBuilder();
        for (int k=0; k<pointset.size(); k++) {
            WeightedPoint p = pointset.get(k);
            String extra_delta = "";
            if (p.getWeight() % 2 != 0) {
                extra_delta = "-0.5";
            }
            ibexconstraint.append(String.format(
                    "sqrt( (%f - {%d} %s)^2 + (%f - {%d} %s)^2 )",
                    p.getX(), k+1, extra_delta,
                    p.getY(), pointset.size()+k+1, extra_delta
            ));
            if (k != pointset.size() - 1){
                ibexconstraint.append(" + ");
            }
        }
        ibexconstraint.append(" = {0}");
        System.out.println(ibexconstraint);

        Variable[] goal_list = new Variable[] {goal};
        Variable[] arguments = ArrayUtils.append(goal_list, i, j);
        model.ibex(String.valueOf(ibexconstraint), arguments).post();

        model.setObjective(Model.MINIMIZE, goal);

        // Solve
        model.getSolver().solve();

        System.out.println("Solution");

        // Set solution back to the problem
        for (int k=0; k<pointset.size(); k++) {
            System.out.println("Point" + k);
            System.out.println(i[k]);
            System.out.println(j[k]);
            WeightedPoint p = pointset.get(k);
            double delta = 0;
            if (p.getWeight() % 2 != 0) {
                delta = -0.5;
            }
            p.setAssigned_x(i[k].getValue() + delta);
            p.setAssigned_y(j[k].getValue() + delta);
        }
    }
}
