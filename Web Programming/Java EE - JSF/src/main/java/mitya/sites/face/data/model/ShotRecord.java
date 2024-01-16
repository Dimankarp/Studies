package mitya.sites.face.data.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="records")
public final class ShotRecord implements Serializable, Comparable<ShotRecord> {
	private Long id;
	@Id
	@GeneratedValue
	public Long getId(){
		return id;
	}
	private void setId(Long id){this.id = id;}

	private double x;
	public double getX() {return x;}
	private void setX(double x){this.x = x;}

	private double y;
	public double getY() {return y;}
	private void setY(double y){this.y = y;}

	private double radius;
	public double getRadius() {return radius;}
	private void setRadius(double radius){this.radius = radius;}

	private Date timeStamp;
	@Column(name= "creation_date")
	public Date getTimeStamp() {return timeStamp;}
	private void setTimeStamp(Date timeStamp){this.timeStamp = timeStamp;}

	private boolean isHit;
	public boolean isHit() {return isHit;}
	private void setHit(boolean isHit){this.isHit = isHit;}

	private transient  User user;
	@ManyToOne(cascade = CascadeType.PERSIST)
	@JoinColumn(name = "user_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	@NotNull
	public User getUser(){return user;}
	private void setUser(User user){this.user = user;}


	public ShotRecord(){

	}
	public ShotRecord(double x, double y, double radius, boolean isHit, Date timestamp){
		this.isHit = isHit;
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.timeStamp = timestamp;
	}
	public ShotRecord(double x, double y, double radius, boolean isHit, Date timestamp, User user){
		this(x, y, radius, isHit, timestamp);
		this.user = user;
	}

	@Override
	public String toString() {
		return "ShotRecord{" +
				"Id=" + id +
				", x=" + x +
				", y=" + y +
				", radius=" + radius +
				", timeStamp=" + timeStamp +
				", isHit=" + isHit +
				'}';
	}


	@Override
	public int compareTo(ShotRecord o) {
		//Natural compare here is reversed for the data table.
		return -this.timeStamp.compareTo(o.timeStamp);
	}
}

