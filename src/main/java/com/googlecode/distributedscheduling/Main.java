package com.googlecode.distributedscheduling;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;


import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import static java.lang.System.out;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;


public class Main  {
	 double ARRIVAL_RATE=19;
	 public int anum;
	 public static   int Fetchedarrivals[];
	 public  static int  UserCloudlets[];
	 public static  int FetchedLoad[];
	 public  int noOfTasks;
	 public  int noOfMac;
	 public  long TotalLen;
	 public long AvgLoad;
	 public int AvgArrivalDiff;
	private JLabel label;
	private JFrame frame;
	private   JPanel panel;
	private JTextField jt;
	private JTextField noMac;
	private JRadioButton r1,r2,r3,r4;
	private ButtonGroup bg;
	public PriorityQueue<Task> p[]; 
	
	
	public Main() {
		 frame = new JFrame();
		 panel = new JPanel(); 
		 noMac =  new JTextField();
		 jt = new JTextField();
		
		JButton generateTasks = new JButton("Generate Cloudlets");
		JButton PredictAlgorithm = new JButton("Predict");
		JButton Simulate = new JButton("Simulate");
		
		r1 = new JRadioButton ("Maksepan");
		r2 = new JRadioButton ("Throughput");
		r3 = new JRadioButton ("Cost");
		r4 = new JRadioButton ("Degree of imbalance");
		bg = new ButtonGroup();
		bg.add(r1);
		bg.add(r2);
		bg.add(r3);
		bg.add(r4);
		
		label = new JLabel();
		panel.setBorder(BorderFactory.createEmptyBorder(30 , 30 , 10 , 30));
		panel.setLayout(new GridLayout(0,1));
		
		
		
		JLabel l1 =new JLabel("Enter number of tasks:");
		panel.add(l1);
		panel.add(jt);
		
		JLabel l2 =new JLabel("Enter number of Vms:");
		
		panel.add(l2);
		panel.add(noMac);
		panel.add(generateTasks);
		generateTasks.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				int n = Integer.parseInt(jt.getText());
				int m =  Integer.parseInt(noMac.getText());
				noOfTasks = n;
				noOfMac = m;
				int LocalTasks[] = new int[n];
				int LocalLoad[] = new int[m];
				p=new PriorityQueue[noOfMac]; 
				for( int i= 0;i<n;i++) {

					Random r = new Random();
					LocalTasks[i]= r.nextInt(100000) ;
					
				}  
				
				for(int i=0; i<m;i++) 
		    	{	
					Random r1 = new Random();
					LocalLoad[i]  = r1.nextInt(5000);
		    	}
				Fetchedarrivals=new ArrivalGenerator(n,ARRIVAL_RATE).getArrival();
				UserCloudlets = LocalTasks;
				FetchedLoad = LocalLoad;
//*************************** compute total len****************************//
		    	long avglen=0;
		    	for(int i=0; i<n;i++) 
		    	{
		    		avglen+=UserCloudlets[i];
		    	}
		    	TotalLen = avglen;
//********************************compute avg load****************************//
		    	int load=0;
		    	for(int j=0; j<m;j++) 
		    	{
		    		load+=FetchedLoad[j];
		    	}
		    	AvgLoad = Math.abs(load/m);
//******************************** Compute Arrival Diff**************************//
		       	int avgArrDiff=0;
		    	for(int i=1; i<n;i++) 
		    	{
		    		avgArrDiff+= (Fetchedarrivals[i]-Fetchedarrivals[i-1]);
		    	}
		    	AvgArrivalDiff =  Math.abs(avgArrDiff);
		    
		    	out.println("NOOFTASKS:"+ noOfTasks +"   Total len : " + TotalLen + "  Number of machines :" +noOfMac+" AvgArrival Diff:"+AvgArrivalDiff + "   Avg Load :"+ AvgLoad);
				label.setText("Tasks Generated");
				panel.add(label);
				JLabel Txt =new JLabel("Select the metric that you want to optimize");
				panel.add(Txt);
				panel.add(r1);
				panel.add(r2);
				panel.add(r3);
				panel.add(r4);
				panel.add(PredictAlgorithm);
				
			}
			
		});
		
		
		PredictAlgorithm.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String s=" Best Algorithm for optimizing  " ;
				int m = 0;
				if(r1.isSelected()) {
					s = s + "makespan is : ";
					m = 1;
				}
					
				else if(r2.isSelected()) {
					s= s +"Throughput is ";
					m=2;
					}
				else if(r3.isSelected()) {
					s= s +"Cost is ";
					m=3;
					}
				else if(r4.isSelected()) {
					s= s +"Degree of imbalance is ";
					m=4;
					}
				
				
			
				Process p;
				try {
				    String[] cmd = {
				    	      "python",
				    	      "G:\\distributedscheduling-master\\NeuralNetworkConti\\NNpred.py",
				    	      Integer.toString(m),
				    	      Integer.toString(noOfTasks),
				    	      Integer.toString(noOfMac),
				    	      Long.toString(TotalLen),
				    	      Long.toString(AvgLoad),
				    	      Integer.toString(AvgArrivalDiff),				    	      
				    	    };
					p = Runtime.getRuntime().exec(cmd);
					BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String ret = in.readLine();
					System.out.println("value is : "+ret);
					anum = Integer.parseInt(ret);
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				
				
				if(anum == 0) {
					s = s + "MET";
				}
				if(anum == 1) {
					s = s + "MCT";
				}
				if(anum == 2) {
					s = s + "Sufferage";
				}
				if(anum == 3) {
					s = s + "MinMin";
				}
				if(anum == 4) {
					s = s + "MinMean";
				}
				if(anum == 5) {
					s = s + "MinVar";
				}

				
				JLabel Txt =new JLabel(s);
				panel.add(Txt);
				panel.add(Simulate);
			}
			
		});
		
		Simulate.addActionListener(new ActionListener() {


			public void actionPerformed(ActionEvent e) {
				
				 DecimalFormat df = new DecimalFormat();
		    	 df.setMaximumFractionDigits(2);		    	 
		         long t1=System.currentTimeMillis();
		         int metaSetSize=29;
		         Heuristic h=null;
		         TaskHeterogeneity TH=null;
		         MachineHeterogeneity MH=null;
		         Heuristic HEURISTIC=null;
		         TaskHeterogeneity th=TH.HIGH;
		         MachineHeterogeneity mh=MH.HIGH;
		         Heuristic htype = null;
					if(anum == 0) {
						  htype = Heuristic.MET;
					}
					if(anum == 1) {
						  htype = Heuristic.MCT;
					}
					if(anum == 2) {
						  htype = Heuristic.Sufferage;
					}
					if(anum == 3) {
						  htype = Heuristic.MinMin;
					}
					if(anum == 4) {
						  htype = Heuristic.MinMean;
					}
					if(anum == 5) {
						  htype = Heuristic.MinVar;
					}
		      
//		        	 SimulatorEngine se=new SimulatorEngine(noOfMac,noOfTasks, ARRIVAL_RATE, metaSetSize,null,th, mh,UserCloudlets,FetchedLoad,Fetchedarrivals);
//		        	
//		        	 se.newSimulation(true);
//		             
//		                 se.setHeuristic(htype);
//		                
//		                 se.simulate();		   
//		                
//		                 out.println("\nMakespan ="+se.getMakespan()+"  COST:"+se.getTotalCost()+"  Throughput:"+se.getThroughPut()+"   DI :"+se.getDI()  + " Strategy:"+htype.toString());	            		                 
					Heuristic[] htype1=Heuristic.values();
					SimulatorEngine se=new SimulatorEngine(noOfMac,noOfTasks, ARRIVAL_RATE, metaSetSize,null,th, mh,UserCloudlets,FetchedLoad,Fetchedarrivals); 
					for(int j=0;j<htype1.length;j++){		         			       		     
						String type = htype1[j].toString();
						se.setHeuristic(htype1[j]);
						se.simulate();
						out.println("\nMakespan ="+se.getMakespan()+"  COST:"+se.getTotalCost()+"  Throughput:"+se.getThroughPut()+"   DI :"+se.getDI()+"  Strategy:"+htype1[j].toString()  );///////////////
		            	}
		         
			}
			
		});
		
		frame.add(panel,BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Cloud simulation");
		frame.pack();
		frame.setVisible(true);
		
		
	} 

	
	
     public static void main(String...args) throws IOException{
    	
    	 new Main();
   	 
     }


 }   	 
    	 
    	 
    	 
    	  			
 /* *************************** Data set generator*********************************************** */
