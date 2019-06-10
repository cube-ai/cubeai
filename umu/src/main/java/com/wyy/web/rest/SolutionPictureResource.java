package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Solution;
import com.wyy.service.NexusArtifactClient;
import com.wyy.service.UmmClient;
import com.wyy.util.FileUtil;
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


/**
 * REST controller for upload solution picture file.
 */
@RestController
@RequestMapping("/api")
public class SolutionPictureResource {

    private final Logger log = LoggerFactory.getLogger(SolutionPictureResource.class);

    private NexusArtifactClient nexusArtifactClient;
    private UmmClient ummClient;

    public SolutionPictureResource(NexusArtifactClient nexusArtifactClient, UmmClient ummClient) {
        this.nexusArtifactClient = nexusArtifactClient;
        this.ummClient = ummClient;
    }

    @RequestMapping(value = "/solution-picture/{solutionUuid}/{fileName}", method = RequestMethod.POST, consumes = "multipart/form-data")
    @Timed
    public ResponseEntity<Void> uploadSolutionPicture(MultipartHttpServletRequest request,
                                               @PathVariable("solutionUuid") String solutionUuid,
                                               @PathVariable("fileName") String fileName) {
        log.debug("REST request to upload solution picture file");

        String userLogin = JwtUtil.getUserLogin(request);
        Solution solution = this.ummClient.getSolutions(solutionUuid).get(0);
        if (null == userLogin || !userLogin.equals(solution.getAuthorLogin())) {
            return ResponseEntity.status(401).build();
        }

        MultipartFile multipartFile = request.getFile("picture");
        if (null == multipartFile) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("No file provided", "file upload")).build();
        }

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

        File file2 = new File(userHome + "/tempfile/picture");
        if (!file2.exists()) {
            file2.mkdir();
        }

        File file3 = new File(userHome + "/tempfile/picture/" + solutionUuid);
        if (!file3.exists()) {
            file3.mkdir();
        }

        File file4= new File(userHome + "/tempfile/picture/" + solutionUuid + "/" + fileName);
        if (file4.exists()) {
            file4.delete();
        }
        try {
            multipartFile.transferTo(file4);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("Save file failed", fileName)).build();
        }

        String shortUrl = solution.getAuthorLogin() + "/" + solutionUuid + "/picture/" + fileName;
        String longUrl= this.nexusArtifactClient.addArtifact(shortUrl, file4);

        if (null == longUrl) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createAlert("Save file to Nexus failed", fileName)).build();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", solution.getId());
        jsonObject.put("pictureUrl", longUrl);
        this.ummClient.updateSolutionPictureUrl(jsonObject);

        file4.delete();
        FileUtil.deleteDirectory(userHome + "/tempfile/picture/" + solutionUuid);

        log.debug("Upload solution picture success: " + fileName + "," + size + "Bytes");
        request.getSession().setAttribute("msg", "Upload solution picture success");

        return ResponseEntity.ok().build();
    }

}
