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
package com.namely.seebee.repository.standard.internal.parameter;

import com.namely.seebee.repositoryclient.StringParameter;
import static java.util.Objects.requireNonNull;

/**
 *
 * @author Per Minborg
 */
public final class StandardStringParameter extends AbstractParameter<String> implements StringParameter {

    private final String value;

    public StandardStringParameter(String name, String value) {
        super(name);
        this.value = requireNonNull(value);
    }

    @Override
    public String get() {
        return value;
    }

}
