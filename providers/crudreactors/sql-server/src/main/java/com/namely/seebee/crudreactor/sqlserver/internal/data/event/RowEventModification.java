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
package com.namely.seebee.crudreactor.sqlserver.internal.data.event;

import com.namely.seebee.crudreactor.CrudEventType;
import com.namely.seebee.crudreactor.RowData;
import com.namely.seebee.crudreactor.RowEvent;

public final class RowEventModification implements RowEvent {
    private final RowData data;

    public RowEventModification(RowData data) {
        this.data = data;
    }

    @Override
    public final CrudEventType type() {
        return CrudEventType.MODIFY;
    }

    @Override
    public RowData data() {
        return data;
    }

    @Override
    public String toString() {
        return "Change " + data.toString();
    }
}
