package Managers;
import model.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
   private static final int LIMIT_HISTORY_TASK = 10;
   private final LinkedList<Task> historyTasks = new LinkedList<>();

   @Override
   public void add(Task task) {
      if (historyTasks.size() >= LIMIT_HISTORY_TASK) {
         // Если достигнут лимит, удаляем самую старую задачу
         historyTasks.removeFirst();
      }
      historyTasks.add(task);
   }

   @Override
   public List<Task> getHistory() {
      return new LinkedList<>(historyTasks);
   }

}
