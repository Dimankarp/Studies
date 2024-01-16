package marine;

import java.io.Serializable;
import java.util.Objects;

public record UserCreditContainer(byte[] nicknameBytes, byte[] passHashBytes) implements Serializable {

    public UserCreditContainer{
        Objects.requireNonNull(nicknameBytes);

    }

    public UserCreditContainer(byte[] nicknameBytes){
        this(nicknameBytes, null);
    }

}
