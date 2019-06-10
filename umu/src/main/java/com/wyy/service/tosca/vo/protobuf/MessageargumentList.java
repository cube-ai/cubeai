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

package com.wyy.service.tosca.vo.protobuf;

import java.io.Serializable;

/**
 *
 * @author *********
 *
 */
public class MessageargumentList implements Serializable{

    private static final long serialVersionUID = 3921037942106214981L;
    private String role;
    private String type;
    private String name;
    private String tag;
    private ComplexType complexType;
    /**
     * @return the complexType
     */
    public ComplexType getComplexType() {
        return complexType;
    }
    /**
     * @param complexType the complexType to set
     */
    public void setComplexType(ComplexType complexType) {
        this.complexType = complexType;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
    public MessageargumentList() {
    }

}

