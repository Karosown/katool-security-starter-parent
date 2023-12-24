package cn.katool.security.starter.gateway.gateway.filter;

import cn.katool.security.starter.gateway.utils.RequestContextUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ReactiveRequestContextFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        return chain.filter(exchange).subscriberContext(ctx -> {
            ctx.put(RequestContextUtil.REQUEST_KEY, request);
            ctx.put(RequestContextUtil.EXCHANGE_KEY, exchange);
            return ctx;
        });
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
