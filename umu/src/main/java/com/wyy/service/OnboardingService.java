package com.wyy.service;

import com.alibaba.fastjson.JSONObject;
import com.wyy.domain.*;
import com.wyy.dto.UserDTO;
import com.wyy.util.FileUtil;
import com.wyy.service.tosca.ProtobufGenerator;
import com.wyy.service.tosca.TgifGenerator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.File;
import java.time.Instant;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import com.wyy.service.tosca.vo.tgif.Tgif;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;


@Service
public class OnboardingService {
    private static final Logger log = LoggerFactory.getLogger(OnboardingService.class);

    private final UmmClient ummClient;
    private final UaaClient uaaClient;
    private final MessageService messageService;
    private final NexusArtifactClient nexusArtifactClient;
    private final MicroserviceGenerator microserviceGenerator;

    public OnboardingService(UmmClient ummClient,
                             UaaClient uaaClient,
                             MessageService messageService,
                             NexusArtifactClient nexusArtifactClient,
                             MicroserviceGenerator microserviceGenerator) {
        this.ummClient = ummClient;
        this.uaaClient = uaaClient;
        this.messageService = messageService;
        this.nexusArtifactClient = nexusArtifactClient;
        this.microserviceGenerator = microserviceGenerator;
    }

    public void onboarding(String taskUuid) {

        Task task = ummClient.getTasks(taskUuid).get(0);
        this.saveTaskProgress(task, "正在执行", 5, "启动模型部署...", null);

        UserDTO user = null;
        try {
            user = uaaClient.getUser(task.getUserLogin()).getBody();
        } catch (Exception e) {
            this.saveTaskProgress(task, "失败", 100, "获取用户信息失败。", Instant.now());
            this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "失败", 100,
                "获取用户信息失败。");
            return;
        }

        Solution solution = new Solution();
        solution.setUuid(task.getTargetUuid());
        solution.setAuthorLogin(task.getUserLogin());
        solution.setAuthorName(user.getFullName());
        // solution的其他基础字段值在umm中的createSolution实际写数据库时填写

        JSONObject modelFiles = new JSONObject();
        modelFiles.put("basePath", System.getProperty("user.home") + "/tempfile/ucumosmodels/" + task.getUserLogin() + "/" + task.getUuid());

