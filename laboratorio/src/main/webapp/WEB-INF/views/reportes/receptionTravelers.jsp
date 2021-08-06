<%--
  Created by IntelliJ IDEA.
  User: souyen-ics
  Date: 04-24-15
  Time: 04:40 PM
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
    <spring:url value="/resources/img/plus.png" var="plus"/>
    <spring:url value="/resources/img/minus.png" var="minus"/>
    <style>
        textarea {
            resize: none;
        }
        /*tr.active {
            color: #3276B1!important;
        }*/

        td.details-control {
            background: url("${plus}") no-repeat center center;
            cursor: pointer;
        }
        tr.shown td.details-control {
            background: url("${minus}") no-repeat center center;
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
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/reports/receptionTravelers/init" htmlEscape="true "/>"><spring:message code="lbl.reception.travelers" /></a></li>
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
    <div class="col-xs-12 col-sm-7 col-md-7 col-lg-7">
        <h1 class="page-title txt-color-blueDark">
            <!-- PAGE HEADER -->
            <i class="fa-fw fa fa-list"></i>
            <spring:message code="lbl.reception.travelers" />

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
<article class="col-xs-12 col-sm-12 col-md-12 col-lg-6">
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
                <input id="smallBox_content" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                <input id="msg_no_results_found" type="hidden" value="<spring:message code="msg.no.results.found"/>"/>
                <input id="fechaActual" type="hidden" value="${fechaActual}">

                <form id="received-samples-form" class="smart-form" autocomplete="off">
                    <fieldset>
                        <div class="row">
                            <section class="col col-sm-6 col-md-6 col-lg-6">
                                <label class="text-left txt-color-blue font-md">
                                    <spring:message code="lbl.receipt.start.date" />
                                </label>
                                <label class="input">
                                    <i class="icon-prepend fa fa-pencil"></i> <i class="icon-append fa fa-calendar"></i>
                                    <input type="text" name="fecInicioRecepcion" id="fecInicioRecepcion"
                                           placeholder="<spring:message code="lbl.date.format"/>"
                                           class="form-control from_date" data-date-end-date="+0d"/>
                                    <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.check-in.startdate"/></b>
                                </label>
                            </section>
                            <section class="col col-sm-6 col-md-6 col-lg-6">
                                <label class="text-left txt-color-blue font-md">
                                    <spring:message code="lbl.receipt.end.date" />
                                </label>
                                <label class="input">
                                    <i class="icon-prepend fa fa-pencil"></i> <i class="icon-append fa fa-calendar"></i>
                                    <input type="text" name="fecFinRecepcion" id="fecFinRecepcion"
                                           placeholder="<spring:message code="lbl.date.format"/>"
                                           class="form-control to_date" data-date-end-date="+0d"/>
                                    <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.check-in.enddate"/></b>
                                </label>
                            </section>

                        </div>
                    </fieldset>
                    <footer>
                        <button type="button" id="all-orders" class="btn btn-info"><i class="fa fa-search"></i> <spring:message code="act.show.all" /></button>
                        <button type="submit" id="search-orders" class="btn btn-info"><i class="fa fa-search"></i> <spring:message code="act.search" /></button>
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
<article class="col-xs-12 col-sm-12 col-md-12 col-lg-6">
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
                <table id="received-samples" class="table table-striped table-bordered table-hover" width="100%">
                    <thead>
                    <tr>
                        <th data-class="expand"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.reception.date.2"/></th>
                        <th><spring:message code="lbl.online.result"/></th>
                        <th><spring:message code="lbl.at.reception"/></th>
                        <th><spring:message code="lbl.total"/></th>
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
<!-- Table Tools Path-->
<spring:url value="/resources/js/plugin/datatables/swf/copy_csv_xls_pdf.swf" var="tabletools" />
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
<!-- JQUERY INPUT MASK -->
<spring:url value="/resources/js/plugin/jquery-inputmask/jquery.inputmask.bundle.min.js" var="jqueryInputMask" />
<script src="${jqueryInputMask}"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<spring:url value="/resources/scripts/reportes/receptionTravelers.js" var="receptionReport" />
<script src="${receptionReport}"></script>
<spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers" />
<script src="${handleDatePickers}"></script>
<spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
<script src="${handleInputMask}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="searchUrl" value="/reports/receptionTravelers/search"/>

<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {searchUrl : "${searchUrl}",
            blockMess: "${blockMess}",
            sTableToolsPath : "${tabletools}"


        };
        ReceptionTravelers.init(parametros);

        handleDatePickers("${pageContext.request.locale.language}");
        handleInputMasks();
        $("li.reportes").addClass("open");
        $("li.receptionTravelers").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.receptionTravelers").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>
