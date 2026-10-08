package org.example;

import org.example.internal.Config;
import org.example.internal.Environment;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        Config config = new Config(
                7,
                7,
                12,
                6,
                -10,
                100,
                -100,
                10e6
        );

        SwingUtilities.invokeLater(() -> {
            Environment env = new Environment(config);
            MazeTrainerFrame frame = new MazeTrainerFrame(env);
            frame.setVisible(true);
        });
    }
}