package com.namely.seebee.application.support.config;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ConfigUtil {
    private static final Pattern PARAM_PATTERN = Pattern.compile("--([^.]+\\.[^=]+)=(.*)");

    /**
     * Parses parameters of the form --[config]=[value] where [config] is of the form [main].[sub]
     *
     * @throws IllegalArgumentException if any parameter is not of the correct form
     * @param args an array of parameters
     * @return a map from [config] to [value]
     */
    public static Map<String, String> getSettingsFromArgs(String[] args) {
        Optional<String> firstMalformedParameter = Arrays.stream(args)
                .filter(p -> !PARAM_PATTERN.matcher(p).matches())
                .findFirst();

        if (firstMalformedParameter.isPresent()) {
            throw new IllegalArgumentException("Malformed parameter: " + firstMalformedParameter.get());
        }

        return Arrays.stream(args)
                .map(p -> {
                    Matcher matcher = PARAM_PATTERN.matcher(p);
                    if (!matcher.matches()) {
                        throw new RuntimeException("Param magically no longer matches: " + p);
                    }
                    return matcher;
                })
                .collect(Collectors.toMap(m -> m.group(1), m -> m.group(2)));
    }
}
