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
        textarea {
            resize: none;
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
							<spring:message code="users.edit" />
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
                            <h2><spring:message code="lbl.user" /> </h2>
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
										<form class="smart-form" autocomplete="off" id="edit-user-form">
                                            <fieldset>
                                                <div class="row">
                                                    <section class="col col-sm-6 col-md-3 col-lg-3">
                                                        <label class="text-left txt-color-blue font-md">
                                                            <spring:message code="users.username" />
                                                        </label>
                                                        <div class="">
                                                            <label class="input">
                                                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-user fa-fw"></i>
                                                                <input class="form-control" type="text" id="username" readonly name="username" value="${user.username}" placeholder=" <spring:message code="users.username" />">
                                                            </label>
                                                        </div>
                                                    </section>
                                                    <section class="col col-sm-6 col-md-5 col-lg-5">
                                                        <label class="text-left txt-color-blue font-md">
                                                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="users.desc" />
                                                        </label>
                                                        <div class="">
                                                            <label class="input">
                                                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                                <input class="form-control" type="text" id="completeName" name="completeName" value="${user.completeName}" placeholder=" <spring:message code="users.desc" />">
                                                                <b class="tooltip tooltip-bottom-right"> <i
                                                                        class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.users.desc"/>
                                                                </b>
                                                            </label>
                                                        </div>
                                                    </section>
                                                    <section class="col col-sm-6 col-md-4 col-lg-4">
                                                        <label class="text-left txt-color-blue font-md">
                                                            <spring:message code="users.email" />
                                                        </label>
                                                        <div class="">
                                                            <label class="input">
                                                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-at fa-fw"></i>
                                                                <input class="form-control" type="text" id="correoe" name="correoe" value="${user.email}" placeholder=" <spring:message code="users.email" />">
                                                                <b class="tooltip tooltip-bottom-right"> <i
                                                                        class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.users.email"/>
                                                                </b>
                                                            </label>
                                                        </div>
                                                    </section>
                                                </div>
                                                <div class="row">
                                                    <section class="col col-sm-4 col-md-3 col-lg-3">
                                                        <label class="text-left txt-color-blue font-md"><spring:message code="users.enabled"/></label>
                                                        <div class="row">
                                                            <div class="col col-4">
                                                                <label class="checkbox">
                                                                    <c:choose>
                                                                        <c:when test="${user.enabled}">
                                                                            <input type="checkbox" name="checkbox-enable" id="checkbox-enable" checked>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <input type="checkbox" name="checkbox-enable" id="checkbox-enable">
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                    <i></i></label>
                                                            </div>
                                                        </div>
                                                    </section>
                                                    <section class="col col-sm-6 col-md-4 col-lg-4">
                                                        <label class="text-left txt-color-blue font-md">
                                                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="users.lab" />
                                                        </label>
                                                        <div class="input-group">
                                                            <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                                            <select id="laboratorio" name="laboratorio"
                                                                    class="select2">
                                                                <option value=""><spring:message code="lbl.select" />...</option>
                                                                <c:forEach items="${laboratorios}" var="laboratorio">
                                                                    <c:choose>
                                                                        <c:when test="${labUser.codigo == laboratorio.codigo}">
                                                                            <option selected value="${laboratorio.codigo}">${laboratorio.nombre}</option>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <option value="${laboratorio.codigo}">${laboratorio.nombre}</option>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </c:forEach>

                                                            </select>
                                                        </div>
                                                    </section>
                                                </div>
                                            </fieldset>
                                            <footer>
                                                <input id="msjSuccessful" type="hidden" value="<spring:message code="msg.user.updated"/>"/>
                                                <input id="disappear" type="hidden" value="<spring:message code="msg.disappear"/>"/>
                                                <button type="submit" class="btn btn-success">
                                                    <i class="icon-ok"> <fmt:message key="users.save" /></i>
                                                </button>
                                                <spring:url value="/usuarios/admin/{username}"
                                                            var="usuarioUrl">
                                                    <spring:param name="username" value="${user.username}" />
                                                </spring:url>
                                                <button type="reset"
                                                        onclick="location.href='${fn:escapeXml(usuarioUrl)}'"
                                                        class="btn btn-danger">
                                                    <i class="icon-remove"> <fmt:message key="users.cancel" /></i>
                                                </button>
                                            </footer>
										</form>
                            </div>
                            <!-- end widget div -->
                        </div>
                        <!-- end widget -->
                    </div>
                </article>

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
<spring:url value="/resources/scripts/usuarios/users.js" var="usersjs" />
<script src="${usersjs}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<spring:url value="/personas/search" var="sPersonUrl"/>
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="updateUserUrl" value="/usuarios/actualizarUsuario"/>
<c:url var="adminUserUrl" value="/usuarios/admin/"/>
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {
            sUpdateUserUrl : "${updateUserUrl}",
            sAdminUserUrl : "${adminUserUrl}",
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
