package randyumi.external.api

import org.scribe.builder.ServiceBuilder
import org.scribe.builder.api.TwitterApi
import org.scribe.model.{OAuthRequest, Token, Verb, Verifier}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import randyumi.external.api.exception.ExternalApiErrorException

import scala.util.control.NonFatal

case class TwitterApiConfig(clientKey: String, clientSecret: String)

case class OAuth10aAccessToken(
  token: String,
  secret: String
  )

object OAuth10aAccessToken {
  def convertToScribeAccessToken(token: OAuth10aAccessToken) = new Token(token.token, token.secret)
}

case class OAuth10aRequestToken(
  token: String,
  secret: String
)

case class OAuth10aVerifier(
  value: String
)

case class Tweet(id: String, description: String)

object Tweet {
  implicit val format = {
    val resp =
      (__ \ "id_str").format[String] and
        (__ \ "text").format[String]
    resp(this.apply _, unlift(this.unapply))
  }
}

class TwitterApiCaller(
  config: TwitterApiConfig
  ) {
  private val service = {
    val service = new ServiceBuilder()
      .provider(classOf[TwitterApi.SSL])
      .apiKey(config.clientKey)
      .apiSecret(config.clientSecret)
      .build()
    service
  }

  private val baseUrl = "https://api.twitter.com/1.1/"

  def getRequestToken: OAuth10aRequestToken = {
    val token = service.getRequestToken
    OAuth10aRequestToken(token.getToken, token.getSecret)
  }

  def getAuthenticationUrl(requestToken: OAuth10aRequestToken): String = {
    service.getAuthorizationUrl(new Token(requestToken.token, requestToken.secret))
  }

  def getAccessToken(requestToken: OAuth10aRequestToken, verifier: OAuth10aVerifier): OAuth10aAccessToken = {
    val token = service.getAccessToken(new Token(requestToken.token, requestToken.secret), new Verifier(verifier.value))
    OAuth10aAccessToken(token.getToken, token.getSecret)
  }


  def callTweetApi(message: String, accessToken: OAuth10aAccessToken): Unit = {
    if (message.length() >= 140) {
      throw new IllegalArgumentException("The message length must be shorter than 140.")
    }
    val request = new OAuthRequest(Verb.POST, baseUrl + "statuses/update.json")
    request.addBodyParameter("status", message)
    service.signRequest(OAuth10aAccessToken.convertToScribeAccessToken(accessToken), request)
    try {
      val res = request.send()
      if (res.getCode != 200) {
        throw new ExternalApiErrorException()
      }
    } catch {
      case NonFatal(e) => throw new ExternalApiErrorException("Exception is occurred while calling API.")
    }
  }

  def callSearchApi(query: String, accessToken: OAuth10aAccessToken): Seq[Tweet] = {
    if (query.length() >= 500) {
      throw new IllegalArgumentException("The message length must be shorter than 140.")
    }
    val request = new OAuthRequest(Verb.GET, baseUrl + "search/tweets.json")
    request.addQuerystringParameter("q", query)
    request.addQuerystringParameter("count", "100")
    request.addQuerystringParameter("result_type", "popular")
    service.signRequest(OAuth10aAccessToken.convertToScribeAccessToken(accessToken), request)
    val resp = request.send()
    (Json.parse(resp.getBody) \ "statuses").validate[Seq[Tweet]] match {
      case JsSuccess(e, _) => e
      case e: JsError => throw new ExternalApiErrorException(e.toString)
    }
  }

  def callRetweetApi(tweet: Tweet, accessToken: OAuth10aAccessToken): Unit = {
    val request = new OAuthRequest(Verb.POST, s"${baseUrl}/statuses/retweet/${tweet.id}.json")
    service.signRequest(OAuth10aAccessToken.convertToScribeAccessToken(accessToken), request)
    println(s"${baseUrl}1.1/statuses/retweet/${tweet.id}.json")
    val resp = request.send()
    println(resp.getBody)
  }
}
