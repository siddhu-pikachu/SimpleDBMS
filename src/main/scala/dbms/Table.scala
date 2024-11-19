package dbms

import java.io.BufferedReader
import java.io.FileReader
import java.io.RandomAccessFile
import scala.collection.mutable

class Table(file: RandomAccessFile, name: String, nP: Int, columns: String) {
  private val tableName = name
  private var numPages = nP
  private val pages: Array[Page] = Array.tabulate(numPages)(i => new Page(file, i))
  private var nextRowId = 0
  
  // Initialize the table by initializing all pages
  def initialize(): Unit = {
    pages.foreach(_.initialize())
  }

  // Hash function to determine the page number
  private def hashFunction_2(ssn: String): Int = {
    val lastFourDigits = ssn.takeRight(4)
    val key = lastFourDigits.toIntOption.getOrElse(0)
    key % numPages
  }

  private def hashFunction(ssn: String): Int = {
    val firstThreeDigits = ssn.take(3).toInt
    (firstThreeDigits/100) % numPages
  }
  
  def processCsv (csvFile : RandomAccessFile): Unit = {
    try{
      var br = new BufferedReader(new FileReader("/Users/rapetisiddhu/DBMS/employee.csv"))
      var line : String = br.readLine()
      // if columns = "" process the header to get the table metadata.
      line = br.readLine()
      while(line!=null){
        var rId = assignRowId()
        var empRecord : Record = parseCSVLine(line, rId)
        val pageNumber = hashFunction(empRecord.getSsn())

        if(!pages(pageNumber).addRecord(empRecord)){
          println(s"Not enough space on page $pageNumber.")
        }

        line = br.readLine()
      }
      csvFile.close()
    }
    catch
      case e: Exception => println(s"Error: Exception $e.")
  }

  // Converts to Char Option if the string has exactly one character
  private def toChar(s: String): Char = if (s.length == 1) s.head else '\u0000'

  // Converts to Short Option using toIntOption and checks range
  def toShort(s: String): Short = { var ss = s.toIntOption.getOrElse(-1)
    if (ss >= Short.MinValue && ss <= Short.MaxValue) ss.toShort else 0:Short
  }

  private def removeDash(s: String): String = {
    val ss = s.substring(0,3) + s.substring(4,6) + s.substring(7,11)
    ss
  }
  private def parseCSVLine(line: String, rId: Int): Record = {
    var attributes : Array[String] = line.split(',')
    //attributes(0).toIntOption.getOrElse(-1)
    val lineRecord = new Record(rId, attributes(0), toChar(attributes(1)), attributes(2), removeDash(attributes(3)), attributes(4), attributes(5), toChar(attributes(6)), attributes(7).toIntOption.getOrElse(-1), toShort(attributes(8)))
    lineRecord
  }

  def assignRowId(): Int = {
    nextRowId+=1
    nextRowId
  }
}
