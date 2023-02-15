package manager;
import manager.structure.SpaceMarine;
import manager.structure.Weapon;

import javax.management.InvalidAttributeValueException;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.*;

public class Storage {

    private final PriorityQueue<SpaceMarine> data;

    private PriorityQueue<SpaceMarine> getData(){
    lastAccessDate = new Date();
    return data;
    }
    final private Date creationDate;
    private Date lastAccessDate;
    private final FileOperator fileOp;

    public Storage(String PATH){
        data = new PriorityQueue<>();
        fileOp = new FileOperator(PATH);
        fetchStorage();
        creationDate = new Date();
        lastAccessDate = new Date();
    }

    private void fetchStorage(){

       try{
           SpaceMarine.SpaceMarineContainer container = (SpaceMarine.SpaceMarineContainer)fileOp.importFromXML();
           data.clear();
           Collections.addAll(data, container.arr);
       }
       catch (FileNotFoundException e){
           System.out.println(e.getMessage());
       }

    }

    public class CommandExecutor {
        private CommandInterpreter interpreter;

        public void setInterpreter(CommandInterpreter interpreter) {
            this.interpreter = interpreter;
        }

        @Command(
                name = "help",
                aliases = {"hlp", "?", "man"},
                desc = "help - shows list of available commands")
        public void help(String[] basicArgs, Object[] complexArgs) {
            System.out.println("Current commands are handled:");
            for (Method m : CommandExecutor.class.getDeclaredMethods()) {
                if (m.isAnnotationPresent(Command.class))
                    System.out.println(m.getAnnotation(Command.class).desc());
            }
        }

        @Command(
                name = "info",
                aliases = {"inf", "!", "data"},
                desc = "info - prints information about current collection (length, date of init etc.)"
        )
        public void info(String[] basicArgs, Object[] complexArgs) {
            System.out.printf("manager.Storage type: %s%n", data.getClass().getSimpleName());
            System.out.println("Element type: Space Marine");
            System.out.printf("Current Size: %d%n", data.size());
            System.out.printf("Creation Date: %s%n", creationDate.toString());
            System.out.printf("Last Access Date: %s%n", lastAccessDate.toString());
        }

        @Command(
                name = "exit",
                aliases = {"ext", "leave", "quit"},
                desc = "exit - exits program (without saving)")
        public void exit(String[] basicArgs, Object[] complexArgs) {
            System.exit(0);
        }

        @Command(
                name = "show",
                aliases = {"shw", ":", "look"},
                desc = "show - prints bio of every Space Marine contained in storage")
        public void show(String[] basicArgs, Object[] complexArgs) {
            System.out.println("The Storage contains:");
            for (Object marine : data.toArray()) System.out.println(marine.toString());
        }


