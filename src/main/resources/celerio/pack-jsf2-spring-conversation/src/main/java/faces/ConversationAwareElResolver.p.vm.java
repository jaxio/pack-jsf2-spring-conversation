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
$output.java($WebFaces, "ConversationAwareElResolver")##

$output.requireStatic($WebConversation, "ConversationHolder.getCurrentConversation")##
$output.require("javax.el.ELContext")##
$output.require("javax.el.ELException")##
$output.require("org.springframework.web.jsf.el.SpringBeanFacesELResolver")##
$output.require($WebConversation, "Conversation")##

/**
 * ConversationAwareElResolver is declared in faces-config.xml.
 * It tries to find values in the current {@link ConversationContext}. 
 */
public class $output.currentClass extends SpringBeanFacesELResolver {

    @Override
    public Object getValue(ELContext elContext, Object base, Object property) throws ELException {
        if (base == null && property != null) {
            Conversation currentConversation = getCurrentConversation();
            if (currentConversation != null) {
                Object result = currentConversation.getVar(property.toString());
                if (result != null) {
                    elContext.setPropertyResolved(true);
                    return result;
                }
            }
        }

        return super.getValue(elContext, base, property);
    }

    @Override
    public Class<?> getType(ELContext elContext, Object base, Object property) throws ELException {
        if (base == null && property != null) {
            Conversation currentConversation = getCurrentConversation();
            if (currentConversation != null) {
                Object value = currentConversation.getVar(property.toString());
                if (value != null) {
                    elContext.setPropertyResolved(true);
                    return value.getClass();
                }
            }
        }

        return super.getType(elContext, base, property);
    }
}
