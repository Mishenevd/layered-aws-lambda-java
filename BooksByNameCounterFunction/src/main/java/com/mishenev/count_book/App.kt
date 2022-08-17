package com.mishenev.count_book

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import software.amazon.awssdk.http.HttpStatusCode.*

class App(private val bookCounter: BookCounter):
    RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    constructor(): this(BookCounter())

    override fun handleRequest(request: APIGatewayProxyRequestEvent, context: Context): APIGatewayProxyResponseEvent {
        val logger = context.logger

        val headers: MutableMap<String, String> = HashMap()
        headers["Content-Type"] = "application/json"

        val response = APIGatewayProxyResponseEvent()
            .withHeaders(headers)

        val bookName = request.queryStringParameters?.let { it["bookName"] } ?: return response
            .withBody("{ error: \"bookName query string parameter is required\" }")
            .withStatusCode(BAD_REQUEST)

        val bookQuantity = try {
            bookCounter.count(bookName, logger)
        } catch (e: IllegalArgumentException) {
            return response
                .withBody("{ \"${e.message}\" }")
                .withStatusCode(BAD_REQUEST)
        } catch (e: Exception) {
            logger.log("SERVICE EXCEPTION: $e")
            return response
                .withBody("{}")
                .withStatusCode(INTERNAL_SERVER_ERROR)
        }
        logger.log("Book name request path variable: $bookName")

        return response
            .withStatusCode(OK)
            .withBody(bookQuantity.toString())
    }
}