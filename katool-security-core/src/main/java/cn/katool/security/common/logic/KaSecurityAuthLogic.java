package cn.katool.security.common.logic;

import org.apache.commons.lang3.ObjectUtils;

import java.util.List;
import java.util.function.Function;

@FunctionalInterface
public interface KaSecurityAuthLogic {

   Boolean doAuth(List<String> roleList);

}
