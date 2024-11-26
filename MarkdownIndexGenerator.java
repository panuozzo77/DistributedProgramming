import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

public class MarkdownIndexGenerator {

    private static final Pattern FILE_PATTERN = Pattern.compile("^(\\d+)(?:_(\\d+))?");

    public static void main(String[] args) {
        String projectFolder = Paths.get("classNotes").toAbsolutePath().toString();
        generateIndex(Paths.get(projectFolder));
    }

    private static void generateIndex(Path rootFolder) {
        StringBuilder indexContent = new StringBuilder("# ClassNotes Index\n\n");

        try {
            // Collect all markdown files and convert their index (1_1 -> 1.1)
            List<FileWithIndex> filesWithIndex = Files.walk(rootFolder)
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".md") && !file.getFileName().toString().equals("index.md"))
                    .map(file -> new FileWithIndex(file, convertFileNameToSortableIndex(file.getFileName().toString())))
                    .sorted(Comparator.comparing(FileWithIndex::getIndex))
                    .collect(Collectors.toList());

            // Generate the index file content
            for (FileWithIndex fileWithIndex : filesWithIndex) {
                Path file = fileWithIndex.getFile();
                Path relativePath = rootFolder.relativize(file);
                String linkPath = "classNotes/" + relativePath.toString().replace("\\", "/");
                indexContent.append("## ").append(file.getFileName()).append("\n\n");

                List<Heading> headings = extractHeadings(Files.readString(file));
                for (Heading heading : headings) {
                    String link = generateLink(linkPath, heading.title);
                    String indent = "  ".repeat(heading.level - 1);
                    indexContent.append(indent).append("- ").append(link).append("\n");
                }
                indexContent.append("\n");
            }

            // Write to index.md
            Path indexFilePath = Paths.get("index.md");
            Files.writeString(indexFilePath, indexContent.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Index generated at: " + indexFilePath.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error processing files: " + e.getMessage());
        }
    }

    private static String convertFileNameToSortableIndex(String fileName) {
        // Match patterns like 1_1, 2_10, etc. and convert to sortable format
        Matcher matcher = FILE_PATTERN.matcher(fileName);
        if (matcher.find()) {
            String part1 = matcher.group(1);
            String part2 = matcher.group(2);
            return part2 != null ? String.format("%03d.%02d", Integer.parseInt(part1), Integer.parseInt(part2)) : String.format("%03d", Integer.parseInt(part1));
        }
        return fileName; // Return as-is if no match
    }

    private static List<Heading> extractHeadings(String fileContent) {
        List<Heading> headings = new ArrayList<>();
        String[] lines = fileContent.split("\n");

        for (String line : lines) {
            Matcher matcher = Pattern.compile("^(#{1,6})\\s+(.*)").matcher(line);
            if (matcher.find()) {
                int level = matcher.group(1).length();
                String title = matcher.group(2).trim();
                headings.add(new Heading(level, title));
            }
        }
        return headings;
    }

    private static String generateLink(String filename, String title) {
        String anchor = title.toLowerCase()
                .replaceAll("[^a-z0-9\\- ]", "")
                .replace(" ", "-");
        filename = filename.replace(" ", "%20");
        return "[" + title + "](" + filename + "#" + anchor + ")";
    }

    private static class Heading {
        int level;
        String title;

        Heading(int level, String title) {
            this.level = level;
            this.title = title;
        }
    }

    private static class FileWithIndex {
        Path file;
        String index;

        FileWithIndex(Path file, String index) {
            this.file = file;
            this.index = index;
        }

        public Path getFile() {
            return file;
        }

        public String getIndex() {
            return index;
        }
    }
}
