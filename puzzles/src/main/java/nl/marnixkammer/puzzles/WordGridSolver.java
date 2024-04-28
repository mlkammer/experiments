package nl.marnixkammer.puzzles;

import java.util.ArrayList;
import java.util.List;

import static nl.marnixkammer.util.Util.filterOutRegex;
import static nl.marnixkammer.util.Util.readResourceLines;

/**
 * @author Marnix Kammer
 */
public class WordGridSolver {

    private static final String REGEX_NON_LETTERS = "[^A-Za-z]";
    private static final List<String> WORDS_TO_FIND = filterOutRegex(readResourceLines(
            "wordgrid4_words.txt"), REGEX_NON_LETTERS);
    private static final List<String> PUZZLE_LINES = filterOutRegex(readResourceLines(
            "wordgrid4_puzzle.txt"), REGEX_NON_LETTERS);
    private static final int SOLUTION_WORD_LENGTH = 15;

    private static final int LINE_LENGTH = PUZZLE_LINES.getFirst().length();
    private static final List<List<Boolean>> LETTER_USAGE_STATUS = new ArrayList<>();

    static {
        for (String line : PUZZLE_LINES) {
            LETTER_USAGE_STATUS.add(repeatValue(false, line.length()));
        }
    }

    public static void main(final String[] args) {
        for (String word : WORDS_TO_FIND) {
            if (!markWord(word)) {
                throw new RuntimeException("Word not found: " + word);
            }
        }
        System.out.println("Found words: " + WORDS_TO_FIND);

        final List<String> remainingLetters = getRemainingLetters();
        System.out.println("Remaining letters for each puzzle line: " + remainingLetters);

        final List<List<String>> candidateSolutionWords = getCandidateSolutionWords();
        System.out.println("Candidate solution words of length >= " + SOLUTION_WORD_LENGTH + ": " + candidateSolutionWords);
    }

    private static List<String> getRemainingLetters() {
        final List<String> lines = new ArrayList<>();
        for (int row = 0; row < PUZZLE_LINES.size(); row++) {
            final StringBuilder line = new StringBuilder();
            for (int col = 0; col < LINE_LENGTH; col++) {
                if (!LETTER_USAGE_STATUS.get(row).get(col)) {
                    line.append(PUZZLE_LINES.get(row).charAt(col));
                }
            }
            lines.add(line.toString());
        }
        return lines;
    }

    private static List<List<String>> getCandidateSolutionWords() {
        return List.of(
                        findAllWordsHorizontal(),
                        findAllWordsVertical(),
                        findAllWordsDiagonalUpToDown(),
                        findAllWordsDiagonalDownToUp()
                );
    }

    private static List<String> findAllWordsHorizontal() {
        final List<String> lines = new ArrayList<>();
        for (int row = 0; row < PUZZLE_LINES.size(); row++) {
            final StringBuilder line = new StringBuilder();
            for (int col = 0; col < LINE_LENGTH; col++) {
                if (!LETTER_USAGE_STATUS.get(row).get(col)) {
                    line.append(PUZZLE_LINES.get(row).charAt(col));
                } else {
                    addCandidateSolution(line, lines);
                }
            }
            addCandidateSolution(line, lines);
        }
        return lines;
    }

    private static List<String> findAllWordsVertical() {
        final List<String> columns = new ArrayList<>();
        for (int col = 0; col < LINE_LENGTH; col++) {
            final StringBuilder column = new StringBuilder();
            for (int row = 0; row < PUZZLE_LINES.size(); row++) {
                if (!LETTER_USAGE_STATUS.get(row).get(col)) {
                    column.append(PUZZLE_LINES.get(row).charAt(col));
                } else {
                    addCandidateSolution(column, columns);
                }
            }
            addCandidateSolution(column, columns);
        }
        return columns;
    }

    private static List<String> findAllWordsDiagonalUpToDown() {
        // TODO: Implement if really needed
        return List.of();
    }

    private static List<String> findAllWordsDiagonalDownToUp() {
        // TODO: Implement if really needed
        return List.of();
    }

    private static void addCandidateSolution(final StringBuilder line, final List<String> lines) {
        if (line.length() >= SOLUTION_WORD_LENGTH) {
            lines.add(line.toString());
            lines.add(line.reverse().toString());
        }
        line.setLength(0);
    }

