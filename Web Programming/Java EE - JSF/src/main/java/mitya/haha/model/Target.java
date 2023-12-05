package mitya.haha.model;


import java.util.Date;

public final class Target {

    private double X_MAX =5;
    private double X_MIN =-5;
    private double Y_MAX = 3;
    private double Y_MIN = -3;
    private double RADIUS_MAX = 4;
    private double RADIUS_MIN = 1;
    Target(double xMax, double xMin, double yMax, double yMin, double radiusMax, double radiusMin){
       this.X_MAX = xMax;
       this.X_MIN = xMin;
       this.Y_MAX = yMax;
       this.Y_MIN = yMin;
       this.RADIUS_MAX = radiusMax;
       this.RADIUS_MIN = radiusMin;
    }

    public ShotRecord processShot(ShotParams shot, Date timestamp, User user) throws IllegalArgumentException{
        if(!areCoordsValid(shot)) throw new IllegalArgumentException("Illegal coordinates provided for the target!");
        return new ShotRecord(shot.getX(), shot.getY(), shot.getRadius(), isHit(shot), timestamp, user);
    }

    public ShotRecord processShot(ShotParams shot, User user) throws IllegalArgumentException{
        return processShot(shot, new Date(), user);
    }
    private static boolean isHit(ShotParams shot){
        //Rectangle Check
        if (-shot.getRadius() <= shot.getX() && shot.getX() <= 0 && (double) -shot.getRadius() / 2 <= shot.getY() && shot.getY() <= 0) return true;
        //Triangle Check
       if (shot.getX() <= shot.getRadius()/2 && shot.getX() >= 0 && shot.getY() >= 0 && shot.getY() <= (-shot.getX() + (double) shot.getRadius() / 2)) return true;
        //Arc Check
       if (shot.getX() >= 0 && shot.getY() <= 0 && (shot.getX() * shot.getX() + shot.getY() * shot.getY() <= shot.getRadius() * shot.getRadius()/4)) return true;
        return false;
    }

    public boolean areCoordsValid(ShotParams shot) {
        if (shot.getX() < X_MIN || shot.getX() > X_MAX) return false;
        if (shot.getY() < Y_MIN || shot.getY() > Y_MAX) return false;
        if (shot.getRadius() < RADIUS_MIN || shot.getRadius() > RADIUS_MAX) return false;
        return true;
    }

}
