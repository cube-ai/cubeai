package com.wyy.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wyy.web.rest.util.HeaderUtil;
import com.wyy.web.rest.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import java.io.File;
import java.io.IOException;
import com.wyy.util.FileUtil;

import javax.servlet.http.HttpServletRequest;


/**
 * REST controller for upload model file.
 */
@RestController
@RequestMapping("/api")
public class ModelFileResource {

    private final Logger log = LoggerFactory.getLogger(ModelFileResource.class);

    public ModelFileResource() {
    }

    @RequestMapping(value = "/modelfile/{taskUuid}", method = RequestMethod.POST, consumes = "multipart/form-data")
    @Timed
    public ResponseEntity<Void> uploadModelFile(MultipartHttpServletRequest request,
                                                           @PathVariable("taskUuid") String taskUuid) {
        log.debug("REST request to upload ucumos model file");

        String userLogin = JwtUtil.getUserLogin(request);

        MultipartFile multipartFile = request.getFile(userLogin);
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

        File file2 = new File(userHome + "/tempfile/ucumosmodels");
        if (!file2.exists()) {
            file2.mkdir();
        }

        File file3 = new File(userHome + "/tempfile/ucumosmodels/" + userLogin);
        if (!file3.exists()) {
            file3.mkdir();
        }

        File file4 = new File(userHome + "/tempfile/ucumosmodels/" + userLogin + "/" + taskUuid);
        if (!file4.exists()) {
            file4.mkdir();
        }

        String[] tmpList = file4.list();

        for (String tmp : tmpList) {
            File tmpFile = new File(userHome + "/tempfile/ucumosmodels/" + userLogin + "/" + taskUuid + "/" + tmp);
            if (tmpFile.isFile()) {
                tmpFile.delete();
            }
        }

        File file5= new File(userHome + "/tempfile/ucumosmodels/" + userLogin + "/" + taskUuid + "/" + fileName);
        if (file5.exists()) {
            file5.delete();
        }
        try {
            multipartFile.transferTo(file5);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("Save file failed", fileName)).build();
        }

        log.debug("Upload model file success: " + fileName + "," + size + "Bytes");
        request.getSession().setAttribute("msg", "Upload model file success");

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/modelfile/{taskUuid}", method = RequestMethod.DELETE)
    @Timed
    public ResponseEntity<Void> deleteTempModelFile(HttpServletRequest httpServletRequest,
                                                @PathVariable("taskUuid") String taskUuid) {
        log.debug("REST request to delete temp ucumos model file");

        String userLogin = JwtUtil.getUserLogin(httpServletRequest);
        FileUtil.deleteDirectory(System.getProperty("user.home") + "/tempfile/ucumosmodels/" + userLogin + "/" + taskUuid);

        return ResponseEntity.ok().build();
    }
}
