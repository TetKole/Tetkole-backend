package com.tetkole.restservice.utils;

import com.tetkole.restservice.models.Annotation;
import com.tetkole.restservice.models.Corpus;
import com.tetkole.restservice.models.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

@Service
public class FileManager {

    private final String resourcesDir = "Tetkole-backend-resources";
    private final String path;
    private final String CORPUS_STATE = "corpus_state.json";


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

    public JSONObject getCorpusStateContent(String corpusName) {
        File corpus_state = new File(path + "/" + corpusName + "/" + CORPUS_STATE);
        if(corpus_state.exists()) {
            return readJSONFile(corpus_state);
        }
        return null;
    }

    public void createCorpusState(Corpus corpus) {
        File corpus_state = createFile(corpus.getName(),CORPUS_STATE);
        JSONObject corpus_content = new JSONObject(corpus);
        writeJSONFile(corpus_state, corpus_content);
    }

    public void addDocumentInCorpusState(Document doc) {
        File corpus_state = new File(path + "/" + doc.getCorpus().getName() + "/" + CORPUS_STATE);
        JSONObject corpus_content = readJSONFile(corpus_state);

        if(corpus_content == null) return;

        JSONArray documents = corpus_content.getJSONArray("documents");
        JSONObject json_doc = doc.toJson();
        json_doc.remove("corpusId");
        documents.put(json_doc);
        corpus_content.put("documents", documents);

        writeJSONFile(corpus_state, corpus_content);
    }

    public void addAnnotationInCorpusState(Annotation annotation) {

        File corpus_state = new File(path + "/" + annotation.getDocument().getCorpus().getName() + "/" + CORPUS_STATE);

        JSONObject corpus_content = readJSONFile(corpus_state);
        if(corpus_content == null) return;

        JSONArray documents = corpus_content.getJSONArray("documents");

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document_json = documents.getJSONObject(i);
            if(document_json.get("docId").equals(annotation.getDocument().getDocId())) {
                JSONArray annotations = document_json.getJSONArray("annotations");
                annotations.put(annotation.toJson());
                document_json.put("annotations",annotations);
                documents.put(i, document_json);
                corpus_content.put("documents",documents);
                break;
            }
        }

        writeJSONFile(corpus_state, corpus_content);
    }

    private JSONObject readJSONFile(File jsonFile) {
        JSONObject corpus_content = null;
        try {
            // Read corpus_state file
            FileInputStream fis = new FileInputStream(jsonFile);
            byte[] data = new byte[(int) jsonFile.length()];
            fis.read(data);
            fis.close();

            String str = new String(data, StandardCharsets.UTF_8);
            corpus_content = new JSONObject(str);
        } catch (Exception IOException) {
            System.err.println("Could not read in " + jsonFile.getName());
        }
        return corpus_content;
    }

    private void writeJSONFile(File jsonFile, JSONObject jsonContent) {
        try {
            // Write in corpus_state file
            FileOutputStream fos = new FileOutputStream(jsonFile);
            byte[] data = jsonContent.toString().getBytes(StandardCharsets.UTF_8);
            fos.flush();
            fos.write(data);
            fos.close();
        } catch (Exception IOException) {
            System.err.println("Could not write in " + jsonFile.getName());
        }
    }
}
