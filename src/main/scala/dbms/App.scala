package dbms

import scala.io.StdIn
import scala.io.Source

object App {
  def main(args: Array[String]): Unit = {
    println("Welcome to SimpleDBMS! Type your commands or 'exit' to quit.")

    val storage = new FileStorage("employee.tbl")
    storage.startCSVProcess()
  }
}
