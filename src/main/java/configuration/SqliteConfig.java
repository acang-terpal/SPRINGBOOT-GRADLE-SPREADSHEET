package configuration;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "repository.sqlite", // Folder repo SQLite
        entityManagerFactoryRef = "sqliteEntityManager",
        transactionManagerRef = "sqliteTransactionManager"
)
public class SqliteConfig {

    @Primary
    @Bean
    @ConfigurationProperties("db1.datasource")
    public DataSource sqliteDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean sqliteEntityManager(EntityManagerFactoryBuilder builder, @Qualifier("sqliteDataSource") DataSource dataSource) {
        Map<String, Object> properties = new HashMap<>();
        // Tambahkan dialect SQLite karena Spring Boot tidak punya default auto-detect untuk SQLite
        properties.put("hibernate.dialect", "org.hibernate.community.dialect.SQLiteDialect");
        return builder
                .dataSource(dataSource)
                .packages("entity.sqlite") // Folder entity SQLite
                .persistenceUnit("sqlite")
                .properties(properties)
                .build();
    }

    @Primary
    @Bean
    public PlatformTransactionManager sqliteTransactionManager(
            @Qualifier("sqliteEntityManager") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
