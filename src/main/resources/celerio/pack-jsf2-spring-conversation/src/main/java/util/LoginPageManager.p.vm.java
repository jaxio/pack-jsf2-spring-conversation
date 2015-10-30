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
$output.java($WebUtil, "LoginPageManager")##

$output.require("javax.faces.context.ExternalContext")##
$output.require("javax.faces.context.FacesContext")##
$output.require("javax.inject.Inject")##
$output.require("javax.inject.Named")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("javax.servlet.http.HttpServletResponse")##
$output.require("org.omnifaces.util.Faces")##
$output.require("org.springframework.context.ApplicationEventPublisher")##
$output.require("org.springframework.context.ApplicationEventPublisherAware")##
$output.require("org.springframework.security.authentication.AccountExpiredException")##
$output.require("org.springframework.security.authentication.AccountStatusException")##
$output.require("org.springframework.security.authentication.BadCredentialsException")##
$output.require("org.springframework.security.authentication.CredentialsExpiredException")##
$output.require("org.springframework.security.authentication.DisabledException")##
$output.require("org.springframework.security.authentication.LockedException")##
$output.require("org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent")##
$output.require("org.springframework.security.core.Authentication")##
$output.require("org.springframework.security.core.AuthenticationException")##
$output.require("org.springframework.security.core.context.SecurityContext")##
$output.require("org.springframework.security.core.context.SecurityContextHolder")##
$output.require("org.springframework.security.core.userdetails.UsernameNotFoundException")##
$output.require("org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter")##
$output.require("org.springframework.security.web.authentication.session.SessionAuthenticationStrategy")##
$output.require("org.springframework.security.web.context.HttpRequestResponseHolder")##
$output.require("org.springframework.security.web.context.SecurityContextRepository")##

/**
 * Manages the login page, implementing Spring Security login page with JSF
 */
@Named
public class $output.currentClass implements ApplicationEventPublisherAware {

    @Inject
    private UsernamePasswordAuthenticationFilter usernamePasswordAuthenticationFilter;

    @Inject
    private SessionAuthenticationStrategy sessionAuthenticationStrategy;

    @Inject
    private SecurityContextRepository securityContextRepository;

    @Inject
    private MessageUtil messageUtil;

    private ApplicationEventPublisher applicationEventPublisher;

    public void setSessionExpired(String value) {
        messageUtil.warning("session_expired");
    }

    public String getSessionExpired() {
        return null;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void doLogin() {
        ExternalContext ctx = FacesContext.getCurrentInstance().getExternalContext();
        HttpServletRequest request = (HttpServletRequest) ctx.getRequest();
        HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
        HttpRequestResponseHolder holder = new HttpRequestResponseHolder(request, response);
        SecurityContext securityContext = securityContextRepository.loadContext(holder);
        SecurityContextHolder.setContext(securityContext);
        try {
            Authentication authResult = usernamePasswordAuthenticationFilter.attemptAuthentication(request, response);
            if (authResult == null) {
                messageUtil.error("security_error");
                return;
            }
            sessionAuthenticationStrategy.onAuthentication(authResult, request, response);
            // below : do the same thing as in AbstractAuthenticationProcessingFilter.successfulAuthentication(),
            // except for the redirection to the login success URL that is managed by JSF
            securityContext.setAuthentication(authResult);
            usernamePasswordAuthenticationFilter.getRememberMeServices().loginSuccess(request, response, authResult);
            if (applicationEventPublisher != null) {
                applicationEventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, getClass()));
            }
            // redirects to the home page
            Faces.navigate("/home.faces?faces-redirect=true");
        } catch (UsernameNotFoundException e) {
            messageUtil.error("security_username_not_found");
        } catch (DisabledException e) {
            messageUtil.error("security_account_disabled");
        } catch (LockedException e) {
            messageUtil.error("security_account_locked");
        } catch (AccountExpiredException e) {
            messageUtil.error("security_account_expired");
        } catch (CredentialsExpiredException e) {
            messageUtil.error("security_account_credentials_expired");
        } catch (AccountStatusException e) {
            messageUtil.error("security_account_status_invalid");
        } catch (BadCredentialsException e) {
            messageUtil.error("security_bad_credentials");
        } catch (AuthenticationException e) {
            messageUtil.error("security_error");
        } finally {
            securityContextRepository.saveContext(securityContext, holder.getRequest(), holder.getResponse());
        }
    }
}
