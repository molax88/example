package io.hpb.contract.config;

public class DynmicDataSourceContextHolder {

    private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

    public static String getDataSourceKey() {
        return contextHolder.get();
    }

    public static void setDataSourceKey(String dataSourcekey) {
        contextHolder.set(dataSourcekey);
    }
}
