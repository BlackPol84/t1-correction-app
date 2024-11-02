package ru.t1.correction.app.util;

import org.mockserver.client.MockServerClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
public abstract class AbstractIntegrationTestInitializer {

    protected static PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:11.13"))
                    .withDatabaseName("test")
                    .withUsername("test")
                    .withPassword("test")
                    .withExposedPorts(5432);

    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName
            .parse("mockserver/mockserver")
            .withTag("mockserver-" + MockServerClient.class.getPackage()
                    .getImplementationVersion());

    public static MockServerContainer mockServer = new MockServerContainer(MOCKSERVER_IMAGE);

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.url.account.unlock",
                () -> String.format("http://%s:%d/unlock", mockServer.getHost(), mockServer.getServerPort()));
    }

    static {
        container.start();
        mockServer.start();
    }
}
