<%-- @annotation@
		  Dynamic tile used to render a subject. If a group 
		  []placed around the group name
--%><%--
  @author Gary Brown.
  @version $Id: groupAsSubjectSearchResultView.jsp,v 1.7 2008-04-08 07:51:52 mchyzer Exp $
--%>
<%@include file="/WEB-INF/jsp/include.jsp"%>
<tiles:importAttribute />
<c:if test="${empty groupSearchResultField}"><c:set scope="request" var="groupSearchResultField" value="${mediaMap['search.group.result-field']}"/></c:if>
<%-- note, dont do a tooltip here since there is a title attribute --%>
<img src="grouper/images/group.gif" class="groupIcon"  alt="Group"
/>&nbsp;<span class="<c:out value="${viewObject.subjectType}"
/>Subject"><c:out value="${viewObject[groupSearchResultField]}" /></span>
