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
package com.namely.seebee.softwareinfo;

/**
 * Version component that is used to hold the version of some software.
 * <p>
 *
 * @author Per Minborg
 */
public interface SoftwareInfo {

    /**
     * Returns the name of the software component.
     * 
     * @return the name of the software component
     */
    String  name();

    /**
     * Returns the version of the software component.
     * 
     * @return the version of the software component
     */
    String version();
    
     /**
     * Returns the name of the vendor of the software component.
     * 
     * @return the name of the vendor of the software component
     */
    String vendor();
    
    /**
     * Returns if this software component can be used in production systems.
     * 
     * @return if this software component can be used in production systems
     */
    boolean isProductionMode();

}
