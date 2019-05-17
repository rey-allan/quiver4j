# quiver4j
:open_file_folder: A Java library for parsing [Quiver](http://happenapps.com/) libraries.

## Usage

```java
class QuiverParser {
    public static void main(String[] args) {
        QuiverLibrary library = new QuiverLibrary(Paths.get("/home/$USER/Quiver.qvlibrary"));
        
        for (QuiverNotebook notebook : library.getNotebooks()) {
            System.out.println(String.format("Notebook '%s' has %d notes", notebook.getName(), notebook.getNumberOfNotes()));
            
            for (QuiverNote note : notebook.getNotes()) {
                List<QuiverCell> content = note.getContent();
                System.out.println(String.format("Note '%s' has %d cells", note.getTitle(), content.size()));
                
                for (QuiverCell cell : content) {
                    System.out.println(String.format("Cell of type '%s' with data: %s", cell.getType(), cell.getData()));
                }
            }
        }
    }
}
```
