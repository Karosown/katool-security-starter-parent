package cn.katool.security.auth.service;

import cn.katool.security.auth.constant.DateUnit;
import cn.katool.security.auth.model.dto.user.KaSecurityUserQueryRequest;
import cn.katool.security.auth.model.entity.KaSecurityUser;
import cn.katool.security.auth.model.graph.IncGraphNode;
import cn.katool.security.auth.model.vo.kauser.KaSecurityLoginUserVO;
import cn.katool.security.auth.model.vo.kauser.KaSecurityUserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* @author 30398
* @description 针对表【ka_security_user】的数据库操作Service
* @createDate 2023-12-28 00:41:40
*/
public interface KaSecurityUserService extends IService<KaSecurityUser> {


    String getUUID(HttpServletRequest request);

    String decrypt(String kaSecurityEntryPassWord, String uuid);

    String generateKeyPairAndReturnPublicKey();

    String getUUID(String pub, String hexMsg);

    KaSecurityUserVO doLogin(String kaSecurityUserName, String kaSecurityUserPassWord, HttpServletRequest request, HttpServletResponse response);

    String getEntryPassWord(String kaSecurityUserPassWord);

    KaSecurityLoginUserVO getLoginKaSecurityUserVO(KaSecurityUser user);

    KaSecurityUserVO getKaSecurityUserVO(KaSecurityUser kaSecurityUser);
    List<KaSecurityUserVO> getKaSecurityUserVO(List<KaSecurityUser> userList);

    QueryWrapper<KaSecurityUser> getQueryWrapper(KaSecurityUserQueryRequest kaSecurityUserQueryRequest);

    KaSecurityUser querryGetKaSecurityUser(KaSecurityUser kaSecurityUser);
    List<IncGraphNode> getGraphIncData(Integer num, DateUnit dateUnit);

    boolean userLogout(String token);
}
