package frc.robot;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;

public class FileManager {

    public static void writeFile(File fileName, String toWrite) {
        try {
            fileName.mkdirs();
            fileName.createNewFile();
            FileWriter fileWriter = new FileWriter(fileName);
            for(int i = 0; i < toWrite.length(); i++) {
                fileWriter.write(toWrite.charAt(i));
            }
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copyFolders(File sourceFolder, File destinationFolder) throws IOException {
        if(sourceFolder.isDirectory()) {
            if(!destinationFolder.exists()) {
                destinationFolder.mkdir();
            }
            for(String files : Objects.requireNonNull(sourceFolder.list())) {
                File srcFile = new File(sourceFolder, files);
                File destFile = new File(destinationFolder, files);

                copyFolders(srcFile, destFile);
            }
        }
    }

    public static LinkedList<File> getFilesUnderFolder(File file) {
        if(file.listFiles() == null) return new LinkedList<>();
        return new LinkedList<>(Arrays.asList(file.listFiles()));
    }

    public static String readFile(String fileName) {
        return readFile(new File(fileName));
    }

    public static String readFile(File file) {
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            // Apparently using these wrappers is a more efficient way of reading text, before we would read a single
            // char, then translate it into the default charset. With a wrapper, it'll buffer what it needs to, and
            // translate later (if it even does)
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringBuilder message = new StringBuilder("");
            String line;
            while ((line = fileReader.readLine()) != null) {
                message.append(line);
            }
            fileReader.close();
            return message.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
