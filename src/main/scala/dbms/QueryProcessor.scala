package dbms

class QueryProcessor(storage: StorageEngine) {

  // Regex patterns for SQL-like commands
  private val insertPattern = """(?i)insert into \w+ *\(\s*(\w+),\s*(\w+)\s*\)\s*values\s*\(\s*'([^']+?)'\s*,\s*'([^']+?)'\s*\);""".r
  private val selectPattern = """(?i)select (\w+) from \w+;""".r
  private val updatePattern = """(?i)update \w+ set \w+ = '(.+)' where \w+ = '(.+)';""".r

  def executeQuery(query: String): String = {
    // Print the query to debug if it reaches here
    println(s"Executing query: $query")

    query.trim.toLowerCase match {

      // Handle INSERT command
      case insertPattern(_, key, value) =>
        println(s"Insert detected: key = $key, value = $value") // Debug line
        if (storage.insert(key, value)) {
          s"Inserted ($key, $value)"
        } else {
          s"Error: Key $key already exists"
        }

      // Handle SELECT command
      case selectPattern(key) =>
        println(s"Select detected: key = $key") // Debug line
        storage.retrieve(key) match {
          case Some(value) => s"Value: $value"
          case None => s"Key $key not found"
        }

      // Handle UPDATE command
      case updatePattern(newValue, key) =>
        println(s"Update detected: key = $key, new value = $newValue") // Debug line
        storage.retrieve(key) match {
          case Some(_) =>
            storage.insert(key, newValue)
            s"Updated ($key, $newValue)"
          case None =>
            s"Error: Key $key does not exist"
        }

      // Handle unknown or unsupported commands
      case _ =>
        println("Unknown command or syntax error") // Debug line
        "Syntax error or unknown command"
    }
  }
}
