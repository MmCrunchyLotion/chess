import chess.*;
import server.Server;
//import service.ChessService;

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
//            DataAccess dataAccess = new MemoryDataAccess();
//            if (args.length >= 2 && args[1].equals("sql")) {
//                dataAccess = new MySqlDataAccess();
//            }
//
//            var service = new ChessService(DataAccess);
//            var server = new Server(service).run(port);
//            System.out.printf("Server started on port %d", port);
//            return;
//        } catch (Throwable ex) {
//            System.out.printf("Unable to start server: %s%n", ex.getMessage());
//        }
//        System.out.println("♕ 240 Chess Server");
//    }
//}