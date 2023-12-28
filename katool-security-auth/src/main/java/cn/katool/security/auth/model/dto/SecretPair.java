package cn.katool.security.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SecretPair {
    String pub;
    String hexMsg;
}
