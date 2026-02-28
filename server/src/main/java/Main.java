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

//public class Main {
//    public static void main(String[] args) {
//        try {
//            var port = 8080;
//            if (args.length >= 1) {
//                port = Integer.parseInt(args[0]);
//            }
//
//
//            AuthDAO authDAO = new AuthDAO();
//            UserDAO userDAO = new UserDAO();
//            GameDAO gameDAO = new GameDAO();
//
////            if (args.length >= 2 && args[1].equals("sql")) {
////                dataAccess = new MySqlDataAccess();
////            }
//
//            var service = new ChessService(DataAccess);
//            Server server = new Server();
//            server.run(8080);
//            System.out.printf("Server started on port %d", port);
//            return;
//        } catch (Throwable ex) {
//            System.out.printf("Unable to start server: %s%n", ex.getMessage());
//        }
//        System.out.println("♕ 240 Chess Server");
//    }
//}