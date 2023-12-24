package cn.katool.security.core.logic;

import cn.katool.security.core.model.entity.KaSecurityValidMessage;

import java.util.List;
@FunctionalInterface
public interface KaSecurityAuthLogic {

   KaSecurityValidMessage doAuth(List<String> roleList);
   default KaSecurityValidMessage checkLogin(Boolean checkLogin){
      if (checkLogin){
         return KaSecurityValidMessage.unKnow().setMessage("请检查您 KaSecurityAuthLogic 实现类是否实现了checkLogin方法");
      }
      return KaSecurityValidMessage.success();
   }

   static KaSecurityValidMessage allValid(KaSecurityValidMessage[] messages){
      for (KaSecurityValidMessage message : messages) {
         if (!KaSecurityValidMessage.success().equals(message)){
            return message;
         }
      }
      return KaSecurityValidMessage.success();
   }

}
