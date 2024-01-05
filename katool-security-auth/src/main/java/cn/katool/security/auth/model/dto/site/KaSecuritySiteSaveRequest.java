package cn.katool.security.auth.model.dto.site;

import cn.katool.security.auth.model.entity.KaSecuritySite;
import lombok.Data;

import java.util.List;

@Data
public class KaSecuritySiteSaveRequest {
    List<KaSecuritySite> kaSecuritySiteList;
}
