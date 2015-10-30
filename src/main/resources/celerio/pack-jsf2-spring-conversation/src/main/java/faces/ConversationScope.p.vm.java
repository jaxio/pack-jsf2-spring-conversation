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
$output.java($WebFaces, "ConversationScope")##

$output.requireStatic($WebConversation, "ConversationHolder.getCurrentConversation")##
$output.require("org.springframework.beans.factory.ObjectFactory")##
$output.require("org.springframework.beans.factory.config.Scope")##
$output.require($WebConversation, "Conversation")##

/**
 * Beans in the conversation scope reside in a {@link Conversation conversation}.
 * They are 'visible' only when the conversation is bound to the current thread of execution.
 */
public class $output.currentClass implements Scope {

    @Override
    public String getConversationId() {
        return getCurrentConversation().getId();
    }
 
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Conversation currentConversation = getCurrentConversation();
        Object bean = currentConversation.getBean(name);
 
        if (bean == null) {
            bean = objectFactory.getObject();
            currentConversation.addBean(name, bean);
        }

        return bean;
    }
 
    @Override
    public Object remove(String name) {
        throw new UnsupportedOperationException("remove is done during conversation.end");
    }
 
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        // no ops
    }
 
    @Override
    public Object resolveContextualObject(String key) {
        // TODO: is it really required?      
        throw new UnsupportedOperationException("remove is done during conversation.end");
    }
}