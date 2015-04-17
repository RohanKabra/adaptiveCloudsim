package adaptive.cloudsim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import adaptive.cloudsim.workflow.Task;


public class TaskSubmitter {
	
	public static List <Task> timeTasks ;
	
	public TaskSubmitter(List <Task> a) {
		// TODO Auto-generated constructor stub
		timeTasks = a ;
		Collections.sort(timeTasks, new TaskTime());	
	}
	
	public static List<Task> getNewReadyTasks(double Time)
	{
		List<Task> result = new ArrayList<Task>();
		Iterator<Task> iter = timeTasks.iterator();
		//Log.printLine("timTasks size : " + timeTasks.size());
		while( iter.hasNext() )
		{   
			Task temp = iter.next();
			if(temp.getTime()<=Time ) 
			{
				result.add(temp); iter.remove();
			}
		}
		return result;
		
	}
	
	@SuppressWarnings("unused")
	public static int getNewReadyTaskNumber(double Time)
	{
		int  total = 0 , result = 0;
		for( Task  t : timeTasks)
		{
			if(t.getTime()==Time ) {total++;}
		}
		//result ;
		//prevIndex = total ; 
		return total;
		
	}
	
	public static int getRemainingTasks(double Time)
	{
		
		int result=0 ;
		Iterator<Task> iter = timeTasks.iterator();
		//Log.printLine("timTasks size : " + timeTasks.size());
		while( iter.hasNext() )
		{   
			Task temp = iter.next();
			if(temp.getTime()>Time ) 
			{
				result++;
			}
		}
		return result;
		
	}
	
	
	
	public static Collection<Double> getNoOfUniqueTimes()
	{
		List<Double> temp ;
		boolean flag = false;
		temp = new ArrayList<Double>();
		
		for(Task t : timeTasks)
		{
			if(!timeTasks.isEmpty())
			{
				flag = false;
				for(Double tm: temp)
				{
				 if(tm.equals((Double)t.getTime())){
					flag = true;
				 }
				}
				if(!flag) temp.add(t.getTime());
			}
			else
			{
				temp.add(t.getTime());
			}
			
		}
		return temp;
	}
	
	public static double getNextTaskTime()
	{
		return timeTasks.get(0).getTime();
	}

	public static class TaskTime implements Comparator<Task > {	

		@Override
		public int compare(Task o1, Task o2) {
			return Double.compare(o1.getTime() , o2.getTime());	
		}
	}
	
	
}
