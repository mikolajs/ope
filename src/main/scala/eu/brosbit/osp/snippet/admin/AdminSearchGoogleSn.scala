package eu.brosbit.osp.snippet.admin

import eu.brosbit.osp.model.ExtraData
import net.liftweb.http.SHtml
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._

/**
 * dodawanie codu z google
 */
class AdminSearchGoogleSn {

def show(): CssSel = {

  var code = ExtraData.getData("googlesearchcode")
  def save(): Unit = {
    ExtraData.updateKey("googlesearchcode", code)
  }
  "#code" #> SHtml.textarea(code, code = _) &
  "#save" #> SHtml.submit("Zapisz", save)
}

}
