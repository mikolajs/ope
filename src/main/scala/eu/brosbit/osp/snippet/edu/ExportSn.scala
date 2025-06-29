package eu.brosbit.osp.snippet.edu

import eu.brosbit.osp.api.Exports.saveExportedFiles
import eu.brosbit.osp.model.User
import net.liftweb.http.S
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

class ExportSn {
  val user: User = User.currentUser.getOrElse(S.redirectTo("/"))
  def exportedFiles():CssSel = {
    val filesLinks = saveExportedFiles(user)
    ".filesExported *" #> filesLinks.map(link =>{
       val url = "/exportdata/" + link.split("/").drop(2).mkString("/")
       <div><a href={url}>{link.split("/").last}</a></div>
    })
  }
}
