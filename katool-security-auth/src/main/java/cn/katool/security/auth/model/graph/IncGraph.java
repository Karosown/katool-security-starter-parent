package cn.katool.security.auth.model.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class IncGraph {
    List<String> columns=new ArrayList<>();
    List<String> values=new ArrayList<>();
}
