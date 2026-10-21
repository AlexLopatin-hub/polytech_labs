package org.example;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Predicate;

public final class LogicalAgent {
    public record Decision(Game.Action action, String explanation) { }

    public Decision decide(KnowledgeBase kb, Position position, Direction direction,
                           Percept percept, boolean hasArrow) {
        if (kb.wampusDead()) {
            Position gold = kb.knownGold();
            if (position.equals(gold)) return new Decision(Game.Action.GRAB, "Забираю найденное золото.");
            if (gold != null) {
                Position goldStep = firstStep(kb, position, gold::equals);
                if (goldStep != null) return move(position, goldStep, direction, "Иду кратчайшим безопасным путём к золоту в " + gold);
            }
        }
        Position wampus = kb.knownWampus();
        if (hasArrow && wampus != null) {
            if (aligned(position, wampus)) {
                Direction target = Direction.toward(position, wampus);
                return new Decision(turnOr(direction, target, Game.Action.SHOOT),
                        "Вампус доказан в " + wampus + ": направляю единственную стрелу по прямой.");
            }
            Position step = firstStep(kb, position, p -> aligned(p, wampus));
            if (step != null) return move(position, step, direction, "Иду к безопасной позиции для выстрела.");
        }

        if (kb.wampusDead()) {
            Position goldStep = firstStep(kb, position, p -> !kb.visited(p) && kb.possibleGold(p));
            if (goldStep != null) return move(position, goldStep, direction, "Проверяю ближайшее возможное местоположение золота.");
        }
        Position step = firstStep(kb, position, p -> !kb.visited(p));
        if (step != null) return move(position, step, direction, "Исследую ближайшую доказанно безопасную комнату.");
        return new Decision(null, "Безопасных новых ходов и доказанного выстрела нет. Охотник в тупике.");
    }

    private boolean aligned(Position a, Position b) {
        return !a.equals(b) && (a.row() == b.row() || a.col() == b.col());
    }
    private Decision move(Position from, Position to, Direction facing, String reason) {
        return new Decision(turnOr(facing, Direction.toward(from, to), Game.Action.FORWARD), reason);
    }
    private Game.Action turnOr(Direction current, Direction target, Game.Action action) {
        if (current == target) return action;
        return current.left() == target ? Game.Action.LEFT : Game.Action.RIGHT;
    }

    private Position firstStep(KnowledgeBase kb, Position start, Predicate<Position> goal) {
        Queue<Position> queue = new ArrayDeque<>();
        Map<Position, Position> parent = new HashMap<>();
        queue.add(start); parent.put(start, null);
        while (!queue.isEmpty()) {
            Position p = queue.remove();
            if (!p.equals(start) && goal.test(p)) {
                while (!parent.get(p).equals(start)) p = parent.get(p);
                return p;
            }
            for (Position next : p.neighbors(kb.size())) {
                if (kb.safe(next) && !parent.containsKey(next)) { parent.put(next, p); queue.add(next); }
            }
        }
        return null;
    }
}
