<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html>
<!-- BEGIN HEAD -->
<head>
    <jsp:include page="../fragments/headTag.jsp" />
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
            <i class="fa fa-angle-right"></i> <a href="<spring:url value="/reports/consolidatedexams/init" htmlEscape="true "/>"><spring:message code="menu.report.consol.exams" /></a></li>
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
							<spring:message code="menu.report.consol.exams" />
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
                <input id="text_opt_select" type="hidden" value="<spring:message code="lbl.select"/>"/>
                <input id="msg_email_ok" type="hidden" value="<spring:message code="msg.email.sent"/>"/>
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
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.dxs.large" /> </label>
                                <div class="input-group">
                                    <span class="input-group-addon"> <i class="fa fa-list"></i></span>
                                    <select  name="idDx" id="idDx" multiple data-placeholder="<spring:message code="act.select" /> <spring:message code="lbl.dxs.large" />" class="select2">
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
                            <section class="col col-sm-12 col-md-4 col-lg-4">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="sp.year" />
                                </label>
                                <div class="input-group">
                                    <span class="input-group-addon"></span>
                                    <select name="anio" id="anio" data-placeholder="<spring:message code="act.select" /> <spring:message code="sp.year" />" class="select2">
                                        <option value=""></option>
                                        <c:forEach items="${anios}" var="anio">
                                            <option value="${anio.valor}">${anio.valor}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </section>
                            <section class="col col-4">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="week1" />
                                </label>
                                <div class="input-group">
                                    <span class="input-group-addon"></span>
                                    <select name="semI" id="semI" data-placeholder="<spring:message code="act.select" /> <spring:message code="week1" />" class="select2">
                                        <option value=""></option>
                                        <c:forEach items="${semanas}" var="semana">
                                            <option value="${semana.valor}">${semana.valor}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </section>
                            <section class="col col-4">
                                <label class="text-left txt-color-blue font-md">
                                    <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="week2" />
                                </label>
                                <div class="input-group">
                                    <span class="input-group-addon"></span>
                                    <select name="semF" id="semF" data-placeholder="<spring:message code="act.select" /> <spring:message code="week2" />" class="select2">
                                        <option value=""></option>
                                        <c:forEach items="${semanas}" var="semana">
                                            <option value="${semana.valor}">${semana.valor}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </section>
                        </div>
                        <!-- START ROW -->
                        <div class="row">
                            <section class="col col-xs-12 col-sm-12 col-md-12 col-lg-12">
                                <div class="inline-group">
                                    <section class="col col-xs-6 col-sm-6 col-md-6 col-lg-6">
                                        <label class="radio">
                                            <input type="radio" name="rbFechaBusqueda" value="FIS" id="FIS" checked="checked">
                                            <i></i><spring:message code="lbl.consolidate.by"/> <spring:message code="lbl.fis.short"/></label>
                                    </section>
                                    <section class="col col-xs-6 col-sm-6 col-md-6 col-lg-6">
                                        <label class="radio">
                                            <input type="radio" name="rbFechaBusqueda" id="rbFA" value="FPROC">
                                            <i></i><spring:message code="lbl.consolidate.by"/> <spring:message code="lbl.processing.date"/></label>
                                    </section>
                                </div>
                            </section>
                        </div>
                        <!-- END ROW -->
                        <!-- END ROW -->
                        <footer>
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
<!-- WIDGET END -->

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
<spring:url value="/resources/scripts/reportes/consolidatedExams.js" var="porResJS" />
<script src="${porResJS}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<c:set var="msgTitle"><spring:message code="lbl.alert" /></c:set>
<c:set var="msgNoData"><spring:message code="lbl.no.data.found" /></c:set>
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="excelUrl" value="/reports/consolidadoTecnica" />
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {
            blockMess:"${blockMess}",
            msgNoData: "${msgNoData}",
            msgTitle: "${msgTitle}",
            excelUrl: "${excelUrl}"
        };
        consolidatedExams.init(parametros);
        handleDatePickers("${pageContext.request.locale.language}");
        $("li.reportes").addClass("open");
        $("li.consolidatedexams").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.consolidatedexams").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>