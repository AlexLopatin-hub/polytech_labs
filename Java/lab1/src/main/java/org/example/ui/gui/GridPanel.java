package org.example.ui.gui;

import org.example.models.agents.Agent;
import org.example.models.agents.Herbivore;
import org.example.models.agents.Plant;
import org.example.models.agents.Predator;
import org.example.models.environment.Environment;

import javax.swing.*;
import java.awt.*;


public class GridPanel extends JPanel {

    private static final int CELL_SIZE = 12;

    private static final Color BG_EMPTY   = new Color(30, 30, 30);
    private static final Color GRID_LINE  = new Color(50, 50, 50);
    private static final Color COLOR_PLANT     = new Color(76, 175, 80);
    private static final Color COLOR_HERBIVORE = new Color(66, 133, 244);
    private static final Color COLOR_PREDATOR  = new Color(219, 68, 55);

    private Environment env;

    public GridPanel(Environment env) {
        this.env = env;
        setPreferredSize(new Dimension(env.width * CELL_SIZE, env.height * CELL_SIZE));
        setBackground(BG_EMPTY);
    }

    public void setEnvironment(Environment env) {
        this.env = env;
        setPreferredSize(new Dimension(env.width * CELL_SIZE, env.height * CELL_SIZE));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Agent[][] map = env.getMap();

        for (int x = 0; x < env.width; x++) {
            for (int y = 0; y < env.height; y++) {
                int px = x * CELL_SIZE;
                int py = y * CELL_SIZE;

                Agent agent = map[x][y];
                g2.setColor(colorFor(agent));
                g2.fillRect(px, py, CELL_SIZE, CELL_SIZE);

                g2.setColor(GRID_LINE);
                g2.drawRect(px, py, CELL_SIZE, CELL_SIZE);
            }
        }
    }

    private Color colorFor(Agent agent) {
        return switch (agent) {
            case Plant plant -> COLOR_PLANT;
            case Herbivore herbivore -> COLOR_HERBIVORE;
            case Predator predator -> COLOR_PREDATOR;
            case null, default -> BG_EMPTY;
        };
    }
}
