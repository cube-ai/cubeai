package com.wyy.web.rest;

import com.wyy.service.ArtifactService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/api/artifacts/")
public class ArtifactResource {
    private final Logger logger = LoggerFactory.getLogger(ArtifactResource.class);

    private final ArtifactService artifactService;

    public ArtifactResource(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }
    @ApiOperation(value = "Gets TOSCA details for specified solutionId and version")
    @RequestMapping(value = "/getJsonTOSCA", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String getJsonTOSCA(@RequestParam(value = "solutionId") String solutionId) {
        logger.debug("getJsonTOSCA() : Begin");
        String result;
        try{
            result = artifactService.readJsonTOSCA(solutionId);
            if (result == null || result.isEmpty()) {
                result = "Failed to fetch the TOSCA details for specified solutionId and version";
            }
        } catch (Exception e) {
            logger.error("Exception in getJsonTOSCA() ", e);
            result = e.getMessage();
        }
        logger.debug("getJsonTOSCA() : End");
        return result;
    }

}
