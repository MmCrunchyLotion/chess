package ui;

public class ResponseParser {

    private final String[] tokens;

    public ResponseParser(String input) {
        this.tokens = input.trim().split("\\s+");
    }

    public String[] getTokens() {
        return tokens;
    }
}