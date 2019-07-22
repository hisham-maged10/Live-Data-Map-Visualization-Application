package model.pojo;/*
  Author: Hisham Maged
  Date : 7/21/2019
  Class Name : An interface that defines the used interface of the parsers in the whole project
*/

import java.io.InputStream;

public interface DataParser<T extends DataEntry & Comparable<? super T>> {

  public boolean parse(); // returns true if parsed successfully, false otherwise
  public Iterable<T> getParsedData(); // returns the record in any form of iterable
  public InputStream getSource(); // returns the source of input as an inputSource
}
