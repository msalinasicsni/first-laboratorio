<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sec"
           uri="http://www.springframework.org/security/tags"%>
<html>
<head>
    <jsp:include page="../fragments/headTag.jsp" />
</head>
<body class="">
<jsp:include page="../fragments/bodyHeader.jsp" />
<jsp:include page="../fragments/bodyNavigation.jsp" />
<!-- Main bar -->
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
    <!-- Matter -->

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
							<spring:message code="users.list" />
						</span>
                </h1>
            </div>
            <!-- end col -->
        </div>
        <!-- end row -->
        <section id="widget-grid" class="">

            <!-- Table -->

            <div class="row">
                <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                    <!-- Widget ID (each widget will need unique ID)-->
                    <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
                        <header>
                            <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
                            <h2><spring:message code="lbl.results" /> </h2>
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
                                <form id="sendOrders-form" class="smart-form" autocomplete="off">
                                    <footer>

                                        <spring:url value="/usuarios/admin/new" var="addUserUrl"/>
                                        <button type="button" class="btn btn-primary pull-right"
                                                onclick="location.href='${fn:escapeXml(addUserUrl)}'">
                                            <spring:message code="users.add" />
                                        </button>
                                    </footer>
                                </form>
                                <input id="blockUI_message" type="hidden" value="<spring:message code="blockUI.message"/>"/>
                                <table class="table table-striped table-bordered table-hover" width="100%" id="users-list">
                                    <thead>
                                    <tr>
                                        <th><spring:message code="users.username" /></th>
                                        <th><spring:message code="users.desc" /></th>
                                        <th><spring:message code="users.email" /></th>
                                        <th><spring:message code="users.enabled" /></th>
                                        <th><spring:message code="users.roles" /></th>
                                        <th><spring:message code="users.lab" /></th>
                                        <th style="width: 10%"><spring:message code="lbl.actions" /></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${usuarios}" var="usuario">
                                        <tr>
                                            <spring:url value="/usuarios/admin/{username}"
                                                        var="usuarioUrl">
                                                <spring:param name="username" value="${usuario.username}" />
                                            </spring:url>
                                            <spring:url value="/usuarios/admin/{username}/edit"
                                                        var="editUrl">
                                                <spring:param name="username" value="${usuario.username}" />
                                            </spring:url>
                                            <spring:url value="/usuarios/admin/{username}/disable"
                                                        var="disableUrl">
                                                <spring:param name="username" value="${usuario.username}" />
                                            </spring:url>
                                            <spring:url value="/usuarios/admin/{username}/chgpass"
                                                        var="chgpassUrl">
                                                <spring:param name="username" value="${usuario.username}" />
                                            </spring:url>
                                            <td><a href="${fn:escapeXml(usuarioUrl)}"><c:out
                                                    value="${usuario.username}" /></a></td>
                                            <td><a href="${fn:escapeXml(usuarioUrl)}"><c:out
                                                    value="${usuario.completeName}" /></a></td>
                                            <td><c:out value="${usuario.email}" /></td>
                                            <c:choose>
                                                <c:when test="${usuario.enabled}">
                                                    <td align="center">
                                                        <span class="label label-success"><i class="fa fa-check"></i></span>
                                                    </td>
                                                </c:when>
                                                <c:otherwise>
                                                    <td align="center">
                                                        <span class="label label-danger"><i class="fa fa-times"></i></span>
                                                    </td>
                                                </c:otherwise>
                                            </c:choose>
                                            <td><c:forEach var="rol" items="${authorities}">
                                                <c:if test="${rol.user.username == usuario.username}">
                                                    <c:out value="${rol.authId.authority}" />
                                                </c:if>
                                            </c:forEach></td>
                                            <td>
                                                <c:forEach var="autLab" items="${autoridadLaboratorios}">
                                                    <c:if test="${autLab.user.username == usuario.username}">
                                                        <c:out value="${autLab.laboratorio.nombre}" />
                                                    </c:if>
                                                </c:forEach>
                                            </td>
                                            <td align="center">
                                                <div class="">
                                                    <button title="<spring:message code="act.show" />"
                                                            onclick="location.href='${fn:escapeXml(usuarioUrl)}'"
                                                            class="btn btn-xs btn-primary">
                                                        <i class="fa fa-search"></i>
                                                    </button>
                                                    <button title="<spring:message code="act.edit" />"
                                                            onclick="location.href='${fn:escapeXml(editUrl)}'"
                                                            class="btn btn-xs btn-warning">
                                                        <i class="fa fa-pencil"></i>
                                                    </button>
                                                    <button title="<spring:message code="act.change.pass" />"
                                                            onclick="location.href='${fn:escapeXml(chgpassUrl)}'"
                                                            class="btn btn-xs btn-warning">
                                                        <i class="fa fa-lock"></i>
                                                    </button>
                                                    <!--<button
                                                            onclick="location.href='${fn:escapeXml(disableUrl)}'"
                                                            class="btn btn-xs btn-warning">
                                                        <i class="icon-remove"><spring:message code="act.disable" /></i>
                                                    </button>-->
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            </div>
                            <!-- end widget div -->
                        </div>
                        <!-- end widget -->
                </article>
                <!-- WIDGET END -->
            </div>

        </section>
        <!-- Matter ends -->
    </div>
</div>

<!-- Content ends -->
<!-- Footer starts -->
<jsp:include page="../fragments/footer.jsp" />
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
<spring:url value="/resources/scripts/usuarios/users.js" var="userJs" />
<script src="${userJs}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<script type="text/javascript" charset="utf-8">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {blockMess : $("#blockUI_message").val()};
        Users.init(parametros);
        $("li.administracion").addClass("open");
        $("li.users").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.users").parents("ul").slideDown(200);
        }
    });
</script>
</body>
</html>