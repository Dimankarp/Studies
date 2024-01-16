package manager;

import marine.structure.Chapter;
import marine.structure.Coordinates;
import marine.structure.SpaceMarine;
import marine.structure.Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.stream.Stream;

public class DBSpaceMarineRepository implements SpaceMarineRepository{

    private static final Logger logger = LoggerFactory.getLogger("manager.DBMarineRepo");
    private DBOperator dbOp;

    public void setOperator(DBOperator dbOp) {
        this.dbOp = dbOp;
    }

    public DBSpaceMarineRepository(DBOperator op) throws SQLException {
        dbOp = op;
        int changed = 0;
        changed = dbOp.executeUpdate((x) ->
                dbOp.getStatement("CREATE TABLE IF NOT EXISTS coordinates(" +
                                "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                                "x BIGINT," +
                                "y BIGINT)"));
        if(changed != 0){
            logger.warn("Couldn't find table \"coordinates\", so created it!");
            System.out.println("Couldn't find table \"coordinates\", so created it!");
        }
        changed = dbOp.executeUpdate((x) ->
                dbOp.getStatement("CREATE TABLE IF NOT EXISTS chapters(" +
                        "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                        "name VARCHAR(255)," +
                        "parent VARCHAR(255)," +
                        "count BIGINT," +
                        "world VARCHAR(255))"));
        if(changed !=0){
            logger.warn("Couldn't find table \"chapters\", so created it!");
            System.out.println("Couldn't find table \"chapters\", so created it!");
        }

        changed = dbOp.executeUpdate((x) ->
                dbOp.getStatement("CREATE TABLE IF NOT EXISTS users(" +
                        "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                        "nick VARCHAR(255) UNIQUE," +
                        "pass_hash BYTEA," +
                        "is_super BOOLEAN DEFAULT FALSE)"));

        if(changed !=0){
            logger.warn("Couldn't find table \"users\", so created it!");
            System.out.println("Couldn't find table \"users\", so created it!");
        }

        changed = dbOp.executeUpdate((x) ->
                dbOp.getStatement("CREATE TABLE IF NOT EXISTS marine(" +
                        "id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY," +
                        "name VARCHAR(255) NOT NULL," +
                        "creation_date DATE," +
                        "health BIGINT CHECK (health > 0)," +
                        "loyal BOOLEAN," +
                        "achievement TEXT," +
                        "coords BIGINT REFERENCES coordinates ON DELETE CASCADE," +
                        "weap_type BIGINT," +
                        "chapt BIGINT REFERENCES chapters ON DELETE CASCADE,"+
                        "owner_id BIGINT REFERENCES users ON DELETE RESTRICT)"));

        if(changed != 0){
            logger.warn("Couldn't find table \"marine\", so created it!");
            System.out.println("Couldn't find table \"marine\", so created it!");
        }

    }

    @Override
    public Stream<SpaceMarine> getMarines() throws IllegalStateException{
        try {
            ResultSet marines = dbOp.executeQuery((x) ->
                    dbOp.getStatement(
                            "SELECT marine.*," +
                                    "coordinates.x," +
                                    "coordinates.y," +
                                    "chapters.name AS chptName," +
                                    "chapters.parent AS chptParent," +
                                    "chapters.count AS chptCount," +
                                    "chapters.world AS chptWorld FROM marine " +
                                    "INNER JOIN coordinates ON marine.coords = coordinates.id " +
                                    "INNER JOIN chapters ON marine.chapt = chapters.id"));

            Stream.Builder<SpaceMarine> builder = Stream.builder();
            while (marines.next()) {
                try {
                    SpaceMarine marine = new SpaceMarine();
                    Chapter chpt = new Chapter();
                    Coordinates coords = new Coordinates();

                    chpt.setMarinesCount(marines.getInt("chptCount"));
                    chpt.setName(marines.getString("chptName"));
                    chpt.setParentLegion(marines.getString("chptParent"));
                    chpt.setWorld(marines.getString("chptWorld"));

                    coords.setX(marines.getInt("x"));
                    coords.setY(marines.getInt("y"));

                    marine.setId(marines.getInt("id"));
                    marine.setName(marines.getString("name"));
                    marine.setHealth(marines.getInt("health"));
                    marine.setAchievements(marines.getString("achievement"));
                    marine.setLoyal(marines.getBoolean("loyal"));
                    marine.setChapter(chpt);
                    marine.setCoordinates(coords);
                    marine.setWeaponType(Weapon.values()[marines.getInt("weap_type") - 1]);

                    builder.add(marine);

                } catch (Exception e) {
                    System.out.println(e);
                }

            }
            marines.close();
            return builder.build();
        }
        catch (SQLException e){
            logger.error("Couldn't get marines from the marine table, because of {}", e.toString());
            System.out.printf("Couldn't get marines from the marine tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't get marines from the marine tables!").initCause(e);
        }
    }

