package org.example;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class MainPanel extends JPanel {
    final GraphPanel graph = new GraphPanel();
    final AverageChartPanel chart = new AverageChartPanel();
    final JSpinner nodeCount = new JSpinner(new SpinnerNumberModel(8, 4, 12, 1));
    final JSpinner cycles = new JSpinner(new SpinnerNumberModel(200, 1, 1000, 10));
    final JSlider speed = new JSlider(1, 20, 5);
    final JButton start = new JButton("Старт");
    final JButton step = new JButton("Один цикл");
    final JButton finish = new JButton("Завершить");
    final JLabel statistics = new JLabel();
    final JTextArea route = new JTextArea(3, 20);
    final Timer timer;
    Colony colony;
    private long seed = System.currentTimeMillis();
    private boolean prepared;

    public MainPanel() {
        super(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel settings = new JPanel(new GridLayout(3, 2, 5, 4));
        settings.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Параметры"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        settings.add(new JLabel("Города:"));
        settings.add(nodeCount);
        settings.add(new JLabel("Циклы:"));
        settings.add(cycles);
        settings.add(new JLabel("Скорость:"));
        speed.setPreferredSize(new java.awt.Dimension(100, 24));
        settings.add(speed);
        JButton newGraph = new JButton("Новый граф");
        JButton reset = new JButton("Сброс");
        JPanel controls = new JPanel(new GridLayout(2, 3, 5, 4));
        controls.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Управление"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        controls.add(newGraph);
        controls.add(reset);
        controls.add(start);
        controls.add(step);
        controls.add(finish);
        add(graph, BorderLayout.CENTER);
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 4));
        info.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Информация"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        info.setPreferredSize(new java.awt.Dimension(300, 90));
        route.setEditable(false);
        route.setOpaque(false);
        route.setLineWrap(true);
        route.setWrapStyleWord(true);
        route.setFont(statistics.getFont());
        info.add(statistics);
        info.add(route);
        JPanel results = new JPanel(new BorderLayout(8, 0));
        results.add(info, BorderLayout.WEST);
        results.add(chart, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout(8, 0));
        bottom.add(settings, BorderLayout.WEST);
        bottom.add(controls, BorderLayout.CENTER);
        bottom.add(results, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
        timer = new Timer(30, event -> animate());
        newGraph.addActionListener(event -> {
            seed++;
            reset();
        });
        reset.addActionListener(event -> reset());
        nodeCount.addChangeListener(event -> reset());
        cycles.addChangeListener(event -> updateLabels());
        start.addActionListener(event -> {
            if (timer.isRunning()) {
                pause();
            } else if (colony.iteration < (int) cycles.getValue()) {
                timer.start();
                start.setText("Пауза");
                step.setEnabled(false);
                finish.setEnabled(false);
            }
        });
        step.addActionListener(event -> {
            if (colony.iteration < (int) cycles.getValue()) {
                completeCycle();
            }
        });
        finish.addActionListener(event -> {
            while (colony.iteration < (int) cycles.getValue()) {
                completeCycle();
            }
        });
        reset();
    }

    private void reset() {
        pause();
        colony = new Colony(Colony.createNodes((int) nodeCount.getValue(), seed), seed);
        graph.setColony(colony);
        chart.setColony(colony);
        prepared = false;
        updateLabels();
    }

    private void pause() {
        timer.stop();
        start.setText("Старт");
        step.setEnabled(true);
        finish.setEnabled(true);
    }

    private void animate() {
        if (colony.iteration >= (int) cycles.getValue()) {
            pause();
            return;
        }
        if (!prepared) {
            colony.prepareIteration();
            prepared = true;
            graph.progress = 0;
        }
        graph.showAnts = true;
        graph.progress += speed.getValue() * 0.025;
        if (graph.progress >= colony.nodes.size()) {
            completeCycle();
        }
        graph.repaint();
    }

    private void completeCycle() {
        if (!prepared) {
            colony.prepareIteration();
        }
        colony.finishIteration();
        prepared = false;
        graph.showAnts = false;
        updateLabels();
        graph.repaint();
        chart.repaint();
    }

    void updateLabels() {
        String lengths = colony.iteration == 0 ? "Лучший путь: —<br>Средний за цикл: —"
                : String.format("Лучший путь: %.4f<br>Средний за цикл: %.4f",
                colony.bestLength, colony.averageLength);
        statistics.setText("<html>Цикл: " + colony.iteration + " / " + cycles.getValue()
                + " | Муравьёв: " + colony.nodes.size() * 3 + "<br>" + lengths + "</html>");
        route.setText("Лучший маршрут:\n" + (colony.bestRoute.isEmpty() ? "—" : colony.routeText()));
    }
}
