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

import com.namely.seebee.condiguration.Configuration;
import com.namely.seebee.repository.Repository;
import com.namely.seebee.version.Version;

/**
 *
 * @author Per Minborg
 */
public  final class RepositoryUtil {
    
    private RepositoryUtil() { throw new UnsupportedOperationException(); }
    
    public static Repository.Builder standardRepositoryBuilder() {
        return Repository.builder()
            .provide(Configuration.class).getting(Configuration::defaultConfiguration)
            .provide(Version.class).getting(Version::defaultVersion);
    }
    
}
