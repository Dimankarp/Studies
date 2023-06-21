package marine.net;

import marine.Command;

import java.io.Serializable;
import java.util.Objects;

public record CommandContainer(Command commandAnnotation, String[] basicArgs, Serializable[] objectArgs, UserCreditContainer userCredit) implements Serializable{

    public CommandContainer{
        Objects.requireNonNull(commandAnnotation);
    }

}
