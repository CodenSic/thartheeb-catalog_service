package com.thartheeb.catalog.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class SuccessResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override public boolean supports(MethodParameter type, Class<? extends HttpMessageConverter<?>> converter) {
        return type.getParameterType() != String.class;
    }
    @Override public Object beforeBodyWrite(Object body, MethodParameter type, MediaType media,
        Class<? extends HttpMessageConverter<?>> converter, ServerHttpRequest request,
        ServerHttpResponse response) {
        int status = response instanceof ServletServerHttpResponse servlet
            ? servlet.getServletResponse().getStatus() : 200;
        String path = request.getURI().getPath();
        if (!json(media) || excluded(path) || status < 200 || status >= 300 || status == 204
            || body instanceof ProblemDetail || body instanceof SuccessResponse<?>) return body;
        return new SuccessResponse<>(200, body);
    }
    private static boolean json(MediaType type) { return type != null
        && (MediaType.APPLICATION_JSON.isCompatibleWith(type) || type.getSubtype().endsWith("+json")); }
    private static boolean excluded(String path) { return path.startsWith("/v3/api-docs")
        || path.startsWith("/swagger-ui") || path.startsWith("/actuator"); }
    public record SuccessResponse<T>(int status, T data) {}
}

@Component
final class SuccessResponseOpenApiCustomizer implements GlobalOpenApiCustomizer {
    @Override public void customise(OpenAPI openApi) {
        if (openApi.getPaths() == null) return;
        openApi.getPaths().values().forEach(path -> path.readOperations().forEach(operation ->
            operation.getResponses().forEach((code, response) -> {
                int status = parse(code);
                if (status < 200 || status >= 300 || status == 204 || response.getContent() == null) return;
                response.getContent().forEach((media, content) -> {
                    Schema<?> original = content.getSchema();
                    if (!media.contains("json") || original == null || wrapped(original)) return;
                    ObjectSchema envelope = new ObjectSchema();
                    envelope.setDescription("Uniform successful JSON response");
                    envelope.addProperty("status", new IntegerSchema().example(200));
                    envelope.addProperty("data", original);
                    envelope.addExtension("x-thartheeb-success-envelope", true);
                    content.setSchema(envelope);
                });
            })));
    }
    private static boolean wrapped(Schema<?> schema) { Map<String, Object> extensions = schema.getExtensions();
        return extensions != null && Boolean.TRUE.equals(extensions.get("x-thartheeb-success-envelope")); }
    private static int parse(String code) { try { return Integer.parseInt(code); }
        catch (NumberFormatException ignored) { return -1; } }
}
