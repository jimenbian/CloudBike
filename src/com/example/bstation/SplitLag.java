package com.example.bstation;



public class SplitLag {
     double[] lag=new double[10];
	 public double[] getLag(String str){
    	   String[] line=str.split("\n");
    	   String NewStr=line[0].substring(line[0].indexOf("(")+1, line[0].lastIndexOf(")"));
    	   String[] lg=NewStr.split(",");
    	   lag[0]=Double.valueOf(lg[0]);
    	   lag[1]=Double.valueOf(lg[1]);
    	   return lag;
    	    
     }
}
