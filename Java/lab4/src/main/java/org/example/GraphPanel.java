package org.example;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class GraphPanel extends JPanel {
    private Colony colony;
    double progress;
    boolean showAnts;

    public GraphPanel() {
        setPreferredSize(new Dimension(1000, 520));
        setBackground(Color.WHITE);
    }

    public void setColony(Colony colony) {
        this.colony = colony;
        progress = 0;
        showAnts = false;
        repaint();
    }

    private int x(Node node) {
        return 35 + (int) (node.x() * (getWidth() - 70));
    }

    private int y(Node node) {
        return 35 + (int) (node.y() * (getHeight() - 70));
    }

    private void line(Graphics2D g, int from, int to) {
        Node a = colony.nodes.get(from);
        Node b = colony.nodes.get(to);
        g.drawLine(x(a), y(a), x(b), y(b));
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (colony == null) {
            return;
        }
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        double max = 1;
        for (int i = 0; i < colony.nodes.size(); i++) {
            for (int j = i + 1; j < colony.nodes.size(); j++) {
                max = Math.max(max, colony.edges[i][j].pheromone);
            }
        }
        for (int i = 0; i < colony.nodes.size(); i++) {
            for (int j = i + 1; j < colony.nodes.size(); j++) {
                float level = (float) (colony.edges[i][j].pheromone / max);
                g.setColor(new Color(50, 110, 200, 25 + (int) (level * 150)));
                g.setStroke(new BasicStroke(1 + 4 * level));
                line(g, i, j);
            }
        }
        g.setColor(new Color(20, 145, 65));
        g.setStroke(new BasicStroke(3));
        for (int i = 0; i < colony.bestRoute.size() - 1; i++) {
            line(g, colony.bestRoute.get(i), colony.bestRoute.get(i + 1));
        }
        if (showAnts) {
            int step = Math.min((int) progress, colony.nodes.size() - 1);
            double part = Math.min(1, progress - step);
            for (int i = 0; i < colony.ants.size(); i++) {
                Ant ant = colony.ants.get(i);
                Node from = colony.nodes.get(ant.route.get(step));
                Node to = colony.nodes.get(ant.route.get(step + 1));
                int ax = (int) (x(from) + (x(to) - x(from)) * part);
                int ay = (int) (y(from) + (y(to) - y(from)) * part);
                g.setColor(new Color(210, 65 + (i % 3) * 30, 20));
                g.fillOval(ax - 4, ay - 4, 8, 8);
            }
        }
        for (Node node : colony.nodes) {
            g.setColor(new Color(35, 45, 65));
            g.fillOval(x(node) - 12, y(node) - 12, 24, 24);
            String text = String.valueOf(node.id() + 1);
            g.setColor(Color.WHITE);
            g.drawString(text, x(node) - g.getFontMetrics().stringWidth(text) / 2, y(node) + 5);
        }
        g.dispose();
    }
}
