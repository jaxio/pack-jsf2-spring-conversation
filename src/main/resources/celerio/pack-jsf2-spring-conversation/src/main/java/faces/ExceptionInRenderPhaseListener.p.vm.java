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
$output.java($WebFaces, "ExceptionInRenderPhaseListener")##

$output.require("java.io.IOException")##

$output.require("javax.faces.context.ExceptionHandler")##
$output.require("javax.faces.context.ExternalContext")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.faces.event.PhaseEvent")##
$output.require("javax.faces.event.PhaseId")##
$output.require("org.slf4j.Logger")##
$output.require("org.slf4j.LoggerFactory")##
$output.require("org.omnifaces.eventlistener.DefaultPhaseListener")##
$output.require("com.google.common.collect.Iterables")##
$output.require($WebConversation, "ConversationManager")##

/**
 * Phase listener to handle exception during RENDER_RESPONSE phase.<br>
 * In other phases, exceptions are handled by factory.exception-handler-factory in faces-config.xml.<br>
 * The exception handler defined in faces-config.xml is also used in this phase listener.<br>
 */
public class $output.currentClass extends DefaultPhaseListener {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(ExceptionInRenderPhaseListener.class);

    public ${output.currentClass}() {
        super(PhaseId.RENDER_RESPONSE);
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        ExceptionHandler exceptionHandler = event.getFacesContext().getExceptionHandler();
        if(Iterables.isEmpty(exceptionHandler.getUnhandledExceptionQueuedEvents())){
            return;
        }
        
        try {
            new OptimisticLockExceptionHandler(exceptionHandler).handle();

            FacesContext faces = FacesContext.getCurrentInstance();
            String nextUrl = ConversationManager.getInstance().getCurrentConversation().nextUrl();

            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            String url = externalContext.getRequestContextPath() + nextUrl;
            faces.getExternalContext().redirect(url);
            faces.responseComplete();
        } catch (IOException e) {
            log.error("Failed to redirect to context page in render response phase", e);
        }
    }
}
