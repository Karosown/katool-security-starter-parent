package cn.katool.security.auth.utils; /**
 * Title
 *
 * @ClassName: utils.cn.yb.thinktank.common.AesUtils
 * @Description:
 * @author: Karos
 * @date: 2023/3/26 23:26
 * @Blog: https://www.wzl1.top/
 */

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.symmetric.AES;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;

/**
 * 登陆密码加密
 */
@Slf4j
public class AesUtils {

    private static final String ALGORITHMS = "AES/ECB/PKCS5Padding";

    public static String touchUUid(String publicKey){
        String s = DigestUtil.md5Hex(String.valueOf(publicKey));
        return s.substring(0,16);
    }

    /**
     * 对明文加密
     *
     * @param content 明文
     * @param key     密钥
     * @return
     */
    public static String encrypt(String content, String key) {
        //加密为16进制表示
        return getAes(key).encryptHex(content);
    }

    /**
     * 对密文解密
     *
     * @param encryptContent 密文
     * @param key            密钥
     * @return
     */
    public static String decrypt(String encryptContent, String key) {
        // 解密为字符串
        return getAes(key).decryptStr(encryptContent, CharsetUtil.CHARSET_UTF_8);
    }

    @NotNull
    private static AES getAes(String key) {
        if (key != null && key.length() != 16) {
            throw new RuntimeException("密钥长度需为16位");
        }
        //构建
        return new AES(Mode.ECB, Padding.PKCS5Padding, key.getBytes());
    }


}
