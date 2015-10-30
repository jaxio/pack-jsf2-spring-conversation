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
$output.java($WebConversationComponent, "ConversationMenu")##

$output.requireStatic("org.apache.commons.lang.StringUtils.isNotBlank")##
$output.require("javax.inject.Inject")##
$output.require("org.primefaces.model.menu.DefaultMenuItem")##
$output.require("org.primefaces.model.menu.DynamicMenuModel")##
$output.require("org.primefaces.model.menu.MenuModel")##
$output.require($WebConversation, "ConversationManager")##
$output.require($WebConversation, "Conversation")##

$output.dynamicAnnotationTakeOver("javax.inject.Named","javax.inject.Singleton")##
public class $output.currentClass {
    @Inject
    private ConversationManager conversationManager;

    public boolean getRender() {
        return !conversationManager.conversationMap().isEmpty();
    }

    public MenuModel getModel() {
        MenuModel model = new DynamicMenuModel();
        Conversation currentConversation = conversationManager.getCurrentConversation();
        for (Conversation conversation : conversationManager.conversationMap().values()) {
            DefaultMenuItem htmlMenuItem = new DefaultMenuItem();
            htmlMenuItem.setValue(conversation.getLabel());
            htmlMenuItem.setUrl(conversation.getUrl());
            if (currentConversation != null && currentConversation.getId().equals(conversation.getId())) {
                htmlMenuItem.setDisabled(true);
            }
            if (isNotBlank(conversation.getLabel())) {
                model.addElement(htmlMenuItem);
            }
        }
        return model;
    }
}
