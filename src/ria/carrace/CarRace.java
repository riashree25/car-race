package ria.carrace;

import javax.swing.*;
import java.applet.Applet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class CarRace extends Applet implements KeyListener, Runnable {

    private int             noOfCollisions = 0;
    private long            score          = 0;
    private int             level          = 1;
    private boolean         loop           = true;
    private final Dimension dim            = new Dimension(500, 500);

    private final Car[]     enemies        = new Car[20];
    private final Car[]     coins          = new Car[10];
    private final Random    random         = new Random();

    private int             road;
    private Car             playerCar;
    private Image           image;
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
    }

    private void prepareResource() {

        Image imgRed = new ImageIcon(getClass().getResource("red_car.gif")).getImage();
        Image imgGreen = new ImageIcon(getClass().getResource("green_car.gif")).getImage();
        Image coinImage = new ImageIcon(getClass().getResource("coin.gif")).getImage();

        MediaTracker mt = new MediaTracker(this);

        try {
            mt.addImage(imgRed, 0);
            mt.addImage(imgGreen, 1);
            mt.addImage(coinImage, 2);
            mt.waitForAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        image = createImage((int) dim.getWidth(), (int) dim.getHeight());
        gb = (Graphics2D) image.getGraphics();
        playerCar = new Car(imgGreen, 250, 250, dim);

        for (int i = 0; i < coins.length; i++) {
            coins[i] = new Car(coinImage, 0, 0);
        }

        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = new Car(imgRed, 0, 0);
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
                Thread.sleep(50);
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

    private void setObject(Car[] object, int index) {

        int x, y;
        next: while (true) {

            x = random.nextInt((int) dim.getWidth() - object[index].getWidth());
            y = -random.nextInt(5000) - 200;

            for (int j = 0; j < object.length; j++) {
                if (j != index && object[j].intersects(x, y)) {
                    continue next;
                }
            }

            object[index].setLocation(x, y);
            break;
        }
    }

    private void checkCarCollision(Car car) {

        if (playerCar.intersects(car)) {

            noOfCollisions++;
            score -= 50;

            if (playerCar.getX() > car.getX()) {
                car.move(-20, 0);
                playerCar.move(20, 0);
            } else {
                car.move(20, 0);
                playerCar.move(-20, 0);
            }

            if (noOfCollisions >= 10) {
                stop();
            }
        }
    }

    private void checkCoinIntersection(Car coin) {

        if (playerCar.intersects(coin)) {
            score += 50;
            coin.setLocation(0, 0);
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
            checkCarCollision(enemies[i]);
        }

        for (int i = 0; i < coins.length; i++) {
            coins[i].move(0, 15);
            coins[i].draw(gb, screen);
            if (coins[i].getY() > dim.getHeight()) {
                setObject(coins, i);
            }
            checkCoinIntersection(coins[i]);
        }

        playerCar.draw(gb, screen);
        gs.drawImage(image, 0, 0, screen);

        Font font = new Font("Helvetica", Font.BOLD, 20);
        gs.setColor(Color.black);
        gs.setFont(font);

        if ((score > 500 && score < 600)) {
            gs.drawString("LEVEL 1 COMPLETE !!", 250, 100);
        } else if ((score > 1000) && (score < 1100)) {
            gs.drawString("LEVEL 2 COMPLETE !!", 250, 100);
        } else {
            gs.drawString("", 250, 100);
        }

        if (!loop) {
            gs.drawString("GAME OVER !!", 110, 50);
        }
    }

    private void drawRoad() {

        if (score < 500) {
            level = 1;
        } else if (score < 1000) {
            level = 2;
        } else if (score < 1500) {
            level = 3;
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
    public void keyPressed(KeyEvent keyEvent) {

        if (keyEvent.getKeyCode() == KeyEvent.VK_SPACE) {
            screen.requestFocus();
            game.start();
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            stop();
            screen.requestFocus();
            init();
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_LEFT) {
            playerCar.move(-30, 0);
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerCar.move(30, 0);
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
            playerCar.move(0, -30);
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            playerCar.move(0, 30);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
