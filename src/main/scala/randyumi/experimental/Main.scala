package randyumi.experimental

import randyumi.external.api.{OAuth10aAccessToken, TwitterApiCaller, TwitterApiConfig}

class Main {
  val accToken = OAuth10aAccessToken("101978526-apYbNWjX1VfxZW0lRd8ON2tJODPwpbox73sYQjM3", "UIHPpXYCTZCMG5BVd9oSLZ8JHlwumw2UfBJwA2DapZwqp")
  val caller = m()
  def m() = {
    val caller = new TwitterApiCaller(TwitterApiConfig("rglA64nQgM3NP1oo9xh69lCGT", "NmGzOpbQCk8yQGuZrL7ZgGjd5IMw6w1rqlxS0gQpgFDlHFQ3v7"))
    caller
  }
}
