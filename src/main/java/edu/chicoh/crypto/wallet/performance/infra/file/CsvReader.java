package edu.chicoh.crypto.wallet.performance.infra.file;

import edu.chicoh.crypto.wallet.performance.infra.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class CsvReader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Stream<String[]> read(String fileName, boolean withHeader) {
        logger.info("Reading CSV file {} from resource.", fileName);
        final URI file = getUri(fileName);

        final Path pathToFile = Paths.get(file);

        Stream<String> lines = streamFileLines(fileName, pathToFile);

        if (withHeader) {
            lines = lines.skip(1);
        }

        return lines.map(line -> line.split(","));
    }

    private URI getUri(String fileName) {
        try {
            return Objects.requireNonNull(
                    this.getClass().getClassLoader().getResource(fileName)
            ).toURI();
        } catch (URISyntaxException e) {
            logger.error("Error getting resource file " + fileName, e);
            throw new SystemException(e);
        }
    }

    private Stream<String> streamFileLines(String fileName, Path pathToFile) {
        try {
            return Files.lines(pathToFile, StandardCharsets.US_ASCII);
        } catch (IOException e) {
            logger.error("Error reading resource file " + fileName, e);
            throw new SystemException(e);
        }
    }
}
