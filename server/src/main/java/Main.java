import chess.*;
import server.Server;
import services.*;
import dataaccess.*;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }

}