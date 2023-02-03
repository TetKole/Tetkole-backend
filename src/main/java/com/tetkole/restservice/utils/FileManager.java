package com.tetkole.restservice.utils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

@Service
public class FileManager {

    private final String resourcesDir = "Tetkole-backend-resources";
    private final String path;


    public FileManager() {
        /* first we manage to get the ressources folder */

        // get the working folder
        String tempPath = System.getProperty("user.dir");

        // replace all \ by / (usefull on windows)
        tempPath = tempPath.replaceAll(Pattern.quote("\\"), "/");

        // split path by /
        String[] splitPath = tempPath.split("/");

        // remove last element (ie the working dir)
        splitPath = Arrays.copyOf(splitPath, splitPath.length - 1);

        // rebuilding the path without the working dir
        tempPath = "";
        StringBuilder sb = new StringBuilder();
        for (String elem: splitPath)
            tempPath = sb.append(elem).append("/").toString();

        // adding the ressources dir
        tempPath = sb.append(this.resourcesDir).toString();

        // set path to the resourcesDir
        this.path = tempPath;
        // create it if needed
        if (!new File(this.path).mkdir())
            System.out.println("INFO: " + this.path + " already exists.");
    }

    public void createCorpusFolder(String folderName) {
        String absolutePath = this.path + "/" + folderName;

        if (!new File(absolutePath).mkdir())
            System.out.println("INFO: " + absolutePath + " already exists.");
    }

    public void createFolder(String relativePath, String folderName) {
        String absolutePath = this.path + "/" + relativePath + "/" + folderName;

        if (!new File(absolutePath).mkdir())
            System.out.println("INFO: " + absolutePath + " already exists.");
    }

    public File createFile(String relativePath, String fileName) {
        File file = new File(path + "/" + relativePath + "/" + fileName);
        try {
            if (file.createNewFile())
                return file;
            else
                System.out.println("File " + file.getName() + " already exists");
        } catch (IOException e) {
            System.err.println("Could not create " + file.getName());
        }
        return null;
    }

    public boolean createMultipartFile(String relativePath, MultipartFile file) {
        try {
            Path pathFile = Paths.get(path + "/" + relativePath + "/" + file.getOriginalFilename());
            file.transferTo(pathFile);
            return true;
        } catch (IOException e) {
            System.err.println("Could not create " + file.getOriginalFilename());
            return false;
        }
    }
}
