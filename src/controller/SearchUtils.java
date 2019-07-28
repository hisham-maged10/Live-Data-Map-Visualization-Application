package controller;/*
  Author: Hisham Maged
  Date : 7/28/2019
  Project Name : 
*/

import java.util.List;
import model.pojo.AirportEntry;

/**
 * <h1>SearchUtils</h1>
 * Made to facilitate Search using different Searching algorithms implemented from scratch for more efficiency for the project
 */
public class SearchUtils {

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

  public static void main(String[] args) {
    List<AirportEntry> data = DataParserBuilder.buildCSVParser().filePath("./data/airports.csv").airports().parse().getParsedData();
    System.out.println(SearchUtils.findName(data,"Jacksonville International Airport"));
  }
}