////System.out.println('l');
////DecimalFormat df = new DecimalFormat();
////df.setMaximumFractionDigits(2);
////
////long t1=System.currentTimeMillis();
////
/////*Specify the parameters here*/
////
////
////int metaSetSize=29;
////
////Heuristic h=null;
////TaskHeterogeneity TH=null;
////MachineHeterogeneity MH=null;
////
////Heuristic HEURISTIC=null;
////TaskHeterogeneity th=TH.HIGH;
////MachineHeterogeneity mh=MH.HIGH;
////
////
////double ARRIVAL_RATE=19;
/////*Specify the parameters here*/  
////// int NUM_MACHINES=10;
//////int NUM_TASKS = 1000;
////Heuristic[] htype=Heuristic.values();
////long sigmaMakespan[]=new long[htype.length];
////long avgMakespan=0;
////Random r = new Random();
//////int NUM_MACHINES=10;
////
////
////int no_of_simulations=1;
////
////for(int i=0;i<no_of_simulations;i++){
////
////
////  int NUM_TASKS = r.nextInt((800 - 500) + 1) + 500;
////  int NUM_MACHINES=r.nextInt((30 - 10) + 1) + 10;
////	int LocalTasks[] = new int[NUM_TASKS];
////	int LocalLoad[] = new int[NUM_MACHINES];
////	for( int k= 0;k<NUM_TASKS;k++) {
////
////		Random rn = new Random();
////		LocalTasks[k]= rn.nextInt(100000) ;
////		
////	}  
////	
////	for(int j=0; j<NUM_MACHINES;j++) 
////	{	
////		Random r1 = new Random();
////		LocalLoad[j]  = r1.nextInt(5000);
////	}
////	Fetchedarrivals=new ArrivalGenerator(NUM_TASKS,ARRIVAL_RATE).getArrival();
////	UserCloudlets = LocalTasks;
////	FetchedLoad = LocalLoad;
////  
////  SimulatorEngine se=new SimulatorEngine(NUM_MACHINES, NUM_TASKS, ARRIVAL_RATE, metaSetSize,null,th, mh);  
////  ArrayList<String> OneRowData = new ArrayList<String>();
////
////
//////SimulatorEngine se=new SimulatorEngine(NUM_MACHINES, NUM_TASKS, ARRIVAL_RATE, metaSetSize,null,th, mh); 
////se.newSimulation(true);
////out.println("NOOFTASKS:"+ NUM_TASKS +"   Total len : " + se.getTotalLength() + "  Number of machines :" +NUM_MACHINES+" AvgArrival Diff:" + se.getAvgArrivalDifference()+"   Avg Load :"+ se.getAvgLoad());
////OneRowData.add(Integer.toString(NUM_TASKS));
////OneRowData.add(Integer.toString(NUM_MACHINES));
////OneRowData.add(Long.toString(se.getTotalLength()));
////OneRowData.add(Long.toString(se.getAvgLoad()));
////OneRowData.add( Integer.toString(se.getAvgArrivalDifference()));
////
////
////ArrayList<Long> CostScoreArray = new ArrayList<Long>();
////for(int j=0;j<htype.length;j++){
////	
////	 
////	double score =0;
////	String type = htype[j].toString();
////   se.setHeuristic(htype[j]);
////   se.simulate();
////  
////   out.println("\nMakespan ="+se.getMakespan()+"  COST:"+se.getTotalCost()+"  Throughput:"+se.getThroughPut()+"   DI :"+se.getDI()+"  Strategy:"+htype[j].toString()  );///////////////
////   
////   CostScoreArray.add(se.getMakespan());
////
////
////
////
////   
////}
////OneRowData.add(Integer.toString(CostScoreArray.indexOf(Collections.min(CostScoreArray))));
////out.println(" oneROw for algo : "+ OneRowData);
////String csv = String.join(",", OneRowData);
////String file= "BEstMksConti.csv";
////FileWriter pw = new FileWriter(file,true);
////pw.append(csv + "\n");
////pw.flush();
////pw.close();
////
////}
////
////
////

       

      
        //out.println(sb);



