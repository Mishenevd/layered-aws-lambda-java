package com.mishenev.count_book

import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.mishenev.CountBookByNameRequestValidator
import com.mishenev.count_book.db.BookRepository
import com.mishenev.count_book.db.BookRepositoryJdbcImpl

class BookCounter(private val countBookByNameRequestValidator: CountBookByNameRequestValidator,
                  private val bookRepository: BookRepository) {

    constructor(): this(CountBookByNameRequestValidator(), BookRepositoryJdbcImpl())

    fun count(bookName: String, logger: LambdaLogger): Long {
        countBookByNameRequestValidator.validateRequest(bookName)
        logger.log("Book name request path variable: $bookName")
        return bookRepository.countBooksByName(bookName)
    }
}