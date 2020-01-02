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

package com.wyy.util;

import org.springframework.stereotype.Component;

@Component
public class ConfigurationProperties {

	private String toscaOutputFolder = "tosca";

    private String compositionSolutionErrorCode = "203";

    private String compositionSolutionErrorDesc = "Failed to save composite solution";

    private String toolKit = "模型组合";

    private String blueprintArtifactType = "蓝图文件";

    public String getCdumpArtifactType() {
        return cdumpArtifactType;
    }

    private String cdumpArtifactType = "CDUMP文件";


    public String getToscaInputArtifactType() {
        return toscaInputArtifactType;
    }

    private String toscaInputArtifactType = "TOSCA生成器输入文件";

    private String protoArtifactType = "PROTOBUF文件";

    private String gdmType = "DataMapper";

    private String databrokerType = "DataBroker";

    private String defaultCollatorType = "Array-based";

    private String defaultSplitterType = "Copy-based";

    private String splitterType = "Splitter";

    private String collatorType = "Collator";

    private String modelImageArtifactType = "模型镜像";

    private String matchingInputPortType = "input";

    private String matchingOutputPortType = "output";

    private int privateCacheRemovalTime = 10;

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
