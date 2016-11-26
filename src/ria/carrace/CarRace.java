package ria.carrace;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

/**
 * @author Ria Shree
 */
public class CarRace extends Applet implements KeyListener, Runnable {

    private int             noOfCollisions = 0;
    private int             level          = 1;
    private long            score          = 0;
    private boolean         loop           = true;
    private final Dimension dim            = new Dimension(500, 600);

    private final Car[]     enemies        = new Car[20];
    private final Car[]     coins          = new Car[10];
    private final Random    rnd            = new Random();

    private int             road;
    private Car             playerCar;
    private Image           buff;
    private Canvas          screen;
    private Thread          game;
    private Graphics2D      gs;
    private Graphics2D      gb;

    @Override
    public void init() {

        prepareResource();
        setBackground(Color.blue);
        initScreen();
        add(screen);

        Button start = new Button("START");
        add(start);

        Button restart = new Button("RESTART");
        add(restart);

        start.addActionListener(actionListener -> {

            screen.requestFocus();
            game.start();
        });

        restart.addActionListener(actionListener -> {

            stop();
            screen.requestFocus();
            init();
        });
    }

    private void prepareResource() {

        Image imgRed = new ImageIcon(getClass().getResource("red_car.gif")).getImage();
        Image imgBlue = new ImageIcon(getClass().getResource("blue_car.gif")).getImage();
        Image imgGreen = new ImageIcon(getClass().getResource("green_car.gif")).getImage();
        Image coin_image = new ImageIcon(getClass().getResource("coin.gif")).getImage();

        MediaTracker mt = new MediaTracker(this);

        try {
            mt.addImage(imgRed, 0);
            mt.addImage(imgBlue, 1);
            mt.addImage(imgGreen, 2);
            mt.addImage(coin_image, 3);
            mt.waitForAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        buff = createImage((int) dim.getWidth(), (int) dim.getHeight());
        gb = (Graphics2D) buff.getGraphics();
        playerCar = new Car(imgRed, 250, 250, dim);

        for (int i = 0; i < coins.length; i++) {
            coins[i] = new Car(coin_image, 0, 0);
        }
        for (int i = 0; i < 10; i++) {
            enemies[i] = new Car(imgBlue, 0, 0);
        }
        for (int i = 10; i < enemies.length; i++) {
            enemies[i] = new Car(imgGreen, 0, 0);
        }

        for (int i = 0; i < enemies.length; i++) {
            setObject(enemies, i);
        }
        for (int i = 0; i < coins.length; i++) {
            setObject(coins, i);
        }
        game = new Thread(this);
    }

    @Override
    public void stop() {
        loop = false;
    }

    @Override
    public void run() {

        while (loop) {

            score += 1;
            drawScreen();

            try {
                Thread.sleep(40);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initScreen() {

        screen = new Canvas() {
            @Override
            public void paint(Graphics g) {
                if (gs == null) {
                    gs = (Graphics2D) screen.getGraphics();
                }
                drawScreen();
            }
        };

        screen.setSize(dim);
        screen.addKeyListener(this);
    }

    private void setObject(Car[] object, int en) {

        int x, y;
        next: while (true) {

            x = rnd.nextInt((int) dim.getWidth() - object[en].getWidth());
            y = -rnd.nextInt(5000) - 200;

            for (int j = 0; j < object.length; j++) {
                if (j != en && object[j].intersects(x, y)) {
                    continue next;
                }
            }

            object[en].setLocation(x, y);
            break;
        }
    }

    private void check(Car en) {

        if (playerCar.intersects(en)) {

            noOfCollisions++;
            score -= 50;

            if (playerCar.getX() > en.getX()) {
                en.move(-20, 0);
                playerCar.move(20, 0);
            } else {
                en.move(20, 0);
                playerCar.move(-20, 0);
            }
            if (noOfCollisions >= 10) {
                stop();
            }
        }
    }

    private void checkCoin(Car en) {

        if (playerCar.intersects(en)) {
            noOfCollisions++;
            score += 50;
            en.setLocation(0, 0);
        }
    }

    private synchronized void drawScreen() {

        gb.clearRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());
        gb.setPaint(new Color(100, 100, 100));
        gb.fillRect(0, 0, (int) dim.getWidth(), (int) dim.getHeight());

        drawRoad();

        for (int i = 0; i < enemies.length; i++) {
            enemies[i].move(0, 20 * level);
            enemies[i].draw(gb, screen);
            if (enemies[i].getY() > dim.getHeight()) {
                setObject(enemies, i);
            }
            check(enemies[i]);
        }

        for (int i = 0; i < coins.length; i++) {
            coins[i].move(0, 15);
            coins[i].draw(gb, screen);
            if (coins[i].getY() > dim.getHeight()) {
                setObject(coins, i);
            }
            checkCoin(coins[i]);
        }

        playerCar.draw(gb, screen);
        gs.drawImage(buff, 0, 0, screen);

        if ((score > 500 && score < 600)) {

            String b = "LEVEL 1 COMPLETE !!";
            gs.setColor(Color.black);

            Font small = new Font("Helvetica", Font.BOLD, 20);

            gs.setFont(small);
            gs.drawString(b, 250, 100);
        } else if ((score > 1000) && (score < 1100)) {

            String b = "LEVEL 2 COMPLETE !!";
            gs.setColor(Color.black);

            Font small = new Font("Helvetica", Font.BOLD, 20);

            gs.setFont(small);
            gs.drawString(b, 250, 100);
        } else {

            String b = "";
            gs.setColor(Color.black);

            Font small = new Font("Helvetica", Font.BOLD, 20);

            gs.setFont(small);
            gs.drawString(b, 250, 100);
        }

        if (!loop) {

            String a = "GAME OVER !!";
            gs.setColor(Color.black);

            Font small = new Font("Helvetica", Font.BOLD, 20);

            gs.setFont(small);
            gs.drawString(a, 110, 50);
        }
    }

    private void drawRoad() {

        if (score == (500 * level)) {
            level++;
        }
        road += level * 20;

        gb.setPaint(Color.yellow);
        gb.fillRect((int) dim.getWidth() / 2, road, 20, 150);
        gb.setFont(new Font("cambria", Font.BOLD, 25));
        gb.setColor(Color.RED);
        gb.drawString(Long.toString(score), 50, 50);

        if (road >= dim.getHeight()) {
            road = -150;
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {

        if (ke.getKeyCode() == KeyEvent.VK_LEFT) {
            playerCar.move(-20, 0);
        } else if (ke.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerCar.move(20, 0);
        } else if (ke.getKeyCode() == KeyEvent.VK_UP) {
            playerCar.move(0, -20);
        } else if (ke.getKeyCode() == KeyEvent.VK_DOWN) {
            playerCar.move(0, 20);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {}

    @Override
    public void keyTyped(KeyEvent ke) {}
}
