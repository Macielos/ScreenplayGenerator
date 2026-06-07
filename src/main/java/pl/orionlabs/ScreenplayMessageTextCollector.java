package pl.orionlabs;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScreenplayMessageTextCollector {

    private static final String INPUT_DIR = "files\\collector\\input";
    private static final String OUTPUT_DIR = "files\\collector\\output";

    private static final Pattern SCREENPLAY_NAME_REGEX = Pattern.compile("^ScreenplayFactory:saveBuilder\\(\"(\\w+)\".*$");
    private static final Pattern SCREENPLAY_NAME2_REGEX = Pattern.compile("^ScreenplayFactory:saveBuilderForMessageChain\\(\"(\\w+)\".*$");
    private static final Pattern ITEM_NO_REGEX = Pattern.compile("^ {8}\\[(\\d+)]\\s*=\\s*\\{");
    private static final Pattern CHOICE_ITEM_NO_REGEX = Pattern.compile("^ {16}\\[(\\d+)]\\s*=\\s*\\{");
    private static final Pattern TEXT_FIELD_REGEX = Pattern.compile("^ {12}text\\s*=\\s*\"([^\"]*)\".*$");
    private static final Pattern CHOICE_TEXT_FIELD_REGEX = Pattern.compile("^ {20}text\\s*=\\s*\"([^\"]*)\".*$");

    private static final Pattern CHOICE_MESSAGE_KEY_PATTERN = Pattern.compile("^.+\\d{3}_\\d{3}$");

    public static void main(String[] args) throws IOException {
        var dirs = Files.list(Path.of(INPUT_DIR)).toList();
        var mapDirs = new HashMap<String, MapScreenplays>();
        for (Path dir : dirs) {
            if (dir.toFile().isFile()) {
                throw new IllegalArgumentException("Illegal file " + dir.toFile().getAbsolutePath());
            }
            mapDirs.put(dir.getFileName().toString(), processMapDirectory(dir));
        }

        for (Map.Entry<String, MapScreenplays> entry : mapDirs.entrySet()) {
            String name = entry.getKey();
            MapScreenplays mapScreenplays = entry.getValue();
            var path = OUTPUT_DIR + File.separator + name;
            new File(path).mkdirs();
            writeMessageFiles(path, mapScreenplays.messages);
            writeSoundFiles(path, name, mapScreenplays.messages);
            writeProcessedScreenplays(path, mapScreenplays.screenplays());
        }
    }

    private static void writeSoundFiles(String path, String name, Map<String, String> messages) throws IOException {
        var messageFilePath = path + File.separator + "ScreenplaySounds.lua";
        var lines = messages
                .entrySet()
                .stream()
                .filter(e -> !CHOICE_MESSAGE_KEY_PATTERN.matcher(e.getKey()).matches())
                .filter(e -> StringUtils.isNotEmpty(e.getValue()) && !"...".equals(e.getValue()))
                .map(Map.Entry::getKey)
                .map(key -> String.format("    %s = SoundUtils.createVoiceSound('Exodus\\\\Voices\\\\Missions\\\\%s\\\\%s'),", key, name, key))
                .collect(Collectors.toList());
        lines.add(0, "ScreenplaySounds = {");
        lines.add("}");
        Files.write(Path.of(messageFilePath), lines);
    }

    private static void writeMessageFiles(String path, Map<String, String> messages) throws IOException {
        var messageFilePath = path + File.separator + "ScreenplayMessages.lua";
        var lines = messages.entrySet().stream().map(entry -> String.format("    %s = \"%s\",", entry.getKey(), entry.getValue())).collect(Collectors.toList());
        lines.add(0, "ScreenplayMessages = {");
        lines.add("}");
        Files.write(Path.of(messageFilePath), lines);
    }

    private static void writeProcessedScreenplays(String path, List<Screenplay> screenplays) throws IOException {
        for (Screenplay processedScreenplay : screenplays) {
            var screenplayFilePath = path + File.separator + processedScreenplay.filename();
            Files.write(Path.of(screenplayFilePath), processedScreenplay.processedLines());
        }
    }

    record ScreenplayItem(int no, String text) {
    }

    record ScreenplayChoiceItem(int itemNo, int choiceNo, String text) {
    }

    record Screenplay(String name, String filename, List<String> processedLines, List<ScreenplayItem> items, List<ScreenplayChoiceItem> choiceItems) {
    }

    record MapScreenplays(Map<String, String> messages, List<Screenplay> screenplays) {
    }

    private static MapScreenplays processMapDirectory(Path mapDirectory) throws IOException {
        var screenplayFiles = Files.list(mapDirectory).sorted().toList();
        var messages = new LinkedHashMap<String, String>();
        var screenplays = new ArrayList<Screenplay>();
        for (Path screenplayFile : screenplayFiles) {
            if(screenplayFile.toFile().isDirectory()) {
                throw new IllegalArgumentException("Illegal directory " + screenplayFile.toFile().getAbsolutePath());
            }
            var lines = Files.readAllLines(screenplayFile);
            Optional<String> screenplayNameOptional = parseScreenplayName(lines.get(0));
            if(screenplayNameOptional.isEmpty()) {
                continue;
            }
            String screenplayName = screenplayNameOptional.get();
            int currentItemNumber = 0;
            int currentChoiceItemNumber = 0;
            var screenplayItems = new ArrayList<ScreenplayItem>();
            var screenplayChoiceItems = new ArrayList<ScreenplayChoiceItem>();
            var processedLines = new ArrayList<String>();
            for (String line : lines) {
                var itemNoMatcher = ITEM_NO_REGEX.matcher(line);
                if (itemNoMatcher.matches() && itemNoMatcher.groupCount() > 0) {
                    currentItemNumber = Integer.parseInt(itemNoMatcher.group(1));
                    processedLines.add(line);
                    continue;
                }
                var choiceItemNoMatcher = CHOICE_ITEM_NO_REGEX.matcher(line);
                if (choiceItemNoMatcher.matches() && choiceItemNoMatcher.groupCount() > 0) {
                    currentChoiceItemNumber = Integer.parseInt(choiceItemNoMatcher.group(1));
                    processedLines.add(line);
                    continue;
                }

                var textMatcher = TEXT_FIELD_REGEX.matcher(line);
                if (textMatcher.matches() && textMatcher.groupCount() > 0) {
                    if(currentItemNumber <= 0) {
                        throw new IllegalArgumentException("Illegal item number " + currentItemNumber + " currently processed line: " + line);
                    }
                    var messageText = textMatcher.group(1);
                    var messageKey = getMessageKey(screenplayName, currentItemNumber);
                    screenplayItems.add(new ScreenplayItem(currentItemNumber, messageText));
                    messages.put(messageKey, messageText);
                    processedLines.add(String.format("            text = ScreenplayMessages['%s'],", messageKey));
                    processedLines.add(String.format("            sound = ScreenplaySounds['%s'],", messageKey));
                    continue;
                }

                var choiceTextMatcher = CHOICE_TEXT_FIELD_REGEX.matcher(line);
                if (choiceTextMatcher.matches() && choiceTextMatcher.groupCount() > 0) {
                    if(currentItemNumber <= 0) {
                        throw new IllegalArgumentException("Illegal item number " + currentItemNumber + " currently processed line: " + line);
                    }
                    if(currentChoiceItemNumber <= 0) {
                        throw new IllegalArgumentException("Illegal choice item number " + currentChoiceItemNumber + " currently processed line: " + line);
                    }
                    var choiceMessageText = choiceTextMatcher.group(1);
                    var messageKey = getChoiceMessageKey(screenplayName, currentItemNumber, currentChoiceItemNumber);
                    screenplayChoiceItems.add(new ScreenplayChoiceItem(currentItemNumber, currentChoiceItemNumber, choiceMessageText));
                    messages.put(messageKey, choiceMessageText);
                    processedLines.add(String.format("                    text = ScreenplayMessages['%s'],", messageKey));
                    continue;
                }

                processedLines.add(line);
            }
            if(screenplayItems.isEmpty()) {
                throw new IllegalArgumentException("No screenplay items found for screenplay " + screenplayName);
            }
            screenplays.add(new Screenplay(screenplayName, screenplayFile.toFile().getName(), processedLines, screenplayItems, screenplayChoiceItems));
        }
        return new MapScreenplays(messages, screenplays);
    }

    private static String getMessageKey(String screenplayName, int currentItemNumber) {
        return String.format("%s%03d", screenplayName, currentItemNumber);
    }

    private static String getChoiceMessageKey(String screenplayName, int currentItemNumber, int currentChoiceItemNumber) {
        return String.format("%s%03d_%03d", screenplayName, currentItemNumber, currentChoiceItemNumber);
    }

    private static Optional<String> parseScreenplayName(String firstLine) {
        return Stream.of(SCREENPLAY_NAME_REGEX, SCREENPLAY_NAME2_REGEX).map(
                        pattern -> {
                            var matcher = pattern.matcher(firstLine);
                            if (matcher.matches() && matcher.groupCount() > 0) {
                                return matcher.group(1);
                            }
                            return null;
                        }
                )
                .filter(Objects::nonNull)
                .findFirst();
    }
}