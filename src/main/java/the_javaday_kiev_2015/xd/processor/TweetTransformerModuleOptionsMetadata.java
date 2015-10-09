/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package the_javaday_kiev_2015.xd.processor;

import org.springframework.xd.module.options.spi.ModuleOption;

public class TweetTransformerModuleOptionsMetadata {

	private String extractField;

	//Makes it mandatory field if necessary
	//@NotNull
	public String getExtractField() {
		return extractField;
	}

	@ModuleOption("field to be extracted from tweet as result of transformation")
	public void setExtractField(String extractField) {
		this.extractField = extractField;
	}

}
