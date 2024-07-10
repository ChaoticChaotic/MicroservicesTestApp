package org.microservices;

import org.springframework.stereotype.Component;
import org.testcontainers.containers.PostgreSQLContainer;

@Component
public class AppPostgresqlContainer extends PostgreSQLContainer<AppPostgresqlContainer> {
    private static final String IMAGE_VERSION = "postgres:11.1";
    private static AppPostgresqlContainer container;

    public AppPostgresqlContainer() {
        super(IMAGE_VERSION);
    }

    public static AppPostgresqlContainer getInstance() {
        if (container == null) {
            container = new AppPostgresqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
