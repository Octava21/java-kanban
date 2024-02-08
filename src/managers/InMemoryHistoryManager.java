package managers;

import model.Task;
import model.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


public class InMemoryHistoryManager implements HistoryManager {
   private static final int LIMIT_HISTORY_TASK = 10;
   private CustomLinkedList historyTasks = new CustomLinkedList();


   @Override
   public void add(Task task) {
      if (historyTasks.size() >= LIMIT_HISTORY_TASK) {
         // Если достигнут лимит, удаляем самую старую задачу
         historyTasks.remove(0);
      }
      historyTasks.linkLast(task);
   }

   @Override
   public List<Task> getHistory() {
      return new LinkedList<>(historyTasks.getTasks());
   }

   @Override
   public void remove(int id) {
      historyTasks.remove(id);
   }


   private class CustomLinkedList {
      private Map<Integer, Node> taskPositions = new HashMap<>();
      private Node head;
      private Node tail;
      private int size;

      void linkLast(Task task) {
         Node element = new Node(task);

         // Если задача уже существует в списке, удаляем её
         if (taskPositions.containsKey(task.getId())) {
            remove(task.getId());
         }

         if (head == null) {
            tail = element;
            head = element;
            element.setNext(null);
            element.setPrev(null);
         } else {
            element.setPrev(tail);
            element.setNext(null);
            tail.setNext(element);
            tail = element;
         }

         taskPositions.put(task.getId(), element);
         size++;
      }


      void remove(int taskId) {
         Node removedNode = taskPositions.remove(taskId);
         if (removedNode != null) {
            if (removedNode == head) {
               head = removedNode.getNext();
            }
            if (removedNode == tail) {
               tail = removedNode.getPrev();
            }
            if (removedNode.getPrev() != null) {
               removedNode.getPrev().setNext(removedNode.getNext());
            }
            if (removedNode.getNext() != null) {
               removedNode.getNext().setPrev(removedNode.getPrev());
            }
            size--;
         }
      }

      List<Task> getTasks() {
         List<Task> result = new LinkedList<>();
         Node current = head;
         while (current != null) {
            result.add(current.getTask());
            current = current.getNext();
         }
         return result;
      }

      int size() {
         return size;
      }
   }
}