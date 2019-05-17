import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.MalformedLibraryException;
import exceptions.MalformedMetadataException;
import lombok.Data;

/**
 * Represents the top-level collection of notebooks: a library.
 */
@Data
public class QuiverLibrary {
    private static final String META_FILE_NAME = "meta.json";

    /**
     * The unique identifier for this library.
     */
    private final String id;
    /**
     * The total number of notebooks inside this library.
     */
    private final int numberOfNotebooks;
    /**
     * The notebooks inside this library.
     */
    private List<QuiverNotebook> notebooks;

    private final Path location;
    private final ObjectMapper mapper;

    // A convenient constructor for users that don't want to manage Jackson themselves
    public QuiverLibrary(Path location) {
        this(location, new ObjectMapper());
    }

    public QuiverLibrary(Path location, ObjectMapper mapper) {
        this.location = location;
        this.mapper = mapper;

        Metadata metadata = loadMetadata();

        this.id = metadata.getUuid();
        this.numberOfNotebooks = metadata.getChildren().size();
    }

    /**
     * Lazily loads the notebooks inside this library.
     *
     * @return A list of {@link QuiverNotebook}.
     */
    public List<QuiverNotebook> getNotebooks() {
        if (null == notebooks) {
            notebooks = loadNotebooks();
        }

        return notebooks;
    }

    private Metadata loadMetadata() {
        Path metadataLocation = location.resolve(Paths.get(META_FILE_NAME));

        try {
            return mapper.readValue(metadataLocation.toFile(), Metadata.class);
        } catch (IOException e) {
            throw new MalformedMetadataException(metadataLocation.toString(), e);
        }
    }

    private List<QuiverNotebook> loadNotebooks() {
        try {
            return Files.list(location)
                    .filter(f -> f.toString().endsWith(".qvnotebook"))
                    .map(f -> new QuiverNotebook(f, mapper))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new MalformedLibraryException(location.toString(), e);
        }
    }

    /**
     * Maps the metadata of this library from its json representation.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Metadata {
        private String uuid;
        private List<Children> children;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Children {
            private String uuid;
        }
    }
}
