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

public class QuiverLibraryTest {
    private ObjectMapper mapper;

    @Before
    public void setup() {
        mapper = spy(new ObjectMapper());
    }

    @Test
    public void when_instantiated_metadata_are_loaded() {
        QuiverLibrary library =
                new QuiverLibrary(Paths.get(getClass().getResource("library.qvlibrary").getFile()), mapper);

        assertEquals("Notebooks", library.getId());
        assertEquals(2, library.getNumberOfNotebooks());
    }

    @Test(expected = MalformedMetadataException.class)
    public void given_invalid_metadata_when_instantiated_throws_MalformedMetadataException() {
        @SuppressWarnings("unused")
        QuiverLibrary library =
                new QuiverLibrary(Paths.get(getClass().getResource("invalid-meta.qvlibrary").getFile()), mapper);
    }

    @Test
    public void when_get_notebooks_is_called_notebooks_are_lazily_loaded() throws IOException {
        QuiverLibrary library =
                new QuiverLibrary(Paths.get(getClass().getResource("library.qvlibrary").getFile()), mapper);
        List<QuiverNotebook> notebooks = library.getNotebooks();

        // Call it again, this time the notebooks should already exist
        library.getNotebooks();

        assertEquals(2, notebooks.size());
        // Inspect the first notebook
        assertEquals("Tutorial", notebooks.get(0).getId());
        assertEquals("Quiver Tutorial", notebooks.get(0).getName());
        // Make sure the notebooks were loaded only once
        // The `mapper` should only be called three times:
        // 1. For the metadata
        // 2. For the first call to `getNotebooks`
        //   2.1 loading the metadata of the first notebook
        //   2.2 loading the metadata of the second notebook
        verify(mapper, times(3)).readValue(any(File.class), (Class<?>) any(Class.class));
    }

    @Test
    public void given_an_empty_library_when_get_notebooks_is_called_no_notebooks_are_loaded() {
        QuiverLibrary library =
                new QuiverLibrary(Paths.get(getClass().getResource("empty.qvlibrary").getFile()), mapper);

        assertEquals(0, library.getNumberOfNotebooks());
        assertEquals(Collections.emptyList(), library.getNotebooks());
    }
}
