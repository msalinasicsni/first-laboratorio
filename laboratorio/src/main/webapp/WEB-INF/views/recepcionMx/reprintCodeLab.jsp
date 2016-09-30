<%--
  Created by IntelliJ IDEA.
  User: souyen-ics
  Date: 06-08-15
  Time: 08:57 AM
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
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/reprint/init" htmlEscape="true "/>"><spring:message code="lbl.reprint.codelab" /></a></li>
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
            <i class="fa-fw fa fa-print"></i>
            <spring:message code="lbl.reprint.codelab" />

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
                <input id="msg_print_confirm_c" type="hidden" value="<spring:message code="msg.print.confirm"/>"/>
                <input id="msg_print_select" type="hidden" value="<spring:message code="msg.print.codesLab.select"/>"/>
                <input id="disappear" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                <input id="msg_no_results_found" type="hidden" value="<spring:message code="msg.no.results.found"/>"/>
                <input id="text_selected_all" type="hidden" value="<spring:message code="lbl.selected.all"/>"/>
                <input id="text_selected_none" type="hidden" value="<spring:message code="lbl.selected.none"/>"/>
                <input id="msg_confirm_print" type="hidden" value="<spring:message code="msg.confirm.title"/>"/>
                <input id="msg_print_canceled" type="hidden" value="<spring:message code="msg.code.printing.canceled"/>"/>
                <input id="yes" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.yes"/>"/>
                <input id="no" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.no"/>"/>
                <input id="text_request" type="hidden" value="<spring:message code="lbl.send.request"/>"/>
                <input id="text_request_date" type="hidden" value="<spring:message code="lbl.send.request.date"/>"/>
                <input id="text_request_type" type="hidden" value="<spring:message code="lbl.send.request.type"/>"/>
                <form id="searchReceptions-form" class="smart-form" autocomplete="off">
                    <fieldset>
                        <div class="row">
                            <section class="col col-sm-12 col-md-12 col-lg-5">
                                <label class="text-left txt-color-blue font-md">
                                    <spring:message code="lbl.receipt.person.applicant.name" />
                                </label>
                                <label class="input"><i class="icon-prepend fa fa-pencil"></i> <i class="icon-append fa fa-sort-alpha-asc"></i>
                                    <input type="text" id="txtfiltroNombre" name="filtroNombre" placeholder="<spring:message code="lbl.receipt.person.applicant.name"/>">
                                    <b class="tooltip tooltip-bottom-right"><i class="fa fa-warning txt-color-pink"></i><spring:message code="tooltip.receipt.person.applicant.name"/></b>
                                </label>
                            </section>
                            <section class="col col-sm-6 col-md-4 col-lg-3">
                                <label class="text-left txt-color-blue font-md">
                                    <spring:message code="lbl.receipt.start.date" />
                                </label>
                                <label class="input">
                                    <i class="icon-prepend fa fa-pencil"></i> <i class="icon-append fa fa-calendar"></i>
                                    <input type="text" name="fechaInicioRecep" id="fechaInicioRecep"
                                           placeholder="<spring:message code="lbl.date.format"/>"
                                           class="form-control from_date" data-date-end-date="+0d"/>
                                    <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.receipt.startdate"/></b>
                                </label>
                            </section>
                            <section class="col col-sm-6 col-md-4 col-lg-3">
                                <label class="text-left txt-color-blue font-md">
                                    <spring:message code="lbl.receipt.end.date" />
                                </label>
                                <label class="input">
                                    <i class="icon-prepend fa fa-pencil"></i> <i class="icon-append fa fa-calendar"></i>
                                    <input type="text" name="fechaFinRecepcion" id="fechaFinRecepcion"
                                           placeholder="<spring:message code="lbl.date.format"/>"
                                           class="form-control to_date" data-date-end-date="+0d"/>
                                    <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.receipt.enddate"/></b>
                                </label>
                            </section>

                        </div>
                        <div class="row">
                            <section class="col col-sm-12 col-md-5 col-lg-3">
                                <label class="text-left txt-color-blue font-md">
                                    <spring:message code="lbl.silais" /> </label>
                                <div class="input-group">
                                    <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                    <select id="codSilais" name="codSilais"
                                            class="select2">
                                        <option value=""><spring:message code="lbl.select" />...</option>
                                        <c:forEach items="${entidades}" var="entidad">
                                            <option value="${entidad.entidadAdtvaId}">${entidad.nombre}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </section>
                            <section class="col col-sm-12 col-md-7 col-lg-6">
                                <label class="text-left txt-color-blue font-md">
                                    <spring:message code="lbl.health.unit" /> </label>
                                <div class="input-group">
                                    <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                    <select id="codUnidadSalud" name="codUnidadSalud"
                                            class="select2">
                                        <option value=""><spring:message code="lbl.select" />...</option>
                                    </select>
                                </div>
                            </section>

                            <section class="col col-sm-6 col-md-6 col-lg-3">
                                <label class="text-left txt-color-blue font-md">
                                    <spring:message code="lbl.sample.type" /> </label>
                                <div class="input-group">
                                    <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                    <select id="codTipoMx" name="codTipoMx"
                                            class="select2">
                                        <option value=""><spring:message code="lbl.select" />...</option>
                                        <c:forEach items="${tipoMuestra}" var="tipoMuestra">
                                            <option value="${tipoMuestra.idTipoMx}">${tipoMuestra.nombre}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </section>
                        </div>


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

                            <section class="col col-sm-12 col-md-12 col-lg-4">
                                <label class="text-left txt-color-blue font-md">
                                    <spring:message code="lbl.desc.request" />
                                </label>
                                <label class="input"><i class="icon-prepend fa fa-pencil"></i> <i class="icon-append fa fa-sort-alpha-asc"></i>
                                    <input type="text" id="nombreSoli" name="nombreSoli" placeholder="<spring:message code="lbl.request.name"/>">
                                    <b class="tooltip tooltip-bottom-right"><i class="fa fa-warning txt-color-pink"></i><spring:message code="tooltip.send.request.name"/></b>
                                </label>
                            </section>

                            <section class="col col-sm-12 col-md-6 col-lg-3">
                                <label class="text-left txt-color-blue font-md">
                                    <spring:message code="lbl.unique.code.mx" /> </label>
                                <label class="input"><i class="icon-prepend fa fa-pencil"></i> <i class="icon-append fa fa-sort-alpha-asc"></i>
                                    <input type="text" id="txtCodUnicoMx" name="txtCodUnicoMx" placeholder="<spring:message code="lbl.unique.code.mx"/>">
                                    <b class="tooltip tooltip-bottom-right"><i class="fa fa-warning txt-color-pink"></i><spring:message code="tooltip.unique.code.mx"/></b>
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
                <table id="receptions-records" class="table table-striped table-bordered table-hover" width="100%">
                    <thead>
                    <tr>
                        <th data-class="expand"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.lab.code.mx"/></th>
                        <th data-hide="phone"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.sample.type"/></th>
                        <th data-hide="phone"><i class="fa fa-fw fa-calendar text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.receipt.dateTime"/></th>
                        <th data-hide="phone"><i class="fa fa-fw fa-calendar text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.sampling.datetime"/></th>
                        <th data-hide="phone"><spring:message code="lbl.silais"/></th>
                        <th data-hide="phone"><spring:message code="lbl.health.unit"/></th>
                        <th data-hide="phone"><i class="fa fa-fw fa-user text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.receipt.person.applicant.name"/></th>
                        <th><spring:message code="lbl.request"/></th>
                    </tr>
                    </thead>
                </table>
                <form class="smart-form" novalidate="novalidate">
                    <footer>
                        <button id="btnPrint" type="button" class="btn btn-success btn-md header-btn pull-right"><i class="fa fa-print"></i> <spring:message code="act.print" /></button>
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
        <div id="siteloader"></div>
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
<!-- Table Tools Path-->
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
<spring:url value="/resources/scripts/recepcionMx/reprintCodeLab.js" var="reprintCode" />
<script src="${reprintCode}"></script>
<spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers" />
<script src="${handleDatePickers}"></script>
<spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
<script src="${handleInputMask}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="searchUrl" value="/reprint/searchReceptionRecords"/>
<c:url var="unidadesURL" value="/api/v1/unidadesPrimariasHospSilais"/>
<spring:url var="printUrl" value="/print/barcode"/>
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {searchUrl : "${searchUrl}",
            sUnidadesUrl : "${unidadesURL}",
            blockMess: "${blockMess}",
            sTableToolsPath : "${tabletools}",
            printUrl: "${printUrl}"

        };
        ReprintCodeLab.init(parametros);

        handleDatePickers("${pageContext.request.locale.language}");
        handleInputMasks();
        $("li.recepcion").addClass("open");
        $("li.reprintLabCode").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.reprintLabCode").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>
