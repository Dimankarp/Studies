package story.scene;

import story.Colorer;
import story.characters.Humanoid;
import story.characters.Talkable;
import story.ships.Part;
import story.ships.Ship;
import story.spells.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static story.Colorer.Colorize;
import static story.ConsoleUtility.*;
import static story.ArrayUtil.*;

public class Scene {

    private Ship playerShip;
    private Ship npcShip;

    public Scene(Ship player, Ship npc) {
        playerShip = player;
        npcShip = npc;
    }

    public void Start() throws BoringSceneException, FriendlyShipsException {
        if (playerShip.getWeaponParts().length == 0 && npcShip.getWeaponParts().length == 0) {
            throw new FriendlyShipsException();
        }

        int moveNum = 1;
        Ship attackingShip = playerShip;
        Ship defendingShip = npcShip;
        Ship tempShipHolder = null;
        Environment env = new Environment(getRandomObjectFromArray(EnvironmentType.values()));


        class Talker {
            private ArrayList<Talkable> talkers;

            public Talker(Talkable... talkers) {
                this.talkers = new ArrayList<>();
                Collections.addAll(this.talkers, talkers);
            }

            public void AddTalker(Talkable entity) {
                talkers.add(entity);
            }

            public String getLine() {
                if (talkers.size() < 1) return "\0";
                return getRandomObjectFromArray(talkers).getRandomSaying();
            }

        }
        Talker sceneTalker = new Talker();
        if (env.godsAttention > 0D) {
            Talkable talkableImpl = new Talkable() {
                static final String[] GODS_SAYINGS = {"THIS IS QUITE INTERESTING!", "MEH... I WANT REAL FUN!", "LET THE BLOODSHED BEGIN", "THE WINNER WILL GET THE GOD'S PRIZE"};

                @Override
                public String getRandomSaying() {
                    return String.format(Colorize("The Gods speak: %s", Colorer.Colors.YELLOW_UNDERLINED) + "\n", getRandomObjectFromArray(GODS_SAYINGS));
                }
            };
            sceneTalker.AddTalker(talkableImpl);
        }
        if (playerShip.getCaptain() instanceof Talkable cap) sceneTalker.AddTalker(cap);
        if (npcShip.getCaptain() instanceof Talkable cap) sceneTalker.AddTalker(cap);


        while (playerShip.isAlive() && npcShip.isAlive()) {
            if (moveNum > 50) {
                throw new BoringSceneException("The scene lasts more than 50 moves:" + playerShip.toString() + " vs " + npcShip.toString());
            }

            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                System.out.println("МЕНЯ ПРЕРВАЛИ!");
            }

            Clear();
            appendToMid(env.toString());
            appendToLeft(playerShip.stateDescribe());
            appendToRight(npcShip.stateDescribe());


            //Разбавим атмосферу фразами!
            if (Math.random() > 0.5) {
                appendToMid(sceneTalker.getLine());
            }


            Spell attSpell = attackingShip.getCurrentSpell();
            if (attSpell != null) {
                attSpell.onFinish(attackingShip, defendingShip);
                appendToMid(attSpell.onFinishDescribe(attackingShip.getCaptain()));
                attackingShip.setCurrentSpell(null);
            }
            if (Math.random() > 0.3 && attackingShip.getCaptain() instanceof Humanoid capHuman) {
                Spell[] spellPool = capHuman.getSpells();
                attackingShip.setCurrentSpell(spellPool[(int) Math.round(Math.random() * (spellPool.length - 1))]);
                attackingShip.getCurrentSpell().onCast(attackingShip, defendingShip);
                appendToMid(attackingShip.getCurrentSpell().onCastDescribe(attackingShip.getCaptain()));
            }
            Attack(attackingShip, defendingShip, env);


            tempShipHolder = attackingShip;
            attackingShip = defendingShip;
            defendingShip = tempShipHolder;
            PrintBuilders();
            env.changeWindSpeed();
            moveNum += 1;

            boolean aliveWeaponExists = false;
            for (Part wp : playerShip.getWeaponParts()) {
                if (wp.getPartPoints() > 0.1E-6D) {
                    aliveWeaponExists = true;
                    break;
                }
            }
            if (!aliveWeaponExists) {
                for (Part wp : npcShip.getWeaponParts()) {
                    if (wp.getPartPoints() > 0.1E-6D) {
                        aliveWeaponExists = true;
                        break;
                    }
                }
            }
            if (!aliveWeaponExists) {
                throw new AllGunsDestroyedException("The ships can't shoot now:" + playerShip.toString() + " vs " + npcShip.toString());
            }

        }

