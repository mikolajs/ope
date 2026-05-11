package eu.brosbit.ope.lib
///https://www.jopendocument.org/docs/index.html
import org.jopendocument.dom.text.TextDocument
import org.jopendocument.dom.text.Paragraph

import java.io.File
import eu.brosbit.ope.model.{Exam, Quiz, QuizQuestion}
import net.liftweb.common.{Box, Full}
import net.liftweb.http.{LiftResponse, NotFoundResponse}
import net.liftweb.json.JsonDSL._

object ExamFileOdt {
  val path = "/home/ope"
  def createExamFileOdt(id:String):String = {
    val aFile = s"$path/$id.odt"
    val outFile = new File(aFile)
    if(!outFile.exists) outFile.createNewFile()
    val doc = TextDocument.createEmpty("")
    val exam = Exam.find(id).getOrElse(Exam.create)
    if(exam.quizzes.isEmpty) ""
    else {
      var gr = 1
      exam.quizzes.foreach(qqElem => {
        doc.add(new Paragraph(s"Imię i nazwisko..................................... kl. ...... gr $gr"))
        gr += 1
        val questions = QuizQuestion.findAll("_id" -> ("$in" -> qqElem.map(q => q.toString)))
          var nr = 1
          for(quest <- questions) {
            val p = new Paragraph(s"$nr. ${quest.question}")
            doc.add(p)
            nr += 1
            if(quest.answers.length > 1) {
              var o = 'A'
              for(ans <- quest.answers) {
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
    if(id.length < 24) Full(NotFoundResponse("Not found"))
    else Full(NotFoundResponse("Not found"))
  }
}
