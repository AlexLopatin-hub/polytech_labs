package org.example;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.List;

public class AverageChartPanel extends JPanel {
    private Colony colony;

    public AverageChartPanel() {
        setPreferredSize(new Dimension(320, 145));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Среднее пройденное расстояние"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    public void setColony(Colony colony) {
        this.colony = colony;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int left = getInsets().left + 50;
        int right = getWidth() - getInsets().right - 12;
        int top = getInsets().top + 20;
        int bottom = getHeight() - getInsets().bottom - 28;
        if (right <= left || bottom <= top) {
            g.dispose();
            return;
        }
        g.setColor(Color.WHITE);
        g.fillRect(left, top, right - left, bottom - top);
        g.setColor(Color.DARK_GRAY);
        g.drawLine(left, top, left, bottom);
        g.drawLine(left, bottom, right, bottom);
        g.drawString("Длина", getInsets().left, top - 12);
        g.drawString("Циклы", right - 35, bottom + 25);
        if (colony == null || colony.averageHistory.isEmpty()) {
            g.drawString("Нет завершённых циклов", left + 8, (top + bottom) / 2);
            g.dispose();
            return;
        }
        List<Double> values = colony.averageHistory;
        double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double padding = Math.max((max - min) * 0.1, max * 0.01);
        min = Math.max(0, min - padding);
        max += padding;
        for (int i = 0; i <= 2; i++) {
            int y = bottom - i * (bottom - top) / 2;
            g.setColor(new Color(225, 225, 225));
            g.drawLine(left + 1, y, right, y);
            g.setColor(Color.DARK_GRAY);
            String label = String.format("%.3f", min + (max - min) * i / 2);
            g.drawString(label, left - g.getFontMetrics().stringWidth(label) - 5, y + 4);
        }
        g.drawString("1", left - 3, bottom + 14);
        if (values.size() > 1) {
            String last = String.valueOf(values.size());
            g.drawString(last, right - g.getFontMetrics().stringWidth(last), bottom + 14);
        }
        g.setColor(new Color(40, 110, 200));
        g.setStroke(new BasicStroke(2));
        int previousX = left;
        int previousY = bottom - (int) ((values.getFirst() - min) / (max - min) * (bottom - top));
        for (int i = 1; i < values.size(); i++) {
            int x = left + i * (right - left) / (values.size() - 1);
            int y = bottom - (int) ((values.get(i) - min) / (max - min) * (bottom - top));
            g.drawLine(previousX, previousY, x, y);
            previousX = x;
            previousY = y;
        }
        g.fillOval(previousX - 3, previousY - 3, 6, 6);
        g.dispose();
    }
}
