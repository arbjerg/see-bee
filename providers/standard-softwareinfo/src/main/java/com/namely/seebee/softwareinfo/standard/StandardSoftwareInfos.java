/**
 *
 * Copyright (c) 2017-2018, Namely, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); You may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.namely.seebee.softwareinfo.standard;

import com.namely.seebee.softwareinfo.SoftwareInfo;
import com.namely.seebee.softwareinfo.standard.internal.JavaSoftwareInfo;
import com.namely.seebee.softwareinfo.standard.internal.SeeBeeSoftwareInfo;

/**
 *
 * @author Per Minborg
 */
public final class StandardSoftwareInfos {

    private StandardSoftwareInfos() {
        throw new UnsupportedOperationException();
    }

    private static final SoftwareInfo SEE_BEE_VERSION = new SeeBeeSoftwareInfo();
    private static final SoftwareInfo JAVA_VERSION = new JavaSoftwareInfo();

    /**
     * Returns a version applicable for See Bee
     *
     * @return a version applicable for See Bee
     */
    public static SoftwareInfo seeBee() {
        return SEE_BEE_VERSION;
    }

    /**
     * Returns a version applicable for Java
     *
     * @return a version applicable for Java
     */
    public static SoftwareInfo java() {
        return JAVA_VERSION;
    }

}
