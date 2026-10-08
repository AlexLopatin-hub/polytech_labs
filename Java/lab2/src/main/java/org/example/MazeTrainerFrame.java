package org.example;

import org.example.internal.Environment;

import javax.swing.*;
import java.awt.*;

public class MazeTrainerFrame extends JFrame {
    private final Environment env;
    private final MazePanel mazePanel;
    private final JSpinner agesSpinner;
    private final JButton trainButton;
    private final JButton demoButton;
    private final JButton stopDemoButton;
    private final JButton resetPolicyButton;
    private final JButton newMazeButton;
    private final JLabel statusLabel;
    private final JLabel agesUsedLabel;
    private volatile boolean demoStopRequested;
    private volatile boolean demoFinishedAtCheese;

    public MazeTrainerFrame(Environment env) {
        super("Maze agent trainer");
        this.env = env;
        this.mazePanel = new MazePanel(env);
        this.agesSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 100_000, 100));
        this.trainButton = new JButton("Обучить");
        this.demoButton = new JButton("Демо");
        this.stopDemoButton = new JButton("Остановить демо");
        this.resetPolicyButton = new JButton("Сбросить политику");
        this.newMazeButton = new JButton("Новый лабиринт");
        this.statusLabel = new JLabel("Готово");
        this.agesUsedLabel = new JLabel();
        this.stopDemoButton.setEnabled(false);
        updateAgesUsedLabel();

        buildUi();
        wireActions();
    }

    private void buildUi() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(12, 12));

        add(new JScrollPane(mazePanel), BorderLayout.CENTER);
        add(buildControls(), BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(760, 760));
        setLocationRelativeTo(null);
    }

    private JPanel buildControls() {
        JPanel controls = new JPanel(new BorderLayout(12, 12));
        controls.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.add(new JLabel("Эпох для обучения:"));
        left.add(agesSpinner);
        left.add(trainButton);
        left.add(demoButton);
        left.add(stopDemoButton);
        left.add(resetPolicyButton);
        left.add(newMazeButton);

        controls.add(left, BorderLayout.CENTER);
        JPanel info = new JPanel(new BorderLayout(12, 0));
        info.add(statusLabel, BorderLayout.WEST);
        info.add(agesUsedLabel, BorderLayout.EAST);
        controls.add(info, BorderLayout.SOUTH);
        return controls;
    }

    private void wireActions() {
        trainButton.addActionListener(e -> {
            int ages = (Integer) agesSpinner.getValue();
            runWorker("Обучение...", () -> env.learn(ages));
        });

        demoButton.addActionListener(e -> {
            demoStopRequested = false;
            demoFinishedAtCheese = false;
            runWorker("Демонстрация...", this::runDemo);
        });

        stopDemoButton.addActionListener(e -> {
            demoStopRequested = true;
            setStatus("Остановка демонстрации...");
        });

        resetPolicyButton.addActionListener(e -> {
            env.resetPolicy();
            mazePanel.repaint();
            updateAgesUsedLabel();
            setStatus("Политика сброшена");
        });

        newMazeButton.addActionListener(e -> {
            env.generateNewMaze();
            mazePanel.refreshPreferredSize();
            mazePanel.revalidate();
            mazePanel.repaint();
            updateAgesUsedLabel();
            setStatus("Сгенерирован новый лабиринт");
        });
    }

    private void runDemo() throws InterruptedException {
        for (int i = 0; i < 2000; i++) {
            if (demoStopRequested) {
                return;
            }

            env.step();
            SwingUtilities.invokeLater(mazePanel::repaint);

            if (env.getAgentRow() == env.maze.cheeseR
                    && env.getAgentCol() == env.maze.cheeseC) {
                demoFinishedAtCheese = true;
                return;
            }

            Thread.sleep(150);
        }
    }

    private void runWorker(String runningStatus, WorkerTask task) {
        setControlsEnabled(false);
        stopDemoButton.setEnabled(runningStatus.startsWith("Демонстрация"));
        setStatus(runningStatus);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                task.run();
                return null;
            }

            @Override
            protected void done() {
                setControlsEnabled(true);
                mazePanel.repaint();
                updateAgesUsedLabel();
                try {
                    get();
                    if (demoFinishedAtCheese) {
                        setStatus("Демонстрация завершена: мышь достигла сыра");
                    } else if (demoStopRequested) {
                        setStatus("Демонстрация остановлена");
                    } else {
                        setStatus("Готово");
                    }
                } catch (Exception ex) {
                    setStatus("Ошибка");
                    JOptionPane.showMessageDialog(
                            MazeTrainerFrame.this,
                            ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage(),
                            "Ошибка",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        worker.execute();
    }

    private void setControlsEnabled(boolean enabled) {
        agesSpinner.setEnabled(enabled);
        trainButton.setEnabled(enabled);
        demoButton.setEnabled(enabled);
        stopDemoButton.setEnabled(false);
        resetPolicyButton.setEnabled(enabled);
        newMazeButton.setEnabled(enabled);
    }

    private void setStatus(String text) {
        statusLabel.setText(text);
    }

    private void updateAgesUsedLabel() {
        agesUsedLabel.setText("Пройдено эпох: " + env.getTrainingAges());
    }

    @FunctionalInterface
    private interface WorkerTask {
        void run() throws Exception;
    }
}
