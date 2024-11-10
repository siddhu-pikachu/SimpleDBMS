package dbms

// trait to create the basic operations for the storage mechanism
trait StorageEngine {
  def insert (key: String, value: String): Boolean
  def retrieve (key: String): Option[String]
}

// simple in-memory-storage
class InMemoryStorage extends StorageEngine {
  private val data = scala.collection.mutable.Map[String, String]()

  // implement insert
  override def insert(key: String, value: String): Boolean = {
    if (data.contains(key)){
      false // key already exists so insertion fails 
    }
    else {
      data(key) = value // insertion successful
      true
    }
  }
  
  // implement retreive
   override def retrieve (key: String): Option[String] = {
      data.get(key) // retreives the value
  }
}