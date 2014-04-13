import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import Trace.Trace;
import BitOperations.BitOperations;


public class Test {

	  public static void main(String a[]) {

	        Thread addthread = new Thread() {
	                public void run() {
	                	for(int i =0;i<20;i++){
	                		System.out.println("a");
	                		try {
								sleep(5);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                	}
	                	
	            }
	        };
	        Thread subtractThread = new Thread() {
	              public void run() {

	              for(int i =0;i<20;i++){
	            	  System.out.println("b");
	            		try {
							sleep(4);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	              }

	            }
	        };
	        addthread .start();
	        subtractThread .start();
	    }
}
