package dbms

import scala.io.StdIn
import scala.io.Source

object App {
  def main(args: Array[String]): Unit = {
    println("Welcome to SimpleDBMS! Type your commands or 'exit' to quit.")

    var continue = true
    while (continue) {
      print("dbms> ") // Prompt symbol
      val args = StdIn.readLine().trim
      val input = args.split(" ")

      input(0).toLowerCase match {
        case "exit" | "quit" => {
          // condition to exit the program
          continue = false
          println("Exiting SimpleDBMS. Goodbye!")
      }
        case ".file" => {
          //expected input format: ".file FILENAME"
          val storage = new FileStorage(input(1), None)
          storage.startCSVProcess()
        }
        case "create" => {
          //expected input format: "create table/index TABLENAME( COLUMNS )
          input(1).toLowerCase match {
            case "table" => {
              try{
                val storage = new FileStorage(input(2), None)
                storage.startCSVProcess()
              }
              //catch Exception as e....
                
            }
            case "index" => {

            }
            case _ => {
              println("wrong syntax!")
            }
          }
        }
        case "show" => {
          //pull from metadata
        }
        case "insert" => {

        }
        case "delete" => {

        }
        case "select" => {

        }
        case "update" => {

        }
        case _ => {
          println("wrong syntax!")
        }
      }
    }
  }
}
