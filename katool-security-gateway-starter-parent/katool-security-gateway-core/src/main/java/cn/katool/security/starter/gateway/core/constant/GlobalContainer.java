/**
 * Title
 *
 * @ClassName: GlobalContainer
 * @Description:
 * @author: Karos
 * @date: 2023/5/27 21:04
 * @Blog: https://www.wzl1.top/
 */

package cn.katool.security.starter.gateway.core.constant;




import cn.katool.security.starter.gateway.utils.NetUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Vector;

public interface GlobalContainer {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class Route{
        String method;
        String url;
        String route;
        Boolean onlyCheckLogin;
        Boolean isDef;
        Boolean isOpen;
        List<String> role;
        List<String> permission;
        public Route setCheckLogin(Boolean onlyCheckLogin) {
            this.onlyCheckLogin = onlyCheckLogin;
            return this;
        }

        public Route setDef(Boolean def) {
            isDef = def;
            return this;
        }

        public Route setOpen(Boolean open) {
            isOpen = open;
            return this;
        }

        public Boolean getOnlyCheckLogin() {
            return onlyCheckLogin;
        }

        public Boolean getDef() {
            return isDef;
        }

        public Boolean getOpen() {
            return isOpen;
        }


        public Route(String method, String url,String route, List<String> role,List<String> permissionCodes) {
            this.method = method;
            this.url = url;
            this.route = route;
            this.role = role;
            this.permission = permissionCodes;
        }
        public Route(String method, String uri ,List<String> role,List<String> permissionCodes) {
            this.method = method;
            this.url = NetUtils.normalizeUrl(uri);
            this.role = role;
            this.permission = permissionCodes;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public void setUrl(String url) {
            this.url = url;
        }
        public void setUrl(String uri,String route){
            this.url = NetUtils.normalizeUrl(uri+route);
        }

        public List<String> getRole() {
            return role;
        }

        public Route setRole(List<String> role) {
            this.role = role;
            return this;
        }

        public Route addRole(String role){
            this.role.add(role);
            return this;
        }
        public String getMethod() {
            return method;
        }

        public String getUrl() {
            return url;
        }
    }
    Vector<Route> authRouteList=new Vector<>();
}
