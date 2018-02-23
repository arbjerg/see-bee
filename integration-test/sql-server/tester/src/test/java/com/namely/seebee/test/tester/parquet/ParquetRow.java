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
