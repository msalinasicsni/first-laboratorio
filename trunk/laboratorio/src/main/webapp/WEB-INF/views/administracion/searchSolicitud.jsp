<%--
  Created by IntelliJ IDEA.
  User: souyen-ics
  Date: 03-02-15
  Time: 03:59 PM
  To change this template use File | Settings | File Templates.
--%>
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
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/administracion/respuestasSolicitud/init" htmlEscape="true "/>"><spring:message code="menu.admin.request.aswers" /></a></li>
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
                <i class="fa-fw fa fa-list-alt"></i>
                <spring:message code="menu.admin.request.aswers" />
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="lbl.diagnostic.search" />
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
                        <span class="widget-icon"> <i class="fa fa-search"></i> </span>
                        <h2><spring:message code="lbl.parameters" /> </h2>
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
                            <input id="text_opt_select" type="hidden" value="<spring:message code="lbl.select"/>"/>
                            <input id="smallBox_content" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                            <input id="msg_no_results_found" type="hidden" value="<spring:message code="msg.no.results.found"/>"/>
                            <form id="search-form" class="smart-form" autocomplete="off">
                                <fieldset>
                                    <div class="row">
                                        <section class="col col-sm-12 col-md-7 col-lg-3">
                                            <label class="text-left txt-color-blue font-md">
                                                <spring:message code="lbl.request.type" /> </label>
                                            <div class="input-group">
                                                <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                                <select id="tipo" name="tipo"
                                                        class="select2">
                                                    <option value=""><spring:message code="lbl.select" />...</option>
                                                    <option value="Estudio"><spring:message code="lbl.study" /></option>
                                                    <option value="Rutina"><spring:message code="lbl.routine" /></option>
                                                </select>
                                            </div>
                                        </section>

                                        <section class="col col-sm-12 col-md-12 col-lg-5">
                                            <label class="text-left txt-color-blue font-md">
                                                <spring:message code="lbl.desc.request"/>
                                            </label>
                                            <label class="input"><i class="icon-prepend fa fa-pencil"></i> <i
                                                    class="icon-append fa fa-sort-alpha-asc"></i>
                                                <input type="text" id="nombre" name="nombre"
                                                       placeholder="<spring:message code="lbl.name"/>">
                                                <b class="tooltip tooltip-bottom-right"><i
                                                        class="fa fa-warning txt-color-pink"></i><spring:message
                                                        code="msg.enter.diagnostic.name"/></b>
                                            </label>
                                        </section>
                                    </div>
                                </fieldset>
                                <footer>
                                    <button type="submit" id="search-request" class="btn btn-info"><i class="fa fa-search"></i> <spring:message code="act.search" /></button>
                                    <button type="button" id="all-request" class="btn btn-info"><i class="fa fa-search"></i> <spring:message code="act.show.all" /></button>
                                </footer>
                            </form>
                        </div>
                        <!-- end widget content -->
                    </div>
                    <!-- end widget div -->
                </div>
                <!-- end widget -->
            </article>
            <!-- WIDGET END -->
            <!-- NEW WIDGET START -->
            <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                <!-- Widget ID (each widget will need unique ID)-->
                <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
                    <header>
                        <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
                        <h2><spring:message code="lbl.search.result" /> </h2>
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
                            <table id="records_dx" class="table table-striped table-bordered table-hover" width="100%">
                                <thead>
                                <tr>
                                    <th data-class="expand"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.desc.request"/></th>
                                    <th data-hide="phone"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.receipt.pcr.area"/></th>
                                    <th></th>
                                </tr>
                                </thead>
                            </table>
                        </div>
                        <!-- end widget content -->
                    </div>
                    <!-- end widget div -->
                </div>
                <!-- end widget -->
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
<spring:url value="/resources/scripts/administracion/requestAnswers.js" var="answersDx" />
<script src="${answersDx}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="sCreateConceptUrl" value="/administracion/respuestasSolicitud/create/"/>
<c:url var="searchDxUrl" value="/administracion/respuestasSolicitud/getDx"/>
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {blockMess: "${blockMess}",
            sActionUrl : "${sCreateConceptUrl}",
            searchDxUrl : "${searchDxUrl}"


        };
        DxAnswers.init(parametros);

        $("li.administracion").addClass("open");
        $("li.respuestaSolicitud").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.respuestaSolicitud").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>