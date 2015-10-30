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
$output.java($WebFilter, "LocaleResolverRequestFilter")##

$output.requireStatic("org.apache.commons.lang.StringUtils.isNotBlank")##

$output.require("java.io.IOException")##
$output.require("java.util.Locale")##
$output.require("javax.servlet.FilterChain")##
$output.require("javax.servlet.ServletException")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("javax.servlet.http.HttpServletResponse")##
$output.require("org.apache.commons.lang.LocaleUtils")##
$output.require("org.slf4j.Logger")##
$output.require("org.slf4j.LoggerFactory")##
$output.require("org.springframework.web.context.WebApplicationContext")##
$output.require("org.springframework.web.context.support.WebApplicationContextUtils")##
$output.require("org.springframework.web.filter.OncePerRequestFilter")##
$output.require("org.springframework.web.servlet.LocaleResolver")##
$output.require($Context, "LocaleHolder")##

/**
 * Set the current {@link Locale} in the {@link LocaleHolder}
 */
public class $output.currentClass extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(${output.currentClass}.class);

    public static final String LOCALE_PARAMETER = "locale";

    private LocaleResolver localeResolver;

     @Override
    protected void initFilterBean() throws ServletException {
        WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        localeResolver = context.getBean(LocaleResolver.class);
     }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        forceLocaleFromParameter(request, response);
        try {
            LocaleHolder.setLocale(localeResolver.resolveLocale(request));
            filterChain.doFilter(request, response);
        } finally {
            LocaleHolder.resetLocaleContext();
        }
    }

    private void forceLocaleFromParameter(HttpServletRequest request, HttpServletResponse response) {
        String localeParameter = null;
        try {
            localeParameter = request.getParameter(LOCALE_PARAMETER);
            if (isNotBlank(localeParameter)) {
                Locale locale = LocaleUtils.toLocale(localeParameter);
                log.info("forcing locale to {}", locale.getLanguage());
                localeResolver.setLocale(request, response, locale);
            }
        } catch (IllegalArgumentException e) {
            log.error("Locale " + localeParameter + " is not valid");
        }
    }
}
