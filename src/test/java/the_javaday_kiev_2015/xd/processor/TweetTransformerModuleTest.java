package the_javaday_kiev_2015.xd.processor;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author Oleg Tsal-Tsalko
 */
public class TweetTransformerModuleTest {

    private AnnotationConfigApplicationContext context;

    @Before
    public void bootstrapApplicationContext() throws Exception {
        context = new AnnotationConfigApplicationContext(TestConfiguration.class);
    }

    @Test
    public void testTransformerModule() throws Exception {
        MessageChannel inputChannel = context.getBean("input", MessageChannel.class);
        SubscribableChannel outputChannel = context.getBean("output", SubscribableChannel.class);

        CompletableFuture<String> result = new CompletableFuture<>();
        outputChannel.subscribe(message -> {result.complete((String) message.getPayload());});

        String tweet = IOUtils.toString(TweetTransformerContainerIntegrationTest.class.getClassLoader().getResourceAsStream("tweet.json"), Charset.forName("UTF-8"));
        inputChannel.send(new GenericMessage<String>(tweet));

        assertEquals("TOPdesk (@TOPdesk) is hiring http://t.co/kzqGQqcZHc #javascript #java", result.get(1, TimeUnit.SECONDS));
    }

    @Configuration
    @Import(ModuleConfiguration.class)
    static class TestConfiguration {
        @Bean
        public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }
}
