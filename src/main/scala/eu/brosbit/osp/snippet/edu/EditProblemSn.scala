package eu.brosbit.osp.snippet.edu

import eu.brosbit.osp.model.{TestProblem, User}
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.http.{S, SHtml}
import scala.xml.{Text, Unparsed}

class EditProblemSn {
  var id = S.param("id").getOrElse("0")
  var user = User.currentUser.getOrElse(S.redirectTo("/login"))
  println("im a user " + user.id.get)
  val problem = if(id == "0") TestProblem.create else TestProblem.find(id).getOrElse(TestProblem.create)
  if(problem.author != 0 && problem.author != user.id.get ) S.redirectTo("/educontent/problems")
  else if(problem.author == 0L) problem.author = user.id.get
  def edit:CssSel = {
    var idP = problem._id.toString
    var titleP = problem.title
    var infoP = problem.info
    var descriptionP = problem.description
    var inData = problem.inputs
    var outData = problem.expectedOutputs
    //println(jsonStr)
    def deleteProblem() {
      problem.delete
      S.redirectTo("/educontent/problems")
    }
    def saveProblem() {
      import net.liftweb.json.DefaultFormats
      import net.liftweb.json.Serialization.read
      implicit val format = DefaultFormats
      if(titleP.trim.isEmpty) titleP = "Brak nazwy!!!!"
      problem.info = infoP
      problem.description = descriptionP
      //println(jsonStr)
      problem.inputs = inData
      problem.expectedOutputs = outData
      problem.title = titleP
      problem.save
      S.redirectTo("/educontent/editproblem/"+problem._id.toString)
    }
    "#idProblem" #> SHtml.text(idP, idP = _) &
    "#titleProblem" #> SHtml.text(titleP, titleP = _ ) &
    "#infoProblem" #> SHtml.text(infoP, infoP = _) &
    "#descriptionProblem" #> SHtml.textarea(descriptionP, descriptionP = _) &
    "#inData" #> SHtml.textarea(inData, inData = _) &
    "#outData" #> SHtml.textarea(outData, outData = _) &
    "#deleteProblem" #> SHtml.button(Text("Usuń"), deleteProblem, "title" -> "Usuń", "style" -> "display:none") &
    "#saveProblem" #> SHtml.submit("Zapisz", saveProblem, "title" -> "Zapisz", "style" -> "display:none")
  }
}
