package story;

public interface Describable {

    default String onDestructionDescribe() {
        return String.format("%s is destroyed!\n", this.toString());
    }

    default String onDamageDescribe(double dmg) {
        return String.format("%s receives %.2f damage!\n", this.toString(), dmg);
    }

    default String stateDescribe() {
        return String.format("%s is fine!\n", this.toString());
    }

}
