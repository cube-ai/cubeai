package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Document;
import com.wyy.domain.Solution;
import com.wyy.service.NexusArtifactClient;
import com.wyy.service.UmmClient;
import com.wyy.util.FileUtil;
import com.wyy.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.io.File;
import java.io.IOException;


/**
 * REST controller for upload document file.
 */
@RestController
@RequestMapping("/api")
public class DocumentResource {

    private final Logger log = LoggerFactory.getLogger(DocumentResource.class);

    private NexusArtifactClient nexusArtifactClient;
    private UmmClient ummClient;

    public DocumentResource(NexusArtifactClient nexusArtifactClient, UmmClient ummClient) {
        this.nexusArtifactClient = nexusArtifactClient;
        this.ummClient = ummClient;
    }

    @RequestMapping(value = "/documents/{solutionUuid}/{fileName}", method = RequestMethod.POST, consumes = "multipart/form-data")
    @Timed
    public ResponseEntity<Void> uploadDocument(MultipartHttpServletRequest request,
                                               @PathVariable("solutionUuid") String solutionUuid,
                                               @PathVariable("fileName") String fileName) {
        log.debug("REST request to upload solution document file");
        MultipartFile multipartFile = request.getFile("document");
        if (null == multipartFile) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("No file provided", "file upload")).build();
        }

        // String fileName = multipartFile.getOriginalFilename();
        fileName = fileName.replaceAll("。", ".");

        long size = multipartFile.getSize();
        if (fileName == null || ("").equals(fileName) || size == 0) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("No file content", "file upload")).build();
        }

        String userHome = System.getProperty("user.home");
        File file1 = new File(userHome + "/tempfile");
        if (!file1.exists()) {
            file1.mkdir();
        }

        File file2 = new File(userHome + "/tempfile/document");
        if (!file2.exists()) {
            file2.mkdir();
        }

        File file3 = new File(userHome + "/tempfile/document/" + solutionUuid);
        if (!file3.exists()) {
            file3.mkdir();
        }

        File file4= new File(userHome + "/tempfile/document/" + solutionUuid + "/" + fileName);
        if (file4.exists()) {
            file4.delete();
        }
        try {
            multipartFile.transferTo(file4);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("Save file failed", "")).build();
        }

        Solution solution = this.ummClient.getSolutions(solutionUuid).get(0);
        String shortUrl = solution.getAuthorLogin()+ "/" + solutionUuid + "/document/" + fileName;
        String longUrl= this.nexusArtifactClient.addArtifact(shortUrl, file4);

        if (null == longUrl) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("Save file to Nexus failed", "")).build();
        }

        Document document = new Document();
        document.setSolutionUuid(solution.getUuid());
        document.setName(file4.getName());
        document.setUrl(longUrl);
        document.setFileSize(file4.length());

        this.ummClient.createDocument(document);

        file4.delete();
        FileUtil.deleteDirectory(userHome + "/tempfile/document/" + solutionUuid);

        log.debug("Upload model file success: " + fileName + "," + size + "Bytes");
        request.getSession().setAttribute("msg", "Upload model file success");

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/documents/{documentId}", method = RequestMethod.DELETE)
    @Timed
    public ResponseEntity<Void> deleteDocument(@PathVariable("documentId") Long documentId) {
        log.debug("REST request to delete a document");

        Document document = this.ummClient.getDocument(documentId).getBody();
        this.nexusArtifactClient.deleteArtifact(document.getUrl());
        this.ummClient.deleteDocument(documentId);

        return ResponseEntity.ok().build();
    }

}
