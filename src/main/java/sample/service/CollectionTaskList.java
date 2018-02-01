package sample.service;

import sample.model.Task;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "tasks")
public class CollectionTaskList implements Serializable {

    @XmlElement(name = "task")
    public List<Task> taskList = new ArrayList<>();

    public void addList(List<Task> list) {
        for (Task task : list) {
            taskList.add(task);
        }
    }

    public void add(Task task) {
        taskList.add(task);
    }

    public void delete(int id) {
        for (int i = 0; i < taskList.size(); i++) {
            if (taskList.get(i).getId() == id) {
                taskList.remove(i);
            }
        }
    }

    public List<Task> getTaskList() {
        return taskList;
    }
}
