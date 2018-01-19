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
package com.namely.seebee.repository.standard;

import com.namely.seebee.repositoryclient.IntParameter;
import com.namely.seebee.repositoryclient.StringParameter;
import com.namely.seebee.repository.standard.internal.parameter.StandardIntParameter;
import com.namely.seebee.repository.standard.internal.parameter.StandardStringParameter;

/**
 *
 * @author Per Minborg
 */
public final class Parameters {

    private Parameters() {
        throw new UnsupportedOperationException();
    }

    public static IntParameter of(String name, int value) {
        return new StandardIntParameter(name, value);
    }

    public static StringParameter of(String name, String value) {
        return new StandardStringParameter(name, value);
    }

}
