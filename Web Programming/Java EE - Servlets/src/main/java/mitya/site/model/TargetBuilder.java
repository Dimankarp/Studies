package mitya.site.model;

public class TargetBuilder {
    private Integer[] X_ALLOWED_ARR  = { -4, -3, -2, -1, 0, 1, 2, 3, 4};
    private double Y_MAX = 5;
    private double Y_MIN = -5;
    private Integer[] RADIUS_ALLOWED_ARR = {1, 2, 3, 4};
    public  void setAllowedRadiuses(Integer[] allowedRadiuses) {
        RADIUS_ALLOWED_ARR = allowedRadiuses.clone();
    }

    public  void setAllowedX(Integer[] allowedX) {
        X_ALLOWED_ARR = allowedX.clone();
    }

    public  void setYMin(double yMin) {
        Y_MIN = yMin;
    }

    public  void setYMax(double yMax) {
        Y_MAX = yMax;
    }
    public Target buildTarget(){
        return new Target(RADIUS_ALLOWED_ARR.clone(), X_ALLOWED_ARR.clone(), Y_MIN, Y_MAX);
    }

}
