package eu.brosbit.ope.lib

import eu.brosbit.ope.model.{Exam, QuizQuestion}
import _root_.net.liftweb.json.JsonDSL._

class CreateTextExam {

  def createDoc(exam:Exam): String = {
    var txtDoc = ""
    val quizList = exam.quizzes.map(q => q.map(_.q.toString))
    var nr = 1
    quizList.foreach( qz => {
      val questions = QuizQuestion.findAll("_id" -> ("$in" -> qz))
      txtDoc += s"Imię i nazwisko..................................... kl. ...... gr $nr"
      for (quest <- questions) {
        txtDoc += quest.question
        txtDoc += "\n"
        txtDoc += quest.answers.mkString(" \uF06D  ")
        txtDoc += "\n"
      }
      nr += 1
    })
    txtDoc
  }

}
