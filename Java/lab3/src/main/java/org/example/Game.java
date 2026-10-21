package org.example;

import java.util.ArrayList;
import java.util.List;

public final class Game {
    public enum Action { FORWARD, LEFT, RIGHT, SHOOT, GRAB }
    public enum Status { PLAYING, WON, LOST, STUCK }
    private final World world;
    private final KnowledgeBase knowledge;
    private final List<String> log = new ArrayList<>();
    private Position position;
    private Direction direction = Direction.NORTH;
    private Status status = Status.PLAYING;
    private boolean arrow = true;
    private boolean hasGold;
    private int steps;
    private Percept percept;

    public Game(World world) {
        this.world = world;
        position = new Position(world.size() - 1, 0);
        knowledge = new KnowledgeBase(world.size());
        percept = world.sense(position, false, false, false);
        knowledge.observeRoom(position, percept, position.equals(world.gold()));
        log.add("Старт " + position + ". " + percept);
    }

    public void act(Action action) {
        if (status != Status.PLAYING) return;
        boolean bump = false, scream = false;
        String message = "";
        steps++;
        switch (action) {
            case LEFT -> { direction = direction.left(); message = "Поворот налево."; }
            case RIGHT -> { direction = direction.right(); message = "Поворот направо."; }
            case FORWARD -> {
                Position next = position.move(direction);
                if (!next.inside(world.size())) { bump = true; message = "Стена: остались в " + position; }
                else {
                    position = next; message = "Переход в " + position;
                    if (world.lethal(position)) {
                        status = Status.LOST;
                        message += world.pitAt(position) ? ". Охотник упал в яму." : ". Охотника съел Вампус.";
                    }
                }
            }
            case SHOOT -> {
                if (!arrow) message = "Стрел больше нет.";
                else {
                    arrow = false;
                    scream = world.shoot(position, direction);
                    knowledge.shot(position, direction, scream);
                    message = scream ? "Выстрел. Слышен крик: Вампус убит!" : "Выстрел. Промах: крика нет.";
                }
            }
            case GRAB -> {
                knowledge.checkGold(position, position.equals(world.gold()), position.equals(world.gold()) && !world.wampusAlive());
                if (!position.equals(world.gold())) message = "В этой комнате нет золота.";
                else if (world.wampusAlive()) message = "Золото охраняется: сначала убейте Вампуса.";
                else { hasGold = true; status = Status.WON; message = "Золото взято. Победа!"; }
            }
        }
        percept = world.sense(position, bump, scream, hasGold);

        if (status != Status.LOST) knowledge.observeRoom(position, percept, !hasGold && position.equals(world.gold()));
        log.add(steps + ". " + message + " " + percept);
    }

    public LogicalAgent.Decision stepAgent(LogicalAgent agent) {
        if (status != Status.PLAYING) return new LogicalAgent.Decision(null, "Симуляция завершена.");
        LogicalAgent.Decision decision = agent.decide(knowledge, position, direction, percept, arrow);
        if (decision.action() == null) {
            status = Status.STUCK;
            log.add("Тупик. Симуляция завершена: " + decision.explanation());
        } else {
            act(decision.action());
        }
        return decision;
    }

    public World world() { return world; }
    public KnowledgeBase knowledge() { return knowledge; }
    public Position position() { return position; }
    public Direction direction() { return direction; }
    public Status status() { return status; }
    public boolean hasArrow() { return arrow; }
    public boolean hasGold() { return hasGold; }
    public int steps() { return steps; }
    public Percept percept() { return percept; }
    public List<String> log() { return List.copyOf(log); }
}
