package com.mishenev.create_author

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mishenev.CreateAuthorEventValidator
import com.mishenev.create_author.db.AuthorRepository

class App(private val authorRepository: AuthorRepository): RequestHandler<SQSEvent, Unit> {
    override fun handleRequest(input: SQSEvent, context: Context) {
        val mapper = jacksonObjectMapper()
        input.records
            .asSequence()
            .filterNotNull()
            .mapNotNull(SQSMessage::getBody)
            .map { mapper.readValue(it, CreateAuthorEvent::class.java) }
            .onEach(CreateAuthorEventValidator::validate)
            .forEach(authorRepository::createNewAuthor)
    }
}
