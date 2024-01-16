package mitya.haha.model;

public final class TargetBuilder {
    private double X_MAX = 2;
    private double X_MIN = -2;
    private double Y_MAX = 5;
    private double Y_MIN = -3;
    private double RADIUS_MAX = 2;
    private double RADIUS_MIN = 0;

    public TargetBuilder setYInterval(double yMax, double yMin){
        this.Y_MAX = yMax;
        this.Y_MIN = yMin;
        return this;
    }

    public TargetBuilder setXInterval(double xMax, double xMin){
        this.X_MAX = xMax;
        this.X_MIN = xMin;
        return this;
    }

    public TargetBuilder setRadiusInterval(double radiusMax, double radiusMin){
        this.RADIUS_MAX = radiusMax;
        this.RADIUS_MIN = radiusMin ;
        return this;
    }
    public Target buildTarget(){
        return new Target(X_MAX, X_MIN, Y_MAX, Y_MIN, RADIUS_MAX, RADIUS_MIN);
    }

}
