package org.example.ui.gui;

import org.example.models.agents.Agent;
import org.example.models.agents.Herbivore;
import org.example.models.agents.Plant;
import org.example.models.agents.Predator;

import javax.swing.*;
import java.awt.*;
import java.util.List;


public class ControlPanel extends JPanel {

    private final JButton startPauseButton = new JButton("Старт");
    private final JButton stepButton = new JButton("Шаг");
    private final JButton resetButton = new JButton("Перезапуск");
    private final JSlider speedSlider = new JSlider(1, 100, 10);
    private final JLabel tickLabel = new JLabel("Шаг: 0");
    private final JLabel plantsLabel = new JLabel("Растения: 0");
    private final JLabel herbivoresLabel = new JLabel("Травоядные: 0");
    private final JLabel predatorsLabel = new JLabel("Хищники: 0");

    public ControlPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(startPauseButton);
        buttons.add(stepButton);
        buttons.add(resetButton);
        add(buttons);

        JPanel speedPanel = new JPanel(new BorderLayout(8, 0));
        speedPanel.add(new JLabel("Скорость (шагов/с):"), BorderLayout.WEST);
        speedSlider.setMajorTickSpacing(10);
        speedSlider.setPaintTicks(true);
        speedPanel.add(speedSlider, BorderLayout.CENTER);
        add(speedPanel);

        JPanel stats = new JPanel(new GridLayout(4, 1, 0, 4));
        stats.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        stats.add(tickLabel);
        stats.add(plantsLabel);
        stats.add(herbivoresLabel);
        stats.add(predatorsLabel);
        add(stats);
    }

    public void onStartPause(Runnable action) {
        startPauseButton.addActionListener(e -> action.run());
    }

    public void onStep(Runnable action) {
        stepButton.addActionListener(e -> action.run());
    }

    public void onReset(Runnable action) {
        resetButton.addActionListener(e -> action.run());
    }

    public void onSpeedChange(java.util.function.IntConsumer action) {
        speedSlider.addChangeListener(e -> action.accept(speedSlider.getValue()));
    }

    public void setRunningLabel(boolean running) {
        startPauseButton.setText(running ? "Пауза" : "Старт");
    }

    public void updateStats(int tick, List<Agent> agents) {
        long plants = agents.stream().filter(a -> a instanceof Plant).count();
        long herbivores = agents.stream().filter(a -> a instanceof Herbivore).count();
        long predators = agents.stream().filter(a -> a instanceof Predator).count();

        tickLabel.setText("Шаг: " + tick);
        plantsLabel.setText("Растения: " + plants);
        herbivoresLabel.setText("Травоядные: " + herbivores);
        predatorsLabel.setText("Хищники: " + predators);
    }
}
