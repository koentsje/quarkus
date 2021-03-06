package io.quarkus.it.kubernetes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class TestUtil {

    private TestUtil() {
    }

    /**
     * Gets the project version from a version.txt (that has been properly setup in maven to contain the version)
     * This is needed in order to avoid hard-coding Quarkus dependency versions that could break the release process
     */
    public static String getProjectVersion() {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(TestUtil.class.getResourceAsStream("/version.txt"), StandardCharsets.UTF_8))) {
            return bufferedReader.readLine();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void assertLogFileContents(Path logfile, String... expectedOutput) {
        // the logs might not be flushed to disk immediately, so wait a few seconds before giving up completely
        await().atMost(3, TimeUnit.SECONDS).untilAsserted(() -> {
            List<String> elements = Collections.emptyList();
            try {
                elements = Files.readAllLines(logfile);
            } catch (IOException ignored) {

            }
            final String entireLogContent = String.join("\n", elements);
            assertThat(entireLogContent).contains(expectedOutput);
        });
    }
}
