package org.example;

import org.example.models.environment.Config;
import org.example.models.environment.Environment;
import org.example.ui.console.ConsoleRenderer;
import org.example.ui.gui.MainFrame;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        Config config = new Config(
                1000,
                50,
                50,
                200,
                100,
                40
        );

        SwingUtilities.invokeLater(() -> new MainFrame(config).setVisible(true));

//        Environment env = new Environment(config);
//        ConsoleRenderer renderer = new ConsoleRenderer();
//
//        for (int t = 0; t < 100; t++) {
//            env.tick();
//            renderer.render(env);
//        }
    }
}
