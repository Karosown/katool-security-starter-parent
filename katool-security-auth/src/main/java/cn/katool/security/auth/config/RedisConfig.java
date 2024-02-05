package cn.katool.security.auth.config;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class RedisConfig {


    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置value的序列化规则和 key的序列化规则

        RedisSerializer<Object> minxSerializer = new RedisSerializer<Object>() {
            @Override
            public byte[] serialize(Object o) throws SerializationException {
                if (o instanceof String) {
                    return ((String) o).getBytes();
                } else {
                    return jackson2JsonRedisSerializer.serialize(o);
                }
            }

            @Override
            public Object deserialize(byte[] bytes) throws SerializationException {
                if (bytes == null || bytes.length <= 0) {
                    return null;
                }
                String deserialize = new StringRedisSerializer().deserialize(bytes);
                if (JSONUtil.isTypeJSON(deserialize)) {
                    return jackson2JsonRedisSerializer.deserialize(bytes);

                }
                return deserialize;
            }
        };
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(minxSerializer);
        redisTemplate.setHashKeySerializer(minxSerializer);
        redisTemplate.setHashValueSerializer(minxSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
