package eu.brosbit.ope.lib

object PolishSorter {
  val order: Map[Char, Char] = createMapForSort()

  // val names = List("Małysz", "Łęcki", "Mamoń", "Pawłowski", "Markowski", "Mażewski", "Maździoch")
  // using: names.sortWith(PolishSorter.polishSort).foreach(println)
  def polishSort(n1: String, n2: String): Boolean =
    convertStringForSort(n1) < convertStringForSort(n2)

  private def createMapForSort() = {
    val alphabet = "aąbcćdeęfghijklłmnńoópqrsśtuvxwzżź"
    (0 until alphabet.length()).map(i =>
      (alphabet(i) -> (i + 65).toChar)
    ).toMap
  }

  private def convertStringForSort(n: String) =
    n.toLowerCase().map(c => if (order.contains(c)) order(c) else 'z')

}