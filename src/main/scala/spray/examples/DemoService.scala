package spray.examples

import akka.actor.{ Props, Actor }
import spray.http._
import spray.http.MediaTypes._
import spray.routing._
import spray.http.BodyPart

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class DemoServiceActor extends Actor with DemoService  {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(demoRoute)
}

// this trait defines our service behavior independently from the service actor
trait DemoService extends HttpService {

  // we use the enclosing ActorContext's or ActorSystem's dispatcher for our Futures and Scheduler
  implicit def executionContext = actorRefFactory.dispatcher

  val demoRoute: Route = {
    get {
      path("") {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete(index)
        }
      }
    } ~
    path("file") {
      post {
        respondWithMediaType(`application/json`) {
          entity(as[MultipartFormData]) { formData =>
            detachTo(singleRequestServiceActor) {
              complete {
                val details = formData.fields.map {
                  case (name, BodyPart(entity, headers)) =>
                    val content = entity.buffer
                    val contentType = headers.find(h => h.is("content-type")).get.value
                    val fileName = headers.find(h => h.is("content-disposition")).get.value.split("filename=").last
                    val result = saveFile(fileName, content)
                    (contentType, fileName, result)
                  case _ =>
                }
                s"""{"status": "Processed POST request, details=$details" }"""
              }
            }
          }
        }
      }
    }
  }

  lazy val index =
    <html>
      <body>
        <h1>Spray file upload example.</h1>
      </body>
    </html>


  private def saveFile(fileName: String, content: Array[Byte]) = {
    val fos = new java.io.FileOutputStream(fileName)
    fos.write(content)
    fos.close()
  }

}