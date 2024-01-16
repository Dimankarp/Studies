package marine.net;

import marine.structure.SpaceMarine;

import java.io.Serializable;

public record ResponseContainer(String message, SpaceMarine[] mentionedMarines, User[] mentionedUsers) implements Serializable {
}
