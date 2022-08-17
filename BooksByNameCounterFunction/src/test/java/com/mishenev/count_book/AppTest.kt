package com.mishenev.count_book

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.tests.annotations.Event
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import software.amazon.awssdk.http.HttpStatusCode
import software.amazon.awssdk.http.HttpStatusCode.OK
import kotlin.test.assertEquals

@ExtendWith(MockKExtension::class)
internal class AppTest {

    @MockK
    private lateinit var contextMock: Context

    @MockK
    private lateinit var loggerMock: LambdaLogger

    @MockK
    private lateinit var bookCounter: BookCounter

    @InjectMockKs
    private lateinit var app: App

    @BeforeEach
    fun reset() = clearAllMocks()

    @ParameterizedTest
    @Event(value = "test_event_template.json", type = APIGatewayProxyRequestEvent::class)
    fun handleRequest(event: APIGatewayProxyRequestEvent) {
        val testBookName = "testBookName"

        every { contextMock.logger } returns loggerMock
        every { bookCounter.count(testBookName, loggerMock) } returns 10L
        every {loggerMock.log(any() as String)} answers {}

        val result = app.handleRequest(event, contextMock)

        assertEquals(OK, result.statusCode)
        assertEquals("10", result.body)

        verify (exactly = 1) { contextMock.logger }
        verify (exactly = 1) { bookCounter.count(testBookName, loggerMock) }
    }

    @ParameterizedTest
    @Event(value = "test_event_template_without_book_name_param.json", type = APIGatewayProxyRequestEvent::class)
    fun requestWithoutTestBookNameParam_handleRequest_returnBadRequest(event: APIGatewayProxyRequestEvent) {

        every { contextMock.logger } returns loggerMock

        val response = app.handleRequest(event, contextMock)

        assertEquals(
            response.body,
            "{ error: \"bookName query string parameter is required\" }")
        assertEquals(HttpStatusCode.BAD_REQUEST, response.statusCode)
        verify (exactly = 1) { contextMock.logger }
    }

    @ParameterizedTest
    @Event(value = "test_event_template_without_any_query_param.json", type = APIGatewayProxyRequestEvent::class)
    fun requestWithoutAnyQueryParam_handleRequest_returnBadRequest(event: APIGatewayProxyRequestEvent) {

        every { contextMock.logger } returns loggerMock

        val response = app.handleRequest(event, contextMock)

        assertEquals(
            response.body,
            "{ error: \"bookName query string parameter is required\" }")
        assertEquals(HttpStatusCode.BAD_REQUEST, response.statusCode)
        verify (exactly = 1) { contextMock.logger }
    }

    @ParameterizedTest
    @Event(value = "test_event_template_invalid_book_name_param.json", type = APIGatewayProxyRequestEvent::class)
    fun invalidRequest_handleRequest_returnBadRequest(event: APIGatewayProxyRequestEvent) {
        val testExceptionDescription = "testExceptionDescription"

        every { contextMock.logger } returns loggerMock
        every { bookCounter.count(any(), loggerMock) } throws IllegalArgumentException(testExceptionDescription)

        val response = app.handleRequest(event, contextMock)

        assertEquals("{ \"$testExceptionDescription\" }", response.body)
        assertEquals(HttpStatusCode.BAD_REQUEST, response.statusCode)
        verify (exactly = 1) { contextMock.logger }
        verify(exactly = 1) {bookCounter.count(any(), loggerMock)}
    }
}