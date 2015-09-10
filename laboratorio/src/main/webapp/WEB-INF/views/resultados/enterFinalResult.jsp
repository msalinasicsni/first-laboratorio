<%--
  Created by IntelliJ IDEA.
  User: souyen-ics
  Date: 02-23-15
  Time: 11:28 AM
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
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/resultadoFinal/init" htmlEscape="true "/>"><spring:message code="menu.enter.final.result" /></a></li>
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
            <i class="fa-fw fa fa-file-text-o"></i>
            <spring:message code="lbl.final.result" />
						<span><i class="fa fa-angle-right"></i>
							<spring:message code="lbl.register" />
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
                    <span class="widget-icon"> <i class="fa fa-list"></i> </span>
                    <h2><spring:message code="lbl.exams.produced" /> </h2>
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
                        <input id="msg_no_results_found" type="hidden" value="<spring:message code="msg.no.records.found"/>"/>
                        <input id="disappear" type="hidden" value="<spring:message code="msg.disappear"/>"/>
                        <input id="msg_result_added" type="hidden" value="<spring:message code="msg.result.successfully.added"/>"/>
                        <input id="msg_result_override" type="hidden" value="<spring:message code="msg.result.successfully.canceled"/>"/>
                        <input id="text_value" type="hidden" value="<spring:message code="lbl.result.value"/>"/>
                        <input id="text_date" type="hidden" value="<spring:message code="lbl.result.date"/>"/>
                        <input id="study_not_answers" type="hidden" value="<spring:message code="msg.dx.not.answers.found"/>"/>
                        <input id="text_response" type="hidden" value="<spring:message code="lbl.approve.response"/>"/>
                        <input type="hidden" id="rutina" value="${rutina}"/>
                        <input type="hidden" id="estudio" value="${estudio}"/>

                        <table id="records_exa" class="table table-striped table-bordered table-hover" width="100%">
                            <thead>
                            <tr>
                                <th data-class="expand"><i class="fa fa-fw fa-calendar text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.request.date"/></th>
                                <th data-hide="phone"><i class="fa fa-fw fa-sort-alpha-asc text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.request.large"/></th>
                                <th data-hide="phone"><i class="fa fa-fw fa-barcode text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.unique.code.mx"/></th>
                                <th data-hide="phone"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.sample.type"/></th>
                                <th data-hide="phone"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.notification.type"/></th>
                                <th data-hide="phone"><i class="fa fa-fw fa-sort-alpha-asc text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.test.name"/></th>
                                <th data-hide="phone"><i class="fa fa-fw fa-sort-alpha-asc text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.lab"/></th>
                                <th data-hide="phone"><i class="fa fa-fw fa-sort-alpha-asc text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.processed"/></th>
                                <th data-class="phone"><i class="fa fa-fw fa-user text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.receipt.person.applicant.name"/></th>
                                <th data-class="phone"><i class="fa fa-fw fa-file-text-o  text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.exam.result"/></th>
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
        <!-- NEW WIDGET START -->
        <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
            <!-- Widget ID (each widget will need unique ID)-->
            <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
                <header>
                    <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
                    <h2><spring:message code="lbl.final.result" /> </h2>
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
                        <form id="result-form" class="smart-form" autocomplete="off">
                        <fieldset>
                            <header>
                                <spring:message code="lbl.header.result.orders.form" />
                            </header>
                            <br>
                            <div id="resultados">
                            </div>
                        </fieldset>
                        <footer>
                            <input id="val_yes" type="hidden" value="<spring:message code="lbl.yes"/>"/>
                            <input id="val_no" type="hidden" value="<spring:message code="lbl.no"/>"/>
                            <input id="idDx" type="hidden" value="${rutina.codDx.idDiagnostico}"/>
                            <input id="idSolicitud" type="hidden" value="${rutina.idSolicitudDx}"/>
                            <input id="idSolicitudE" type="hidden" value="${estudio.idSolicitudEstudio}"/>
                            <input id="idEstudio" type="hidden" value="${estudio.tipoEstudio.idEstudio}"/>
                            <button type="button" id="override-result" class="btn btn-danger btn-lg pull-right header-btn"><i class="fa fa-times"></i> <spring:message code="act.override" /></button>
                            <button type="submit" id="save-result" class="btn btn-success btn-lg pull-right header-btn"><i class="fa fa-save"></i> <spring:message code="act.save" /></button>
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
    </div>
    <!-- end row -->
    <!-- row -->
    <div class="row">
        <!-- a blank row to get started -->
        <div class="col-sm-12">
            <!-- your contents here -->
            <!-- Modal -->
            <div class="modal fade" id="myModal" aria-hidden="true" data-backdrop="static">
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
                                    <spring:message code="lbl.result.header.modal.override" />
                                </h4>
                            </div>
                        </div>
                        <div class="modal-body"> <!--  no-padding -->
                            <form id="override-result-form" class="smart-form" novalidate="novalidate">
                                <fieldset>
                                    <div class="row">
                                        <section class="col col-sm-12 col-md-12 col-lg-12">
                                            <label class="text-left txt-color-blue font-md">
                                                <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.nullified" /> </label>
                                            <div class="">
                                                <label class="textarea">
                                                    <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                    <textarea class="form-control" rows="3" name="causaAnulacion" id="causaAnulacion"
                                                              placeholder="<spring:message code="lbl.nullified" />"></textarea>
                                                    <b class="tooltip tooltip-bottom-right"> <i
                                                            class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.nullified"/>
                                                    </b>
                                                </label>
                                            </div>
                                        </section>
                                    </div>
                                </fieldset>
                                <footer>
                                    <button type="submit" class="btn btn-primary" id="btnAceptarAnulacion">
                                        <spring:message code="act.ok" />
                                    </button>
                                    <button type="button" class="btn btn-default" data-dismiss="modal">
                                        <spring:message code="act.cancel" />
                                    </button>

                                </footer>

                            </form>
                        </div>
                    </div><!-- /.modal-content -->
                </div><!-- /.modal-dialog -->
            </div><!-- /.modal -->
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
<spring:url value="/resources/js/plugin/datatables/swf/copy_csv_xls_pdf.swf" var="tabletools" />
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
<spring:url value="/resources/scripts/resultados/enterFinalResult.js" var="enterFinalResult" />
<script src="${enterFinalResult}"></script>
<spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers" />
<script src="${handleDatePickers}"></script>
<spring:url value="/resources/scripts/utilidades/calcularEdad.js" var="calculateAge" />
<script src="${calculateAge}"></script>
<spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
<script src="${handleInputMask}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="searchUrl" value="/resultadoFinal/searchExams"/>
<c:url var="listasUrl" value="/resultadoFinal/getCatalogosListaConcepto"/>
<c:url var="detResultadosUrl" value="/resultadoFinal/getDetResFinalBySolicitud"/>
<c:url var="conceptosUrl" value="/administracion/respuestasSolicitud/getRespuestasActivas"/>
<c:url var="saveFinalResult" value="/resultadoFinal/saveFinalResult"/>
<c:url var="overrideUrl" value="/resultadoFinal/overrideResult"/>

<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();


        var parametros = {blockMess: "${blockMess}",
                          searchUrl:"${searchUrl}",
                          listasUrl:"${listasUrl}",
                          detResultadosUrl: "${detResultadosUrl}",
                          conceptosUrl: "${conceptosUrl}",
                          esIngreso : 'true',
                          saveFinalResult: "${saveFinalResult}",
                          overrideUrl: "${overrideUrl}"

        };
        enterFinalResult.init(parametros);

        handleDatePickers("${pageContext.request.locale.language}");
        handleInputMasks();
        $("li.resultado").addClass("open");
        $("li.enterFinalResult").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.enterFinalResult").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>
