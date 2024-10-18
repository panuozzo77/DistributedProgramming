import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class MarkdownIndexGenerator {

    // Regular expression to match Markdown headings
    private static final Pattern HEADING_PATTERN = Pattern.compile("^(#{1,6})\\s+(.*)");

    public static void main(String[] args) {
        // Set the root folder to 'classNotes'
        String projectFolder = Paths.get("classNotes").toAbsolutePath().toString();
        createIndex(Paths.get(projectFolder));
    }

    // Method to create the index
    private static void createIndex(Path rootFolder) {
        StringBuilder indexContent = new StringBuilder("# ClassNotes Index\n\n");

        try {
            Files.walk(rootFolder)
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".md") && !file.getFileName().toString().equals("index.md"))
                    .sorted()
                    .forEach(file -> {
                        Path relativePath = rootFolder.relativize(file);
                        String linkPath = "classNotes/" + relativePath.toString();

                        try {
                            List<Heading> headings = extractHeadings(Files.readString(file));
                            if (!headings.isEmpty()) {
                                indexContent.append("## ").append(file.getFileName()).append("\n\n");
                                for (Heading heading : headings) {
                                    String link = generateLink(linkPath, heading.title);
                                    String indent = "  ".repeat(heading.level - 1); // Indent based on heading level
                                    indexContent.append(indent).append(link).append("\n");
                                }
                                indexContent.append("\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            // Write the index to 'index.md' in the root of the project
            Path indexFilePath = Paths.get("index.md");
            Files.writeString(indexFilePath, indexContent.toString(), StandardOpenOption.CREATE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to extract headings from file content
    private static List<Heading> extractHeadings(String fileContent) {
        List<Heading> headings = new ArrayList<>();
        String[] lines = fileContent.split(System.lineSeparator());

        for (String line : lines) {
            Matcher matcher = HEADING_PATTERN.matcher(line);
            if (matcher.find()) {
                int level = matcher.group(1).length(); // Get heading level
                String title = matcher.group(2).trim();
                headings.add(new Heading(level, title));
            }
        }
        return headings;
    }

    // Method to generate a clickable link
    private static String generateLink(String filename, String title) {
        String anchor = title.toLowerCase().replace(" ", "-").replace(".", "").replace("'", "");
        filename = filename.replace(" ", "%20"); // Replace spaces with %20 in filename
        return "- [" + title + "](" + filename + "#" + anchor + ")";
    }

    // Inner class to represent a heading with its level and title
    private static class Heading {
        int level;
        String title;

        Heading(int level, String title) {
            this.level = level;
            this.title = title;
        }
    }
}