        if (!playerShip.isAlive()) {

            appendToMid(playerShip.onDestructionDescribe());
        }
        if (!npcShip.isAlive()) {
            appendToMid(npcShip.onDestructionDescribe());
        }
        PrintBuilders();

    }

    private void Attack(Ship att, Ship def, Environment currEnv) {
        double attack = att.getAttack() * currEnv.getMissCoeff(att, def) * currEnv.getDmgCoeff();
        if (attack < 3D) {
            appendToMid(String.format("%s attacks and misses!\n", att.toString()));
            return;
        }
        appendToMid(String.format("%s attacks %s\n", att.toString(), def.toString()));
        appendToMid(def.receiveDamageAndGetLog(attack));
    }


    private class Environment {
        private EnvironmentType baseType;
        private double windSpeed;
        private double godsAttention; //Простите, уж на что хватило фантазии! Фактически - множитель урона, зависящий от рас капитанов.

        public Environment(EnvironmentType baseType) {
            this.baseType = baseType;
            windSpeed = Math.random() * 0.5;
            godsAttention = 0;
            if (playerShip.getCaptain() instanceof Humanoid player && npcShip.getCaptain() instanceof Humanoid npc) {
                godsAttention = Math.abs(player.getRace().ordinal() - npc.getRace().ordinal());
            }
        }

        public void changeWindSpeed() {
            windSpeed += Math.random() * 0.1 - 0.05;
        }

        public double getMissCoeff(Ship def, Ship att) {
            return 1 - ((baseType.baseMissCoeff + windSpeed) / (def.getMaxHP() / att.getMaxHP() + baseType.baseMissCoeff + windSpeed));
        }

        public double getDmgCoeff() {
            return baseType.baseDamageCoeff + godsAttention;
        }

        public String toString() {
            return String.format("Location - %s \n " + Colorize("Wind: %.2f", Colorer.Colors.WHITE_UNDERLINED) + " " + Colorize("Gods's Attention: %.2f", Colorer.Colors.YELLOW_UNDERLINED) + "\n",
                    baseType.toString(), windSpeed, godsAttention);
        }

    }

    private enum EnvironmentType {

        INFINITE_FALL(0.2, 1.3, Colorer.Colors.YELLOW_BOLD, "Infinite Fall"),
        ASH_STORM(1.5, 2, Colorer.Colors.BLACK_BRIGHT, "Ash Storm"),
        CLEAR_SEA(0.16, 1, Colorer.Colors.BLUE_BOLD, "Clear Sea"),
        ICE_SHEET(0.01, 3, Colorer.Colors.BLUE_BRIGHT, "Ice Sheet"),
        SKY(2, 6, Colorer.Colors.WHITE_BRIGHT, "Sky");


        private double baseMissCoeff;
        private double baseDamageCoeff;
        private Colorer.Colors color;

        private String name;

        EnvironmentType(double missCoeff, double dmgCoeff, Colorer.Colors col, String name) {
            baseMissCoeff = missCoeff;
            baseDamageCoeff = dmgCoeff;
            color = col;
            this.name = name;
        }

        @Override
        public String toString() {
            return Colorize(name, color);
        }

        ;

    }


}
