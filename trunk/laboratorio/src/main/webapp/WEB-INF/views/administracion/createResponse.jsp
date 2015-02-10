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
        .styleButton {

            float: right;
            height: 31px;
            margin: 10px 0px 0px 5px;
            padding: 0px 22px;
            font: 300 15px/29px "Open Sans", Helvetica, Arial, sans-serif;
            cursor: pointer;
        }
        .modal .modal-dialog {
            width: 60%;
        }
        .anularConcepto {
            padding-left: 0;
            padding-right: 10px;
            text-align: center;
            width: 5%;
        }
        .editarConcepto {
            padding-left: 0;
            padding-right: 10px;
            text-align: center;
            width: 5%;
        }
        .alert {
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
				<li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/administracion/createConcepts" htmlEscape="true "/>"><spring:message code="menu.admin.respuestas" /></a></li>
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
						<i class="fa-fw fa fa-group"></i> 
							<spring:message code="lbl.response.title" />
						<span> <i class="fa fa-angle-right"></i>  
							<spring:message code="lbl.response.create.subtitle" />
						</span>
					</h1>
				</div>
				<!-- end col -->
				<!-- right side of the page with the sparkline graphs -->
				<!-- col -->
				<div class="col-xs-12 col-sm-5 col-md-5 col-lg-8">
					<!-- sparks -->
					<ul id="sparks">
						<li class="sparks-info">
							<h5> <spring:message code="sp.day" /> <span class="txt-color-greenDark"><i class="fa fa-arrow-circle-down"></i>17</span></h5>
							<div class="sparkline txt-color-blue hidden-mobile hidden-md hidden-sm">
								0,1,3,4,11,12,11,13,10,11,15,14,20,17
							</div>
						</li>
						<li class="sparks-info">
							<h5> <spring:message code="sp.week" /> <span class="txt-color-red"><i class="fa fa-arrow-circle-up"></i>&nbsp;57</span></h5>
							<div class="sparkline txt-color-purple hidden-mobile hidden-md hidden-sm">
								23,32,11,23,33,45,44,54,45,48,57
							</div>
						</li>
						<li class="sparks-info">
							<h5> <spring:message code="sp.month" /> <span class="txt-color-red"><i class="fa fa-arrow-circle-up"></i>&nbsp;783</span></h5>
							<div class="sparkline txt-color-purple hidden-mobile hidden-md hidden-sm">
								235,323,114,231,333,451,444,541,451,483,783
							</div>
						</li>
					</ul>
					<!-- end sparks -->
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
                                <span class="widget-icon"> <i class="fa fa-th"></i> </span>
                                <h2><spring:message code="lbl.response.widgettitle" /> </h2>
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
                                    <input id="msg_no_results_found" type="hidden" value="<spring:message code="msg.no.response.found"/>"/>
                                    <input id="msg_receipt_added" type="hidden" value="<spring:message code="msg.receipt.successfully.added"/>"/>
                                    <input id="msg_concept_cancel" type="hidden" value="<spring:message code="msg.response.successfully.cancel"/>"/>
                                    <input id="msg_concept_added" type="hidden" value="<spring:message code="msg.response.successfully.added"/>"/>
                                    <input id="msg_concept_updated" type="hidden" value="<spring:message code="msg.response.successfully.updated"/>"/>
                                    <input id="msg_receipt_cancel" type="hidden" value="<spring:message code="msg.receipt.cancel"/>"/>
                                    <input id="val_yes" type="hidden" value="<spring:message code="lbl.yes"/>"/>
                                    <input id="val_no" type="hidden" value="<spring:message code="lbl.no"/>"/>
                                    <form id="dataexamen-form" class="smart-form" autocomplete="off">
                                        <fieldset>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.test.name"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="nombreExamen" name="nombreExamen" value="${examen.nombre}" placeholder=" <spring:message code="lbl.test.name" />">
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.notification.type"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled name="nombreNoti" id="nombreNoti" value="${tipoNotificacion.valor}" placeholder=" <spring:message code="lbl.notification.type" />" />
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.dx.type"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled name="nobreDx" id="nobreDx" value="${diagnostico.nombre}" placeholder=" <spring:message code="lbl.dx.type" />" />
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.receipt.pcr.area"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled name="nombreArea" id="nombreArea" value="${examen.area.nombre}" placeholder=" <spring:message code="lbl.receipt.pcr.area" />"/>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                        </fieldset>
                                        <footer>
                                            <button type="button" id="btnAddConcept" class="btn btn-primary styleButton" data-toggle="modal"
                                                    data-target="myModal">
                                                <i class="fa fa-plus icon-white"></i>
                                                <spring:message code="act.add.response"/>
                                            </button>
                                            <input id="idExamen" type="hidden" value="${examen.idExamen}"/>
                                            <input id="idDx" type="hidden" value="${diagnostico.idDiagnostico}"/>
                                            <input id="codTipoNoti" type="hidden" value="${tipoNotificacion.codigo}"/>
                                        </footer>
                                    </form>
                                </div>
                                <!-- end widget content -->
                            </div>
                            <!-- end widget div -->
                        </div>
                        <!-- end widget -->
                    </article>
                    <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <!-- Widget ID (each widget will need unique ID)-->
                        <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
                            <header>
                                <span class="widget-icon"> <i class="fa fa-font"></i> </span>
                                <h2><spring:message code="lbl.response.header" /> </h2>
                            </header>
                            <!-- widget div-->
                            <div>
                                <!-- widget edit box -->
                                <div class="jarviswidget-editbox">
                                    <!-- This area used as dropdown edit box -->
                                    <input class="form-control" type="text">
                                </div>
                                <div class="widget-body no-padding">
                                    <table id="concepts_list" class="table table-striped table-bordered table-hover" width="100%">
                                        <thead>
                                        <tr>
                                            <th data-class="expand"><spring:message code="lbl.response.name"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.response.concept"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.response.order"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.response.required"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.response.pasive"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.response.minvalue"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.response.maxvalue"/></th>
                                            <th><spring:message code="act.override"/></th>
                                            <th><spring:message code="act.edit"/></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        </tbody>
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
                        <spring:message code="lbl.response.header.modal.add" />
                    </h4>
                </div>
            </div>
            <div class="modal-body"> <!--  no-padding -->
            <form id="concepto-form" class="smart-form" novalidate="novalidate">
            <fieldset>
                <div class="row">
                    <input id="idRespuestaEdit" type="hidden" value=""/>
                    <input id="codigoDatoNumerico" type="hidden" value="${codigoDatoNumerico}"/>
                    <section class="col col-sm-12 col-md-6 col-lg-6">
                        <label class="text-left txt-color-blue font-md">
                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.response.name"/>
                        </label>
                        <div class="">
                            <label class="input">
                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                <input class="form-control" type="text" name="nombreConcepto" id="nombreConcepto" placeholder=" <spring:message code="lbl.response.name" />"/>
                                <b class="tooltip tooltip-bottom-right"> <i
                                        class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.response.name"/>
                                </b>
                            </label>
                        </div>
                    </section>
                    <section class="col col-sm-12 col-md-6 col-lg-6">
                        <label class="text-left txt-color-blue font-md">
                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.response.concept" />
                        </label>
                        <div class="input-group">
                        <span class="input-group-addon">
                            <i class="fa fa-location-arrow fa-fw"></i>
                        </span>
                            <select  class="select2" id="codConcepto" name="codConcepto" >
                                <option value=""><spring:message code="lbl.select" />...</option>
                                <c:forEach items="${conceptsList}" var="respuesta">
                                    <option value="${respuesta.idConcepto}">${respuesta.nombre}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </section>
                </div>
                <div class="row">
                    <section class="col col-sm-4 col-md-3 col-lg-3">
                        <label class="text-left txt-color-blue font-md">
                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.response.order"/>
                        </label>
                        <div class="">
                            <label class="input">
                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>
                                <input class="form-control entero" type="text" name="ordenConcepto" id="ordenConcepto" placeholder=" <spring:message code="lbl.response.order" />"/>
                                <b class="tooltip tooltip-bottom-right"> <i
                                        class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.response.order"/>
                                </b>
                            </label>
                        </div>
                    </section>
                    <section class="col col-sm-4 col-md-3 col-lg-3">
                        <label class="text-left txt-color-blue font-md"><spring:message code="lbl.response.required"/></label>
                                <label class="checkbox">
                                    <input type="checkbox" name="checkbox-required" id="checkbox-required">
                                    <i></i>
                                </label>

                    </section>
                    <section class="col col-sm-4 col-md-3 col-lg-3">
                        <label class="text-left txt-color-blue font-md"><spring:message code="lbl.response.pasive"/></label>
                        <div class="row">
                            <div class="col col-4">
                                <label class="checkbox">
                                    <input type="checkbox" name="checkbox-pasive" id="checkbox-pasive">
                                    <i></i></label>
                            </div>
                        </div>
                    </section>
                </div>
                <div class="row" id="divNumerico">
                    <section class="col col-sm-6 col-md-3 col-lg-3">
                        <label class="text-left txt-color-blue font-md">
                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.response.minvalue"/>
                        </label>
                        <div class="">
                            <label class="input">
                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>
                                <input class="form-control entero" type="text" name="minimoConcepto" id="minimoConcepto" placeholder=" <spring:message code="lbl.response.minvalue" />"/>
                                <b class="tooltip tooltip-bottom-right"> <i
                                        class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.response.minvalue"/>
                                </b>
                            </label>
                        </div>
                    </section>
                    <section class="col col-sm-6 col-md-3 col-lg-3">
                        <label class="text-left txt-color-blue font-md">
                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i><spring:message code="lbl.response.maxvalue"/>
                        </label>
                        <div class="">
                            <label class="input">
                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>
                                <input class="form-control entero" type="text" name="maximoConcepto" id="maximoConcepto" placeholder=" <spring:message code="lbl.response.maxvalue" />"/>
                                <b class="tooltip tooltip-bottom-right"> <i
                                        class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.response.maxvalue"/>
                                </b>
                            </label>
                        </div>
                    </section>
                </div>
            </fieldset>
            <footer>
                <button type="submit" class="btn btn-primary" id="btnAgregarExamen">
                    <spring:message code="act.save" />
                </button>
                <button type="button" class="btn btn-default" data-dismiss="modal">
                    <spring:message code="act.end" />
                </button>

            </footer>

            </form>
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
	<spring:url value="/resources/scripts/administracion/responseReview.js" var="concepts" />
	<script src="${concepts}"></script>
    <spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
    <script src="${handleInputMask}"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
	<spring:url value="/personas/search" var="sPersonUrl"/>
    <c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
    <c:url var="sConceptosUrl" value="/administracion/respuestas/getRespuetasExamen"/>
    <c:url var="sConceptoUrl" value="/administracion/respuestas/getRespuestaById"/>
    <c:url var="sActionUrl" value="/administracion/respuestas/agregarActualizarRespuesta"/>
    <c:url var="sTipoDatoUrl" value="/administracion/respuestas/getTipoDato"/>
    <script type="text/javascript">
		$(document).ready(function() {
			pageSetUp();
			var parametros = {sPersonUrl: "${sPersonUrl}",
                blockMess: "${blockMess}",
                sConceptosUrl : "${sConceptosUrl}",
                sConceptoUrl : "${sConceptoUrl}",
                sActionUrl : "${sActionUrl}",
                sFormConcept : "SI",
                sTipoDatoUrl : "${sTipoDatoUrl}"
            };
			Conceptos.init(parametros);
            $("#divNumerico").hide();
	    	$("li.administracion").addClass("open");
	    	$("li.conceptos").addClass("active");
	    	if("top"!=localStorage.getItem("sm-setmenu")){
	    		$("li.conceptos").parents("ul").slideDown(200);
	    	}
            handleInputMasks();

        });
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>