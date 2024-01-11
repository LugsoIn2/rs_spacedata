package SpaceData.view

trait TUIHelpers {
  def printListInChunks[T](objList: List[T], attribute1Extractor: T => String, attribute2Extractor: T => String, chunkSize: Int, cancelKey: String): Unit = {
    var continuePrinting = true
    objList.grouped(chunkSize).foreach { chunk =>
      if (continuePrinting) {
        chunk.foreach(obj => println(s"Name: ${attribute1Extractor(obj)}, ID: ${attribute2Extractor(obj)}"))
        val userInput = scala.io.StdIn.readLine(s"Press enter for the next page or '$cancelKey' to abort: ")
        if (userInput.toLowerCase == cancelKey.toLowerCase) {
          continuePrinting = false
        }
      }
    }
  }

  def getUserInput(): String = {
    print("Input: ")
    scala.io.StdIn.readLine()
  }
}
