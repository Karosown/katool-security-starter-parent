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




import cn.katool.security.core.constant.KaSecurityAuthCheckMode;
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
        List<String> anyRole;
        List<String> anyPermission;

        List<String> mustRole;
        List<String> mustPermission;

        KaSecurityAuthCheckMode roleMode;
        KaSecurityAuthCheckMode permissionMode;

        List<Integer> logicIndex;
        public Route setLogicIndex(List<Integer> logicIndex) {
            this.logicIndex = logicIndex;
            return this;
        }
        public List<Integer> getLogicIndex() {
            return logicIndex;
        }
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


        public Route(String method, String url,String route, List<String> anyRole,List<String> anyPermissionCodes,List<String> mustRole,List<String> mustPermission) {
            this.method = method;
            this.url = url;
            this.route = route;
            this.anyRole = anyRole;
            this.anyPermission = anyPermissionCodes;
        }
        public Route(String method, String uri ,List<String> anyRole,List<String> anyPermission) {
            this.method = method;
            this.url = NetUtils.normalizeUrl(uri);
            this.anyRole = anyRole;
            this.anyPermission = anyPermission;
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

        public List<String> getAnyRole() {
            return this.anyRole;
        }

        public Route setAnyRole(List<String> role) {
            this.anyRole = role;
            return this;
        }
        public Route addAnyRole(String role){
            this.anyRole.add(role);
            return this;
        }


        public List<String> getAnyPermission() {
            return this.anyPermission;
        }

        public Route setAnyPermission(List<String> permission) {
            this.anyPermission = permission;
            return this;
        }
        public Route addAnyPermission(String permission){
            this.anyPermission.add(permission);
            return this;
        }

        public List<String> getMustRole() {
            return this.mustRole;
        }

        public Route setMustRole(List<String> role) {
            this.mustRole = role;
            return this;
        }
        public Route addMustRole(String role){
            this.mustRole.add(role);
            return this;
        }
        public List<String> getMustPermission() {
            return this.mustPermission;
        }
        public Route setMustPermission(List<String> permission) {
            this.mustPermission = permission;
            return this;
        }
        public Route addMustPermission(String permission){
            this.mustPermission.add(permission);
            return this;
        }
        public String getRoute() {
            return route;
        }
        public Route setRoleMode(KaSecurityAuthCheckMode roleMode) {
            this.roleMode = roleMode;
            return this;
        }
        public Route setPermissionMode(KaSecurityAuthCheckMode permissionMode) {
            this.permissionMode = permissionMode;
            return this;
        }
        public Route setRoleMode(Integer roleMode){
            this.roleMode = 0==roleMode?KaSecurityAuthCheckMode.OR:KaSecurityAuthCheckMode.AND;
            return this;
        }
        public Route setPermissionMode(Integer permissionMode){
            this.permissionMode = 0==permissionMode?KaSecurityAuthCheckMode.OR:KaSecurityAuthCheckMode.AND;
            return this;
        }
        public String getMethod() {
            return method;
        }

        public String getUrl() {
            return url;
        }

        public KaSecurityAuthCheckMode getRoleMode() {
            return roleMode;
        }
        public KaSecurityAuthCheckMode getPermissionMode() {
            return permissionMode;
        }
    }
    Vector<Route> authRouteList=new Vector<>();
}
