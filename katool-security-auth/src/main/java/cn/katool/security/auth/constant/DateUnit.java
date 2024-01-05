package cn.katool.security.auth.constant;

public enum DateUnit {
    DAYS("day"),
    WEEKS("week"),
    MONTHS("month"),
    YEARS("year");

    String value;

    DateUnit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
