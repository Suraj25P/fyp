package com.googlecode.distributedscheduling;

import java.util.Vector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import static java.lang.System.out;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

public class SimulatorEngine {

    

	/*p[i] represents all tasks submitted to the ith machine*/
    public PriorityQueue<Task> p[];   

    /*Comparator for tasks*/
    private Comparator<Task> comparator;

    /*The total number of tasks*/
    int n;

    /*The number of machines*/
    int m;

    /*The poisson arrival rate*/
    double lambda;

    /*Meta-task set size*/
    int S;
    

    /*Arrival time of tasks*/
    public int arrivals[];
    

    
    /*ETC matrix*/
    public int etc[][];
    
    public int Tasks[];
    public int Load[];

    /*Machine availability time, the time at which machine i finishes all previously assigned tasks.*/
    public int mat[];

    private SchedulingEngine eng;

    TaskHeterogeneity TH;
    MachineHeterogeneity MH;
    
    /*For calculating avg completion time*/
    long sigma;
    double costperunit = 0.5; 
    /*For calculating makespan*/
    long TotalLength;
    long makespan;
    int turnaroundTime;
    int avgThroughput;
    int avgWaitingTime;
    double TotalCost;
    ArrayList<Integer> ExeTimeArray = new ArrayList<Integer>();

    public SimulatorEngine(int NUM_MACHINES, int NUM_TASKS, double ARRIVAL_RATE, int metaSetSize,Heuristic HEURISTIC, TaskHeterogeneity th, MachineHeterogeneity mh, int[] userCloudlets, int[] fetchedLoad, int[] fetchedarrivals){
        
        sigma=0;
        makespan=0;
        MH=mh;
        TH=th;
        n=NUM_TASKS;
        S=metaSetSize;
       
        m=NUM_MACHINES;
        
        lambda=ARRIVAL_RATE;
        comparator=new TaskComparator();
        eng=new SchedulingEngine(this,HEURISTIC);
        p=new PriorityQueue[m];
        //int LocalTasks[] = new int[n];
        for(int i=0;i<p.length;i++)
            p[i]=new PriorityQueue<Task>(5,comparator);
        
//		for( int i= 0;i<n;i++) {
//
//			Random r = new Random();
//			LocalTasks[i]= r.nextInt(100000) ;
//			
//		}  
		Tasks = userCloudlets;
		//Tasks = LocalTasks;
		
		//int LocalLoad[] = new int[m];
//		for(int i=0; i<this.m;i++) 
//    	{	
//			Random r1 = new Random();
//			LocalLoad[i]  = r1.nextInt(5000);
//    	}
		Load = fetchedLoad;
		//Load=LocalLoad;
		arrivals = fetchedarrivals;
    	long avglen=0;
    	for(int i=0; i<this.n;i++) 
    	{
    		avglen+=Tasks[i];
    	}
    	TotalLength = avglen;
    	mat=new int[m];  
    	
    	
        generateRandoms();
       

    }



	private void generateRandoms(){  
		//arrivals = new ArrivalGenerator(n,lambda).getArrival();
        etc=new ETCGenerator(m,n,TH,MH).getETC(Tasks , Load);
    }

    public void newSimulation(boolean generateRandoms){
        makespan=0;
        TotalCost =0;
        avgThroughput=0;
        ExeTimeArray.clear();
        
        
        sigma=0;
        if(generateRandoms)
            generateRandoms();
        for(int i=0;i<m;i++){
            mat[i]=0;
            p[i].clear();
        }
    }

    public void setHeuristic(Heuristic h){
        this.eng.h=h;
    }

    public long getMakespan() {
        return makespan;
    }
    public int getWaitingTime() {
        return   avgWaitingTime;
    }
    public int getThroughPut() {
    	return avgThroughput;
    }
    public int getTurnAroundTime() {
        return Math.abs(turnaroundTime);
    }
    public double getTotalCost() {
        return Math.abs(TotalCost);
    }
    public long getTotalLength() {

    	return TotalLength;
    }
    public double getDI() {
    	
        int sum = 0;
        for (int i: ExeTimeArray) {
            sum += i;
        }
        double Avg = sum/this.n;      
       int Max = Collections.max(ExeTimeArray);
        int Min = Collections.min(ExeTimeArray); 
        
        double DI =(Max-Min )/Avg;
       
    	return DI;
    }
    public int getAvgLoad() {
    	int len=0;
    	for(int j=0; j<this.m;j++) 
    	{
    		len+=Load[j];
    	}
    	return  Math.abs(len/this.m);
    }
    public long getAvgLengthDifference() {
    	long avglen=0;
    	for(int i=1; i<this.n;i++) 
    	{
    		avglen+= (Tasks[i]-Tasks[i-1]);
    	}
    	return Math.abs(avglen);
    }
    public int getAvgArrivalDifference() {
    	int avglen=0;
    	for(int i=1; i < this.n;i++) 
    	{	
    		avglen+= (arrivals[i]-arrivals[i-1]);
    	}
    	return Math.abs(avglen);
    }
    
