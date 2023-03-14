package com.tetkole.restservice.utils;

import com.tetkole.restservice.models.Annotation;
import com.tetkole.restservice.models.Corpus;
import com.tetkole.restservice.models.Document;
import com.tetkole.restservice.models.EDocumentType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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

    /**
     * Rename file's name to newName.
     */
    public File renameFile(File file, String newName) {
        System.gc();

        file.renameTo(new File(file.getParentFile() + "/" + newName));
        return new File(file.getParentFile() + "/" + newName);
    }

    public File absoluteCopyFile(File fileToCopy, String destPath) {
        try {
            Path newFilePath = Files.copy(fileToCopy.toPath(), (new File(destPath).toPath()), StandardCopyOption.REPLACE_EXISTING);
            return newFilePath.toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void renameDirectoryDocument(Document doc, String newName) {
        String destPath = this.path + "/" + doc.getCorpus().getName() + "/Annotations/" + newName;
        File source = new File(this.path + "/" + doc.getCorpus().getName() + "/Annotations/" + doc.getName());
        File dest = new File(destPath);
        dest.mkdir();
        File[] files = source.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File destAnnot = new File(destPath + "/" + files[i].getName());
                destAnnot.mkdir();
                File[] filesAnnot = files[i].listFiles();
                for (int j = 0; j < filesAnnot.length; j++) {
                    this.absoluteCopyFile(filesAnnot[j], destAnnot.getPath() + "/" + filesAnnot[j].getName());
                }
            }
        }
        this.deleteFolder(source);
    }

    public void deleteFile(File fileToDelete) {
        if (!fileToDelete.delete()) {
            System.err.println("\nCannot delete file " + fileToDelete.getName());
        }
    }

    public void deleteFolder(File folderToDelete) {
        File[] allContents = folderToDelete.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    this.deleteFile(file);
                }
            }
        }
        if (!folderToDelete.delete()) {
            System.err.println("\nCannot delete directory " + folderToDelete.getName());
        }
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

    public boolean deleteFile(String relativePath, String fileName) {
        File file = new File(path + "/" + relativePath + "/" + fileName);
        return file.delete();
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

    public void deleteAnnotationInCorpusState(Annotation annotation) {
        File corpus_state = new File(path + "/" + annotation.getDocument().getCorpus().getName() + "/" + CORPUS_STATE);

        JSONObject corpus_content = readJSONFile(corpus_state);
        if(corpus_content == null) return;

        JSONArray documents = corpus_content.getJSONArray("documents");

        boolean done = false;

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            JSONArray annotations = document.getJSONArray("annotations");

            for (int j = 0; j < annotations.length(); j++) {
                JSONObject a = annotations.getJSONObject(j);

                if (a.getInt("annotationId") == annotation.getAnnotationId()) {
                    annotations.remove(j);
                    document.put("annotations", annotations);
                    documents.put(i, document);
                    corpus_content.put("documents",documents);
                    done = true;
                    break;
                }

                if (done) break;
            }
        }

        writeJSONFile(corpus_state, corpus_content);
    }

    public void renameDirectoryAnnotation(Annotation annotation, String newName) {
        String destPath = this.path + "/" + annotation.getDocument().getCorpus().getName() + "/Annotations/" + annotation.getDocument().getName() + "/" + newName;
        File source = new File(this.path + "/" + annotation.getDocument().getCorpus().getName() + "/Annotations/" + annotation.getDocument().getName() + "/" + annotation.getName());
        File dest = new File(destPath);
        System.out.println(destPath);
        dest.mkdir();
        File[] files = source.listFiles();
        if (files != null) {
            for (int j = 0; j < files.length; j++) {
                this.absoluteCopyFile(files[j], dest.getPath() + "/" + files[j].getName());
            }
        }
        this.deleteFolder(source);
    }

    public void renameAnnotation(Annotation annotation, String newName) {
        String folderPathAnnotation = path + "/" + annotation.getDocument().getCorpus().getName() + "/" + "Annotations" + "/" + annotation.getDocument().getName() + "/" + annotation.getName();
        File fileAudio = new File(folderPathAnnotation + "/" + annotation.getName());
        File fileJSON = new File(folderPathAnnotation + "/" + annotation.getName().split("\\.")[0] + "." + "json");

        // Rename the field "recordName" in annotation.json
        JSONObject jsonObject =  this.readJSONFile(fileJSON);
        jsonObject.put("recordName", newName);
        this.writeJSONFile(fileJSON, jsonObject);

        // Rename the files
        this.renameFile(fileJSON, newName.split("\\.")[0] + ".json");
        this.renameFile(fileAudio, newName);
        this.renameDirectoryAnnotation(annotation, newName);

        // Rename annotation's name in corpus_state.json
        File corpus_state = new File(path + "/" + annotation.getDocument().getCorpus().getName() + "/" + CORPUS_STATE);
        JSONObject corpus_content = readJSONFile(corpus_state);
        if(corpus_content == null) return;

        JSONArray documents = corpus_content.getJSONArray("documents");

        boolean done = false;

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document = documents.getJSONObject(i);
            JSONArray annotations = document.getJSONArray("annotations");

            for (int j = 0; j < annotations.length(); j++) {
                JSONObject a = annotations.getJSONObject(j);

                if (a.getInt("annotationId") == annotation.getAnnotationId()) {
                    a.put("name", newName);
                    annotations.put(i, a);
                    document.put("annotations", annotations);
                    documents.put(i, document);
                    corpus_content.put("documents",documents);
                    done = true;
                    break;
                }

                if (done) break;
            }
        }

        writeJSONFile(corpus_state, corpus_content);
    }

    public void renameDocument(Document doc, String newName) {

        // Rename the doc name in all json annotation
        String folderPathAnnotation = path + "/" + doc.getCorpus().getName() + "/" + "Annotations" + "/" + doc.getName();
        for (Annotation a: doc.getAnnotations()
             ) {
            File fileJSON = new File(folderPathAnnotation + "/" + a.getName() + "/" + a.getName().split("\\.")[0] + "." + "json");
            JSONObject jsonAnnotation = this.readJSONFile(fileJSON);
            jsonAnnotation.put("fileName", newName);
            this.writeJSONFile(fileJSON, jsonAnnotation);
        }

        // Rename the file audio
        File fileAudio = new File(path + "/" + doc.getCorpus().getName() + "/" + doc.getType() + "/" + doc.getName());
        this.renameFile(fileAudio, newName);
        this.renameDirectoryDocument(doc, newName);

        File corpus_state = new File(path + "/" + doc.getCorpus().getName() + "/" + CORPUS_STATE);

        // Rename doc's name in corpus_state.json
        JSONObject corpus_content = readJSONFile(corpus_state);
        if(corpus_content == null) return;

        JSONArray documents = corpus_content.getJSONArray("documents");

        boolean done = false;

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document_json = documents.getJSONObject(i);

            if(document_json.get("docId").equals(doc.getDocId())) {
                document_json.put("name", newName);
                documents.put(i, document_json);
                corpus_content.put("documents",documents);
                break;
            }
        }

        writeJSONFile(corpus_state, corpus_content);
    }

    public void deleteDocumentInCorpusState(Document document) {

        File corpus_state = new File(path + "/" + document.getCorpus().getName() + "/" + CORPUS_STATE);

        JSONObject corpus_content = readJSONFile(corpus_state);
        if(corpus_content == null) return;

        JSONArray documents = corpus_content.getJSONArray("documents");

        for (int i = 0; i < documents.length(); i++) {
            JSONObject document_json = documents.getJSONObject(i);

            if(document_json.get("docId").equals(document.getDocId())) {
                documents.remove(i);
                corpus_content.put("documents",documents);
                break;
            }
        }

        writeJSONFile(corpus_state, corpus_content);
    }

    /**
     * Read json file's content
     */
    public JSONObject readJSONFile(File file) {
        try {
            String data = Files.readString(Path.of(file.getPath()));
            return new JSONObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Write content in json file
     */
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

    public boolean deleteAnnotationFromFolder(String relativePathFolder) {
        File folder = new File(path + "/" + relativePathFolder);
        File[] files = folder.listFiles();
        assert files != null;
        for (File f : files) {
            if (!f.delete()) return false;
        }
        return folder.delete();
    }
}
