package org.mywiki.cp.examples.forkjoin;

import java.io.File;

import jsr166y.ForkJoinPool;
import jsr166y.RecursiveAction;

public class XMLProcessingForkJoin {
	 
	   class ProcessXMLFiles extends RecursiveAction {
	       static final int FILE_COUNT_THRESHOLD = 2;
	       String sourceDirPath;
	       String targetDirPath;
	       File[] xmlFiles = null;
	 
	       public ProcessXMLFiles(String sourceDirPath, String targetDirPath, File[] xmlFiles) {
	           this.sourceDirPath = sourceDirPath;
	           this.targetDirPath = targetDirPath;
	           this.xmlFiles = xmlFiles;
	       }
	 
	       public ProcessXMLFiles(String sourceDirPath, String targetDirPath, File[] xmlFiles, int from, int to) {
	       	   this(sourceDirPath, targetDirPath, xmlFiles);
	           this.from = from;
	           this.to = to;
	       }
	 
	       @Override
	       protected void compute() {
	           try {
	               // Make sure the directory has been scanned
	               if ( xmlFiles == null ) {
	                   File sourceDir = new File(sourceDirPath);
	                   if ( sourceDir.isDirectory() ) {
	                       xmlFiles = sourceDir.listFiles();
	                   }
	               }
	 
	               // Check the number of files
	               if (to -from <= FILE_COUNT_THRESHOLD ) {
	                   parseXMLFiles(xmlFiles, from, to);
	               }
	               else {
	                   // Split the array of XML files into two equal parts
	                   int center = (from+ to) / 2;
	                   //File[] part1 = (File[])splitArray(xmlFiles, 0, center);
	                   //File[] part2 = (File[])splitArray(xmlFiles, center, xmlFiles.length);
	 
	                   invokeAll(new ProcessXMLFiles(sourceDirPath, targetDirPath, xmlFiles, from, center),
	                             new ProcessXMLFiles(sourceDirPath, targetDirPath, xmlFiles, from + center, to));
	 
	               }
	           }
	           catch ( Exception e ) {
	               e.printStackTrace();
	           }
	       }
	 
	       protected void parseXMLFiles(File[] filesToParse, int from, int to) {
	       	   for (int i = from; i < to; i++) {
	           // Parse and copy the given set of XML files
	           // ...
	       	   }
	       }
	   }
	 
	   public XMLProcessingForkJoin(String source, String target) {
	       // Periodically invoke the following lines of code:
	       ProcessXMLFiles process = new ProcessXMLFiles(source, target, null);                
	       ForkJoinPool pool = new ForkJoinPool();
	       pool.invoke(process);
	   }
	 
	   // Start the XML file parsing process with the Java SE 7 Fork/Join framework
	   public static void main(String[] args) {
	       if ( args.length < 2 ) {
	           System.out.println("args - please specify source and target dirs");
	           System.exit(-1);
	       }
	       String source = args[0];
	       String target = args[1];
	 
	       XMLProcessingForkJoin forkJoinProcess = 
	               new XMLProcessingForkJoin(source, target);
	   }
	}
