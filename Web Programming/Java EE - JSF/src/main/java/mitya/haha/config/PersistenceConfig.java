package mitya.haha.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.jndi.JndiTemplate;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:persistence-jndi.properties")
public class PersistenceConfig {

    @Autowired
    private Environment environment;

    @Bean()
    public LocalContainerEntityManagerFactoryBean entityManagerFactory()
            throws NamingException {
        LocalContainerEntityManagerFactoryBean entityManagerFactory
                = new LocalContainerEntityManagerFactoryBean();
        DataSource ds = dataSource();
        entityManagerFactory.setDataSource(dataSource());
       // entityManagerFactory.setJpaProperties(jpaProperties());
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
        //entityManagerFactory.setJpaDialect(jpaDialect());
        entityManagerFactory.setPackagesToScan("mitya.haha.model");
        return entityManagerFactory;
    }

    @Bean()
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.PostgreSQLDialect");
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        return vendorAdapter;
    }

    @Bean()
    public JpaDialect jpaDialect() {
        return new HibernateJpaDialect();
    }

    public Properties jpaProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
        properties.setProperty("hibernate.dialect", environment.getProperty("hibernate.dialect"));
        properties.setProperty("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
        properties.setProperty("hibernate.format_sql", environment.getProperty("hibernate.format_sql"));
        return properties;
    }

    @Bean
    public DataSource dataSource() throws NamingException {
        return (DataSource) new JndiTemplate().lookup(environment.getProperty("jdbc.url"));
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

}


