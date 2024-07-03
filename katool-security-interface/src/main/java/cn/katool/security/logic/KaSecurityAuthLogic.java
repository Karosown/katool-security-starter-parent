package cn.katool.security.logic;

import cn.katool.Exception.ErrorCode;
import cn.katool.Exception.KaToolException;
import cn.katool.security.core.config.KaSecurityCoreConfig;
import cn.katool.security.core.config.KaSecurityModeConfig;
import cn.katool.security.core.constant.KaSecurityAuthCheckMode;
import cn.katool.security.core.constant.KaSecurityMode;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import cn.katool.security.starter.utils.DefaultKaSecurityAuthUtilInterface;
import com.alibaba.excel.util.ListUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.yaml.snakeyaml.introspector.PropertySubstitute;

import javax.management.MXBean;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public interface KaSecurityAuthLogic<T> extends  DefaultKaSecurityAuthUtilInterface<T>{

   List<String> getUserRoleList();
   List<String> getUserPermissionCodeList();

   default boolean caseAndValidMode(boolean any,boolean must,List<String> anyList,List<String> mustList,KaSecurityAuthCheckMode mode){
      boolean validRole;
      switch (mode){
         case OR:
            validRole = any || must;
            break;
         case AND:
            validRole = ((ObjectUtils.isEmpty(anyList) || any) && (ObjectUtils.isEmpty(mustList) || must));
            break;
         default:
            throw new KaToolException(ErrorCode.PARAMS_ERROR,"【KaTool-Security】请检测接口的roleMode鉴权的参数");
      }
      return validRole;
   }
   default KaSecurityValidMessage doAuth(List<String> anyRoleList, List<String> mustRoleList,
                                         List<String> anyPermissionCodeList, List<String> mustPermissionCodeList,
                                         KaSecurityAuthCheckMode roleMode,KaSecurityAuthCheckMode permissionMode){
      List<String> userRoleList = this.getUserRoleList();
      if (ObjectUtils.isEmpty(userRoleList)) {
         userRoleList = ListUtils.newArrayList();
      }
      // 取交集
      boolean validRole = false;
      boolean validAnyRole =   userRoleList.stream().filter(anyRoleList::contains).count() > 0;
      boolean validMustRole =  userRoleList.stream().filter(mustRoleList::contains).count() == mustRoleList.size();
      switch (roleMode){
         case OR:
            validRole = validAnyRole || validMustRole;
            break;
         case AND:
            validRole = ((ObjectUtils.isEmpty(anyRoleList) || validAnyRole) && (ObjectUtils.isEmpty(mustRoleList) || validMustRole));
            break;
         default:
            throw new KaToolException(ErrorCode.PARAMS_ERROR,"【KaTool-Security】请检测接口的roleMode鉴权的参数");
      }
      List<String> userPermissionCodeList = this.getUserPermissionCodeList();
      if (ObjectUtils.isEmpty(userPermissionCodeList)) {
         userPermissionCodeList = ListUtils.newArrayList();
      }
      boolean validPermission = false;
      boolean validAnyPermission =  userPermissionCodeList.stream().filter(anyPermissionCodeList::contains).count() > 0;
      boolean validMustPermission = userPermissionCodeList.stream().filter(mustPermissionCodeList::contains).count() == mustPermissionCodeList.size();
      switch (permissionMode){
         case OR:
            validPermission = validAnyRole || validMustPermission;
            break;
         case AND:
            validPermission = (ObjectUtils.isEmpty(anyPermissionCodeList) || validAnyPermission)
                    && (ObjectUtils.isEmpty(mustPermissionCodeList) || validMustPermission);
            break;
         default:
            throw new KaToolException(ErrorCode.PARAMS_ERROR,"【KaTool-Security】请检测接口的permissionMode鉴权的参数");
      }
      if (!validRole && !validPermission) {
         return KaSecurityValidMessage.noAuth();
      }
      return KaSecurityValidMessage.success();
   }

   default KaSecurityValidMessage doCheckLogin(Boolean onlyCheckLogin){
      if (!this.isLogin()){
         return KaSecurityValidMessage.unLogin();
      }
      if (onlyCheckLogin){
         // 仅检查登录
         return KaSecurityValidMessage.onlyLogin();
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
   static KaSecurityValidMessage ValidFilter(KaSecurityAuthLogic kaSecurityAuthLogic,
                                             List<String> anyRoleList,List<String> mustRoleList,
                                             List<String> anyPermissionCodeList,List<String> mustPermissionCodeList,
                                             Boolean onlyCheckLogin,KaSecurityAuthCheckMode roleMode,KaSecurityAuthCheckMode permissionMode){
      KaSecurityValidMessage message = kaSecurityAuthLogic.doCheckLogin(onlyCheckLogin);
      if (!KaSecurityValidMessage.success().equals(message)||KaSecurityValidMessage.onlyLogin().equals(message)){
         return message;
      }
      message = kaSecurityAuthLogic.doAuth(anyRoleList,mustRoleList,anyPermissionCodeList,mustPermissionCodeList,roleMode,permissionMode);
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
      if (KaSecurityMode.GATEWAY.equals(KaSecurityModeConfig.currentMode)) {
         Logger logger = Logger.getLogger(PropertySubstitute.class.getPackage().getName());
         logger.log(Level.WARNING, "KaSecurityMode.GATEWAY 模式下，网关层请使用KaSecurityAuthUtil来获取Token，我们不建议使用Request来获取");
      }
      return request;
   }


   @Bean
   void loadPlugin();

}
