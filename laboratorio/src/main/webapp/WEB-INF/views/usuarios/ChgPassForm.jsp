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
            <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/usuarios/list" htmlEscape="true "/>"><spring:message code="menu.admin.users" /></a></li>
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
							<spring:message code="act.change.pass" />
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
                            <h2><spring:message code="lbl.person" /> </h2>
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
									<div class="">
										<form class="smart-form" autocomplete="off"
											id="cpass-user-form">
                                            <fieldset>
                                                <div class="row">
                                                    <section class="col col-sm-12 col-md-12 col-lg-12">
                                                        <label class="text-left txt-color-blue font-md">
                                                            <fmt:message key="users.user" />
                                                            :
                                                        </label>
                                                        <label class="text-left font-md">
                                                            <c:out value="${user.username}" />
                                                        </label>
                                                    </section>
                                                </div>
                                                <div class="row">
                                                    <section class="col col-sm-12 col-md-12 col-lg-12">
                                                        <label class="text-left txt-color-blue font-md">
                                                            <fmt:message key="users.desc" />
                                                            :
                                                        </label>
                                                        <label class="text-left font-italic font-md">
                                                            <c:out value="${user.completeName}" />
                                                        </label>
                                                    </section>
                                                </div>
                                            </fieldset>
                                            <fieldset>
                                                <div class="row">
                                                    <section class="col col-sm-6 col-md-3 col-lg-3">
                                                        <label class="text-left txt-color-blue font-md">
                                                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="users.pass1" />
                                                        </label>
                                                        <div class="">
                                                            <label class="input">
                                                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-lock fa-fw"></i>
                                                                <input class="form-control" id="password" type="password"  name="password" placeholder=" <spring:message code="users.pass1" />">
                                                                <b class="tooltip tooltip-bottom-right"> <i
                                                                        class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.users.pass1"/>
                                                                </b>
                                                            </label>
                                                        </div>
                                                    </section>
                                                    <section class="col col-sm-6 col-md-3 col-lg-3">
                                                        <label class="text-left txt-color-blue font-md">
                                                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="users.pass2" />
                                                        </label>
                                                        <div class="">
                                                            <label class="input">
                                                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-lock fa-fw"></i>
                                                                <input class="form-control" id="confirm_password" name="confirm_password"  type="password" placeholder=" <spring:message code="users.pass2" />">
                                                                <b class="tooltip tooltip-bottom-right"> <i
                                                                        class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.users.pass2"/>
                                                                </b>
                                                            </label>
                                                        </div>
                                                    </section>
                                                </div>
                                            </fieldset>
											<footer>
                                                <input id="msjSuccessful" type="hidden" value="<spring:message code="msg.user.changePass"/>"/>
                                                <input id="disappear" type="hidden" value="<spring:message code="msg.disappear"/>"/>
                                                <input type="hidden" value="${user.username}" id="username" />
                                                    <button type="submit" class="btn btn-success">
                                                        <i class="fa fa-save"></i>  <spring:message code="users.save" />
                                                    </button>
                                                <c:choose>
                                                    <c:when test="${origen == 'usuario'}">
                                                        <spring:url value="/usuarios/admin/{username}"
                                                                    var="cancelUrl">
                                                            <spring:param name="username" value="${user.username}" />
                                                        </spring:url>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <spring:url value="/usuarios/list"
                                                                    var="cancelUrl"/>
                                                    </c:otherwise>
                                                </c:choose>
                                                    <button type="reset"
                                                            onclick="location.href='${fn:escapeXml(cancelUrl)}'"
                                                            class="btn btn-danger">
                                                        <i class="fa fa-times"></i>  <spring:message code="users.cancel" />
                                                    </button>
											</footer>

										</form>
									</div>
                            </div>
                            <!-- end widget -->
                        </div>
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
<spring:url value="/resources/scripts/usuarios/users.js" var="userJs" />
<script src="${userJs}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<spring:url value="/personas/search" var="sPersonUrl"/>
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="changePassUrl" value="/usuarios/changePassword"/>
<spring:url value="/usuarios/list" var="usuariosUrl"/>
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {sChangePassUrl: "${changePassUrl}",
            blockMess: "${blockMess}",
            sUsuariosUrl : "${usuariosUrl}"
        };
        Users.init(parametros);
        $("li.administracion").addClass("open");
        $("li.users").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.users").parents("ul").slideDown(200);
        }
        $('#fechaNacimiento').change();
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>
