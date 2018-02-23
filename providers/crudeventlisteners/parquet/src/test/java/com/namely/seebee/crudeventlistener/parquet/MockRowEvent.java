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
package com.namely.seebee.crudeventlistener.parquet;

import com.namely.seebee.crudreactor.CrudEventType;
import com.namely.seebee.crudreactor.RowData;
import com.namely.seebee.crudreactor.RowEvent;
import com.namely.seebee.typemapper.ColumnValue;

import java.util.ArrayList;
import java.util.List;

public class MockRowEvent implements RowEvent {
    private List<ColumnValue<?>> values;

    public MockRowEvent(int age) {
        values = new ArrayList<>();
        values.add(new IntegerColumnValue("id", 2));
        values.add(new IntegerColumnValue("age", age));
    }

    @Override
    public CrudEventType type() {
        return CrudEventType.ADD;
    }

    @Override
    public RowData data() {
        return () -> values;
    }
}
