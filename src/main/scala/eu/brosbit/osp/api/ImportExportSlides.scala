package eu.brosbit.osp.api

import bootstrap.liftweb.MongoConnectionIdentifier
import net.liftweb._
import common._
import http._
import eu.brosbit.osp.model.Presentation
import java.util.zip.{CRC32, ZipEntry, ZipInputStream, ZipOutputStream}
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import com.mongodb.gridfs._
import net.liftweb.mongodb._
import scala.xml._
import eu.brosbit.osp.model.User
import json.JsonDSL._

///TO DELETE????

object ImportExportSlides {


  val user: User =  User.currentUser match {
    case Full(u) => u
    case _ => S.redirectTo("/login")
  }

  def zip(): Box[LiftResponse] = {


    val baos = mkExport
    val path = java.nio.file.Paths.get("/tmp/export.zip")
    java.nio.file.Files.write(path, baos.toByteArray)

    val inputStream = new ByteArrayInputStream(baos.toByteArray)
    if(inputStream.available() < 10){
      Full(NotFoundResponse("Not found stream"))
    }
    else {
      Full(StreamingResponse(inputStream, () => (), inputStream.available().toLong,
        ("Content-Type", "application/zip") :: ("Content-Disposition", "attachment; filename=\"export.zip\"") :: Nil,
        Nil, 200))
    }
  }

  def loadZip(fileBox:Box[FileParamHolder]):Boolean = {

      if(isCorrect(fileBox)) {
        val fph = fileBox.openOrThrowException("Impossible, file must be present!")
        val bArray = fph.file
        val bais = new ByteArrayInputStream(bArray)
        val baos = new ByteArrayOutputStream()
        val zis = new ZipInputStream(bais)

        val buffer = new Array[Byte](4096)
        var len = 1
        var name = ""
        val idChange = scala.collection.mutable.Map[String, String]()
        var slidesStr = ""
        //val pathDir = "/home/ms/Pobrane/test/"

        var entry = zis.getNextEntry
        while(entry != null && !entry.isDirectory) {

          name = entry.getName

          val date =  new java.util.Date(entry.getTime)
          println(s"Zip entry name: $name compresed size size: ${entry.getCompressedSize} size: ${entry.getSize} time: $date " )
          println("ZIP comment " + entry.getComment)
          if( !entry.isDirectory) {
            while(len > 0) {
              len = zis.read(buffer)
              if(len > 0) {
                baos.write(buffer, 0, len)
              }
            }
//            val path = java.nio.file.Paths.get(pathDir + name)
//            java.nio.file.Files.write(path, baos.toByteArray)
//            println(s"saved file $path")
          }

          if(name == "slides.xml") {
            slidesStr = baos.toString("UTF-8")
          }
          else {
            val extension = name.split('.').last.toLowerCase
            if(extension == "png" || extension == "jpg" || extension == "gif" || extension == "jpeg")
                idChange(name.split('.').head) = saveImage(baos.toByteArray, name)
          }

          baos.reset()
          zis.closeEntry()
          entry = zis.getNextEntry
          len = 1
        }
        zis.closeEntry()
        bais.close()
        insertSlides(slidesStr, idChange)
        zis.close()
      }
    true
    }


