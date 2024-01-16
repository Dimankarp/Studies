package story.ships;

import story.ships.WrongCaptainException;

public class GoblinExpectedException extends WrongCaptainException {

    public GoblinExpectedException() {
        super();
    }

    public GoblinExpectedException(String message) {
        super(message);
    }

}
