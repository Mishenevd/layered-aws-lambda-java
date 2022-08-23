package com.mishenev.post_book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mishenev.CreateBookRequestValidator;
import com.mishenev.post_book.db.BookRepository;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
import com.amazonaws.util.json.Jackson;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AppTest {

    @Mock
    Context contextMock;

    @Mock
    LambdaLogger loggerMock;

    @Mock
    BookRepository bookRepository;

    @Mock
    CreateBookRequestValidator createBookRequestValidatorMock;

    @InjectMocks
    App app;

    @BeforeEach
    public void beforeTest() {
        Mockito.clearInvocations(contextMock, loggerMock, bookRepository, createBookRequestValidatorMock);
    }

    @AfterEach
    public void afterTest() {
        Mockito.verifyNoMoreInteractions(contextMock, loggerMock, bookRepository, createBookRequestValidatorMock);
    }

    @ParameterizedTest
    @Event(value = "test_event_template.json", type = APIGatewayProxyRequestEvent.class)
    public void givenInvalidRequest_whenHandleRequest_thenReceiveBadRequest(APIGatewayProxyRequestEvent event) {
        // given
        IllegalArgumentException exception = new IllegalArgumentException("Create book request body is invalid");
        CreateBookRequest expectedRequest = Jackson.fromJsonString(event.getBody(), CreateBookRequest.class);
        when(contextMock.getLogger()).thenReturn(loggerMock);
        doThrow(exception)
                .when(createBookRequestValidatorMock).validateRequest(expectedRequest);

        // when
        APIGatewayProxyResponseEvent result = app.handleRequest(event, contextMock);

        // then
        assertEquals(400, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        assertEquals("{ \"Create book request body is invalid\" }", result.getBody());
        verify(contextMock).getLogger();
        verify(createBookRequestValidatorMock).validateRequest(expectedRequest);
        verify(loggerMock).log("EXCEPTION: " + exception);
    }

    @ParameterizedTest
    @Event(value = "test_event_template.json", type = APIGatewayProxyRequestEvent.class)
    public void givenValidRequest_whenHandleRequest_thenStoreBookAndReturnSuccess(APIGatewayProxyRequestEvent event) {
        // given
        CreateBookRequest expectedRequest = Jackson.fromJsonString(event.getBody(), CreateBookRequest.class);
        when(contextMock.getLogger()).thenReturn(loggerMock);

        // when
        APIGatewayProxyResponseEvent result = app.handleRequest(event, contextMock);

        // then
        assertEquals(200, result.getStatusCode().intValue());
        assertEquals("application/json", result.getHeaders().get("Content-Type"));
        assertEquals(event.getBody(), result.getBody());
        verify(contextMock).getLogger();
        verify(createBookRequestValidatorMock).validateRequest(eq(expectedRequest));
        verify(bookRepository).createBook(eq(expectedRequest));
        verify(loggerMock).log(matches("CREATE BOOK REQUEST: .+"));
    }
}
