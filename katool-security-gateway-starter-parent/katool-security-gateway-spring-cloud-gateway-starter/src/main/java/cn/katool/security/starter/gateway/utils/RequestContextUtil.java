package cn.katool.security.starter.gateway.utils;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RequestContextUtil {
    public static final Class<ServerHttpRequest> REQUEST_KEY = ServerHttpRequest.class;
    public static final Class<ServerWebExchange> EXCHANGE_KEY = ServerWebExchange.class;

    /**
     * 获取 request
     * @return
     */
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.subscriberContext().map(ctx -> ctx.get(REQUEST_KEY));
    }

    /**
     * 获取 exchange
     * @return
     */
    public static Mono<ServerWebExchange> getExchange() {
        return Mono.subscriberContext().map(ctx -> ctx.get(EXCHANGE_KEY));
    }


}
