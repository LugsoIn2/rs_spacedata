package SpaceData.view

trait TUIHelpers {
  def printListInChunks[T, A](
    objList: List[T],
    attribute1Extractor: T => A,
    attribute2Extractor: T => A,
    optionalAttribute1Extractor: Option[T => String] = None,
    optionalAttribute2Extractor: Option[T => String] = None,
    chunkSize: Int,
    cancelKey: String,
    attributeName1: String = "Name",
    attributeName2: String = "ID",
    optionalAttributeName1: String = "Optional1",
    optionalAttributeName2: String = "Optional2"
  ): Unit = {
    var continuePrinting = true
    objList.grouped(chunkSize).foreach { chunk =>
      if (continuePrinting) {
        chunk.foreach { obj =>
          val attribute1: A = attribute1Extractor(obj)
          val attribute2: A = attribute2Extractor(obj)
          val optionalAttribute1: String = optionalAttribute1Extractor.map(extractor => extractor(obj)).getOrElse("")
          val optionalAttribute2: String = optionalAttribute2Extractor.map(extractor => extractor(obj)).getOrElse("")

          val firstTwoAttributes = List(
            s"$attributeName1: $attribute1",
            s"$attributeName2: $attribute2"
          )

          val optionalAttributes = List(
            if (optionalAttribute1.nonEmpty) s"$optionalAttributeName1: $optionalAttribute1" else "",
            if (optionalAttribute2.nonEmpty) s"$optionalAttributeName2: $optionalAttribute2" else ""
          ).filter(_.nonEmpty)

          println(firstTwoAttributes.mkString(", "))
          println(optionalAttributes.mkString(", "))
          if (optionalAttributes.nonEmpty){
            println()
          }
        }

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
