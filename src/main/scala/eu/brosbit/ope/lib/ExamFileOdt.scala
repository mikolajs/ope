package eu.brosbit.ope.lib
///https://www.jopendocument.org/docs/index.html
import org.jopendocument.dom.text.TextDocument
import org.jopendocument.dom.text.Paragraph

import java.io.{ByteArrayInputStream, File}
import eu.brosbit.ope.model.{Exam,  QuizQuestion}
import net.liftweb.common.{Box, Full}
import net.liftweb.http.{LiftResponse, NotFoundResponse, StreamingResponse}
import scala.util.Random
import net.liftweb.json.JsonDSL._

object ExamFileOdt {
  val path = "/home/ope"
  val rand = new Random()
  def createExamFileOdt(id:String):String = {
    val aFile = s"$path/$id.odt"
    val outFile = new File(aFile)
    if(!outFile.exists) outFile.createNewFile()
    val doc = TextDocument.createEmpty("")
    val exam = Exam.find(id).getOrElse(Exam.create)
    //println(s"""$id: ${exam.quizzes.head.map(qe => qe.q.toString).mkString(",")}""")
    if(exam.quizzes.isEmpty) ""
    else {
      var gr = 1
      exam.quizzes.foreach(qqElem => {
        doc.add(new Paragraph(s"Imię i nazwisko..................................... kl. ...... gr $gr"))
        gr += 1
        val questions = QuizQuestion.findAll("_id" -> ("$in" -> qqElem.map(qe => qe.q.toString)))
        //println(s"size of questions ${questions.length}")
          var nr = 1
          for(quest <- questions) {
            val p = new Paragraph(s"$nr. ${rmHtml(quest.question)}")
            doc.add(p)
            nr += 1
            val response = rand.shuffle(quest.answers ++ quest.fake)
            if(response.length > 1) {
              var o = 'A'
              for(ans <- response) {

                val pa = new Paragraph(s"$o. $ans")
                doc.add(pa)
                o = (o.toInt + 1).toChar
              }
            }
          }
      })
      doc.saveAs(outFile)
      println(s"Saved file: $aFile")
      aFile
    }
  }
  def getDoc(id:String):Box[LiftResponse] = {
    val path = s"/home/ope/$id.odt"
    //println(path)
    val f = new java.io.File(path)

    if(id.length > 23 && f.exists()) {
      val bytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(f.toURI))
      val inputStream = new ByteArrayInputStream(bytes)
      Full(StreamingResponse(inputStream, () =>
        (), inputStream.available().toLong, ("Content-Type", "application/vnd.oasis.opendocument.text") :: Nil, Nil, 200))
    }
    else Full(NotFoundResponse("Not found"))
  }

  private def rmHtml(html:String):String = {
    println(scala.xml.Unparsed(html).text)
    html.replace("<p>", "").replace("</p>","")
      .replace("&oacute;", "ó").replace("&nbsp;", " ")
      .replace("<br>", "\n").replace("<br />", "\n")
      .replace("&ndash;", "–").replace("&gt;", ">").replace("&lt;", "<")
  }

}
