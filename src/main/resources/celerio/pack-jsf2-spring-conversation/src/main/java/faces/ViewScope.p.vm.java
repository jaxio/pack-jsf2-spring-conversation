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
$output.java($WebFaces, "ViewScope")##

$output.require("java.util.Map")##
$output.require("javax.faces.context.FacesContext")##
$output.require("org.springframework.beans.factory.ObjectFactory")##
$output.require("org.springframework.beans.factory.config.Scope")##

public class ${output.currentClass} implements Scope {
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        Map<String, Object> viewMap = getViewMap();

        if (viewMap == null) {
            return objectFactory.getObject();
        } else if (viewMap.containsKey(name)) {
            return viewMap.get(name);
        } else {
            Object object = objectFactory.getObject();
            viewMap.put(name, object);
            return object;
        }
    }

    @Override
    public Object remove(String name) {
        Map<String, Object> viewMap = getViewMap();
        if (viewMap == null) {
            return null;
        }
        return viewMap.remove(name);
    }

    @Override
    public String getConversationId() {
        return null;
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    private Map<String, Object> getViewMap() {
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        if (currentInstance != null && currentInstance.getViewRoot() != null) {
            return currentInstance.getViewRoot().getViewMap();
        }
        return null;
    }
}