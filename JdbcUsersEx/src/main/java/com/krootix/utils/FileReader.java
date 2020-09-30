package com.krootix.utils;

import com.krootix.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

public class FileReader {

    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);

    private static final String PATH = "db.properties";

    private final PropertiesValidator propertiesValidator;

    public FileReader(Supplier<PropertiesValidator> propertiesValidator) {
        this.propertiesValidator = propertiesValidator.get();
    }

    public Properties readProperties() {
        String rootPath = requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();

        String defaultConfigPath = rootPath + PATH;
        Properties dbProperties = new Properties();
        try {
            dbProperties.load(new FileInputStream(defaultConfigPath));
            boolean isValidated = propertiesValidator.validate(dbProperties);

            logger.error("validating properties. correct: {}", isValidated);

            if (!isValidated) throw new IllegalArgumentException("Check the properties file");
        } catch (IOException e) {
            logger.error("IOException: {}", e.getMessage());

        }
        return dbProperties;
    }

    public String readSQLFile(String fileName) throws URISyntaxException, IOException {
        Class clazz = Main.class;
        Path path = Paths.get(Objects.requireNonNull(clazz.getClassLoader()
                .getResource(fileName)).toURI());

        String data;
        try (Stream<String> lines = Files.lines(path)) {
            data = lines.collect(Collectors.joining("\n"));
        }
        return data;
    }
}