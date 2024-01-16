package mitya.haha.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
public class ShotRecordDTO {

    private double x;
    private double y;
    private double radius;
    private boolean isHit;
    private Date timeStamp;

}
