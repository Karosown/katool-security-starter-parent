package cn.katool.security.auth.model.graph;

import lombok.Data;

import java.io.Serializable;

@Data
public class IncGraphNode implements Serializable {
    String column;

    String value;
    private static final long serialVersionUID = 1L;
}
