package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import static com.google.common.base.Preconditions.checkArgument;

@SpringBootApplication
@ComponentScan(basePackages = {"hello", "aws", "service", "dao", "google", "web"})
public class Application {
    private final static Logger LOG = LogManager.getFormatterLogger();

    public static void main(String[] args) {
        checkArgument(Strings.isNotBlank(System.getProperty("googleApiKey")),
                "Google API key is not provided.");
        SpringApplication.run(Application.class, args);
    }

}