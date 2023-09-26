package Server;

public enum TextColor {
    RESET("\u001B[0m"),
    PURPLE("\u001B[35m"),
    BLUE("\u001B[34m");
    private String ansiCode;

    private TextColor(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    @Override
    public String toString() {
        return this.ansiCode;
    }
}