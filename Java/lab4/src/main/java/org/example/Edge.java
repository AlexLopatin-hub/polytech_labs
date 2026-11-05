package org.example;

public class Edge {
    final Node from;
    final Node to;
    final double length;
    double pheromone = 1.0;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
        length = from.distanceTo(to);
    }
}
