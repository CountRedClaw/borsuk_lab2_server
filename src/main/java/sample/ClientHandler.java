package sample;

import sample.model.Task;
import sample.service.CollectionTaskList;
import sample.service.Utilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static sample.Server.connections;

public class ClientHandler extends Thread {
    private final Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private CollectionTaskList collection = new CollectionTaskList();
    private String name = "";

    public ClientHandler(Socket client) {
        this.client = client;

        try {
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            close();
        }
        //start();
    }

    public void sendData(List<Task> sentableCollection) {
        synchronized (connections) {
            try {
            Iterator<ClientHandler> iter = connections.iterator();
            while (iter.hasNext()) {
                ClientHandler temp = iter.next();
                if (temp.name.equals(name)) {
                    temp.out.writeObject(sentableCollection);
                    temp.out.flush();
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object input;
                try {
                    input = in.readObject();
                } catch (Exception e) {
                    continue;
                }

                System.out.println("hello0");
                HashMap<String, Task> request = (HashMap<String, Task>) input;

                if (request.containsKey("load")) {
                    name = ((Task) request.get("load")).getName();
                    System.out.println(name);
                    collection = Utilities.getUserCollection(name);

                    if (!(collection == null)) {
                        List<Task> sentableCollection = new ArrayList<>();
                        sentableCollection.addAll(collection.getTaskList());
                        System.out.println(sentableCollection);
                        sendData(sentableCollection);
                    }

                } else if (request.containsKey("save")) {
                    Utilities.saveToXML(name, collection);

                } else if (request.containsKey("add")) {
                    collection = Utilities.getUserCollection(name);
                    if (collection == null) {
                        collection = new CollectionTaskList();
                    }
                    collection.add(request.get("add"));
                    List<Task> sentableCollection = new ArrayList<>();
                    sentableCollection.addAll(collection.getTaskList());
                    Utilities.saveToXML(name, collection);
                    sendData(sentableCollection);

                } else if (request.containsKey("delete")) {
                    collection = Utilities.getUserCollection(name);
                    if (collection == null) {
                        collection = new CollectionTaskList();
                    }
                    collection.delete(request.get("delete").getId());
                    System.out.println(request.get("delete"));
                    Utilities.saveToXML(name, collection);
                    List<Task> sentableCollection = new ArrayList<>();
                    sentableCollection.addAll(collection.getTaskList());
                    System.out.println(sentableCollection);
                    sendData(sentableCollection);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            client.close();

            // Если больше не осталось соединений, закрываем всё, что есть и завершаем работу сервера
            connections.remove(this);
            if (connections.size() == 0) {
                Server.closeAll();
                System.exit(0);
            }
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }
}
