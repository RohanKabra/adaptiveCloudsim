package adaptive.cloudsim.workflow.scheduler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

import adaptive.cloudsim.DynamicHost;
import adaptive.cloudsim.DynamicVm;
import adaptive.cloudsim.TaskSubmitter;
import adaptive.cloudsim.examples.Parameters;
import adaptive.cloudsim.workflow.DataDependency;
import adaptive.cloudsim.workflow.Task;
import adaptive.cloudsim.workflow.Workflow;


public abstract class AbstractWorkflowScheduler extends DatacenterBroker
		implements WorkflowScheduler {

	public static List<Workflow> workflows;
	private Map<Integer, Vm> vms;
	private int taskSlotsPerVm;
	private Queue<Vm> idleTaskSlots;
	private double runtime;

	// two collections of tasks, which are currently running;
	// note that the second collections is a subset of the first collection
	private Map<Integer, Task> tasks;
	private Map<Integer, Task> speculativeTasks;
	private Collection<Vm> VmInUse;
	
	public Queue<Vm> getIdleTaskSlots() {
		return idleTaskSlots;
	}
	
	public AbstractWorkflowScheduler(String name, int taskSlotsPerVm)
			throws Exception {
		super(name);
		workflows = new ArrayList<>();
		vms = new HashMap<>();
		this.taskSlotsPerVm = taskSlotsPerVm;
		idleTaskSlots = new LinkedList<>();
		tasks = new HashMap<>();
		speculativeTasks = new HashMap<>();
		VmInUse = new ArrayList<Vm>();
	}

	public List<Workflow> getWorkflows() {
		return workflows;
	}

	public int getTaskSlotsPerVm() {
		return taskSlotsPerVm;
	}

	public void submitWorkflow(Workflow workflow) {
		
		workflows.add(workflow);
	}

	private void submitTask(Task task, Vm vm) {
		Log.printLine(CloudSim.clock() + ": " + getName() + ": VM # "
				+ vm.getId() + " starts executing Task # "
				+ task.getCloudletId() + " \"" + task.getName() + " "
				+ task.getParams() + " \"");
		task.setVmId(vm.getId());
		if (Parameters.numGen.nextDouble() < Parameters.likelihoodOfFailure) {
			task.setScheduledToFail(true);
			task.setCloudletLength((long) (task.getCloudletLength() * Parameters.runtimeFactorInCaseOfFailure));
		} else {
			task.setScheduledToFail(false);
		}
		sendNow(getVmsToDatacentersMap().get(vm.getId()),
				CloudSimTags.CLOUDLET_SUBMIT, task);
	}

//	public void registerPeriodicChecks()
//	{
//		/** This loop will run for 100 (100 x 1 ms )ms  **/
//		for(int i=0 ; i<100 ; i++)
//		send(WorkflowExample.dcntr.getId(), 5*i, CloudSimTags.PERIODIC_PREDICTION);
//		
//	}
//	
//	public void registerDynamicChecks()
//	{
//		Collection<Double> temp = TaskSubmitter.getNoOfUniqueTimes();
//		for(Double i : temp)
//		{
//			send(WorkflowExample.dcntr.getId(), i, CloudSimTags.SUBMIT_CLOUDLETS);
//		}
//		
//	}
	
	private void submitSpeculativeTask(Task task, Vm vm) {
		Log.printLine(CloudSim.clock() + ": " + getName() + ": VM # "
				+ vm.getId() + " starts executing speculative copy of Task # "
				+ task.getCloudletId() + " \"" + task.getName() + " "
				+ task.getParams() + " \"");
		task.setVmId(vm.getId());
		if (Parameters.numGen.nextDouble() < Parameters.likelihoodOfFailure) {
			task.setScheduledToFail(true);
			task.setCloudletLength((long) (task.getCloudletLength() * Parameters.runtimeFactorInCaseOfFailure));
		} else {
			task.setScheduledToFail(false);
		}
		sendNow(getVmsToDatacentersMap().get(vm.getId()),
				CloudSimTags.CLOUDLET_SUBMIT, task);
	}

	/**
	 * Submit cloudlets to the created VMs. This function is called after Vms
	 * have been created.
	 * 
	 * @pre $none
	 * @post $none
	 */
	
	@Override
	public void submitCloudlets() {
		
		for (Vm vm : getVmsCreatedList()) {
			vms.put(vm.getId(), vm);
			for (int i = 0; i < getTaskSlotsPerVm(); i++) {
				if(!idleTaskSlots.contains(vm) && !VmInUse.contains(vm)) idleTaskSlots.add(vm);
			}
		}
		
//		for (Workflow workflow : workflows) {
			Collection<Task> tasks = TaskSubmitter.getNewReadyTasks(CloudSim.clock());
			Log.printLine("clock :  "+CloudSim.clock()+" task size : "+ tasks.size());
			reschedule(tasks , vms.values());
			//TaskSubmitter.getNewReadyTasks(CloudSim.clock());
			for (Task task : tasks) {
				if (task.readyToExecute()) {
					taskReady(task);
				}
			}
//		}
		submitTasks();
	}

	protected void submitTasks() {
		Queue<Vm> taskSlotsKeptIdle = new LinkedList<>();
		while (tasksRemaining() && !idleTaskSlots.isEmpty()) {
			Vm vm = idleTaskSlots.remove();
			VmInUse.add(vm);
			Task task = getNextTask(vm);
			// task will be null if scheduler has tasks to be executed, but not
			// for this VM (e.g., if it abides to a static schedule or this VM
			// is a straggler, which the scheduler does not want to assign tasks
			// to)
			if (task == null) {
				VmInUse.add(vm);
				taskSlotsKeptIdle.add(vm);
			} else if (tasks.containsKey(task.getCloudletId())) {
				speculativeTasks.put(task.getCloudletId(),
						task);
				submitSpeculativeTask(task, vm);
			} else {
				tasks.put(task.getCloudletId(), task);
				submitTask(task, vm);
			}
		}
		idleTaskSlots.addAll(taskSlotsKeptIdle);
	}

	protected void clearDatacenters() {
		for (Vm vm : getVmsCreatedList()) {
			if (vm instanceof DynamicVm) {
				DynamicVm dVm = (DynamicVm) vm;
				dVm.closePerformanceLog();
			}
		}
		super.clearDatacenters();
		runtime = CloudSim.clock();
	}

	public double getRuntime() {
		return runtime;
	}

	protected void resetTask(Task task) {
		task.setCloudletFinishedSoFar(0);
		try {
			task.setCloudletStatus(Cloudlet.CREATED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void processCloudletReturn(SimEvent ev) {
		// determine what kind of task was finished,
		Task task = (Task) ev.getData();
		Vm vm = vms.get(task.getVmId());
		Host host = vm.getHost();

		// if the task finished successfully, cancel its copy and remove them
		// both from internal data structures
		if (task.getCloudletStatus() == Cloudlet.SUCCESS) {
			Task originalTask = tasks.remove(task.getCloudletId());
			Task speculativeTask = speculativeTasks
					.remove(task.getCloudletId());

			if ((task.isSpeculativeCopy() && speculativeTask == null)
					|| originalTask == null) {
				return;
			}

			if (task.isSpeculativeCopy()) {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": VM # "
						+ speculativeTask.getVmId()
						+ " completed speculative copy of Task # "
						+ speculativeTask.getCloudletId() + " \""
						+ speculativeTask.getName() + " "
						+ speculativeTask.getParams() + " \"");
				Log.printLine(CloudSim.clock() + ": " + getName() + ": VM # "
						+ originalTask.getVmId() + " cancelled Task # "
						+ originalTask.getCloudletId() + " \""
						+ originalTask.getName() + " "
						+ originalTask.getParams() + " \"");
				vms.get(originalTask.getVmId()).getCloudletScheduler()
						.cloudletCancel(originalTask.getCloudletId());
			} else {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": VM # "
						+ originalTask.getVmId() + " completed Task # "
						+ originalTask.getCloudletId() + " \""
						+ originalTask.getName() + " "
						+ originalTask.getParams() + " \"");
				if (speculativeTask != null) {
					Log.printLine(CloudSim.clock() + ": " + getName()
							+ ": VM # " + speculativeTask.getVmId()
							+ " cancelled speculative copy of Task # "
							+ speculativeTask.getCloudletId() + " \""
							+ speculativeTask.getName() + " "
							+ speculativeTask.getParams() + " \"");
					vms.get(speculativeTask.getVmId()).getCloudletScheduler()
							.cloudletCancel(speculativeTask.getCloudletId());
				}
			}

			// free task slots occupied by finished / cancelled tasks
			Vm originalVm = vms.get(originalTask.getVmId());
			VmInUse.add(vm);
			idleTaskSlots.add(originalVm);
			taskSucceeded(originalTask, originalVm);
			if (speculativeTask != null) {
				Vm speculativeVm = vms.get(speculativeTask.getVmId());
				VmInUse.add(vm );
				idleTaskSlots.add(speculativeVm);
				taskFailed(speculativeTask, speculativeVm);
			}

			// update the task queue by traversing the successor nodes in the
			// workflow
			for (DataDependency outgoingEdge : originalTask.getWorkflow()
					.getGraph().getOutEdges(originalTask)) {
				if (host instanceof DynamicHost) {
					DynamicHost dHost = (DynamicHost) host;
					dHost.addFile(outgoingEdge.getFile());
				}
				Task child = originalTask.getWorkflow().getGraph()
						.getDest(outgoingEdge);
				child.decNDataDependencies();
				if (child.readyToExecute()) {
					taskReady(child);
				}
			}

			// if the task finished unsuccessfully,
			// if it was a speculative task, just leave it be
			// otherwise, -- if it exists -- the speculative task becomes the
			// new original
		} else {
			Task speculativeTask = speculativeTasks
					.remove(task.getCloudletId());
			if (task.isSpeculativeCopy()) {
				Log.printLine(CloudSim.clock()
						+ ": "
						+ getName()
						+ ": VM # "
						+ task.getVmId()
						+ " encountered an error with speculative copy of Task # "
						+ task.getCloudletId() + " \"" + task.getName() + " "
						+ task.getParams() + " \"");
			} else {
				Log.printLine(CloudSim.clock() + ": " + getName() + ": VM # "
						+ task.getVmId() + " encountered an error with Task # "
						+ task.getCloudletId() + " \"" + task.getName() + " "
						+ task.getParams() + " \"");
				tasks.remove(task.getCloudletId());
				if (speculativeTask != null) {
					speculativeTask.setSpeculativeCopy(false);
					tasks.put(speculativeTask.getCloudletId(), speculativeTask);
				} else {
					resetTask(task);
					taskReady(task);
				}
			}
			VmInUse.add(vm);
			idleTaskSlots.add(vm);
			taskFailed(task, vm);
		}

		if (tasksRemaining()) {
			submitTasks();
		} else if (idleTaskSlots.size() == getVmsCreatedList().size()
				* getTaskSlotsPerVm() && TaskSubmitter.getRemainingTasks(CloudSim.clock())==0) {
			Log.printLine(CloudSim.clock() + ": " + getName()
					+ ": All Tasks executed. Finishing...");
			terminate();
			clearDatacenters();
			finishExecution();
		}
	}

}
