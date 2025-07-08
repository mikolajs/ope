package eu.brosbit.osp.snippet.page

import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http.{FileParamHolder, RequestVar, S, SHtml}
import Helpers._
import bootstrap.liftweb.MongoConnectionIdentifier

import java.awt.image.BufferedImage
import java.awt.Image
import javax.imageio.ImageIO
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, File, FileOutputStream}
import com.mongodb.gridfs._
import net.liftweb.mongodb.MongoDB
//import _root_.net.liftweb.mongodb.DefaultMongoIdentifier

/**
 * snipet ma zapisywać grafikę i pliki dla strony
 */
class FilesSn {

  object linkpath extends RequestVar[String]("")

  var fileHold: Box[FileParamHolder] = Empty
  var mimeType = ""
  var fullFileName = ""
  var mimeTypeFull = ""

  def addImage(): CssSel = {
    val imgSize = S.param("s").getOrElse("n") match {
      case "f" => 300
      case "s" => 400
      case "n" => 800
      case "l" => 1600
    } //normal size 800
    def isCorrect = readImage()

    def save(): Unit = {
      if (isCorrect) {
        var isAnimation = false
        if(mimeType.substring(1).toLowerCase() == "gif") {
          val imgReader = ImageIO.getImageReadersBySuffix("GIF").next()
          val impStream = ImageIO.createImageInputStream(
            fileHold.openOrThrowException("Pusty plik").fileStream)
          imgReader.setInput(impStream)
          if(imgReader.getNumImages(true) > 1) {
            isAnimation = true
          }
        }
        val inputStream = if (isAnimation){
          //no resize because is GIF!
          fileHold.openOrThrowException("Pusty plik").fileStream
        } else {
          val imageBuf: BufferedImage = ImageIO.read(
            fileHold.openOrThrowException("Brak pliku").fileStream)
          val resizedImageBuf = resizeImageWithFixWidth(imageBuf, imgSize)
          val outputStream = new ByteArrayOutputStream()
          ImageIO.write(resizedImageBuf, mimeType.substring(1), outputStream)
          new ByteArrayInputStream(outputStream.toByteArray())
        }

        MongoDB.use(MongoConnectionIdentifier) {
          db =>
            val fs = new GridFS(db)
            val inputFile = fs.createFile(inputStream)
            inputFile.setContentType(mimeTypeFull)
            inputFile.setFilename(fullFileName)
            inputFile.save
            linkpath("/img/" + inputFile.getId().toString() + mimeType)
        }

      }
    }

    "#file" #> SHtml.fileUpload(fileUploaded => fileHold = Full(fileUploaded)) &
      "#submit" #> SHtml.submit("Dodaj!", save) &
      "#linkpath" #> <span id="linkpath">
        {linkpath.is}
      </span>
  }

  def addThumbnail(): CssSel = {

    def isCorrect = readImage()

    def save(): Unit = {
      if (isCorrect) {
        val imageBuf: BufferedImage = ImageIO.read(new ByteArrayInputStream(
          fileHold.openOrThrowException("Brak pliku").file))
        val resizedImageBuf = resizeImageWithFixWidth(imageBuf, 270)
        var outputStream = new ByteArrayOutputStream()
        ImageIO.write(resizedImageBuf, mimeType.substring(1), outputStream)
        val inputStream = new ByteArrayInputStream(outputStream.toByteArray())
        //println("file: %s, mimetype: %s, ".format(fileName, mimeType))
        MongoDB.use(MongoConnectionIdentifier) {
          db =>
            val fs = new GridFS(db)
            val inputFile = fs.createFile(inputStream)
            inputFile.setContentType(mimeTypeFull)
            inputFile.setFilename(fullFileName)
            inputFile.save
            linkpath("/img/" + inputFile.getId().toString() + mimeType)
        }
      }
    }

    "#file" #> SHtml.fileUpload(fileUploaded => fileHold = Full(fileUploaded)) &
      "#submit" #> SHtml.submit("Dodaj!", save) &
      "#linkpath" #> <span id="linkpath" style="display:none;">
        {linkpath.is}
      </span>
  }

  //do tworzenia ikonek wpisu w celu pomniejszania ich
  def refreshThumbnail(url: String): String = {
    val splitURL = url.split("/")
    var newURL = url;
    if (splitURL.length == 2 && splitURL.head == "" && splitURL(1) == "img") {
      MongoDB.use(MongoConnectionIdentifier) {
        db =>
          val fs = new GridFS(db)
        //pobrać plik i sprawdzić wymiary jeśli ma szerokość 270px nic nie robić
        //gdy ma mniej zeskalować i zapisać.
        //          val inputFile = fs.createFile(inputStream)
        //          inputFile.setContentType(mimeTypeFull)
        //          inputFile.setFilename(fullFileName)
        //          inputFile.save
        //          newURL = "/img/" + inputFile.getId().toString() + mimeType
      }
    }
    newURL
  }

  private def readImage(): Boolean = {
    fileHold match {
      case Full(FileParamHolder(_, mime, _, data)) => {
        if (mime.startsWith("image/")) {
          mimeType = "." + mime.split("/").last
          mimeTypeFull = mime.toString.toLowerCase()
          fullFileName = fileHold.openOrThrowException("Brak pliku").
            fileName.split('.').dropRight(1).mkString("") + mimeType
          println("mimetype is good")
          true
        } else {
          println("mimetype is wrong")
          false
        }
      }
      case Full(_) => {
        println("NOT FileParamHolder?????")
        S.error("Nieprawidłowy format pliku!")
        false
      }
      case _ => {
        println("Noting????????????????????????????")
        S.error("Nie wczytano pliku")
        false
      }
    }
  }


