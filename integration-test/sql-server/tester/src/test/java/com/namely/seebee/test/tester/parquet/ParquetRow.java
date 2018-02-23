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
package com.namely.seebee.test.tester.parquet;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public class ParquetRow {
    private final String[] names;
    private final String[] values;
    private final BigInteger[] integers;

    public ParquetRow(String[] names) {
        this.names = names;
        values = new String[names.length];
        integers = new BigInteger[names.length];
    }

    public String[] names() {
        return names;
    }

    public String[] values() {
        return values;
    }

    public void set(int idx, String value) {
        values[idx] = value;
    }

    public void set(int idx, BigInteger value) {
        integers[idx] = value;
    }

    public Map<String, String> getStringMap() {
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < names.length; i++) {
            result.put(names[i], values[i]);
        }
        return result;
    }

    public Map<String, BigInteger> getBigIntegerMap() {
        Map<String, BigInteger> result = new HashMap<>();
        for (int i = 0; i < names.length; i++) {
            BigInteger integer = integers[i];
            if (integer != null) {
                result.put(names[i], integer);
            }
        }
        return result;
    }

    public String toString() {
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            if (integers[i] != null) {
                parts.add(names[i] + " = " + integers[i]);
            } else {
                parts.add(names[i] + " = " + values[i]);
            }
        }
        return "Row{" + parts.stream().collect(joining(", ")) + "}";
    }
}
