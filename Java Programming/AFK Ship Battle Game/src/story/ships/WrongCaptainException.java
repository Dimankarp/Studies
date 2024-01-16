package story.ships;

public class WrongCaptainException extends Exception{

    public WrongCaptainException() {
        super();
    }

    public WrongCaptainException(String message) {
        super(message);
    }

    public WrongCaptainException(String message, Throwable e) {
        super(message, e);
    }

    public WrongCaptainException(Throwable e) {
        super(e);
    }

}
