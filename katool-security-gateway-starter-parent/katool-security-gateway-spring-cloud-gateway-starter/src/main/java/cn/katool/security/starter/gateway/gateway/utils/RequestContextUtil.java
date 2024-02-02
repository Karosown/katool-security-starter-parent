package cn.katool.security.starter.gateway.gateway.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RequestContextUtil {
    public static final Class<ServerHttpRequest> REQUEST_KEY = ServerHttpRequest.class;
    public static final Class<ServerWebExchange> EXCHANGE_KEY = ServerWebExchange.class;

    public static final Class<ServerHttpResponse> RESPONSE_KEY = ServerHttpResponse.class;

    /**
     * 获取 request
     * @return
     */
    public static Mono<ServerHttpRequest> getRequestMono() {
        return Mono.subscriberContext().map(ctx -> ctx.get(REQUEST_KEY));
    }

    public static Mono<ServerHttpResponse> getResponseMono() {
        return Mono.subscriberContext().map(ctx -> ctx.get(RESPONSE_KEY));
    }

    /**
     * 获取 exchange
     * @return
     */
    public static Mono<ServerWebExchange> getExchangeMono() {
        return Mono.subscriberContext().map(ctx -> ctx.get(EXCHANGE_KEY));
    }

    public static ServerHttpResponse getResponse() {
        Mono<ServerWebExchange> exchange = getExchangeMono();
        ServerHttpResponse response = exchange.flatMap(exg -> Mono.just(exg.getResponse())).block();
        return response;
    }

    public static ServerHttpRequest getRequest() {
        Mono<ServerWebExchange> exchange = getExchangeMono();
        ServerHttpRequest request = exchange.flatMap(exg -> Mono.just(exg.getRequest())).block();
        return request;
    }

}
