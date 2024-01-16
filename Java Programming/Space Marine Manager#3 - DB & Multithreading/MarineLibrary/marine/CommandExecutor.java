package marine;

import marine.structure.SpaceMarine;
import javax.management.InvalidAttributeValueException;


public abstract class CommandExecutor {
    protected CommandInterpreter interpreter;

    public void setInterpreter(CommandInterpreter interpreter) {
        this.interpreter = interpreter;
    }

    @Command(
            name = "help",
            aliases = {"hlp", "?", "man"},
            desc = "help - shows list of available commands")
   abstract public void help(String[] basicArgs, Object[] complexArgs);
    @Command(
            name = "info",
            aliases = {"inf", "!", "data"},
            desc = "info - prints information about current collection (length, date of init etc.)"
    )
   abstract public void info(String[] basicArgs, Object[] complexArgs);

    @Command(
            name = "exit",
            aliases = {"ext", "leave", "quit"},
            desc = "exit - exits program (without saving)")
    abstract public void exit(String[] basicArgs, Object[] complexArgs);

    @Command(
            name = "show",
            aliases = {"shw", ":", "look"},
            desc = "show - prints bio of every Space Marine contained in storage")
    abstract public void show(String[] basicArgs, Object[] complexArgs);


    @Command(
            name = "add",
            aliases = {"+", "put", "enqueue"},
            desc = "add {SpaceMarine} - adds a new Space Marine to manager.Storage",
            objectArgsCount = 1,
            objectArgsTypes = {SpaceMarine.class})
    abstract public void add(String[] basicArgs, Object[] complexArgs);

    @Command(
            name = "update",
            aliases = {"updt", "^", "change"},
            desc = "update id {SpaceMarine} - updates attributes of the Marine with passed id with data of a passed SpaceMarine",
            basicArgsCount = 1,
            objectArgsCount = 1,
            objectArgsTypes = {SpaceMarine.class})
    abstract public void update(String[] basicArgs, Object[] complexArgs) throws InvalidAttributeValueException;

    @Command(
            name = "remove_by_id",
            aliases = {"rm_id", "-id"},
            desc = "remove_by_id id - removes the Marine with passed id from Storage",
            basicArgsCount = 1)
    abstract public void remove_by_id(String[] basicArgs, Object[] complexArgs) throws Exception;

    @Command(
            name = "clear",
            aliases = {"clr", "erase"},
            desc = "clear  - clears the Storage")
    abstract public void clear(String[] basicArgs, Object[] complexArgs);

    /*
    As this command is no longer in use by Client,
    I can't just leave it in the abstract class - COMMENTING IT!

    @Command(
            name = "save",
            aliases = {"sve", "store", "export"},
            desc = "save - exports Storage to XML file")
    abstract public void save(String[] basicArgs, Object[] complexArgs);
   */
    @Command(
            name = "execute_script",
            aliases = {"exec", "script", "sh"},
            desc = "execute_script file_name - interprets commands from provided file in PATH by lines",
            basicArgsCount = 1)
    abstract public void execute_script(String[] basicArgs, Object[] complexArgs) throws Exception;

    @Command(
            name = "remove_first",
            aliases = {"rm_first", "-first"},
            desc = "remove_first - removes first Space Marine from the Storage")
    abstract public void remove_first(String[] basicArgs, Object[] complexArgs) throws Exception;

    @Command(
            name = "add_if_min",
            aliases = {"add_min", "if_min"},
            desc = "add_if_min {SpaceMarine} - adds new Space Marine to Storage if his rating is less, than current minimum",
            objectArgsCount = 1,
            objectArgsTypes = {SpaceMarine.class})
    abstract public void add_if_min(String[] basicArgs, Object[] complexArgs);
    @Command(
            name = "remove_greater",
            aliases = {"rm_greater", "rm_big"},
            desc = "remove_greater {SpaceMarine} - removes every Space marine that has bigger rating than passed one from Storage.",
            objectArgsCount = 1,
            objectArgsTypes = {SpaceMarine.class})
    abstract public void remove_greater(String[] basicArgs, Object[] complexArgs) throws Exception;
    @Command(
            name = "sum_of_health",
            aliases = {"sum", "hp"},
            desc = "sum_of_health  - prints sum of health of every Marine in Storage")
    abstract public void sum_of_health(String[] basicArgs, Object[] complexArgs);
    @Command(
            name = "average_of_health",
            aliases = {"average", "avrg"},
            desc = "average_of_health - prints average of health of every Marine in Storage")
    abstract public void average_of_health(String[] basicArgs, Object[] complexArgs) throws Exception;

    @Command(
            name = "print_unique_weapon_type",
            aliases = {"unique", "weapType"},
            desc = "print_unique_weapon_type - prints every unique weapon type of Marines in Storage")
    abstract public void print_unique_weapon_type(String[] basicArgs, Object[] complexArgs) throws Exception;

}
