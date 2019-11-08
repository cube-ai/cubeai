/*-
 * ===============LICENSE_START=======================================================
 * Acumos
 * ===================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
 * ===================================================================================
 * This Acumos software file is distributed by AT&T and Tech Mahindra
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * This file is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===============LICENSE_END=========================================================
 */

package com.wyy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:application.properties")
public class ConfigurationProperties {

	@Value("${tosca.outputfolder}")
	private String toscaOutputFolder;

    @Value("${compositionSolutionErrorCode}")
    private String compositionSolutionErrorCode;

    @Value("${compositionSolutionErrorDesc}")
    private String compositionSolutionErrorDesc;

    @Value("模型组合")
    private String toolKit;


    @Value("蓝图文件")
    private String blueprintArtifactType;

    public String getCdumpArtifactType() {
        return cdumpArtifactType;
    }

    @Value("CDUMP文件")
    private String cdumpArtifactType;


    public String getToscaInputArtifactType() {
        return toscaInputArtifactType;
    }

    @Value("TOSCA生成器输入文件")
    private String toscaInputArtifactType;

    @Value("PROTOBUF文件")
    private String protoArtifactType;

    @Value("${gdmType}")
    private String gdmType;

    @Value("${databrokerType}")
    private String databrokerType;

    @Value("${defaultCollatorType}")
    private String defaultCollatorType;

    @Value("${defaultSplitterType}")
    private String defaultSplitterType;

    @Value("${splitterType}")
    private String splitterType;

    @Value("${collatorType}")
    private String collatorType;

    @Value("模型镜像")
    private String modelImageArtifactType;

    @Value("${matchingInputPortType}")
    private String matchingInputPortType;

    @Value("${matchingOutputPortType}")
    private String matchingOutputPortType;

    @Value("${privateCacheRemovalTime}")
    private int privateCacheRemovalTime;

    public String getProtoArtifactType() {
        return protoArtifactType;
    }

    public void setProtoArtifactType(String protoArtifactType) {
        this.protoArtifactType = protoArtifactType;
    }

    public int getPrivateCacheRemovalTime() {
        return privateCacheRemovalTime;
    }

    public String getMatchingInputPortType() {
        return matchingInputPortType;
    }

    public String getMatchingOutputPortType() {
        return matchingOutputPortType;
    }

    public String getSplitterType() {
        return splitterType;
    }

    public String getCollatorType() {
        return collatorType;
    }


    public String getDefaultCollatorType() {
        return defaultCollatorType;
    }

    public String getDefaultSplitterType() {
        return defaultSplitterType;
    }

    /**
     * @return the modelImageArtifactType
     */
    public String getModelImageArtifactType() {
        return modelImageArtifactType;
    }

    /**
     * @return the gdmType
     */
    public String getGdmType() {
        return gdmType;
    }

    /**
     * @return the databrokerType
     */
    public String getDatabrokerType() {
        return databrokerType;
    }

    /**
     * @return the blueprintArtifactType
     */
    public String getBlueprintArtifactType() {
        return blueprintArtifactType;
    }

    /**
     * @return the compositionSolutionErrorCode
     */
    public String getCompositionSolutionErrorCode() {
        return compositionSolutionErrorCode;
    }

    /**
     * @return the compositionSolutionErrorDesc
     */
    public String getCompositionSolutionErrorDesc() {
        return compositionSolutionErrorDesc;
    }

    /**
     * @return the toolKit
     */
    public String getToolKit() {
        return toolKit;
    }

	/**
	 * @return the toscaOutputFolder
	 */
	public String getToscaOutputFolder() {
		return toscaOutputFolder;
	}


}
