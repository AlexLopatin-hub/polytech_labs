package org.example;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashSet;
import java.util.List;

public class ColonyTest {
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static double exact(Colony colony, int current, int mask) {
        if (mask == (1 << colony.nodes.size()) - 1) {
            return colony.edges[current][0].length;
        }
        double best = Double.POSITIVE_INFINITY;
        for (int next = 1; next < colony.nodes.size(); next++) {
            if ((mask & (1 << next)) == 0) {
                best = Math.min(best, colony.edges[current][next].length
                        + exact(colony, next, mask | (1 << next)));
            }
        }
        return best;
    }

    private static void layout(Container container) {
        container.doLayout();
        for (Component component : container.getComponents()) {
            if (component instanceof Container child) {
                layout(child);
            }
        }
    }

    private static void screenshot(MainPanel panel, String name) throws Exception {
        panel.setSize(panel.getPreferredSize());
        layout(panel);
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        panel.printAll(g);
        g.dispose();
        ImageIO.write(image, "png", new File("docs/screenshots/" + name + ".png"));
    }

    public static void main(String[] args) throws Exception {
        Colony square = new Colony(List.of(new Node(0, 0, 0), new Node(1, 1, 0),
                new Node(2, 1, 1), new Node(3, 0, 1)), 42);
        square.prepareIteration();
        double expected = 1 - Colony.EVAPORATION;
        for (Ant ant : square.ants) {
            for (int i = 0; i < ant.route.size() - 1; i++) {
                int a = ant.route.get(i);
                int b = ant.route.get(i + 1);
                if ((a == 0 && b == 1) || (a == 1 && b == 0)) {
                    expected += Colony.Q / ant.length;
                }
            }
        }
        square.finishIteration();
        check(Math.abs(square.edges[0][1].pheromone - expected) < 1e-9, "Pheromone update");
        for (int i = 1; i < 100; i++) {
            square.runIteration();
        }
        check(Math.abs(square.bestLength - 4) < 1e-9, "Square optimum");
        for (int size = 4; size <= 12; size++) {
            Colony colony = new Colony(Colony.createNodes(size, 42), 42);
            for (int cycle = 0; cycle < 200; cycle++) {
                double previous = colony.bestLength;
                colony.runIteration();
                check(colony.bestLength <= previous, "Best length must not grow");
                for (Ant ant : colony.ants) {
                    check(ant.route.size() == size + 1, "Closed tour size");
                    check(ant.route.getFirst().equals(ant.route.getLast()), "Return to start");
                    check(new HashSet<>(ant.route).size() == size, "Visit every city once");
                    double length = 0;
                    for (int i = 0; i < size; i++) {
                        length += colony.edges[ant.route.get(i)][ant.route.get(i + 1)].length;
                    }
                    check(Math.abs(length - ant.length) < 1e-9, "Tour length");
                }
                for (int i = 0; i < size; i++) {
                    for (int j = i + 1; j < size; j++) {
                        check(colony.edges[i][j] == colony.edges[j][i], "Undirected edge");
                        check(Double.isFinite(colony.edges[i][j].pheromone)
                                && colony.edges[i][j].pheromone > 0, "Positive pheromone");
                    }
                }
            }
        }
        SwingUtilities.invokeAndWait(() -> {
            try {
                MainPanel panel = new MainPanel();
                panel.colony.prepareIteration();
                panel.graph.showAnts = true;
                panel.graph.progress = 0.45;
                screenshot(panel, "initial");
                panel.step.doClick();
                System.out.printf(java.util.Locale.US, "First: best=%.8f mean=%.8f%n",
                        panel.colony.bestLength, panel.colony.averageLength);
                screenshot(panel, "first-cycle");
                panel.start.doClick();
                check(panel.timer.isRunning(), "Start timer");
                panel.timer.getActionListeners()[0].actionPerformed(null);
                panel.start.doClick();
                check(!panel.timer.isRunning(), "Pause timer");
                panel.step.doClick();
                check(panel.colony.iteration == 2, "Finish paused cycle once");
                panel.finish.doClick();
                check(panel.colony.iteration == 200, "Finish all cycles");
                double optimum = exact(panel.colony, 0, 1);
                check(Math.abs(panel.colony.bestLength - optimum) < 1e-9, "Default graph optimum");
                for (Ant ant : panel.colony.ants) {
                    check(Math.abs(ant.length - optimum) < 1e-9, "All ants reached default optimum");
                }
                screenshot(panel, "final");
                System.out.printf(java.util.Locale.US, "Final: best=%.8f mean=%.8f exact=%.8f%n",
                        panel.colony.bestLength, panel.colony.averageLength, optimum);
                System.out.println("Route: " + panel.colony.routeText());
                panel.nodeCount.setValue(12);
                check(panel.colony.iteration == 0 && panel.colony.nodes.size() == 12, "Reset on resize");
                panel.cycles.setValue(1);
                panel.finish.doClick();
                panel.step.doClick();
                check(panel.colony.iteration == 1, "Cycle limit");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("All checks passed");
    }
}
