package exceptions;

public class CollectionTaskException extends RuntimeException {
    public CollectionTaskException() {
        super();
    }

    public CollectionTaskException(String message) {
        super(message);
    }
}
