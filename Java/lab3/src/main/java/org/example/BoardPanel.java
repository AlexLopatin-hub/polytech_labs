package org.example;

import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public final class BoardPanel extends JPanel {
    private final boolean knowledgeView;
    private Game game;
    private int cell, left, top;
    public BoardPanel(boolean knowledgeView) {
        this.knowledgeView = knowledgeView;
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(440, 440));
        ToolTipManager.sharedInstance().registerComponent(this);
    }
    public void update(Game game) { this.game = game; repaint(); }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (game == null) return;
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int n = game.world().size();
        cell = Math.max(1, (Math.min(getWidth(), getHeight()) - 32) / n);
        left = (getWidth() - n * cell) / 2; top = (getHeight() - n * cell) / 2;
        for (int r = 0; r < n; r++) for (int c = 0; c < n; c++) {
            Position p = new Position(r, c);
            boolean visited = game.knowledge().visited(p);
            String mark = game.knowledge().mark(p);
            String goldMark = knowledgeView ? game.knowledge().goldMark(p) : "";
            Color background;
            if (knowledgeView) {
                background = goldMark.equals("G") || goldMark.equals("G✓") ? new Color(255, 239, 176)
                        : mark.contains("!") ? new Color(255, 226, 218)
                        : game.knowledge().safe(p) ? new Color(220, 244, 232)
                        : new Color(239, 243, 248);
            } else background = new Color(248, 248, 240);
            int x = left + c * cell, y = top + r * cell;
            g.setColor(background); g.fillRect(x + 1, y + 1, cell - 2, cell - 2);
            g.setColor(new Color(190, 201, 212)); g.drawRect(x, y, cell, cell);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(9, cell / 7)));
            g.setColor(new Color(104, 117, 130));
            g.drawString((r + 1) + "," + (c + 1), x + 5, y + Math.max(12, cell / 6));
            String text;
            if (knowledgeView) text = mark.replace(" · посещено", "");
            else if (game.world().pitAt(p)) text = "P";
            else if (p.equals(game.world().wampus())) text = game.world().wampusAlive() ? "W" : "W×";
            else if (p.equals(game.world().gold()) && !game.hasGold()) text = "G";
            else text = "·";
            if (knowledgeView && goldMark.equals("G")) text = "G";
            if (p.equals(game.position())) text = "H " + game.direction().arrow;
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(12, cell / 3)));
            g.setColor(p.equals(game.position()) ? new Color(16, 109, 193)
                    : new Color(34, 57, 69));
            int width = g.getFontMetrics().stringWidth(text);
            g.drawString(text, x + (cell - width) / 2, y + cell / 2 + g.getFontMetrics().getAscent() / 3);
            if (visited && cell >= 48) {
                Percept percept = game.knowledge().observation(p);
                String sensors = (percept.stench() ? "S " : "") + (percept.breeze() ? "B " : "")
                        + (percept.glitter() ? "G*" : "");
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(10, cell / 7)));
                g.drawString(sensors, x + 6, y + cell - 7);
            }
            if (!goldMark.isEmpty()) {
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(10, cell / 6)));
                g.setColor(new Color(151, 103, 0));
                g.drawString(goldMark, x + cell - g.getFontMetrics().stringWidth(goldMark) - 4, y + cell - 5);
            }
            if (p.equals(game.position())) {
                g.setStroke(new BasicStroke(3)); g.setColor(new Color(34, 133, 218));
                g.drawRect(x + 3, y + 3, cell - 6, cell - 6); g.setStroke(new BasicStroke(1));
            }
        }
        g.dispose();
    }

    @Override public String getToolTipText(MouseEvent event) {
        if (game == null || cell <= 0 || event.getX() < left || event.getY() < top) return null;
        Position p = new Position((event.getY() - top) / cell, (event.getX() - left) / cell);
        if (!p.inside(game.world().size())) return null;
        Percept observation = game.knowledge().observation(p);
        String gold = game.knowledge().goldMark(p);
        return p + ": " + game.knowledge().mark(p) + (gold.isEmpty() ? "" : " | Золото: " + gold)
                + (observation == null ? "" : " | " + observation.russian());
    }
}
