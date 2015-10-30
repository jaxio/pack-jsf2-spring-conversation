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
$output.java($entity.graphLoader)##
#if ($entity.xToOne.flatUp.isEmpty() && $entity.xToMany.flatUp.isEmpty())
$output.stopFileGeneration()##
#end

$output.require("javax.inject.Inject")##
$output.require($entity.model)##
$output.require($entity.repository)##
$output.require($entity.root.primaryKey)##
$output.require("com.jaxio.jpa.querybyexample.EntityGraphLoader")##

/**
 * Preloads the {@link $entity.model.type} associations required by the view layer.
 * 
 * Do not use other GraphLoaders in this GraphLoader. 
 */
$output.dynamicAnnotationTakeOver("javax.inject.Named","javax.inject.Singleton")##
public class $output.currentClass extends EntityGraphLoader<$entity.model.type, $entity.root.primaryKey.type> {
    // required by cglib to create a proxy around the object as we are using the @Transactional annotation
    protected ${output.currentClass}() {
        super();
    }

    @Inject
    public ${output.currentClass}(${entity.repository.type} ${entity.repository.var}) {
        super(${entity.repository.var});
    }

    @Override
    public void loadGraph($entity.model.type $entity.model.var) {
#foreach ($relation in $entity.xToOne.flatUp.list)
        loadSingular(${entity.model.var}.${relation.to.getter}());
#end
#foreach ($relation in $entity.xToMany.flatUp.list)
        loadCollection(${entity.model.var}.${relation.to.getters}());
#end
    }
}