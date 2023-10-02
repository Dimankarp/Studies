package mitya.site;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Shot {
	private static final Integer[] X_ALLOWED_ARR = { -4, -3, -2, -1, 0, 1, 2, 3, 4}; //Must be sorted for a cool hack to work!
	private static final double Y_MAX = 5;
	private static final double Y_MIN = -5;
	private static final Integer[] RADIUS_ALLOWED_ARR = {1, 2, 3, 4};
	
	private int xCoord;
	public int getX() {return xCoord;}
	private double yCoord;
	public double getY() {return yCoord;}
	private int radius;
	public int getRadius() {return radius;}
	private Date timeStamp;
	public Date getTimeStamp() {return timeStamp;}
	
	private boolean isHit;
	public boolean isHit() {return isHit;}
	
	public Shot(int x, double y, int radius, Date timestamp) throws IllegalArgumentException{
		isHit = isHit(x, y, radius);
		this.xCoord = x;
		this.yCoord = y;
		this.radius = radius;
		this.timeStamp = timestamp;
	}
		
	private static boolean isHit(int x, double y, int radius) throws IllegalArgumentException{
		if(!(Arrays.asList(X_ALLOWED_ARR).contains(x)))throw new IllegalArgumentException("Illegal xCoord!");
		if(!(Arrays.asList(RADIUS_ALLOWED_ARR).contains(radius)))throw new IllegalArgumentException("Illegal radius!");
		if(!(y <= Y_MAX && y >=Y_MIN))throw new IllegalArgumentException("Illegal yCoord!");
		
		//Rectangle Check
		if(-radius <= x && x <= 0 && -radius/2 <= y && y<= 0) return true;
	    //Triangle Check
	    if(-radius <= x && x <= 0 && 0<= y && y <= (0.5*x + radius/2)) return true;
	    //Arc Check
	    if(x>=0 && y>=0 && (x*x + y*y <= radius*radius)) return true;
	     return false;
	}
	
	
	
}

