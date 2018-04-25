/*
 Programmer:Cory Hershman
 Class: Operating Systems
 Instructor: Mr. Kennedy
 Assignment #: P0000
 Due Date: 10/20/2017
 Last Update: 10/19/2017
 Related Files: MergeThread.java, output.txt, input.txt
 Description: This class uses a main method to bring in a list of integers from an input file and writes out a sorted 
 version of the list to an output file. It delegates the list to a MergeThread to recursively make more MergeThreads
 to sort the list.
 */

package edu.frostburg.cosc460.Hershman;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

public class ThreadedSort {
  
  public static void main(String[] args) {
    File inFile = new File("input.txt");
    File outFile = new File("output.txt");
    ArrayList<Integer> arrayList = new ArrayList<Integer>();
    int left, right, middle;
    
    //Create a scanner from an input file, create a printWriter to print to an output file
    try {
      Scanner scan = new Scanner(inFile);
      PrintWriter printWriter = new PrintWriter(outFile);
      
      //Fill the arraylist with integers from the input file
      for(int i = 0; i < 1; i++){
        while(scan.hasNextInt()) {
          arrayList.add(i, scan.nextInt());
          i++;
        }
      }
      scan.close();
      
      //Fill an array with the items from the arraylist, now that the exact size is known
      int[] array = new int[arrayList.size()];
      for(int i = 0; i < array.length; i++){
        array[i] = arrayList.get(i);
      }
      
      //Left, Right, and Middle will be used to seperate the list
      left = 0;
      right = array.length - 1;
      middle = (left + right)/2;
      
      //Creates a thread to process the list, delegates sections of the list to other threads
      Thread headThread = new Thread(new MergeThread(left, right, array, true));
      
      //Start the MergeThread
      headThread.start();
      
      //Wait for the MergeThread to finish
      try{
        headThread.join();
      } catch(InterruptedException e) {
    	  System.err.println("Interrupted");
          e.printStackTrace();
      }
      
      //Print the now sorted contents of the list to the console for debugging and to the output file
      System.out.println();
      for(int i = 0; i < array.length; i++) {
        System.out.println(array[i]);
        printWriter.print(array[i] + " ");
      }
      printWriter.close();
      
    } catch(FileNotFoundException e) {
      //If either the input or output files were not found, the program ends
      System.out.println("Input file not found");
      System.exit(0);
    }
  }
}

