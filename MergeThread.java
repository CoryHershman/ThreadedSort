/*
 Programmer:Cory Hershman
 Class: Operating Systems
 Instructor: Mr. Kennedy
 Assignment #: P0000
 Due Date: 10/20/2017
 Last Update: 10/19/2017
 Related Files: ThreadedSort.java, output.txt, input.txt
 Description: This class implements Runnable, to be used as part of a constructor for a Thread. This thread
 sorts a list using a merge sort. Sorts a specific section of a list.
 Creates threads recursively to sort the list.
 Multiple MergeThreads can be used to sort a list this way. As long as different threads sort 
 different sections of a list, this should be thread safe. If indexes cross, this will not be thread safe.
 Methods include: run, sort, recThreadSort, and merge.
 */

package edu.frostburg.cosc460.Hershman;


public class MergeThread implements Runnable {
  
  int[] array;   //will be used as a pointer to the list that will be sorted
  int left, right, middle;
  boolean isRecursive;
  volatile static int flush;
  
  //MergeThread object constructor
  //Requires: First index of list to be sorted; last index of list to be sorted; array that holds the list; boolean, true if recursively creating threads, 
  //false if creating single thread 
  //If the thread is sorting only a portion of the list, be mindful to put the correct indexes into the constructor
  //Do not make threads that share indexes, these threads are meant to be used on different sections of a list
  public MergeThread(int l, int r, int[] array, boolean isRecursive) {
    this.array = array;       //The static variable, array, points to the main array that will be manipulated/sorted
    left = l;
    right = r;
    this.isRecursive = isRecursive;
  }
  
  //Run method. Called by start() to run in parrallel.
  //If recursively creating threads, calls recThreadSort
  //If sorting the list without creating threads, calls sort
  public void run() {
     if(isRecursive) {
    	 recThreadSort(left, right, array);
     }
     else {
    	 sort(left, right, array);
     }
    
  }
  
  //sort method
  //Requires: Leftmost index of the sublist to be sorted; rightmost index of the sublist to be sorted; the array 
  //of the main list
  //Returns: Nothing
  //Sorts the list handed to the thread with a merge sort
  public void sort(int left, int right, int array[]) {
    
    if (right == left) return;         //Base case
    int middle = (left + right) /2;    //Middle index will be used to split list
    
    //recursive sort method calls split the list down to single elements
    sort(left, middle, array);         
    sort(middle + 1, right, array);                                       
    
    //merge method merges sublists in the list together, in order
    merge(left, middle, right, array);
    
  }
  
  //recThreadSort method
  //Requires: Leftmost index of the sublist to be sorted; rightmost index of the sublist to be sorted; the array 
  //of the main list
  //Returns: Nothing
  //Sorts the list handed to the thread by creating child threads recursively
  public void recThreadSort(int left, int right, int array[]) {
    if (right == left) return;         //Base case
    int middle = (left + right) /2;    //Middle index will be used to split list
    
    //Recursively make threads on both halves of the list down to single elements
    Thread leftThread = new Thread(new MergeThread(left, middle, array, true));      
    Thread rightThread = new Thread(new MergeThread(middle+1, right, array, true));  
    leftThread.start();
    rightThread.start();
    //Current thread continues once both child threads have finished
    try{                                                                       
      leftThread.join();                                                       
      rightThread.join();                                                      
    }catch(InterruptedException e){                                        
      System.err.println("Interrupted");
      e.printStackTrace();
    }
    
    //merge method merges sublists in the list together, in order
    merge(left, middle, right, array);
  }
  
 //merge method
 //Requires: Leftmost index of the left sublist to be merged; Rightmost index of the right sublist to be merged; the 
 //array of the main list
 //Returns: Nothing
 //Merges sublists together in a sorted order
  private static void merge(int left, int middle, int right, int array[])
  {
    //tmpArray will be used to hold the merged list before writing over the real list
    int tmpArray[] = new int[right - left +1];
    int leftIndex = left;
    int rightIndex = middle + 1;
    int targetIndex = 0;
    
    //Compare the next element of the left list to the next element of the right list.
    //Starts at the beginning of each list
    //Loops until either the left or right list is finished
    while (leftIndex <= middle && rightIndex <= right)
    {
      if ( array[leftIndex] < array[rightIndex] )
      {
        tmpArray[targetIndex] = array[leftIndex];
        leftIndex++;
      }
      else
      {
        tmpArray[targetIndex] = array[rightIndex];
        rightIndex++;
      }
      targetIndex++;
    }
    
    //Add the remaining elements of the sublist that is not yet finished
    while(leftIndex <= middle)
    {
      tmpArray[targetIndex] = array[leftIndex];
      leftIndex++;
      targetIndex++;
    }
    while(rightIndex <= right)
    {
      tmpArray[targetIndex] = array[rightIndex];
      rightIndex++;
      targetIndex++;
    }
    
     //The list is now merged in tmpArray. Copy the values in tmpArray to the array
    for (targetIndex = 0; targetIndex < tmpArray.length; targetIndex++)
    {
      array[left + targetIndex] = tmpArray[targetIndex];
      flush = 0;		//write to a volatile variable to flush the change to the array from the cpu's cache to main memory immediately
    }
  } 
  
}
