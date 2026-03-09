package com.malgn.cmsserver.support.response;

import com.malgn.cmsserver.support.exception.ErrorMessage;
import com.malgn.cmsserver.support.exception.ErrorType;

public record ApiResponse<T>(
        ResultType result,
        T data,
        ErrorMessage error
) {

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(ResultType.SUCCESS, null, null);
    }

    public static <S> ApiResponse<S> success(S data) {
        return new ApiResponse<>(ResultType.SUCCESS, data, null);
    }

    public static ApiResponse<Void> error(ErrorType error) {
        return new ApiResponse<>(ResultType.ERROR, null, ErrorMessage.of(error));
    }
}
