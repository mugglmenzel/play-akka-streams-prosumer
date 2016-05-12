import org.scalatestplus.play._
import play.api.http.MediaType
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {

  "Routes" should {

    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }

  }

  "StreamController" should {

    "return a stream" in {
      val stream = route(app, FakeRequest(GET, "/source")).get

      status(stream) mustBe OK
      contentType(stream) mustBe Some(JSON)
    }

  }

}
