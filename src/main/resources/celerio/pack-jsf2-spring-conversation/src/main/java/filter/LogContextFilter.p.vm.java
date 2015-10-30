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
$output.java($WebFilter, "LogContextFilter")##

$output.require("java.io.IOException")##
$output.require("javax.servlet.Filter")##
$output.require("javax.servlet.FilterChain")##
$output.require("javax.servlet.FilterConfig")##
$output.require("javax.servlet.ServletException")##
$output.require("javax.servlet.ServletRequest")##
$output.require("javax.servlet.ServletResponse")##
$output.require("javax.servlet.http.HttpServletRequest")##
$output.require("javax.servlet.http.HttpServletResponse")##
$output.require($Context, "LogContext")##
$output.require($Context, "UserContext")##

/**
 * Set the <code>login</code> and <code>sessionId<code> in the {@link LogContext} so they can be used in the log patterns.
 */
public class ${output.currentClass} implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String username = UserContext.getUsername();
        LogContext.setLogin(username != null ? username : "no username");
        LogContext.setSessionId(request.getSession().getId());

        filterChain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}