package cn.katool.security.auth.model.dto;


import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class IncGraphRequest {
    @Range(min = 1,max = 62,message = "选择天数最多为62天")
    Integer day;
    @Range(min = 1,max = 8,message = "选择星期最多为8周")
    Integer week;
    @Range(min = 1,max = 12,message = "选择月份最多为12月")
    Integer month;
    @Range(min = 1,max = 20,message = "选择年份最多为20年")
    Integer year;
}
