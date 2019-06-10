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

package com.wyy.service.tosca.vo.tgif;

import java.io.Serializable;

/**
 *
 *
 *
 */
public class Stream implements Serializable {

    private static final long serialVersionUID = -2733793486307877528L;

    private String[] subscribes;
    private String[] publishes;

    public Stream() {
        super();
    }

    /**
     *
     * @param subscribes
     *            Array of String
     * @param publishes
     *            Array of String
     */
    public Stream(String[] subscribes, String[] publishes) {
        super();
        this.subscribes = subscribes;
        this.publishes = publishes;
    }

    /**
     * @return the subscribes
     */
    public String[] getSubscribes() {
        return subscribes;
    }

    /**
     * @param subscribes
     *            the subscribes to set
     */
    public void setSubscribes(String[] subscribes) {
        this.subscribes = subscribes;
    }

    /**
     * @return the publishes
     */
    public String[] getPublishes() {
        return publishes;
    }

    /**
     * @param publishes
     *            the publishes to set
     */
    public void setPublishes(String[] publishes) {
        this.publishes = publishes;
    }

}

