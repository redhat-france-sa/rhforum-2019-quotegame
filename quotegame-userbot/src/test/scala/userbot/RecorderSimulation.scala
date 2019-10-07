package userbot

import scala.concurrent.duration._
import scala.util.Properties
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

	var quoteBaseURL = System.getProperty("quoteAPIURL")
	println("Got this base URL: " + quoteBaseURL)
	val httpProtocol = http
		.baseUrl(quoteBaseURL)
		//.proxy(Proxy("localhost", 4200).httpsPort(443))
		.inferHtmlResources(BlackList(), WhiteList())
		.acceptHeader("*/*")
		.contentTypeHeader("application/json")
		.userAgentHeader("insomnia/6.6.2")

	val headers_0 = Map("Proxy-Connection" -> "Keep-Alive")

	val scn = scenario("RecordedSimulation")
		.exec(http("request_0")
			.post("/api/user")
			.headers(headers_0)
			.body(RawFileBody("../resources/bodies/user.json"))) //create quote user 
		.pause(12)
		.exec(http("request_1")
			.post("/api/order")
			.headers(headers_0)
			.body(RawFileBody("../resources/bodies/buy-tyr.json"))) // buy 5 TYR stocks 
		.pause(3)
		.exec(http("request_2")
			.post("/api/order")
			.headers(headers_0)
			.body(RawFileBody("../resources/bodies/buy-cyb.json"))) // buy 5 CYB stocks
		.pause(7)
		.exec(http("request_3")
			.post("/api/order")
			.headers(headers_0)
			.body(RawFileBody("../resources/bodies/sell-cyb.json"))) // sell 5 CYB stocks
		.pause(9)
		.exec(http("request_4")
			.post("/api/order")
			.headers(headers_0)
			.body(RawFileBody("../resources/bodies/sell-tyr.json"))) // sell 5 TYR stocks
		.pause(20)
	setUp(scn.inject(
		constantUsersPerSec(20) during(15 seconds)
		)).protocols(httpProtocol)
}