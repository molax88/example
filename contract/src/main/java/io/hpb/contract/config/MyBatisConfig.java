package io.hpb.contract.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
public class MyBatisConfig {

    @Autowired
    private DruidConfigProperties druidConfigProperties;
   /*@Autowired
    private PageHelperProperties pageHelperProperties;*/
   

    @Primary
    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource writeDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(druidConfigProperties.getDriverClassName());
        druidDataSource.setUrl(druidConfigProperties.getWriteUrl());
        druidDataSource.setUsername(druidConfigProperties.getWriteUsername());
        druidDataSource.setPassword(druidConfigProperties.getWritePassword());
        druidDataSource.setInitialSize(druidConfigProperties.getMinIdle());
        druidDataSource.setMinIdle(druidConfigProperties.getMinIdle());
        druidDataSource.setMaxActive(druidConfigProperties.getMaxActive());
        druidDataSource.setMaxWait(druidConfigProperties.getMaxWait());
        druidDataSource.setTimeBetweenEvictionRunsMillis(druidConfigProperties.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(druidConfigProperties.getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(druidConfigProperties.getValidationQuery());
        druidDataSource.setTestWhileIdle(druidConfigProperties.getTestWhileIdle());
        druidDataSource.setTestOnBorrow(druidConfigProperties.getTestOnBorrow());
        druidDataSource.setTestOnReturn(druidConfigProperties.getTestOnReturn());
        druidDataSource.setPoolPreparedStatements(druidConfigProperties.getPoolPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidConfigProperties.getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setFilters(druidConfigProperties.getFilters());
        return druidDataSource;
    }

    @Bean(initMethod = "init", destroyMethod = "close")
    public DataSource readDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setDriverClassName(druidConfigProperties.getDriverClassName1());
        druidDataSource.setUrl(druidConfigProperties.getReadUrl());
        druidDataSource.setUsername(druidConfigProperties.getReadUsername());
        druidDataSource.setPassword(druidConfigProperties.getReadPassword());
        druidDataSource.setInitialSize(druidConfigProperties.getMinIdle());
        druidDataSource.setMinIdle(druidConfigProperties.getMinIdle());
        druidDataSource.setMaxActive(druidConfigProperties.getMaxActive());
        druidDataSource.setMaxWait(druidConfigProperties.getMaxWait());
        druidDataSource.setTimeBetweenEvictionRunsMillis(druidConfigProperties.getTimeBetweenEvictionRunsMillis());
        druidDataSource.setMinEvictableIdleTimeMillis(druidConfigProperties.getMinEvictableIdleTimeMillis());
        druidDataSource.setValidationQuery(druidConfigProperties.getValidationQuery());
        druidDataSource.setTestWhileIdle(druidConfigProperties.getTestWhileIdle());
        druidDataSource.setTestOnBorrow(druidConfigProperties.getTestOnBorrow());
        druidDataSource.setTestOnReturn(druidConfigProperties.getTestOnReturn());
        druidDataSource.setPoolPreparedStatements(druidConfigProperties.getPoolPreparedStatements());
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(druidConfigProperties.getMaxPoolPreparedStatementPerConnectionSize());
        druidDataSource.setFilters(druidConfigProperties.getFilters());
        return druidDataSource;
    }

    @Bean(name = "hpbbcDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hpbbc")
    public DataSource hpbbcDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        return druidDataSource;
    }

    @Bean
    public DataSource dynmicDataSource() throws SQLException {
        DynmicDataSource dynmicDataSource = new DynmicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("writeDataSource", writeDataSource());
        targetDataSources.put("readDataSource", readDataSource());
        targetDataSources.put("hpbbc", hpbbcDataSource());
        dynmicDataSource.setTargetDataSources(targetDataSources);

        dynmicDataSource.setDefaultTargetDataSource(writeDataSource());
        return dynmicDataSource;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dynmicDataSource")DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:/mapper/*.xml"));
      /*  PageInterceptor pageInterceptor=new PageInterceptor();
        pageInterceptor.setProperties(pageHelperProperties.getProperties());
        sqlSessionFactoryBean.setPlugins(new Interceptor[]{pageInterceptor});*/

        return sqlSessionFactoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager(@Qualifier("dynmicDataSource")DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
