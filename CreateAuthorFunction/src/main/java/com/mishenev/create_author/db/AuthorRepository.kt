package com.mishenev.create_author.db

import com.mishenev.create_author.AuthorCreatedEvent
import com.mishenev.create_author.CreateAuthorEvent

interface AuthorRepository {

    fun createNewAuthor(createAuthorEvent: CreateAuthorEvent): AuthorCreatedEvent
}