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
$output.java($entity.editForm)##

$output.require("javax.inject.Inject")##
$output.require($entity.model)##
$output.require($entity.root.primaryKey)##
$output.require($entity.repository)##
$output.require($WebModelSupport, "GenericEditForm")##

/**
 * View Helper/Controller to edit {@link ${entity.model.type}}.
 */
$output.dynamicAnnotationTakeOver("javax.inject.Named","${WebFaces.packageName}.ConversationContextScoped")##
public class $output.currentClass extends GenericEditForm<$entity.model.type, $entity.root.primaryKey.type> {
#if($output.requireFirstTime($entity.controller))
    @Inject
    protected $entity.controller.type $entity.controller.var;
#end
#foreach ($relation in $entity.xToOne.flatUp.list)
#if($output.requireFirstTime($relation.toEntity.controller))
    @Inject
    protected $relation.toEntity.controller.type $relation.toEntity.controller.var;
#end
$output.require($relation.toEntity.model)##
$output.require($relation.toEntity.root.primaryKey)##
$output.require($WebModelSupport, "GenericToOneAssociation")##
    protected GenericToOneAssociation<$relation.toEntity.model.type, $relation.toEntity.root.primaryKey.type> $relation.to.var;
#end
#foreach ($relation in $entity.xToMany.flatUp.list)
$output.require($relation.toEntity.model)##
#if($output.requireFirstTime($relation.toEntity.controller))
    @Inject
    protected $relation.toEntity.controller.type $relation.toEntity.controller.var;
#end
$output.require($relation.toEntity.model)##
$output.require($relation.toEntity.root.primaryKey)##
$output.require($WebModelSupport, "GenericToManyAssociation")##
    protected GenericToManyAssociation<$relation.toEntity.model.type, $relation.toEntity.root.primaryKey.type> $relation.to.vars;
#end
#if( $entity.xToMany.flatUp.size > 0)
$output.require($WebUtil, "TabBean")##
    protected TabBean tabBean = new TabBean();
#end
#if ($entity.xToOne.flatUp.size > 0 || $entity.xToMany.flatUp.size > 0)

$output.require($entity.graphLoader)##
    @Inject
    public ${output.currentClass}($entity.repository.type $entity.repository.var, $entity.graphLoader.type $entity.graphLoader.var) {
        super($entity.repository.var, $entity.graphLoader.var);
    }
#if( $entity.xToMany.flatUp.size > 0)

    /**
     * View helper to store the x-to-many associations tab's index. 
     */
    @Override
    public TabBean getTabBean() {
        return tabBean;
    }
#end
#else

    @Inject
    public ${output.currentClass}($entity.repository.type $entity.repository.var) {
        super($entity.repository.var);
    }
#end

    /**
     * The entity to edit/view.
     */
    public $entity.model.type ${entity.model.getter}() {
        return getEntity();
    }

    public String print() {
        return ${entity.controller.var}.print(${entity.model.getter}());
    }
#foreach ($relation in $entity.xToOne.flatUp.list)
$output.require("javax.annotation.PostConstruct")##

    @PostConstruct
    void setup${relation.to.varUp}Actions() {
$output.require($relation.toEntity.root.primaryKey)##
$output.requireMetamodel($entity.model)##
        $relation.to.var = new GenericToOneAssociation<$relation.toEntity.model.type, $relation.toEntity.root.primaryKey.type>($relation.toEntity.controller.var, ${entity.model.type}_.$relation.to.var) {
            @Override
            protected $relation.toEntity.model.type get() {
                return ${entity.model.getter}().${relation.to.getter}();
            }

            @Override
            protected void set($relation.toEntity.model.type $relation.toEntity.model.var) {
                ${entity.model.getter}().${relation.to.setter}($relation.toEntity.model.var);
            }
#if ($relation.isMandatory())
$output.require("javax.validation.constraints.NotNull")##

            @NotNull
            @Override
            public $relation.toEntity.model.type getSelected() {
                return super.getSelected();
            }
#end
        };
    }

    public GenericToOneAssociation<$relation.toEntity.model.type, $relation.toEntity.root.primaryKey.type> ${relation.to.getter}() {
        return $relation.to.var;
    }
#end
#foreach ($relation in $entity.xToMany.flatUp.list)
$output.require("javax.annotation.PostConstruct")##

    @PostConstruct
    void setup${relation.to.varsUp}Actions() {
$output.require($relation.toEntity.model)##
$output.requireMetamodel($entity.model)##
        $relation.to.vars = new GenericToManyAssociation<$relation.toEntity.model.type, $relation.toEntity.root.primaryKey.type>(${entity.model.getter}().${relation.to.getters}(), $relation.toEntity.controller.var, ${entity.model.type}_.$relation.to.vars) {
            @Override
            protected void remove($relation.toEntity.model.type $relation.toEntity.model.var) {
                ${entity.model.getter}().${relation.to.remover}($relation.toEntity.model.var);
            }

            @Override
            protected void add($relation.toEntity.model.type $relation.toEntity.model.var) {
#if ($relation.isOneToMany())
                ${entity.model.getter}().${relation.to.adder}($relation.toEntity.model.var);
#else
                // add the object only to the ${relation.to.var} side of the relation 
                ${entity.model.getter}().${relation.to.getters}().add($relation.toEntity.model.var);
#end
            }
#if ($relation.isOneToMany())

            @Override
            protected void onCreate($relation.toEntity.model.type $relation.to.var) {
                ${relation.to.var}.${relation.from.setter}(${entity.model.getter}()); // for display
            }
#end
        };
    }

    public GenericToManyAssociation<$relation.toEntity.model.type, $relation.toEntity.root.primaryKey.type> ${relation.to.getters}() {
        return $relation.to.vars;
    }
#end
}
