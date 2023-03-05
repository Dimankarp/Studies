package marine;

import java.io.Serializable;

public record CommandContainer(Command commandAnnotation, String[] basicArgs, Serializable[] objectArgs) implements Serializable{

    public CommandContainer{

        if(commandAnnotation == null) throw new IllegalArgumentException("Provided command annotation is null");

    }

}
