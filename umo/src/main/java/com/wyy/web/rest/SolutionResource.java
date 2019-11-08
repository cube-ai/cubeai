package com.wyy.web.rest;

import com.alibaba.fastjson.JSONArray;
import com.wyy.domain.CompositeSolution;
import com.wyy.domain.DSResult;
import com.wyy.domain.Solution;
import com.wyy.domain.cdump.*;
import com.wyy.domain.cdump.collator.CollatorMap;
import com.wyy.domain.cdump.databroker.DataBrokerMap;
import com.wyy.domain.cdump.datamapper.FieldMap;
import com.wyy.domain.cdump.splitter.SplitterMap;
import com.wyy.domain.protobuf.MessageargumentList;
import com.wyy.dto.SolutionNodes;
import com.wyy.service.CompositeSolutionService;
import com.wyy.service.SolutionService;
import com.wyy.util.ConfigurationProperties;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class SolutionResource {
    private final Logger logger = LoggerFactory.getLogger(SolutionResource.class);

    private final SolutionService solutionService;

    private final CompositeSolutionService compositeSolutionService;

    private final ConfigurationProperties configurationProperties;

    public SolutionResource(SolutionService solutionService, CompositeSolutionService compositeSolutionService, ConfigurationProperties configurationProperties) {
        this.solutionService = solutionService;
        this.compositeSolutionService = compositeSolutionService;
        this.configurationProperties = configurationProperties;
    }

    /**
	 *
	 * get all the solutions by different parameters
	 * @return Solutions
	 * @throws Exception
	 *             On failure
	 */
//	@ApiOperation(value = "Get Solutions")
//	@RequestMapping(value = "/solutions", method = RequestMethod.GET)
//	public ResponseEntity<List<Solution>> getSolutions(@RequestParam(value = "active", required = false) String active,
//                               @RequestParam(value = "uuid", required = false) String uuid,
//                               @RequestParam(value = "name", required = false) String name,
//                               @RequestParam(value = "authorLogin", required = false) String authorLogin,
//                               @RequestParam(value = "modelType", required = false) String modelType,
//                               @RequestParam(value = "toolkitType", required = false) String toolkitType,
//                               @RequestParam(value = "publishStatus", required = false) String publishStatus,
//                               @RequestParam(value = "publishRequest", required = false) String publishRequest,
//                               @RequestParam(value = "subject1", required = false) String subject1,
//                               @RequestParam(value = "subject2", required = false) String subject2,
//                               @RequestParam(value = "subject3", required = false) String subject3,
//                               @RequestParam(value = "tag", required = false) String tag,
//                               @RequestParam(value = "filter", required = false) String filter,
//                                Pageable pageable) {
//		List<Solution> result = null;
//		try {
//			logger.debug(" getSolutions() Begin ");
//			result = solutionService.getAllSolutions(active, uuid, name, authorLogin, modelType, toolkitType, publishStatus,
//                publishRequest, subject1, subject2, subject3, tag, filter, pageable);
//
//		} catch (Exception e) {
//			logger.error(" Exception in getSolutions() ", e);
//		}
//		logger.debug(" getSolutions() End ");
//        HttpHeaders headers = new HttpHeaders();
//        return new ResponseEntity<>(result, headers, HttpStatus.OK);
//	}

	@ApiOperation(value = "Save the Composite Solution cdump")
	@PostMapping(value = "/compositeSolutions/cdumps")
	@ResponseBody
	public ResponseEntity<String> saveCompositeSolutionCdump(@RequestBody CompositeSolution solution) {
        String result = "";
        String error = "{\"errorCode\" : \"%s\", \"errorDescription\" : \"%s\"}";
        logger.debug(" updateCompositeSolution() Begin ");
        boolean success = false;
        try {
            compositeSolutionService.saveCompositeSolutionCdump(solution);
            success = true;
        } catch (Exception e) {
            logger.error(" Exception in saveCompositeSolution() ", e);
            result = String.format(error, configurationProperties.getCompositionSolutionErrorCode(),
                configurationProperties.getCompositionSolutionErrorDesc());
        }
        logger.debug(" updateCompositeSolution() End ");
        if (success){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }

	}

    @ApiOperation(value = "Create the Composite Solution")
    @PostMapping(value = "/compositeSolutions")
    @ResponseBody
    public ResponseEntity<String> createCompositeSolution(@RequestBody Solution solution) {
        String result = "";
        String error = "{\"errorCode\" : \"%s\", \"errorDescription\" : \"%s\"}";
        logger.debug(" createCompositeSolution() Begin ");

        String isValidmsg = checkMandatoryFieldsforSave(solution);
        boolean success = false;
        if (null != isValidmsg) {
            result = String.format(error, "603", isValidmsg);
        } else {
            logger.debug(" SuccessFully validated mandatory fields ");
            try {
                result = compositeSolutionService.createCompositeSolution(solution);
                success = true;
            } catch (Exception e) {
                logger.error(" Exception in saveCompositeSolution() ", e);
                result = String.format(error, configurationProperties.getCompositionSolutionErrorCode(),
                    configurationProperties.getCompositionSolutionErrorDesc());
            }
        }

        logger.debug(" saveCompositeSolution() End ");
        if (success){
            return ResponseEntity.status(201).body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }

    }

	public String checkMandatoryFieldsforSave(Solution solution) {
		List<String> errorList = new ArrayList<>();

		if (null == solution.getAuthorLogin().trim()) {
			errorList.add("userId is missing");
		}
		if (null == solution.getName().trim()) {
			errorList.add("Solution Name is missing");
		}
		if (null == solution.getVersion()) {
			errorList.add("Version is missing");
		}
		if (!errorList.isEmpty()) {
			return errorList.toString();
		} else {
			return null;
		}

	}

	@ApiOperation(value = "add Node Operation")
	@PostMapping(value = "/solutions/nodes")
	public ResponseEntity<String> addNode(@RequestBody @Valid SolutionNodes solutionNodes) {
		String result = "";
		logger.debug(" addNode()  : Begin");
        boolean success = false;
		try {
			boolean validNode = solutionService.validateNode(solutionNodes.getNode());
			if (validNode) {
				if (solutionNodes.getSolutionId() != null ) {
					result = solutionService.addNode(solutionNodes.getName(), solutionNodes.getUserId(), solutionNodes.getSolutionId(), solutionNodes.getCdumpVersion(), solutionNodes.getNode());
					success = true;
				} else {
					result = "{\"error\": \"Either SolutionId and Version need to Pass\"}";
				}
			} else {
				result = "{\"error\": \"JSON schema not valid, Please check the input JSON\"}";
			}
		} catch (Exception e) {
			logger.error("Exception in  addNode() ", e);
		}
		logger.debug(" addNode()  : End");
        if (success){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }
	}

	@ApiOperation(value = "Gets existing composite solution details for specified solutionId")
	@GetMapping(value = "/solutions/compositeSolutionGraphs")
	@ResponseBody
	public ResponseEntity<String> readCompositeSolutionGraph(@RequestParam(value = "solutionId") String solutionId,
                                                             @RequestParam(value = "login") String login) {
		logger.debug(" readCompositeSolutionGraph()  : Begin");
		String result;
        boolean success = false;
		try {
			result = solutionService.readCompositeSolutionGraph(login, solutionId);
			success = true;
		} catch (Exception e) {
			logger.error("Failed to read the CompositeSolution", e);
			result = "";
		}
		logger.debug(" readCompositeSolutionGraph()  : End");
        if (success){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }
	}

	@ApiOperation(value = "Modify Node Operation")
	@PutMapping(value = "/solutions/nodes")
	@ResponseBody
	public ResponseEntity<String> modifyNode(@RequestBody ModifyNode modifyNode) {
	    System.out.println("ModifyNode********************************" + modifyNode.toString());
		String result = null;
		FieldMap fieldMap = null;
		DataBrokerMap databrokerMap = null;
		CollatorMap collatorMap = null;
		SplitterMap splitterMap = null;
		logger.debug("------- modifyNode() ------- : Begin");
        boolean success = false;
		DataConnector dataConnector = modifyNode.getDataConnector();
		try {
			if(null != dataConnector){
				if(null != dataConnector.getFieldMap()){
					fieldMap = dataConnector.getFieldMap();
				}
				if(null != dataConnector.getDatabrokerMap()){
					databrokerMap = dataConnector.getDatabrokerMap();
				}
				if(null != dataConnector.getCollatorMap()){
					collatorMap = dataConnector.getCollatorMap();
				}
				if(null != dataConnector.getSplitterMap()){
					splitterMap = dataConnector.getSplitterMap();
				}
			}
			result = solutionService.modifyNode(modifyNode.getUserId(), modifyNode.getSolutionId(), modifyNode.getNodeId(),
                modifyNode.getNodeName(), modifyNode.getNdata(), fieldMap, databrokerMap, collatorMap, splitterMap);
			success = true;
		} catch (Exception e) {
			logger.error("-------Exception in  modifyNode() -------", e);
		}
		logger.debug("------- modifyNode() ------- : End");
        if (success){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }
	}

    @ApiOperation(value = "add link Operation")
    @PostMapping(value = "/solutions/links")
    public ResponseEntity<String> addLink(@RequestBody(required = false) @Valid Link link) {
        logger.debug(" link()  : Begin");
        String result = null;
        boolean linkAdded = false;
        String resultTemplate = "{\"success\" : \"%s\", \"errorDescription\" : \"%s\"}";
        try {
            if (link.getSourceNodeName() != null && link.getTargetNodeName() != null && link.getTargetNodeId() != null
                && link.getTargetNodeCapabilityName() != null) {
                if (solutionService.validateProperty(link.getProperty())) {
                    linkAdded = solutionService.addLink(link.getUserId(), link.getLinkName(), link.getLinkId(), link.getSourceNodeName(), link.getSourceNodeId(),
                        link.getTargetNodeName(), link.getTargetNodeId(), link.getSourceNodeRequirement(), link.getTargetNodeCapabilityName(),
                        link.getSolutionId(), link.getProperty(), link.getInput(), link.getOutput(), link.getStart(),link.getEnd());
                    if (linkAdded) {
                        result = String.format(resultTemplate, true, "");
                    } else {
                        result = String.format(resultTemplate, false, "Link not added");
                    }
                } else {
                    result = String.format(resultTemplate, false, "Invalid input: properties");
                }
            } else {
                result = solutionService.validateAddLinkInputs(link.getSourceNodeName(), link.getLinkId(), link.getTargetNodeName(), link.getTargetNodeCapabilityName(), link.getSourceNodeId());
            }
        } catch (Exception e) {
            logger.error(" Exception in link() ", e);
        }
        logger.debug(" link()  : End");
        if (linkAdded){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }
    }


	@ApiOperation(value = "Modify Link Operation")
	@PutMapping(value = "/solutions/links")
	@ResponseBody
	public ResponseEntity<String> modifyLink(@RequestBody Link modifyLink) {
		String result = null;
		logger.debug(" modifyLinkName()  : Begin");
		boolean success = false;
		try {
			result = solutionService.modifyLinkName(modifyLink.getUserId(), modifyLink.getSolutionId(), modifyLink.getLinkId(), modifyLink.getLinkName());
			success = true;
		} catch (Exception e) {
			logger.error("Exception in  modifyLinkName() ", e);
		}
		logger.debug(" modifyLinkName()  : End");
        if (success){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }
	}

	@ApiOperation(value = "delete Node Operation")
	@DeleteMapping(value = "/solutions/{solutionId}/nodes/{nodeId}/{userId}")
	public ResponseEntity<String> deleteNode(@PathVariable String solutionId, @PathVariable String nodeId, @PathVariable String userId) {
		logger.debug(" deleteNode() in SolutionController Begin ");
		String result = "";
		String resultTemplate = "{\"success\":\"%s\", \"errorMessage\":\"%s\"}";
        boolean deletedNode =false;
		if (null == userId || null == nodeId || null == solutionId) {
			result = String.format(resultTemplate, false, "Mandatory feild(s) missing");
		} else {
			try {
				deletedNode = solutionService.deleteNode(userId, solutionId, nodeId);
				if (deletedNode) {
					result = String.format(resultTemplate, true, "");
				} else {
					result = String.format(resultTemplate, false, "Invalid Node Id – not found");
				}
			} catch (Exception e) {
				logger.error(" Exception in deleteNode() ", e);
			}
		}
		logger.debug(" deleteNode() Ends ");
		if (deletedNode){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }

	}


    @ApiOperation(value = "delete link Operation")
    @DeleteMapping(value = "/solutions/{solutionId}/links/{linkId}/{userId}")
    public ResponseEntity<String> deleteLink(@PathVariable String solutionId, @PathVariable String linkId, @PathVariable String userId) {
        logger.debug(" deleteLink() in SolutionController begins -");
        String result = "";
        boolean deletedLink = false;
        String resultTemplate = "{\"success\":\"%s\", \"errorMessage\":\"%s\"}";
        if (null == userId || null == linkId || null == solutionId) {
            result = String.format(resultTemplate, false, "Mandatory feild(s) missing");
        } else {
            try {
                deletedLink = solutionService.deleteLink(userId, solutionId, linkId);
                if (deletedLink) {
                    result = String.format(resultTemplate, true, "");
                } else {
                    result = String.format(resultTemplate, false, "Invalid Link Id – not found");
                }
            } catch (Exception e) {
                logger.error(" Exception in deleteLink() in SolutionController ", e);
            }
        }
        logger.debug(" deleteLink() in SolutionController Ends ");
        if (deletedLink){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @ApiOperation(value = "Clear Composite Solution Operation")
    @PutMapping (value = "/compositeSolutions/cdumps")
    @ResponseBody
    public ResponseEntity<String> clearCompositeSolution(@RequestBody CompositeSolution solution) {
        logger.debug(" clearCdumpFile(): Begin ");
        String result = "";
        boolean success = false;
        try {
            result = compositeSolutionService.clearCdumpFile(solution.getAuthorLogin(), solution.getUuid());
            success = true;
        } catch (Exception e) {
            logger.error(" Exception in clearCdumpFile() ", e);
        }
        logger.debug(" clearCdumpFile(): End ");
        if (success){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }

    }

	@ApiOperation(value = "Close Composite Solution Operation")
	@DeleteMapping(value = "/compositeSolutions/cdumps/{solutionId}/{userId}")
	@ResponseBody
	public ResponseEntity<String> closeCompositeSolution(@PathVariable String solutionId, @PathVariable String userId) {
		logger.debug(" closeCompositeSolution(): Begin ");
		String result = "";
		boolean success = false;
		try {
			result = compositeSolutionService.deleteCdumpFile(userId, solutionId);
			success = true;
		} catch (Exception e) {
			logger.error(" Exception in closeCompositeSolution() ", e);
		}
		logger.debug(" closeCompositeSolution(): End ");
        if (success){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }
	}

    @ApiOperation(value = "set the ProbeIndicator")
    @PutMapping(value = "/compositeSolutions/cdumps/probeIndicator")
    public @ResponseBody
    ResponseEntity<String> setProbeIndicator(@RequestBody CompositeSolution solution) {
        DSResult successErrorMessage = null;
        logger.info("setProbeIndicator() in SolutionController Begin:" + solution.toString());
        try {
            successErrorMessage = compositeSolutionService.setProbeIndicator(solution.getAuthorLogin(), solution.getUuid(),solution.getProbeIndicator());

        }catch (Exception e) {
            logger.error("Exception in setProbeIndicator() in SolutionController", e);
        }
        logger.debug("setProbeIndicator() in SolutionController End");
        if (successErrorMessage.isSuccess()){
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.badRequest().body(successErrorMessage.getErrorDescription());
        }
    }

//	@ApiOperation(value = "Fetch the list of active public Composite Solution for the specified User Id")
//	@RequestMapping(value = "/solutions", method = RequestMethod.GET, produces = "text/plain")
//	@ResponseBody
//	public ResponseEntity<List<Solution>>  getSolutions(@RequestParam(value = "userId") String userId,
//			@RequestParam(value = "filter", required = false) String filter, Pageable pageable) {
//		logger.debug(" getCompositeSolutions()  : Begin");
//		List<Solution> result = new ArrayList<>();
//		try {
//			result = solutionService.getSolutions(userId, filter, pageable);
//
//		} catch (Exception e) {
//			logger.error(" Exception in getCompositeSolutions()", e);
//
//		}
//		logger.debug(" getCompositeSolutions()  : End");
//        HttpHeaders headers = new HttpHeaders();
//        return new ResponseEntity<>(result, headers, HttpStatus.OK);0
//	}

	@ApiOperation(value = "Fetch the all the maching models for any specified model")
	@RequestMapping(value = "/solutions/matchingModels", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<String> getMatchingModels(@RequestParam(value = "userId", required = false) String userId,
                                                    @RequestParam(value = "portType") String portType,
                                                    @RequestParam(value = "protobufJsonString") String protobufJsonString) {
		logger.debug(" getMatchingModels()  : Begin");
		String result = "";
		String resultTemplate = "{\"success\" : %s,\"matchingModels\" : %s}";
		String error = "{\"error\" : %s";
		boolean success = false;
		try {
//		    protobufJsonString = URLDecoder.decode(protobufJsonString);
			result = solutionService.getMatchingModels(userId, portType, protobufJsonString);
			if (!result.equals("false")) {
				result = String.format(resultTemplate, "true", result);
				success = true;
			} else {
				result = String.format(resultTemplate, "false", "No matching models found");
			}
		} catch (Exception e) {
			logger.error(" Exception in getMatchingModels() ", e);
			result = String.format(error, e.getMessage());
		}
		logger.debug(" getMatchingModels()  : End");
        if (success){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }
	}

	@ApiOperation(value = "Validate Composite Solution")
	@RequestMapping(value = "/validateCompositeSolution", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> validateCompositeSolution(@RequestBody CompositeSolution solution) {
		logger.debug("validateCompositeSolution() : Begin ");
        DSResult result = null;
		try {
			result = compositeSolutionService.validateCompositeSolution(solution.getAuthorLogin(), solution.getName(), solution.getUuid(), solution.getVersion());

		} catch (Exception e) {
			logger.debug(" Exception in validateCompositeSolution() ", e);
		}
		logger.debug("validateCompositeSolution() : End ");
        if (result.isSuccess()){
            return ResponseEntity.ok().body(result.getErrorDescription());
        }else {
            return ResponseEntity.badRequest().body(result.getErrorDescription());
        }
	}

    @ApiOperation(value = "Delete the CompositeSolution")
    @DeleteMapping(value = "/compositeSolutions/{solutionId}")
    @ResponseBody
    public ResponseEntity<String> deleteCompositeSolution(@PathVariable String solutionId) {
        String resultTemplate = "{\"success\":\"%s\",\"errorMessage\":\"%s\"}";
        String result;
        logger.info(" deleteCompositeSolution() solutionId : " + solutionId);
        boolean deleted = false;
        try {

            deleted = compositeSolutionService.deleteCompositeSolution(solutionId);
            if (!deleted) {
                result = String.format(resultTemplate, "false", "Requested Solution Not Found");
            } else {
                result = String.format(resultTemplate, "true", "");
            }
        } catch (Exception e) {
            logger.debug("Exception in  deleteCompositeSolution() ", e);
            result = String.format(resultTemplate, "false", "Exception : Requested Solution Not Found");
        }
        logger.debug(" deleteCompositeSolution()  : End");
        if (deleted){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }

    }

    @ApiOperation(value = "Gets TOSCA for specified solutionId")
    @GetMapping(value = "/solutions/tosca")
    @ResponseBody
    public ResponseEntity<String> fetchJsonTOSCA(@RequestParam(value = "solutionId") String solutionId) {
        logger.debug(" fetchJsonTOSCA()  : Begin");
        String result;
        boolean success = false;
        try {
            result = solutionService.fetchJsonTOSCA(solutionId);
            success = true;
        } catch (Exception e) {
            logger.error("Failed to fetchJsonTOSCA", e);
            result = "";
        }
        System.out.println("*********************tosca:" + result);
        logger.debug(" fetchJsonTOSCA()  : End");
        if (success){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @ApiOperation(value = "Get the profobuf file details for specified solutionID")
    @GetMapping(value = "/solutions/protobuf")
    @ResponseBody
    public ResponseEntity<String> fetchProtoBufJSON(@RequestParam(value = "solutionId") String solutionId) {
        logger.debug(" fetchProtoBufJSON()  : Begin");
        String result;
        boolean success = false;
        try {
            result = solutionService.fetchProtoBufJSON(solutionId);
            success = true;
        } catch (Exception e) {
            logger.error("Failed to fetchProtoBufJSON", e);
            result = "";
        }
        logger.debug(" fetchProtoBufJSON()  : End");
        System.out.println("*********************protobufjson:" + result);
        if (success){
            return ResponseEntity.ok().body(result);
        }else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}

