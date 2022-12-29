package story.scene;

public class BoringSceneException extends RuntimeException {

    public BoringSceneException() {
        super();
    }

    public BoringSceneException(String message) {
        super(message);
    }

    public BoringSceneException(String message, Throwable cause) {
        super(message, cause);
    }

    public BoringSceneException(Throwable cause) {
        super(cause);
    }

}
