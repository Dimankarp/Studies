package manager;

import marine.structure.SpaceMarine;
import marine.structure.Weapon;

import java.util.stream.Stream;

public interface SpaceMarineRepository {

    Stream<SpaceMarine> getMarines() throws IllegalStateException;

    Stream<Weapon> getUniqueWeapons() throws IllegalStateException;

    SpaceMarine getMarine(int id) throws IllegalArgumentException, IllegalStateException;

    int getOwnerId(int id) throws IllegalArgumentException, IllegalStateException;

    void addMarine(SpaceMarine addedMarine, User caller) throws IllegalArgumentException, IllegalStateException;

    boolean addIfMin(SpaceMarine addedMarine, User caller) throws IllegalArgumentException, IllegalStateException;
    void updateMarine(int id, SpaceMarine updatingMarine, User caller) throws IllegalArgumentException, IllegalStateException, IllegalAccessException;

    void removeMarine(int id, User caller) throws IllegalArgumentException, IllegalStateException, IllegalAccessException;

    void removeFirst(User caller) throws IllegalArgumentException, IllegalStateException, IllegalAccessException;

    void removeGreater(SpaceMarine comparable, User caller) throws IllegalArgumentException, IllegalStateException, IllegalAccessException;
    void clear(User caller) throws  IllegalStateException, IllegalAccessException;

    int getSumOfHealth() throws  IllegalStateException;

    int getAverageOfHealth() throws  IllegalStateException ;

    int getMarinesCount() throws  IllegalStateException ;





}
