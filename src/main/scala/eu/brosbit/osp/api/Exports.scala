package eu.brosbit.osp.api

import bootstrap.liftweb.MongoConnectionIdentifier
import com.mongodb.gridfs.GridFS
import com.mongodb.BasicDBObject
import eu.brosbit.osp.api.JsonExportImport.{CourseImport, DocumentsImport, LessonImport, PresentationImport, ProblemImport, QuestionImport, VideoImport}

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import eu.brosbit.osp.lib.Zipper
import eu.brosbit.osp.model.User
import eu.brosbit.osp.model._
import net.liftweb.json.JsonDSL._
import net.liftweb.common.{Box, Full}
import net.liftweb.http.{InMemoryResponse, LiftResponse, NotFoundResponse, S, StreamingResponse}
import net.liftweb.mongodb.MongoDB
import org.bson.types.ObjectId

//import java.nio.file.{Files, Paths}
/*It should be export only for Administrator!!!*/

object Exports {

  def saveExportedFiles(user:User):List[String] = {
    val map =  jsonStringDataToMap(user)
    val filesMap = getImgAndFiles(user)
    val zipJsons = packager.toZipFilesFromBytes(map.map(m => (m._1, m._2.getBytes)))
    //println("DIR NAME::::: " + user.userDirName)
    val zipDirLink = s"/home/${user.userDirName}/"
    val zipJsonLink = zipDirLink + "export_json.zip"
    saveFile(zipJsonLink, zipJsons)
    var filesList:List[String] = List(zipJsonLink)
    var numberFile = 0
    var sizeFiles = 0
    val zipParts:scala.collection.mutable.Map[String, Array[Byte]] = scala.collection.mutable.Map()
    //println(filesMap.size)
    for(fileMap <- filesMap){
      sizeFiles += fileMap._2.length
      zipParts ++= filesMap
      if(sizeFiles > 64*1024*1024) {
        val dataFile = zipDirLink + "export_files_" + numberFile.toString + ".zip"
        val aZip = packager.toZipFilesFromBytes(zipParts.toMap)
        saveFile(dataFile, aZip)
        filesList = dataFile :: filesList
        numberFile += 1
        sizeFiles = 0
        zipParts.clear()
      }
    }
    if(sizeFiles > 0) {
      val dataFile = zipDirLink + "export_files_" + numberFile.toString + ".zip"
      val aZip = packager.toZipFilesFromBytes(zipParts.toMap)
      saveFile(dataFile, aZip)
      filesList = dataFile :: filesList
    }
    filesList
  }

  def getFileFromDisk(userDirName:String, fileName:String): Full[LiftResponse] = {
    val user: User = User.currentUser.getOrElse(S.redirectTo("/"))
    //println(s"GET FILE FROM DISK userDirName: $userDirName is? ${user.userDirName}, fileName: $fileName")
    if(user.userDirName != userDirName)
      Full(NotFoundResponse("Not Yours file"))
    else {
      val fullPath = s"/home/$userDirName/$fileName"
      if(java.nio.file.Files.exists(java.nio.file.Paths.get(fullPath))) {
        val bytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(fullPath))
        val inputStream = new ByteArrayInputStream(bytes)
        val headerFile = ("Content-Disposition", "form-data; filename=\"" + fileName + "\"")
        val headerMain = ("Content-Type", "file/zip")
        val headers = if(fileName.isEmpty) headerMain :: Nil else headerMain :: headerFile :: Nil
        Full(StreamingResponse(inputStream, () => (), inputStream.available().toLong, headers, Nil, 200))
      } else  Full(NotFoundResponse("Wrong url"))
    }
  }

  private def saveFile(link:String, data:Array[Byte]) =
      java.nio.file.Files.write(java.nio.file.Paths.get(link), data)
/*
  def export(): Box[LiftResponse] = {
    val mime = "zip"
    val userBox = User.currentUser
    if(userBox.isEmpty || (userBox.openOrThrowException("Not possible").role.get != "n"  &&
      userBox.openOrThrowException("Not possible").role.get != "a"  &&
      userBox.openOrThrowException("Not possible").role.get == "d" ))
      Full(NotFoundResponse("You are not permitted!"))
    else {
      val data = createZip(userBox.openOrThrowException("Not possible"))
      val inputStream = new ByteArrayInputStream(data)
      if (inputStream.available() < 10) {
        Full(NotFoundResponse("Not found"))
      }
      else {
        Full(InMemoryResponse(data, ("Content-Type", "application/zip") :: ("Content-Disposition", "form-data; filename=\"export.zip\"") :: Nil, Nil, 200))
      }
    }
  }

 */

  private val packager = new Zipper()

  object JsonFileNames extends Enumeration {
    val Documents:Value  = Value("documents.json")
    val Presentations:Value = Value("presentations.json")
    val Questions:Value = Value("questions.json")
    val Videos:Value = Value("videos.json")
    val Lessons:Value  = Value("lessons.json")
    //val Problems:Value = Value("problems.json")
    val Courses:Value = Value("courses.json")
  }
