package exceptions;

/**
 * Thrown when the underlying notebook is malformed.
 */
public class MalformedNotebookException extends RuntimeException {
    private final String notebook;

    public MalformedNotebookException(String notebook, Throwable t) {
        super(t);
        this.notebook = notebook;
    }
}
