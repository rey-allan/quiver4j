import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Represents the most basic unit of all Quiver notes: a cell.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@RequiredArgsConstructor
public class QuiverCell {
    /**
     * The type of the cell, e.g. {@code text}.
     */
    private String type;
    /**
     * The contents of the cell.
     */
    private String data;
    /**
     * The language used when the type of the cell is {@code code}.
     */
    private String language;
    /**
     * The type of the diagram when the type of cell is {@code diagram}.
     */
    private String diagramType;

    public QuiverCell(QuiverCell other) {
        this.type = other.getType();
        this.data = other.getData();
        this.language = other.getLanguage();
        this.diagramType = other.getDiagramType();
    }
}
