package mitya.site.model;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public final class Shot {

	
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
	
	public Shot(int x, double y, int radius, boolean isHit, Date timestamp) throws IllegalArgumentException{
		this.isHit = isHit;
		this.xCoord = x;
		this.yCoord = y;
		this.radius = radius;
		this.timeStamp = timestamp;
	}
		

	
	
}

