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
$output.java($WebFaces, "ConversationContextScope")##

$output.requireStatic($WebConversation, "ConversationHolder.getCurrentConversation")##
$output.require("org.springframework.beans.factory.ObjectFactory")##
$output.require("org.springframework.beans.factory.config.Scope")##
$output.require($WebConversation, "Conversation")##
$output.require($WebConversation, "ConversationContext")##

/**
 * Beans in the <code>conversationContext</code> scope reside in a {@link Conversation conversation}'s {@link ConversationContext}.
 * They are <code>visible</code> only when the conversation is bound to the current thread of execution and their 
 * hosting ConversationContext is on top of the conversation's contextes stack.
 * <p>
 * Such a design decision allows a conversation to have 2 <code>conversation scoped</code> beans with 
 * the same name (they just have to reside in 2 different ConversationContext).
 * This prevents bean name clash in complex navigation scenario within the same conversation.
 */
public class $output.currentClass implements Scope {

    @Override
    public String getConversationId() {
        return getCurrentConversation().getCid();
    }
 
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Conversation currentConversation = getCurrentConversation();
        Object bean = currentConversation.getCurrentContext().getBean(name, Object.class);
 
        if (bean == null) {
            bean = objectFactory.getObject();
            currentConversation.getCurrentContext().addBean(name, bean);
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
        return getCurrentConversation().getCurrentContext().getVar(key);
    }
}