//TODO: implement files to zip
  private def createZip(user:User): Array[Byte] = {
   val map =  jsonStringDataToMap(user)
    val fileMap = getImgAndFiles(user)
    //fileMap.foreach(m => println(m._1 + ": " + m._2.length))
    packager.toZipJsonAndBinary(map, fileMap)
    //map("dane").getBytes("UTF-8")
  }
  private def jsonStringDataToMap(user: User):Map[String, String]= {
    Map(
      JsonFileNames.Documents.toString     -> getDocuments(user),
      JsonFileNames.Presentations.toString -> getPresentations(user),
      JsonFileNames.Questions.toString     -> getQuizQuestions(user),
      JsonFileNames.Videos.toString        -> getVideos(user),
      JsonFileNames.Lessons.toString       -> getLessonCourses(user),
      //JsonFileNames.Problems.toString       -> getProblems(user),
      JsonFileNames.Courses.toString       -> getCourses(user)
    )
  }

  private def getDocuments(user: User) = {
    val docs = Document.findAll("authorId" -> user.id.get).map(d => {
      DocumentsImport(d._id.toString, d.title, d.descript, d.subjectName, d.department, d.content, d.lev).toJson
    }).mkString(", ")
    "[" + docs + "]"
  }
  private def getQuizQuestions(user: User) = {
    val quizzes = QuizQuestion.findAll("authorId" -> user.id.get).map(q => {
      QuestionImport(q._id.toString, q.dificult, q.lev, q.subjectName, q.info, q.question, q.department, q.answers, q.fake, q.hint).toJson
    }).mkString(", ")
    "[" + quizzes + "]"
  }

  private def getPresentations(user: User) = {
    val presentations = Presentation.findAll("authorId"->user.id.get).map(p => {
      PresentationImport(p._id.toString, p.title, p.descript, p.subjectName, p.department, p.slides, p.lev).toJson
    }).mkString(", ")
    "[" + presentations + "]"
  }

  private def getVideos(user:User) = {
    val videos = Video.findAll("authorId" -> user.id.get).map(v => {
      VideoImport(v._id.toString, v.title, v.descript, v.subjectName, v.department, v.link, v.lev, v.onServer, v.mime, v.oldPath).toJson
    }).mkString(", ")
    "[" + videos + "]"
  }
  private def getLessonCourses(user:User) = {
    val lessons = LessonCourse.findAll("authorId"->user.id.get).map(l => {
      LessonImport(l._id.toString, l.title, l.descript, l.chapter, l.courseId.toString, l.contents, l.nr).toJson
    }).mkString(", ")
    "[" + lessons + "]"
  }

//only for test!
  private def getImgsAndFiles(user: User) = {
    val userId = user.id.get
    "[" + ImgFilesSearch.look(userId).map(i => "\"" + i + "\"").mkString(", ") + "]"
  }

  private def getImgAndFiles(user: User):Map[String, Array[Byte]] = {
    val ids = ImgFilesSearch.look(user.id.get)
    var filesMap:Map[String, Array[Byte]] = Map()
    val byteArrayOutputStream = new ByteArrayOutputStream()
    val query = new BasicDBObject("_id", new BasicDBObject("$in", ids))
    MongoDB.use(MongoConnectionIdentifier) {
      db =>
        val fs = new GridFS(db)
        fs.getFileList(query)
        val cursor = fs.getFileList()
        while (cursor.hasNext) {
          val gfsFile = fs.find(cursor.next()).get(0)
          val id = gfsFile.getId().asInstanceOf[ObjectId].toString
          val name = id + "." + gfsFile.getFilename
          //println(s"File: :::::  $name")
          gfsFile.writeTo(byteArrayOutputStream)
          filesMap += (name -> byteArrayOutputStream.toByteArray)
          byteArrayOutputStream.reset()
        }
    }
    //testSaveFiles(filesMap)
    filesMap
  }
 /*
  private def getProblems(user: User) = {
    val userId = user.id.get
    val problems = TestProblem.findAll("author"->userId).map(tp =>
      {
        ProblemImport(tp._id.toString, tp.title, tp.description, tp.info, tp.inputs, tp.expectedOutputs).toJson
      }).mkString(", ")
    "[" + problems + "]"
  }
 */
  private def getCourses(user: User) = {
    val userId = user.id.get
    val courses = Course.findAll("authorId"->userId).map(c => {
      CourseImport(c._id.toString, c.title, c.chapters, c.subjectName, c.descript, c.img).toJson
    }).mkString(", ")
    "[" + courses + "]"
  }



}
