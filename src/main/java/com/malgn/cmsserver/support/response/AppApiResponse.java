package com.malgn.cmsserver.support.response;

import com.malgn.cmsserver.support.exception.ErrorMessage;
import com.malgn.cmsserver.support.exception.ErrorType;

public record AppApiResponse<T>(
        ResultType result,
        T data,
        ErrorMessage error
) {

    public static AppApiResponse<Void> success() {
        return new AppApiResponse<>(ResultType.SUCCESS, null, null);
    }

    public static <S> AppApiResponse<S> success(S data) {
        return new AppApiResponse<>(ResultType.SUCCESS, data, null);
    }

    public static AppApiResponse<Void> error(ErrorType error) {
        return new AppApiResponse<>(ResultType.ERROR, null, ErrorMessage.of(error));
    }

    public static AppApiResponse<Void> error(ErrorType error, String message) {
        return new AppApiResponse<>(ResultType.ERROR, null, ErrorMessage.of(error, message));
    }
}
