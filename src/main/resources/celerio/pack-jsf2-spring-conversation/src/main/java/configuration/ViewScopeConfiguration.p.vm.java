## Copyright 2015 JAXIO http://www.jaxio.com
##
## Licensed under the Apache License, Version 2.0 (the "License");
## you may not use this file except in compliance with the License.
## You may obtain a copy of the License at
##
##    http://www.apache.org/licenses/LICENSE-2.0
##
## Unless required by applicable law or agreed to in writing, software
## distributed under the License is distributed on an "AS IS" BASIS,
## WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
## See the License for the specific language governing permissions and
## limitations under the License.
##
$output.java($WebConfiguration, "ViewScopeConfiguration")##

$output.requireStatic("com.google.common.collect.Maps.newHashMap")##
$output.require("java.util.Map")##
$output.require("org.springframework.beans.factory.config.CustomScopeConfigurer")##
$output.require("org.springframework.context.annotation.Bean")##
$output.require("org.springframework.context.annotation.Configuration")##
$output.require($WebFaces, "ConversationContextScope")##
$output.require($WebFaces, "ConversationScope")##
$output.require($WebFaces, "ViewScope")##

@Configuration
public class $output.currentClass {
    @Bean
    public static CustomScopeConfigurer viewScope() {
        CustomScopeConfigurer scope = new CustomScopeConfigurer();
        Map<String, Object> map = newHashMap();
        map.put("view", new ViewScope());
        map.put("conversationContext", new ConversationContextScope());
        map.put("conversation", new ConversationScope());
        scope.setScopes(map);
        return scope;
    }
}