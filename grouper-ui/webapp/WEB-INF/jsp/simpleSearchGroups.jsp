<%-- @annotation@
		Tile which displays the simple search form for groups
--%><%--
  @author Gary Brown.
  @version $Id: simpleSearchGroups.jsp,v 1.10 2008-04-08 07:51:52 mchyzer Exp $
--%>
<%@include file="/WEB-INF/jsp/include.jsp"%>
<grouper:recordTile key="Not dynamic" tile="${requestScope['javax.servlet.include.servlet_path']}">
<a href="<c:out value="${pageUrl}"/>#endSearch" class="noCSSOnly"><grouper:message bundle="${nav}" key="page.skip.search"/></a>
<div class="searchGroups">
<grouper:subtitle key="groups.heading.search">
  <a class="underline subtitleLink" href="<c:out value="${pageUrlMinusQueryString}"/>?advancedSearch=true"><grouper:message 
  bundle="${nav}" key="find.action.select.groups-advanced-search"/></a>
</grouper:subtitle>	
	<html:form styleId="SearchFormBean" action="/searchGroups${browseMode}" method="post">
		<html:hidden property="searchInNameOrExtension"/>
		<html:hidden property="searchInDisplayNameOrExtension"/>
		<input type="hidden" name="callerPageId" value="<c:out value="${thisPageId}"/>"/>
	<fieldset>
		<label for="searchTerm" class="noCSSOnly"><grouper:message bundle="${nav}" key="find.search-term"/></label><html:text property="searchTerm" size="25" styleId="searchTerm"/>
		<c:if test="${mediaMap['search.default.any']=='true'}">
		<grouper:message bundle="${nav}" key="find.groups.search-in"/> <html:radio property="searchIn" value="name"/> 
		
		<grouper:message bundle="${nav}" key="find.groups.search-in.name"/> 
		<html:radio property="searchIn" value="any"/> <grouper:message bundle="${nav}" key="find.groups.search-in.any"/> 
		</c:if>
		<br/>
		<tiles:insert definition="searchFromDef"/><br/>
		<tiles:insert definition="searchGroupResultFieldChoiceDef"/>
		<html:submit property="submit.search" value="${navMap['groups.action.search']}"/>
	</fieldset>
	</html:form>
</div>
<a name="endSearch" id="endSearch"></a>
</grouper:recordTile>