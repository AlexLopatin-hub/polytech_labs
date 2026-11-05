package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Ant {
    final List<Integer> route = new ArrayList<>();
    private final boolean[] visited;
    double length;

    public Ant(int start, int nodeCount) {
        visited = new boolean[nodeCount];
        route.add(start);
        visited[start] = true;
    }

    public int chooseNext(Edge[][] edges, Random random, double alpha, double beta) {
        int current = route.getLast();
        double[] weights = new double[visited.length];
        double sum = 0;
        for (int i = 0; i < visited.length; i++) {
            if (!visited[i]) {
                Edge edge = edges[current][i];
                weights[i] = Math.pow(edge.pheromone, alpha)
                        * Math.pow(1.0 / edge.length, beta);
                sum += weights[i];
            }
        }
        double value = random.nextDouble() * sum;
        int last = -1;
        for (int i = 0; i < weights.length; i++) {
            if (!visited[i]) {
                last = i;
                value -= weights[i];
                if (value < 0) {
                    return i;
                }
            }
        }
        return last;
    }

    public void move(int next, Edge[][] edges) {
        length += edges[route.getLast()][next].length;
        route.add(next);
        visited[next] = true;
    }

    public void buildRoute(Edge[][] edges, Random random, double alpha, double beta) {
        while (route.size() < visited.length) {
            move(chooseNext(edges, random, alpha, beta), edges);
        }
        move(route.getFirst(), edges);
    }
}
