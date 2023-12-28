package cn.katool.security.auth.model.dto;

import cn.katool.security.core.constant.KaSecurityConstant;
import lombok.Data;

@Data
public class PageRequest {
    /**
     * 当前页号
     */
    private long current = 1;

    /**
     * 页面大小
     */
    private long pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = KaSecurityConstant.SORT_ORDER_ASC;

}
