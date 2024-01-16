package story.ships;

import story.Colorer;
import story.Describable;
import story.characters.Creature;
import story.characters.Talkable;
import story.spells.Spell;

import java.util.ArrayList;

import static story.ArrayUtil.getRandomObjectFromArray;
import static story.Colorer.Colorize;
import static story.Colorer.getColorByPercent;

public class Ship implements Describable {

    private final String[] DEFAULT_NAMES = {"Корабль", "Посудина", "Корыто"};
    private Part[] parts;

    private Spell currentSpell;
    private Weapon[] weaponParts;
    private double maxHP;
    private String name;
    private Creature captain;

    public double getMaxHP() {
        return maxHP;
    }

    public String getName() {
        return name;
    }

    public Part[] getParts() {
        return parts;
    }

    public Part[] getWeaponParts() {
        return weaponParts;
    }

    public Creature getCaptain() {
        return captain;
    }


    public void assignCaptain(Creature captain) throws WrongCaptainException {
        setCaptain(captain);
    }

    public void setCaptain(Creature captain) {
        this.captain = captain;
    }

    public Spell getCurrentSpell() {
        return currentSpell;
    }

    public void setCurrentSpell(Spell spell) {
        currentSpell = spell;
    }

    public double getHP() {
        double total = 0;
        for (Part prt : parts) {
            total += prt.getPartPoints();
        }
        return total;
    }


    {
        parts = new Part[]{new Part(Materials.OAK, PartTypes.HULL)};
        weaponParts = new Weapon[0];
        maxHP = getHP();
    }

    public Ship() {
        this.name = (String) getRandomObjectFromArray(DEFAULT_NAMES);
    }

    public Ship(String name) {
        this.name = name;
    }

    public void setParts(Part[] prt) {
        assignParts(prt);
        maxHP = getHP();
    }

    public void assignParts(Part[] prt) {
        ArrayList<Part> wepParts = new ArrayList<>();
        parts = prt;
        for (Part item : prt) {
            if (item instanceof Weapon) {
                wepParts.add(item);
            }
        }
        weaponParts = wepParts.toArray(weaponParts);
    }

    public double getAttack() {
        double temp = 0;
        for (Part item : parts) {
            if (item instanceof Weapon) {
                temp += ((Weapon) item).getRandomizedAttack();
            }
        }
        return temp;
    }


    //Где-то моя архитектура подкачала...
    public String receiveDamageAndGetLog(double dmg) {
        Part[] hits = getHits(dmg);
        double dmgPart = dmg / hits.length;

        StringBuilder sb = new StringBuilder();
        sb.append(this.onDamageDescribe(dmg));

        for (Part prt : hits) {
            prt.applyDamage(dmgPart);
            sb.append(prt.onDamageDescribe(dmgPart));
            if (prt.getPartPoints() < 0.1E-6D) {
                sb.append(prt.onDestructionDescribe());
                if (captain != null && captain instanceof Talkable && Math.random() < 0.7) {
                    sb.append(((Talkable) captain).getMentioning(prt, "%s screams:\n \"Why did you destroy my %s\"\n "));
                }
            }

        }
        return sb.toString();
    }

    private void applyDamage(double dmg, Part[] prts) {
        for (Part prt : prts) {
            prt.applyDamage(dmg);
        }
    }

    private Part[] getHits(double dmg) {
        Part[] hparts = getHealthyParts();
        int tempRes = (int) Math.round(Math.pow(10D * dmg, 2D / 3D) / 50D);
        tempRes = tempRes == 0 ? 1 : tempRes;
        int hitNum = Math.min(tempRes, hparts.length);

        Part[] hitParts = new Part[hitNum];

        //Shuffling parts array
        Part[] shuffled = hparts.clone();
        Part temp = null;
        int randIndex;
        for (int i = 0; i < shuffled.length; i++) {
            temp = shuffled[i];
            randIndex = (int) Math.round(Math.random() * (shuffled.length - 1));
            shuffled[i] = shuffled[randIndex];
            shuffled[randIndex] = temp;
        }

        for (int i = 0; i < hitNum; i++) {
            hitParts[i] = shuffled[i];
        }
        return hitParts;

    }

    //Пришлось такое городить, потому что Коллекции я пока не знаю
    private Part[] getHealthyParts() {
        ArrayList<Part> hpart = new ArrayList<>();
        for (Part prt : parts) {
            if (prt.getPartPoints() > 0.1E-6D) {
                hpart.add(prt);
            }
        }
        Part[] prts = new Part[hpart.size()];
        prts = hpart.toArray(prts);
        return prts;
    }

    public String onDestructionDescribe() {
        return String.format("%s is sunk!\n", this.toString());
    }

    public String stateDescribe() {
        double percent = getHP() / getMaxHP();
        StringBuilder result = new StringBuilder(String.format("%s is at " + Colorize("%.2f%%", getColorByPercent(percent)) + " durability: \n", this.toString(), percent * 100));
        if (captain != null) {
            result.append("Current captain is: " + captain.toString() + '\n');
        }
        for (Part prt : parts) {
            result.append(prt.stateDescribe());
        }
        return result.toString();
    }

    public boolean isAlive() {
        return getHP() > maxHP * 0.1;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + Colorize(name, Colorer.Colors.BLUE_BOLD_BRIGHT);
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Ship)) {
            return false;
        }

        Ship p = (Ship) o;

        if (parts.length != p.parts.length) {
            return false;
        }
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] != p.parts[i]) {
                return false;
            }

        }
        if (!p.getCaptain().equals(this.captain)) {
            return false;
        }
        if (!p.getCurrentSpell().equals(this.currentSpell)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 50;
        hash += name.hashCode() + parts.hashCode() + captain.hashCode() + currentSpell.hashCode();
        return hash;

    }

}
