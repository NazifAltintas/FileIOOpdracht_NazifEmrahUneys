package be.intecbrussel;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileType {

    public static void main(String[] args) throws IOException {

        //Value to justify text to the left for use in the writeToSummaryText method.
        int length;

        Path path = Paths.get("C:\\Users\\nazif\\Desktop\\unsorted");
        Path sortedFolderPath = path.getParent().resolve("sorted_folder");
        Path summaryFolderPath = sortedFolderPath.resolve("summary");
        Path hiddenFolderPath = sortedFolderPath.resolve("hidden");
        Path summaryFilePath = summaryFolderPath.resolve("summary.txt");

        //to keep all unsorted files in a list
        List<Path> paths = listFiles(path);

        //to create the "sorted_folder" folder.
        sortedFolderPath.toFile().mkdir();
        //to create the "summary" folder in "sorted_folder".
        summaryFolderPath.toFile().mkdir();

        //to keep all file extensions in a list
        List<String> lastNameList = folderNameByExtension(paths);

        createFoldersByExtension(lastNameList, sortedFolderPath);

        //we provide the left justification value from the "copyToSortedFolder" method
        length = copyToSortedFolder(paths, lastNameList, sortedFolderPath, hiddenFolderPath);

        createSummaryFileAsTxt(summaryFilePath);

        //to keep all sorted files in a new list
        List<Path> newPaths = listFiles(sortedFolderPath);

        createHiddenFolder(newPaths, hiddenFolderPath);

        copyToHiddenFolderIfFileIsHidden(newPaths, hiddenFolderPath);

        writeToSummaryText(length, lastNameList, newPaths, summaryFilePath);

        System.out.println("All files sorted successfully.");


    }

    private static void copyToHiddenFolderIfFileIsHidden(List<Path> newPaths, Path hiddenPath) {

        for (Path w : newPaths) {
            try {
                if (Files.isHidden(w)) {
                    Files.move(w, hiddenPath.resolve(w.getFileName()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void writeToSummaryText(int length, List<String> lastNameList, List<Path> newPaths, Path summaryFile) {

        String x = "%-" + (length + 5) + "s"; //left justification sign

        try (FileWriter fileWriter = new FileWriter(summaryFile.toFile(), true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

            Formatter myFormat = new Formatter();

//            We have created a condition because the list will be empty when the program is run for the second time.
            if (lastNameList.size() > 0) {
                //first line
                bufferedWriter.write(String.valueOf(myFormat.format(x + "|     %s    |     %s     |\n\n", "name", "readable", "writable")));
            }
            for (int i = 0; i < lastNameList.size(); i++) {
                bufferedWriter.write(lastNameList.get(i) + "\n------\n");
                for (Path w : newPaths) {
                    Formatter myFormat1 = new Formatter();
                    if (w.getFileName().toString().substring(w.getFileName().toString().lastIndexOf(".") + 1).equals(lastNameList.get(i))) {
                        String isWritable, isReadable;
                        if (Files.isWritable(w)) {
                            isWritable = "X";
                        } else {
                            isWritable = "/";
                        }
                        if (Files.isReadable(w)) {
                            isReadable = "X";
                        } else {
                            isReadable = "/";
                        }
                        bufferedWriter.write(String.valueOf(myFormat1.format(x + "|        %s        |        %s         |\n",
                                w.getFileName().toString(), isReadable, isWritable)));
                    }
                }
                bufferedWriter.write("\n------\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createHiddenFolder(List<Path> newPaths, Path hiddenPath) {
        for (Path w : newPaths) {
            try {
                if (Files.isHidden(w) && Files.notExists(hiddenPath)) {
                    Files.createDirectories(hiddenPath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private static void createSummaryFileAsTxt(Path summaryFile) {
        try {
            if (Files.notExists(summaryFile)) {
                Files.createFile(summaryFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int copyToSortedFolder(List<Path> paths, List<String> lastNameList, Path copiedPath, Path hiddenPath) {
        int length = 0;
        for (Path w : paths) {
            for (String k : lastNameList) {
                if (w.getFileName().toString().length() > length) {
                    length = w.getFileName().toString().length();
                }
                try {
                    if ((w.getFileName().toString().substring(w.getFileName().toString().lastIndexOf(".") + 1).equals(k)) && Files.exists(w)) {
                        Files.move(w, copiedPath.resolve(k).resolve(w.getFileName()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return length;
    }

    private static void createFoldersByExtension(List<String> lastNameList, Path copiedPath) {
        for (String w : lastNameList) {
            Path path1 = copiedPath.resolve(w);
            try {
                if (Files.notExists(path1)) {
                    Files.createDirectories(path1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // list all files from this path
    public static List<Path> listFiles(Path path) throws IOException {
        List<Path> result;
        try (Stream<Path> walk = Files.walk(path)) {
            result = walk.filter(Files::isRegularFile)
                    .collect(Collectors.toList());
        }
        return result;
    }

    private static List<String> folderNameByExtension(List<Path> path) {
        List<String> folderNames = new ArrayList<>();
        int x;
        String folderName;
        System.out.println();
        for (Path w : path) {
            x = w.toString().lastIndexOf(".");
            folderName = w.toString().substring(x + 1);
            if (!folderNames.contains(folderName)) {
                folderNames.add(folderName);
            }
        }
        return folderNames;
    }
}
