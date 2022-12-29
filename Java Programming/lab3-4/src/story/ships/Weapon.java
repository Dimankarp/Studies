package story.ships;

import java.lang.reflect.ParameterizedType;

public class Weapon extends Part {

    private double attack;
    private double accuracy;

    public double getAttack() {
        return attack;
    }

    public double getAccuracy() {
        return accuracy;
    }

    private double attackBuff;
    private double accuracyBuff;

    public void setAttackBuff(double buff) {
        attackBuff = buff;
    }

    public void setAccuracyBuff(double buff) {
        accuracyBuff = buff;
    }

    public double getRandomizedAttack() {
        if (Math.random() < (accuracy + accuracyBuff)) {
            return (attack + attackBuff) * getDurability() * (1 - Math.pow(Math.random(), 2));
        }
        return 0;
    }

    public Weapon(Materials mat, double att, double acc) {
        super(mat, PartTypes.CANON_ROW);
        attack = att;
        accuracy = acc;
    }

    public Weapon(Materials mat, double att, double acc, double scale) {
        super(mat, PartTypes.CANON_ROW, scale);
        attack = att;
        accuracy = acc;
    }

    public Weapon(Materials mat, double att, double acc, String optName) {
        super(mat, PartTypes.CANON_ROW, optName);
        attack = att;
        accuracy = acc;
    }

    public Weapon(Materials mat, double att, double acc, String optName, double scale) {
        super(mat, PartTypes.CANON_ROW, optName, scale);
        attack = att;
        accuracy = acc;
    }


}
