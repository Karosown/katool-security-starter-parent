package cn.katool.security.auth.model.graph;

import cn.katool.security.auth.model.vo.graph.AntVVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AntVNode{
    String id;
    Integer name;
    String label;

    List<AntVNode> children;
}