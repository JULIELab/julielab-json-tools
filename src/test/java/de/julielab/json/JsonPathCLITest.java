package de.julielab.json;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import de.julielab.java.utilities.FileUtilities;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class JsonPathCLITest {
    @Test
    public void test1() throws Exception {
        JsonPathCLI cli = new JsonPathCLI();
        InputStream is = FileUtilities.getInputStreamFromFile(Path.of("src", "test", "resources", "testfile1.json").toFile());
        ReadContext parse = JsonPath.parse(is);
        // The first argument is the file - not needed in this test.
        List<List<?>> values = cli.applyJsonPath(new String[]{"", "$.books[*]", "$.title"}, parse);
        assertThat(values).flatExtracting((Function<? super List<?>, ?>) l -> l.get(0)).containsExactly("Unicorns and Rainbows. An Experience Report.", "The Adventures of John Johnson Smith.");
    }

    @Test
    public void test2() throws Exception {
        JsonPathCLI cli = new JsonPathCLI();
        InputStream is = FileUtilities.getInputStreamFromFile(Path.of("src", "test", "resources", "testfile1.json").toFile());
        ReadContext parse = JsonPath.parse(is);
        // The first argument is the file - not needed in this test.
        List<List<?>> values = cli.applyJsonPath(new String[]{"", "$.books[*]", "$.title", "$.ISBN"}, parse);
        assertThat(values).flatExtracting(l -> l.get(0), l -> l.get(1)).containsExactly("Unicorns and Rainbows. An Experience Report.", 1, "The Adventures of John Johnson Smith.", 2);
    }

    @Test
    public void testRootarray() throws Exception {
        JsonPathCLI cli = new JsonPathCLI();
        InputStream is = FileUtilities.getInputStreamFromFile(Path.of("src", "test", "resources", "testfile2.json").toFile());
        ReadContext parse = JsonPath.parse(is);
        // The first argument is the file - not needed in this test.
        List<List<?>> values = cli.applyJsonPath(new String[]{"", "$", "$.title"}, parse);
        assertThat(values).flatExtracting((Function<? super List<?>, ?>) l -> l.get(0)).containsExactly("Unicorns and Rainbows. An Experience Report.", "The Adventures of John Johnson Smith.");
    }
}