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
public class Service implements Serializable {

    private static final long serialVersionUID = 5637513244290307708L;

    private Call[] calls;
    private Provide[] provides;

    public Service() {

    }

    /**
     *
     * @param calls
     *            Array of Call objects
     * @param provides
     *            Array of Provide objects
     */
    public Service(Call[] calls, Provide[] provides) {
        super();
        this.calls = calls;
        this.provides = provides;
    }

    /**
     * @return the calls
     */
    public Call[] getCalls() {
        return calls;
    }

    /**
     * @param calls
     *            the calls to set
     */
    public void setCalls(Call[] calls) {
        this.calls = calls;
    }

    /**
     * @return the provides
     */
    public Provide[] getProvides() {
        return provides;
    }

    /**
     * @param provides
     *            the provides to set
     */
    public void setProvides(Provide[] provides) {
        this.provides = provides;
    }

}

