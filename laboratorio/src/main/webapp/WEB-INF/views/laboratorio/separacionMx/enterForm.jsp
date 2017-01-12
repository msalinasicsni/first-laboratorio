<%--
  Created by IntelliJ IDEA.
  User: souyen-ics
  Date: 01-05-15
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<!-- BEGIN HEAD -->
<head>
    <jsp:include page="../../fragments/headTag.jsp" />
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
    </style>
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="">
<!-- #HEADER -->
<jsp:include page="../../fragments/bodyHeader.jsp" />
<!-- #NAVIGATION -->
<jsp:include page="../../fragments/bodyNavigation.jsp" />
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
        <li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/separacionMx/init" htmlEscape="true "/>"><spring:message code="menu.generate.aliquot" /></a></li>
    </ol>
    <!-- end breadcrumb -->
    <jsp:include page="../../fragments/layoutOptions.jsp" />
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
            <i class="fa-fw fa fa-ticket"></i>
            <spring:message code="menu.generate.aliquot" />
						<span> <i class="fa fa-angle-right"></i>
							<spring:message code="lbl.receipt.orders" />
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
    <span class="widget-icon"> <i class="fa fa-ticket"></i> </span>
    <h2><spring:message code="menu.generate.aliquot" /> </h2>
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
        <input id="msjSuccessful" type="hidden" value="<spring:message code="msg.aliquot.added"/>"/>
        <input id="disappear" type="hidden" value="<spring:message code="msg.disappear"/>"/>
        <input id="msjErrorSaving" type="hidden" value="<spring:message code="msg.aliquot.error"/>"/>
        <input id="confirm_msg_opc_yes" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.yes"/>"/>
        <input id="confirm_msg_opc_no" type="hidden" value="<spring:message code="lbl.confirm.msg.opc.no"/>"/>
        <input id="msg_print_confirm" type="hidden" value="<spring:message code="msg.confirm.title"/>"/>
        <input id="msg_print_confirm_content" type="hidden" value="<spring:message code="msg.print.confirm.content"/>"/>
        <input id="msg_print_canceled" type="hidden" value="<spring:message code="msg.print.canceled"/>"/>
        <input id="msg_print_aliquot_select" type="hidden" value="<spring:message code="msg.print.aliquot.select"/>"/>
        <input id="text_selected_all" type="hidden" value="<spring:message code="lbl.selected.all"/>"/>
        <input id="text_selected_none" type="hidden" value="<spring:message code="lbl.selected.none"/>"/>



            <table id="test-orders-list" class="table table-striped table-bordered table-hover" width="100%">
                <thead>
                <tr data-class="expand">

                    <th data-hide="phone" ><i
                            class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message
                            code="lbl.receipt.test"/>
                    </th>

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
    <article  class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
        <!-- Widget ID (each widget will need unique ID)-->
        <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
            <header>
                <span class="widget-icon"> <i class="fa fa-reorder"></i> </span>
                <h2><spring:message code="lbl.records" /> </h2>
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
                    <table id="aliquots-list" class="table table-striped table-bordered table-hover" width="100%">
                        <thead>
                        <tr data-class="expand">
                            <th>


                            </th>
                            <th data-hide="phone"><i
                                    class="fa fa-fw fa-eyedropper text-muted hidden-md hidden-sm hidden-xs"></i> <spring:message
                                    code="lbl.id.aliquot"/>

                            </th>
                            <th data-hide="phone"><i class="fa fa-fw fa-list-alt text-muted hidden-md hidden-sm hidden-xs"></i>
                                <spring:message code="lbl.ticket.for"/></th>
                            <th data-hide="phone"><i
                                    class="fa fa-fw fa-sort-numeric-asc text-muted hidden-md hidden-sm hidden-xs"></i>
                                <spring:message code="lbl.volume"/></th>

                            <th></th>
                        </tr>
                        </thead>
                    </table>

                </div>
                <form class="smart-form" novalidate="novalidate">
                    <footer> <!---->
                        <button id="btnPrint" type="button" class="btn btn-success btn-md header-btn pull-right"><i class="fa fa-print"></i> <spring:message code="act.print" /></button>
                        <button type="button" id="btnAddAliquot" class="btn btn-success btn-md header-btn pull-right" data-toggle="modal" data-target="#myModal"> <i class="fa fa-plus icon-white"></i> <spring:message code="lbl.aliquot" /></button>
                    </footer>
                </form>
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
        <!-- Modal Aliquot -->
        <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header" >
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
                            &times;
                        </button>
                        <h4 class="modal-title" id="myModalLabel"><spring:message code="lbl.add.aliquot"/></h4>
                    </div>

                    <div class="modal-body">
                        <form:form id="generateAliquot-form" class="smart-form" autocomplete="off">
                            <input id="idSoliE" hidden="hidden" value="${soliE.idSolicitudEstudio}" type="text" name="idSoliE"/>
                            <input id="idSoliDx" hidden="hidden" value="${soliDx.idSolicitudDx}" type="text" name="idSoliDx"/>

                            <div class="row">
                                <section class="col col-sm-12 col-md-6 col-lg-6">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.aliquot" /> </label>
                                    <div class="input-group">
                                        <span class="input-group-addon"><i class="fa fa-list fa-fw"></i></span>
                                        <select id="etiqueta" name="etiqueta"
                                                class="select2">
                                            <option value=""><spring:message code="lbl.select" />...</option>
                                            <c:forEach items="${alicuotaCat}" var="alic">
                                                <option value="${alic.idAlicuota}">${alic.alicuota}</option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                </section>


                                <section class="col col-sm-12 col-md-6 col-lg-6">
                                    <label class="text-left txt-color-blue font-md">
                                        <spring:message code="lbl.volume"/>
                                    </label>
                                    <div class="">
                                        <label class="input">
                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>
                                            <input class="form-control" type="text" name="volumen" id="volumen" placeholder=" <spring:message code="lbl.volume" />" />
                                            <b class="tooltip tooltip-bottom-right"> <i
                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.enter.volume"/>
                                            </b>
                                        </label>
                                    </div>

                                    <div class="note">
                                     <input style="border: none" id="inVolume" />
                                    </div>
                                </section>
                            </div>

                        </form:form>
                    </div>

                    <div class="modal-footer">
                        <input id="codigoUnicoMx" type="hidden" value="${recepcionMx.tomaMx.codigoUnicoMx}"/>
                        <button type="submit" id="btnAdd" class="btn btn-success styleButton"><i class="fa fa-save"></i> <spring:message code="act.save" /></button>

                    </div>
                </div>
                <!-- /.modal-content -->
            </div>
            <!-- /.modal-dialog -->
        </div>
        <!-- /.modal -->
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
<jsp:include page="../../fragments/footer.jsp" />
<!-- END FOOTER -->
<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
<jsp:include page="../../fragments/corePlugins.jsp" />
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
<!-- JQUERY VALIDATE -->
<spring:url value="/resources/js/plugin/jquery-validate/jquery.validate.min.js" var="jqueryValidate" />
<script src="${jqueryValidate}"></script>
<spring:url value="/resources/js/plugin/jquery-validate/messages_{language}.js" var="jQValidationLoc">
    <spring:param name="language" value="${pageContext.request.locale.language}" /></spring:url>
