package mitya.site.model;

import java.util.Arrays;
import java.util.Date;

public final class Target {

    private final Integer[] X_ALLOWED_ARR;
    private final double Y_MAX;
    private final double Y_MIN;
    private final Integer[] RADIUS_ALLOWED_ARR;


    Target(Integer[] allowdRadiuses, Integer[] allowedX, double yMin, double yMax){
        X_ALLOWED_ARR = allowedX;
        RADIUS_ALLOWED_ARR = allowdRadiuses;
        Y_MAX = yMax;
        Y_MIN = yMin;
    }

    public Shot processShot(int x, double y, int radius, Date timestamp) throws IllegalArgumentException{
        if(!validateCoords(x, y, radius)) throw new IllegalArgumentException("Illegal coordinates provided for the target!");
        return new Shot(x, y, radius, isHit(x, y, radius), timestamp);
    }

    public Shot processShot(int x, double y, int radius) throws IllegalArgumentException{
        return processShot(x, y, radius, new Date());
    }
    private static boolean isHit(int x, double y, int radius){
        //Rectangle Check
        if (-radius <= x && x <= 0 && -radius / 2 <= y && y <= 0) return true;
        //Triangle Check
        if (-radius <= x && x <= 0 && 0 <= y && y <= (0.5 * x + radius / 2)) return true;
        //Arc Check
        if (x >= 0 && y >= 0 && (x * x + y * y <= radius * radius)) return true;
        return false;
    }

    private boolean validateCoords(int x, double y, int radius) {
        if (!(Arrays.asList(X_ALLOWED_ARR).contains(x))) return false;
        if (!(Arrays.asList(RADIUS_ALLOWED_ARR).contains(radius))) return false;
        if (!(y <= Y_MAX && y >= Y_MIN)) return false;
        return true;
    }

}
