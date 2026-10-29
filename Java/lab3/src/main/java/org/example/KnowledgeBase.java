package org.example;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class KnowledgeBase {
    private final int size;
    private final Map<Position, Percept> observations = new LinkedHashMap<>();
    private final Set<Position> noPit = new LinkedHashSet<>();
    private final Set<Position> noWampus = new LinkedHashSet<>();
    private final Set<Position> certainPits = new LinkedHashSet<>();
    private final Set<Position> possiblePits = new LinkedHashSet<>();
    private final Set<Position> wampusCandidates = new LinkedHashSet<>();
    private final Set<Position> deadWampusCandidates = new LinkedHashSet<>();
    private final Set<Position> goldCandidates = new LinkedHashSet<>();
    private boolean goldObserved;
    private boolean goldCollected;
    private final List<Set<Position>> pitClauses = new ArrayList<>();
    private boolean wampusDead;

    public KnowledgeBase(int size) {
        this.size = size;
        for (int r = 0; r < size; r++) for (int c = 0; c < size; c++) {
            wampusCandidates.add(new Position(r, c));
            goldCandidates.add(new Position(r, c));
        }
    }

    public void observe(Position p, Percept percept) {
        observations.put(p, percept);
        noPit.add(p); noWampus.add(p);
        List<Position> neighbors = p.neighbors(size);
        if (!goldCollected) {
            if (percept.glitter()) {
                goldObserved = true;
                goldCandidates.clear(); goldCandidates.add(p);
            } else goldCandidates.remove(p);
        }
        if (!percept.breeze()) noPit.addAll(neighbors);
        else pitClauses.add(new LinkedHashSet<>(neighbors));
        if (!wampusDead) {
            if (!percept.stench()) noWampus.addAll(neighbors);
            else wampusCandidates.retainAll(neighbors);
        }
        infer();
    }

    public void observeRoom(Position p, Percept percept, boolean goldHere) {
        observe(p, percept);
        if (!goldCollected) checkGold(p, goldHere, false);
    }

    public void shot(Position origin, Direction direction, boolean scream) {
        if (scream) {
            wampusDead = true;
            for (Position p = origin.move(direction); p.inside(size); p = p.move(direction)) {
                if (wampusCandidates.contains(p)) deadWampusCandidates.add(p);
            }
            if (deadWampusCandidates.size() == 1) noPit.addAll(deadWampusCandidates);
        } else for (Position p = origin.move(direction); p.inside(size); p = p.move(direction)) noWampus.add(p);
        infer();
    }

    private void infer() {
        wampusCandidates.removeAll(noWampus);
        if (!wampusDead) noPit.addAll(wampusCandidates.size() == 1 ? wampusCandidates : Set.of());
        certainPits.clear(); possiblePits.clear();
        for (Set<Position> clause : pitClauses) {
            Set<Position> remaining = new LinkedHashSet<>(clause);
            remaining.removeAll(noPit);
            possiblePits.addAll(remaining);
            if (remaining.size() == 1) certainPits.addAll(remaining);
        }

        wampusCandidates.removeAll(certainPits);
        if (!goldCollected) {
            goldCandidates.removeAll(certainPits);
            Set<Position> guards = wampusDead ? deadWampusCandidates
                    : knownWampus() == null ? Set.of() : Set.of(knownWampus());
            if (!guards.isEmpty()) {
                Set<Position> nearby = new LinkedHashSet<>();
                for (Position guard : guards) nearby.addAll(guard.neighbors(size));
                goldCandidates.retainAll(nearby);
            }
        }
        if (!wampusDead && wampusCandidates.size() == 1 && noPit.addAll(wampusCandidates)) infer();
    }

    public int size() { return size; }
    public void checkGold(Position p, boolean found, boolean collected) {
        if (found) {
            goldCandidates.clear(); goldCandidates.add(p);
            goldObserved = true; goldCollected = collected;
        } else goldCandidates.remove(p);
        Position gold = knownGold();
        if (gold != null) {
            noPit.add(gold); noWampus.add(gold);
        }
        infer();
    }
    public Position knownGold() {
        return goldObserved && !goldCollected && goldCandidates.size() == 1
                ? goldCandidates.iterator().next() : null;
    }
    public boolean possibleGold(Position p) {
        return (goldObserved || goldSearchKnown()) && !goldCollected && goldCandidates.contains(p);
    }
    public boolean goldSearchKnown() {
        return knownWampus() != null || !deadWampusCandidates.isEmpty();
    }
    public Position wampusLocation() {
        return wampusDead ? deadWampusCandidates.size() == 1 ? deadWampusCandidates.iterator().next() : null
                : knownWampus();
    }
    public String goldMark(Position p) {
        if ((!goldObserved && !goldSearchKnown()) || !goldCandidates.contains(p)) return "";
        if (goldCollected) return "G✓";
        return goldObserved ? "G" : "G?";
    }
    public boolean visited(Position p) { return observations.containsKey(p); }
    public Percept observation(Position p) { return observations.get(p); }
    public boolean wampusDead() { return wampusDead; }
    public boolean safe(Position p) {
        return noPit.contains(p) && (wampusDead || !wampusCandidates.contains(p));
    }
    public boolean certainDanger(Position p) {
        return certainPits.contains(p) || p.equals(knownWampus());
    }
    public double riskScore(Position p) {
        if (certainDanger(p)) return Double.POSITIVE_INFINITY;
        double pitRisk = noPit.contains(p) ? 0.0 : 0.2;
        if (!noPit.contains(p)) for (Set<Position> clause : pitClauses) {
            if (!clause.contains(p)) continue;
            long candidates = clause.stream().filter(candidate -> !noPit.contains(candidate)).count();
            if (candidates > 0) pitRisk = Math.max(pitRisk, 1.0 / candidates);
        }
        double wampusRisk = !wampusDead && wampusCandidates.contains(p)
                ? 1.0 / wampusCandidates.size() : 0.0;
        return 1.0 - (1.0 - pitRisk) * (1.0 - wampusRisk);
    }
    public Position knownWampus() {
        return !wampusDead && wampusCandidates.size() == 1 ? wampusCandidates.iterator().next() : null;
    }
    public String mark(Position p) {
        if (visited(p)) return "OK · посещено";
        if (certainPits.contains(p)) return "P!";
        if (p.equals(knownWampus())) return "W!";
        if (safe(p)) return "OK";
        List<String> marks = new ArrayList<>();
        if (possiblePits.contains(p)) marks.add("P?");
        if (!wampusDead && wampusCandidates.contains(p)) marks.add("W?");
        return marks.isEmpty() ? "?" : String.join(" ", marks);
    }

    public List<String> sentences() {
        List<String> result = new ArrayList<>();
        for (Map.Entry<Position, Percept> e : observations.entrySet()) {
            result.add("Ощущения охотника " + e.getKey() + ": " + e.getValue());
        }
        Set<Set<Position>> unique = new LinkedHashSet<>(pitClauses);
        for (Set<Position> clause : unique) result.add("Ветерок ⇒ хотя бы одна яма среди " + clause);
        result.add("Нет ям: " + noPit);
        result.add("Доказанные ямы: " + certainPits);
        if (goldObserved || goldSearchKnown()) result.add(goldCollected ? "Золото собрано в " + goldCandidates
                : "Золото " + (goldObserved ? "найдено в " : "возможно в ") + goldCandidates);
        result.add(wampusDead ? "Вампус мёртв: подтверждено криком. Положение: " + deadWampusCandidates
                : "Единственный Вампус находится среди " + wampusCandidates);
        return result;
    }
}
