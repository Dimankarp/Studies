package story;

import story.characters.Creature;
import story.characters.Humanoid;
import story.characters.Races;
import story.characters.specs.Mother;
import story.characters.specs.Pirate;
import story.characters.specs.Student;
import story.scene.BoringSceneException;
import story.scene.FriendlyShipsException;
import story.scene.Scene;
import story.ships.*;
import story.ships.types.*;

import java.util.Arrays;

import static story.ArrayUtil.getRandomObjectFromArray;


public class Storyteller {

    private static final Class[] SHIP_CLASSES = new Class[]{Barque.class, Canoe.class, Frigate.class, Galleass.class, Galleon.class, Raft.class, Schooner.class, WineBarrel.class};
    private static final Class[] CAPTAINS_CLASSES = {Student.class, Pirate.class, Mother.class};

    //Грёбаный Reflection API... - Мог сделать на каких-нибудь enum'ах - но тогда бы вообще красоты не было
    public static void main(String[] args) {

        while (true) {
            Simulation.simulateBattle();
        }
    }


    static class Simulation {
        static class EntityGenerator<T> {
            final Class[] CLASS_ARRAY;

            EntityGenerator(Class[] arr) {
                CLASS_ARRAY = arr;
            }

            T getRandomEntity() {
                return generateRandomEntity((Class<T>) getRandomObjectFromArray(CLASS_ARRAY));
            }

            T generateRandomEntity(Class<T> type) {
                try {
                    return (T) type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    return null;
                }
            }

        }

        static private final EntityGenerator<Ship> shipGenerator;
        static private final EntityGenerator<Creature> captainGenerator;

        static {
            shipGenerator = new EntityGenerator<>(SHIP_CLASSES);
            captainGenerator = new EntityGenerator<>(CAPTAINS_CLASSES);
        }

        static public Ship getRandomActor() {
            Ship actor = shipGenerator.getRandomEntity();

            try {
                actor.assignCaptain(captainGenerator.getRandomEntity());
            } catch (WrongCaptainException e) {
                if (e instanceof MotherExpectedException) {
                    actor.setCaptain(new Mother());
                } else if (e instanceof GoblinExpectedException) {
                    actor.setCaptain(new Humanoid(Races.GOBLIN));
                }
            }
            return actor;
        }

        static public void simulateBattle() {
            Ship player = getRandomActor();
            while (player.isAlive()) {
                Ship npc = getRandomActor();
                Scene battleScene = new Scene(player, npc);
                try {
                    battleScene.Start();
                } catch (FriendlyShipsException e) {
                    Part[] newParts = Arrays.copyOf(player.getParts(), player.getParts().length + 1);
                    newParts[newParts.length - 1] = new Weapon(Materials.OAK, 20, 0.6, "Torch", 0.7);//У каждого матроса же есть факел? Почему бы его не использовать?
                    player.setParts(newParts);
                } catch (BoringSceneException e) {
                    break;
                }
            }


        }


    }
}

