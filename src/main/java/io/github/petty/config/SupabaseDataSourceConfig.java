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
 * SupabbaseDataSourceConfig
 *
 * Supabase PostgreSQL 저장된 Users, Community Spring JPA를 통하여 연결
 *
 * - DB 연결 설정
 * - JPA Entity 클래스 위치 지정
 * - JPA Repository 위치 지정
 * - 트랜잭션(읽기 전용)
 * - Users, Community 두 군데서 사용하므로 멀티 패키지 설정
 *
 */

@Configuration
// Supabase PostgreSQL Repository 설정
@EnableTransactionManagement // @Transactional 동작 설정
@EnableJpaRepositories(
        basePackages = {
                "io.github.petty.users.repository",
//                "io.github.petty.community.repository",
        },
        entityManagerFactoryRef = "supabaseEntityManagerFactory",
        transactionManagerRef = "supabaseTransactionManager"
)

public class SupabaseDataSourceConfig {
    /**
     * Supabase PostgreSQL 연결 DataSource 생성
     * yml에서 spring.datasource.supabase.* 가져와서 생성
     */
    @Bean(name="supabaseDataSource")
    @ConfigurationProperties(prefix="spring.datasource.supabase")
    public DataSource supabaseDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * JPA에서 사용할 EntityManagerFactory 설정
     * JPA DB 연동 - Entity 관리
     */
    @Bean(name="supabaseEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean supabaseEntityManagerFactory(
            // datasource 2개 이상일 경우 명시
            @Qualifier("supabaseDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder
    ) {
        Map<String, Object> jpaProperties = new HashMap<>();
        jpaProperties.put("hibernate.physical_naming_strategy", "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");

        // ddl.auto
        jpaProperties.put("hibernate.hbm2ddl.auto", "update");

        return builder.dataSource(dataSource)
                .packages(
                        "io.github.petty.users.entity"
//                        "io.github.petty.community.entity"
                ).persistenceUnit("supabase") // 중복 X
                .properties(jpaProperties) // 대소문자 구분 X
                .build();
    }

    /**
     * Transaction Manager 설정
     */
    @Bean(name="supabaseTransactionManager")
    public PlatformTransactionManager supabaseTransactionManager(
            @Qualifier("supabaseEntityManagerFactory") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
