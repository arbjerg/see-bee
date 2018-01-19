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
package com.namely.seebee.application.internal.util;

import com.namely.seebee.configuration.Configuration;
import com.namely.seebee.configuration.standard.StandardConfigurations;
import com.namely.seebee.repository.Repository;
import com.namely.seebee.repository.standard.StandardRepositories;
import com.namely.seebee.softwareinfo.SoftwareInfo;

import com.namely.seebee.softwareinfo.standard.StandardSoftwareInfos;

/**
 *
 * @author Per Minborg
 */
public  final class RepositoryUtil {
    
    private RepositoryUtil() { throw new UnsupportedOperationException(); }
    
    public static Repository.Builder standardRepositoryBuilder() {
        return StandardRepositories.builder()
            .provide(Configuration.class).getting(StandardConfigurations::create)
            .provide(SoftwareInfo.class).getting(StandardSoftwareInfos::seeBee)
            .provide(SoftwareInfo.class).getting(StandardSoftwareInfos::java);
    }
    
}