        if (this.doOnboarding(task, solution, modelFiles)) {
            this.messageService.sendMessage(solution.getAuthorLogin(),
                "模型 " + solution.getName() + " 导入完成",
                "你的模型 " + solution.getName() + " 已导入系统！\n\n请点击下方[目标页面]按钮进入模型页面...",
                "/pmodelhub/#/solution/" + solution.getUuid(),
                false);

        } else {
            this.revertbackOnboarding(taskUuid, modelFiles.getString("basePath"));
            this.messageService.sendMessage(solution.getAuthorLogin(),
                "模型 " + task.getTaskName() + " 导入失败",
                "你的模型 " + task.getTaskName() + " 导入失败！\n\n请点击下方[目标页面]按钮查看任务执行情况...",
                "/pmodelhub/#/task-onboarding/" + taskUuid + "/" + task.getTaskName(),
                false);
        }
    }

    private Boolean doOnboarding(Task task, Solution solution, JSONObject modelFiles) {
        this.saveTaskProgress(task, "正在执行", 10, "提取模型文件...", null);

        if (!extractModelFile(task, modelFiles)) {
            this.saveTaskProgress(task, "失败", 100, "提取模型文件失败。", Instant.now());
            FileUtil.deleteDirectory(modelFiles.getString("basePath"));
            return false;
        }
        this.saveTaskProgress(task, "正在执行", 15, "完成文件验证并成功提取模型文件。", null);

        if (!this.createSolution(task, solution, modelFiles)) {
            this.saveTaskProgress(task, "失败", 100, "创建模型对象失败。", Instant.now());
            FileUtil.deleteDirectory(modelFiles.getString("basePath"));
            return false;
        }
        this.saveTaskProgress(task, "正在执行", 30, "完成模型对象创建。", null);

        if (!this.addArtifacts(task, solution, modelFiles)) {
            this.saveTaskProgress(task, "失败", 100, "添加artifact文件失败。", Instant.now());
            FileUtil.deleteDirectory(modelFiles.getString("basePath"));
            return false;
        }
        this.saveTaskProgress(task, "正在执行", 50, "完成添加artifact文件。", null);

        if (!this.generateTOSCA(task, solution, modelFiles)) {
            this.saveTaskProgress(task, "失败", 100, "生成TOSCA文件失败。", Instant.now());
            FileUtil.deleteDirectory(modelFiles.getString("basePath"));
            return false;
        }
        this.saveTaskProgress(task, "正在执行", 70, "完成TOSCA文件生成。", null);

        if (!this.generateMicroService(task, solution, modelFiles)) {
            this.saveTaskProgress(task, "失败", 100, "生成微服务失败。", Instant.now());
            FileUtil.deleteDirectory(modelFiles.getString("basePath"));
            return false;
        }
        this.saveTaskProgress(task, "正在执行", 90, "完成微服务生成。", null);

        FileUtil.deleteDirectory(modelFiles.getString("basePath"));
        this.saveTaskProgress(task, "成功", 100, "完成模型导入。", Instant.now());

        return true;
    }

    private void revertbackOnboarding(String solutionUuid, String basePath) {
        FileUtil.deleteDirectory(basePath);

        List<Solution> solutionList = this.ummClient.getSolutions(solutionUuid);
        if (solutionList.size() > 0) {
            Solution solution = solutionList.get(0);
            List<Artifact> artifactList = this.ummClient.getAllArtifacts(solutionUuid);

            for (Artifact artifact: artifactList) {
                if (!artifact.getType().equals("DOCKER镜像")) {
                    this.nexusArtifactClient.deleteArtifact(artifact.getUrl());
                }
                this.ummClient.deleteArtifact(artifact.getId());
            }

            this.ummClient.deleteSolution(solution.getId());
        }
    }

    private void saveTaskProgress(Task task, String taskStatus, Integer taskProgress, String description, Instant endDate) {
        task.setTaskStatus(taskStatus);
        task.setTaskProgress(taskProgress);
        task.setDescription(description);
        task.setEndDate(endDate);
        try {
            this.ummClient.updateTask(task);
        } catch (Exception e) {
            log.info("saveTaskProgress fail!");
        }

    }

    private void saveTaskStepProgress(String taskUuid, String stepName, String stepStatus, Integer stepProgress, String description) {
        TaskStep taskStep = new TaskStep();
        taskStep.setTaskUuid(taskUuid);
        taskStep.setStepName(stepName);
        taskStep.setStepStatus(stepStatus);
        taskStep.setStepProgress(stepProgress);
        taskStep.setDescription(description);
        taskStep.setStepDate(Instant.now());
        try {
            this.ummClient.createTaskStep(taskStep);
        } catch (Exception e) {
            log.info("saveTaskStepProgress fail!");
        }

    }

    private Boolean extractModelFile(Task task, JSONObject modelFiles) {

        this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "执行", 20,
            "开始从上传压缩包中提取模型文件...");

        File filefolder = new File(modelFiles.getString("basePath"));

        if (!filefolder.exists()) {
            this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "失败", 100,
                "保存压缩文件的文件夹不存在。");
            return false;
        }

        String[] tmpList = filefolder.list();
        if (null == tmpList || tmpList.length < 1) {
            this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "失败", 100,
                "压缩文件包不存在。");
            return false;
        }

        String fileName = tmpList[0];
        if (!fileName.contains(".zip")) {
            this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "失败", 100,
                "压缩文件名后缀不是.zip。");
            return false;
        }

        String filePath = modelFiles.getString("basePath") + "/" + fileName;
        File file = new File(filePath);
        if (!FileUtil.validateModelFile(file)) {
            this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "失败", 100,
                "zip文件中包含的模型文件不全。");
            return false;
        }

        if (!FileUtil.extractZipFile(file, modelFiles.getString("basePath"))) {
            this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "失败", 100,
                "解压缩zip文件失败。");
            return false;
        }

        this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "执行", 40,
            "解压缩zip文件。");


        file.delete();
        this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "执行", 60,
            "删除原始zip文件。");
        this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "执行", 80,
            "获取文件列表。");

        List<File> fileList = FileUtil.getListOfFiles(modelFiles.getString("basePath"));
        if (null != fileList) {
            for (File file1 : fileList){
                if (file1.isFile()
                    && (file1.getName().contains(".zip")
                    || file1.getName().contains(".jar")
                    || file1.getName().contains(".bin")
                    || file1.getName().contains(".tar")
                    || file1.getName().toUpperCase().contains(".R"))) {
                    modelFiles.put("modelFile", new File(file1.getAbsolutePath()));
                }
                if (file1.isFile() && file1.getName().contains(".proto")) {
                    modelFiles.put("schemaFile", new File(file1.getAbsolutePath()));
                }
                if (file1.isFile() && file1.getName().contains(".json")) {
                    modelFiles.put("metadataFile", new File(file1.getAbsolutePath()));
                }
            }
        }

        this.saveTaskStepProgress(task.getUuid(), "提取模型文件", "成功", 100,
            "完成文件验证并成功提取模型文件。");
        return true;
    }


    private Boolean createSolution(Task task, Solution solution, JSONObject modelFiles) {

        this.saveTaskStepProgress(task.getUuid(), "创建模型对象", "执行", 20,
            "开始创建模型对象...");

        if (!this.parseMetadata(solution, modelFiles.getObject("metadataFile", File.class))) {
            this.saveTaskStepProgress(task.getUuid(), "创建模型对象", "失败", 100,
                "解析metadata.json文件失败。");
            return false;
        }
        this.saveTaskStepProgress(task.getUuid(), "创建模型对象", "执行", 50,
            "解析metadata.json文件。");

        try {
            this.ummClient.createSolution(solution);
        } catch (Exception e) {
            this.saveTaskStepProgress(task.getUuid(), "创建模型对象", "失败", 100,
                "向数据库中保存模型对象失败。");
            return false;
        }

        this.saveTaskStepProgress(task.getUuid(), "创建模型对象", "执行", 80,
            "向数据库中保存模型对象。");

        this.saveTaskStepProgress(task.getUuid(), "创建模型对象", "成功", 100,
            "完成模型对象创建。");
        return true;
    }

    private Boolean parseMetadata(Solution solution, File dataFile) {
        JSONObject metadataJson;

        try {
            String jsonString = FileUtils.readFileToString(dataFile, "UTF-8");
            // solution.setMetadata(jsonString); // 作废metadata字段，直接从文件中获取

            metadataJson = JSONObject.parseObject(jsonString);

            //==========================================================================================================
            // validate schemaVersion
            String schemafile = null;
            String schemaVersion = metadataJson.get("schema").toString();

            if (schemaVersion.contains("3")) {
                schemafile = "/model-schema/model-schema-0.3.0.json";
            } else if (schemaVersion.contains("4")) {
                schemafile = "/model-schema/model-schema-0.4.0.json";
            } else if (schemaVersion.contains("5")) {
                schemafile = "/model-schema/model-schema-0.5.0.json";
            } else {
                log.error("No matching model schema");
                return false;
            }

            JsonNode schema = JsonLoader.fromResource(schemafile);    // 直接以resources为根目录读取
            JsonNode metadataJson1 = JsonLoader.fromFile(dataFile);
            JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
            com.github.fge.jsonschema.main.JsonValidator validator = factory.getValidator();
            ProcessingReport report = validator.validate(schema, metadataJson1);
            if (!report.isSuccess()) {
                StringBuilder sb = new StringBuilder();
                for (ProcessingMessage processingMessage : report) {
                    if (!processingMessage.getMessage()
                        .equals("the following keywords are unknown and will be ignored: [self]"))
                        sb.append(processingMessage.getMessage() + "\n");
                }
                log.error("Input JSON is not as per schema cause: '" + sb.toString() + "'");
                return false;
            }
            //==========================================================================================================

            if (metadataJson.containsKey("name")) {
                solution.setName(metadataJson.get("name").toString());
            }

            if (metadataJson.containsKey("modelVersion")) {
                solution.setVersion(metadataJson.get("modelVersion").toString());
            } else {
                solution.setVersion("snapshot");
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private Boolean addArtifacts(Task task, Solution solution, JSONObject modelFiles) {

        this.saveTaskStepProgress(task.getUuid(), "添加artifact", "执行", 20,
            "开始添加artifact文件...");

        if (!this.addArtifact(solution, modelFiles.getObject("modelFile", File.class), "模型程序")) {
            this.saveTaskStepProgress(task.getUuid(), "添加artifact", "失败", 100,
                "添加模型镜像artifact文件失败。");
            return false;
        }
        this.saveTaskStepProgress(task.getUuid(), "添加artifact", "执行", 40,
            "成功添加模型镜像artifact文件：" + modelFiles.getObject("modelFile", File.class).getName());

        if (!this.addArtifact(solution, modelFiles.getObject("schemaFile", File.class), "PROTOBUF文件")) {
            this.saveTaskStepProgress(task.getUuid(), "添加artifact", "失败", 100,
                "添加PROTOBUF artifact文件失败。");
            return false;
        }
        this.saveTaskStepProgress(task.getUuid(), "添加artifact", "执行", 70,
            "成功添加PROTOBUF artifact文件：" + modelFiles.getObject("schemaFile", File.class).getName());

        if (!this.addArtifact(solution, modelFiles.getObject("metadataFile", File.class), "元数据")) {
            this.saveTaskStepProgress(task.getUuid(), "添加artifact", "失败", 100,
                "添加元数据artifact文件失败。");
            return false;
        }
        this.saveTaskStepProgress(task.getUuid(), "添加artifact", "执行", 90,
            "成功添加元数据artifact文件：" + modelFiles.getObject("metadataFile", File.class).getName());

        this.saveTaskStepProgress(task.getUuid(), "添加artifact", "成功", 100,
            "完成artifact文件添加。");
        return true;
    }

    private Boolean addArtifact(Solution solution, File file, String type) {

        String shortUrl = solution.getAuthorLogin()+ "/" + solution.getUuid() + "/artifact/" + file.getName();
        String longUrl= this.nexusArtifactClient.addArtifact(shortUrl, file);

        if (null == longUrl) {
            return false;
        }

        Artifact artifact = new Artifact();
        artifact.setSolutionUuid(solution.getUuid());
        artifact.setName(file.getName());
        artifact.setType(type);
        artifact.setUrl(longUrl);
        artifact.setFileSize(file.length());

        try {
            this.ummClient.createArtifact(artifact);
        } catch (Exception e) {
            log.error("向数据库中写Artifact失败。");
            return false;
        }

        return true;
    }

    private Boolean generateTOSCA(Task task, Solution solution, JSONObject modelFiles) {

         this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "执行", 10,
            "开始生成TOSCA文件...");

        String  protoJsonStr = new ProtobufGenerator().createProtoJson(modelFiles.getObject("schemaFile", File.class));
        this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "执行", 20,
            "将POTOBUF文件内容转换成JSON格式的TOSCA-SCHEMA字符串。");

        PrintWriter writer = null;
        try{
            writer = new PrintWriter(modelFiles.getString("basePath") + "/protobuf.json", "UTF-8");
            writer.write(protoJsonStr);
            modelFiles.put("protobufFile", new File(modelFiles.getString("basePath") + "/protobuf.json"));
        } catch (Exception e) {
            log.error("将JSON格式的TOSCA-SCHEMA字符串写入临时文件失败。");
            return false;
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
        this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "执行", 30,
            "将JSON格式的TOSCA-SCHEMA字符串写入临时文件。");

        // 上传文件作为artifact到nexus服务器，并添加信息到数据库。
        if (!this.addArtifact(solution, new File(modelFiles.getString("basePath") + "/protobuf.json"), "TOSCA-SCHEMA")) {
            this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "失败", 100,
                "上传TOSCA-SCHEMA文件到NEXUS服务器，并向数据库中插入artifact记录失败。");
            return false;
        }
        this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "执行", 50,
            "上传TOSCA-SCHEMA文件到NEXUS服务器，并向数据库中插入artifact记录。");

        this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "执行", 60,
            "开始生成TOSCA生成器输入文件（tgif）...");
        FileReader fr = null;
        BufferedReader br = null;
        try{
            fr = new FileReader(modelFiles.getString("metadataFile"));
            br = new BufferedReader(fr);

            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            br.close();
            fr.close();
            String meatDataStr = sb.toString();
            this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "执行", 70,
                "从metadata文件读取JSON字符串。");

            // 读取metadata.json文件，和proto一起生成tgif
            JSONObject metaDataJson = JSONObject.parseObject(meatDataStr.replace("\t", ""));
            JSONObject protobufJson = JSONObject.parseObject(protoJsonStr.replace("\t", ""));
            Tgif tgif = new TgifGenerator().populateTgif(solution.getVersion(), metaDataJson, protobufJson);
            this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "执行", 70,
                "基于metadata和proto文件内容生成tgif。");

            // convert Tgif to json
            ObjectMapper mapper = new ObjectMapper();
            String tgifJsonString = "";
            tgifJsonString = mapper.writeValueAsString(tgif);
            tgifJsonString = tgifJsonString.replace("[null]", "[]");
            tgifJsonString = tgifJsonString.replace("null", "{}");
            writer = new PrintWriter(modelFiles.getString("basePath") + "/tgif.json", "UTF-8");
            writer.write(tgifJsonString);
            writer.close();
            modelFiles.put("tgifFile", new File(modelFiles.getString("basePath") + "/tgif.json"));
            this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "执行", 70,
                "将tgif转换成JSON格式。");

            // 上传文件作为artifact到nexus服务器，并添加信息到数据库。
            if (!this.addArtifact(solution, new File(modelFiles.getString("basePath") + "/tgif.json"), "TOSCA生成器输入文件")) {
                this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "失败", 100,
                    "上传tgif文件到NEXUS服务器，并向数据库中插入artifact记录失败。");
                return false;
            }
            this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "执行", 80,
                "上传tgif文件到NEXUS服务器，并向数据库中插入artifact记录。");
        } catch (Exception e) {
            this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "失败", 100,
                "生成TOSCA生成器输入文件（tgif）失败。");
            return false;
        }
        this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "执行", 90,
            "成功生成TOSCA生成器输入文件（tgif）。");

        this.saveTaskStepProgress(task.getUuid(), "生成TOSCA文件", "成功", 100,
            "完成TOSCA文件生成。");

        return true;
    }


    private Boolean generateMicroService(Task task, Solution solution, JSONObject modelFiles) {

        this.saveTaskStepProgress(task.getUuid(), "创建微服务", "执行", 10,
            "开始创建微服务...");

        // 创建微服务子任务不再通过调用单独的微服务来执行，而是直接在umu中执行。
        // 理由： “创建微服务”作为模型导入任务的一个子任务，是应同步执行而不是异步执行的。
        //       模型导入主程序在调用“创建微服务”后，需要等待其执行完毕之后才能继续下一步操作。
        //       而feign调用远程微服务接口，通常是有熔断设置的，超时后客户端会异常报错，不适合长时延的同步操作调用。
        //

        if (microserviceGenerator.generateMicroservice(solution, modelFiles)) {
            this.saveTaskStepProgress(task.getUuid(), "创建微服务", "成功", 100,
                "成功创建微服务docker镜像。");
            return true;
        } else {
            this.saveTaskStepProgress(task.getUuid(), "创建微服务", "失败", 100,
                "创建微服务docker镜像失败。");
            return false;
        }

    }

}
