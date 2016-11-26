package ria.carrace;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

/**
 * @author Ria Shree
 */
class Car {

    private int       x;
    private int       y;
    private final Image     img;
    private Dimension dim;

    Car(Image img, int x, int y) {

        this.img = img;
        this.x = x;
        this.y = y;
    }

    Car(Image img, int x, int y, Dimension dim) {

        this(img, x, y);
        this.dim = dim;
    }

    void draw(Graphics g, ImageObserver observer) {
        g.drawImage(img, x, y, observer);
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int getWidth() {
        return img.getWidth(null);
    }

    private int getHeight() {
        return img.getHeight(null);
    }

    private Rectangle2D getRectangle() {
        return new Rectangle2D.Float(x, y, getWidth(), getHeight());
    }

    void move(int dx, int dy) {

        x += dx;
        y += dy;

        if (dim != null) {

            if (x < 0) {
                x = 0;
            }

            if (x + getWidth() > dim.getWidth()) {
                x = (int) dim.getWidth() - getWidth();
            }
        }
    }

    boolean intersects(Car car) {
        return getRectangle().intersects(car.getRectangle());
    }

    boolean intersects(int x, int y) {
        return getRectangle().intersects(x, y, getWidth(), getHeight());
    }
}
