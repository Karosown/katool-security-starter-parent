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
        String browserName         = browser.getName();
        String browserType         = browserTypeObj.getName();
        String browserversion      = browserVersionObj.getVersion();
        String BrowserMajorVersion = browserVersionObj.getMajorVersion();
        String BrowserMinorVersion = browserVersionObj.getMinorVersion();
        String sysName             = operatingSystem.getName();
        Boolean isMobile           = operatingSystem.isMobileDevice();
        String deviceType          = deviceTypeObj.getName();
        return new UserAgentInfo(browserName,browserType,browserversion,BrowserMajorVersion,BrowserMinorVersion,sysName,deviceType,isMobile);
    }
}
