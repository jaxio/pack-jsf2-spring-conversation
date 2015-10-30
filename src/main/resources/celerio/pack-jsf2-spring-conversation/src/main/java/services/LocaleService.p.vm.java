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
$output.java($WebService, "LocaleService")##

$output.requireStatic($WebConversation, "ConversationHolder.getCurrentConversation")##
$output.requireStatic("java.util.Locale.ENGLISH")##
$output.requireStatic("java.util.Locale.FRENCH")##
$output.require("java.util.Locale")##
$output.require("javax.faces.context.ExternalContext")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.inject.Inject")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("javax.servlet.http.HttpServletResponse")##
$output.require("org.springframework.web.servlet.LocaleResolver")##
$output.require($Context, "LocaleHolder")##
$output.require($WebConversation, "Conversation")##
#if(!$output.isAbstract())
$output.requireStatic("org.springframework.web.context.WebApplicationContext.SCOPE_SESSION")##
$output.dynamicAnnotationTakeOver("javax.inject.Named", "org.springframework.context.annotation.Scope(SCOPE_SESSION)")##
#end
public class $output.currentClass {
    @Inject
    private LocaleResolver localeResolver;

    public String getLocale() {
        return LocaleHolder.getLocale().toString();
    }

    public String getLanguage() {
        return LocaleHolder.getLocale().getLanguage();
    }

    public String switchToFrench() {
        return switchToLocale(FRENCH);
    }

    public String switchToEnglish() {
        return switchToLocale(ENGLISH);
    }

    private String switchToLocale(Locale locale) {
        updateJsfLocale(locale);
        updateSpringLocale(locale);
        return redirectToSelf();
    }

    private String redirectToSelf() {
        Conversation currentConversation = getCurrentConversation();
        String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
        if (currentConversation != null) {
            return viewId + "?faces-redirect=true&_cid=" + currentConversation.getCid();
        } else {
            return viewId + "?faces-redirect=true";
        }
    }

    private void updateJsfLocale(Locale locale) {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
    }

    private void updateSpringLocale(Locale locale) {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        localeResolver.setLocale((HttpServletRequest) externalContext.getRequest(), (HttpServletResponse) externalContext.getResponse(), locale);
        LocaleHolder.setLocale(locale);
    }

    public boolean isFrench() {
        // check 'fr_FR' or simply 'fr'
        return FRENCH.equals(LocaleHolder.getLocale()) || FRENCH.getLanguage().equals(getLanguage());
    }
}
