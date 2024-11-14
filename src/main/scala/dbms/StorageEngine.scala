package dbms

trait StorageEngine {
  // an abstract method to create a table
  def createTable(tableName: String, columns: Seq[String]): Boolean
  // an abstract to insert records into a table 
  def insert(tableName: String, record: Record): Boolean
  // an abstract to delete records from a table
  def delete(tableName: String, condition: Record => Boolean): Int
  // an abstract to update a record from the table
  def update(tableName: String, updates: Map[String, String], condition: Record => Boolean): Int
  // an abstract to select records from a table
  def select(tableName: String, condition: Record => Boolean): Seq[Record]
  // an abstract method to get list of table names
  def getTableNames: Seq[String] // New method to get list of table names
}
