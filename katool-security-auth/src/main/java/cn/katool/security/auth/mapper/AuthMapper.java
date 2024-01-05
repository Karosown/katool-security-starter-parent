package cn.katool.security.auth.mapper;

import cn.katool.security.auth.model.graph.IncGraphNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.katool.security.auth.model.entity.Auth;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 30398
* @description 针对表【auth】的数据库操作Mapper
* @createDate 2023-05-27 11:29:05
* @Entity cn.katool.security.auth.model.entity.Auth
*/
public interface AuthMapper extends BaseMapper<Auth> {

    List<IncGraphNode> getAllByCreateTimeIncGraphs(@Param("num") Integer num, @Param("dateUnit") String dateUnit);
}