<script src="${jQValidationLoc}"></script>
<!-- JQUERY BLOCK UI -->
<spring:url value="/resources/js/plugin/jquery-blockui/jquery.blockUI.js" var="jqueryBlockUi" />
<script src="${jqueryBlockUi}"></script>
<!-- JQUERY BAR CODE -->
<spring:url value="/resources/js/plugin/jquery-barcode-2.0.3/jquery-barcode.js" var="jqueryBarCode" />
<script src="${jqueryBarCode}"></script>
<spring:url value="/resources/js/plugin/jquery-barcode-2.0.3/jquery-barcode.min.js" var="jqueryBarCodeM" />
<script src="${jqueryBarCodeM}"></script>
<!-- END PAGE LEVEL PLUGINS -->
<!-- BEGIN PAGE LEVEL SCRIPTS -->
<spring:url value="/resources/scripts/laboratorio/generacionAlicuota/enterForm.js" var="generateAliquot" />
<script src="${generateAliquot}"></script>
<!-- END PAGE LEVEL SCRIPTS -->

<c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
<c:url var="addAliquot" value="/separacionMx/addAliquot"/>
<c:url var="getAliquots" value="/separacionMx/getAliquots"/>
<c:url var="overrideAliquot" value="/separacionMx/overrideAliquot/"/>
<c:url var="getTestOrders" value="/separacionMx/getTestOrders"/>
<spring:url var="sPrintUrl" value="/print/barcode"/>
<c:url var="getVolume" value="/separacionMx/getVolume"/>

<c:url var="sAddReceiptUrl" value="/recepcionMx/agregarRecepcion"/>
<script type="text/javascript">
    $(document).ready(function() {
        pageSetUp();
        var parametros = {addAliquot: "${addAliquot}",
            blockMess: "${blockMess}",
            getAliquots: "${getAliquots}",
            sTableToolsPath : "${tabletools}",
            overrideAliquot: "${overrideAliquot}",
            sPrintUrl : "${sPrintUrl}",
            getTestOrders: "${getTestOrders}",
            volumeUrl: "${getVolume}"

        };
        GenerateAliquot.init(parametros);
        $("li.laboratorio").addClass("open");
        $("li.separacionMx").addClass("active");
        if("top"!=localStorage.getItem("sm-setmenu")){
            $("li.separacionMX").parents("ul").slideDown(200);
        }
    });
</script>
<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>
