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
    private final Dimension dim            = new Dimension(800, 800);

    private final Car[]     enemies        = new Car[15];
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
        setSize(dim);
        setName("Car Race");
        initScreen();
        add(screen);
    }

    private void prepareResource() {

        Image enemyCar = new ImageIcon(getClass().getResource("enemy_car.png")).getImage();
        Image playerCar = new ImageIcon(getClass().getResource("player_car.png")).getImage();
        Image theCoin = new ImageIcon(getClass().getResource("coin.png")).getImage();

        MediaTracker mt = new MediaTracker(this);

        try {
            mt.addImage(enemyCar, 0);
            mt.addImage(playerCar, 1);
            mt.addImage(theCoin, 2);
            mt.waitForAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        image = createImage((int) dim.getWidth(), (int) dim.getHeight());
        gb = (Graphics2D) image.getGraphics();
        this.playerCar = new Car(playerCar, (int) dim.getWidth() / 2, (int) dim.getHeight() / 4 * 3, dim);

        for (int i = 0; i < coins.length; i++) {
            coins[i] = new Car(theCoin, 0, 0);
        }

        for (int i = 0; i < enemies.length; i++) {
            enemies[i] = new Car(enemyCar, 0, 0);
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
                Thread.sleep(30);
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
            int dy = (level == 1) ? 20 : (level == 2) ? 35 : 50;
            enemies[i].move(0, dy);
            enemies[i].draw(gb, screen);

            if (enemies[i].getY() > dim.getHeight()) {
                setObject(enemies, i);
            }
            checkCarCollision(enemies[i]);
        }

        for (int i = 0; i < coins.length; i++) {
            coins[i].move(0, 20);
            coins[i].draw(gb, screen);

            if (coins[i].getY() > dim.getHeight()) {
                setObject(coins, i);
            }
            checkCoinIntersection(coins[i]);
        }

        playerCar.draw(gb, screen);
        gs.drawImage(image, 0, 0, screen);

        Font font = new Font("cambria", Font.BOLD, 25);
        gs.setColor(Color.RED);
        gs.setFont(font);

        if (!loop) {
            gs.drawString("GAME OVER !!", 300, 300);
        }
    }

    private void drawRoad() {

        if (score < 1000) {
            level = 1;
            road += 20;
        } else if (score < 2000) {
            level = 2;
            road += 35;
        } else if (score < 3000) {
            level = 3;
            road += 50;
        }

        gb.setPaint(Color.yellow);
        gb.fillRect((int) dim.getWidth() / 2, road, 20, 150);
        gb.setFont(new Font("cambria", Font.BOLD, 25));
        gb.setColor(Color.RED);

        String msg = "Score - " + score + ", Level = " + level + ", Lives - " + (10 - noOfCollisions);
        gb.drawString(msg, 25, 25);

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
            playerCar.move(-50, 0);
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_RIGHT) {
            playerCar.move(50, 0);
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_UP) {
            playerCar.move(0, -50);
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_DOWN) {
            playerCar.move(0, 50);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
