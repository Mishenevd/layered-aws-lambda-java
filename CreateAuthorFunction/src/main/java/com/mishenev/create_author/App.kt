package com.mishenev.create_author

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mishenev.CreateAuthorEventValidator
import com.mishenev.create_author.db.AuthorRepository
import com.mishenev.create_author.db.AuthorRepositoryJdbcImpl

class App(private val authorRepository: AuthorRepository): RequestHandler<SQSEvent, Unit> {
    constructor(): this(AuthorRepositoryJdbcImpl())

    override fun handleRequest(input: SQSEvent, context: Context) {
        val logger = context.logger
        val mapper = jacksonObjectMapper()
        input.records
            .asSequence()
            .filterNotNull()
            .mapNotNull(SQSMessage::getBody)
            .map { mapper.readValue(it, CreateAuthorEvent::class.java) }
            .onEach {event -> logger.log(event.toString())}
            .onEach(CreateAuthorEventValidator::validate)
            .forEach(authorRepository::createNewAuthor)

        logger.log("Batch processed. Messages: " +
                input.records.map {it.messageId}.reduce {s1, s2 -> "$s1, $s2"})
    }
}
