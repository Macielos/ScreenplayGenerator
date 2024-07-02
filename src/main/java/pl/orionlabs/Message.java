package pl.orionlabs;

import java.util.List;

public record Message(String actor, String message, List<String> choices) {

    public Message(String actor, String message) {
        this(actor, message, null);
    }
}
