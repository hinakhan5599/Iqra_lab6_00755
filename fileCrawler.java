
import java.util.*;
import java.io.*;
 
public class fileCrawler {
 
  private Queue workQ;
  static int i = 0;
  

  private String fileNameToSearch;
  private List<String> result = new ArrayList<String>();
 
  public String getFileNameToSearch() {
	return fileNameToSearch;
  }
 
  public void setFileNameToSearch(String fileNameToSearch) {
	this.fileNameToSearch = fileNameToSearch;
  }
 
  public List<String> getResult() {
	return result;
  }
 
 
 private class Worker implements Runnable {
 
  private Queue queue;
 
  public Worker(Queue q) {
   queue = q;
  }
 
//  since main thread has placed all directories into the workQ, we
//  know that all of them are legal directories; therefore, do not need
//  to try ... catch in the while loop below
 
  public void run() {
   String name;
   while ((name = queue.remove()) != null) {
    File file = new File(name);
    String entries[] = file.list();
    if (entries == null)
     continue;
    for (String entry : entries) {
     if (entry.compareTo(".") == 0)
      continue;
     if (entry.compareTo("..") == 0)
      continue;
     String fn = name + "\\" + entry;
     System.out.println(fn);
    }
   }
  }
 }
 
 public fileCrawler() {
  workQ = new Queue();
 }
 
 public Worker createWorker() {
  return new Worker(workQ);
 }
 
 
// need try ... catch below in case the directory is not legal
 
 public void processDirectory(String dir) {
   try{
   File filename = new File(dir);
   if (filename.isDirectory()) {
    String entries[] = filename.list();
    if (entries != null)
     workQ.add(dir);
 
    for (String entry : entries) {
     String subdir;
     if (entry.compareTo(".") == 0)
      continue;
     if (entry.compareTo("..") == 0)
      continue;
     if (dir.endsWith("\\"))
      subdir = dir+entry;
     else
      subdir = dir+"\\"+entry;
     processDirectory(subdir);
    }
    fileCrawler filecrawler = new fileCrawler();
    
    filecrawler.searchDirectory(new File(dir), "iqra.txt");
    
	int count = filecrawler.getResult().size();
	if(count ==0){
	    System.out.println("\nNo result found!");
	}else{
	    System.out.println("\nFound " + count + " result!\n");
	    for (String matched : filecrawler.getResult()){
		System.out.println("Found : " + matched);
   }
	}
   }
   }
 
	catch(Exception e){}
   }
 
   public void searchDirectory(File directory, String fileNameToSearch) {
	   
		setFileNameToSearch(fileNameToSearch);
	 
		if (directory.isDirectory()) {
		    search(directory);
		} else {
		    System.out.println(directory.getAbsoluteFile() + " is not a directory!");
		}
	 
	  }
	 
	  private void search(File file) {
	 
		if (file.isDirectory()) {
		  System.out.println("Searching directory ... " + file.getAbsoluteFile());
	 
	            //do you have permission to read this directory?	
		    if (file.canRead()) {
		    	try {
			for (File temp : file.listFiles()) {
			    if (temp.isDirectory()) {
				search(temp);
			    } else {
				if (getFileNameToSearch().equals(temp.getName().toLowerCase())) {			
				    result.add(temp.getAbsoluteFile().toString());
			    }
	 
			}
		    }
		    	}
		    	catch (NullPointerException npe) {
		    	    
		    	}
	 
		 } else {
			System.out.println(file.getAbsoluteFile() + "Permission Denied");
		 }
	      }
	 
	  }
	 
	
 public static void main(String Args[]) {
 
  fileCrawler fc = new fileCrawler();
 
//  now start all of the worker threads
 
  int N = 5;
  ArrayList<Thread> thread = new ArrayList<Thread>(N);
  for (int i = 0; i < N; i++) {
   Thread t = new Thread(fc.createWorker());
   thread.add(t);
   t.start();
  }
 
//  now place each directory into the workQ
  //fc.processDirectory("C:/");
  fc.processDirectory("C:/Users/Dell/Documents");
  fc.processDirectory("C:/Users/Dell/Downloads");
  fc.processDirectory("C:/Users/Dell/Desktop");
  fc.processDirectory("D:/");
//  indicate that there are no more directories to add
 
  fc.workQ.finish();
 
  for (int i = 0; i < N; i++){
   try {
    thread.get(i).join();
   } catch (Exception e) {};
  }
 }
}



