package org.example.internal.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Policy {
    public Policy(int r, int c) {
        Q = new double[r][c][4];
    }

    public double[][][] Q;    // [x][c][direction]

    private final double discountFactor = 0.5;
    private final double learningRate = 0.5;

    private void normalize(double[] arr) {
        double sum = Arrays.stream(arr).sum();

        for (int direction = 0; direction < arr.length; direction++) {
            arr[direction] /= sum;
        }
    }

    public Action selectAction(State state) {
        return selectAction(state, new boolean[]{true, true, true, true});
    }

    public Action selectAction(State state, boolean[] allowedActions) {
        double[] variants = this.Q[state.r][state.c];
        int actionID;
        ArrayList<Integer> actionIDs = new ArrayList<Integer>(4);
        double r = new Random().nextDouble();

        if (r <= 0.9) {
            // select the most profitable action
            double maxVal = Double.NEGATIVE_INFINITY;

            for (int i = 0; i < variants.length; i++) {
                if (allowedActions[i] && variants[i] > maxVal) {
                    maxVal = variants[i];
                }
            }

            for (int i = 0; i < variants.length; i++) {
                if (allowedActions[i] && variants[i] == maxVal) {
                    actionIDs.add(i);
                }
            }

            actionID = actionIDs.get(new Random().nextInt(actionIDs.size()));

//            System.out.println(Arrays.toString(variants) + " -> " + actionID);
        } else {
            // epsilon-greedy
            for (int i = 0; i < allowedActions.length; i++) {
                if (allowedActions[i]) {
                    actionIDs.add(i);
                }
            }
            actionID = actionIDs.get(new Random().nextInt(actionIDs.size()));
        }

        return Action.actionFromID(actionID);
    };

    // Q_new(s,a) = Q_old(s,a) + learningRate[reward + discountFactor * max_s'(Q(s',a')) - Q_old(s,a)]
    public void update(State oldState, State newState, int actionID, double reward) {
        double[] Qs = this.Q[oldState.r][oldState.c];
        double[] Qs2 = this.Q[newState.r][newState.c];

        double maxQs2 = 0;
        for (double v : Qs2) {
            if (v > maxQs2) {
                maxQs2 = v;
            }
        }

        Qs[actionID] += this.learningRate * (reward + this.discountFactor * maxQs2 - Qs[actionID]);
    };

}


