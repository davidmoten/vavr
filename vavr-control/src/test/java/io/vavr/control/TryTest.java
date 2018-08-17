/* ____  ______________  ________________________  __________
 * \   \/   /      \   \/   /   __/   /      \   \/   /      \
 *  \______/___/\___\______/___/_____/___/\___\______/___/\___\
 *
 * Copyright 2014-2018 Vavr, http://vavr.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vavr.control;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class TryTest {

    // -- Testees

    private static final String SUCCESS_VALUE = "success";
    private static final Try<String> SUCCESS = Try.success(SUCCESS_VALUE);

    private static final Exception FAILURE_CAUSE = new IllegalStateException("failure");
    private static final Try<String> FAILURE = Try.failure(FAILURE_CAUSE);

    // ---- alternate errors, e.g. when chaining a failure

    private static final Error ERROR = new Error();

    // ---- unexpected behavior like running unexpected code

    private static final AssertionError ASSERTION_ERROR = new AssertionError("unexpected");

    // ---- rethrown fatal errors

    private static final LinkageError LINKAGE_ERROR = new LinkageError();

    private static final ThreadDeath THREAD_DEATH = new ThreadDeath();

    private static final VirtualMachineError VM_ERROR = new VirtualMachineError() {
        private static final long serialVersionUID = 1L;
    };

    // -- static .of(Callable)

    @Test
    void shouldCreateSuccessWhenCallingTryOfWithNullValue() {
        assertNotNull(Try.of(() -> null));
    }

    @Test
    void shouldCreateSuccessWhenCallingTryOfCallable() {
        assertTrue(Try.of(() -> SUCCESS_VALUE).isSuccess());
    }

    @Test
    void shouldCreateFailureWhenCallingTryOfCallable() {
        assertTrue(Try.of(() -> { throw FAILURE_CAUSE; }).isFailure());
    }

    @Test
    void shouldThrowNPEWhenCallingTryOfCallable() {
        assertEquals(
            assertThrows(NullPointerException.class, () -> Try.of(null)).getMessage(),
            "callable is null"
        );
    }

    @Test
    void shouldRethrowLinkageErrorWhenCallingTryOfCallable() {
        assertSame(
                assertThrows(LINKAGE_ERROR.getClass(), () -> Try.of(() -> { throw LINKAGE_ERROR; })),
                LINKAGE_ERROR
        );
    }

    @Test
    void shouldRethrowThreadDeathWhenCallingTryOfCallable() {
        assertSame(
                assertThrows(THREAD_DEATH.getClass(), () -> Try.of(() -> { throw THREAD_DEATH; })),
                THREAD_DEATH
        );
    }

    @Test
    void shouldRethrowVirtualMachoneErrorWhenCallingTryOfCallable() {
        assertSame(
                assertThrows(VM_ERROR.getClass(), () -> Try.of(() -> { throw VM_ERROR; })),
                VM_ERROR
        );
    }

    @Test
    void shouldBeIndistinguishableWhenCreatingFailureWithOfFactoryOrWithFailureFactory() {
        final Try<?> failure1 = Try.of(() -> { throw FAILURE_CAUSE; });
        final Try<?> failure2 = Try.failure(FAILURE_CAUSE);
        {
            final Throwable t = assertThrows(RuntimeException.class, failure1::get);
            assertEquals(t.getMessage(), "Failure.get()");
            assertSame(t.getCause(), FAILURE_CAUSE);
        }
        {
            final Throwable t = assertThrows(RuntimeException.class, failure2::get);
            assertEquals(t.getMessage(), "Failure.get()");
            assertSame(t.getCause(), FAILURE_CAUSE);
        }
        assertSame(failure1.getCause(), failure2.getCause());
        assertEquals(failure1.isFailure(), failure2.isFailure());
        assertEquals(failure1.isSuccess(), failure2.isSuccess());
        assertEquals(failure1, failure2);
        assertEquals(failure1.hashCode(), failure2.hashCode());
        assertEquals(failure1.toString(), failure2.toString());
    }

    @Test
    void shouldBeIndistinguishableWhenCreatingSuccessWithOfFactoryOrWithSuccessFactory() {
        final Try<?> success1 = Try.of(() -> SUCCESS_VALUE);
        final Try<?> success2 = Try.success(SUCCESS_VALUE);
        assertSame(success1.get(), success2.get());
        assertThrows(UnsupportedOperationException.class, success1::getCause);
        assertThrows(UnsupportedOperationException.class, success2::getCause);
        assertEquals(success1.isFailure(), success2.isFailure());
        assertEquals(success1.isSuccess(), success2.isSuccess());
        assertEquals(success1, success2);
        assertEquals(success1.hashCode(), success2.hashCode());
        assertEquals(success1.toString(), success2.toString());
    }

    // -- static .run(CheckedRunnable)

    @Test
    void shouldCreateSuccessWhenCallingTryRunCheckedRunnable() {
        assertTrue(Try.run(() -> {}).isSuccess());
    }

    @Test
    void shouldCreateFailureWhenCallingTryRunCheckedRunnable() {
        assertTrue(Try.run(() -> { throw ERROR; }).isFailure());
    }

    @Test
    void shouldThrowNPEWhenCallingTryRunCheckedRunnable() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> Try.run(null)).getMessage(),
                "runnable is null"
        );
    }

    @Test
    void shouldRethrowLinkageErrorWhenCallingTryRunCheckedRunnable() {
        assertSame(
                assertThrows(LINKAGE_ERROR.getClass(), () -> Try.run(() -> { throw LINKAGE_ERROR; })),
                LINKAGE_ERROR
        );
    }

    @Test
    void shouldRethrowThreadDeathWhenCallingTryRunCheckedRunnable() {
        assertSame(
                assertThrows(THREAD_DEATH.getClass(), () -> Try.run(() -> { throw THREAD_DEATH; })),
                THREAD_DEATH
        );
    }

    @Test
    void shouldRethrowVirtualMachineErrorWhenCallingTryRunCheckedRunnable() {
        assertSame(
                assertThrows(VM_ERROR.getClass(), () -> Try.run(() -> { throw VM_ERROR; })),
                VM_ERROR
        );
    }

    // -- static .success(Object)
    
    @Test
    void shouldCreateSuccessWithNullValue() {
        assertNotNull(Try.success(null));
    }

    @Test
    void shouldCreateSuccess() {
        assertNotNull(Try.success(SUCCESS_VALUE));
    }

    @Test
    void shouldVerifyBasicSuccessProperties() {
        final Try<?> success = Try.success(SUCCESS_VALUE);
        assertSame(success.get(), SUCCESS_VALUE);
        assertSame(
                assertThrows(UnsupportedOperationException.class, success::getCause).getMessage(),
                "Success.getCause()"
        );
        assertFalse(success.isFailure());
        assertTrue(success.isSuccess());
        assertEquals(success, SUCCESS);
        assertEquals(success.hashCode(), Objects.hashCode(SUCCESS_VALUE));
        assertEquals(success.toString(), "Success(" + SUCCESS_VALUE + ")");
    }

    // -- static .failure(Throwable)

    @Test
    void shouldCreateFailureWithNullValue() {
        assertNotNull(Try.failure(null));
    }

    @Test
    void shouldCreateFailure() {
        assertNotNull(Try.failure(FAILURE_CAUSE));
    }

    @Test
    void shouldVerifyBasicFailureProperties() {
        final Try<?> failure = Try.failure(FAILURE_CAUSE);
        assertSame(
                assertThrows(RuntimeException.class, failure::get).getCause(),
                FAILURE_CAUSE
        );
        assertSame(failure.getCause(), FAILURE_CAUSE);
        assertFalse(failure.isSuccess());
        assertTrue(failure.isFailure());
        assertEquals(failure, FAILURE);
        assertEquals(failure.hashCode(), Objects.hashCode(FAILURE_CAUSE));
        assertEquals(failure.toString(), "Failure(" + FAILURE_CAUSE + ")");
    }

    @Test
    void shouldRethrowLinkageErrorWhenCallingTryFailure() {
        assertSame(
                assertThrows(LINKAGE_ERROR.getClass(), () -> Try.failure(LINKAGE_ERROR)),
                LINKAGE_ERROR
        );
    }

    @Test
    void shouldRethrowThreadDeathWhenCallingTryFailure() {
        assertSame(
                assertThrows(THREAD_DEATH.getClass(), () -> Try.failure(THREAD_DEATH)),
                THREAD_DEATH
        );
    }

    @Test
    void shouldRethrowVirtualMachineErrorWhenCallingTryFailure() {
        assertSame(
                assertThrows(VM_ERROR.getClass(), () -> Try.failure(VM_ERROR)),
                VM_ERROR
        );
    }

    // -- .failed()

    @Test
    void shouldInvertSuccessByCallingFailed() {
        final Try<?> testee = SUCCESS.failed();
        assertTrue(testee.isFailure());
        assertEquals(testee.getCause().getClass(), UnsupportedOperationException.class);
        assertEquals(testee.getCause().getMessage(), "Success.failed()");
    }

    @Test
    void shouldInvertSuccessWithNullValueByCallingFailed() {
        assertNotNull(Try.success(null).failed());
    }

    @Test
    void shouldInvertFailureByCallingFailed() {
        assertEquals(FAILURE.failed(), Try.success(FAILURE_CAUSE));
    }

    @Test
    void shouldInvertFailureWithNullCauseByCallingFailed() {
        assertNotNull(Try.failure(null).failed());
    }

    // -- .filter(CheckedPredicate)

    @Test
    void shouldFilterMatchingPredicateOnFailure() {
        assertSame(FAILURE.filter(s -> true), FAILURE);
    }

    @Test
    void shouldFilterNonMatchingPredicateOnFailure() {
        assertSame(FAILURE.filter(s -> false), FAILURE);
    }

    @Test
    void shouldFilterWithExceptionOnFailure() {
        assertSame(FAILURE.filter(t -> { throw ERROR; }), FAILURE);
    }

    @Test
    void shouldFilterMatchingPredicateOnSuccess() {
        assertSame(SUCCESS.filter(s -> true), SUCCESS);
    }

    @Test
    void shouldFilterNonMatchingPredicateOnSuccess() {
        final Try<String> testee = SUCCESS.filter(s -> false);
        assertTrue(testee.isFailure());
        assertEquals(testee.getCause().getClass(), NoSuchElementException.class);
        assertEquals(testee.getCause().getMessage(), "Predicate does not hold for " + SUCCESS_VALUE);
    }

    @Test
    void shouldFilterWithExceptionOnSuccess() {
        final Try<String> testee = SUCCESS.filter(t -> { throw ERROR; });
        assertTrue(testee.isFailure());
        assertSame(testee.getCause(), ERROR);
    }

    @Test
    void shouldThrowNPEWhenFilteringFailureWithNullPredicate() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.filter(null)).getMessage(),
                "predicate is null"
        );
    }

    @Test
    void shouldThrowNPEWhenFilteringSuccessWithNullPredicate() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.filter(null)).getMessage(),
                "predicate is null"
        );
    }

    @Test
    void shouldFilterFailureWithNullCause() {
        assertNotNull(Try.failure(null).filter(x -> true));
    }

    @Test
    void shouldFilterSuccessWithNullValue() {
        assertNotNull(Try.success(null).filter(x -> true));
    }

    // -- .flatMap(CheckedFunction)

    @Test
    void shouldFlatMapSuccessToNull() {
        assertNull(SUCCESS.flatMap(ignored -> null));
    }

    @Test
    void shouldFlatMapToSuccessOnSuccess() {
        assertSame(SUCCESS.flatMap(ignored -> SUCCESS), SUCCESS);
    }

    @Test
    void shouldFlatMapToFailureOnSuccess() {
        assertSame(SUCCESS.flatMap(ignored -> FAILURE), FAILURE);
    }

    @Test
    void shouldFlatMapOnFailure() {
        assertSame(FAILURE.flatMap(ignored -> SUCCESS), FAILURE);
    }

    @Test
    void shouldCaptureExceptionWhenFlatMappingSuccess() {
        assertEquals(SUCCESS.flatMap(ignored -> { throw ERROR; }), Try.failure(ERROR));
    }

    @Test
    void shouldIgnoreExceptionWhenFlatMappingFailure() {
        assertSame(FAILURE.flatMap(ignored -> { throw ERROR; }), FAILURE);
    }

    @Test
    void shouldThrowNPEWhenFlatMappingFailureWithNullParam() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.flatMap(null)).getMessage(),
                "mapper is null"
        );
    }

    @Test
    void shouldThrowNPEWhenFlatMappingSuccessWithNullParam() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.flatMap(null)).getMessage(),
                "mapper is null"
        );
    }

    @Test
    void shouldFlatMapFailureWithNullCause() {
        assertNotNull(Try.failure(null).flatMap(x -> null));
    }

    @Test
    void shouldFlatMapSuccessWithNullValue() {
        assertSame(Try.success(null).flatMap(s -> SUCCESS), SUCCESS);
    }

    // -- .fold(Function, Function)

    @Test
    void shouldFoldFailureWhenCauseIsNull() {
        assertEquals(Try.failure(null).<Integer> fold(x -> 0, s -> 1).intValue(), 0);
    }

    @Test
    void shouldFoldSuccessWhenValueIsNull() {
        assertEquals(Try.success(null).<Integer> fold(x -> 0, s -> 1).intValue(), 1);
    }

    @Test
    void shouldFoldFailureToNull() {
        assertNull(FAILURE.<Object> fold(x -> null, s -> ""));
    }

    @Test
    void shouldFoldSuccessToNull() {
        assertNull(SUCCESS.<Object> fold(x -> "", s -> null));
    }

    @Test
    void shouldFoldAndReturnValueIfSuccess() {
        final int folded = SUCCESS.fold(x -> { throw ASSERTION_ERROR; }, String::length);
        assertEquals(folded, SUCCESS_VALUE.length());
    }

    @Test
    void shouldFoldAndReturnAlternateValueIfFailure() {
        final String folded = FAILURE.fold(x -> SUCCESS_VALUE, a -> { throw ASSERTION_ERROR; });
        assertEquals(folded, SUCCESS_VALUE);
    }

    @Test
    void shouldFoldAndThrowNPEOnWhenOnFailureFunctionIsNullIfSuccess() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.fold(null, Function.identity())).getMessage(),
                "onFailure is null"
        );
    }

    @Test
    void shouldFoldAndThrowNPEOnWhenOnFailureFunctionIsNullIfFailure() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.fold(null, Function.identity())).getMessage(),
                "onFailure is null"
        );
    }

    @Test
    void shouldFoldAndThrowNPEOnWhenOnSuccessFunctionIsNullIfSuccess() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.fold(Function.identity(), null)).getMessage(),
                "onSuccess is null"
        );
    }

    @Test
    void shouldFoldAndThrowNPEOnWhenOnSuccessFunctionIsNullIfFailure() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.fold(Function.identity(), null)).getMessage(),
                "onSuccess is null"
        );
    }

    // -- .get()

    @Test
    void shouldGetOnSuccessWhenValueIsNull() {
        assertNull(Try.success(null).get());
    }
    
    @Test
    void shouldThrowCauseWrappedInRuntimeExceptionWhenGetOnFailure() {
        final Throwable t = assertThrows(RuntimeException.class, FAILURE::get);
        assertEquals(t.getMessage(), "Failure.get()");
        assertSame(t.getCause(), FAILURE_CAUSE);
    }

    @Test
    void shouldThrowNullCauseWrappedInRuntimeExceptionWhenGetOnFailure() {
        final Throwable t = assertThrows(RuntimeException.class, () -> Try.failure(null).get());
        assertEquals(t.getMessage(), "Failure.get()");
        assertNull(t.getCause());
    }

    @Test
    void shouldGetOnSuccess() {
        assertEquals(SUCCESS.get(), SUCCESS_VALUE);
    }

    // -- .getCause()

    @Test
    void shouldGetCauseOnFailureWhenCauseIsNull() {
        assertNull(Try.failure(null).getCause());
    }
    
    @Test
    void shouldGetCauseOnFailure() {
        assertSame(FAILURE.getCause(), FAILURE_CAUSE);
    }

    @Test
    void shouldThrowWhenCallingGetCauseOnSuccess() {
        assertEquals(
                assertThrows(UnsupportedOperationException.class, SUCCESS::getCause).getMessage(),
                "Success.getCause()"
        );
    }

    // -- .getOrElse(Object)

    @Test
    void shouldReturnElseWhenOrElseOnFailure() {
        assertSame(FAILURE.getOrElse(SUCCESS_VALUE), SUCCESS_VALUE);
    }

    @Test
    void shouldGetOrElseOnSuccess() {
        assertSame(SUCCESS.getOrElse(null), SUCCESS_VALUE);
    }

    // -- .getOrElseGet(Supplier)

    @Test
    void shouldReturnElseWhenOrElseGetOnFailure() {
        assertSame(FAILURE.getOrElseGet(() -> SUCCESS_VALUE), SUCCESS_VALUE);
    }

    @Test
    void shouldOrElseGetOnSuccess() {
        assertSame(SUCCESS.getOrElseGet(() -> null), SUCCESS_VALUE);
    }

    // -- .getOrElseThrow(Function)

    @Test
    void shouldThrowOtherWhenGetOrElseThrowOnFailure() {
        assertSame(
                assertThrows(ERROR.getClass(), () -> FAILURE.getOrElseThrow(x -> ERROR)),
                ERROR
        );
    }

    @Test
    void shouldOrElseThrowOnSuccess() {
        assertSame(SUCCESS.getOrElseThrow(x -> null), SUCCESS_VALUE);
    }

    // -- .isFailure()

    @Test
    void shouldDetectFailureIfFailure() {
        assertTrue(FAILURE.isFailure());
    }

    @Test
    void shouldDetectNonFailureIfSuccess() {
        assertFalse(SUCCESS.isFailure());
    }

    // -- .isSuccess()

    @Test
    void shouldDetectSuccessIfSuccess() {
        assertTrue(SUCCESS.isSuccess());
    }

    @Test
    void shouldDetectNonSuccessIfSuccess() {
        assertFalse(FAILURE.isSuccess());
    }

    // -- .iterator()

    @Test
    void shouldReturnIteratorOfSuccess() {
        assertNotNull(SUCCESS.iterator());
    }

    @Test
    void shouldReturnIteratorOfFailure() {
        assertNotNull(FAILURE.iterator());
    }

    // -- .map(CheckedFunction)

    @Test
    void shouldMapOnFailure() {
        assertSame(FAILURE.map(s -> s + "!"), FAILURE);
    }

    @Test
    void shouldMapWithExceptionOnFailure() {
        assertSame(FAILURE.map(ignored -> { throw ERROR; }), FAILURE);
    }

    @Test
    void shouldMapOnSuccess() {
        assertEquals(SUCCESS.map(s -> s + "!"), Try.success(SUCCESS_VALUE + "!"));
    }

    @Test
    void shouldMapOnSuccessWhenValueIsNull() {
        assertEquals(Try.success(null).map(s -> s + "!"), Try.success("null!"));
    }

    @Test
    void shouldMapWithExceptionOnSuccess() {
        assertEquals(SUCCESS.map(ignored -> { throw ERROR; }), Try.failure(ERROR));
    }

    @Test
    void shouldThrowNPEWhenMappingFailureAndParamIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.map(null)).getMessage(),
                "mapper is null"
        );
    }

    @Test
    void shouldThrowNPEWhenMappingSuccessAndParamIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.map(null)).getMessage(),
                "mapper is null"
        );
    }

    // -- .mapFailure(CheckedFunction)

    @Test
    void shouldMapFailureOnFailure() {
        assertEquals(FAILURE.mapFailure(x -> ERROR), Try.failure(ERROR));
    }

    @Test
    void shouldMapFailureOnFailureWhenCauseIsNull() {
        assertEquals(Try.failure(null).mapFailure(x -> ERROR), Try.failure(ERROR));
    }

    @Test
    void shouldMapFailureWithExceptionOnFailure() {
        assertEquals(FAILURE.mapFailure(x -> { throw ERROR; }), Try.failure(ERROR));
    }

    @Test
    void shouldMapFailureOnSuccess() {
        assertSame(SUCCESS.mapFailure(x -> ERROR), SUCCESS);
    }

    @Test
    void shouldMapFailureWithExceptionOnSuccess() {
        assertSame(SUCCESS.mapFailure(x -> { throw ERROR; }), SUCCESS);
    }

    @Test
    void shouldThrowNPEWhenCallingMapFailureOnFailureAndParamIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.mapFailure(null)).getMessage(),
                "mapper is null"
        );
    }

    @Test
    void shouldThrowNPEWhenCallingMapFailureOnSuccessAndParamIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.mapFailure(null)).getMessage(),
                "mapper is null"
        );
    }

    // -- .onFailure(Consumer)

    @Test
    void shouldConsumeThrowableWhenCallingOnFailureGivenFailure() {
        final List<Throwable> sideEffect = new ArrayList<>();
        FAILURE.onFailure(sideEffect::add);
        assertEquals(sideEffect, Collections.singletonList(FAILURE_CAUSE));
    }

    @Test
    void shouldNotHandleUnexpectedExceptionWhenCallingOnFailureGivenFailure() {
        assertSame(
                assertThrows(ERROR.getClass(), () -> FAILURE.onFailure(ignored -> { throw ERROR; })),
                ERROR
        );
    }
    
    @Test
    void shouldDoNothingWhenCallingOnFailureGivenSuccess() {
        assertSame(SUCCESS.onFailure(x -> { throw ERROR; }), SUCCESS);
    }

    @Test
    void shouldThrowNPEWhenCallingOnFailureWithNullParamOnFailure() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.onFailure(null)).getMessage(),
                "action is null"
        );
    }
    
    @Test
    void shouldThrowNPEWhenCallingOnFailureWithNullParamOnSuccess() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.onFailure(null)).getMessage(),
                "action is null"
        );
    }

    // -- .onSuccess(Consumer)

    @Test
    void shouldConsumeValueWhenCallingOnSuccessGivenSuccess() {
        final List<String> sideEffect = new ArrayList<>();
        SUCCESS.onSuccess(sideEffect::add);
        assertEquals(sideEffect, Collections.singletonList(SUCCESS_VALUE));
    }

    @Test
    void shouldNotHandleUnexpectedExceptionWhenCallingOnSuccessGivenSuccess() {
        assertSame(
                assertThrows(ERROR.getClass(), () -> SUCCESS.onSuccess(ignored -> { throw ERROR; })),
                ERROR
        );
    }

    @Test
    void shouldDoNothingWhenCallingOnSuccessGivenFailure() {
        assertSame(FAILURE.onSuccess(x -> { throw ERROR; }), FAILURE);
    }

    @Test
    void shouldThrowNPEWhenCallingOnSuccessWithNullParamOnFailure() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.onSuccess(null)).getMessage(),
                "action is null"
        );
    }

    @Test
    void shouldThrowNPEWhenCallingOnSuccessWithNullParamOnSuccess() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.onSuccess(null)).getMessage(),
                "action is null"
        );
    }

    // -- .orElse(Callable)

    @Test
    void shouldReturnSelfOnOrElseIfSuccess() {
        assertSame(SUCCESS.orElse(() -> null), SUCCESS);
    }

    @Test
    void shouldReturnAlternativeOnOrElseIfFailure() {
        assertSame(FAILURE.orElse(() -> SUCCESS), SUCCESS);
    }

    @Test
    void shouldCaptureErrorOnOrElseIfFailure() {
        assertSame(FAILURE.orElse(() -> { throw ERROR; }).getCause(), ERROR);
    }

    @Test
    void shouldThrowNPEOnOrElseWithNullParameterIfSuccess() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.orElse(null)).getMessage(),
                "supplier is null"
        );
    }

    @Test
    void shouldThrowNPEOnOrElseWithNullParameterIfFailure() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.orElse(null)).getMessage(),
                "supplier is null"
        );
    }

    // -- .recover(Class, CheckedFunction)

    @Test
    void shouldRecoverWhenFailureMatchesExactly() {
        assertEquals(FAILURE.recover(FAILURE_CAUSE.getClass(), x -> SUCCESS_VALUE), SUCCESS);
    }

    @Test
    void shouldRecoverWhenFailureIsAssignableFrom() {
        assertEquals(FAILURE.recover(Throwable.class, x -> SUCCESS_VALUE), SUCCESS);
    }

    @Test
    void shouldNotRecoverWhenFailureIsNotAssignableFrom() {
        assertEquals(FAILURE.recover(VirtualMachineError.class, x -> SUCCESS_VALUE), FAILURE);
    }

    @Test
    void shouldRecoverWhenSuccess() {
        assertSame(SUCCESS.recover(Throwable.class, x -> null), SUCCESS);
    }

    @Test
    void shouldThrowNPEOnRecoverFailureWhenExceptionTypeIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.recover(null, x -> null)).getMessage(),
                "exceptionType is null"
        );
    }

    @Test
    void shouldThrowNPEOnRecoverFailureWhenRecoveryFunctionIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.recover(Error.class, null)).getMessage(),
                "recoveryFunction is null"
        );
    }

    @Test
    void shouldThrowNPEOnRecoverSuccessWhenExceptionTypeIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.recover(null, x -> null)).getMessage(),
                "exceptionType is null"
        );
    }

    @Test
    void shouldThrowNPEOnRecoverSuccessWhenRecoveryFunctionIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.recover(Error.class, null)).getMessage(),
                "recoveryFunction is null"
        );
    }

    // -- .recoverWith(Class, CheckedFunction)

    @Test
    void shouldRecoverWithWhenFailureMatchesExactly() {
        assertSame(FAILURE.recoverWith(FAILURE_CAUSE.getClass(), x -> SUCCESS), SUCCESS);
    }

    @Test
    void shouldRecoverWithSuccessWhenFailureIsAssignableFrom() {
        assertSame(FAILURE.recoverWith(Throwable.class, x -> SUCCESS), SUCCESS);
    }

    @Test
    void shouldRecoverWithFailureWhenFailureIsAssignableFrom() {
        final Try<String> failure = Try.failure(ERROR);
        assertSame(FAILURE.recoverWith(Throwable.class, x -> failure), failure);
    }

    @Test
    void shouldNotRecoverWithWhenFailureIsNotAssignableFrom() {
        assertSame(FAILURE.recoverWith(VirtualMachineError.class, x -> SUCCESS), FAILURE);
    }

    @Test
    void shouldRecoverWithWhenSuccess() {
        assertSame(SUCCESS.recoverWith(Throwable.class, x -> null), SUCCESS);
    }

    @Test
    void shouldThrowNPEOnRecoverWithFailureWhenExceptionTypeIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.recoverWith(null, x -> null)).getMessage(),
                "exceptionType is null"
        );
    }

    @Test
    void shouldThrowNPEOnRecoverWithFailureWhenRecoveryFunctionIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.recoverWith(Error.class, null)).getMessage(),
                "recoveryFunction is null"
        );
    }

    @Test
    void shouldThrowNPEOnRecoverWithSuccessWhenExceptionTypeIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.recoverWith(null, x -> null)).getMessage(),
                "exceptionType is null"
        );
    }

    @Test
    void shouldThrowNPEOnRecoverWithSuccessWhenRecoveryFunctionIsNull() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.recoverWith(Error.class, null)).getMessage(),
                "recoveryFunction is null"
        );
    }

    // -- .stream()

    // TODO

    // -- .toEither(Function)

    // TODO

    // -- .toOption()

    // TODO

    // -- .toOptional()

    @Test
    void shouldConvertFailureToOptional() {
        assertEquals(FAILURE.toOptional(), Optional.empty());
    }


    @Test
    void shouldConvertSuccessOfNonNullToOptional() {
        assertEquals(SUCCESS.toOptional(), Optional.of(SUCCESS_VALUE));
    }

    @Test
    void shouldConvertSuccessOfNullToOptional() {
        assertEquals(Try.success(null).toOptional(), Optional.empty());
    }

    // -- .transform(CheckedFunction, CheckedFunction)

    @Test
    void shouldTransformFailureWhenCauseIsNull() {
        assertSame(Try.failure(null).transform(x -> SUCCESS, s -> SUCCESS), SUCCESS);
    }

    @Test
    void shouldTransformSuccessWhenValueIsNull() {
        assertSame(Try.success(null).transform(x -> SUCCESS, s -> SUCCESS), SUCCESS);
    }

    @Test
    void shouldTransformFailureToNull() {
        assertNull(FAILURE.transform(x -> null, s -> SUCCESS));
    }

    @Test
    void shouldTransformSuccessToNull() {
        assertNull(SUCCESS.transform(x -> FAILURE, s -> null));
    }

    @Test
    void shouldTransformAndReturnValueIfSuccess() {
        final Try<Integer> transformed = SUCCESS.transform(x -> { throw ASSERTION_ERROR; }, s -> Try.success(s.length()));
        assertEquals(transformed, Try.success(SUCCESS_VALUE.length()));
    }

    @Test
    void shouldTransformAndReturnAlternateValueIfFailure() {
        final Try<String> transformed = FAILURE.transform(x -> SUCCESS, a -> { throw ASSERTION_ERROR; });
        assertSame(transformed, SUCCESS);
    }

    @Test
    void shouldTransformAndThrowNPEOnWhenOnFailureFunctionIsNullIfSuccess() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.transform(null, s -> SUCCESS)).getMessage(),
                "onFailure is null"
        );
    }

    @Test
    void shouldTransformAndThrowNPEOnWhenOnFailureFunctionIsNullIfFailure() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.transform(null, s -> SUCCESS)).getMessage(),
                "onFailure is null"
        );
    }

    @Test
    void shouldTransformAndThrowNPEOnWhenOnSuccessFunctionIsNullIfSuccess() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> SUCCESS.transform(x -> FAILURE, null)).getMessage(),
                "onSuccess is null"
        );
    }

    @Test
    void shouldTransformAndThrowNPEOnWhenOnSuccessFunctionIsNullIfFailure() {
        assertEquals(
                assertThrows(NullPointerException.class, () -> FAILURE.transform(x -> FAILURE, null)).getMessage(),
                "onSuccess is null"
        );
    }

    @Test
    void shouldTransformFailureAndCaptureException() {
        final Try<String> transformed = FAILURE.transform(x -> { throw ERROR; }, s -> SUCCESS);
        assertEquals(transformed, Try.failure(ERROR));
    }

    @Test
    void shouldTransformSuccessAndCaptureException() {
        final Try<String> transformed = SUCCESS.transform(x -> FAILURE, s -> { throw ERROR; });
        assertEquals(transformed, Try.failure(ERROR));
    }

    // -- Object.equals(Object)

    @Test
    void shouldEqualFailureIfObjectIsSame() {
        assertEquals(FAILURE, FAILURE);
    }

    @Test
    void shouldNotEqualFailureIfObjectIsNotSame() {
        assertNotEquals(Try.failure(new Error()), Try.failure(new Error()));
    }

    @Test
    void shouldEqualSuccessIfObjectIsSame() {
        assertEquals(SUCCESS, SUCCESS);
    }

    @Test
    void shouldEqualSuccessIfObjectIsNotSame() {
        assertEquals(Try.success(1), Try.success(1));
    }

    @Test
    void shouldNotEqualSuccessIfValueTypesDiffer() {
        assertNotEquals(Try.success(1), Try.success("1"));
    }

    // -- Object.hashCode()

    @Test
    void shouldHashFailure() {
        assertEquals(FAILURE.hashCode(), Objects.hashCode(FAILURE_CAUSE));
    }

    @Test
    void shouldHashFailureWithNullCause() {
        assertEquals(Try.failure(null).hashCode(), Objects.hashCode(null));
    }

    @Test
    void shouldHashSuccess() {
        assertEquals(SUCCESS.hashCode(), Objects.hashCode(SUCCESS_VALUE));
    }

    @Test
    void shouldHashSuccessWithNullValue() {
        assertEquals(Try.success(null).hashCode(), Objects.hashCode(null));
    }

    // -- Object.toString()

    @Test
    void shouldConvertFailureToString() {
        assertEquals(FAILURE.toString(), "Failure(" + FAILURE_CAUSE + ")");
    }

    @Test
    void shouldConvertFailureWithNullCauseToString() {
        assertEquals(Try.failure(null).toString(), "Failure(null)");
    }

    @Test
    void shouldConvertSuccessToString() {
        assertEquals(SUCCESS.toString(), "Success(" + SUCCESS_VALUE + ")");
    }

    @Test
    void shouldConvertSuccessWithNullValueToString() {
        assertEquals(Try.success(null).toString(), "Success(null)");
    }
}
