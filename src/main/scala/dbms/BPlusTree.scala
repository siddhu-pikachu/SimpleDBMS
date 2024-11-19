package dbms

sealed triat Node {
  def isLeaf: Boolean
  var pageNumber: Int
}

class LeafNode(var pageNum: Int) extends Node {
  val isLeaf = true
  var nextLeafPageNum: Option[Int] = None  // Link to next leaf's page number
  var page: Page = new Page(file, pageNum)  // Reference to actual page storing records
}

class InternalNode(var pageNum: Int) extends Node {
  val isLeaf = false
  var keys = new Array[String](4) // SSNs as dividing points
  var children = new Array[Node](5) // References to child nodes
  var childrenOffsets = new Array[Long](5) // File offsets for children
  var numKeys = 0
}

class BPlusTree(file: RandomAccessFile, pageSize: Int = 512) {
  private var numPages = 1
  private val pageDirectory = scala.collection.mutable.Map[Int, List[Int]]()  // initialPage -> chain of split pages

  // Initialize directory
  initializeDirectory()

  private def initializeDirectory(): Unit = {
    // Start with each initial page mapping to itself
    for(i <- 0 until 10) { // 0-9 for first digit of SSN
      pageDirectory += (i -> List(i))
    }
  }

  def insert(record: Record, initialPage: Int): Boolean = {
    try {
      // Get chain of pages for this initial page number
      val pageChain = pageDirectory.getOrElse(initialPage, List(initialPage))

      // Find correct page in chain based on SSN ordering
      val targetPageNum = findTargetPage(pageChain, record.getSsn())
      val page = new Page(file, targetPageNum)

      if (!page.isFull()) {
        page.addRecord(record)
        true
      } else {
        handlePageSplit(page, record, initialPage)
        true
      }
    } catch {
      case e: Exception =>
        println(s"Error inserting record: ${e.getMessage}")
        false
    }
  }

  private def handlePageSplit(fullPage: Page, newRecord: Record, initialPage: Int): Unit = {
    // Create new page
    val newPageNum = allocateNewPage()
    val newPage = new Page(file, newPageNum)
    newPage.initialize()

    // Combine all records and sort
    val allRecords = fullPage.getAllRecords() :+ newRecord
    val sortedRecords = allRecords.sortBy(_.getSsn())
    val midPoint = sortedRecords.length / 2

    // Redistribute records
    fullPage.clear()
    sortedRecords.take(midPoint).foreach(fullPage.addRecord)
    sortedRecords.drop(midPoint).foreach(newPage.addRecord)

    // Update page chain in directory
    val currentChain = pageDirectory(initialPage)
    pageDirectory(initialPage) = currentChain :+ newPageNum

    // Update page links
    newPage.setNextPageNum(fullPage.getNextPageNum())
    fullPage.setNextPageNum(Some(newPageNum))
  }

  private def findTargetPage(pageChain: List[Int], ssn: String): Int = {
    // Find page in chain where SSN belongs based on ordering
    pageChain.find(pageNum => {
      val page = new Page(file, pageNum)
      val pageRecords = page.getAllRecords()
      if (pageRecords.isEmpty) true
      else {
        val minSsn = pageRecords.map(_.getSsn()).min
        val maxSsn = pageRecords.map(_.getSsn()).max
        ssn >= minSsn && ssn <= maxSsn
      }
    }).getOrElse(pageChain.head)
  }

  def search(ssn: String, initialPage: Int): Option[Record] = {
    val pageChain = pageDirectory.getOrElse(initialPage, List(initialPage))
    val targetPageNum = findTargetPage(pageChain, ssn)
    val page = new Page(file, targetPageNum)
    page.findRecord(ssn)
  }

  def rangeSearch(startSsn: String, endSsn: String, initialPage: Int): List[Record] = {
    var results = List[Record]()

    // Start with the page where startSsn would be
    val pageChain = pageDirectory.getOrElse(initialPage, List(initialPage))
    var currentPageNum = findTargetPage(pageChain, startSsn)

    var continueSearch = true
    while (continueSearch) {
      val currentPage = new Page(file, currentPageNum)
      val pageRecords = currentPage.getAllRecords()

      // Add records within range
      results = results ++ pageRecords.filter(r =>
        r.getSsn() >= startSsn && r.getSsn() <= endSsn
      )

      // Move to next page if needed
      currentPage.getNextPageNum() match {
        case Some(nextNum) =>
          val nextPage = new Page(file, nextNum)
          val nextRecords = nextPage.getAllRecords()
          if (nextRecords.isEmpty || nextRecords.head.getSsn() > endSsn) {
            continueSearch = false
          } else {
            currentPageNum = nextNum
          }
        case None =>
          continueSearch = false
      }
    }

    results.sortBy(_.getSsn())
  }

  private def allocateNewPage(): Int = {
    val newPageNum = numPages
    numPages += 1
    file.setLength(pageSize * numPages)
    newPageNum
  }
}