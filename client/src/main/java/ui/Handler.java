package ui;

public class Handler {

    protected String arg0;
    protected String arg1;
    protected String arg2;
    protected String arg3;

    public Handler() {}

    protected void setArgs(String[] args) {
        clearArgs();
        for (int i = 0; i < args.length; i++) {
            switch (i) {
                case 0 -> this.arg0 = args[0];
                case 1 -> this.arg1 = args[1];
                case 2 -> this.arg2 = args[2];
                case 3 -> this.arg3 = args[3];
            }
        }
    }

    protected void clearArgs() {
        this.arg0 = null;
        this.arg1 = null;
        this.arg2 = null;
        this.arg3 = null;
    }
}