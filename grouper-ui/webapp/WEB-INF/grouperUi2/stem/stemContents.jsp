<%@ include file="../assetsJsp/commonTaglib.jsp"%>

               <table class="table table-hover table-bordered table-striped table-condensed data-table">
                  <thead>
                    <tr>
                      <th class="sorted">${textContainer.text['stemObjectName'] }</th>
                    </tr>
                  </thead>
                  <tbody>
                    <c:if test="${ ! grouperRequestContainer.stemContainer.guiStem.stem.rootStem}">
                      <tr>
                        <td><i class="icon-chevron-up"></i> <a href="#" onclick="return guiV2link('operation=UiV2Stem.viewStem&stemId=${grouperRequestContainer.stemContainer.guiStem.stem.parentUuid}');">${textContainer.text['stemUpOneFolder'] }</a></td>
                      </tr>
                    </c:if>
                    <%--
                    <tr>
                      <td><i class="icon-folder-close"></i><a href="#"> Directories</a>
                      </td>
                    </tr>
                    --%>
                    <c:forEach items="${grouperRequestContainer.stemContainer.childGuiObjectsAbbreviated}" var="guiObjectBase">
                      <tr>
                        <td>${guiObjectBase.shortLinkWithIcon }</td>
                      </tr>
                    </c:forEach>
                    
                  </tbody>
                </table>
                <div class="data-table-bottom gradient-background">
                  <div class="pull-right">Showing 1-10 of 25 &middot; <a href="#">First</a> | <a href="#">Prev</a> | <a href="#">Next</a> | <a href="#">Last</a></div>
                  <form class="form-inline form-small">
                    <label for="show-entries">Show:&nbsp;</label>
                    <select id="show-entries" class="span2">
                      <option>10</option>
                      <option>25</option>
                      <option>50</option>
                      <option>100</option>
                    </select>
                  </form>
                </div>
 