package dbms

import java.io._
import scala.collection.mutable

class FileStorage(filename: String){
  private val PAGE_SIZE = 4096
  private val NUM_PAGES = 10
  private val file = new RandomAccessFile(filename, "rw")
  private val tableName = "Employees"
  private val table = new Table(file)

  // Initialize the table
  initializeFile()

  private def initializeFile(): Unit = {
    // Set the file size to PAGE_SIZE * NUM_PAGES (4096 * 10 = 40960 bytes)
    file.setLength(0);
    file.setLength(PAGE_SIZE * NUM_PAGES)

    // Overwrite the entire file with zeros to clear any previous data
    val emptyPage = new Array[Byte](PAGE_SIZE)
    for (pageNumber <- 0 until NUM_PAGES) {
      file.seek(pageNumber * PAGE_SIZE)
      file.write(emptyPage)
    }

    // Initialize the table and its pages
    table.initialize()
  }
  
  def startCSVProcess(): Unit = {
    table.processCsv(file)
  }
}
