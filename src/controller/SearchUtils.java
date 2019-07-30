package controller;/*
  Author: Hisham Maged
  Date : 7/28/2019
  Project Name : 
*/

import java.util.Arrays;
import java.util.List;
import model.pojo.AirportEntry;

/**
 * <h1>SearchUtils</h1>
 * Made to facilitate Search using different Searching algorithms implemented from scratch for more efficiency for the project
 */
public class SearchUtils {

  public static int binarySearch(int target, int[] numbers)
  {
    int low=0,high=numbers.length-1;
    while(low <= high)
    {
      int mid=low+((high-low)/2);
      if(target < numbers[mid])
        high = mid - 1;
      else if(target > numbers[mid])
        low = mid + 1;
      else
        return mid;
    }
    return -1;
  }

  // ay hbl eb2a 3'yrha
  //linear search
  public static int findName(List<AirportEntry> data,String targetName)
  {
    for(AirportEntry entry : data)
    {
      if(entry.getName().equalsIgnoreCase(targetName))
        return entry.getID();
    }
    return -1;
  }

  public static void selectionSort(int[] arr)
  {
    for(int i = 0 , n = arr.length-1, minIdx = i ; i < n ; ++i, minIdx = i )
    {
      for(int j = i+1 ; j < arr.length ; ++j)
      {
        if(arr[j] < arr[minIdx])
          minIdx = j;
      }
      if( i != minIdx)
        swap(arr,i,minIdx);
    }
  }

  private static void swap(int[] arr, int i, int j)
  {
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }

  public static void main(String[] args) {
//    List<AirportEntry> data = DataParserBuilder.buildCSVParser().filePath("./data/airports.csv").airports().parse().getParsedData();
//    System.out.println(SearchUtils.findName(data,"Jacksonville International Airport"));
//    System.out.println(binarySearch(5,new int[]{1,2,3,4,5}));
    int[] arr = {5,4,7,8,90,2,1,9,10};
    for (int e : arr)
      System.out.print(e+", ");
    selectionSort(arr);
      System.out.println();
    for (int e : arr)
      System.out.print(e+", ");


  }
}
