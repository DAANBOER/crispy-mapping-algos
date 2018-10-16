package vsgridmaps;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.ESat;
import org.chocosolver.util.tools.ArrayUtils;

public class ProblemInstance {

    private static final String TIME_LIMIT = "120s";
    public static final double PRECISION = 0.01;
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

            if (Math.abs(dx) >= PRECISION) {
                return false;
            }
            if (Math.abs(dy) >= PRECISION) {
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

    public boolean solveLP() {
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

                int offset_k = 0;
                int offset_h = 0;

                if (pk.getWeight() % 2 != 0) { offset_k = 1; }
                if (ph.getWeight() % 2 != 0) { offset_h = 1; }



                // ik - ok + sk/2 ≤ ih -oh - sh/2
                // ik ≤ ih -sk/2 -sh/2 +ok -oh
                Constraint c1 = model.arithm(
                        i[k],
                        "<=",
                        i[h],
                        "+",
                        -half_k_size -half_h_size - offset_h
                );


                // ik -ok - sk/2 ≥ ih -oh + sh/2
                // ik ≥ ih +sk/2 +sh/2 +ok -oh
                Constraint c2 = model.arithm(
                        i[k],
                        ">=",
                        i[h],
                        "+",
                        +half_k_size +half_h_size + offset_k
                );

                // jk - ok + sk/2 ≤ jh -oh - sh/2
                // jk ≤ jh -sk/2 -sh/2 +ok -oh
                Constraint c3 = model.arithm(
                        j[k],
                        "<=",
                        j[h],
                        "+",
                        -half_k_size -half_h_size - offset_h
                );

                // jk - ok - sk/2 ≥ jh -oh + sh/2
                // jk ≥ jh +sk/2 +sh/2 +ok -oh
                Constraint c4 = model.arithm(
                        j[k],
                        ">=",
                        j[h],
                        "+",
                        +half_k_size +half_h_size + offset_k
                );

                //model.or(c1, c2).post();
                model.or(c1, c2, c3, c4).post();

            }

        }

        // Minimization goal

        setGoalWithIbex(model, i, j);
        //setApproxGoal(model, i, j);

        // Solve
        model.getSolver().limitTime(TIME_LIMIT);
        Solution s = new Solution(model);

        while (model.getSolver().solve()){
            s.record();
        }
        // Check if it is actually a solution to not break the program
        if (model.getSolver().isFeasible() != ESat.TRUE) {
            return false;
        }
        System.out.println("Solution found (objective = "+model.getSolver().getBestSolutionValue()+")");

        // Set solution back to the problem
        for (int k=0; k<pointset.size(); k++) {
            WeightedPoint p = pointset.get(k);
            double delta = 0;
            if (p.getWeight() % 2 != 0) {
                delta = -0.5;
            }
            p.setAssigned_x(s.getIntVal(i[k]) + delta);
            p.setAssigned_y(s.getIntVal(j[k]) + delta);
        }
        return true;
    }

    private void setGoalWithIbex(Model model, IntVar[] i, IntVar[] j) {
        RealVar goal = model.realVar("goal", 0, 100000, PRECISION);
        StringBuilder ibexconstraint = new StringBuilder();
        for (int k=0; k<pointset.size(); k++) {
            WeightedPoint p = pointset.get(k);
            String extra_delta = "";
            if (p.getWeight() % 2 != 0) {
                extra_delta = "+0.5";
            }
            ibexconstraint.append(String.format(
                    "(%f - {%d} %s)^2 + (%f - {%d} %s)^2",
                    p.getX(), k+1, extra_delta,
                    p.getY(), pointset.size()+k+1, extra_delta
            ));
            if (k != pointset.size() - 1){
                ibexconstraint.append(" + ");
            }
        }
        ibexconstraint.append(" = {0}");

        Variable[] goal_list = new Variable[] {goal};
        Variable[] arguments = ArrayUtils.append(goal_list, i, j);
        model.ibex(String.valueOf(ibexconstraint), arguments).post();

        model.setObjective(Model.MINIMIZE, goal);

        IntVar[] minimized = new IntVar[] {};
        minimized = ArrayUtils.append(minimized, i, j);
        model.getSolver().setSearch(Search.activityBasedSearch(minimized));
    }
}
