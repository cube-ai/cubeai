package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.domain.Ueditor;
import com.wyy.service.NexusArtifactClient;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


/**
 * REST controller for upload and Ueditor file.
 */
@RestController
@RequestMapping("/api")
public class UeditorResource {

    private final Logger log = LoggerFactory.getLogger(UeditorResource.class);

    private NexusArtifactClient nexusArtifactClient;

    public UeditorResource(NexusArtifactClient nexusArtifactClient) {
        this.nexusArtifactClient = nexusArtifactClient;
    }

    @RequestMapping(value = "/ueditor", method = RequestMethod.GET)
    @Timed
    public String getConfig(@RequestParam(value = "action") String action) {
        log.debug("REST request to get config json string");

        Ueditor ueditor = new Ueditor();

        if (action.equals("config")) {
            try {
                ClassPathResource classPathResource = new ClassPathResource("ueditor/config.json");
                InputStream stream = classPathResource.getInputStream();
                String config = IOUtils.toString(stream, "UTF-8");
                stream.close();
                return config;
            } catch (Exception e) {
                ueditor.setState("找不到配置文件！");
                return JSONObject.toJSONString(ueditor);
            }
        } else {
            ueditor.setState("不支持操作！");
            return JSONObject.toJSONString(ueditor);
        }
    }

    @RequestMapping(value = "/ueditor", method = RequestMethod.POST, consumes = "multipart/form-data")
    @Timed
    public String ueditorController(MultipartHttpServletRequest request,
                                             @RequestParam(value = "action") String action) {
        log.debug("REST request to perform Ueditor actions");

        Ueditor ueditor = new Ueditor();

        if (!action.equals("uploadimage") && ! action.equals("uploadscrawl")) {
            ueditor.setState("不支持操作！");
            return JSONObject.toJSONString(ueditor);
        }

        MultipartFile multipartFile = request.getFile("upfile");
        if (null == multipartFile) {
            ueditor.setState("文件为空！");
            return JSONObject.toJSONString(ueditor);
        }

        String fileName = multipartFile.getOriginalFilename();

        long size = multipartFile.getSize();
        if (fileName == null || ("").equals(fileName) || size == 0) {
            ueditor.setState("文件为空！");
            return JSONObject.toJSONString(ueditor);
        }

        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        fileName = uuid + "." + ext;

        String userHome = System.getProperty("user.home");
        File file1 = new File(userHome + "/tempfile");
        if (!file1.exists()) {
            file1.mkdir();
        }

        File file2 = new File(userHome + "/tempfile/ueditor");
        if (!file2.exists()) {
            file2.mkdir();
        }

        File file3 = new File(userHome + "/tempfile/ueditor/picture");
        if (!file3.exists()) {
            file3.mkdir();
        }

        File file4 = new File(userHome + "/tempfile/ueditor/picture/" + fileName);
        if (file4.exists()) {
            file4.delete();
        }
        try {
            multipartFile.transferTo(file4);
        } catch (IOException e) {
            e.printStackTrace();
            ueditor.setState("保存临时文件出错！");
            return JSONObject.toJSONString(ueditor);
        }

        String shortUrl = "ueditor/picture/" + fileName;
        String longUrl= this.nexusArtifactClient.addArtifact(shortUrl, file4);

        if (null == longUrl) {
            ueditor.setState("上传文件出错！");
            return JSONObject.toJSONString(ueditor);
        }

        file4.delete();

        ueditor.setState("SUCCESS");
        ueditor.setUrl(longUrl);
        return JSONObject.toJSONString(ueditor);
    }

}
