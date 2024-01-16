package marine.structure;

import java.io.Serializable;

/**
 * Class representing one of the Warhammer's 40K weapons.
 *
 * <p>
 * Each Weapon is defined by its name and damage. My favourite
 * is surely the Grav-Gun - so iconic.
 * </p>
 * @author Mitya Ha-ha
 *
 */
public enum Weapon implements Serializable {
    /**
     * Just a regular gun that shoots flames. Pretty popular.
     */
    FLAMER(1, "Flamer"),
    /**
     * A weapon that uses gravitational fields for dealing damage.
     */
    GRAV_GUN(2, "Grav-Gun"),
    /**
     * An upgraded version of a good-old Flamer.
     */
    HEAVY_FLAMER(3, "Heavy Flamer"),
    /**
     * Space Marine's heaviest weapon, deals significant damage to the vehicles.
     */
    MISSILE_LAUNCHER(4, "Missile Launcher");

    private final int dmg;
    public int getDmg(){return dmg;}

    private final String name;
    public String getName(){return name;}


    /**
     * @param dmg - a damage this weapon deals
     * @param nm - simple, grammatically correct name of the weapon
     */
     Weapon(int dmg, String nm){
        this.dmg = dmg;
        this.name = nm;
    }


}
