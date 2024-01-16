package story.ships;

import static story.Colorer.*;

import story.Describable;

public class Part implements Describable {
    private double damage;
    private final Materials material;
    private final PartTypes type;
    private double maxHealth;

    public double getMaxHealth() {
        return maxHealth;
    }

    private double scale;
    private String name;

    public double getPartPoints() {
        return type.getCoeff() * material.getDurability() * getDurability() * scale;
    }


    protected double getDurability() {
        return 1 - damage / maxHealth;
    }

    public Part(Materials mat, PartTypes type) {
        damage = 0;
        material = mat;
        this.type = type;
        scale = 1D;
        maxHealth = type.getCoeff() * material.getDurability();
        name = type.toString();

    }

    public Part(Materials mat, PartTypes type, String optName) {
        this(mat, type);
        name = optName;
    }

    public Part(Materials mat, PartTypes type, double scale) {
        this(mat, type);
        this.scale = scale;
        maxHealth = type.getCoeff() * material.getDurability() * scale;
    }

    public Part(Materials mat, PartTypes type, String optName, double scale) {
        this(mat, type);
        name = optName;
        this.scale = scale;
        maxHealth = type.getCoeff() * material.getDurability() * scale;
    }

    public void applyDamage(double dmg) {
        if (dmg + damage >= maxHealth) {
            damage = maxHealth;
        } else if (dmg + damage < 0) {
            damage = 0;
        } else {
            damage += dmg;
        }
    }

    private double normalizeDamage(double dmg) {
        if (Math.abs(dmg) > maxHealth) {
            return maxHealth;
        }
        return dmg;
    }

    public String onDestructionDescribe() {
        return String.format("%s is " + Colorize("destroyed", Colors.WHITE_UNDERLINED) + "!\n", this.toString());
    }

    public String onDamageDescribe(double dmg) {
        if (dmg >= 0) {
            return String.format("%s is damaged by %.2f%%!\n", this.toString(), normalizeDamage(dmg) / maxHealth * 100);
        } else {
            return String.format("%s is healed by %.2f%%!\n", this.toString(), normalizeDamage(dmg) / maxHealth * 100);
        }


    }

    public String stateDescribe() {
        double hpPercent = (1 - damage / maxHealth);
        return String.format("%s - " + Colorize("%.2f%%", getColorByPercent(hpPercent)) + "\n", this.toString(), hpPercent * 100);
    }

    @Override
    public String toString() {
        return material.toString() + " " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Part)) {
            return false;
        }

        Part p = (Part) o;

        return damage == p.damage &&
                material == p.material &&
                type == p.type;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash += (int) damage * 100 + material.hashCode() + type.hashCode();
        return hash;

    }


}
