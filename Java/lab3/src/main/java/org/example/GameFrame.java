package org.example;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public final class GameFrame extends JFrame {
    private final BoardPanel worldBoard = new BoardPanel(false);
    private final BoardPanel knowledgeBoard = new BoardPanel(true);
    private final JTextArea journal = textArea();
    private final JTextArea facts = textArea();
    private final JLabel status = new JLabel();
    private final JLabel sensations = new JLabel();
    private final JSpinner size = new JSpinner(new SpinnerNumberModel(4, 4, 12, 1));
    private final JButton auto = new JButton("Старт ▶");
    private final JButton step = new JButton("Шаг агента");
    private final List<JButton> manualButtons = new ArrayList<>();
    private final Timer timer;
    private Game game;
    private LogicalAgent agent;

    public GameFrame() {
        super("World of Wampus");
        timer = new Timer(400, event -> agentStep());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(buildContent());
        setMinimumSize(new Dimension(1000, 760));
        setSize(1200, 880);
        setLocationRelativeTo(null);
        newRandomGame();
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        root.setBackground(new Color(241, 245, 249));
        JPanel maps = new JPanel(new GridLayout(1, 2, 12, 0)); maps.setOpaque(false);
        maps.add(card("ПЕЩЕРА", worldBoard));
        maps.add(card("ПРЕДСТАВЛЕНИЕ АГЕНТА", knowledgeBoard));
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Журнал действий", new JScrollPane(journal));
        tabs.addTab("База знаний", new JScrollPane(facts));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, maps, tabs);
        split.setResizeWeight(0.73); split.setDividerLocation(440); split.setBorder(null);
        root.add(split, BorderLayout.CENTER);

        JPanel south = new JPanel(); south.setOpaque(false); south.setLayout(new BoxLayout(south, BoxLayout.Y_AXIS));
        status.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        south.add(status); south.add(Box.createVerticalStrut(5)); south.add(sensations);
        JPanel controls = new JPanel(new BorderLayout(8, 0)); controls.setOpaque(false);
        controls.setAlignmentX(0.0f);
        for (JLabel label : new JLabel[]{status, sensations}) label.setAlignmentX(0.0f);
        JPanel playback = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        playback.add(auto); playback.add(step);
        JPanel settings = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        settings.add(new JLabel("Размер N:")); settings.add(size);
        JButton random = new JButton("Новое случайное поле");
        random.addActionListener(event -> {
            try {
                size.commitEdit();
                newRandomGame();
            } catch (java.text.ParseException ex) {
                JOptionPane.showMessageDialog(this, "Введите размер от 4 до 12.", "Параметры", JOptionPane.WARNING_MESSAGE);
            }
        });
        settings.add(random);

        JPanel manual = new JPanel(new GridLayout(2, 3, 5, 5));
        addManual(manual, "↶ Налево", Game.Action.LEFT);
        addManual(manual, "↑ Вперёд", Game.Action.FORWARD);
        addManual(manual, "↷ Направо", Game.Action.RIGHT);
        addManual(manual, "Выстрел", Game.Action.SHOOT);
        addManual(manual, "Взять золото", Game.Action.GRAB);
        controls.add(controlGroup("Старт / пауза", playback), BorderLayout.WEST);
        controls.add(controlGroup("Генерация мира", settings), BorderLayout.CENTER);
        controls.add(controlGroup("Ручное управление", manual), BorderLayout.EAST);
        step.addActionListener(event -> { stopAuto(); agentStep(); });
        auto.addActionListener(event -> {
            if (timer.isRunning()) stopAuto();
            else { timer.start(); auto.setText("Пауза ❚❚"); }
        });
        south.add(Box.createVerticalStrut(8)); south.add(controls);
        root.add(south, BorderLayout.SOUTH);
        return root;
    }

    private JPanel controlGroup(String title, JPanel content) {
        JPanel group = new JPanel(new BorderLayout());
        group.setBorder(BorderFactory.createTitledBorder(title));
        group.add(content, BorderLayout.CENTER);
        return group;
    }

    private JPanel card(String title, JPanel board) {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(215, 225, 233)),
                BorderFactory.createEmptyBorder(10, 10, 8, 10)));
        JLabel heading = new JLabel(title); heading.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        panel.add(heading, BorderLayout.NORTH); panel.add(board, BorderLayout.CENTER);
        return panel;
    }

    private void addManual(JPanel panel, String name, Game.Action action) {
        JButton button = new JButton(name); manualButtons.add(button); panel.add(button);
        button.addActionListener(event -> {
            stopAuto(); game.act(action); refresh();
        });
    }
    private void newRandomGame() {
        int n = (Integer) size.getValue();
        newGame(World.random(n));
    }
    private void newGame(World world) {
        stopAuto(); game = new Game(world); agent = new LogicalAgent();
        refresh();
    }
    private void agentStep() {
        if (game.status() != Game.Status.PLAYING) { stopAuto(); return; }
        game.stepAgent(agent);
        refresh();
    }
    private void stopAuto() { timer.stop(); auto.setText("Старт ▶"); }
    private void refresh() {
        boolean playing = game.status() == Game.Status.PLAYING;
        if (!playing) stopAuto();
        for (JButton button : manualButtons) button.setEnabled(playing);
        step.setEnabled(playing); auto.setEnabled(playing);
        worldBoard.update(game); knowledgeBoard.update(game);
        String state = switch (game.status()) { case PLAYING -> "Игра идёт"; case WON -> "ПОБЕДА - золото получено"; case LOST -> "ПОРАЖЕНИЕ - охотник погиб"; case STUCK -> "ТУПИК - симуляция завершена"; };
        status.setText(state + "   |   " + game.position() + "   " + game.direction().title() + "   |   Ход: " + game.steps()
                + "   |   Стрелы: " + (game.hasArrow() ? 1 : 0) + "   |   Вампус: " + (game.world().wampusAlive() ? "жив" : "мёртв"));
        sensations.setText("Ощущения охотника: " + game.percept().russian());
        journal.setText(String.join("\n", game.log())); journal.setCaretPosition(journal.getDocument().getLength());
        facts.setText(String.join("\n", game.knowledge().sentences())); facts.setCaretPosition(0);
    }
    private static JTextArea textArea() {
        JTextArea text = new JTextArea(); text.setEditable(false); text.setLineWrap(true); text.setWrapStyleWord(true);
        text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13)); text.setMargin(new java.awt.Insets(8, 10, 8, 10)); return text;
    }
}
