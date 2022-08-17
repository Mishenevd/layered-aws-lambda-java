package com.mishenev.count_book.db

interface BookRepository {

    fun countBooksByName(name: String): Long
}