package dbms

import java.io._
import scala.collection.mutable

class FileStorage(filename: String, columns: Option[String]){
  private val PAGE_SIZE = 4096
  private val NUM_PAGES = 1
  private val file : mutable.Map[Table, java.io.RandomAccessFile] = mutable.Map()
  private val table : mutable.Map[String, Table] = mutable.Map()

  // Initialize the table
  initializeFile()

  private def initializeFile(): Unit = {
    // create the table and its file
    val tableName = filename.split('.')(0)
    val tempFile = new RandomAccessFile(filename, "rw")
    table += (filename -> Table(tempFile, tableName, NUM_PAGES, columns.getOrElse("")))
    file += (table(filename) -> tempFile)
    // Set the file size to PAGE_SIZE * NUM_PAGES (4096 * 10 = 40960 bytes)
    file(table(filename)).setLength(0);
    file(table(filename)).setLength(PAGE_SIZE * NUM_PAGES)

    // Overwrite the entire file with zeros to clear any previous data
    val emptyPage = new Array[Byte](PAGE_SIZE)
    for (pageNumber <- 0 until NUM_PAGES) {
      file(table(filename)).seek(pageNumber * PAGE_SIZE)
      file(table(filename)).write(emptyPage)
    }

    // Initialize the table and its pages
      table(filename).initialize()
  }

  def startCSVProcess(): Unit = {
    table(filename).processCsv(file(table(filename)))
  }
}
