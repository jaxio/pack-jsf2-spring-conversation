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
$output.java($WebConversation, "ConversationManager")##

$output.requireStatic("com.google.common.collect.Maps.newTreeMap")##
$output.requireStatic($WebConversation, "ConversationHolder.setCurrentConversation")##
$output.require("java.util.Collection")##
$output.require("java.util.Comparator")##
$output.require("java.util.Map")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("javax.servlet.http.HttpSession")##
$output.require("org.omnifaces.util.Faces")##
$output.require("org.slf4j.Logger")##
$output.require("org.slf4j.LoggerFactory")##
$output.require("org.springframework.beans.BeansException")##
$output.require("org.springframework.context.ApplicationContext")##
$output.require("org.springframework.context.ApplicationContextAware")##

/**
 * The conversation manager is responsible for creating conversations, managing their lifecycle and calling the conversation listeners.
 */
#if($output.isAbstract())
// Add the following line in subclass :
// @Named
// @Singleton
// @Lazy(false)
// public class $output.currentRootClass extends ${output.currentRootClass}Base {
//    
//   private static $output.currentRootClass instance;
//   public static $output.currentRootClass getInstance() {
//        return instance;
//   }
//   public ${output.currentRootClass}() {
//        instance = this;
//   }
// }
 #else
 $output.dynamicAnnotationTakeOver("javax.inject.Named","javax.inject.Singleton","org.springframework.context.annotation.Lazy(false)")##
 #end
public class $output.currentClass implements ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(ConversationManager.class);
    private static final String CONVERSATION_MAP = "conversationMap";
#if(!$output.isAbstract())
    private static ${output.currentClass} instance;
    public static ${output.currentClass} getInstance() {
        return instance;
    }

#end

    /**
     * This method should be used only in the following cases: 1) from code having no spring awareness, like filters. 2) from code that are session scoped in
     * order to avoid serialization of the service. In other cases, please have the conversationManager injected normally.
     */
    
    private ApplicationContext applicationContext;
    private Collection<ConversationListener> conversationListeners;
    protected int maxConversations = 5;
#if(!$output.isAbstract())

    public ${output.currentClass}() {
            instance = this;
        }
