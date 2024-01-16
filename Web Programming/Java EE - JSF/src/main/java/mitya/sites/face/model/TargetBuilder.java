package mitya.sites.face.model;

public final class TargetBuilder {
    private double X_MAX =5;
    private double X_MIN =-5;
    private double Y_MAX = 3;
    private double Y_MIN = -3;
    private double RADIUS_MAX = 4;
    private double RADIUS_MIN = 1;

    public void setYInterval(double yMax, double yMin){
        this.Y_MAX = yMax;
        this.Y_MIN = yMin;
    }

    public void setXInterval(double xMax, double xMin){
        this.X_MAX = xMax;
        this.X_MIN = xMin;
    }

    public void setRadiusInterval(double radiusMax, double radiusMin){
        this.RADIUS_MAX = radiusMax;
        this.RADIUS_MIN = radiusMin ;
    }
    public Target buildTarget(){
        return new Target(X_MAX, X_MIN, Y_MAX, Y_MIN, RADIUS_MAX, RADIUS_MIN);
    }

}
