import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.MalformedMetadataException;
import org.junit.Before;
import org.junit.Test;

public class QuiverNoteTest {
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = spy(new ObjectMapper());
    }

    @Test
    public void when_instantiated_metadata_are_loaded() {
        QuiverNote note = new QuiverNote(Paths.get(getClass().getResource("note.qvnote").getFile()), mapper);

        assertEquals("D2A1CC36-CC97-4701-A895-EFC98EF47026", note.getId());
        assertEquals("1 - Getting Started", note.getTitle());
        assertEquals(Collections.singletonList("tutorial"), note.getTags());
        assertEquals("1403566023", note.getCreatedAt());
        assertEquals("1443042305", note.getUpdatedAt());
    }

    @Test(expected = MalformedMetadataException.class)
    public void given_invalid_metadata_when_instantiated_throws_MalformedMetadataException() {
        @SuppressWarnings("unused")
        QuiverNote note = new QuiverNote(Paths.get(getClass().getResource("invalid-meta.qvnote").getFile()), mapper);
    }

    @Test
    public void when_get_content_is_called_cells_are_lazily_loaded() throws IOException {
        QuiverNote note = new QuiverNote(Paths.get(getClass().getResource("note.qvnote").getFile()), mapper);
        List<QuiverCell> content = note.getContent();

        // Call it again, this time the content should already exist
        note.getContent();

        assertEquals(5, content.size());
        // Inspect the first cell
        assertEquals("text", content.get(0).getType());
        assertEquals("Welcome to Quiver!", content.get(0).getData());
        // Make sure the content was loaded only once
        // The `mapper` should only be called two times: for the metadata, and for the first call to `getContent`
        verify(mapper, times(2)).readValue(any(File.class), (Class<?>) any(Class.class));
    }
}
