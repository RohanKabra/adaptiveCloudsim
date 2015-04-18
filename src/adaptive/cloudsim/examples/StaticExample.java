package adaptive.cloudsim.examples;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.distributions.ContinuousDistribution;

import adaptive.cloudsim.CloudletSchedulerGreedyDivided;
import adaptive.cloudsim.DynamicHost;
import adaptive.cloudsim.DynamicModel;
import adaptive.cloudsim.DynamicVm;
import adaptive.cloudsim.TaskSubmitter;
import adaptive.cloudsim.VmAllocationPolicyRandom;
import adaptive.cloudsim.WorkLoadAnalyzer;
import adaptive.cloudsim.workflow.Task;
import adaptive.cloudsim.workflow.Workflow;
import adaptive.cloudsim.workflow.io.CloudletFileReader;
import adaptive.cloudsim.workflow.scheduler.AbstractWorkflowScheduler;
import adaptive.cloudsim.workflow.scheduler.DynamicScheduler;

public class StaticExample {
	public static AbstractWorkflowScheduler scheduler = null; 
	public static Datacenter dcntr = null;
	public static int id;
	//public static double latency ;
	public static void main(String[] args) {
		double totalRuntime = 0;
		Parameters.parseParameters(args);
		AbstractWorkflowScheduler.staticVMProvisioning = true ;
		try {
			
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < Parameters.numberOfRuns; i++) {
				StaticExample ex = new StaticExample();
				if (!Parameters.outputDatacenterEvents) {
					Log.disable();
				}
				// Initialize the CloudSim package
				int num_user = 1; // number of grid users
				Calendar calendar = Calendar.getInstance();
				boolean trace_flag = false; // mean trace events
				CloudSim.init(num_user, calendar, trace_flag);
				
				scheduler = ex.createScheduler(i);
				Workflow workflow = buildWorkflow(scheduler);
				
				Log.printLine("worflow size : " + workflow.getTasks().size());
				ex.submitWorkflow(workflow, scheduler);
				
				dcntr = ex.createDatacenter("Datacenter");
				//createVmsPredicted(i, scheduler);
				
				List<Vm> vmlist = createVMList(5.0 ,  scheduler.getId(), i);
				scheduler.submitVmList(vmlist);//Log.printLine("predicted size : "+ vmlist.size());
				//scheduler.submitVmList(vmlist);

//				scheduler.createVmsInDatacenter(dcntr.getId());
				//scheduler.registerDynamicChecks();
				//scheduler.registerPeriodicChecks();
				
				// Start the simulation
				CloudSim.startSimulation();
				CloudSim.stopSimulation();
				
				long stopTime = System.currentTimeMillis();

				System.out.println("Time in milliseconds :"+(stopTime-startTime));

				//totalRuntime += scheduler.getRuntime();
				//System.out.println(scheduler.getRuntime() / 60);
			}

			Log.printLine("Average runtime in minutes: " + totalRuntime
					/ Parameters.numberOfRuns / 60);
			Log.printLine("Total Workload: " + Task.getTotalMi() + "mi "
					+ Task.getTotalIo() + "io " + Task.getTotalBw() + "bw");
//			Log.printLine("Total VM Performance: " + DynamicHost.getTotalMi()
//					+ "mips " + DynamicHost.getTotalIo() + "iops "
//					+ DynamicHost.getTotalBw() + "bwps");
//			Log.printLine("minimum minutes (quotient): " + Task.getTotalMi()
//					/ DynamicHost.getTotalMi() / 60 + " " + Task.getTotalIo()
//					/ DynamicHost.getTotalIo() / 60 + " " + Task.getTotalBw()
//					/ DynamicHost.getTotalBw() / 60);
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}

	}

	public AbstractWorkflowScheduler createScheduler(int i) {
		try {
			switch (Parameters.scheduler) {

			case DYNAMIC:
				return new DynamicScheduler("DynamicScheduler", Parameters.taskSlotsPerVm);
				
			default:
				return new DynamicScheduler("DynamicScheduler", Parameters.taskSlotsPerVm);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
	
	public static void createVmsPredicted(int run, AbstractWorkflowScheduler scheduler) {
		// Create VMs
//		double arrivalRate = WorkLoadAnalyzer.getPredictedArrivalRate(0 , CloudSim.clock() + 10 , 5 , 5 );	
//		//Log.printLine("arrivale predicted rate : "+ (arrivalRate-scheduler.getIdleTaskSlots().size()) );
////		Log.printLine("time is www " + CloudSim.clock());
//		double temp = arrivalRate-scheduler.getIdleTaskSlots().size();
//		Log.printLine("predicted arrival rate :"+ temp+" Time :"+CloudSim.clock());
//		
//		if (temp>0)
//		{
//			List<Vm> vmlist = createVMList(arrivalRate-scheduler.getIdleTaskSlots().size() ,  scheduler.getId(), run);
//			scheduler.submitVmList(vmlist);//Log.printLine("predicted size : "+ vmlist.size());
//			scheduler.createVmsInDatacenter(dcntr.getId());
//			//Log.printLine("vm predicted " + CloudSim.clock());
//		}
//		else
//		{
//			scheduler.submitCloudlets();
//		}
//		
		
	}

	
	
	public static void createVmsActual(int run, AbstractWorkflowScheduler scheduler) {
//		// Create VMs
//		//Log.printLine("vm actual " + CloudSim.clock());
//		double actualArrival = TaskSubmitter.getNewReadyTaskNumber(CloudSim.clock());
//		//
//		Log.printLine("actual arrival rate :"+ actualArrival+"Time :"+CloudSim.clock());
//		List<Vm> vmlist = new ArrayList<Vm>();
//		double temp = actualArrival-scheduler.getIdleTaskSlots().size();
//		if (temp>0) 
//		{
//         Log.printLine("availaibility  :"+temp);
//		vmlist = createVMList(temp ,  scheduler.getId(), run);
//		//Log.printLine("actual size : "+ vmlist.size());
//		latency+=vmlist.size();
//		scheduler.submitVmList(vmlist);
//		//Log.printLine("size : "+vmlist.size()+" actual arrival rate 1 :"+ actualArrival+"Time :"+CloudSim.clock());
//		scheduler.createVmsInDatacenter(dcntr.getId());
//		//Log.printLine(" bla bla  bla ");
//		}
//		else
//		{
//			int stragglerNumber = (int) Math.ceil( actualArrival+10)<scheduler.getIdleTaskSlots().size()?(scheduler.getIdleTaskSlots().size()-(int) Math.ceil( actualArrival+10)):0;
//			//Log.printLine("abcd\n");
//			Log.printLine(" deficit :"+ (-temp)+"Time :"+CloudSim.clock());
//			while(stragglerNumber>0)
//			{
//				
//					if(!scheduler.getIdleTaskSlots().isEmpty())
//					{
//						
//					  Vm vm = scheduler.getIdleTaskSlots().remove();
//					  
//					    //List<VM> result = new ArrayList<Task>();
//						Iterator<Vm> iter = scheduler.getVmsCreatedList().iterator();
//						//Log.printLine("timTasks size : " + timeTasks.size());
//						
//						while( iter.hasNext() )
//						{   
//							
//							Vm v = iter.next();
//							if(vm.getId()==v.getId() ) 
//							{
//							  Log.print("Lol"); 	
//							  iter.remove();
//							  
//							}
//						}		
//						
//
//						 //List<VM> result = new ArrayList<Task>();
//						Iterator<Vm> iter2 = scheduler.getVmList().iterator();
////						int index = 0;
//						//Log.printLine("timTasks size : " + timeTasks.size());
//						while( iter2.hasNext() )
//						{   
//							Vm v = iter2.next();
//							if(vm.getId()==v.getId() ) 
//							{
//							  iter2.remove();
//							}
//						}	
//
//					       
//					  //vm.getHost().getBw();
//					
//					} 
//					
//					stragglerNumber--;
//					
//			}
//			
//			Log.printLine("ffgfgf\n");
			
		//}
	scheduler.submitCloudlets();
		
	}
	
	public static Workflow buildWorkflow(AbstractWorkflowScheduler scheduler) {
		switch (Parameters.experiment) {
		case SINUSODIAL_CLOUDLET:
			return new CloudletFileReader().parseLogFile(scheduler.getId(),
					"examples/CloudletInput", true, true, ".*jpg");
		default : 
			Log.printLine("Wrong choice\n");
			return null;
		}
	}

	
	
	
	public void submitWorkflow(Workflow workflow, AbstractWorkflowScheduler scheduler) {
		// Create Cloudlets and send them to Scheduler
		List<Task> tasks = new ArrayList<Task>(workflow.getTasks());
		Log.printLine("task input size : "+ tasks.size());
		TaskSubmitter tsubmitter = new TaskSubmitter(tasks);
		scheduler.submitWorkflow(workflow);
	}

	// all numbers in 1000 (e.g. kb/s)
	public Datacenter createDatacenter(String name) {
		Random numGen;
		numGen = Parameters.numGen;
		List<DynamicHost> hostList = new ArrayList<DynamicHost>();
		int hostId = 0;
		long storage = 1024 * 1024;

		int ram = (int) (2 * 1024 * Parameters.nCusPerCoreOpteron270 * Parameters.nCoresOpteron270);
		for (int i = 0; i < Parameters.nOpteron270; i++) {
			double mean = 1d;
			double dev = Parameters.bwHeterogeneityCV;
			ContinuousDistribution dist = Parameters.getDistribution(
					Parameters.bwHeterogeneityDistribution, mean,
					Parameters.bwHeterogeneityAlpha,
					Parameters.bwHeterogeneityBeta, dev,
					Parameters.bwHeterogeneityShape,
					Parameters.bwHeterogeneityLocation,
					Parameters.bwHeterogeneityShift,
					Parameters.bwHeterogeneityMin,
					Parameters.bwHeterogeneityMax,
					Parameters.bwHeterogeneityPopulation);
			long bwps = 0;
			while (bwps <= 0) {
				bwps = (long) (dist.sample() * Parameters.bwpsPerPe);
			}
			mean = 1d;
			dev = Parameters.ioHeterogeneityCV;
			dist = Parameters.getDistribution(
					Parameters.ioHeterogeneityDistribution, mean,
					Parameters.ioHeterogeneityAlpha,
					Parameters.ioHeterogeneityBeta, dev,
					Parameters.ioHeterogeneityShape,
					Parameters.ioHeterogeneityLocation,
					Parameters.ioHeterogeneityShift,
					Parameters.ioHeterogeneityMin,
					Parameters.ioHeterogeneityMax,
					Parameters.ioHeterogeneityPopulation);
			long iops = 0;
			while (iops <= 0) {
				iops = (long) (long) (dist.sample() * Parameters.iopsPerPe);
			}
			mean = 1d;
			dev = Parameters.cpuHeterogeneityCV;
			dist = Parameters.getDistribution(
					Parameters.cpuHeterogeneityDistribution, mean,
					Parameters.cpuHeterogeneityAlpha,
					Parameters.cpuHeterogeneityBeta, dev,
					Parameters.cpuHeterogeneityShape,
					Parameters.cpuHeterogeneityLocation,
					Parameters.cpuHeterogeneityShift,
					Parameters.cpuHeterogeneityMin,
					Parameters.cpuHeterogeneityMax,
					Parameters.cpuHeterogeneityPopulation);
			long mips = 0;
			while (mips <= 0) {
				mips = (long) (long) (dist.sample() * Parameters.mipsPerCoreOpteron270);
			}
			if (numGen.nextDouble() < Parameters.likelihoodOfStraggler) {
				bwps *= Parameters.stragglerPerformanceCoefficient;
				iops *= Parameters.stragglerPerformanceCoefficient;
				mips *= Parameters.stragglerPerformanceCoefficient;
			}
			hostList.add(new DynamicHost(hostId++, ram, bwps, iops, storage,
					Parameters.nCusPerCoreOpteron270, Parameters.nCoresOpteron270, mips));
		}

		ram = (int) (2 * 1024 * Parameters.nCusPerCoreOpteron2218 * Parameters.nCoresOpteron2218);
		for (int i = 0; i < Parameters.nOpteron2218; i++) {
			double mean = 1d;
			double dev = Parameters.bwHeterogeneityCV;
			ContinuousDistribution dist = Parameters.getDistribution(
					Parameters.bwHeterogeneityDistribution, mean,
					Parameters.bwHeterogeneityAlpha,
					Parameters.bwHeterogeneityBeta, dev,
					Parameters.bwHeterogeneityShape,
					Parameters.bwHeterogeneityLocation,
					Parameters.bwHeterogeneityShift,
					Parameters.bwHeterogeneityMin,
					Parameters.bwHeterogeneityMax,
					Parameters.bwHeterogeneityPopulation);
			long bwps = 0;
			while (bwps <= 0) {
				bwps = (long) (dist.sample() * Parameters.bwpsPerPe);
			}
			mean = 1d;
			dev = Parameters.ioHeterogeneityCV;
			dist = Parameters.getDistribution(
					Parameters.ioHeterogeneityDistribution, mean,
					Parameters.ioHeterogeneityAlpha,
					Parameters.ioHeterogeneityBeta, dev,
					Parameters.ioHeterogeneityShape,
					Parameters.ioHeterogeneityLocation,
					Parameters.ioHeterogeneityShift,
					Parameters.ioHeterogeneityMin,
					Parameters.ioHeterogeneityMax,
					Parameters.ioHeterogeneityPopulation);
			long iops = 0;
			while (iops <= 0) {
				iops = (long) (long) (dist.sample() * Parameters.iopsPerPe);
			}
			mean = 1d;
			dev = Parameters.cpuHeterogeneityCV;
			dist = Parameters.getDistribution(
					Parameters.cpuHeterogeneityDistribution, mean,
					Parameters.cpuHeterogeneityAlpha,
					Parameters.cpuHeterogeneityBeta, dev,
					Parameters.cpuHeterogeneityShape,
					Parameters.cpuHeterogeneityLocation,
					Parameters.cpuHeterogeneityShift,
					Parameters.cpuHeterogeneityMin,
					Parameters.cpuHeterogeneityMax,
					Parameters.cpuHeterogeneityPopulation);
			long mips = 0;
			while (mips <= 0) {
				mips = (long) (long) (dist.sample() * Parameters.mipsPerCoreOpteron2218);
			}
			if (numGen.nextDouble() < Parameters.likelihoodOfStraggler) {
				bwps *= Parameters.stragglerPerformanceCoefficient;
				iops *= Parameters.stragglerPerformanceCoefficient;
				mips *= Parameters.stragglerPerformanceCoefficient;
			}
			hostList.add(new DynamicHost(hostId++, ram, bwps, iops, storage,
					Parameters.nCusPerCoreOpteron2218, Parameters.nCoresOpteron2218, mips));
		}

		ram = (int) (2 * 1024 * Parameters.nCusPerCoreXeonE5430 * Parameters.nCoresXeonE5430);
		for (int i = 0; i < Parameters.nXeonE5430; i++) {
			double mean = 1d;
			double dev = Parameters.bwHeterogeneityCV;
			ContinuousDistribution dist = Parameters.getDistribution(
					Parameters.bwHeterogeneityDistribution, mean,
					Parameters.bwHeterogeneityAlpha,
					Parameters.bwHeterogeneityBeta, dev,
					Parameters.bwHeterogeneityShape,
					Parameters.bwHeterogeneityLocation,
					Parameters.bwHeterogeneityShift,
					Parameters.bwHeterogeneityMin,
					Parameters.bwHeterogeneityMax,
					Parameters.bwHeterogeneityPopulation);
			long bwps = 0;
			while (bwps <= 0) {
				bwps = (long) (dist.sample() * Parameters.bwpsPerPe);
			}
			mean = 1d;
			dev = Parameters.ioHeterogeneityCV;
			dist = Parameters.getDistribution(
					Parameters.ioHeterogeneityDistribution, mean,
					Parameters.ioHeterogeneityAlpha,
					Parameters.ioHeterogeneityBeta, dev,
					Parameters.ioHeterogeneityShape,
					Parameters.ioHeterogeneityLocation,
					Parameters.ioHeterogeneityShift,
					Parameters.ioHeterogeneityMin,
					Parameters.ioHeterogeneityMax,
					Parameters.ioHeterogeneityPopulation);
			long iops = 0;
			while (iops <= 0) {
				iops = (long) (long) (dist.sample() * Parameters.iopsPerPe);
			}
			mean = 1d;
			dev = Parameters.cpuHeterogeneityCV;
			dist = Parameters.getDistribution(
					Parameters.cpuHeterogeneityDistribution, mean,
					Parameters.cpuHeterogeneityAlpha,
					Parameters.cpuHeterogeneityBeta, dev,
					Parameters.cpuHeterogeneityShape,
					Parameters.cpuHeterogeneityLocation,
					Parameters.cpuHeterogeneityShift,
					Parameters.cpuHeterogeneityMin,
					Parameters.cpuHeterogeneityMax,
					Parameters.cpuHeterogeneityPopulation);
			long mips = 0;
			while (mips <= 0) {
				mips = (long) (long) (dist.sample() * Parameters.mipsPerCoreXeonE5430);
			}
			if (numGen.nextDouble() < Parameters.likelihoodOfStraggler) {
				bwps *= Parameters.stragglerPerformanceCoefficient;
				iops *= Parameters.stragglerPerformanceCoefficient;
				mips *= Parameters.stragglerPerformanceCoefficient;
			}
			hostList.add(new DynamicHost(hostId++, ram, bwps, iops, storage,
					Parameters.nCusPerCoreXeonE5430, Parameters.nCoresXeonE5430, mips));
		}

		String arch = "x86";
		String os = "Linux";
		String vmm = "Xen";
		double time_zone = 10.0;
		double cost = 3.0;
		double costPerMem = 0.05;
		double costPerStorage = 0.001;
		double costPerBw = 0.0;
		LinkedList<Storage> storageList = new LinkedList<Storage>();

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
				arch, os, vmm, hostList, time_zone, cost, costPerMem,
				costPerStorage, costPerBw);

		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics,
					new VmAllocationPolicyRandom(hostList, Parameters.seed++),
					storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	public static List<Vm> createVMList(double arrivalRate , int userId, int run) {

		// Creates a container to store VMs. This list is passed to the broker later
		LinkedList<Vm> list = new LinkedList<Vm>();
		// VM Parameters
		long storage = 10000;
		String vmm = "Xen";
		// create VMs
		Vm[] vm = new DynamicVm[(int)arrivalRate];

		for (int i = 0; i < arrivalRate; i++) {
			DynamicModel dynamicModel = new DynamicModel();
			vm[i] = new DynamicVm(id++, userId, Parameters.numberOfCusPerPe, Parameters.numberOfPes,
					Parameters.ram, storage, vmm, new CloudletSchedulerGreedyDivided(),
					dynamicModel, "output/run_" + run + "_vm_" + i + ".csv",
					Parameters.taskSlotsPerVm);
			list.add(vm[i]);
		}

		return list;
	}
}