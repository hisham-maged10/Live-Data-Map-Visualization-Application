package model.parser;/*
  Author: Hisham Maged
  Date : 7/21/2019
  Class Name : An interface that defines the used interface of the parsers in the whole project
*/

import java.io.InputStream;
import java.util.List;
import model.pojo.DataEntry;

/**
 * An Interface that defines behaviour of <b>Data Parsing</b> for different formats
 * @author Hisham Maged
 * @version 1.1
 * @since 21/7/2019
 * @see AbstractDataParser
 * @see DataEntry
 * @param <T> A type Parameter that implements DataEntry Interface and is Comparable (implements Comparable interface)
 */
public interface DataParser<T extends DataEntry & Comparable<? super T>> {

  /**
   * Defines behaviour of parsing for different format types.
   * @return A DataParser type that is a sub-class of DataParser interface.
   */
  public DataParser<T> parse(); // returns true if parsed successfully, false otherwise

  /**
   * Gets the Parsed Data in an UnModefiable container
   * @return List that holds the parsed data to be easily iterated on.
   */
  public List<T> getParsedData(); // returns the record in any form of iterable

  /**
   * Gets the Source of Data.
   * @return The source of input as an InputStream.
   */
  public InputStream getSource(); // returns the source of input as an inputSource
}
