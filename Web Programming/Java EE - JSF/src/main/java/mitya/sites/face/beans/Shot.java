package mitya.sites.face.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

import java.io.Serializable;


@Named("shot")
@RequestScoped
public class Shot implements Serializable {
    private static final double DEFAULT_X = 0;
    private static final double DEFAULT_Y = 0;
    private static final double DEFAULT_RADIUS = 1;

    private double x =  DEFAULT_X;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    private double y = DEFAULT_Y;

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    private double radius = DEFAULT_RADIUS;

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Shot() {

    }

}
