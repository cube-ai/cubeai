package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Attachment;

import com.wyy.repository.AttachmentRepository;
import com.wyy.service.NexusArtifactClient;
import com.wyy.web.rest.util.FileUtil;
import com.wyy.web.rest.util.HeaderUtil;
import com.wyy.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * REST controller for managing Attachment.
 */
@RestController
@RequestMapping("/api")
public class AttachmentResource {

    private final Logger log = LoggerFactory.getLogger(AttachmentResource.class);

    private static final String ENTITY_NAME = "attachment";

    private final AttachmentRepository attachmentRepository;
    private NexusArtifactClient nexusArtifactClient;

    public AttachmentResource(AttachmentRepository attachmentRepository, NexusArtifactClient nexusArtifactClient) {
        this.attachmentRepository = attachmentRepository;
        this.nexusArtifactClient = nexusArtifactClient;
    }


    /**
     * GET  /attachments : get all the attachments.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of attachments in body
     */
    @GetMapping("/attachments")
    @Timed
    @Secured({"ROLE_CONTENT"})
    public ResponseEntity<List<Attachment>> getAllAttachments(Pageable pageable) {
        log.debug("REST request to get all Attachments");

        Page<Attachment> page = attachmentRepository.findAll(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/attachments");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


    /**
     * DELETE  /attachments/:id : delete the "id" attachment.
     *
     * @param id the id of the attachment to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/attachments/{id}")
    @Timed
    @Secured({"ROLE_CONTENT"})
    public ResponseEntity<Void> deleteAttachment(HttpServletRequest request, @PathVariable Long id) {
        log.debug("REST request to delete Attachment : {}", id);

        Attachment attachment = attachmentRepository.findOne(id);
        String userLogin = request.getRemoteUser();
        Boolean isAdmin = request.isUserInRole("ROLE_ADMIN");

        if (userLogin.equals(attachment.getAuthorLogin()) || isAdmin) {
            this.nexusArtifactClient.deleteArtifact(attachment.getUrl());
            attachmentRepository.delete(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(403).build(); // 403 Forbidden
        }
    }

    @RequestMapping(value = "/attachments/upload", method = RequestMethod.POST, consumes = "multipart/form-data")
    @Timed
    @Secured({"ROLE_CONTENT"})
    public ResponseEntity<Void> uploadAttachmentFile(MultipartHttpServletRequest request) {
        log.debug("REST request to upload Attachment file");
        MultipartFile multipartFile = request.getFile("attachment");
        if (null == multipartFile) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("No file provided", "file upload")).build();
        }

        String fileName = multipartFile.getOriginalFilename();
        long size = multipartFile.getSize();
        if (fileName == null || ("").equals(fileName) || size == 0) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("No file content", "file upload")).build();
        }

        String userHome = System.getProperty("user.home");
        File file1 = new File(userHome + "/tempfile");
        if (!file1.exists()) {
            file1.mkdir();
        }

        File file2 = new File(userHome + "/tempfile/attachment");
        if (!file2.exists()) {
            file2.mkdir();
        }

        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        File file3 = new File(userHome + "/tempfile/attachment/" + uuid);
        if (!file3.exists()) {
            file3.mkdir();
        }

        File file4 = new File(userHome + "/tempfile/attachment/" + uuid + "/" + fileName);
        if (file4.exists()) {
            file4.delete();
        }
        try {
            multipartFile.transferTo(file4);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("Save file failed", fileName)).build();
        }

        String shortUrl = "attachment/" + uuid + "/" + fileName;
        String longUrl = this.nexusArtifactClient.addArtifact(shortUrl, file4);

        if (null == longUrl) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("Save file to Nexus failed", fileName)).build();
        }

        FileUtil.deleteDirectory(userHome + "/tempfile/attachment/" + uuid);

        Attachment attachment = new Attachment();
        attachment.setAuthorLogin(request.getRemoteUser());
        attachment.setName(fileName);
        attachment.setFileSize(size);
        attachment.setUrl(longUrl);
        attachment.setCreatedDate(Instant.now());
        attachment.setModifiedDate(Instant.now());
        attachmentRepository.save(attachment);

        log.debug("Upload attachment file success: " + fileName + "," + size + "Bytes");
        request.getSession().setAttribute("msg", "Upload attachment file success");

        return ResponseEntity.ok().build();
    }

}
