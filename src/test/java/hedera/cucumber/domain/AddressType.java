package hedera.cucumber.domain;

public enum AddressType {
    id("id"),
    alias("alias");

    private String text;

    AddressType(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public static AddressType fromString(String text) {
        for (AddressType b : AddressType.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
