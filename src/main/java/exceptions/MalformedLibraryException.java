package exceptions;

/**
 * Thrown when the underlying library is malformed.
 */
public class MalformedLibraryException extends RuntimeException {
    private final String library;

    public MalformedLibraryException(String library, Throwable t) {
        super(t);
        this.library = library;
    }
}