    public double  getVariance() 
    { 
	    long sum = 0; 
	    for (int i = 0; i < n; i++) 
	    sum += Tasks[i]; 
	    double mean = (double)sum /  (double)n;
	   
	    double sqDiff = 0; 
	    for (int i = 0; i < n; i++) {
	    	sqDiff += (Tasks[i] - mean) *  (Tasks[i] - mean); 
	    } 
	    	

    	return Math.sqrt((double)sqDiff / n); 
    }
    


    public int[] getArrivals() {
        return arrivals;
    }

    public int[][] getEtc() {
        return etc;
    }

    public void mapTask(Task t, int machine){
        t.set_eTime(etc[t.tid][machine]);
        t.set_cTime( mat[machine]+etc[t.tid][machine] );
        p[machine].offer(t);
        mat[machine]=t.cTime;
    }

    


    public void simulate(){
        /*tick represents the current time*/
        int tick=0;
  
        Vector<Task> metaSet=new Vector<Task>(S);
        int i1=0;
        int i2=S;

        /*Initialization*/
        /*Add the first S tasks to the meta set and schedule them*/
        for(int i=i1;i<i2;i++){
            Task t=new Task(arrivals[i],i);
            metaSet.add(t);
        }
        i1=i2;
        i2=(int) min(i1+S, arrivals.length);
        /*Set tick to the time of the first mapping event*/
        tick=arrivals[i1-1];
        eng.schedule(metaSet,tick);

        /*Set tick to the time of the next mapping event*/
        tick=arrivals[i2-1];

        /*Simulation Loop*/
        do{

            /*Set the current tick value*/
            if(i2==i1){
                tick=Integer.MAX_VALUE;                
                /*Remove all the completed tasks from all the machines*/
                removeCompletedTasks(tick);
                break;
            }
            else{
                /*The time at which the next mapping event takes place*/
                tick=arrivals[i2-1];
                /*Remove all the completed tasks from all the machines*/
                removeCompletedTasks(tick);
            }
            /**/
            
            /*Collect next S OR (i2-i1) tasks to the meta set and schedule them*/
            metaSet=new Vector<Task>(i2-i1);

            for(int i=i1;i<i2;i++){
                Task t=new Task(arrivals[i],i);
                metaSet.add(t);
            }
            eng.schedule(metaSet, tick);
            /**/

            /*Set values for next iteration.*/
            i1=i2;
            i2=(int) min(i1+S, arrivals.length);
            /**/

        }while(!discontinueSimulation());
    }

    private void removeCompletedTasks(int currentTime){
    	//ArrayList<Integer> ExeTimeArray = new ArrayList<Integer>();
    	int txe = 0;
    	int wt =0;
    	double cost =0;
    	int tt[] = new int[this.m];
        for(int i=0;i<this.m;i++){
        	
            if(!p[i].isEmpty()){
                Task t=p[i].peek();               
                while(t.cTime<=currentTime){                 
                    sigma+=t.cTime;
                    makespan=max(makespan,t.cTime);
                    txe +=t.eTime;
                    ExeTimeArray.add(t.eTime);
                    cost += costperunit * t.get_eTime();
                    wt = txe-t.eTime;
                    //out.println("Removing task "+t.tid+" at time "+currentTime + "    arrival time" + t.get_aTime() + "    ExecTime"+t.get_eTime() +"    CompTIme:"+t.get_cTime());////////////////////////
                    tt[i]+=(t.eTime);
                    t=p[i].poll();
                    if(!p[i].isEmpty())
                        t=p[i].peek();
                    else
                        break;
                }
            }
            
        }
        for(int i=0;i<this.m;i++) {
        	turnaroundTime +=Math.abs(tt[i]);
        }
        
        avgThroughput = txe;
        avgWaitingTime = wt/this.m;
        TotalCost = cost;

       
        



    }

    private boolean discontinueSimulation(){
        boolean result=true;
        for(int i=0;i<this.m && result;i++)
            result=result && p[i].isEmpty();
        return result;
    }

    private long max(long a,long b){
        if(a>b)
            return a;
        else
            return b;
    }

    private long min(long a,long b){
        if(a<b)
            return a;
        else
            return b;
    }

   
}
