package eu.brosbit.ope.snippet.edu

import eu.brosbit.ope.model.User
import eu.brosbit.ope.model.Groups
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.json.JsonDSL._

import scala.xml.NodeSeq

class GroupsSn {
  val user: User.TheUserType = User.currentUser.openOrThrowException("Nie jesteś zalogowany")
  val groups: List[Groups] = Groups.findAll("authorId" -> user.id.get)

  def showGroups: CssSel = {
    "#tbody" #> groups.map(group => {
      <tr id={"id_" + group._id.toString}>
        <td>
          {group.name}
        </td> <td>
        {group.description}
      </td> <td>
        {group.students.length.toString}
      </td>
        <td>
          <a class="btn btn-success" href={"/educontent/groupedit/" + group._id.toString}>Edytuj</a>
        </td>
        <td>
          <button class={if(group.blocked) "btn btn-danger" else "btn btn-success"} onclick="groups.change(this);">
            Zmień</button>
        </td>
      </tr>
    })
  }


  def blocking: CssSel = {
    var check = false
    var id = ""
    def changeBlock():JsCmd = {
      val grOpt = groups.find(g => g._id.toString == id)
      if(grOpt.nonEmpty) {
        val gr = grOpt.get
       // println(s"""run changeBlock ${gr.name} blocked is ${gr.blocked} and will be $check""")
        gr.blocked = check
        gr.save
        Run(s"groups.setGroup('$id', $check)")
      }
      else Run(s"""alert("Grupy ${id} nie znaleziono!")""")
    }

    val f = "#ajaxId" #> SHtml.text(id, x => id = x, "id" -> "ajaxId") &
      "#ajaxBlock" #> SHtml.checkbox(check, x => check = x, "id" -> "ajaxBlock") &
      "#ajaxSubmit" #> SHtml.ajaxSubmit("OK", changeBlock) andThen SHtml.makeFormsAjax
    "form" #> ((in:NodeSeq) => f(in))
  }
}