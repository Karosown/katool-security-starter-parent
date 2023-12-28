package cn.katool.security.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class DeleteRequest {
    private String id;


    private List<String> ids;

    private static final long serialVersionUID = 1L;
}
