package marine.structure;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import javax.management.InvalidAttributeValueException;
import java.io.Serializable;

/**
 * Represents a Chapter of Space Marines (analog to cohorts of HRE)
 *  <p>
 *      Each Chapter consists of around 1000 marines and attached
 *      to a particular Legion.
 *  </p>
 *
 * @author Mitya Ha-ha
 */
@XmlRootElement(name = "chapter")
@XmlType(propOrder = { "name", "parentLegion", "marinesCount", "world"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Chapter implements Serializable {

    private String name;

    @SetByUser(attributeName = "name")
    public void setName(String nm) throws InvalidAttributeValueException {
        if (!nm.equals("")) {
            this.name = nm;
        } else {
            throw new InvalidAttributeValueException("Name can't be just an empty string.");
        }
    }

    private String parentLegion;

    @SetByUser(attributeName = "parentLegion", canBeNull = true)
    public void setParentLegion(String parLeg) {
        this.parentLegion = parLeg;
    }

    private int marinesCount;

    public void setMarinesCount(int marinesCount) throws InvalidAttributeValueException {
        if (marinesCount > 0 && marinesCount <= 1000) {
            this.marinesCount = marinesCount;
        } else {
            throw new InvalidAttributeValueException("There should be at least 1 marine and less than a 1000 of them.");
        }

    }
    @SetByUser(attributeName = "marinesCount")
    public void setMarinesCount(String marinesCount) throws InvalidAttributeValueException {
        try {
            setMarinesCount(Integer.parseInt(marinesCount));
        } catch (Exception e) {
            if (e instanceof InvalidAttributeValueException) throw e;

            throw new InvalidAttributeValueException("The number of marines should be defined with an integer.");
        }
    }


    private String world;

    @SetByUser(attributeName = "world")
    public void setWorld(String world) {
        this.world = world;
    }

    @Override
    public String toString() {
        return String.format("Chpt. %s of %s Legion - %d  men", name, parentLegion, marinesCount);
    }
}