  private def resizeImageWithProportion(imageBuf: BufferedImage, maxDimension: Int): BufferedImage = {
    val bufferedImageTYPE = getImageType
    var imageBufferOut: BufferedImage = imageBuf
    val width = imageBuf.getWidth
    val height = imageBuf.getHeight
    if (width > maxDimension || height > maxDimension) {
      if (width > height) {
        val image: java.awt.Image = imageBuf.getScaledInstance(maxDimension,
          (height.toDouble * maxDimension.toDouble / width.toDouble).toInt, Image.SCALE_SMOOTH)
        imageBufferOut = new BufferedImage(maxDimension,
          (height.toDouble * maxDimension.toDouble / width.toDouble).toInt, bufferedImageTYPE)
        imageBufferOut.getGraphics.drawImage(image, 0, 0, null)
        //imageBuf = im.asInstanceOf[BufferedImage]
      } else {
        val image: java.awt.Image =
          imageBuf.getScaledInstance((width.toDouble * maxDimension / height.toDouble).toInt,
            maxDimension, Image.SCALE_SMOOTH)
        imageBufferOut = new BufferedImage((width.toDouble * maxDimension.toDouble / height.toDouble).toInt,
          maxDimension, bufferedImageTYPE)
        imageBufferOut.getGraphics.drawImage(image, 0, 0, null)
      }
    } else {
      imageBufferOut = new BufferedImage(width, height, bufferedImageTYPE)
      imageBufferOut.getGraphics.drawImage(imageBuf, 0, 0, null)
    }
    imageBufferOut
  }

  private def resizeImageWithFixWidth(imageBuf: BufferedImage, maxWidth: Int): BufferedImage = {
    val bufferedImageTYPE = getImageType
    var imageBufferOut: BufferedImage = imageBuf
    val width = imageBuf.getWidth
    val height = imageBuf.getHeight
    val image: java.awt.Image = imageBuf.getScaledInstance(maxWidth,
      (height.toDouble * maxWidth.toDouble / width.toDouble).toInt, Image.SCALE_SMOOTH)
    imageBufferOut = new BufferedImage(maxWidth,
      (height.toDouble * maxWidth.toDouble / width.toDouble).toInt, bufferedImageTYPE)
    imageBufferOut.getGraphics.drawImage(image, 0, 0, null)
    //imageBuf = im.asInstanceOf[BufferedImage]
    imageBufferOut
  }

  /* load file non image method*/
  def uploadFile(): CssSel = {
    var fileHold: Box[FileParamHolder] = Empty
    def isCorrect = fileHold match {
      case Full(FileParamHolder(_, mime, fileNameIn, data)) => {
        fullFileName = fileNameIn
        mimeType = mime.toString.toLowerCase()
        if (data.length < 30000000) true
        else {
          println("Za duży plik!")
          S.error("Za duży plik!")
          false
        }
      }
      case Full(_) => {
        println("Nieprawidłowy format pliku!")
        S.error("Nieprawidłowy format pliku!")
        false
      }
      case _ => {
        println("Brak pliku")
        S.error("Brak pliku?")
        false
      }
    }

    def save(): Unit = {
      if (isCorrect) {
        MongoDB.use(MongoConnectionIdentifier) {
          db =>
            val fs = new GridFS(db)
            val inputFile = fs.createFile(fileHold.openOrThrowException("Brak pliku")
              .fileStream)
            inputFile.setContentType(mimeType)
            inputFile.setFilename(fullFileName)
            inputFile.save()
            linkpath("/file/" + inputFile.getId().toString() + "." + fullFileName.split('.').last)
        }
      }
    }
    "#file" #> SHtml.fileUpload(x => fileHold = Full(x)) &
      "#submit" #> SHtml.submit("Dodaj!", save) &
      "#linkpath" #> <span id="linkpath">
        {linkpath.is}
      </span>
  }

  def addSlide(): CssSel = {
    def isCorrect = readImage()
    val bufferedImageTYPE = getImageType

    def save(): Unit = {
      if (isCorrect) {
        val fileName = fileHold.openOrThrowException("Brak pliku").fileName
        MongoDB.use(MongoConnectionIdentifier) {
          db =>
            val fs = new GridFS(db)
            val inputFile = fs.createFile(fileHold.openOrThrowException("Brak pliku").
              fileStream)
            inputFile.setContentType(mimeTypeFull)
            inputFile.setFilename(fileName)
            inputFile.save()
            linkpath("/img/" + inputFile.getId.toString + mimeType)
        }
      }
    }

    "#file" #> SHtml.fileUpload(fileUploaded => fileHold = Full(fileUploaded)) &
      "#submit" #> SHtml.submit("Dodaj!", save) &
      "#linkpath" #> <span id="linkpath">
        {linkpath.is}
      </span>
  }

  lazy val serverPath: String = {
    S.request match {
      case Full(r) => r.hostAndPath
      case _ => "add host name"
    }
  }

  private def getImageType = if (mimeType == ".jpeg" || mimeType == ".jpg") BufferedImage.TYPE_INT_RGB
  else BufferedImage.TYPE_INT_ARGB
}