    private static boolean markWord(final String wordArg) {
        final String word = wordArg.toUpperCase();
        final String reversed = new StringBuilder(word).reverse().toString();

        if (markWordHorizontal(word, reversed))
            return true;
        if (markWordVertical(word, reversed))
            return true;
        if (markWordDiagonalUpToDown(word, reversed))
            return true;
        if (markWordDiagonalDownToUp(word, reversed))
            return true;

        return false;
    }

    private static boolean markWordHorizontal(final String word, final String reversed) {
        for (int row = 0; row < PUZZLE_LINES.size(); row++) {
            final String line = PUZZLE_LINES.get(row);
            final int index = line.contains(word) ? line.indexOf(word) : line.indexOf(reversed);
            if (index != -1) {
                for (int col = index; col - index < word.length(); col++) {
                    LETTER_USAGE_STATUS.get(row).set(col, true);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean markWordVertical(final String word, final String reversed) {
        for (int col = 0; col < LINE_LENGTH; col++) {
            final String column = getColumn(col);
            final int index = column.contains(word) ? column.indexOf(word) : column.indexOf(reversed);
            if (index != -1) {
                for (int row = index; row - index < word.length(); row++) {
                    LETTER_USAGE_STATUS.get(row).set(col, true);
                }
                return true;
            }
        }
        return false;
    }

    private static boolean markWordDiagonalUpToDown(final String word, final String reversed) {
        int startCol = 0;
        int startRow = PUZZLE_LINES.size() - 1;
        while (startRow > 0) {
            if (markWordDiagonalUpToDown(word, reversed, startRow, startCol)) return true;
            startRow--;
        }
        while (startCol < LINE_LENGTH) {
            if (markWordDiagonalUpToDown(word, reversed, startRow, startCol)) return true;
            startCol++;
        }
        return false;
    }

    private static boolean markWordDiagonalDownToUp(final String word, final String reversed) {
        int startCol = 0;
        int startRow = 0;
        while (startRow < PUZZLE_LINES.size()) {
            if (markWordDiagonalDownToUp(word, reversed, startRow, startCol)) return true;
            startRow++;
        }
        startRow = PUZZLE_LINES.size() - 1;
        while (startCol < LINE_LENGTH) {
            if (markWordDiagonalDownToUp(word, reversed, startRow, startCol)) return true;
            startCol++;
        }
        return false;
    }

    private static boolean markWordDiagonalUpToDown(final String word, final String reversed, final int startRow, final int startCol) {
        final StringBuilder lineBuilder = new StringBuilder();
        for (int row = startRow; row < PUZZLE_LINES.size(); row++) {
            final int col = startCol + (row - startRow);
            if (col >= LINE_LENGTH) break;
            lineBuilder.append(PUZZLE_LINES.get(row).charAt(col));
        }
        return markDiagonalWord(word, reversed, startRow, startCol, lineBuilder.toString());
    }

    private static boolean markWordDiagonalDownToUp(final String word, final String reversed, final int startRow, final int startCol) {
        final StringBuilder lineBuilder = new StringBuilder();
        for (int row = startRow; row >= 0; row--) {
            final int col = startCol + (startRow - row);
            if (col >= LINE_LENGTH) break;
            lineBuilder.append(PUZZLE_LINES.get(row).charAt(col));
        }
        return markDiagonalWord(word, reversed, startRow, startCol, lineBuilder.toString());
    }

    private static boolean markDiagonalWord(final String word, final String reversed, final int startRow, final int startCol,
                                            final String diagonalPuzzleLine) {
        final int index = diagonalPuzzleLine.contains(word)
                ? diagonalPuzzleLine.indexOf(word)
                : diagonalPuzzleLine.indexOf(reversed);
        if (index == -1) {
            return false;
        }
        for (int row = startRow + index; row - index < word.length(); row++) {
            final int col = startCol + index;
            LETTER_USAGE_STATUS.get(row).set(col, true);
        }
        return true;
    }

    private static String getColumn(final int col) {
        final StringBuilder column = new StringBuilder();
        for (final String puzzleLine : PUZZLE_LINES) {
            column.append(puzzleLine.charAt(col));
        }
        return column.toString();
    }

    private static <T> List<T> repeatValue(final T value, final int length) {
        final List<T> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(value);
        }
        return result;
    }
}