    @Override
    public Stream<Weapon> getUniqueWeapons() throws IllegalStateException {
        try {
            ResultSet uniqueWeaps = dbOp.executeQuery((x) ->
                    dbOp.getStatement(
                            "SELECT weap_type " +
                                    "FROM marine " +
                                    "GROUP BY weap_type"));
            Stream.Builder<Weapon> builder = Stream.builder();
            while (uniqueWeaps.next()) {
                try {
                    builder.add(Weapon.values()[uniqueWeaps.getInt("weap_type")-1]);

                } catch (Exception e) {
                    System.out.println(e);
                }

            }
            uniqueWeaps.close();
            return builder.build();
        }
        catch (SQLException e){
            logger.error("Couldn't get unique weapons from the marine table, because of {}", e.toString());
            System.out.printf("Couldn't get unique weapons from the marine tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't get unique weapons from the marine tables!").initCause(e);

        }
    }

    @Override
    public SpaceMarine getMarine(int id) throws IllegalArgumentException, IllegalStateException{
        try {
            ResultSet marines = dbOp.executeQuery((x) -> {
                    PreparedStatement statement = dbOp.getStatement(
                            "SELECT marine.*," +
                                    "coordinates.x," +
                                    "coordinates.y," +
                                    "chapters.name AS chptName," +
                                    "chapters.parent AS chptParent," +
                                    "chapters.count AS chptCount," +
                                    "chapters.world AS chptWorld FROM marine " +
                                    "INNER JOIN coordinates ON marine.coords = coordinates.id " +
                                    "INNER JOIN chapters ON marine.chapt = chapters.id " +
                                    "WHERE marine.id = ?");
                    statement.setInt(1, id);
                    return statement;
            });

            if(marines.next()) {
                try {
                    SpaceMarine marine = new SpaceMarine();
                    Chapter chpt = new Chapter();
                    Coordinates coords = new Coordinates();

                    chpt.setMarinesCount(marines.getInt("chptCount"));
                    chpt.setName(marines.getString("chptName"));
                    chpt.setParentLegion(marines.getString("chptParent"));
                    chpt.setWorld(marines.getString("chptWorld"));

                    coords.setX(marines.getInt("x"));
                    coords.setY(marines.getInt("y"));

                    marine.setId(marines.getInt("id"));
                    marine.setName(marines.getString("name"));
                    marine.setHealth(marines.getInt("health"));
                    marine.setAchievements(marines.getString("achievement"));
                    marine.setLoyal(marines.getBoolean("loyal"));
                    marine.setChapter(chpt);
                    marine.setCoordinates(coords);
                    marine.setWeaponType(Weapon.values()[marines.getInt("weap_type") - 1]);
                    return marine;

                } catch (Exception e) {
                    System.out.println(e);
                    throw  (RuntimeException) new RuntimeException("Couldn't create new marine!").initCause(e);
                }

            }
            else{
                marines.close();
                throw new IllegalArgumentException("Couldn't find a marine by the id.");
            }

        }
        catch (SQLException e){
            logger.error("Couldn't get marines from the marine table, because of {}", e.toString());
            System.out.printf("Couldn't get marines from the marine tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't get marine by id from the marine tables!").initCause(e);
        }
    }

