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
$output.java($WebConversation, "ConversationUtil")##

$output.requireStatic("org.apache.commons.lang.StringUtils.substringAfter")##
$output.requireStatic("org.apache.commons.lang.StringUtils.substringBefore")##
$output.require("javax.servlet.http.HttpServletRequest")##

public final class $output.currentClass {
    public static final String CID_PARAM_NAME = "_cid";
    public static final String CID_PARAM_SEPARATOR = "c";

    private ${output.currentClass}(){
    }

    public static String cidParamValue(String conversationId, String conversationContextId) {
        return conversationId + CID_PARAM_SEPARATOR + conversationContextId;
    }
    
    public static String cidParamNameValue(String conversationId, String conversationContextId) {
        return CID_PARAM_NAME + "=" + cidParamValue(conversationId, conversationContextId);
    }
    
    public static String getConversationId(HttpServletRequest request) {
        String _cid = request.getParameter(CID_PARAM_NAME);
        return _cid != null ? substringBefore(_cid, CID_PARAM_SEPARATOR) : null;
    }
    
    public static String getConversationContextId(HttpServletRequest request) {
        String _cid = request.getParameter(CID_PARAM_NAME);
        return _cid != null ? substringAfter(_cid, CID_PARAM_SEPARATOR) : null;
    }
}