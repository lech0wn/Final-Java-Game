package main;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, FontFormatException {
        JFrame window = new JFrame("Tetris");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        MainMenu mainMenu = new MainMenu(window);
        window.add(mainMenu);
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
}