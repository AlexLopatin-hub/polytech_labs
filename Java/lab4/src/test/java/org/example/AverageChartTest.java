package org.example;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

public class AverageChartTest {
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void layout(Container container) {
        container.doLayout();
        for (Component component : container.getComponents()) {
            if (component instanceof Container child) {
                layout(child);
            }
        }
    }

    private static void render(MainPanel panel, String name) throws Exception {
        panel.setSize(panel.getPreferredSize());
        layout(panel);
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        panel.printAll(g);
        g.dispose();
        ImageIO.write(image, "png", new File("tmp/work/" + name + ".png"));
    }

    public static void main(String[] args) throws Exception {
        java.nio.file.Files.createDirectories(java.nio.file.Path.of("tmp/work"));
        Colony colony = new Colony(Colony.createNodes(8, 42), 42);
        for (int cycle = 0; cycle < 200; cycle++) {
            colony.runIteration();
            double mean = colony.ants.stream().mapToDouble(ant -> ant.length).average().orElseThrow();
            check(colony.averageHistory.size() == cycle + 1, "One average per cycle");
            check(Math.abs(colony.averageHistory.get(cycle) - mean) < 1e-9, "Mean of all ants");
        }
        check(colony.averageHistory.getFirst() > colony.history.getFirst(), "Mean differs from best");
        SwingUtilities.invokeAndWait(() -> {
            try {
                MainPanel panel = new MainPanel();
                render(panel, "chart-empty");
                panel.step.doClick();
                check(panel.colony.averageHistory.size() == 1, "Step updates chart data");
                render(panel, "chart-one");
                panel.finish.doClick();
                check(panel.colony.averageHistory.size() == 200, "Finish retains every cycle");
                render(panel, "chart-final");
                panel.chart.setColony(colony);
                render(panel, "chart-fixed-example");
                panel.nodeCount.setValue(12);
                check(panel.colony.averageHistory.isEmpty(), "Reset clears history");
                render(panel, "chart-reset");
                double mean = 2.0;
                panel.colony.averageHistory.add(mean);
                panel.colony.averageHistory.add(mean);
                render(panel, "chart-constant");
                check(SwingUtilities.convertPoint(panel.chart.getParent(), panel.chart.getLocation(), panel).x
                        > SwingUtilities.convertPoint(panel.statistics.getParent(), panel.statistics.getLocation(), panel).x,
                        "Chart is to the right of information");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Average history and chart checks passed");
    }
}
