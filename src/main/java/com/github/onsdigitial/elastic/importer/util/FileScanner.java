package com.github.onsdigitial.elastic.importer.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sullid (David Sullivan) on 18/12/2017
 * @project dp-elastic-importer
 */
public class FileScanner {

    private String baseDir;
    private List<File> files;

    public FileScanner(String baseDir) {
        this.baseDir = baseDir;
        this.files = new ArrayList<>();
    }

    private void scan(File directory) throws IOException {
        // Scans all directories for json files
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                this.scan(file);
            } else if (file.getName().equals("data.json")) {
                this.files.add(file);
            }
        }
    }

    private void scan() throws IOException {
        File baseDirectory = new File(this.baseDir);
        if (!baseDirectory.isDirectory()) {
            throw new IOException("Base directory provided is not a directory");
        }
        this.scan(baseDirectory);
    }

    private File[] subDirectories(File currentDirectory) {
        return currentDirectory.listFiles(File::isDirectory);
    }

    public String getBaseDir() {
        return baseDir;
    }

    public List<File> getFiles() throws IOException {
        if (this.files.size() == 0) {
            this.scan();
        }
        return files;
    }

    public static void main(String[] args) {
        String zebedeeRoot = System.getenv("zebedee_root");
        String dataDirectory = String.format("%s/zebedee/master/", zebedeeRoot);

        FileScanner scanner = new FileScanner(dataDirectory);
        try {
            List<File> files = scanner.getFiles();
            System.out.println(files.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
