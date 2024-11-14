//package dbms
//
//import net.sf.jsqlparser.parser.CCJSqlParserUtil
//import net.sf.jsqlparser.statement._
//import net.sf.jsqlparser.statement.create.table.CreateTable
//import net.sf.jsqlparser.statement.insert.Insert
//import net.sf.jsqlparser.statement.delete.Delete
//import net.sf.jsqlparser.statement.update.Update
//import net.sf.jsqlparser.statement.select.Select
//import net.sf.jsqlparser.util.TablesNamesFinder
//import scala.jdk.CollectionConverters._
//import scala.util.matching.Regex
//
//class QueryProcessor(storage: StorageEngine) {
//
//  def executeQuery(sql: String): String = {
//    try {
//      val trimmedSql = sql.trim
//      if (trimmedSql.equalsIgnoreCase("SHOW TABLES;") || trimmedSql.equalsIgnoreCase("SHOW TABLES")) {
//        handleShowTables()
//      } else {
//        val statement: Statement = CCJSqlParserUtil.parse(sql)
//        statement match {
//          case create: CreateTable =>
//            handleCreateTable(create)
//          case insert: Insert =>
//            handleInsert(insert)
//          case delete: Delete =>
//            handleDelete(delete)
//          case update: Update =>
//            handleUpdate(update)
//          case select: Select =>
//            handleSelect(select)
//          case _ =>
//            "Unsupported SQL statement."
//        }
//      }
//    } catch {
//      case e: net.sf.jsqlparser.JSQLParserException =>
//        s"SQL Parsing Error: ${e.getMessage}"
//      case e: Exception =>
//        s"Error: ${e.getMessage}"
//    }
//  }
//
//  private def handleShowTables(): String = {
//    val tableNames = storage.getTableNames
//    if (tableNames.nonEmpty) {
//      "Tables:\n" + tableNames.mkString("\n")
//    } else {
//      "No tables found."
//    }
//  }
//
//  private def handleCreateTable(create: CreateTable): String = {
//    val tableName = create.getTable.getName
//    val columnDefinitions = create.getColumnDefinitions
//
//    if (columnDefinitions == null || columnDefinitions.isEmpty) {
//      return s"Error: No column definitions found for table '$tableName'. Please specify columns with data types."
//    }
//
//    val columns = columnDefinitions.asScala.map(_.getColumnName).toSeq
//    val success = storage.createTable(tableName, columns)
//    if (success)
//      s"Table '$tableName' created with columns: ${columns.mkString(", ")}"
//    else
//      s"Table '$tableName' already exists."
//  }
//
//  private def handleInsert(insert: Insert): String = {
//    val tableName = insert.getTable.getName
//
//    val columns = Option(insert.getColumns)
//      .map(_.asScala.map(_.getColumnName).toSeq)
//      .getOrElse(Seq())
//
//    val itemsList = insert.getItemsList match {
//      case exprList: net.sf.jsqlparser.expression.operators.relational.ExpressionList =>
//        exprList.getExpressions.asScala.map(_.toString.replaceAll("'", "")).toSeq
//      case multiExprList: net.sf.jsqlparser.expression.operators.relational.MultiExpressionList =>
//        // Handle multi-expression lists if necessary
//        Seq()
//      case _ => Seq()
//    }
//
//    val data = columns.zip(itemsList).toMap
//    val record = Record(data)
//    val success = storage.insert(tableName, record)
//    if (success)
//      s"Record inserted into '$tableName'."
//    else
//      s"Failed to insert record into '$tableName'."
//  }
//
//  private def handleDelete(delete: Delete): String = {
//    val tableName = delete.getTable.getName
//    val condition = getCondition(delete.getWhere)
//    val deletedCount = storage.delete(tableName, condition)
//    s"Deleted $deletedCount records from '$tableName'."
//  }
//
//  private def handleUpdate(update: Update): String = {
//    val tableName = update.getTable.getName
//
//    val updateSets = update.getUpdateSets.asScala
//    val updates = updateSets.flatMap { updateSet =>
//      val columns = updateSet.getColumns.asScala.map(_.getColumnName)
//      val expressions = updateSet.getExpressions.asScala.map(_.toString.replaceAll("'", ""))
//      columns.zip(expressions)
//    }.toMap
//
//    val condition = getCondition(update.getWhere)
//    val updatedCount = storage.update(tableName, updates, condition)
//    s"Updated $updatedCount records in '$tableName'."
//  }
//
//  private def handleSelect(select: Select): String = {
//    val tablesNamesFinder = new TablesNamesFinder()
//    val tableList = tablesNamesFinder.getTableList(select).asScala
//    val plainSelect = select.getSelectBody.asInstanceOf[net.sf.jsqlparser.statement.select.PlainSelect]
//    val condition = getCondition(plainSelect.getWhere)
//    val selectedColumns = plainSelect.getSelectItems.asScala.map(_.toString)
//
//    val selectedRecords = storage.select(tableList.head, condition)
//    if (selectedRecords.nonEmpty) {
//      val output = selectedRecords.map { record =>
//        val dataToDisplay = if (selectedColumns.contains("*")) {
//          record.data
//        } else {
//          record.data.filterKeys(selectedColumns.contains)
//        }
//        dataToDisplay.map { case (k, v) => s"$k: $v" }.mkString(", ")
//      }.mkString("\n")
//      output
//    } else {
//      s"No records found in '${tableList.head}'."
//    }
//  }
//
//  // Helper method to get condition from where expression
//  private def getCondition(whereExpr: net.sf.jsqlparser.expression.Expression): Record => Boolean = {
//    if (whereExpr != null) {
//      record => evaluateCondition(record, whereExpr.toString)
//    } else {
//      _ => true
//    }
//  }
//
//  // Simple condition evaluator (Note: This is a very basic implementation)
//  private def evaluateCondition(record: Record, condition: String): Boolean = {
//    // This is a placeholder for condition evaluation logic.
//    // For demonstration purposes, we'll handle simple equality checks.
//    val conditionPattern: Regex = "(\\w+)\\s*=\\s*'?([^']+)'?".r
//    condition match {
//      case conditionPattern(column, value) =>
//        record.data.get(column).contains(value)
//      case _ =>
//        false
//    }
//  }
//}
