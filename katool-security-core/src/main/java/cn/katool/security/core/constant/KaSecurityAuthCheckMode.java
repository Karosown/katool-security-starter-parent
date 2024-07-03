package cn.katool.security.core.constant;


class KaSeucrityAuthCheckModeEntity{
    
}
public enum KaSecurityAuthCheckMode {
    OR(0),
    AND(1);

    private int mode;
    KaSecurityAuthCheckMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}