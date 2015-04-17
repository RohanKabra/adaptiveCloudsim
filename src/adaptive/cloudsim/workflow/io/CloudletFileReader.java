package adaptive.cloudsim.workflow.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import adaptive.cloudsim.workflow.Task;
import adaptive.cloudsim.workflow.Workflow;
//import org.cloudbus.cloudsim.ParameterException;


public class CloudletFileReader extends LogFileReader {

	@Override
	protected void fillDataStructures(int userId, String filePath,
			boolean fileNames, boolean kernelTime, Workflow workflow) {
		try {
			BufferedReader logfile = new BufferedReader(
					new FileReader(filePath));

			String line = null;


//				int timeInMs = 0;
				int inputSize = 0;
				int outputSize = 0;
				int incomingTime = 0;
				
				
				while ((line = logfile.readLine())!=null) {
					//splitLine = line.split(" : ");
					
					int noOfTasks = Integer.parseInt(line);
					int temp = noOfTasks;
					
					while(temp--!=0 )
					{
						Task task = new Task("name"+incomingTime, "No params", 10*incomingTime ,  workflow, userId,
								cloudletId, 2000, (inputSize + outputSize), 0, 1,
								inputSize, outputSize, utilizationModel,
								utilizationModel, utilizationModel);
						
						taskIdToTask.put((long) cloudletId, task);

						cloudletId++;
					}
					incomingTime++;
//					int fileSize = 1;
//					if (splitLine.length > 4) {
//						fileSize = Integer.parseInt(splitLine[4]) / 1024;
//					}
//					fileSize = (fileSize > 0) ? fileSize : 1;
//
//					if (!fileNameToFile.containsKey(fileName)) {
//						fileNameToFile.put(fileName,
//								new org.cloudbus.cloudsim.File(fileName,
//										fileSize));
//						fileNameToConsumingTaskIds.put(fileName,
//								new ArrayList<Long>());
//					}
//					fileNameToConsumingTaskIds.get(fileName).add(
//							(long) cloudletId);
//					inputSize += fileSize;
				}

//				for (int i = 0; i < 4; i++) {
//					String[] userTime = logfile.readLine().split("\t")[1]
//							.split("m|\\.|s");
//					int tempTimeInt = Integer.parseInt(userTime[0]) * 60 * 1000
//							+ Integer.parseInt(userTime[1]) * 1000
//							+ Integer.parseInt(userTime[2]);
//					if (i < 3)
//						timeInMs += tempTimeInt;
//					else if (i == 3)
//						incomingTime = tempTimeInt;
//					if (!kernelTime) {
//						logfile.readLine();
//						break;
//					}
//				}

//				while ((line = logfile.readLine()) != null
//						&& line.contains("output")) {
//					splitLine = line.split(" : ");
//					String fileName = splitLine[3];
//					if (fileNames) {
//						fileName = fileName.substring(Math.max(0,
//								fileName.lastIndexOf('/') + 1));
//					}
//					int fileSize = 1;
//					if (splitLine.length > 4) {
//						fileSize = Integer.parseInt(splitLine[4]) / 1024;
//					}
//					fileSize = (fileSize > 0) ? fileSize : 1;
//
//					if (!fileNameToFile.containsKey(fileName)) {
//						fileNameToFile.put(fileName,
//								new org.cloudbus.cloudsim.File(fileName,
//										fileSize));
//						fileNameToConsumingTaskIds.put(fileName,
//								new ArrayList<Long>());
//					}
//					fileNameToProducingTaskId.put(fileName, (long) cloudletId);
//					outputSize += fileSize;
//				}

				
				
			
			

			logfile.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
