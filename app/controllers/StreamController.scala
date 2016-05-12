package controllers

import javax.inject._

import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.mugglmenzel.iot.model.ReadingProtos
import models.Reading
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.http.{HttpChunk, HttpEntity}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.{Json, Writes}
import play.api.mvc._

import scala.concurrent.Future

@Singleton
class StreamController @Inject() extends Controller {

  implicit val readingWrites = new Writes[Reading] {
    def writes(reading: Reading) = Json.obj(
      "metricId" -> reading.metricId,
      "timestamp" -> reading.timestamp
    )
  }

  def source = Action { implicit request =>
    Form(single("produce" -> optional(number))).bindFromRequest().fold(error => BadRequest,
      produce =>
        Ok.sendEntity(HttpEntity.Chunked(Source(1 to produce.getOrElse(99)).map { i =>
          lazy val json = Json.toJson(Reading("metric1", i)).toString()
          Future {
            Logger.info("returning: " + json.length + " bytes, as String: " + json)
          }
          HttpChunk.Chunk(ByteString(json))
        }, Some(JSON)))
    )
  }

  def protobufSource = Action { implicit request =>
    Form(single("produce" -> optional(number))).bindFromRequest().fold(error => BadRequest,
      produce =>
        Ok.sendEntity(HttpEntity.Chunked(Source(1 to produce.getOrElse(99)).map { i =>
          lazy val proto = ReadingProtos.ReadingProto.newBuilder().setMetricId("metric1").setTimestamp(i).build()
          Future {
            Logger.info("returning: " + proto.getSerializedSize + " bytes, as String: " + proto.toByteString.toStringUtf8)
          }
          HttpChunk.Chunk(ByteString(proto.toByteArray))
        }, Some(TEXT)))
    )
  }

}
