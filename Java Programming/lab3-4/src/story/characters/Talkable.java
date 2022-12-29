package story.characters;

public interface Talkable {

    default String getSaying(String phrase) {
        return String.format("%s says: \"%s\"\n", this.toString(), phrase);
    }

    default String getMentioning(Object o, String phrase) {
        return String.format(phrase, this.toString(), o.toString());
    }

    String getRandomSaying();


}
