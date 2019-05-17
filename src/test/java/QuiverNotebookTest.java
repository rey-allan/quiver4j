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

public class QuiverNotebookTest {
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = spy(new ObjectMapper());
    }

    @Test
    public void when_instantiated_metadata_are_loaded() {
        QuiverNotebook notebook =
                new QuiverNotebook(Paths.get(getClass().getResource("notebook.qvnotebook").getFile()), mapper);

        assertEquals("Tutorial", notebook.getId());
        assertEquals("Quiver Tutorial", notebook.getName());
        assertEquals(2, notebook.getNumberOfNotes());
    }

    @Test(expected = MalformedMetadataException.class)
    public void given_invalid_metadata_when_instantiated_throws_MalformedMetadataException() {
        @SuppressWarnings("unused")
        QuiverNotebook notebook =
                new QuiverNotebook(Paths.get(getClass().getResource("invalid-meta.qvnotebook").getFile()), mapper);
    }

    @Test
    public void when_get_notes_is_called_notes_are_lazily_loaded() throws IOException {
        QuiverNotebook notebook =
                new QuiverNotebook(Paths.get(getClass().getResource("notebook.qvnotebook").getFile()), mapper);
        List<QuiverNote> notes = notebook.getNotes();

        // Call it again, this time the notes should already exist
        notebook.getNotes();

        assertEquals(2, notes.size());
        // Inspect the first note
        assertEquals("D2A1CC36-CC97-4701-A895-EFC98EF47026", notes.get(0).getId());
        assertEquals("1 - Getting Started", notes.get(0).getTitle());
        // Make sure the notes were loaded only once
        // The `mapper` should only be called three times:
        // 1. For the metadata
        // 2. For the first call to `getNotes`
        //   2.1 loading the metadata of the first note
        //   2.2 loading the metadata of the second note
        verify(mapper, times(3)).readValue(any(File.class), (Class<?>) any(Class.class));
    }

    @Test
    public void given_an_empty_notebook_when_get_notes_is_called_no_notes_are_loaded() {
        QuiverNotebook notebook =
                new QuiverNotebook(Paths.get(getClass().getResource("empty.qvnotebook").getFile()), mapper);

        assertEquals(0, notebook.getNumberOfNotes());
        assertEquals(Collections.emptyList(), notebook.getNotes());
    }
}