    @Override
    public int getOwnerId(int id) throws IllegalArgumentException, IllegalStateException {
        try {
            ResultSet owner = dbOp.executeQuery((x) -> {
                PreparedStatement statement = dbOp.getStatement(
                        "SELECT owner_id " +
                                "FROM marine " +
                                "WHERE id = ?");
                statement.setInt(1, id);
                return statement;
            });
            if (owner.next()) {
                    return owner.getInt("owner_id");
            } else {
                owner.close();
                throw new IllegalArgumentException("Couldn't find the marine!");
            }
        } catch (SQLException e) {
            logger.error("Couldn't get owner from the marine table, because of {}", e.toString());
            System.out.printf("Couldn't owner from the marine tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't get owner from the marine tables!").initCause(e);
        }
    }

    @Override
    public void addMarine(SpaceMarine marine, User caller) throws IllegalArgumentException, IllegalStateException{

        try {
            dbOp.executeUpdate((x) -> {
                SpaceMarine currMarine = (SpaceMarine) x[0];
                PreparedStatement chapterStatement = dbOp.getStatement("INSERT INTO chapters(name, parent, count, world) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

                chapterStatement.setString(1, currMarine.getChapter().getName());
                chapterStatement.setString(2, currMarine.getChapter().getParentLegion());
                chapterStatement.setInt(3, 100);
                chapterStatement.setString(4, currMarine.getChapter().getWorld());

                chapterStatement.executeUpdate();

                ResultSet chaptKeys = chapterStatement.getGeneratedKeys();
                chaptKeys.next();
                int chaptId = chaptKeys.getInt(1);

                PreparedStatement coordsStatement = dbOp.getStatement("INSERT INTO coordinates(x, y) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);

                coordsStatement.setLong(1, currMarine.getCoordinates().getX());
                coordsStatement.setLong(2, currMarine.getCoordinates().getY());

                coordsStatement.executeUpdate();

                ResultSet coordsKeys = coordsStatement.getGeneratedKeys();
                coordsKeys.next();
                int coordsKey = coordsKeys.getInt(1);

                PreparedStatement marineStatement = dbOp.getStatement("INSERT INTO marine(name, creation_date, health, loyal, achievement, coords, weap_type, chapt, owner_id)" +
                        " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

                marineStatement.setString(1, currMarine.getName());
                marineStatement.setDate(2, new java.sql.Date(1, 1, 1));
                marineStatement.setInt(3, currMarine.getHealth());
                marineStatement.setBoolean(4, currMarine.getLoyal());
                marineStatement.setString(5, currMarine.getAchievements());
                marineStatement.setInt(6, coordsKey);
                marineStatement.setInt(7, currMarine.getWeaponType().ordinal() + 1);
                marineStatement.setInt(8, chaptId);
                marineStatement.setInt(9, caller.getId());

                return marineStatement;
            }, marine);
        } catch (SQLException e) {
            logger.error("Couldn't add marine to the marine table, because of {}", e.toString());
            System.out.printf("Couldn't add marine to the marine table because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't add marine to the marine table!").initCause(e);
        }
    }


    @Override
    public boolean addIfMin(SpaceMarine addedMarine, User caller) throws IllegalArgumentException, IllegalStateException {
        Iterator<SpaceMarine> marines = getMarines().iterator();
        while(marines.hasNext()){
            if(addedMarine.compareTo(marines.next()) > 0)return false;
        }
        addMarine(addedMarine, caller);
        return true;
    }

    public int getChapterId(int id) throws IllegalArgumentException, IllegalStateException {
        try {
            ResultSet owner = dbOp.executeQuery((x) -> {
                PreparedStatement statement = dbOp.getStatement(
                        "SELECT chapters.id " +
                                "FROM marine " +
                                "INNER JOIN chapters ON marine.chapt = chapters.id " +
                                "WHERE marine.id = ?");
                statement.setInt(1, id);
                return statement;
            });
            if (owner.next()) {
                return owner.getInt("id");
            } else {
                owner.close();
                throw new IllegalArgumentException("Couldn't find the marine!");
            }
        } catch (SQLException e) {
            logger.error("Couldn't get chapter from the marine table, because of {}", e.toString());
            System.out.printf("Couldn't chapter from the marine tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't get chapter from the marine tables!").initCause(e);
        }
    }

    public int getCoordinatesId(int id) throws IllegalArgumentException, IllegalStateException {
        try {
            ResultSet owner = dbOp.executeQuery((x) -> {
                PreparedStatement statement = dbOp.getStatement(
                        "SELECT coordinates.id " +
                                "FROM marine " +
                                "INNER JOIN coordinates ON marine.coords = coordinates.id " +
                                "WHERE marine.id = ?");
                statement.setInt(1, id);
                return statement;
            });
            if (owner.next()) {
                return owner.getInt("id");
            } else {
                owner.close();
                throw new IllegalArgumentException("Couldn't find the marine!");
            }
        } catch (SQLException e) {
            logger.error("Couldn't get coordinates from the marine table, because of {}", e.toString());
            System.out.printf("Couldn't coordinates from the marine tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't get coordinates from the marine tables!").initCause(e);
        }
    }

    @Override
    public void updateMarine(int id, SpaceMarine updatingMarine, User caller) throws IllegalAccessException, IllegalStateException {
        if(getOwnerId(id) == caller.getId() || caller.isSuperuser()){
            try {
                dbOp.executeUpdate((x) -> {
                    PreparedStatement marineStatement = dbOp.getStatement("UPDATE marine SET name=?, creation_date=?, health=?, loyal=?, achievement=?, coords=?, weap_type=?, chapt=?, owner_id=?)" +
                            " WHERE id=?");

                    marineStatement.setString(1, updatingMarine.getName());
                    marineStatement.setDate(2, new java.sql.Date(1, 1, 1));
                    marineStatement.setInt(3, updatingMarine.getHealth());
                    marineStatement.setBoolean(4, updatingMarine.getLoyal());
                    marineStatement.setString(5, updatingMarine.getAchievements());
                    marineStatement.setInt(6, getCoordinatesId(updatingMarine.getId()));
                    marineStatement.setInt(7, updatingMarine.getWeaponType().ordinal() + 1);
                    marineStatement.setInt(8, getChapterId(updatingMarine.getId()));
                    marineStatement.setInt(9, id);

                    return marineStatement;
                }, updatingMarine);
            } catch (SQLException e) {
                logger.error("Couldn't update marine in the marine table, because of {}", e.toString());
                System.out.printf("Couldn't update marine in the marine table because of %s.", e.toString());
                throw (IllegalStateException) new IllegalStateException("Couldn't update marine in the marine table!").initCause(e);
            }

        }
        else{
            throw new IllegalAccessException("You don't have permission to edit this Marine");
        }

    }

    @Override
    public void removeMarine(int id, User caller) throws IllegalAccessException {
        if(getOwnerId(id) == caller.getId() || caller.isSuperuser()){
            try {
                dbOp.executeUpdate((x) -> {
                    PreparedStatement marineStatement = dbOp.getStatement("DELETE FROM marine" +
                            " WHERE id=?");
                    marineStatement.setInt(1, id);
                    return marineStatement;
                });
            } catch (SQLException e) {
                logger.error("Couldn't remove marine from the marine table, because of {}", e.toString());
                System.out.printf("Couldn't remove marine from the marine table because of %s.", e.toString());
                throw (IllegalStateException) new IllegalStateException("Couldn't remove marine from the marine table!").initCause(e);
            }

        }
        else{
            throw new IllegalAccessException("You don't have permission to edit this Marine");
        }
    }

    @Override
    public void removeFirst(User caller) throws IllegalArgumentException, IllegalStateException, IllegalAccessException {
        Iterator<SpaceMarine> marines = getMarines().iterator();
        SpaceMarine minimalMarine;
        if(marines.hasNext()) minimalMarine = marines.next();
        else{
            logger.error("Couldn't get marine from the marine table, because of it's empty!");
            System.out.printf("Couldn't get marine from the marine table because of it's empty!");
            throw (IllegalStateException) new IllegalStateException("Couldn't get marine from the marine table!");
        }
        while (marines.hasNext()) {
            SpaceMarine comparedMaraine = marines.next();
            if (minimalMarine.compareTo(comparedMaraine) > 0) minimalMarine = comparedMaraine;
        }
        if(getOwnerId(minimalMarine.getId()) == caller.getId() || caller.isSuperuser()) {
            removeMarine(minimalMarine.getId(), caller);
        }
        else{
            throw new IllegalAccessException("You don't have permission to edit this Marine");
        }
    }

    @Override
    public void removeGreater(SpaceMarine comparable, User caller) throws IllegalArgumentException, IllegalStateException, IllegalAccessException {
        Iterator<SpaceMarine> marines = getMarines().iterator();
        while (marines.hasNext()) {
            SpaceMarine comparedMaraine = marines.next();
            if (comparable.compareTo(comparedMaraine) < 0){
                if(getOwnerId(comparedMaraine.getId()) == caller.getId() || caller.isSuperuser()) {
                    removeMarine(comparedMaraine.getId(), caller);
                }
                else{
                    throw new IllegalAccessException("You don't have permission to edit this Marine");
                }
            }
        }

    }


    @Override
    public void clear(User caller) throws IllegalAccessException {
        if(caller.isSuperuser()) {
            getMarines().forEach((x) -> {
                try {
                    removeMarine(x.getId(), caller);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Couldn't remove marine!");
                }
            });
        }
        else{
            throw new IllegalAccessException("You are not a superuser!");
        }

    }

    @Override
    public int getSumOfHealth() {
        try {
            ResultSet counts = dbOp.executeQuery((x) ->
                    dbOp.getStatement(
                            "SELECT SUM(health) " +
                                    "FROM marine"));

            counts.next();
            return counts.getInt(1);
        }
        catch (SQLException e){
            logger.error("Couldn't get sum of health of marines from the marine table, because of {}", e.toString());
            System.out.printf("Couldn't get sum of health of marines from the marine tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("get sum of health of marines from the marine tables!").initCause(e);

        }
    }

    @Override
    public int getAverageOfHealth() {
        try {
            ResultSet counts = dbOp.executeQuery((x) ->
                    dbOp.getStatement(
                            "SELECT AVG(health) " +
                                    "FROM marine"));

            counts.next();
            return counts.getInt(1);
        }
        catch (SQLException e){
            logger.error("Couldn't get average health of marines from the marine table, because of {}", e.toString());
            System.out.printf("Couldn't get average health of marines from the marine tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("get average health of marines from the marine tables!").initCause(e);

        }
    }

    @Override
    public int getMarinesCount() throws IllegalStateException{
        try {
            ResultSet counts = dbOp.executeQuery((x) ->
                    dbOp.getStatement(
                            "SELECT count(*) " +
                                    "FROM marine"));

                counts.next();
                return counts.getInt(1);
        }
        catch (SQLException e){
            logger.error("Couldn't count marines from the marine table, because of {}", e.toString());
            System.out.printf("Couldn't count marines from the marine tables because of %s.", e.toString());
            throw (IllegalStateException) new IllegalStateException("Couldn't count marines from the marine tables!").initCause(e);

        }
    }

    @Override
    public String toString(){
        return String.format("SpaceMarine repository implemented by DB with URL: %s", dbOp.getURL());
    }
}
