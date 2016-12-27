package hello;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "hello")
@Component
public class GreetingController {
    private final static Logger LOG = LogManager.getFormatterLogger();

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping(path = "/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        LOG.info("Receiving request for name: " + name);
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }
}