package nl.schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
    
    
public class RecursiveTaskScheduling extends RecursiveTask<Long> {

	private static final long serialVersionUID = 1L;
	private final long workLoad;
	
    public RecursiveTaskScheduling(long workLoad) {
        this.workLoad = workLoad;
    }

    protected Long compute() {

        //if work is above threshold, break tasks up into smaller tasks
        if(this.workLoad > 16) {
            System.out.println("Splitting workLoad : " + this.workLoad);

            List<RecursiveTaskScheduling> subtasks = createSubtasks();
            subtasks.stream().forEach(subtask->subtask.fork());

            long result = 0;
            for(RecursiveTaskScheduling subtask : subtasks) {
                result += subtask.join();
            }
            return result;

        } else {
            System.out.println("Doing workLoad myself: " + this.workLoad);
            return workLoad * 3;
        }
    }

    private List<RecursiveTaskScheduling> createSubtasks() {
        List<RecursiveTaskScheduling> subtasks =
        new ArrayList<RecursiveTaskScheduling>();

        RecursiveTaskScheduling subtask1 = new RecursiveTaskScheduling(this.workLoad / 2);
        RecursiveTaskScheduling subtask2 = new RecursiveTaskScheduling(this.workLoad / 2);

        subtasks.add(subtask1);
        subtasks.add(subtask2);

        return subtasks;
    }
    public static final void main(String[] args)
    {
    	System.out.println( new RecursiveTaskScheduling(128).compute());
    }
}