package io.github.petty.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * AivenDataSourceConfig
 * 
 * Aiven MySQL에 저장된 Pet Tour Data를 Spring JPA를 통하여 연결
 * 
 * - DB 연결 설정
 * - JPA Entity 클래스 위치 지정
 * - JPA Repository 위치 지정
 * - 트랜잭션(읽기 전용)
 *
 */

@Configuration
// Aiven MySQL Repository 설정
@EnableTransactionManagement // @Transactional 동작 설정
@EnableJpaRepositories(
        basePackages = "io.github.petty.tour.repository",
        entityManagerFactoryRef = "aivenEntityManagerFactory",
        transactionManagerRef = "aivenTransactionManager"
)

public class AivenDataSourceConfig {
    /**
     * Aiven MySql 연결 DataSource 생성
     * yml에서 spring.datasource.aiven.* 가져와서 생성
     */
    @Primary
    @Bean(name="aivenDataSource")
    @ConfigurationProperties(prefix="spring.datasource.aiven")
    public DataSource aivenDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * JPA에서 사용할 EntityManagerFactory 설정
     * JPA DB 연동 - Entity 관리
     */
    @Primary
    @Bean(name="aivenEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean aivenEntityManagerFactory(
            // datasource 2개 이상일 경우 명시
            @Qualifier("aivenDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder
    ) {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        return builder.dataSource(dataSource)
                .packages("io.github.petty.tour.entity")
                .persistenceUnit("aiven") // 중복 X
                .properties(jpaProperties) // 대소문자 구분 X
                .build();
    }

    /**
     * Transaction Manager 설정
     */
    @Primary
    @Bean(name="aivenTransactionManager")
    public PlatformTransactionManager aivenTransactionManager(
            @Qualifier("aivenEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
