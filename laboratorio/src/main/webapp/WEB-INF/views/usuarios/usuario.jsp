<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <jsp:include page="../fragments/headTag.jsp" />
    <style>
        .modal .modal-dialog {
            width: 75%;
        }
        .styleButton {
            float: right;
            height: 31px;
            margin: 10px 0px 0px 5px;
            padding: 0px 22px;
            font: 300 15px/29px "Open Sans", Helvetica, Arial, sans-serif;
            cursor: pointer;
        }
        .alert{
            margin-bottom: 0px;
        }
    </style>
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="">
<!-- #HEADER -->
<jsp:include page="../fragments/bodyHeader.jsp" />
<!-- #NAVIGATION -->
<jsp:include page="../fragments/bodyNavigation.jsp" />
<!-- MAIN PANEL -->
<div id="main" data-role="main">
<!-- RIBBON -->
<div id="ribbon">
			<span class="ribbon-button-alignment">
				<span id="refresh" class="btn btn-ribbon" data-action="resetWidgets" data-placement="bottom" data-original-title="<i class='text-warning fa fa-warning'></i> <spring:message code="msg.reset" />" data-html="true">
					<i class="fa fa-refresh"></i>
				</span>
			</span>
    <!-- breadcrumb -->
    <ol class="breadcrumb">
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/personas/search" htmlEscape="true "/>"><spring:message code="menu.persons" /></a></li>
    </ol>
    <!-- end breadcrumb -->
    <jsp:include page="../fragments/layoutOptions.jsp" />
</div>
<!-- END RIBBON -->
<!-- MAIN CONTENT -->
<div id="content">
<!-- row -->
<div class="row">
    <!-- col -->
    <div class="col-xs-12 col-sm-7 col-md-7 col-lg-4">
        <h1 class="page-title txt-color-blueDark">
            <!-- PAGE HEADER -->
            <i class="fa-fw fa fa-users"></i>
            <spring:message code="users" />
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="users.user" />
						</span>
        </h1>
    </div>
    <!-- end col -->
