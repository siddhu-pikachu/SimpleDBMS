//package dbms
//
//import scala.collection.mutable
//
//// implementation of in memory storage
//class InMemoryStorage extends StorageEngine {
//  // Map to hold tables by their names
//  private val tables = mutable.Map[String, Table]()
//
//  override def createTable(tableName: String, columns: Seq[String]): Boolean = {
//    if (tables.contains(tableName)) {
//      false // Table already exists
//    } else {
//      tables(tableName) = new Table(tableName, columns)
//      true
//    }
//  }
//
//  override def insert(tableName: String, record: Record): Boolean = {
//    tables.get(tableName) match {
//      case Some(table) =>
//        table.insert(record)
//        true
//      case None =>
//        false // Table does not exist
//    }
//  }
//
//  // returns number of records deleted
//  override def delete(tableName: String, condition: Record => Boolean): Int = {
//    tables.get(tableName) match {
//      case Some(table) =>
//        table.delete(condition)
//      case None =>
//        0 // Table does not exist
//    }
//  }
//
//  // returns number of records updated
//  override def update(tableName: String, updates: Map[String, String], condition: Record => Boolean): Int = {
//    tables.get(tableName) match {
//      case Some(table) =>
//        table.update(updates, condition)
//      case None =>
//        0 // Table does not exist
//    }
//  }
//
//  // returns the sequence of records satisfying the condition
//  override def select(tableName: String, condition: Record => Boolean): Seq[Record] = {
//    tables.get(tableName) match {
//      case Some(table) =>
//        table.select(condition)
//      case None =>
//        Seq() // Table does not exist, return empty sequence
//    }
//  }
//
//  // New method to get list of table names
//  override def getTableNames: Seq[String] = tables.keys.toSeq
//}
