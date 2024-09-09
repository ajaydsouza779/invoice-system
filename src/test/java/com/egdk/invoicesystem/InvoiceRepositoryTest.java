package com.egdk.invoicesystem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class InvoiceRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void contextLoads() throws Exception {
        assertThat(dataSource).isNotNull();
    }
}
