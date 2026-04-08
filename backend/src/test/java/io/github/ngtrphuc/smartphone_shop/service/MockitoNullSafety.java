package io.github.ngtrphuc.smartphone_shop.service;

import static org.mockito.ArgumentMatchers.any;

import java.util.Objects;

import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

final class MockitoNullSafety {

    private MockitoNullSafety() {
    }

    @SuppressWarnings("null")
    static <T> T anyNonNull(Class<T> type) {
        return any(type);
    }

    @SuppressWarnings("null")
    static <T> T captureNonNull(ArgumentCaptor<T> captor) {
        return captor.capture();
    }

    static <T> T capturedValue(ArgumentCaptor<T> captor) {
        return Objects.requireNonNull(captor.getValue());
    }

    @SuppressWarnings("null")
    static <T> Iterable<T> nonNullIterable(Iterable<T> iterable) {
        return iterable;
    }

    static <T> Answer<T> returnsFirstArgument(Class<T> type) {
        return invocation -> Objects.requireNonNull(invocation.getArgument(0, type));
    }
}
