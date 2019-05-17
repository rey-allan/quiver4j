import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.MalformedMetadataException;
import exceptions.MalformedNotebookException;
import lombok.Data;

/**
 * Represents a collection of notes: a notebook.
 */
@Data
public class QuiverNotebook {
    private static final String META_FILE_NAME = "meta.json";

    /**
     * The unique identifier for this notebook.
     */
    private final String id;
    /**
     * The name of this notebook.
     */
    private final String name;
    /**
     * The total number of notes inside this notebook.
     */
    private final long numberOfNotes;
    /**
     * The notes inside this notebook.
     */
    private List<QuiverNote> notes;

    private final Path location;
    private final ObjectMapper mapper;

    public QuiverNotebook(Path location, ObjectMapper mapper) {
        this.location = location;
        this.mapper = mapper;

        Metadata metadata = loadMetadata();

        this.id = metadata.getUuid();
        this.name = metadata.getName();
        this.numberOfNotes = countNumberOfNotes();
    }

    /**
     * Lazily loads the notes inside this notebook.
     *
     * @return A list of {@link QuiverNote}.
     */
    public List<QuiverNote> getNotes() {
        if (null == notes) {
            notes = loadNotes();
        }

        return notes;
    }

    private Metadata loadMetadata() {
        Path metadataLocation = location.resolve(Paths.get(META_FILE_NAME));

        try {
            return mapper.readValue(metadataLocation.toFile(), Metadata.class);
        } catch (IOException e) {
            throw new MalformedMetadataException(metadataLocation.toString(), e);
        }
    }

    private long countNumberOfNotes() {
        try {
            return Files.list(location).filter(f -> f.toString().endsWith(".qvnote")).count();
        } catch (IOException e) {
            throw new MalformedNotebookException(location.toString(), e);
        }
    }

    private List<QuiverNote> loadNotes() {
        try {
            return Files.list(location)
                    .filter(f -> f.toString().endsWith(".qvnote"))
                    .map(f -> new QuiverNote(f, mapper))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new MalformedNotebookException(location.toString(), e);
        }
    }

    /**
     * Maps the metadata of this notebook from its json representation.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Metadata {
        private String uuid;
        private String name;
    }
}