  private def mkExport: ByteArrayOutputStream = {
    val user = User.currentUser match {
      case Full(u) => u
      case _ => S.redirectTo("/login?r=/export")
    }
    println("========== User want get export: " + user.getFullName)
    val baos = new ByteArrayOutputStream()
    val zos = new ZipOutputStream(baos)
    val crc = new CRC32
    zos.setLevel(9)
    val slides = Presentation.findAll("authorId" -> user.id.get).map(sh => {
      val reg = """ src=\"(.*)\"""".r
      val srcs = reg.findAllIn(sh.slides)
      var imgs: List[String] = Nil
      while (srcs.hasNext) {
        val ar = srcs.next.split('"')
        if (ar.length > 1) imgs = ar(1) :: imgs
      }
      imgs.foreach(img => {
        if (img.startsWith("/") && !img.startsWith("//")) {
          val p = img.split('/')
          if (p.length > 2) {
            //println("++++ " + p(2))
            val array = p(2).split('.')
            val outStream = getImages(array(0))
            val e = new ZipEntry(array.mkString("."))
            val binary = outStream.toByteArray
            if (binary.length > 100) {
              crc.reset()
              crc.update(binary)
              e.setMethod(ZipEntry.STORED)
              e.setSize(binary.length)
              e.setCrc(crc.getValue)
              zos.putNextEntry(e)
              zos.write(binary, 0, binary.length)

              zos.closeEntry()
            } else println("!!!!! emtpy output stream")
          }
        }
      })
      <slide>
        <title>
          {sh.title}
        </title>
        <content>
          {Unparsed(sh.slides.replaceAll("&.{2,4};", ""))}
        </content>
      </slide>
    })

    val html = <slides>
      {slides}
    </slides>
    val xmlStr = toXML(html.toString)
    val xml = <root>
      {Unparsed(xmlStr)}
    </root>
    val e = new ZipEntry("slides.xml")
    val ar = xml.toString.getBytes
    crc.reset()
    crc.update(ar)
    e.setMethod(ZipEntry.DEFLATED)
    e.setSize(ar.length)
    e.setCrc(crc.getValue)
    zos.putNextEntry(e)
    zos.write(ar, 0, ar.length)
    println(s"SIeze of Entry slidex.xml: ${e.getSize}")
    zos.closeEntry()
    zos.finish()
    zos.close()
    baos
  }

  private def getImages(id: String): ByteArrayOutputStream = {
    val outputStream = new ByteArrayOutputStream()
    if (id.nonEmpty) {
      MongoDB.use(MongoConnectionIdentifier) { db =>
        val fs = new GridFS(db)
        val foundFile = fs.findOne(new org.bson.types.ObjectId(id))
        if (foundFile == null) println("null!!!")
        else {
          foundFile.writeTo(outputStream)
        }
      }
    }
    outputStream
  }

  private def toXML(str: String): String = {
    import org.jsoup.Jsoup
    import org.jsoup.safety.Whitelist
    //import scala.collection.JavaConversions._
    val par = org.jsoup.parser.Parser.xmlParser()
    val doc = Jsoup.parse(str, "", par)
    doc.toString()
  }

  private def isCorrect(fileHold:Box[FileParamHolder]) = {

    fileHold match {

      case Full(FileParamHolder(_, mime, _, _)) =>
        if(mime == "application/zip") true
        else false
      case Full(_) => {
        println("Error, File import failed")
        false
      }
      case _ => {
        println("Error, File not fount")
        false
      }
    }
  }

  private def saveImage(data: Array[Byte], name: String): String = {

    val mimeType = "image/" + name.last.toLower
    var fileId = ""
    MongoDB.use(MongoConnectionIdentifier) {
      db =>
        val fs = new GridFS(db)
        val inputFile = fs.createFile(new ByteArrayInputStream(data))
        inputFile.setContentType(mimeType)
        inputFile.setFilename(name)
        inputFile.save
        fileId = inputFile.getId.toString
    }
    fileId
  }

  private def insertSlides(slidesStr: String, idChange: scala.collection.mutable.Map[String, String]): Unit = {

    var newSliesStr = slidesStr
    val reg = """src="(.*)" """.r
    val hits = reg.findAllIn(slidesStr)
    hits.foreach(hit => {
      val ar = hit.split('"')
      if (ar.length > 1) {
        val src = ar(1)
        if (!src.startsWith("http") && !src.startsWith("//")) {
          val paths = src.split('/')
          if(paths.length > 2){
            val a = paths(2).split('.')
            if(a.length > 0){
              val id = a(0)
              if(idChange.contains(id)){
                newSliesStr = newSliesStr.replace(id, idChange(id))
              }
            }
          }
        }
      }
    })
    val rootXML = XML.loadString(newSliesStr)
    val uId = user.id.get
    val slides = (rootXML \ "slides") \ "slide"
    slides.foreach(slide => {
      val title = (slide \ "title").text
      println(title)
      val content =  ((slide \ "content") \ "section").mkString("\n")
      val slideMod = Presentation.create
      slideMod.authorId = uId
      slideMod.title = title
      slideMod.slides = content
      slideMod.save
    })
  }

}