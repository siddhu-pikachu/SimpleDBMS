package dbms

import scala.io.StdIn

object App {
  def main(args: Array[String]): Unit = {
    println("Welcome to SimpleDBMS! Type your commands or 'exit' to quit.")

    // Initialize storage and query processor
    val storage = new InMemoryStorage()
    val processor = new QueryProcessor(storage)

    // Main loop for user input
    var continue = true
    while (continue) {
      print("dbms> ") // Prompt symbol
      val input = StdIn.readLine().trim

      // Check for exit condition
      input.toLowerCase match {
        case "exit" | "quit" =>
          continue = false
          println("Exiting SimpleDBMS. Goodbye!")

        case command =>
          // Process the input command
          println(command)
          val result = processor.executeQuery(command)
          println(result) // Display the result of the command
      }
    }
  }
}
