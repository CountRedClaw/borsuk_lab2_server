package sample.service;


import javafx.application.Application;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utilities {

    public static CollectionTaskList getUserCollection(String name) {
        CollectionTaskList collection = new CollectionTaskList();
        try {
            JAXBContext context = JAXBContext.newInstance(CollectionTaskList.class);
            File file = new File("src/main/resources/" + name + ".xml");
            Unmarshaller unmarshaller = context.createUnmarshaller();

            collection = (CollectionTaskList) unmarshaller.unmarshal(file);

        } catch (Exception e) {
            return null;
        }
        return collection;
    }

    public static void saveToXML(String name, CollectionTaskList collection) {
        try {
            JAXBContext context = JAXBContext.newInstance(CollectionTaskList.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            File file = new File("src/main/resources/" + name + ".xml");
            marshaller.marshal(collection, file);
        } catch (JAXBException exception) {
            Logger.getLogger(Application.class.getName()).log(Level.SEVERE, "marshallExample threw JAXBException", exception);
        }
    }
}
