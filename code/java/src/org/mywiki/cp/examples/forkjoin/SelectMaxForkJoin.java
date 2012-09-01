package org.mywiki.cp.examples.forkjoin;

import jsr166y.ForkJoinPool;  
import jsr166y.ForkJoinTask;  
import jsr166y.RecursiveAction;  
  
public class SelectMaxForkJoin extends RecursiveAction {  
  private final int threshold;  
  private final SelectMaxProblem problem;  
  public int result;  
  
  public SelectMaxForkJoin(SelectMaxProblem problem, int threshold) {  
    this.problem = problem;  
    this.threshold = threshold;  
  }  
  
  @Override  
  protected void compute() {  
    if (problem.getSize() < threshold)  
      result = problem.solveSequentially();  
    else {  
      int midpoint = problem.getSize() / 2;  
      SelectMaxForkJoin left = new SelectMaxForkJoin(problem.subproblem(0, midpoint + 1), threshold);  
      SelectMaxForkJoin right = new SelectMaxForkJoin(problem.subproblem(midpoint + 1,  
                                                         problem.getSize()),  
                                      threshold);  
      invokeAll(new ForkJoinTask[]{left, right});  
      result = Math.max(left.result, right.result);  
    }  
  }  
  
  public static void main(String[] args) {  
    int[] data = {1, 200, 7, 800, 90, 19};  
    SelectMaxProblem problem = new SelectMaxProblem(data, 0, data.length);  
    int threshold = 3;  
    int nThreads = 4;  
    SelectMaxForkJoin mfj = new SelectMaxForkJoin(problem, threshold);  
    ForkJoinPool pool = new ForkJoinPool(nThreads);  
    pool.invoke(mfj);  
    int result = mfj.result;  
    System.out.println(result);  
  }  
  
  private static final long serialVersionUID = 1L;  
}  