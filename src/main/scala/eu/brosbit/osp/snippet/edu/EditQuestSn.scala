package eu.brosbit.osp.snippet.edu

import scala.xml.{NodeSeq, Unparsed}
import _root_.net.liftweb._
import http.SHtml
import common._
import util._
import eu.brosbit.osp.model._
import Helpers._
import json.JsonDSL._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.http.js.JsCmd
import _root_.net.liftweb.http.js.JE._
import eu.brosbit.osp.lib.Formater

class EditQuestSn extends BaseResourceSn {

  def subjectChoice() = { super.subjectChoice("/educontent/questions")}
  def subjectAndDepartmentChoice() = {
    super.subjectAndDepartmentChoice("/educontent/questions")
  }
  def showQuests() = {
    val userId = user.id.get
    val questionsList =
      QuizQuestion.findAll(
        query,
        ("nr" -> 1)
      )

    "tr" #> questionsList.map(quest => {
      <tr id={quest._id.toString}>
        <td onclick="editQuest.showUsing(this);">{Formater.mkLongExerciseNumber(quest.nr)}</td>
        <td class="question">
          {Unparsed(quest.question)}
        </td>
        <td>
          {Unparsed(quest.info)}
        </td>
        <td>
          {quest.answers.map(f => <span class="good">
          {f}
        </span>)}
        </td> <td>
        {quest.fake.map(f => <span class="wrong">
          {f}
        </span>)}
      </td>
        <td>{if(quest.hint.trim.size > 0) "TAK" else "NIE" }
          <small style="word-break: break-word;"> {Unparsed(quest.hint)} </small></td>
        <td>
          {levMap(quest.lev.toString)}
        </td>
        <td>
          {quest.dificult}
        </td>
        <td>
          <button class="btn btn-success" onclick="editQuest.editQuestion(this);">
            <span class="glyphicon glyphicon-edit"></span>
          </button>
        </td>
      </tr>
    })

  }

  //working ....
  def editQuest(): CssSel = {
    var id = ""
    var nr = 0
    var question = ""
    var level = ""
    var answer = ""
    var wrongAnswers = ""
    var department = ""
    var difficult = "1"
    var info = ""
    var hint = ""

    def save(): JsCmd = {
      //add nr of quest
     // QuizQuestion.findAll(("subjectId" -> subjectId)~ ("department" -> department)
    //  ~ ("authorId" -> userId)).map(qq => qq.nr).max
      val userId = user.id.get
      val quest = QuizQuestion.find(id).getOrElse(QuizQuestion.create)
      if (quest.authorId != 0L && quest.authorId != userId) return Alert("To nie twoje pytanie!")
      if (subjectNow.departments.isEmpty) return Alert("Musisz najpierw utworzyc dział w ustawieniach")
      quest.authorId = userId
      if(nr == 0) {
        val qi = QuestIndex.find(("authorId" ->  userId) ~ ("subjectId" -> subjectNow.id))
          .getOrElse(QuestIndex.create(userId, subjectNow.id))
        if(qi.nr == 0) {
          nr = 1
          qi.nr = 1
        } else {
          nr = qi.nr + 1
          qi.nr = nr
        }
        qi.save
      }
      quest.nr = nr
      quest.answers = answer.split(getSeparator).toList.map(a => a.trim).filterNot(_.isEmpty)
      quest.fake = wrongAnswers.split(getSeparator).toList.map(a => a.trim).filterNot(_.isEmpty)
      quest.question = question.trim
      quest.info = info.trim
      quest.subjectId = subjectNow.id
      quest.subjectName = subjectNow.name
      quest.department = department.trim
      quest.dificult = tryo(difficult.toInt).openOr(1)
      quest.lev = tryo(level.toInt).openOr(1)
      quest.hint = hint.trim
      quest.save
      JsFunc("editQuest.insertQuestion", quest._id.toString, quest.nr).cmd
    }

    def delete(): JsCmd = {
      //println("+++++++++++++++++++ Del QUEST ")
      val userId = user.id.get
      QuizQuestion.find(id) match {
        case Some(quest) => {
          if (quest.authorId == userId) {
            quest.delete
            JsFunc("editQuest.deleteQuestion", quest._id.toString).cmd
          }
          else Alert("To nie twoje pytanie!")
        }
        case _ => Alert("Nie znaleziono pytania!")
      }
    }

    val difficultList = List(("1", "Normalne"),("2", "Trudniejsze"),("3", "Bardzo trudne"))
    val departments = subjectNow.departments.map(d => (d, d))

    val form = "#idQuest" #> SHtml.text(id, id = _) &
      "#nrQuest" #> SHtml.text(nr.toString, x => nr = x.toInt) &
      "#infoQuest" #> SHtml.text(info, info = _) &
      "#questionQuest" #> SHtml.textarea(question, x => question = x.trim) &
      "#answerQuest" #> SHtml.text(answer, x => answer = x.trim) &
      "#subjectQuest" #> SHtml.text(subjectNow.name, x => (), "readonly" -> "readonly") &
      "#levelQuest" #> SHtml.select(levList, Full(subjectNow.lev.toString), level = _) &
      "#wrongQuest" #> SHtml.text(wrongAnswers, x => wrongAnswers = x.trim) &
      "#departmentQuest" #> SHtml.select(departments, Full(departName), department = _) &
      "#hintQuest"#> SHtml.textarea(hint, x => hint = x.trim) &
      "#dificultQuest" #> SHtml.select(difficultList, Full(difficult), difficult = _) &
      "#saveQuest" #> SHtml.ajaxSubmit("Zapisz", save) &
      "#deleteQuest" #> SHtml.ajaxSubmit("Usuń", delete) andThen SHtml.makeFormsAjax

    "form" #> ((in:NodeSeq) => form(in))

  }

  def showUsing():CssSel = {
    var nr = ""
    val form =
    "#nrOfQuest" #> SHtml.text(nr, nr = _) &
    "#buttonQuest" #> SHtml.ajaxSubmit("Pokaż",() => {
      val info = lookingQuestUsing("_" + nr)
      SetHtml("usingQuestInfo", <div>{Unparsed(info)}</div>)
      //JsFunc("editQuest.showQuestInfo").cmd
    }, "style" -> "display:none;") andThen SHtml.makeFormsAjax

    "form" #> ((in:NodeSeq) => form(in))
  }

  private def lookingQuestUsing(id:String): String = {
    val e = Exam.findAll("authorId" -> user.id.get).filter(e => {
       e.quizzes.exists(qe => qe.exists(q => {
         q.q.toString == id.drop(1)
          }))
      }).map(e => s" – ${e.description}  ").mkString("<br>")

    val s = LessonCourse.findAll("authorId" -> user.id.get).filter(k => k.contents.exists(w => w.id == id)).map(k =>
        k.title + " - <i>" + k.chapter + "</i> <span>nr: " + k.nr  + "</span>").mkString("<br>")
   // println("LOOKING using questions/n " + s)
    val str1 = if(e.nonEmpty) s"<div><h4>Sprawdziany:</h4>$e</div>" else ""
    val str2 = if(s.nonEmpty) s"<div><h4>Lekcje:</h4>$s</div>" else ""
    str1+str2
//
  }


  //private def printParam = println("subjectId="+ subjectId + " level=" + level)

}