</div>
<!-- end row -->
<!-- widget grid -->
<section id="widget-grid" class="">
    <!-- row -->
    <div class="row">
        <!-- NEW WIDGET START -->
        <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
            <!-- Widget ID (each widget will need unique ID)-->
            <div class="jarviswidget jarviswidget-color-darken" id="wid-id-0">
                <header>
                    <span class="widget-icon"> <i class="fa fa-edit"></i> </span>
                    <h2><spring:message code="users.user" /> </h2>
                </header>
                <!-- widget div-->
                <div>
                    <!-- widget edit box -->
                    <div class="jarviswidget-editbox">
                        <!-- This area used as dropdown edit box -->
                        <input class="form-control" type="text">
                    </div>
                    <!-- end widget edit box -->
                    <!-- widget content -->
                    <div class="widget-body no-padding">
                    <c:set var="rolesString">
                        <c:forEach var="rol" items="${authorities}">
                            <c:out value="${rol.authId.authority}" />
                        </c:forEach>
                    </c:set>
                    <br />
                    <table class="table table-striped table-bordered table-hover"
                           id="tabla">
                        <tr>
                            <th><spring:message code="users.username" /></th>
                            <td><b><c:out value="${user.username}" /></b></td>
                        </tr>
                        <tr>
                            <th><spring:message code="users.desc" /></th>
                            <td><c:out value="${user.completeName}" /></td>
                        </tr>
                        <tr>
                            <th><spring:message code="users.enabled" /></th>
                            <c:choose>
                                <c:when test="${user.enabled}">
                                    <td>
                                        <span class="label label-success"><i class="fa fa-check"></i></span>
                                    </td>
                                </c:when>
                                <c:otherwise>
                                    <td>
                                        <span class="label label-danger"><i class="fa fa-times"></i></span>
                                    </td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                        <tr>
                            <th><spring:message code="users.roles" /></th>
                            <td><c:out value="${rolesString}" /></td>
                        </tr>
                        <tr>
                            <th><spring:message code="users.lab" /></th>
                            <td>
                                <c:forEach var="autLab" items="${autoridadLaboratorios}">
                                    <c:if test="${autLab.user.username == user.username}">
                                        <c:out value="${autLab.laboratorio.nombre}" />
                                    </c:if>
                                </c:forEach>
                            </td>
                        </tr>
                    </table>
                        </div>
                    </div>
                    <!-- end widget div -->
                </div>
                <!-- end widget -->
        </article>
        <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
            <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
                <div>
                    <div class="widget-body no-padding">
                        <input type="hidden" value="${user.username}" id="username" />
                        <input id="disappear" type="hidden" value="<spring:message code="msg.disappear"/>"/>
                        <input id="msjMkAdmin" type="hidden" value="<spring:message code="msg.user.mk.admin"/>"/>
                        <input id="msjMkNoAdmin" type="hidden" value="<spring:message code="msg.user.mk.no.admin"/>"/>
                        <input id="msjMkRecept" type="hidden" value="<spring:message code="msg.user.mk.receptionist"/>"/>
                        <input id="msjMkNoRecept" type="hidden" value="<spring:message code="msg.user.mk.no.receptionist"/>"/>
                        <input id="msjMkAnalyst" type="hidden" value="<spring:message code="msg.user.mk.analyst"/>"/>
                        <input id="msjMkNoAnalyst" type="hidden" value="<spring:message code="msg.user.mk.no.analyst"/>"/>

                        <table class="table table-striped table-bordered table-hover"
                               id="tabla2">
                            <tr class="warning">
                                <td><spring:url value="{username}/edit" var="editUrl">
                                        <spring:param name="username" value="${user.username}" />
                                    </spring:url>
                                    <button onclick="location.href='${fn:escapeXml(editUrl)}'"
                                            class="btn btn-info">
                                        <spring:message code="act.edit" />
                                    </button>
                                <td><spring:url value="{username}/chgpass"
                                                var="chgpassUrl">
                                        <spring:param name="username" value="${user.username}" />
                                    </spring:url>
                                    <button
                                            onclick="location.href='${fn:escapeXml(chgpassUrl)}'"
                                            class="btn btn-info">
                                        <spring:message code="act.change.pass" />
                                    </button> <c:choose>
                                    <c:when test="${user.enabled}">
                                <td><spring:url value="{username}/disable"
                                                var="disableUrl">
                                        <spring:param name="username" value="${user.username}" />
                                    </spring:url>
                                    <button
                                            onclick="location.href='${fn:escapeXml(disableUrl)}'"
                                            class="btn btn-info">
                                        <spring:message code="users.disable" />
                                    </button>
                                    </c:when>
                                    <c:otherwise>
                                <td><spring:url value="{username}/enable"
                                                var="enableUrl">
                                        <spring:param name="username" value="${user.username}" />
                                    </spring:url>
                                    <button
                                            onclick="location.href='${fn:escapeXml(enableUrl)}'"
                                            class="btn btn-info">
                                        <spring:message code="users.enabled" />
                                    </button>
                                    </c:otherwise>
                                    </c:choose>
                                <td><spring:url value="/usuarios/list" var="listUrl"/>
                                    <button onclick="location.href='${fn:escapeXml(listUrl)}'"
                                            class="btn btn-info">
                                        <spring:message code="act.show.all" />
                                    </button></td>
                            </tr>
                            <tr class="info">
                                <td>
                                <c:choose>
                                    <c:when test="${fn:contains(rolesString,'ROLE_ADMIN')}">
                                            <button id="btn-mkNoAdmin" type="button" class="btn btn-danger">
                                                <spring:message code="users.noadmin" />
                                            </button>
                                    </c:when>
                                    <c:otherwise>
                                            <button id="btn-mkAdmin" type="button" class="btn btn-success">
                                                <spring:message code="users.admin" />
                                            </button>
                                    </c:otherwise>
                                   </c:choose>
                                </td>
                                   <c:choose>
                                    <c:when test="${fn:contains(rolesString,'ROLE_ANALISTA')}">
                                        <td>
                                            <button id="btn-mkNoAnalyst" type="button" class="btn btn-danger">
                                                <spring:message code="users.noanalyst" />
                                            </button>
                                            <br/><br/>
                                            <button id="btn-areaExam" type="button" class="btn btn-primary">
                                                <spring:message code="act.admin.areas.and.exams" />
                                            </button>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <button id="btn-mkAnalyst" type="button" class="btn btn-success">
                                                <spring:message code="users.analyst" />
                                            </button>
                                         </td>
                                    </c:otherwise>
                                    </c:choose>
                                <c:choose>
                                    <c:when test="${fn:contains(rolesString,'ROLE_RECEPCION')}">
                                        <td>
                                            <button id="btn-mkNoRecep" type="button" class="btn btn-danger">
                                                <spring:message code="users.norecep" />
                                            </button>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <button id="btn-mkRecep" type="button" class="btn btn-success">
                                                <spring:message code="users.recep" />
                                            </button>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${fn:contains(rolesString,'ROLE_DIR')}">
                                        <td>
                                            <spring:url value="{username}/nosup"  var="nosupUrl">
                                                <spring:param name="username" value="${user.username}" />
                                            </spring:url>
                                            <button onclick="location.href='${fn:escapeXml(nosupUrl)}'" class="btn btn-danger">
                                                <spring:message code="users.nodir" />
                                            </button>
                                            <br/><br/>
                                            <button id="btn-direction" type="button" class="btn btn-primary">
                                                <spring:message code="act.admin.direction" />
                                            </button>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <spring:url value="{username}/mksup" var="supUrl">
                                                <spring:param name="username" value="${user.username}" />
                                            </spring:url>
                                            <button onclick="location.href='${fn:escapeXml(supUrl)}'" class="btn btn-success">
                                                <spring:message code="users.dir" />
                                            </button>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                                <c:choose>
                                    <c:when test="${fn:contains(rolesString,'ROLE_JEFE')}">
                                        <td>
                                            <spring:url value="{username}/nosup"  var="nosupUrl">
                                                <spring:param name="username" value="${user.username}" />
                                            </spring:url>
                                            <button onclick="location.href='${fn:escapeXml(nosupUrl)}'" class="btn btn-danger">
                                                <spring:message code="users.nodept" />
                                            </button>
                                            <br/><br/>
                                            <button id="btn-department" type="button" class="btn btn-primary">
                                                <spring:message code="act.admin.department" />
                                            </button>
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td>
                                            <spring:url value="{username}/mksup" var="supUrl">
                                                <spring:param name="username" value="${user.username}" />
                                            </spring:url>
                                            <button onclick="location.href='${fn:escapeXml(supUrl)}'" class="btn btn-success">
                                                <spring:message code="users.dept" />
                                            </button>
                                        </td>
                                    </c:otherwise>
                                </c:choose>
                                <td></td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </article>
        <!-- WIDGET END -->
    </div>
    <!-- end row -->
    <!-- row -->
    <div class="row">
        <!-- a blank row to get started -->
        <div class="col-sm-12">
            <!-- your contents here -->
        </div>
    </div>
    <!-- end row -->