#end

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * The maximum number of conversations a given user can open simultaneously.
     */
    public int getMaxConversations() {
        return maxConversations;
    }

    /**
     * Return the number of conversations for the current user 
     */
    public int getNbConversations() {
        return conversationMap().size();
    }

    /**
     * Whether the max number of conversations per user is reached. Used in createConversation (which has no FacesContext yet).
     */
    public boolean isMaxConversationsReached(HttpSession session) {
        return conversationMap(session).size() >= maxConversations;
    }

    /**
     * Returns the current conversation. Note that this method is mainly here so it can be used from the view.
     * Use directly ConversationHolder.getCurrentConversation() from Java code.
     */
    public Conversation getCurrentConversation() {
        return ConversationHolder.getCurrentConversation();
    }

    // --------------------------------------
    // Manage conversation lifecycle
    // --------------------------------------

    public Conversation beginConversation(ConversationContext<?> ctx) {
        HttpSession session = Faces.getSession();
        Map<String, Conversation> conversationMap = conversationMap(session);

        handleMaxConversationsReached(session, conversationMap);

        Conversation conversation = Conversation.newConversation(session, ctx);
        conversationMap.put(conversation.getId(), conversation);
        conversationCreated(conversation);
        conversation.pushNextContextIfNeeded();
        return conversation;
    }

    private void handleMaxConversationsReached(HttpSession session, Map<String, Conversation> conversationMap) {
        if (isMaxConversationsReached(session)) {
            // FIFO conversation eviction
            String keyToEvict = conversationMap.keySet().iterator().next();
            log.info("Max number of conversations ({}) reached. Evicting conversation {} using fifo policy", maxConversations, keyToEvict);
            conversationMap.remove(keyToEvict); // TODO: special treatment for evicted conversation?
        }
    }

    /**
     * Resume the {@link Conversation} having the passed id. Before resuming it, if a pending ConversationContext is present, 
     * it is pushed on the conversation contextes stack. 
     * @param id the id of the conversation to resume 
     * @param ccid the id of the conversation context that should be on top of the stack. 
     * @param request
     * @throws UnexpectedConversationException
     */
    public void resumeConversation(String id, String ccid, HttpServletRequest request) throws UnexpectedConversationException {
        Conversation conversation = conversationMap(request.getSession()).get(id);

        if (conversation != null) {
            conversation.pushNextContextIfNeeded();

            // compare the context id
            if (!conversation.getCurrentContext().getId().equals(ccid)) {
                conversation.handleOutOfSynchContext(ccid, request);
            }
            conversationResuming(conversation, request);
            setCurrentConversation(conversation);
        } else {
            throw new UnexpectedConversationException("No conversation found for id=" + id, request.getRequestURI(), "/home.faces");
        }
    }

    /**
     * Pause the current conversation. Before pausing it, pops the current context as needed.
     * In case all contextes are popped, then conversation is ended.
     */
    public void pauseCurrentConversation(HttpServletRequest request) {
        Conversation conversation = getCurrentConversation();

        // we check for not null because the conversation could have 
        // been ended during the current request.
        if (conversation != null) {
            // call order of 2 methods below is important as we want all the contextes (even the one we are about to be popped)
            // to be visible from the conversation listener.
            conversationPausing(conversation);
            conversation.popContextesIfNeeded();

            if (conversation.getConversationContextesCount() == 0) {
                // all was popped, we consider that this is the natural end of the conversation.
                endCurrentConversation(request.getSession());
            } else {
                setCurrentConversation(null);
            }
        }
    }

    /**
     * End the current Conversation. Invoked from place where no FacesContext is present.
     */
    public void endCurrentConversation(HttpSession session) {
        Conversation endedConversation = endCurrentConversationCommon();
        conversationMap(session).remove(endedConversation.getId());
    }

    /**
     * End the current Conversation. Requires a FacesContext to be present.
     */
    public void endCurrentConversation() {
        Conversation endedConversation = endCurrentConversationCommon();
        conversationMap().remove(endedConversation.getId());
    }

    private Conversation endCurrentConversationCommon() {
        Conversation conversation = getCurrentConversation();
        log.info("Ending conversation {}", conversation.getId());
        conversationEnding(conversation);
        setCurrentConversation(null);
        return conversation;
    }

    // --------------------------------------------
    // Impl details
    // --------------------------------------------    

    /**
     * Holds the current user's conversations.
     * Note: When calling this method, you must be sure that the FacesContext is present. For example, FacesContext is not present in ConversationFilter.
     */
    public Map<String, Conversation> conversationMap() {
        @SuppressWarnings("unchecked")
        Map<String, Conversation> map = (Map<String, Conversation>) sessionMap().get(CONVERSATION_MAP);
        if (map == null) {
            map = newConversationOrderedMap();
            sessionMap().put(CONVERSATION_MAP, map);
        }
        return map;
    }

    private Map<String, Conversation> conversationMap(HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<String, Conversation> map = (Map<String, Conversation>) session.getAttribute(CONVERSATION_MAP);
        if (map == null) {
            map = newConversationOrderedMap();
            session.setAttribute(CONVERSATION_MAP, map);
        }
        return map;
    }

    /**
     * Constructs an ordered map so we can evict conversation on a FIFO basis.
     * We rely on an Integer comparator as String comparison would not be exact.
     */
    private Map<String, Conversation> newConversationOrderedMap() {
        return newTreeMap(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
            }
        });
    }

    private Map<String, Object> sessionMap() {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
    }

    // --------------------------------------------
    // Support for conversation listeners
    // --------------------------------------------    

    private Collection<ConversationListener> getConversationListeners() {
        if (conversationListeners == null) {
            conversationListeners = applicationContext.getBeansOfType(ConversationListener.class).values();

        }
        return conversationListeners;
    }

    private void conversationCreated(Conversation conversation) {
        for (ConversationListener cl : getConversationListeners()) {
            cl.conversationCreated(conversation);
        }
    }

    private void conversationPausing(Conversation conversation) {
        for (ConversationListener cl : getConversationListeners()) {
            cl.conversationPausing(conversation);
        }
    }

    private void conversationResuming(Conversation conversation, HttpServletRequest request) {
        for (ConversationListener cl : getConversationListeners()) {
            cl.conversationResuming(conversation, request);
        }
    }

    private void conversationEnding(Conversation conversation) {
        for (ConversationListener cl : getConversationListeners()) {
            cl.conversationEnding(conversation);
        }
    }
}
