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
$output.java($WebFaces, "LogPhaseListener")##

$output.require("javax.faces.event.PhaseEvent")##
$output.require("javax.faces.event.PhaseId")##
$output.require("org.slf4j.Logger")##
$output.require("org.slf4j.LoggerFactory")##
$output.require("org.omnifaces.eventlistener.DefaultPhaseListener")##

/**
 * Simple phase listener logger. Convenient when learning/debugging JSF.
 * Must be activated in faces-config.xml
 */
public class $output.currentClass extends DefaultPhaseListener {
    private static final long serialVersionUID = 1L;
    private static final Logger log = LoggerFactory.getLogger(LogPhaseListener.class);
    
    public ${output.currentClass}() {
        super(PhaseId.ANY_PHASE);
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        log.debug("{}", event.getPhaseId());
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        log.debug("{}", event.getPhaseId());
    }
}
