package exceptions;

/**
 * Thrown when the underlying metadata is malformed.
 */
public class MalformedMetadataException extends RuntimeException {
    private final String metadata;

    public MalformedMetadataException(String metadata, Throwable t) {
        super(t);
        this.metadata = metadata;
    }
}