        @Command(
                name = "add",
                aliases = {"+", "put", "enqueue"},
                desc = "add {SpaceMarine} - adds a new Space Marine to manager.Storage",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void add(String[] basicArgs, Object[] complexArgs) {
            getData().add((SpaceMarine) complexArgs[0]);
        }

        @Command(
                name = "update",
                aliases = {"updt", "^", "change"},
                desc = "update id {SpaceMarine} - updates attributes of the Marine with passed id with data of a passed SpaceMarine",
                basicArgsCount = 1,
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void update(String[] basicArgs, Object[] complexArgs) throws InvalidAttributeValueException {
            int id;
            try {
                id = Integer.parseInt(basicArgs[0]);
            } catch (Exception e) {
                throw new InvalidAttributeValueException("Passed id is not an Integer!");
            }
            getData().forEach(x -> {
                SpaceMarine marine = (SpaceMarine) x;
                if (marine.getId() == id) {
                    try {
                        SpaceMarine fieldHolder = (SpaceMarine) complexArgs[0];
                        marine.setName(fieldHolder.getName());
                        marine.setCoordinates(fieldHolder.getCoordinates());
                        marine.setHealth(fieldHolder.getHealth());
                        marine.setLoyal(fieldHolder.getLoyal());
                        marine.setAchievements(fieldHolder.getAchievements());
                        marine.setWeaponType(fieldHolder.getWeaponType());
                        marine.setChapter(fieldHolder.getChapter());
                        System.out.printf("Successfully update attributes of Space Marine id: %d%n", id);
                    } catch (Exception e) {
                        System.out.println("Something went terribly wrong!");
                    }
                }
            });
        }

        @Command(
                name = "remove_by_id",
                aliases = {"rm_id", "-id"},
                desc = "remove_by_id id - removes the Marine with passed id from Storage",
                basicArgsCount = 1)
        public void remove_by_id(String[] basicArgs, Object[] complexArgs) throws InvalidAttributeValueException {
            int id;
            try {
                id = Integer.parseInt(basicArgs[0]);
            } catch (Exception e) {
                throw new InvalidAttributeValueException("Passed id is not an Integer!");
            }
            getData().removeIf(x -> x.getId() == id);
            System.out.printf("Successfully removed marine with id: %d\n", id);
        }

        @Command(
                name = "clear",
                aliases = {"clr", "erase"},
                desc = "clear  - clears the Storage")
        public void clear(String[] basicArgs, Object[] complexArgs) {

            getData().clear();
            System.out.println("Successfully cleared the  Storage.");
        }

        @Command(
                name = "save",
                aliases = {"sve", "store", "export"},
                desc = "save - exports Storage to XML file")
        public void save(String[] basicArgs, Object[] complexArgs) {
            SpaceMarine.SpaceMarineContainer container = new SpaceMarine.SpaceMarineContainer();
            container.arr = getData().toArray(getData().toArray(new SpaceMarine[0]));
            fileOp.exportToXML(container);
            System.out.println("Successfully exported Storage.");
        }

        @Command(
                name = "execute_script",
                aliases = {"exec", "script", "sh"},
                desc = "execute_script file_name - interprets commands from provided file in PATH by lines",
                basicArgsCount = 1)
        public void execute_script(String[] basicArgs, Object[] complexArgs) throws InvalidAttributeValueException {
            try {
                Scanner fileScanner = fileOp.getScanner(basicArgs[0]);
                Scanner oldScanner = interpreter.getScanner();
                interpreter.setScanner(fileScanner);
                while (fileScanner.hasNextLine()) {
                    interpreter.interpreterCycle();
                }
                interpreter.setScanner(oldScanner);
                System.out.printf("Successfully executed %s script\n", basicArgs[0]);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }


        }

        @Command(
                name = "remove_first",
                aliases = {"rm_first", "-first"},
                desc = "remove_first - removes first Space Marine from the Storage")
        public void remove_first(String[] basicArgs, Object[] complexArgs) throws Exception {
            if (data.poll() == null) {
                throw new Exception("The Storage is empty!");
            }
            System.out.println("Successfully removed first Marine.");

        }

        @Command(
                name = "add_if_min",
                aliases = {"add_min", "if_min"},
                desc = "add_if_min {SpaceMarine} - adds new Space Marine to Storage if his rating is less, than current minimum",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void add_if_min(String[] basicArgs, Object[] complexArgs) {
            SpaceMarine marine = (SpaceMarine) complexArgs[0];
            if(marine.compareTo(data.peek()) < 0){
                getData().offer(marine);
                System.out.println("Successfully added new Space Marine to the Storage");
                return;
            }
            System.out.println("Provided Space Marine has a rating bigger, than current minimum");
        }

        @Command(
                name = "remove_greater",
                aliases = {"rm_greater", "rm_big"},
                desc = "remove_greater {SpaceMarine} - removes every Space marine that has bigger rating than passed one from Storage.",
                objectArgsCount = 1,
                objectArgsTypes = {SpaceMarine.class})
        public void remove_greater(String[] basicArgs, Object[] complexArgs) {
            SpaceMarine marine = (SpaceMarine) complexArgs[0];
            getData().removeIf(x -> (marine.compareTo(x) < 0));
            System.out.println("Successfully remove greater elements");
        }

        @Command(
                name = "sum_of_health",
                aliases = {"sum", "hp"},
                desc = "sum_of_health  - prints sum of health of every Marine in Storage")
        public void sum_of_health(String[] basicArgs, Object[] complexArgs) {
            int sumHp = 0;
            for (SpaceMarine marine : data) sumHp += marine.getHealth();
            System.out.printf("Sum of health: %d\n", sumHp);
        }

        @Command(
                name = "average_of_health",
                aliases = {"average", "avrg"},
                desc = "average_of_health - prints average of health of every Marine in Storage")
        public void average_of_health(String[] basicArgs, Object[] complexArgs) throws Exception {
            if(data.size() == 0) throw new Exception("The Storage is empty - can't calculate average");
            int sumHp = 0;
            for (SpaceMarine marine : data) sumHp += marine.getHealth();
            System.out.printf("Average of health: %.2f\n", (float)sumHp/data.size());
        }

        @Command(
                name = "print_unique_weapon_type",
                aliases = {"unique", "weapType"},
                desc = "print_unique_weapon_type - prints every unique weapon type of Marines in Storage")
        public void print_unique_weapon_type(String[] basicArgs, Object[] complexArgs) throws Exception {
            HashSet<Weapon> uniqueTypes = new HashSet<>();
            for (SpaceMarine marine : data) uniqueTypes.add(marine.getWeaponType());
            System.out.print("Unique weapons: ");
            for(Weapon weap : uniqueTypes)System.out.print(weap.name() + " ");
            System.out.println();

        }

    }
}
