package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.service.NexusArtifactClient;
import com.wyy.service.UmmClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;


/**
 * REST controller for download model arifact or document files.
 */
@RestController
@RequestMapping("/api")
public class DownloadResource {

    private final Logger log = LoggerFactory.getLogger(DownloadResource.class);

    private UmmClient ummClient;
    private NexusArtifactClient nexusArtifactClient;

    public DownloadResource(UmmClient ummClient, NexusArtifactClient nexusArtifactClient) {
        this.ummClient = ummClient;
        this.nexusArtifactClient = nexusArtifactClient;
    }

    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @Timed
    public void downloadArtifact(@RequestParam(value = "url") String url, HttpServletResponse response) {
        log.debug("REST request to download artifact/document file");

        // 暂时不在HTTP响应头中传递文件名，因为文件名可能是中文
        // String fileName = url.substring(artifactUrl.lastIndexOf("/") + 1);
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        // response.setHeader("x-filename", fileName);
        // response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Content-Disposition", "attachment");
        response.setStatus(HttpServletResponse.SC_OK);

        try {
            ByteArrayOutputStream byteArrayOutputStream = nexusArtifactClient.getArtifact(url);
            byteArrayOutputStream.writeTo(response.getOutputStream());
            response.flushBuffer();
            if (null != byteArrayOutputStream) {
                byteArrayOutputStream.close();
            }
        } catch (Exception e) {
        }
    }

    @RequestMapping(value = "/get-file-text", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<JSONObject> getFileText(@RequestParam(value = "url") String url) {
        log.debug("REST request to download text file");

        String text = nexusArtifactClient.getArtifact(url).toString();
        JSONObject result = new JSONObject();
        result.put("text", text);

        return ResponseEntity.ok().body(result);
    }

}
