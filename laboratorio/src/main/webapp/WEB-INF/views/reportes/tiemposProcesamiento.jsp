<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html>
<!-- BEGIN HEAD -->
<head>
    <jsp:include page="../fragments/headTag.jsp" />
    <style>
        /* columns right and center aligned datatables */
        .aw-right {
            padding-left: 0;
            padding-right: 10px;
            text-align: right;
        }
        td.highlight {
            font-weight: bold;
            color: red;
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
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i>
            <spring:message code="menu.reports" />
            <i class="fa fa-angle-right"></i> <a href="<spring:url value="/reports/reportResultDx/init" htmlEscape="true "/>"><spring:message code="menu.report.proc.times" /></a></li>
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
            <i class="fa-fw fa fa-line-chart"></i>
            <spring:message code="menu.reports" />
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="menu.report.proc.times" />
						</span>
        </h1>
    </div>
    <!-- end col -->
    <!-- right side of the page with the sparkline graphs -->
    <!-- col -->
    <div class="col-xs-12 col-sm-5 col-md-5 col-lg-8">
        <!-- sparks -->
        <ul id="sparks">

        </ul>
        <!-- end sparks -->
    </div>
    <!-- end col -->
</div>
<!-- end row -->
<!--
    The ID "widget-grid" will start to initialize all widgets below
    You do not need to use widgets if you dont want to. Simply remove
    the <section></section> and you can use wells or panels instead
    -->
<!-- widget grid -->
<section id="widget-grid" class="">
<!-- row -->
<div class="row">
<!-- NEW WIDGET START -->
<article class="col-xs-12 col-sm-12 col-md-8 col-lg-8">
    <!-- Widget ID (each widget will need unique ID)-->
    <div class="jarviswidget jarviswidget-color-darken" id="wid-id-0">
        <!-- widget options:
            usage: <div class="jarviswidget" id="wid-id-0" data-widget-editbutton="false">
            data-widget-colorbutton="false"
            data-widget-editbutton="false"
            data-widget-togglebutton="false"
            data-widget-deletebutton="false"
            data-widget-fullscreenbutton="false"
            data-widget-custombutton="false"
            data-widget-collapsed="true"
            data-widget-sortable="false"
        -->
        <header>
            <span class="widget-icon"> <i class="fa fa-wrench"></i> </span>
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
            <div class="widget-body">
                <input id="from" type="hidden" value="<spring:message code="lbl.from"/>"/>
                <input id="to" type="hidden" value="<spring:message code="lbl.to"/>"/>
                <input id="silaisT" type="hidden" value="<spring:message code="lbl.silais"/>"/>
                <input id="municT" type="hidden" value="<spring:message code="lbl.muni"/>"/>
                <input id="usT" type="hidden" value="<spring:message code="lbl.health.unit"/>"/>
                <input id="text_opt_select" type="hidden" value="<spring:message code="lbl.select"/>"/>
                <input id="msg_email_ok" type="hidden" value="<spring:message code="msg.email.sent"/>"/>
                <input id="fileName" type="hidden" value="reporteResDx"/>
                <input id="fileTitle" type="hidden" value="<spring:message code="menu.report.proc.times"/>"/>
                <form id="result_form" class ="smart-form">
                    <fieldset>
                        <!-- START ROW -->
                        <div class="row">
                            <section class="col col-sm-12 col-md-12 col-lg-12">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.lab" /> </label>
                                <div class="input-group">
                                    <span class="input-group-addon"> <i class="fa fa-list"></i></span>
                                    <select  name="codigoLab" id="codigoLab" data-placeholder="<spring:message code="act.select" /> <spring:message code="lbl.lab" />" class="select2">
                                        <option value=""></option>
                                        <c:forEach items="${laboratorios}" var="lab">
                                            <option value="${lab.codigo}">${lab.nombre}</option>
                                        </c:forEach>
                                        <c:if test="${fn:length(laboratorios) gt 1}">
                                            <option value="ALL"><spring:message code="act.show.all" /></option>
                                        </c:if>
                                    </select>
                                </div>
                            </section>
                        </div>
                        <!-- END ROW -->
                        <!-- START ROW -->
                        <div class="row">
                            <section class="col col-sm-12 col-md-12 col-lg-12">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.request.large" /> </label>
                                <div class="input-group">
                                    <span class="input-group-addon"> <i class="fa fa-list"></i></span>
                                    <select  name="idDx" id="idDx" data-placeholder="<spring:message code="act.select" /> <spring:message code="lbl.request.large" />" class="select2">
                                        <option value=""></option>
                                        <c:forEach items="${dxs}" var="dx">
                                            <option value="${dx.idDiagnostico}-R">${dx.nombre}</option>
                                        </c:forEach>
                                        <c:forEach items="${estudios}" var="est">
                                            <option value="${est.idEstudio}-E">${est.nombre}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </section>
                        </div>
                        <!-- END ROW -->
                        <!-- START ROW -->
                        <div class="row">
                            <section class="col col-sm-12 col-md-12 col-lg-12">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.level" /> </label>
                                <div class="input-group">
                                    <span class="input-group-addon"> <i class="fa fa-location-arrow"></i></span>
                                    <select  name="codArea" id="codArea" data-placeholder="<spring:message code="act.select" /> <spring:message code="lbl.level" />" class="select2">
                                        <option value=""></option>
                                        <c:forEach items="${areas}" var="area">
                                            <option value="${area.codigo}">${area.valor}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </section>
                        </div>
                        <!-- END ROW -->
                        <!-- START ROW -->
                        <div id="dNivelPais" hidden="hidden" class="row">
                            <section class="col col-xs-12 col-sm-12 col-md-12 col-lg-12">
                                <div class="inline-group">
                                    <section class="col col-xs-6 col-sm-6 col-md-6 col-lg-6">
                                        <label class="radio">
                                            <input type="radio" name="rbNivelPais" id="rbNPSILAIS" value="true" checked="checked">
                                            <i></i><spring:message code="lbl.silais"/></label>
                                    </section>
                                    <section class="col col-xs-6 col-sm-6 col-md-6 col-lg-6">
                                        <label class="radio">
                                            <input type="radio" name="rbNivelPais" value="false" id="rbNPDepa">
                                            <i></i><spring:message code="lbl.department"/></label>
                                    </section>
                                </div>
                            </section>
                        </div>
                        <!-- END ROW -->
                        <!-- START ROW -->
                        <div class="row">
                            <section class="col col-sm-12 col-md-12 col-lg-12" id="silais" hidden="hidden">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.silais" /> </label>
                                <div class="input-group">
                                    <span class="input-group-addon"> <i class="fa fa-location-arrow"></i></span>
                                    <select data-placeholder="<spring:message code="act.select" /> <spring:message code="lbl.silais" />" name="codSilais" id="codSilais" class="select2">
                                        <option value=""></option>
                                        <c:forEach items="${entidades}" var="entidad">
                                            <option value="${entidad.codigo}">${entidad.nombre}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </section>
                        </div>
                        <!-- END ROW -->
                        <!-- START ROW -->
                        <div class="row">
                            <section class="col col-sm-12 col-md-12 col-lg-12" id="municipio" hidden="hidden">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.muni" /> </label>
                                <div class="input-group">
                                    <span class="input-group-addon"> <i class="fa fa-location-arrow"></i></span>
                                    <select data-placeholder="<spring:message code="act.select" /> <spring:message code="lbl.muni" />" name="codMunicipio" id="codMunicipio" class="select2">
                                        <option value=""></option>
                                    </select>
                                </div>
                            </section>
                        </div>
                        <!-- END ROW -->
                        <!-- START ROW -->
                        <div class="row">
                            <section class="col col-sm-12 col-md-12 col-lg-12" id="unidad" hidden="hidden">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.health.unit" /> </label>
                                <div class="input-group">
                                    <span class="input-group-addon"> <i class="fa fa-location-arrow"></i></span>
                                    <select data-placeholder="<spring:message code="act.select" /> <spring:message code="lbl.health.unit" />" name="codUnidadAtencion" id="codUnidadAtencion" class="select2">
                                        <option value=""></option>
                                    </select>
                                </div>
                            </section>
                        </div>
                        <!-- END ROW -->
                        <!-- START ROW -->
                        <div class="row">

                            <section class="col col-sm-12 col-md-12 col-lg-6">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>
                                    <spring:message code="lbl.receipt.start.date.mx" />
                                </label>
                                <label class="input">
                                    <i class="icon-prepend fa fa-pencil"></i> <i
                                        class="icon-append fa fa-calendar fa-fw"></i>
                                    <input class="form-control date-picker" type="text" name="initDate" id="initDate" placeholder=" <spring:message code="lbl.receipt.start.date.mx"/>"/>
                                </label>
                            </section>
                            <section class="col col-sm-12 col-md-6 col-lg-6">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.receipt.end.date.mx"/>
                                </label>
                                <label class="input">
                                    <i class="icon-prepend fa fa-pencil"></i> <i
                                        class="icon-append fa fa-calendar fa-fw"></i>
                                    <input class="form-control date-picker" type="text" name="endDate" id="endDate" placeholder=" <spring:message code="lbl.receipt.end.date.mx"/>"/>
                                </label>
                           </section>
                        </div>
                        <!-- END ROW -->
                        <footer>
                            <!--<button type="submit" class="btn btn-info"><i class="fa fa-refresh"></i> <spring:message code="act.refresh" /></button> -->
                            <a id="exportExcel" class="btn btn-success btn-lg pull-right header-btn" href="#"><i class="fa fa-file-excel-o"></i> <spring:message code="lbl.export.excel" /></a>
                        </footer>
                    </fieldset>
                </form>
            </div>
            <!-- end widget content -->
        </div>
        <!-- end widget div -->
    </div>
    <!-- end widget -->
</article>

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
<!-- JQUERY VALIDATE -->
<spring:url value="/resources/js/plugin/jquery-validate/jquery.validate.min.js" var="jqueryValidate" />
<script src="${jqueryValidate}"></script>
<spring:url value="/resources/js/plugin/jquery-validate/messages_{language}.js" var="jQValidationLoc">
    <spring:param name="language" value="${pageContext.request.locale.language}" /></spring:url>
<script src="${jQValidationLoc}"></script>
<!-- jQuery Select2 Input -->
<spring:url value="/resources/js/plugin/select2/select2.min.js" var="selectPlugin"/>
<script src="${selectPlugin}"></script>
<!-- jQuery Select2 Locale -->
<spring:url value="/resources/js/plugin/select2/select2_locale_{language}.js" var="selectPluginLocale">
    <spring:param name="language" value="${pageContext.request.locale.language}" /></spring:url>
<script src="${selectPluginLocale}"></script>
<!-- bootstrap datepicker -->
<spring:url value="/resources/js/plugin/bootstrap-datepicker/bootstrap-datepicker.js" var="datepickerPlugin" />
<script src="${datepickerPlugin}"></script>
<spring:url value="/resources/js/plugin/bootstrap-datepicker/locales/bootstrap-datepicker.{languagedt}.js" var="datePickerLoc">
    <spring:param name="languagedt" value="${pageContext.request.locale.language}" /></spring:url>
<script src="${datePickerLoc}"></script>
<!-- DATE PICKER -->
<spring:url value="/resources/scripts/utilidades/handleDatePickers.js" var="handleDatePickers"/>
<script src="${handleDatePickers}"></script>
<!-- JQUERY BLOCK UI -->
<spring:url value="/resources/js/plugin/jquery-blockui/jquery.blockUI.js" var="jqueryBlockUi" />
<script src="${jqueryBlockUi}"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<spring:url value="/resources/scripts/reportes/tiemposProcesamiento.js" var="porResJS" />
<script src="${porResJS}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="msgTitle"><spring:message code="lbl.alert" /></c:set>
<c:set var="msgNoData"><spring:message code="lbl.no.data.found" /></c:set>
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="unidadesURL" value="/api/v1/unidadesPrimHosp"/>
<c:url var="municipiosURL" value="/api/v1/municipiosbysilais"/>
<c:url var="sExcelUrl" value="/reports/tiemposProcesamiento"/>
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {
            blockMess:"${blockMess}",
            sUnidadesUrl: "${unidadesURL}",
            sMunicipiosUrl:"${municipiosURL}",
            msgNoData: "${msgNoData}",
            msgTitle: "${msgTitle}",
            sExcelUrl: "${sExcelUrl}"
        };
        tiemposProcesamiento.init(parametros);
        handleDatePickers("${pageContext.request.locale.language}");
        $("li.reportes").addClass("open");
        $("li.tiemposProcesamiento").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.tiemposProcesamiento").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>