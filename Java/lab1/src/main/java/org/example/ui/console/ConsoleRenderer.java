package org.example.ui.console;

import org.example.models.agents.Agent;
import org.example.models.agents.Herbivore;
import org.example.models.agents.Plant;
import org.example.models.agents.Predator;
import org.example.models.environment.Environment;

public class ConsoleRenderer {

    private static final char EMPTY_SYMBOL = '.';

    public void render(Environment env) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < env.height; y++) {
            for (int x = 0; x < env.width; x++) {
                sb.append(symbolFor(env.getAgentByCoords(x, y))).append(' ');
            }
            sb.append('\n');
        }
        System.out.println(sb);
    }

    private char symbolFor(Agent agent) {
        return switch (agent) {
            case null -> EMPTY_SYMBOL;
            case Plant ignored -> 'P';
            case Herbivore ignored -> 'H';
            case Predator ignored -> 'X';
            default -> '?';
        };
    }
}
