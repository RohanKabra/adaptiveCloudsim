package adaptive.cloudsim.workflow.scheduler;

import java.util.Collection;

import org.cloudbus.cloudsim.Vm;

import adaptive.cloudsim.workflow.Task;


public interface WorkflowScheduler {
	
	public void reschedule(Collection<Task> tasks, Collection<Vm> vms);

	public void taskReady(Task task);

	public Task getNextTask(Vm vm);

	public void taskSucceeded(Task task, Vm vm);

	public void taskFailed(Task task, Vm vm);

	public boolean tasksRemaining();

	public void terminate();
}
