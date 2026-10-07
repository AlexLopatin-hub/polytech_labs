package org.example;

import org.example.internal.Config;
import org.example.internal.Environment;


public class Main {
    public static void main(String[] args) {
        Config config = new Config(
                5,
                5,
                100 / 6,
                100 / 12,
                100,
                -100,
                10e6
        );

        Environment env = new Environment(config);

        env.learn(5000);

        for (int i = 0; i < 100; i++) {
            System.out.println(env.maze);
            env.step();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}