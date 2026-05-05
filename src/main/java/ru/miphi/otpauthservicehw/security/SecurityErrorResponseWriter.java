package ru.miphi.otpauthservicehw.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.miphi.otpauthservicehw.dto.response.ErrorResponse;
import ru.miphi.otpauthservicehw.exception.ErrorType;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class SecurityErrorResponseWriter {

    ObjectMapper objectMapper;

    public void write(
            @Nonnull HttpServletResponse response,
            @Nonnull ErrorType errorType
    ) throws IOException {
        response.setStatus(errorType.getStatus().value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(UTF_8.name());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(errorType.getCode())
                .message(errorType.getMessage())
                .build();

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

}
