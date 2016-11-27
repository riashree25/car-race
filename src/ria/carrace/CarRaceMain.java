package ria.carrace;

import javax.swing.*;

public class CarRaceMain {

    public static void main(String[] args) {

        CarRace carRace = new CarRace();
        JFrame frame = new JFrame("Car Race");

        frame.getContentPane().add(carRace);
        frame.setSize(800, 800);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        carRace.init();
    }
}
