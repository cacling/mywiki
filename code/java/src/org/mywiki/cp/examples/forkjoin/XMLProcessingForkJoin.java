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
	               if ( xmlFiles.length <= FILE_COUNT_THRESHOLD ) {
	                   parseXMLFiles(xmlFiles);
	               }
	               else {
	                   // Split the array of XML files into two equal parts
	                   int center = xmlFiles.length / 2;
	                   File[] part1 = (File[])splitArray(xmlFiles, 0, center);
	                   File[] part2 = (File[])splitArray(xmlFiles, center, xmlFiles.length);
	 
	                   invokeAll(new ProcessXMLFiles(sourceDirPath, targetDirPath, part1 ),
	                             new ProcessXMLFiles(sourceDirPath, targetDirPath, part2 ));
	 
	               }
	           }
	           catch ( Exception e ) {
	               e.printStackTrace();
	           }
	       }
	 
	       protected Object[] splitArray(Object[] array, int start, int end) {
	           int length = end - start;
	           Object[] part = new Object[length];
	           for ( int i = start; i < end; i++ ) {
	               part[i-start] = array[i];
	           }
	           return part;
	       }
	 
	       protected void parseXMLFiles(File[] filesToParse) {
	           // Parse and copy the given set of XML files
	           // ...
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