import static org.junit.Assert.assertEquals;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

public class QuiverCellTest {
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = new ObjectMapper();
    }

    @Test
    public void deserialize_text_cell() throws IOException {
        String json = "{\"type\": \"text\",\"data\": \"<h3>Text Cell</h3>\"}";
        QuiverCell cell = mapper.readValue(json, QuiverCell.class);

        assertEquals("text", cell.getType());
        assertEquals("<h3>Text Cell</h3>", cell.getData());
    }

    @Test
    public void deserialize_code_cell() throws IOException {
        String json = "{\"type\": \"code\",\"language\": \"javascript\",\"data\":\"console.log('Hello!');\"}";
        QuiverCell cell = mapper.readValue(json, QuiverCell.class);

        assertEquals("code", cell.getType());
        assertEquals("javascript", cell.getLanguage());
        assertEquals("console.log('Hello!');", cell.getData());
    }

    @Test
    public void deserialize_diagram_cell() throws IOException {
        String json = "{\"type\": \"diagram\",\"diagramType\": \"sequence\"," +
                "\"data\": \"Title: Here is a title A->B: Normal line B-->C: Dashed line\"}";
        QuiverCell cell = mapper.readValue(json, QuiverCell.class);

        assertEquals("diagram", cell.getType());
        assertEquals("sequence", cell.getDiagramType());
        assertEquals("Title: Here is a title A->B: Normal line B-->C: Dashed line", cell.getData());
    }
}
