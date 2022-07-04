package com.mishenev.post_book;

import com.mishenev.CreateBookRequest;
import com.mishenev.CreateBookRequestValidator;
import com.mishenev.post_book.db.BookRepository;
import com.mishenev.post_book.db.BookRepositoryJdbcImpl;
import com.mishenev.post_book.exception.ServiceException;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.util.json.Jackson;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CreateBookRequestValidator createBookRequestValidator;

    private final BookRepository bookRepository;

    public App() {
        this.createBookRequestValidator = new CreateBookRequestValidator();
        this.bookRepository = new BookRepositoryJdbcImpl();
    }

    /**
     * Constructor for unit testing. Allows test code to inject mocked services.
     * @param createBookRequestValidator Injected CreateBookRequestValidator object.
     * @param bookRepository Book repository abstraction.
     */
    public App(final CreateBookRequestValidator createBookRequestValidator,
               final BookRepository bookRepository) {
        this.createBookRequestValidator = createBookRequestValidator;
        this.bookRepository = bookRepository;
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent request,
                                                      final Context context) {
        final LambdaLogger logger = context.getLogger();

        final Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        final APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        CreateBookRequest createBookRequest;

        try {
            createBookRequest = Jackson.fromJsonString(request.getBody(), CreateBookRequest.class);
            createBookRequestValidator.validateRequest(createBookRequest);
        } catch (IllegalArgumentException | SdkClientException e) {
            logger.log("EXCEPTION: " + e);
            return response
                    .withBody(wrapInJson(e.getMessage()))
                    .withStatusCode(400);
        }

        logger.log("CREATE BOOK REQUEST: " + createBookRequest);

        try {
            bookRepository.createBook(createBookRequest);

            return response
                    .withStatusCode(200)
                    .withBody(request.getBody());

        } catch (ServiceException e) {
            logger.log("SERVICE EXCEPTION: " + e);
            return response
                    .withBody("{}")
                    .withStatusCode(500);
        }
    }

    private String wrapInJson(String string) {
        return "{ \"" + string  +"\" }";
    }
}
