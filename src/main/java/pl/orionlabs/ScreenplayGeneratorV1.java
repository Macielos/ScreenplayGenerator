package pl.orionlabs;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ScreenplayGeneratorV1 {

    private static final String DIRECTORY = "files\\generator";
    private static final String INPUT = DIRECTORY + "\\Input.txt";
    private static final String OUTPUT_DIRECTORY = DIRECTORY + "\\output";

    private static final String TEMPLATE = """
            ScreenplayFactory:saveBuilderForMessageChain("TODO", function()
            [ACTORS]
            return {
            [CONTENT]
             }
            end)
            """;

    private static final String THREE_DOTS = "…";
    private static final int INITIAL_INDEX = 1;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    private static final String CHOICES_PREFIX = "[choices]";

    public static void main(String[] args) throws IOException {
        System.out.println("Starting...");
        List<Message> messages = Files.readAllLines(Paths.get(INPUT))
                .stream()
                .filter(StringUtils::isNotBlank)
                .filter(s -> s.contains(":"))
                .map(ScreenplayGeneratorV1::processLine)
                .toList();
        Set<String> actors = messages.stream()
                .map(Message::actor)
                .collect(Collectors.toSet());

        String screenplayActorsDeclaration = actors
                .stream()
                .map(ScreenplayGeneratorV1::createActorsDeclaration)
                .collect(Collectors.joining());
        String screenplayMessageList = IntStream.range(0, messages.size())
                .mapToObj(index -> createMessage(index + INITIAL_INDEX, messages.get(index)))
                .collect(Collectors.joining());

        String screenplay = TEMPLATE.replace("[ACTORS]", screenplayActorsDeclaration)
                .replace("[CONTENT]", screenplayMessageList);
        String outputFilename = OUTPUT_DIRECTORY + "\\screenplay_" + DATE_FORMATTER.format(LocalDateTime.now().withSecond(0).withNano(0)) + ".lua";
        Files.writeString(Paths.get(outputFilename), screenplay);
    }

    private static String createMessage(int msgNo, Message message) {
        return message.choices() == null
                ? createMessageWithoutChoices(msgNo, message)
                : createMessageWithChoices(msgNo, message);
    }

    private static String createMessageWithoutChoices(int msgNo, Message message) {
        return String.format("""
                [%d] = {
                    actor = actor%s,
                    text = "%s",
                },
                    """, msgNo, capitalize(message.actor()), message.message());
    }

    private static String createMessageWithChoices(int msgNo, Message message) {
        return String.format("""
                [%d] = {
                    actor = actor%s,
                    choices = {
                        %s
                    },
                },
                    """, msgNo, capitalize(message.actor()), createChoices(message.choices()));
    }

    private static String createChoices(List<String> choices) {
        return IntStream.range(0, choices.size())
                .mapToObj(i -> {
                    String choice = choices.get(i);
                    int index = i + 1;
                    return String.format("""
                            [%d] = {
                                text = "%s",
                                onChoice = function()
                                    ScreenplaySystem:currentItem().choices[%d].visible = false
                                end,
                                onChoiceGoTo = TODO
                            },
                            """,
                    index, choice, index);
                }).collect(Collectors.joining("\n"));
    }

    private static String createActorsDeclaration(String s) {
        return String.format("    actor%s = ScreenplayFactory.createActor(udg_%s)\n", capitalize(s), s);
    }

    private static Message processLine(String s) {
        var parts = s.split(":", 2);
        var actor = parts[0].trim().toLowerCase().split(" ")[0];
        var message = parts[1].replace(THREE_DOTS, "...").trim();
        var isChoiceMessage = message.toLowerCase().startsWith(CHOICES_PREFIX);
        if(!isChoiceMessage) {
            return new Message(actor, message);
        }
        var choices = Arrays.stream(StringUtils.removeStart(message, CHOICES_PREFIX).split("\\|"))
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .toList();
        return new Message(actor, null, choices);
    }

    private static String capitalize(String s) {
        if (s.isBlank()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}