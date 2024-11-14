package dbms

import scala.collection.mutable

class Record (
   private var rowId: Int,
            val firstName: String,
            val middleInitial: Char,
            val lastName: String,
            val ssn: String,
            val birthDate: String,
            val address: String,
            val sex: Char,
            val salary: Int,
            val departmentNumber: Short
){
  private var deletionMarker : Char  = '\u0000'
  def setRowId(id: Int): Unit = {
    this.rowId = id
  }
  def setDeletionMarker(d: Char): Unit = {
    this.deletionMarker = '1'
  }
  def getSsn(): String = {
    this.ssn
  }
  def serialize() : Array[Byte] = {
    //var buffer : String = ""
    var buffer = new Array[Byte](112)

    def fixedLengthString(s: String, l: Int): String = {
      if (s.length() >= l) s.substring(0, l)
      else s+("\u0000"*(l-s.length()))
    }

    val byteBuffer = java.nio.ByteBuffer.wrap(buffer)
    byteBuffer.order(java.nio.ByteOrder.BIG_ENDIAN)

    // Serialize the rowId (4 bytes) and name (20 bytes)
    byteBuffer.putInt(rowId) // 4 bytes for rowId
    byteBuffer.put(fixedLengthString(ssn, 9).getBytes("US-ASCII")) // 20 bytes for name
    byteBuffer.put(fixedLengthString(firstName, 20).getBytes()) // 20 bytes for name
    byteBuffer.put(fixedLengthString(middleInitial.toString,1).getBytes()) // 20 bytes for name
    byteBuffer.put(fixedLengthString(lastName, 20).getBytes()) // 20 bytes for name
    byteBuffer.put(fixedLengthString(birthDate, 10).getBytes()) // 20 bytes for name
    byteBuffer.put(fixedLengthString(address, 40).getBytes()) // 20 bytes for name
    byteBuffer.put(fixedLengthString(sex.toString,1).getBytes()) // 20 bytes for name
    byteBuffer.putInt(salary) // 4 bytes for rowId
    byteBuffer.putShort(departmentNumber) // 4 bytes for rowId
    byteBuffer.put(fixedLengthString(deletionMarker.toString,1).getBytes())

    buffer
  }
}

