package org.example.internal;

public record Config(
        int rows,
        int cols,
        int waterCount,
        int shockCount,
        double emptyReward,
        double waterReward,
        double shockReward,
        double cheeseReward
) {}
