package com.ifc.populationorderdeterminant.utils;

public enum RegexEnum {

    FIND_LEFT_JOIN_PATTERN("(?i).*?left join %s\\b.*?"),
    FIND_VIEW_NAME_PATTERN("(?i)from %s.(.+?)\\b"),
    FIND_TABLE_NAME_PATTERN("(?i).*?\\b%s\\b.*?");

    private String value;

    RegexEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
