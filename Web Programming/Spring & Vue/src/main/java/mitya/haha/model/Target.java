package mitya.haha.model;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class Target {

    private double X_MAX;
    private double X_MIN;
    private double Y_MAX;
    private double Y_MIN;
    private double RADIUS_MAX;
    private double RADIUS_MIN;

    private final static double X_STEP = 0.5d;
    private final static double RADIUS_STEP = 0.5d;

    private final List<Double> VALID_X;
    private final List<Double> VALID_RADIUS;

    Target(double xMax, double xMin, double yMax, double yMin, double radiusMax, double radiusMin) {
        this.X_MAX = xMax;
        this.X_MIN = xMin;
        this.Y_MAX = yMax;
        this.Y_MIN = yMin;
        this.RADIUS_MAX = radiusMax;
        this.RADIUS_MIN = radiusMin;

        VALID_X = new ArrayList<>();
        double currX = X_MIN;
        while(currX <= X_MAX){
            VALID_X.add(currX);
            currX+=X_STEP;
        }

        VALID_RADIUS = new ArrayList<>();
        double currRadius = RADIUS_MIN;
        while(currRadius <= RADIUS_MAX){
            VALID_RADIUS.add(currRadius);
            currRadius+=RADIUS_STEP;
        }


    }
    public ShotRecord processShot(ShotParams shot, Date timestamp, User user) throws IllegalArgumentException {
        if (!areCoordsValid(shot)) throw new IllegalArgumentException("Illegal coordinates provided for the target!");
        return new ShotRecord(shot.getX(), shot.getY(), shot.getRadius(), isHit(shot), timestamp, user);
    }

    public ShotRecord processShot(ShotParams shot, User user) throws IllegalArgumentException {
        return processShot(shot, new Date(), user);
    }

    private static boolean isHit(ShotParams shot) {
        //Rectangle Check
        if (-shot.getRadius() <= shot.getX() && shot.getX() <= 0 && -shot.getRadius() / 2 <= shot.getY() && shot.getY() <= 0)
            return true;
        //Triangle Check
        if (shot.getX() <= 2*shot.getY() + shot.getRadius() && shot.getX() >= 0 && shot.getY() <= 0 && shot.getY() >= (0.5 * shot.getX() - shot.getRadius() / 2))
            return true;
        //Arc Check
        if (shot.getX() >= 0 && shot.getY() >= 0 && (shot.getX() * shot.getX() + shot.getY() * shot.getY() <= shot.getRadius() * shot.getRadius()))
            return true;
        return false;
    }

    public boolean areCoordsValid(ShotParams shot) {
        if (VALID_X.stream().noneMatch((item)->Math.abs(shot.getX() - item) < 0.00001d)) return false;
        if (shot.getY() < Y_MIN || shot.getY() > Y_MAX) return false;
        if (VALID_RADIUS.stream().noneMatch((item)->Math.abs(shot.getRadius() - item) < 0.00001d)) return false;
        return true;
    }

}
