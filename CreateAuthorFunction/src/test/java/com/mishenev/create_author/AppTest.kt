package com.mishenev.create_author

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.tests.annotations.Event
import com.mishenev.create_author.db.AuthorRepository
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest

@ExtendWith(MockKExtension::class)
internal class AppTest {

    @MockK
    private lateinit var contextMock: Context

    @MockK
    private lateinit var authorRepositoryMock: AuthorRepository

    @InjectMockKs
    private lateinit var app: App

    @ParameterizedTest
    @Event(value = "test_event_template.json", type = SQSEvent::class)
    fun handleRequest(event: SQSEvent) {

        val createAuthorEvent1 = CreateAuthorEvent("George", "Orwell")
        val createAuthorEvent2 = CreateAuthorEvent("Sergei", "Dovlatov")
        val authorCreatedEvent1 = AuthorCreatedEvent(1L,"George", "Orwell")
        val authorCreatedEvent2 = AuthorCreatedEvent(2L,"Sergei", "Dovlatov")

        every { authorRepositoryMock.createNewAuthor(createAuthorEvent1) } answers {authorCreatedEvent1}
        every { authorRepositoryMock.createNewAuthor(createAuthorEvent2) } answers {authorCreatedEvent2}

        app.handleRequest(event, contextMock)

        verify(exactly = 1) { authorRepositoryMock.createNewAuthor(createAuthorEvent1) }
        verify(exactly = 1) { authorRepositoryMock.createNewAuthor(createAuthorEvent2) }
    }
}