</section>
<!-- end widget grid -->
<!-- Modal -->
<div class="modal fade" id="modalAreaExamen" aria-hidden="true" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <!--<h4 class="modal-title">
                    <spring:message code="lbl.response.header.modal.add" />
                </h4>-->
                <div class="alert alert-info">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                        &times;
                    </button>
                    <h4 class="modal-title">
                        <i class="fa-fw fa fa-font"></i>
                        <spring:message code="lbl.response.header.modal.add" />
                    </h4>
                </div>
            </div>
            <div class="modal-body"> <!--  no-padding -->
                <div class="row">
                    <div class="col col-sm-12 col-md-12 col-lg-6">
                        <form id="area-examen-form" class="smart-form" novalidate="novalidate">
                            <div class="row">
                                <section class="col col-sm-12 col-md-9 col-lg-10">
                                    <label class="text-left txt-color-blue font-md">
                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.area" />
                                    </label>
                                    <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-location-arrow fa-fw"></i>
                                    </span>
                                        <select  class="select2" id="idArea" name="idArea" >
                                            <option value=""><spring:message code="lbl.select" />...</option>
                                            <c:forEach items="${areas}" var="area">
                                                <option value="${area.idArea}">${area.nombre}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </section>
                                <section class="col col-sm-12 col-md-3 col-lg-2">
                                    <button type="button" class="btn btn-primary styleButton" id="btnArea">
                                        <i class="fa fa-save"></i>
                                    </button>
                                </section>
                            </div>
                        </form>
                    </div>
                    <div class="col col-sm-12 col-md-12 col-lg-6">
                        <form id="examen-form" class="smart-form" novalidate="novalidate">
                            <div class="row">
                                <section class="col col-sm-12 col-md-9 col-lg-10">
                                    <label class="text-left txt-color-blue font-md">
                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.test.name" />
                                    </label>
                                    <div class="input-group">
                                    <span class="input-group-addon">
                                        <i class="fa fa-location-arrow fa-fw"></i>
                                    </span>
                                        <select  class="select2" id="idExamen" name="idExamen" >
                                            <option value=""><spring:message code="lbl.select" />...</option>
                                            <c:forEach items="${areas}" var="area">
                                                <option value="${area.idArea}">${area.nombre}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </section>
                                <section class="col col-sm-12 col-md-3 col-lg-2">
                                    <button type="button" class="btn btn-primary styleButton" id="btnExamen">
                                        <i class="fa fa-save"></i>
                                    </button>
                                </section>
                            </div>
                        </form>
                    </div>
                </div>
                    <div class="widget-body no-padding">
                    <div class="row">
                                            <section class="col col-sm-12 col-md-6 col-lg-6">
                            <table class="table table-striped table-bordered table-hover" id="areas-list">
                                <thead>
                                <tr>
                                    <th data-class="expand"><i class="fa fa-fw fa-file-text-o text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.name"/></th>
                                    <th><spring:message code="lbl.override"/></th>
                                </tr>
                                </thead>
                            </table>
                        </section>
                        <section class="col col-sm-12 col-md-6 col-lg-6">
                            <table class="table table-striped table-bordered table-hover"   id="examenes-list">
                                <thead>
                                <tr>
                                    <th data-class="expand"><i class="fa fa-fw fa-file-text-o text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.name"/></th>
                                    <th data-class="expand"><i class="fa fa-fw fa-file-text-o text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.area"/></th>
                                    <th><spring:message code="lbl.override"/></th>
                                </tr>
                                </thead>
                            </table>
                        </section>
                    </div>
                </div>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

