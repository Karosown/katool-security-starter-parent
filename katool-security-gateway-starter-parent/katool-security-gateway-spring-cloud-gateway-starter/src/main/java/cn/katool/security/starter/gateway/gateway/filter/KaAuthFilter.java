package cn.katool.security.starter.gateway.gateway.filter;

import cn.katool.security.core.logic.KaToolSecurityAuthQueue;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import cn.katool.security.starter.gateway.utils.KaToolSecurityResultUtils;
import cn.katool.security.starter.gateway.core.constant.GlobalContainer;
import cn.katool.security.starter.gateway.core.constant.KaToolSecurityErrorCode;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class KaAuthFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 请求
        ServerHttpRequest request = exchange.getRequest();
        // 响应
        ServerHttpResponse response = exchange.getResponse();
        // 获取路由路径
        RequestPath path = request.getPath();
        String requestMethod = request.getMethod().name();
        for(GlobalContainer.Route v:GlobalContainer.authRouteList){
            String method = v.getMethod();
            String url = v.getUrl();
            log.info("url:{} path:{}",url,path);
            log.info("method:{} name:{}",method,requestMethod);
            if (url.equals(path.value())&&method.equals(requestMethod)){
                Boolean onlyCheckLogin = v.getOnlyCheckLogin();
                KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(v.getRole(),v.getPermission(),onlyCheckLogin);
                if(!KaSecurityValidMessage.success().equals(run)){
                    return handleNoAuth(response);
                }
                ServerHttpRequest build = request.mutate().headers(header-> header.add("Authed","KaTool-Security")).build();
                exchange=exchange.mutate().request(build).build();
                break;
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    private Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("处理完毕");
        try {
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("body instanceof Flux: {}", (body instanceof Flux));
                        if (body instanceof Flux) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);

                            // 往返回值里写数据
                            // 拼接字符串
                            return super.writeWith(
                                    fluxBody.map(dataBuffer -> {
                                        // 7. 调用成功，
                                        // 输出日志
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        DataBufferUtils.release(dataBuffer);//释放掉内存
                                        // 构建日志
                                        StringBuilder sb2 = new StringBuilder(200);
                                        List<Object> rspArgs = new ArrayList<>();
                                        rspArgs.add(originalResponse.getStatusCode());
                                        String data = new String(content, StandardCharsets.UTF_8); //data
                                        sb2.append(data);
                                        // 打印日志
                                        log.info("响应状态：{} 响应结果：{} 响应体：{}" , rspArgs , data , sb2);
                                        return bufferFactory.wrap(content);
                                    }));
                        } else {
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            }
            // 降级处理返回数据
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }
    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        log.error("无权限");
        return handleOther(HttpStatus.FORBIDDEN, KaToolSecurityResultUtils.error(KaToolSecurityErrorCode.NO_AUTH_ERROR),response);
    }
    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        log.error("调用出错");
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
    public Mono<Void> handleOther(HttpStatus httpStatus,Object o,ServerHttpResponse response) {
        log.error("httpCode:\n{} \nRestful:\n{}",httpStatus,o);
        response.setStatusCode(httpStatus==null?HttpStatus.FORBIDDEN:httpStatus);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        DataBuffer wrap = response.bufferFactory().wrap(JSON.toJSONString(o).getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(wrap));
    }
}
