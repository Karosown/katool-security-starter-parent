package cn.katool.security;

import cn.katool.util.classes.ClassUtil;

public class Main {
    public static void main(String[] args) {
        ClassUtil classUtil = new ClassUtil();
        classUtil.complieClass(
                "G:/KaTool/katool-security-starter-parent/katool-security-plugin-genrous-demo/src/main/java/cn/katool/security",
                "AuthLogicPluginDemo.java"
        );
        // 将类文件填写后，可以直接生成class文件，生成后将放于target目录下
    }
}