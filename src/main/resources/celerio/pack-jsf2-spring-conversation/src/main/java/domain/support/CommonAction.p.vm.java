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
$output.java($WebModelSupport, "CommonAction")##

$output.requireStatic($WebConversation, "ConversationHolder.getCurrentConversation")##
$output.require($WebConversation, "ConversationContext")##
$output.require($WebConversation, "ConversationManager")##

/**
 * Simple actions that can be shared.
 */
public abstract class ${output.currentClass}<E> {

    /**
     * Back action is used from readonly page to go back. It is expected to be non-ajax.
     */
    public String back() {
        return context().getCallBack().back();
    }    

    /**
     * Ends the current conversation. It is expected to be non-ajax.
     */
    public String quit() {
        ConversationManager.getInstance().endCurrentConversation();
        return "/home.faces?faces-redirect=true"; // TODO: clean url, referer or else
    }    
    
    /**
     * Returns the current {@link ConversationContext}.
     */
    public ConversationContext<E> context() {
        return getCurrentConversation().getCurrentContext();
    }
}