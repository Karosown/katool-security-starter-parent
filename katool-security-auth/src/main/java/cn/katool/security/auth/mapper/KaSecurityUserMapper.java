package cn.katool.security.auth.mapper;

import cn.katool.security.auth.model.entity.KaSecurityUser;
import cn.katool.security.auth.model.graph.IncGraphNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 30398
* @description 针对表【ka_security_user】的数据库操作Mapper
* @createDate 2023-12-28 00:41:40
* @Entity cn.katool.security.auth.model.entity.KaSecurityUser
*/
public interface KaSecurityUserMapper extends BaseMapper<KaSecurityUser> {

    List<IncGraphNode> getAllByCreateTimeIncGraphs(@Param("num") Integer num, @Param("dateUnit") String dateUnit);
}




