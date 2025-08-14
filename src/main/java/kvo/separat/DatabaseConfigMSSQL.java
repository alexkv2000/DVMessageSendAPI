package kvo.separat;


//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(
////        basePackages = "kvo.separat", // укажите конкретный пакет для MSSQL репозиториев
////        entityManagerFactoryRef = "entityManagerFactoryMSSQL",
////        transactionManagerRef = "transactionManagerMSSQL"
//)
public class DatabaseConfigMSSQL {

//    @Bean
//    @ConfigurationProperties("spring.datasource.mssql") // исправлено на mssql
//    public DataSource dataSourceMSSQL() {
//        return DataSourceBuilder.create().type(HikariDataSource.class).build();
//    }
//
//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactoryMSSQL(
//            EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(dataSourceMSSQL())
//                .packages("kvo.separat") // укажите пакет с MSSQL сущностями
//                .persistenceUnit("mssqlPU")
//                .properties(jpaProperties())
//                .build();
//    }
//
//    @Bean
//    public PlatformTransactionManager transactionManagerMSSQL(
//            @Qualifier("entityManagerFactoryMSSQL") EntityManagerFactory entityManagerFactory) {
//        return new JpaTransactionManager(entityManagerFactory);
//    }
//
//    private Map<String, Object> jpaProperties() {
//        Map<String, Object> props = new HashMap<>();
//        props.put("hibernate.dialect", "org.hibernate.dialect.SQLServer2012Dialect");
//        props.put("hibernate.hbm2ddl.auto", "none");
//        return props;
//    }
}
