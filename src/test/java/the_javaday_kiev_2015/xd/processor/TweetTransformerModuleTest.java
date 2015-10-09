package the_javaday_kiev_2015.xd.processor;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author Oleg Tsal-Tsalko
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TweetTransformerModuleTest.TweetTransformerTestConfiguration.class)
@TestPropertySource("/test.properties")
public class TweetTransformerModuleTest {

    @Autowired
    @Qualifier("input")
    MessageChannel inputChannel;

    @Autowired
    @Qualifier("output")
    SubscribableChannel outputChannel;

    @Test
    public void testTransformerModule() throws Exception {
        CompletableFuture<String> result = new CompletableFuture<>();
        outputChannel.subscribe(message -> {
            result.complete((String) message.getPayload());
        });

        String tweet = IOUtils.toString(TweetTransformerContainerIntegrationTest.class.getClassLoader().getResourceAsStream("tweet.json"), Charset.forName("UTF-8"));
        inputChannel.send(new GenericMessage<String>(tweet));

        assertEquals("643115920458649600", result.get(1, TimeUnit.SECONDS));
    }

    @Configuration
    @Import(TweetTransformerModuleConfiguration.class)
    public static class TweetTransformerTestConfiguration {
        @Bean
        public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }
    }
}
