package dbms

import dbms.Record

import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import scala.collection.mutable

class Page(file: RandomAccessFile, pageNumber: Int) {
  private val PAGE_SIZE = 4096
  private val RECORD_SIZE = 112
  private val HEADER_SIZE = 2 // Record count (short integer)
  private val MAX_RECORDS = (PAGE_SIZE - HEADER_SIZE) / (RECORD_SIZE + 2) // 2 bytes per offset
  // Class member variables
  private var freeSpaceOffset: Int = PAGE_SIZE
  private val pageNo = pageNumber
  private val fileStream = file
  private var recordCount: Short = 0
  private val ssnToOffsetMap = mutable.Map[String, Short]()

  // Initialize the page (if needed)
  def initialize(): Unit = {
    // Clear the page content
    file.seek(pageNo * PAGE_SIZE)
    recordCount = 0
    // Initialize header
    file.writeShort(recordCount) // Record count
    freeSpaceOffset = PAGE_SIZE
    ssnToOffsetMap.clear()
  }

  // Add a record to the page
  def addRecord(record: Record): Boolean = {
    // Space calculation
    if (freeSpaceOffset - RECORD_SIZE - HEADER_SIZE - (recordCount + 1) * 2 < 0) {
      println(s"Not enough space on page $pageNumber.")
      return false
    }
    // Decrement freeSpaceOffset
    freeSpaceOffset -= RECORD_SIZE
    ssnToOffsetMap += (record.getSsn() -> freeSpaceOffset.toShort)

    file.seek(pageNo * PAGE_SIZE + freeSpaceOffset)
    val recordData = record.serialize()
    file.write(recordData)
    recordCount = (recordCount + 1).toShort

    updateHeader()
  }

  def updateHeader(): Boolean = {
    // Update the header
    file.seek(pageNo * PAGE_SIZE)
    file.writeShort(recordCount)

    // Update the offsets
    val sortedOffsets = ssnToOffsetMap.toSeq.sortBy(_._1)

    // Write the sorted offsets
    var offsetPosition = pageNo * PAGE_SIZE + HEADER_SIZE
    for ((_, offset) <- sortedOffsets) {
      file.seek(offsetPosition)
      file.writeShort(offset)
      offsetPosition += 2
    }

    true
  }
}
