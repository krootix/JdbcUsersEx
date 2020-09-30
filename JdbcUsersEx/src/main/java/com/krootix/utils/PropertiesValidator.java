package com.krootix.utils;

import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class PropertiesValidator {
    private static final String HOST = "db.host";
    private static final String LOGIN = "db.login";
    private static final String PASSWORD = "db.password";

    private final Set<String> required = Set.of(HOST, LOGIN, PASSWORD);

    public boolean validate(Properties properties) {
        Set<Object> validated = properties
                .entrySet()
                .stream()
                .filter(e -> required.contains(e.getKey()) && !e.getValue().equals(""))
                .map(e -> e.getKey())
                .collect(Collectors.toSet());
        return required.size() == validated.size();
    }
}