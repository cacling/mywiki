package org.mywiki.cp.forkjoin;

import java.util.Arrays;  

import jsr166y.ForkJoinPool;  
import jsr166y.RecursiveAction;  
  
public class MergeSort extends RecursiveAction {  
  final int[] numbers;  
  final int startPos, endPos;  
  final int[] result;  
  
  public MergeSort(int[] numbers, int startPos, int endPos) {  
    this.numbers = numbers;  
    this.startPos = startPos;  
    this.endPos = endPos;  
    this.result = new int[size()];  
  }  
  
  private void merge(MergeSort left, MergeSort right) {  
    int i = 0, leftPos = 0, rightPos = 0, leftSize = left.size(), rightSize = right.size();  
    while (leftPos < leftSize && rightPos < rightSize)  
      result[i++] = (left.result[leftPos] <= right.result[rightPos])  
          ? left.result[leftPos++]  
          : right.result[rightPos++];  
    while (leftPos < leftSize)  
      result[i++] = left.result[leftPos++];  
    while (rightPos < rightSize)  
      result[i++] = right.result[rightPos++];  
  }  
  
  public int size() {  
    return endPos - startPos;  
  }  
  
  @Override  
  protected void compute() {  
    if (size() < 3) { // threshold  
      System.arraycopy(numbers, startPos, result, 0, size());  
      Arrays.sort(result, 0, size());  
    } else {  
      int midpoint = size() / 2;  
      MergeSort left = new MergeSort(numbers, startPos, startPos + midpoint);  
      MergeSort right = new MergeSort(numbers, startPos + midpoint, endPos);  
      invokeAll(left, right);  
      merge(left, right);  
    }  
  }  
  
  public static void main(String[] args) {  
    int[] numbers = {12, 23, 100, 1, 2, 9};  
    int nThreads = 4;  
    MergeSort mfj = new MergeSort(numbers, 0, numbers.length);  
    ForkJoinPool pool = new ForkJoinPool(nThreads);  
    pool.invoke(mfj);  
    printArray(mfj.result);  
  }  
  
  private static void printArray(int[] arr) {  
    for (int n : arr)  
      System.out.printf("%d ", n);  
    System.out.println();  
  }  
  
  private static final long serialVersionUID = 1L;  
}  