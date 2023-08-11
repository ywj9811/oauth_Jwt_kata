package code.kata.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) //Json 결과에서 Null은 빼고 나타남
public class SuccessResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> SuccessResponse<T> create(int code, String message) {
        return new SuccessResponse(code, message, null);
    }
    public static <T> SuccessResponse<T> create(int code, String message, T dto) {
        return new SuccessResponse(code, message, dto);
    }
}