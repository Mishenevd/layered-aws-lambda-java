package com.mishenev.create_author

data class CreateAuthorEvent(val name: String, val surname: String)

data class AuthorCreatedEvent(val id: Long, val name: String, val surname: String)