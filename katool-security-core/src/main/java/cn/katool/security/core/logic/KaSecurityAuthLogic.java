package cn.katool.security.core.logic;

import cn.katool.security.core.config.KaSecurityCoreConfig;
import cn.katool.security.core.constant.KaSecurityMode;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.yaml.snakeyaml.introspector.PropertySubstitute;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@FunctionalInterface
public interface KaSecurityAuthLogic{

   KaSecurityValidMessage doAuth(List<String> roleList,List<String> permissionCodeList);

   default KaSecurityValidMessage doCheckLogin(Boolean onlyCheckLogin){
      if (onlyCheckLogin){
         return KaSecurityValidMessage.unKnow().setMessage("请检查您 KaSecurityAuthLogic 实现类是否实现了onlyCheckLogin方法");
      }
      return KaSecurityValidMessage.success();
   }

   @Deprecated
   static KaSecurityValidMessage allValid(KaSecurityValidMessage[] messages){
      for (KaSecurityValidMessage message : messages) {
         if (!KaSecurityValidMessage.success().equals(message)||KaSecurityValidMessage.onlyLogin().equals(message)){
            return message;
         }
      }
      return KaSecurityValidMessage.success();
   }
   static KaSecurityValidMessage ValidFilter(KaSecurityAuthLogic kaSecurityAuthLogic,List<String> roleList,List<String> permissionCodeList,Boolean onlyCheckLogin){
      KaSecurityValidMessage message = kaSecurityAuthLogic.doCheckLogin(onlyCheckLogin);
      if (!KaSecurityValidMessage.success().equals(message)||KaSecurityValidMessage.onlyLogin().equals(message)){
         return message;
      }
      message = kaSecurityAuthLogic.doAuth(roleList,permissionCodeList);
      if (KaSecurityValidMessage.onlyLogin().equals(message)){
         throw new RuntimeException("请勿在 KaSecurityAuthLogic 实现类中 doAuth 方法返回 onlyLogin 状态");
      }
      if (!KaSecurityValidMessage.success().equals(message)){
         return message;
      }
      return KaSecurityValidMessage.success();
   }
   default HttpServletRequest getRequest(){
      HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
      if (KaSecurityMode.GATEWAY.equals(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER)) {
         Logger logger = Logger.getLogger(PropertySubstitute.class.getPackage().getName());
         logger.log(Level.WARNING, "KaSecurityMode.GATEWAY 模式下，网关层请使用KaSecurityAuthUtil来获取Token，我们不建议使用Request来获取");
      }
      return request;
   }

   default void loadPlugin(){
      return ;
   }

}
