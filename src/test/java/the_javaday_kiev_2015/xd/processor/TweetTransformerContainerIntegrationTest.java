/*
 * Copyright 2014 the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package the_javaday_kiev_2015.xd.processor;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.xd.dirt.server.singlenode.SingleNodeApplication;
import org.springframework.xd.dirt.test.SingleNodeIntegrationTestSupport;
import org.springframework.xd.dirt.test.SingletonModuleRegistry;
import org.springframework.xd.dirt.test.process.SingleNodeProcessingChain;
import org.springframework.xd.module.ModuleType;
import org.springframework.xd.test.RandomConfigurationSupport;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.springframework.xd.dirt.test.process.SingleNodeProcessingChainSupport.chain;

/**
 * Unit tests a module deployed to an XD single node container.
 */
public class TweetTransformerContainerIntegrationTest {

	private static int RECEIVE_TIMEOUT = 1000;
	private static String MODULE_NAME = "tweet-transformer";

	private static SingleNodeApplication application;

	/**
	 * Start the single node container, binding random unused ports, etc. to not conflict with any other instances
	 * running on this host. Configure the ModuleRegistry to include the project module.
	 */
	@BeforeClass
	public static void setUp() {
		RandomConfigurationSupport randomConfigSupport = new RandomConfigurationSupport();
		application = new SingleNodeApplication().run();
		SingleNodeIntegrationTestSupport singleNodeIntegrationTestSupport = new SingleNodeIntegrationTestSupport(application);
		singleNodeIntegrationTestSupport.addModuleRegistry(new SingletonModuleRegistry(ModuleType.processor, MODULE_NAME));
	}

	/**
	 * Each test creates a stream with the module under test, or in general a "chain" of processors. The
	 * SingleNodeProcessingChain is a test fixture that allows the test to send and receive messages to verify each
	 * message is processed as expected.
	 */
	@Test
	public void testTweetTransformerInXDContainer() throws Exception {
		String STREAM_NAME = "test_stream";
		String tweet = IOUtils.toString(TweetTransformerContainerIntegrationTest.class.getClassLoader().getResourceAsStream("tweet.json"), Charset.forName("UTF-8"));

		SingleNodeProcessingChain chain = chain(application, STREAM_NAME, MODULE_NAME);
		chain.sendPayload(tweet);
		String result = (String) chain.receivePayload(RECEIVE_TIMEOUT);
		System.out.println(result);

		assertEquals("TOPdesk (@TOPdesk) is hiring http://t.co/kzqGQqcZHc #javascript #java", result);
		chain.destroy();
	}

	@Test
	public void testTweetTransformerInXDContainerWithOptions() throws Exception {
		String STREAM_NAME = "test_stream";
		String tweet = IOUtils.toString(TweetTransformerContainerIntegrationTest.class.getClassLoader().getResourceAsStream("tweet.json"), Charset.forName("UTF-8"));

		String moduleDefinition = String.format("%s --extractField=%s", MODULE_NAME, "id_str");
		SingleNodeProcessingChain chain = chain(application, STREAM_NAME, moduleDefinition);
		chain.sendPayload(tweet);
		String result = (String) chain.receivePayload(RECEIVE_TIMEOUT);
		System.out.println(result);

		assertEquals("643115920458649600", result);
		chain.destroy();
	}

}
