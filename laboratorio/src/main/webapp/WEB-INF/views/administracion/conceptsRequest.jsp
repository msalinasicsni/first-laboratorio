<%--
  Created by IntelliJ IDEA.
  User: souyen-ics
  Date: 03-02-15
  Time: 03:40 PM
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
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/administracion/respuestasSolicitud/init" htmlEscape="true "/>"><spring:message code="menu.admin.request.concepts" /></a></li>
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
            <spring:message code="menu.admin.request.concepts" />
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="lbl.response.create.subtitle" />
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
                    <span class="widget-icon"> <i class="fa fa-th"></i> </span>
                    <h2><spring:message code="lbl.response.widgettitle.request" /> </h2>
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

                        <input id="val_yes" type="hidden" value="<spring:message code="lbl.yes"/>"/>
                        <input id="val_no" type="hidden" value="<spring:message code="lbl.no"/>"/>
                        <form id="dataDx-form" class="smart-form" autocomplete="off">
                            <fieldset>
                                <div class="row">
                                    <section class="col col-sm-6 col-md-6 col-lg-8">
                                        <label class="text-left txt-color-blue font-md">
                                            <spring:message code="lbl.desc.request"/>
                                        </label>
                                        <div>
                                            <label class="input">
                                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                <c:choose>
                                                    <c:when test="${not empty estudio}">
                                                        <input class="form-control" type="text" disabled id="nombreEstudio" name="nombreEstudio" value="${estudio.nombre}" placeholder=" <spring:message code="lbl.desc.request" />">
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input class="form-control" type="text" disabled id="nombreDx" name="nombreDx" value="${dx.nombre}" placeholder=" <spring:message code="lbl.desc.request" />">
                                                    </c:otherwise>
                                                </c:choose>

                                            </label>
                                        </div>
                                    </section>

                                    <section class="col col-sm-6 col-md-6 col-lg-4">
                                        <label class="text-left txt-color-blue font-md">
                                            <spring:message code="lbl.receipt.pcr.area"/>
                                        </label>
                                        <div>
                                            <label class="input">
                                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                <c:choose>
                                                    <c:when test="${not empty estudio}">
                                                        <input class="form-control" type="text" disabled name="nombreAreaE" id="nombreAreaE" value="${estudio.area.nombre}" placeholder=" <spring:message code="lbl.receipt.pcr.area" />"/>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <input class="form-control" type="text" disabled name="nombreArea" id="nombreArea" value="${dx.area.nombre}" placeholder=" <spring:message code="lbl.receipt.pcr.area" />"/>
                                                    </c:otherwise>
                                                </c:choose>

                                            </label>
                                        </div>
                                    </section>
                                </div>
                            </fieldset>
                            <footer>
                                <button type="button" id="btnAddConcept" class="btn btn-primary styleButton" data-toggle="modal"
                                        data-target="myModal">
                                    <i class="fa fa-plus icon-white"></i>
                                    <spring:message code="act.add.data"/>
                                </button>
                                <c:choose>
                                    <c:when test="${not empty estudio}">
                                        <input id="idSolicitud" type="hidden" value="${estudio.idEstudio}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <input id="idSolicitud" type="hidden" value="${dx.idDiagnostico}"/>
                                    </c:otherwise>
                                </c:choose>

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
<spring:url value="/resources/scripts/administracion/requestData.js" var="dataJs" />
<script src="${dataJs}"></script>
<spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
<script src="${handleInputMask}"></script>
<!-- END PAGE LEVEL SCRIPTS -->
<spring:url value="/personas/search" var="sPersonUrl"/>
<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="sDatosUrl" value="/administracion/datosSolicitud/getDatosIngresoSolicitud"/>
<c:url var="sDatoUrl" value="/administracion/datosSolicitud/getDatoSolicitudById"/>
<c:url var="actionUrl" value="/administracion/datosSolicitud/agregarActualizarDato"/>
<c:url var="sTipoDatoUrl" value="/administracion/datosSolicitud/getTipoDato"/>
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {blockMess: "${blockMess}",
            sDatosUrl : "${sDatosUrl}",
            sDatoUrl : "${sDatoUrl}",
            actionUrl : "${actionUrl}",
            sFormConcept : "SI",
            sTipoDatoUrl : "${sTipoDatoUrl}"
        };
        RequestData.init(parametros);
        $("#divNumerico").hide();
        $("li.administracion").addClass("open");
        $("li.respuestaSolicitud").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.respuestaSolicitud").parents("ul").slideDown(200);
        }
        handleInputMasks();

    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>