package eu.brosbit.ope.snippet.edu

import java.util.Date
import _root_.net.liftweb.util._
import Helpers._
import eu.brosbit.ope.lib.{Formater, PolishSorter}
import net.liftweb.json.JsonDSL._
import eu.brosbit.ope.model.{Work, WorkAnswer}
import net.liftweb.http.S

class ShowWorkAnswersSn {
  private val workId = S.param("id").getOrElse("0")
  private val work = Work.find(workId).getOrElse(S.redirectTo("/educontent/works"))
  private val workAns = WorkAnswer.findAll("work" -> workId)

  def showInfo(): CssSel = {
    "span *" #> work.lessonTitle &
    "small *" #> Formater.formatTime(new Date(work.start))
  }

  def showWorkAnswers(): CssSel = {
    "tr" #>  workAns.sortWith((wa1, wa2) => PolishSorter.polishSort(wa1.authorName, wa2.authorName)).map(wa => {
      println(s"${wa.authorName}, answers: ${wa.answers.length}")
      ".col1 *" #> wa.authorName &
        ".col2 *" #> (if(wa.pupilChanged) <span class="isRed"></span> else <span class="notRed"></span>) &
        ".col3 *" #> wa.answers.length &
        ".col4 *" #> <a href={"/educontent/checkwork/" + wa._id.toString + "?workId="+workId}
                        class="btn btn-small btn-success"><span class="glyphicon glyphicon-check"></span>Sprawdź</a>
    })

  }

}
