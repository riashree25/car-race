package ria.carrace;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;

class Car {

    private int       x;
    private int       y;
    private Image     img;
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

    int getHeight() {
        return img.getHeight(null);
    }

    void draw(Graphics g, ImageObserver observer) {
        g.drawImage(img, x, y, observer);
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

    Rectangle2D getRectangle() {
        return new Rectangle2D.Float(x, y, getWidth(), getHeight());
    }

    boolean intersects(Car car) {
        return getRectangle().intersects(car.getRectangle());
    }

    boolean intersects(int x, int y) {
        return getRectangle().intersects(x, y, getWidth(), getHeight());
    }
}
