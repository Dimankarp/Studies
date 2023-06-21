package marine.structure;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import javax.management.InvalidAttributeValueException;
import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a Position of Space Marine on the Planet (in the World)
 *
 *  <p>
 *      Position is defined by integer x and y coordinates.
 *  </p>
 *
 * @author Mitya Ha-ha
 *
 */
@XmlRootElement(name = "coordinates")
@XmlType(propOrder = { "x", "y"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Coordinates implements Serializable {

    @Serial
    private static final long serialVersionUID = 500001L;

    private long x;

    public long getX() {
        return x;
    }


    @SetByUser(attributeName = "x")
    public void setX(String strX) throws InvalidAttributeValueException {
        try {
            this.x = Long.parseLong(strX);
        } catch (Exception e) {
            throw new InvalidAttributeValueException("X coordinate must be an integer!");
        }
    }


    public void setX(long x) {
        this.x = x;
    }

    private Integer y;

    public Integer getY() {
        return y;
    }


    @SetByUser(attributeName = "y")
    public void setY(String strY) throws InvalidAttributeValueException {
        try {
            this.y = Integer.parseInt(strY);
        } catch (Exception e) {
            throw new InvalidAttributeValueException("Y coordinate must be an integer!");
        }
    }

    public void setY(Integer y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", x, y);
    }
}
