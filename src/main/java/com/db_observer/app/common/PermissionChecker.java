package com.db_observer.app.common;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class PermissionChecker {

    public static void check(@NonNull final Boolean securityCheck, @NonNull final String accessDeniedMessage) {
        if (BooleanUtils.isFalse(securityCheck)) {
            throw new SecurityException(accessDeniedMessage);
        }
    }

    public static void check(@NonNull final Boolean securityCheck,
                             @NonNull final String accessDeniedMessage,
                             @NonNull final Function<String, ? extends RuntimeException> function) {
        RuntimeException ex = function.apply(accessDeniedMessage);
        if (BooleanUtils.isFalse(securityCheck)) {
            throw ex;
        }
    }

    public static void check(@NonNull final Boolean securityCheck, @NonNull final Supplier<RuntimeException> supplier) {
        RuntimeException ex = supplier.get();
        if (BooleanUtils.isFalse(securityCheck)) {
            throw ex;
        }
    }

    public static void checkNotBlank(@NonNull final String value, @NonNull final String fieldName) {
        check(StringUtils.isNotBlank(value), String.format("%s should not be blank!", fieldName));
    }

    public static void checkNotNull(final Object value, @NonNull final String message) {
        check(Objects.nonNull(value), message);
    }

    public static void checkNotEmpty(final Object[] array, @NonNull final String message) {
        check(ArrayUtils.isNotEmpty(array), message);
    }

}