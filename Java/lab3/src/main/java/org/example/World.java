package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class World {
    private final int size;
    private final Set<Position> pits;
    private final Position wampus, gold;
    private boolean wampusAlive = true;
    public World(int size, Set<Position> pits, Position wampus, Position gold) {
        if (size < 4 || size > 12) throw new IllegalArgumentException("Размер: от 4 до 12.");
        Position start = new Position(size - 1, 0);
        if (!wampus.inside(size) || !gold.inside(size) || wampus.equals(start) || gold.equals(start)
                || !wampus.neighbors(size).contains(gold) || pits.contains(wampus)
                || pits.contains(gold) || pits.contains(start) || pits.stream().anyMatch(p -> !p.inside(size)))
            throw new IllegalArgumentException("Некорректное расположение объектов.");
        this.size = size; this.pits = Set.copyOf(pits); this.wampus = wampus; this.gold = gold;
    }
    public static World demo() {
        return new World(4, Set.of(new Position(0, 3), new Position(1, 2), new Position(3, 2)),
                new Position(1, 0), new Position(1, 1));
    }
    public static World random(int size) {
        return random(size, System.currentTimeMillis());
    }
    public static World random(int size, long seed) {
        Random random = new Random(seed);
        Position start = new Position(size - 1, 0);
        List<Position> available = new ArrayList<>();
        for (int r = 0; r < size; r++) for (int c = 0; c < size; c++) {
            Position p = new Position(r, c);

            if (!p.equals(start) && !start.neighbors(size).contains(p)) available.add(p);
        }
        Collections.shuffle(available, random);
        Position wampus = available.getFirst();
        List<Position> options = new ArrayList<>(wampus.neighbors(size));
        options.remove(start);
        Position gold = options.get(random.nextInt(options.size()));
        available.remove(wampus); available.remove(gold);
        Collections.shuffle(available, random);
        return new World(size, new HashSet<>(available.subList(0, (int) Math.round(size * size * 0.20))), wampus, gold);
    }
    public int size() { return size; }
    public boolean pitAt(Position p) { return pits.contains(p); }
    public Position wampus() { return wampus; }
    public Position gold() { return gold; }
    public boolean wampusAlive() { return wampusAlive; }
    public int pitCount() { return pits.size(); }
    public boolean lethal(Position p) { return pitAt(p) || (wampusAlive && wampus.equals(p)); }
    public Percept sense(Position p, boolean bump, boolean scream, boolean hasGold) {
        List<Position> neighbors = p.neighbors(size);
        return new Percept(wampusAlive && neighbors.contains(wampus), neighbors.stream().anyMatch(pits::contains),
                !hasGold && (gold.equals(p) || neighbors.contains(gold)), bump, scream);
    }

    public boolean shoot(Position origin, Direction direction) {
        for (Position p = origin.move(direction); p.inside(size); p = p.move(direction)) {
            if (wampusAlive && p.equals(wampus)) { wampusAlive = false; return true; }
        }
        return false;
    }
}
