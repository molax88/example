package io.hpb.contract.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynmicDataSource extends AbstractRoutingDataSource {
    /**
     * 返回的内容是targetDataSources 的Key
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DynmicDataSourceContextHolder.getDataSourceKey();
    }
}
