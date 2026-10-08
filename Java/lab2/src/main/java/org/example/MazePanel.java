package org.example;

import org.example.internal.Environment;
import org.example.internal.models.CellType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MazePanel extends JPanel {
    private final Environment env;

    public MazePanel(Environment env) {
        this.env = env;
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(12, 12, 12, 12));
        refreshPreferredSize();
    }

    public void refreshPreferredSize() {
        int cellSize = 28;
        setPreferredSize(new Dimension(
                env.getMazeWidth() * cellSize,
                env.getMazeHeight() * cellSize
        ));
        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        CellType[][] grid = env.getMazeSnapshot();
        boolean[][] visited = env.getVisitedSnapshot();
        if (grid.length == 0 || grid[0].length == 0) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Insets insets = getInsets();
            int availableWidth = getWidth() - insets.left - insets.right;
            int availableHeight = getHeight() - insets.top - insets.bottom;
            int cellSize = Math.max(12, Math.min(
                    availableWidth / grid[0].length,
                    availableHeight / grid.length
            ));
            int mazeWidth = cellSize * grid[0].length;
            int mazeHeight = cellSize * grid.length;
            int offsetX = insets.left + Math.max(0, (availableWidth - mazeWidth) / 2);
            int offsetY = insets.top + Math.max(0, (availableHeight - mazeHeight) / 2);

            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[r].length; c++) {
                    int x = offsetX + c * cellSize;
                    int y = offsetY + r * cellSize;

                    Color color = switch (grid[r][c]) {
                        case WALL -> new Color(55, 55, 55);
                        case CHEESE -> new Color(245, 205, 66);
                        case WATER -> new Color(74, 144, 226);
                        case SHOCK -> new Color(220, 84, 84);
                        case MOUSE -> new Color(83, 180, 102);
                        case EMPTY -> visited[r][c] ? new Color(223, 245, 247) : Color.WHITE;
                    };

                    g2.setColor(color);
                    g2.fillRect(x, y, cellSize, cellSize);
                    g2.setColor(new Color(215, 215, 215));
                    g2.drawRect(x, y, cellSize, cellSize);
                }
            }

            g2.setFont(g2.getFont().deriveFont(Font.BOLD, Math.max(12f, cellSize * 0.42f)));
            g2.setColor(new Color(30, 30, 30));
            for (int r = 0; r < grid.length; r++) {
                for (int c = 0; c < grid[r].length; c++) {
                    char symbol = grid[r][c].symbol;
                    if (symbol == ' ' || symbol == '#') {
                        continue;
                    }
                    String text = String.valueOf(symbol);
                    FontMetrics fm = g2.getFontMetrics();
                    int textWidth = fm.stringWidth(text);
                    int textHeight = fm.getAscent();
                    int x = offsetX + c * cellSize + (cellSize - textWidth) / 2;
                    int y = offsetY + r * cellSize + (cellSize + textHeight) / 2 - 2;
                    g2.drawString(text, x, y);
                }
            }
        } finally {
            g2.dispose();
        }
    }
}
