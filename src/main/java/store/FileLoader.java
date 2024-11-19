package store;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {

    public List<String> loadFile(String fileName) {
        List<String> lines = new ArrayList<>();
        try (InputStream is = getFileStream(fileName);
             BufferedReader reader = createBufferReader(is)) {
            if (is == null) {
                System.err.println("[ERROR] " + fileName);
                return lines;
            }
            lines = readLines(reader);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public List<String> loadProductsFile() {
        return loadFile("products.md");
    }

    public List<String> loadPromotionsFile() {
        return loadFile("promotions.md");
    }

    private InputStream getFileStream(String fileName) {
        return getClass().getClassLoader().getResourceAsStream(fileName);
    }

    private BufferedReader createBufferReader(InputStream is) {
        return new BufferedReader((new InputStreamReader(is, StandardCharsets.UTF_8)));
    }

    private List<String> readLines(BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines;
    }
}


