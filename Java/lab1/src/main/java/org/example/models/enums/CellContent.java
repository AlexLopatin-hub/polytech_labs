package org.example.models.enums;

import org.example.models.agents.Agent;
import org.example.models.agents.Herbivore;
import org.example.models.agents.Plant;
import org.example.models.agents.Predator;

public enum CellContent {
    EMPTY, PLANT, HERBIVORE, PREDATOR, OUT_OF_BOUNDS;

    public static CellContent contentOf(Agent agent) {
        return switch (agent) {
            case Plant ignored -> CellContent.PLANT;
            case Herbivore ignored -> CellContent.HERBIVORE;
            case Predator ignored -> CellContent.PREDATOR;
            case null, default -> CellContent.EMPTY;
        };
    }
}

