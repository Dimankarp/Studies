package marine.structure;

import jakarta.xml.bind.annotation.*;
import marine.net.User;

import javax.management.InvalidAttributeValueException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class representing a Space Marine, contained in Storage
 */
@XmlRootElement(name = "marine")
@XmlType(propOrder = {"id", "name", "coordinates", "creationDate", "health", "loyal", "achievements", "weaponType", "chapter"})
@XmlAccessorType(XmlAccessType.FIELD)
public class SpaceMarine implements Comparable<SpaceMarine>, Serializable {


    @Serial
    private static final long serialVersionUID = 400002L; //40K - get it?

    /**
     * Current Marine's id. Must be unique. Set automatically.
     */
    @XmlAttribute
    private int id;
    /**
     * The Date of creation of this Marine Object. Set automatically.
     */
    private java.util.Date creationDate;


    public void setId(int id) {
        this.id = id;
    }

    public void updateCreationDate(){
        creationDate = new Date();
    }

    public int getId() {
        return id;
    }


    public SpaceMarine() {

    }

    private User owner;

    public void setOwner(User owner){
        this.owner = owner;
    }
    public User getOwner() {
        return owner;
    }

    private String name;

    /**
     * Name setter used by user input
     *
     * <p>
     * Can't be null of an empty string
     * </p>
     *
     * @param name non-null and non-empty Space Marine name
     */
    @SetByUser(attributeName = "name")
    public void setName(String name) throws InvalidAttributeValueException {
        if (!name.equals("")) {
            this.name = name;
        } else {
            throw new InvalidAttributeValueException("The Name can't be just an empty string");
        }
    }

    public String getName() {
        return name;
    }


    /**
     * Current marine's position
     */
    private Coordinates coordinates;

    /**
     * Coordinate setter for current marine's position
     *
     * @param coords current marine's position
     */
    @SetByUser(attributeName = "coordinates", isComplex = true)
    public void setCoordinates(Coordinates coords) {
        this.coordinates = coords;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    private int health;

    /**
     * Regular health setter.
     */
    public void setHealth(int health) throws InvalidAttributeValueException {
        if (health > 0) {
            this.health = health;
        } else {
            throw new InvalidAttributeValueException("The marine can't already be dead!");
        }
    }

    /**
     * Health setter used by user input.
     *
     * @param health a string representing positive integer
     */
    @SetByUser(attributeName = "health")
    public void setHealth(String health) throws InvalidAttributeValueException {
        try {
            setHealth(Integer.parseInt(health));
        } catch (Exception e) {
            {
                if (e instanceof InvalidAttributeValueException) throw e;

                throw new InvalidAttributeValueException("Marine's health should be expressed with an integer.");
            }
        }

    }

    public int getHealth() {
        return health;
    }

    private boolean loyal;

    /**
     * Regular Loyal setter
     */
    public void setLoyal(boolean state) {
        this.loyal = state;
    }

    /**
     * Loyality setter used by user input
     *
     * @param loyal a string equal to one of 2 states: "true" or "false" in any case.
     */
    @SetByUser(attributeName = "loyal")
    public void setLoyal(String loyal) throws InvalidAttributeValueException {
        if (loyal.equalsIgnoreCase("true") || loyal.equalsIgnoreCase("false")) {
            setLoyal(Boolean.parseBoolean(loyal));
        } else {
            throw new InvalidAttributeValueException("The marine's loyalness should be expressed with true/false");
        }

    }

    public boolean getLoyal() {
        return loyal;
    }

    private String achievements;

    /**
     * Achievement setter used by user input
     *
     * @param achievements marine's accomplishments
     */
    @SetByUser(attributeName = "achievements")
    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }

    public String getAchievements() {
        return achievements;
    }

    private Weapon weaponType;

    /**
     * Regular Weapon setter
     */
    public void setWeaponType(Weapon wt) {
        this.weaponType = wt;
    }

    /**
     * Weapon setter used by user input
     *
     * @param weapType type of Weapon wielded by this marine, one of the WeaponType enum constants
     */
    @SetByUser(attributeName = "weaponType", isEnum = true, enumClass = Weapon.class)
    public void setWeaponType(String weapType) throws InvalidAttributeValueException {
        try {
            setWeaponType(Weapon.valueOf(weapType));
        } catch (Exception e) {
            throw new InvalidAttributeValueException("Choose an existing Weapon Type.");
        }
    }

    public Weapon getWeaponType() {
        return weaponType;
    }

    private Chapter chapter;

    /**
     * Chapter Setter used by user input
     *
     * @param chpt a name of the chapter this marine is asigned to.
     */
    @SetByUser(attributeName = "chapter", isComplex = true)
    public void setChapter(Chapter chpt) {
        this.chapter = chpt;
    }

    public Chapter getChapter() {
        return chapter;
    }

    @Override
    public int compareTo(SpaceMarine b) {
        return getRating() - b.getRating();
    }

    /**
     * A method for calculation Marine's battle rating.
     *
     * <p>
     * Used most dominantly in comparing to Marines together.
     * The formula for this calculation is:
     * <p>
     * if marine is loyal:
     * <p>
     * rating = 10 * weapin.dmg * health
     * <p>
     * if marine is disloyal:
     * <p>
     * rating = 2 * weapin.dmg * health
     *
     * </p>
     */
    public int getRating() {

        int rating = loyal ? 10 : 2;
        if (weaponType != null) rating *= weaponType.getDmg();
        return rating * health;
    }

    @Override
    public String toString() {
        return String.format("Brother %s - id: %d, hp: %d, weapon: %s, chapter: %s, location: %s, isLoyal: %b", name, id, health, weaponType.getName(), chapter.toString(), coordinates.toString(), loyal);
    }

    /**
     * A Wrapper container for Space Marines used in Serializing arrays of marines
     */
    @XmlRootElement(name = "marines")
    @XmlType(propOrder = {"arr"})
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SpaceMarineContainer {

        @XmlElement(name = "marine")
        public SpaceMarine[] arr;

        public SpaceMarineContainer(SpaceMarine[] arr) {
            this.arr = arr;
        }

        /**
         * Blank constructor for JAXB serialization/deserialization
         */
        public SpaceMarineContainer() {
        }

    }


}
