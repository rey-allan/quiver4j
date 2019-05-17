import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.MalformedContentException;
import exceptions.MalformedMetadataException;
import lombok.Data;

/**
 * Represents the core unit of all notebooks: a note.
 */
@Data
public class QuiverNote {
    private static final String CONTENT_FILE_NAME = "content.json";
    private static final String META_FILE_NAME = "meta.json";
    private static final String RESOURCES_DIRECTORY_NAME = "resources";

    /**
     * The unique identifier for this note.
     */
    private final String id;
    /**
     * The title of this note.
     */
    private final String title;
    /**
     * The list of tags of this note.
     */
    private final List<String> tags;
    /**
     * The time this note was created in seconds since Epoch.
     */
    private final String createdAt;
    /**
     * The time this note was last updated in seconds since Epoch.
     */
    private final String updatedAt;
    /**
     * The content of the note as a list of {@link QuiverCell}.
     */
    private List<QuiverCell> content;

    private final Path location;
    private final ObjectMapper mapper;

    public QuiverNote(Path location, ObjectMapper mapper) {
        this.location = location;
        this.mapper = mapper;

        Metadata metadata = loadMetadata();

        this.id = metadata.getUuid();
        this.title = metadata.getTitle();
        this.tags = metadata.getTags();
        this.createdAt = metadata.getCreatedAt();
        this.updatedAt = metadata.getUpdatedAt();
    }

    /**
     * Lazily loads the content of the note.
     *
     * @return A list of {@link QuiverCell}.
     */
    public List<QuiverCell> getContent() {
        if (null == content) {
            content = sanitizeResources(loadContent().getCells());
        }

        return content;
    }

    private Metadata loadMetadata() {
        Path metadataLocation = location.resolve(Paths.get(META_FILE_NAME));

        try {
            return mapper.readValue(metadataLocation.toFile(), Metadata.class);
        } catch (IOException e) {
            throw new MalformedMetadataException(metadataLocation.toString(), e);
        }
    }

    private Content loadContent() {
        Path contentLocation = location.resolve(Paths.get(CONTENT_FILE_NAME));

        try {
            return mapper.readValue(contentLocation.toFile(), Content.class);
        } catch (IOException e) {
            throw new MalformedContentException(contentLocation.toString(), e);
        }
    }

    private List<QuiverCell> sanitizeResources(List<QuiverCell> content) {
        String resourcesLocation = location.resolve(Paths.get(RESOURCES_DIRECTORY_NAME)).toString();

        return content.stream().map(cell -> {
            QuiverCell newCell = new QuiverCell(cell);
            // Sanitize by replacing the Quiver url with the local resources folder
            newCell.setData(newCell.getData().replaceAll("quiver-image-url", resourcesLocation));

            return newCell;
        }).collect(Collectors.toList());
    }

    /**
     * Maps the metadata of this note from its json representation.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Metadata {
        private String uuid;
        private String title;
        private List<String> tags;
        @JsonProperty("created_at")
        private String createdAt;
        @JsonProperty("updated_at")
        private String updatedAt;
    }

    /**
     * Maps the content of this note from its json representation.
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Content {
        private String title;
        private List<QuiverCell> cells;
    }
}
