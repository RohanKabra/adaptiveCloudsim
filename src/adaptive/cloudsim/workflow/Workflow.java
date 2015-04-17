package adaptive.cloudsim.workflow;

import java.util.*;

import org.cloudbus.cloudsim.File;
import org.cloudbus.cloudsim.ParameterException;

import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

public class Workflow {
	
	private Graph<Task, DataDependency> workflow;
	
	public Workflow() {
		workflow = new DirectedSparseMultigraph<Task, DataDependency>();
	}
	
	public void addTask(Task task) {
		workflow.addVertex(task);
	}
	
	public void addFile(File file, Task taskGeneratingThisFile, List<Task> tasksRequiringThisFile) {
		try {
			int depth = taskGeneratingThisFile.getDepth() + 1;
			for (Task t : tasksRequiringThisFile) {
				workflow.addEdge(new DataDependency(file), taskGeneratingThisFile, t);
				t.incNDataDependencies();
				setDepth(t, depth);
			}
		} catch (ParameterException e) {
			e.printStackTrace();
		}
	}
	
	private void setDepth(Task task, int depth) {
		if (task.getDepth() < depth) {
			task.setDepth(depth);
			for (Task successor : workflow.getSuccessors(task)) {
				setDepth(successor, depth + 1);
			}
		}
	}
	
	public Graph<Task, DataDependency> getGraph() {
		return workflow;
	}
	
	public Collection<Task> getTasks() {
		return workflow.getVertices();
	}
	
//	public List<Task> getSortedTasks() {
//		List<Task> tasks = new ArrayList<Task>(getTasks());
//		Collections.sort(tasks);
//		return tasks;
//	}
	
	public Collection<Task> getChildren(Task task) {
		return workflow.getSuccessors(task);
	}
	
	public List<Task> getSortedChildren (Task task) {
		List<Task> children = new ArrayList<Task>(getChildren(task));
		Collections.sort(children);
		return children;
	}
	
	public int getNTasks() {
		return workflow.getVertexCount();
	}
	
	@Override
	public String toString() {
		return workflow.toString();
	}
}
