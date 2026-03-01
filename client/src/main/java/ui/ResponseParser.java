package ui;

public class ResponseParser {

    private final String[] tokens;

    public ResponseParser(String input) {
        String[] parts = input.trim().split("\\s+");
        if (parts.length >= 5) {
            this.tokens = null;
        } else {
            this.tokens = parts;
        }
    }

    public String[] getTokens() {
        return tokens;
    }
}
