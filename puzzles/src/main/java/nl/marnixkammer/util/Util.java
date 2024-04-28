package nl.marnixkammer.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.function.Predicate.not;

@UtilityClass
public class Util {

    public static List<String> filterOutRegex(final List<String> input, final String regexToFilterOut) {
        return input.stream().map(String::trim)
                .map(line -> line.replaceAll(regexToFilterOut, ""))
                .filter(not(String::isBlank))
                .toList();
    }

    @SneakyThrows
    public static List<String> readResourceLines(final String resourceName) {
        final Path resourcePath = Path.of("puzzles/src/main/resources", resourceName);
        return Files.readAllLines(resourcePath);
    }
}
