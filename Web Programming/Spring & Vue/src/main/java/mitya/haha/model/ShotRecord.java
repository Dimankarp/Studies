package mitya.haha.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString
@EqualsAndHashCode
@Entity
@Table(name="records")
public final class  ShotRecord implements Serializable, Comparable<ShotRecord> {

    private Long id;
    @Id
    @GeneratedValue
    public Long getId(){
        return id;
    }
    private void setId(Long id){this.id = id;}

    @Getter
    @Setter
    private double x;

    @Getter
    @Setter
    private double y;

    @Getter
    @Setter
    private double radius;

    @Getter
    @Setter
    private boolean isHit;

    private Date timeStamp;
    @Column(name= "creation_date")
    public Date getTimeStamp() {return timeStamp;}
    private void setTimeStamp(Date timeStamp){this.timeStamp = timeStamp;}

    @JsonIgnore
    private transient User user;
    @ManyToOne()
    @JoinColumn(name = "user_id")
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
    public int compareTo(ShotRecord o) {
        //Natural compare here is reversed for the data table.
        return -this.timeStamp.compareTo(o.timeStamp);
    }
}

