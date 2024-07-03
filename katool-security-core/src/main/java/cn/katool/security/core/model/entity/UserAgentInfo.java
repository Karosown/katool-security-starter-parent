package cn.katool.security.core.model.entity;
import eu.bitwalker.useragentutils.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UserAgentInfo {

    String browserName;
    String browserType;
    String browserversion;
    String BrowserMajorVersion;
    String BrowserMinorVersion;
    String sysName;
    String deviceType;
    Boolean isMobile;


    public static UserAgentInfo convert(UserAgent userAgent){
        //TODO 转换userAgent信息
        Browser browser = userAgent.getBrowser();
        Version browserVersionObj = userAgent.getBrowserVersion();
        BrowserType browserTypeObj = browser.getBrowserType();
        OperatingSystem operatingSystem = userAgent.getOperatingSystem();
        DeviceType deviceTypeObj = operatingSystem.getDeviceType();
        String browserName = null;
        if (null != browser) {
            browserName = browser.getName();
        }
        String browserType         = null;
        String browserversion      = null;
        String BrowserMajorVersion = null;
        String BrowserMinorVersion = null;
        if (null!=browserVersionObj) {
            browserType = browserTypeObj.getName();
            browserversion = browserVersionObj.getVersion();
            BrowserMajorVersion = browserVersionObj.getMajorVersion();
            BrowserMinorVersion = browserVersionObj.getMinorVersion();
        }
        String sysName             = null;
        Boolean isMobile           = null;
        if (null != operatingSystem) {
            sysName = operatingSystem.getName();
            isMobile = operatingSystem.isMobileDevice();
        }
        String deviceType          = null;
        if (null != deviceTypeObj) {
            deviceType = deviceTypeObj.getName();
        }
        return new UserAgentInfo(browserName,browserType,browserversion,BrowserMajorVersion,BrowserMinorVersion,sysName,deviceType,isMobile);
    }
}
