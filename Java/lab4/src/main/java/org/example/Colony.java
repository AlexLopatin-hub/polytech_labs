package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Colony {
    static final double ALPHA = 1;
    static final double BETA = 3;
    static final double EVAPORATION = 0.35;
    static final double Q = 100;
    final List<Node> nodes;
    final Edge[][] edges;
    final List<Ant> ants = new ArrayList<>();
    final List<Double> history = new ArrayList<>();
    final List<Double> averageHistory = new ArrayList<>();
    private final Random random;
    List<Integer> bestRoute = new ArrayList<>();
    double bestLength = Double.POSITIVE_INFINITY;
    double averageLength;
    int iteration;

    public Colony(List<Node> nodes, long seed) {
        this.nodes = List.copyOf(nodes);
        random = new Random(seed);
        edges = new Edge[nodes.size()][nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                Edge edge = new Edge(nodes.get(i), nodes.get(j));
                edges[i][j] = edge;
                edges[j][i] = edge;
            }
        }
    }

    public static List<Node> createNodes(int count, long seed) {
        Random random = new Random(seed);
        List<Node> nodes = new ArrayList<>();
        while (nodes.size() < count) {
            Node node = new Node(nodes.size(), 0.08 + random.nextDouble() * 0.84,
                    0.08 + random.nextDouble() * 0.84);
            if (nodes.stream().allMatch(other -> node.distanceTo(other) > 0.12)) {
                nodes.add(node);
            }
        }
        return nodes;
    }

    public void prepareIteration() {
        ants.clear();
        for (int i = 0; i < nodes.size() * 3; i++) {
            Ant ant = new Ant(i % nodes.size(), nodes.size());
            ant.buildRoute(edges, random, ALPHA, BETA);
            ants.add(ant);
        }
    }

    public void finishIteration() {
        if (ants.isEmpty()) {
            throw new IllegalStateException("Сначала нужно построить маршруты");
        }
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                edges[i][j].pheromone *= 1 - EVAPORATION;
            }
        }
        averageLength = 0;
        for (Ant ant : ants) {
            averageLength += ant.length;
            if (ant.length < bestLength) {
                bestLength = ant.length;
                bestRoute = new ArrayList<>(ant.route);
            }
            for (int i = 0; i < ant.route.size() - 1; i++) {
                edges[ant.route.get(i)][ant.route.get(i + 1)].pheromone += Q / ant.length;
            }
        }
        averageLength /= ants.size();
        iteration++;
        history.add(bestLength);
        averageHistory.add(averageLength);
    }

    public void runIteration() {
        prepareIteration();
        finishIteration();
    }

    public String routeText() {
        return String.join(" → ", bestRoute.stream().map(i -> String.valueOf(i + 1)).toList());
    }
}
