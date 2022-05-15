package com.st.workspace;

import com.github.cloudyrock.spring.v5.EnableMongock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMongock
@Slf4j
public class DecompressPayloadSyncApplication {

    public static void main(String[] args) {
        var env = SpringApplication.run(DecompressPayloadSyncApplication.class, args).getEnvironment();

        log.info("""

                        ----------------------------------------------------------
                        \tApplication '{}' is running!\s
                        \tAccess URL: http://localhost:{}
                        \tSwagger UI: http://localhost:{}/swagger-ui.html
                        \tProfile(s): {}
                        ----------------------------------------------------------""",
                env.getProperty("spring.application.name"),
                env.getProperty("server.port"),
                env.getProperty("server.port"),
                env.getActiveProfiles());
    }

}
