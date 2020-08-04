package com.wyy.web.rest;

import com.alibaba.fastjson.JSONObject;
import com.codahale.metrics.annotation.Timed;
import com.wyy.web.rest.util.RandomPictureUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * REST controller for managing random picture
 */
@RestController
@RequestMapping("/api")
public class RandomPictureResource {

    private final Logger log = LoggerFactory.getLogger(RandomPictureResource.class);

    public RandomPictureResource() {
    }

    /**
     * GET  /randompicture : return a new created random picture.
     *
     * @return the ResponseEntity with status 200 (OK) and with body the random picture
     */
    @GetMapping("/randompicture/{width}/{height}")
    @Timed
    public ResponseEntity<JSONObject> getRandomPicture(@PathVariable int width, @PathVariable int height) {
        log.debug("REST request to get a new random picture");

        String pictureDataUrl = RandomPictureUtil.drawRandomPicture(width, height);
        JSONObject result = new JSONObject();
        result.put("pictureDataUrl", pictureDataUrl);

        return ResponseEntity.ok().body(result);
    }

}
