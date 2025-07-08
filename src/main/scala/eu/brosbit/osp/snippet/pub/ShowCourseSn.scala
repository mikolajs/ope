package eu.brosbit.osp.snippet.pub

import net.liftweb.util.Helpers._
import eu.brosbit.osp.snippet.BaseShowCourseSn
import net.liftweb.util.CssSel

class ShowCourseSn extends BaseShowCourseSn {

  override val basePath = "/public/course/"
//controla czy można oglądać kurs
  def show(): CssSel = {

    if (course.title != "" && course.pub) {
      "#subjectListLinks a" #> createLessonList &
        "#courseInfo" #> <div class="alert alert-success">
          <h2>
            {course.title}
          </h2> <p>
            {course.descript}
          </p>
        </div> &
        ".content *" #> this.showAsDocument(currentLesson, admin = false)
    } else ".main *" #> <h1>Nie ma takiego kursu lub brak lekcji udostępnionej publicznie</h1>
  }



}