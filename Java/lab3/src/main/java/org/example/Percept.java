package org.example;

public record Percept(boolean stench, boolean breeze, boolean glitter, boolean bump, boolean scream) {
    @Override public String toString() {
        return "(Stench=" + stench + ", Breeze=" + breeze + ", Glitter=" + glitter
                + ", Bump=" + bump + ", Scream=" + scream + ")";
    }
    public String russian() {
        StringBuilder s = new StringBuilder();
        if (stench) s.append("Запах  ");
        if (breeze) s.append("Ветерок  ");
        if (glitter) s.append("Блеск  ");
        if (bump) s.append("Удар о стену  ");
        if (scream) s.append("Крик Вампуса  ");
        return s.isEmpty() ? "Ничего не ощущается" : s.toString();
    }
}
