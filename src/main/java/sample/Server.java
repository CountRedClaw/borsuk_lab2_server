package sample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    public static List<ClientHandler> connections = Collections.synchronizedList(new ArrayList<ClientHandler>());
    private static ServerSocket server;

    public static void main(String[] args) {
        new Server();
    }

    public Server() {
        try {
            server = new ServerSocket(19000);

            /*Task tas = new Task("f","23:50");
            System.out.println(tas.getName() + tas.getTime());*/

            while (true) {
                Socket socket = server.accept();

                // Создаём объект Connection и добавляем его в список
                ClientHandler con = new ClientHandler(socket);
                connections.add(con);

                // Инициализирует нить и запускает метод run(),
                // которая выполняется одновременно с остальной программой
                con.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeAll();
        }
    }

    public static void closeAll() {
        try {
            server.close();

            // Перебор всех Connection и вызов метода close() для каждого. Блок
            // synchronized {} необходим для правильного доступа к одним данным
            // их разных нитей
            synchronized(connections) {
                Iterator<ClientHandler> iter = connections.iterator();
                while(iter.hasNext()) {
                    ((ClientHandler) iter.next()).close();
                }
            }
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }
}
