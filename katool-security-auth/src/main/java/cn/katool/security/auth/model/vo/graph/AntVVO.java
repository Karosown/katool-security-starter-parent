package cn.katool.security.auth.model.vo.graph;

import cn.katool.security.auth.model.graph.AntVEdge;
import cn.katool.security.auth.model.graph.AntVNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AntVVO{
    List<AntVNode> nodes;
    List<AntVEdge> edges;
}