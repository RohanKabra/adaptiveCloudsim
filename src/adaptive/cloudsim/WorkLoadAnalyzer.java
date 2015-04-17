package adaptive.cloudsim;

public class WorkLoadAnalyzer {
	
	public WorkLoadAnalyzer() {
		// TODO Auto-generated constructor stub
	}

	public static int getPredictedArrivalRate(int type , double time , double a , double b){
		switch(type)
		{
		case 0 : 
			return (int)(a*Math.sin(Math.toRadians(time))+b);
		case 1 :
			return (int)(a*(time*time)+b);
		
		}
		return 0;
		
	}
}
