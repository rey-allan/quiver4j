package exceptions;

/**
 * Thrown when the underlying content is malformed.
 */
public class MalformedContentException extends RuntimeException {
    private final String content;

    public MalformedContentException(String content, Throwable t) {
        super(t);
        this.content = content;
    }
}
