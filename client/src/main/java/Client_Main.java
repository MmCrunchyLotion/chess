import ui.UILoop;
import exception.ResponseException;

public class Client_Main {

    public static void main(String[] args) throws ResponseException {
        System.out.println("♕ Welcome to 240 chess. Type 'Help' to get started. ♕\n");
        new UILoop().startUILoop();
    }

}
