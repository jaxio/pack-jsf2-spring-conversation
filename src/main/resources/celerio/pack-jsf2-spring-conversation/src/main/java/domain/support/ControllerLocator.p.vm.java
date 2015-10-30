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
$output.java($WebModelSupport, "ControllerLocator")##

$output.requireStatic("com.google.common.collect.Maps.newHashMap")##
$output.requireStatic("org.hibernate.proxy.HibernateProxyHelper.getClassWithoutInitializingProxy")##
$output.require("java.io.Serializable")##
$output.require("java.util.List")##
$output.require("java.util.Map")##
$output.require("javax.inject.Inject")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##

$output.dynamicAnnotationTakeOver("javax.inject.Named","javax.inject.Singleton")##
@SuppressWarnings("rawtypes")
public class ${output.currentClass} {

    private Map<Class, GenericController> permissions = newHashMap();

    @Inject
    void buildCache(List<GenericController> registredPermissions) {
        for (GenericController permission : registredPermissions) {
            permissions.put(permission.getRepository().getType(), permission);
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends Identifiable<? extends Serializable>> GenericController<E, ?> getController(E entity) {
        return (GenericController<E, ?>) permissions.get(getClassWithoutInitializingProxy(entity));
    }
}
