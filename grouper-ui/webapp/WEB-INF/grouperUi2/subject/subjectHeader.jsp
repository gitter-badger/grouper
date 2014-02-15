<%@ include file="../assetsJsp/commonTaglib.jsp"%>

            <!-- start group/viewGroup.jsp -->
            <div class="bread-header-container">
              <%--
              <ul class="breadcrumb">
                <li><a href="index.html">Home </a><span class="divider"><i class='icon-angle-right'></i></span></li>
                <li class="active">${grouperRequestContainer.subjectContainer.guiSubject.shortLinkWithIcon}</li>
              </ul>
              --%>
              ${grouperRequestContainer.subjectContainer.guiSubject.breadcrumbs}
              <div class="page-header blue-gradient">
                <div class="row-fluid">
                  <div class="span9">
                    <h1><i class="icon-user"></i> ${grouperRequestContainer.subjectContainer.guiSubject.screenLabelShort2noLink}</h1>
                    <div id="group-search" tabindex="-1" role="dialog" aria-labelledby="group-search-label" aria-hidden="true" class="modal hide fade">
                      <div class="modal-header"><a href="#" data-dismiss="modal" aria-hidden="true" class="close">x</a>
                        <h3 id="group-search-label">${textContainer.text['subjectSearchForGroupButton']}</h3>
                      </div>

                      <div class="modal-body">
                        <form class="form form-inline" id="addGroupSearchFormId">
                          <input id="addGroupSubjectSearchId" name="addGroupSubjectSearch" type="text" placeholder="${textContainer.text['subjectSearchGroupPlaceholder']}" />
                          <button class="btn" onclick="ajax('../app/UiV2Subject.addGroupSearch?subjectId=${grouperRequestContainer.subjectContainer.guiSubject.subject.id}&sourceId=${grouperRequestContainer.subjectContainer.guiSubject.subject.sourceId}', {formIds: 'addGroupSearchFormId'}); return false;" >${textContainer.text['subjectSearchButton'] }</button>
                          <br />
                          <span style="white-space: nowrap;"><input type="checkbox" name="matchExactId" value="true"/> ${textContainer.text['subjectSearchExactIdMatch'] }</span>
                        </form>
                        <div id="addGroupResults">
                        </div>
                      </div>
                      <div class="modal-footer">
                        <button data-dismiss="modal" aria-hidden="true" class="btn">${textContainer.text['subjectSearchCloseButton']}</button>
                      </div>
                    </div>

                    <div id="add-block-container" class="well gradient-background hide">
                      <div id="add-groups">
                        <form id="add-groups-form" target="#" class="form-horizontal form-highlight">
                          <div class="control-group">
                            <label for="add-block-input" class="control-label">${textContainer.text['subjectSearchGroupName'] }</label>
                            <div class="controls">
                              <div id="add-members-container">

                                <%-- placeholder: Enter the name of a group --%>
                                <grouper:combobox2 idBase="groupAddMemberCombo" style="width: 30em"
                                  filterOperation="../app/UiV2Subject.addToGroupFilter?subjectId=${grouperRequestContainer.subjectContainer.guiSubject.subject.id}&sourceId=${grouperRequestContainer.subjectContainer.guiSubject.subject.sourceId}"/>
                                <%--a href="#member-search" onclick="$('#addMemberResults').empty();" role="button" data-toggle="modal" class="btn"><i class="icon-search"></i></a --%>
                                <br />
                                ${textContainer.text['subjectSearchLabelPreComboLink']} <a href="#group-search" onclick="$('#addGroupResults').empty(); $('#addGroupSubjectSearchId').val('');" role="button" data-toggle="modal" style="text-decoration: underline !important;">${textContainer.text['subjectSearchForGroupLink']}</a>
                                
                              </div>
                            </div>
                          </div>
                          <div id="add-members-privileges-select" class="control-group">
                            <label class="control-label">${textContainer.text['subjectViewAssignThesePrivileges']}</label>
                            <div class="controls">
                              <label class="radio inline">
                                <input type="radio" id="priv1" value="default" name="privilege-options" checked="checked" onclick="this.blur();" value="true" onchange="$('#add-members-privileges').hide('slow');"/>${textContainer.text['subjectViewDefaultPrivileges'] }
                              </label>
                              <label class="radio inline">
                                <input type="radio" id="priv2" value="custom" name="privilege-options" onclick="this.blur();" value="true" onchange="$('#add-members-privileges').show('slow');"/>${textContainer.text['subjectViewCustomPrivileges'] }
                              </label>
                            </div>
                          </div>
                          <div id="add-members-privileges" class="control-group hide">
                            <div class="controls">
                              <label class="checkbox inline">
                                <input type="checkbox" name="privileges_members" value="true" checked="checked"/>${textContainer.text['priv.memberUpper']}
                              </label>
                              <label class="checkbox inline">
                                <%--
                                <input type="checkbox" name="privileges_admins" value="true" 
                                  ${grouperRequestContainer.groupContainer.configDefaultGroupsCreateGrantAllAdmin ? 'checked="checked"' : '' } />ADMIN
                                --%>
                                <input type="checkbox" name="privileges_admins" value="true" />${textContainer.text['priv.adminUpper'] }
                              </label>
                              <label class="checkbox inline">
                                <input type="checkbox" name="privileges_updaters" value="true" />${textContainer.text['priv.updateUpper'] }
                              </label>
                              <label class="checkbox inline">
                                <input type="checkbox" name="privileges_readers" value="true" />${textContainer.text['priv.readUpper'] }
                              </label>
                              <label class="checkbox inline">
                                <input type="checkbox" name="privileges_viewers" value="true" />${textContainer.text['priv.viewUpper'] }
                              </label>
                              <label class="checkbox inline">
                                <input type="checkbox" name="privileges_optins" value="true" />${textContainer.text['priv.optinUpper'] }
                              </label>
                              <label class="checkbox inline">
                                <input type="checkbox" name="privileges_optouts" value="true" />${textContainer.text['priv.optoutUpper'] }
                              </label>
                              <label class="checkbox inline">
                                <input type="checkbox" name="privileges_groupAttrReaders" value="true" />${textContainer.text['priv.groupAttrReadUpper'] }
                              </label>
                              <label class="checkbox inline">
                                <input type="checkbox" name="privileges_groupAttrUpdaters" value="true" />${textContainer.text['priv.groupAttrUpdateUpper'] }
                              </label>
                            </div>
                          </div>
                          <div class="control-group">
                            <div class="controls">
                              <button onclick="ajax('../app/UiV2Subject.addGroupSubmit?subjectId=${grouperRequestContainer.subjectContainer.guiSubject.subject.id}&sourceId=${grouperRequestContainer.subjectContainer.guiSubject.subject.sourceId}', {formIds: 'add-groups-form,groupFilterFormId,groupPagingFormId'}); return false;" 
                                id="add-members-submit" type="submit" class="btn btn-primary">${textContainer.text['subjectViewAddGroupLink']}</button> ${textContainer.text['subjectViewTextBetweenAddAndBulk']} <a href="bulk-add.html" class="blue-link">${textContainer.text['subjectViewBulkLink'] }</a> ${textContainer.text['subjectViewTextPostBulkLink'] }
                            </div>
                          </div>
                        </form>
                      </div>
                    </div>


                    <div class="row-fluid">
                      <div class="span2"><strong>${textContainer.text['subjectViewLabelId'] }</strong></div>
                      <div class="span10">
                        <p>${grouper:escapeHtml(grouperRequestContainer.subjectContainer.guiSubject.subject.id)}</p>
                      </div>
                    </div>
                    <c:if test="${grouperRequestContainer.subjectContainer.guiSubject.hasEmailAttributeInSource }">
                      <div class="row-fluid">
                        <div class="span2"><strong>${textContainer.text['subjectViewLabelEmail']}</strong></div>
                        <div class="span10">
                          <p>${grouperRequestContainer.subjectContainer.guiSubject.email}</p>
                        </div>
                      </div>
                    </c:if>
                    <div class="row-fluid">
                      <div class="span2"><strong>${textContainer.text['subjectViewLabelName'] }</strong></div>
                      <div class="span10">
                        <p>${grouper:escapeHtml(grouperRequestContainer.subjectContainer.guiSubject.subject.name)}</p>
                      </div>
                    </div>
                    <div class="row-fluid">
                      <div class="span2"><strong>${textContainer.text['subjectViewLabelDescription'] }</strong></div>
                      <div class="span10">
                        <p>${grouper:escapeHtml(grouperRequestContainer.subjectContainer.guiSubject.subject.description)}</p>
                      </div>
                    </div>
                    <div style="display: none;" id="subjectDetailsId">
                      <table class="table table-condensed table-striped">
                        <tbody>
                          <c:forEach items="${grouperRequestContainer.subjectContainer.guiSubject.attributeNamesNonInternal}" 
                              var="attributeName" >
                            <tr>
                              <td><strong>${grouperRequestContainer.subjectContainer.guiSubject.attributeLabel[attributeName] }</strong></td>
                              <td>${grouper:escapeHtml(grouperRequestContainer.subjectContainer.guiSubject.attributes[attributeName]) }</td>
                            </tr>

                          </c:forEach>

                          <tr>
                            <td><strong>${textContainer.text['subjectViewLabelMemberId']}</strong></td>
                            <td>${grouper:escapeHtml(grouperRequestContainer.subjectContainer.guiSubject.memberId)}</td>
                          </tr>
                          <tr>
                            <td><strong>${textContainer.text['subjectViewLabelSourceId'] }</strong></td>
                            <td>${grouper:escapeHtml(grouperRequestContainer.subjectContainer.guiSubject.subject.sourceId)}</td>
                          </tr>
                          <tr>
                            <td><strong>${textContainer.text['subjectViewLabelSourceName'] }</strong></td>
                            <td>${grouper:escapeHtml(grouperRequestContainer.subjectContainer.guiSubject.subject.source.name)}</td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                    <p id="subjectDetailsMoreId"><a href="#" onclick="$('#subjectDetailsId').show('slow'); $('#subjectDetailsMoreId').hide(); $('#subjectDetailsLessId').show(); return false" >${textContainer.text['guiMore']} <i class="icon-angle-down"></i></a></p>
                    <p id="subjectDetailsLessId" style="display: none"><a href="#" onclick="$('#subjectDetailsId').hide('slow'); $('#subjectDetailsLessId').hide(); $('#subjectDetailsMoreId').show(); return false" >${textContainer.text['guiLess']} <i class="icon-angle-up"></i></a></p>
                  </div>
                  <div class="span3" id="subjectMoreActionsButtonContentsDivId">
                    <%@ include file="subjectMoreActionsButtonContents.jsp"%>
                  </div>
                </div>
              </div>
            </div>
