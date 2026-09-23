package org.example.ui.gui;

import org.example.models.environment.Config;
import org.example.models.environment.Environment;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final Config config;
    private Environment env;
    private final GridPanel gridPanel;
    private final ControlPanel controlPanel;
    private final Timer timer;
    private int tickCount = 0;
    private boolean running = false;

    public MainFrame(Config config) {
        super("Life Simulation");
        this.config = config;
        this.env = new Environment(config);

        this.gridPanel = new GridPanel(env);
        this.controlPanel = new ControlPanel();

        setLayout(new BorderLayout());
        add(new JScrollPane(gridPanel), BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);

        this.timer = new Timer(config.tickTime(), e -> stepOnce());

        wireControls();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private void wireControls() {
        controlPanel.onStartPause(() -> {
            running = !running;
            controlPanel.setRunningLabel(running);
            if (running) {
                timer.start();
            } else {
                timer.stop();
            }
        });

        controlPanel.onStep(() -> {
            if (!running) stepOnce();
        });

        controlPanel.onReset(() -> {
            timer.stop();
            running = false;
            controlPanel.setRunningLabel(false);
            tickCount = 0;
            env = new Environment(config);
            gridPanel.setEnvironment(env);
            controlPanel.updateStats(tickCount, env.getAgents());
            gridPanel.repaint();
        });

        controlPanel.onSpeedChange(timer::setDelay);

        controlPanel.updateStats(tickCount, env.getAgents());
    }

    private void stepOnce() {
        env.tick();
        tickCount++;
        controlPanel.updateStats(tickCount, env.getAgents());
        gridPanel.repaint();

        if (env.getAgents().isEmpty()) {
            timer.stop();
            running = false;
            controlPanel.setRunningLabel(false);
            JOptionPane.showMessageDialog(this, "Все агенты вымерли на шаге " + tickCount);
        }
    }
}