</div>
<!-- END MAIN CONTENT -->
</div>
<!-- END MAIN PANEL -->
<!-- BEGIN FOOTER -->
<jsp:include page="../fragments/footer.jsp" />
<!-- END FOOTER -->
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<jsp:include page="../fragments/corePlugins.jsp" />
<!-- BEGIN PAGE LEVEL PLUGINS -->
<spring:url value="/resources/js/plugin/datatables/jquery.dataTables.min.js" var="dataTables" />
<script src="${dataTables}"></script>
<spring:url value="/resources/js/plugin/datatables/dataTables.colVis.min.js" var="dataTablesColVis" />
<script src="${dataTablesColVis}"></script>
<spring:url value="/resources/js/plugin/datatables/dataTables.tableTools.min.js" var="dataTablesTableTools" />
<script src="${dataTablesTableTools}"></script>
<spring:url value="/resources/js/plugin/datatables/dataTables.bootstrap.min.js" var="dataTablesBootstrap" />
<script src="${dataTablesBootstrap}"></script>
<spring:url value="/resources/js/plugin/datatable-responsive/datatables.responsive.min.js" var="dataTablesResponsive" />
<script src="${dataTablesResponsive}"></script>
<!-- jQuery Selecte2 Input -->
<spring:url value="/resources/js/plugin/select2/select2.min.js" var="selectPlugin"/>
<script src="${selectPlugin}"></script>
<!-- bootstrap datepicker -->
<spring:url value="/resources/js/plugin/bootstrap-datepicker/bootstrap-datepicker.js" var="datepickerPlugin" />
<script src="${datepickerPlugin}"></script>
<spring:url value="/resources/js/plugin/bootstrap-datepicker/locales/bootstrap-datepicker.{languagedt}.js" var="datePickerLoc">
    <spring:param name="languagedt" value="${pageContext.request.locale.language}" /></spring:url>
<script src="${datePickerLoc}"></script>
<!-- JQUERY VALIDATE -->
<spring:url value="/resources/js/plugin/jquery-validate/jquery.validate.min.js" var="jqueryValidate" />
<script src="${jqueryValidate}"></script>
<spring:url value="/resources/js/plugin/jquery-validate/messages_{language}.js" var="jQValidationLoc">
    <spring:param name="language" value="${pageContext.request.locale.language}" /></spring:url>
<script src="${jQValidationLoc}"></script>
<!-- JQUERY BLOCK UI -->
<spring:url value="/resources/js/plugin/jquery-blockui/jquery.blockUI.js" var="jqueryBlockUi" />
<script src="${jqueryBlockUi}"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<spring:url value="/resources/scripts/usuarios/users.js" var="usersJs" />
<script src="${usersJs}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="mkAdminUrl" value="/usuarios/adminUser"/>
<c:url var="mkNoAdminUrl" value="/usuarios/noAdminUser"/>
<c:url var="mkReceptUrl" value="/usuarios/receptionistUser"/>
<c:url var="mkNoReceptUrl" value="/usuarios/noReceptionistUser"/>
<c:url var="mkAnalystUrl" value="/usuarios/analystUser"/>
<c:url var="mkNoAnalystUrl" value="/usuarios/noAnalystUser"/>
<c:url var="autoridadAreaUrl" value="/usuarios/getAutoridadAreaUsuario"/>
<c:url var="autoridadExamenUrl" value="/usuarios/getAutoridadExamenUsuario"/>

<c:url var="usuarioUrl" value="/usuarios/admin/"/>
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {sUsuarioUrl: "${usuarioUrl}",
            sMkAdminUrl : "${mkAdminUrl}",
            sMkNoAdminUrl : "${mkNoAdminUrl}",
            mkReceptUrl : "${mkReceptUrl}",
            mkNoReceptUrl : "${mkNoReceptUrl}",
            mkAnalystUrl : "${mkAnalystUrl}",
            mkNoAnalystUrl : "${mkNoAnalystUrl}",
            autoridadAreaUrl : "${autoridadAreaUrl}",
            autoridadExamenUrl : "${autoridadExamenUrl}",
            blockMess: "${blockMess}"
        };
        Users.init(parametros);
        $("li.mantenimiento").addClass("open");
        $("li.personas").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.personas").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>