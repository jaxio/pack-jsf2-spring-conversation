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
$output.java($WebUtil, "MessageUtil")##

$output.requireStatic("javax.faces.application.FacesMessage.SEVERITY_ERROR")##
$output.requireStatic("javax.faces.application.FacesMessage.SEVERITY_FATAL")##
$output.requireStatic("javax.faces.application.FacesMessage.SEVERITY_INFO")##
$output.requireStatic("javax.faces.application.FacesMessage.SEVERITY_WARN")##
$output.require("javax.faces.application.FacesMessage")##
$output.require("javax.faces.application.FacesMessage.Severity")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.inject.Inject")##
$output.require("com.jaxio.jpa.querybyexample.Identifiable")##
$output.require($PrinterSupport, "TypeAwarePrinter")##
$output.require($Util, "ResourcesUtil")##

/**
 * Convenient bean to create JSF info/warn/error messages.

 * Business exceptions can be mapped to user friendly messages inside the {@link ${pound}error(Throwable)} method. 
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
public class $output.currentClass {
#if(!$output.isAbstract())
    private static ${output.currentClass} instance;
    public static ${output.currentClass} getInstance() {
        return instance;
    }

#end
    public static String toCssFriendly(Severity severity) {
        if (severity.equals(SEVERITY_INFO)) {
            return "info";
        } else if (severity.equals(SEVERITY_WARN)) {
            return "warn";
        } else if (severity.equals(SEVERITY_ERROR)) {
            return "error";
        } else if (severity.equals(SEVERITY_FATAL)) {
            return "fatal";
        }

        throw new IllegalStateException("Unexpected message severity: " + severity.toString());
    }

    @Inject
    private ResourcesUtil resourcesUtil;
    @Inject
    private TypeAwarePrinter printer;
#if(!$output.isAbstract())

    public ${output.currentClass}() {
        instance = this;
    }
#end

    // -- info

    public void info(String summaryKey, Object... args) {
        addFacesMessageUsingKey(SEVERITY_INFO, summaryKey, args);
    }

    public void infoEntity(String summaryKey, Identifiable<?> entity) {
        addFacesMessageUsingKey(SEVERITY_INFO, summaryKey, printer.print(entity));
    }

    public FacesMessage newInfo(String summaryKey, Object... args) {
        return newFacesMessageUsingKey(SEVERITY_INFO, summaryKey, args);
    }

    // -- warning

    public void warning(String summaryKey, Object... args) {
        addFacesMessageUsingKey(SEVERITY_WARN, summaryKey, args);
    }

    public FacesMessage newWarning(String summaryKey, Object... args) {
        return newFacesMessageUsingKey(SEVERITY_WARN, summaryKey, args);
    }

    // -- error

    public void error(String summaryKey, Object... args) {
        addFacesMessageUsingKey(SEVERITY_ERROR, summaryKey, args);
    }

    public FacesMessage newError(String summaryKey, Object... args) {
        return newFacesMessageUsingKey(SEVERITY_ERROR, summaryKey, args);
    }

    // -- fatal

    public void fatal(String summaryKey, Object... args) {
        addFacesMessageUsingKey(SEVERITY_FATAL, summaryKey, args);
    }

    public FacesMessage newFatal(String summaryKey, Object... args) {
        return newFacesMessageUsingKey(SEVERITY_FATAL, summaryKey, args);
    }

    private void addFacesMessage(FacesMessage fm) {
        if (fm != null) {
            FacesContext.getCurrentInstance().addMessage(null, fm);
        }
    }

    private void addFacesMessageUsingKey(Severity severity, String summaryKey, Object arg) {
        addFacesMessageUsingKey(severity, summaryKey, new Object[] {arg});
    }

    private void addFacesMessageUsingKey(Severity severity, String summaryKey, Object[] args) {
        addFacesMessage(newFacesMessageUsingKey(severity, summaryKey, args));
    }

    private FacesMessage newFacesMessageUsingKey(Severity severity, String summaryKey, Object[] args) {
        return newFacesMessageUsingText(severity, resourcesUtil.getProperty(summaryKey, args));
    }

    private FacesMessage newFacesMessageUsingText(Severity severity, String text) {
        FacesMessage fm = new FacesMessage(text);
        fm.setSeverity(severity);
        return fm;
    }
}