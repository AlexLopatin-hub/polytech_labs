package org.example.models.environment;

public record Config(
        int tickTime,
        int width,
        int height,
        int plantsCount,
        int herbivoresCount,
        int predatorsCount
) {}
