<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<html>
<!-- BEGIN HEAD -->
<head>
	<jsp:include page="fragments/headTag.jsp" />
    <style type="text/css">
        .flotTip
        {
            padding: 3px 5px;
            background-color: #000;
            z-index: 100;
            color: #fff;
            box-shadow: 0 0 10px #555;
            opacity: .7;
            filter: alpha(opacity=70);
            border: 2px solid #fff;
            -webkit-border-radius: 4px;
            -moz-border-radius: 4px;
            border-radius: 4px;
        }
    </style>
</head>
<!-- END HEAD -->
<!-- BEGIN BODY -->
<body class="">
	<!-- #HEADER -->
	<jsp:include page="fragments/bodyHeader.jsp" />
	<!-- #NAVIGATION -->
	<jsp:include page="fragments/bodyNavigation.jsp" />
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
				<li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a></li>
			</ol>
			<!-- end breadcrumb -->
			<jsp:include page="fragments/layoutOptions.jsp" />
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
						<i class="fa-fw fa fa-home"></i> 
							<spring:message code="menu.home" />
					</h1>
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
					<article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
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
								<span class="widget-icon"> <i class="fa fa-tint"></i> </span>
								<h2><spring:message code="lbl.home.widgettitle" /> </h2>
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
                                    <p class="alert alert-info">
                                        <spring:url value="/recepcionMx/init" var="recepcionUrl"/>
                                        <button type="button" class="btn btn-primary"
                                                onclick="location.href='${fn:escapeXml(recepcionUrl)}'">
                                            <spring:message code="lbl.go.to.receipt" />
                                        </button>
                                    </p>
                                    <input id="smallBox_content" type="hidden" value="<spring:message code="smallBox.content.4s"/>"/>
                                    <input id="msg_no_results_found" type="hidden" value="<spring:message code="msg.no.results.found"/>"/>
                                    <input id="msg_no_data_found" type="hidden" value="<spring:message code="lbl.no.data.found"/>"/>
									<!-- this is what the user will see -->
                                    <table id="orders_result" class="table table-striped table-bordered table-hover" width="100%">
                                        <thead>
                                        <tr>
                                            <th><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.lab.code.mx"/></th>
                                            <th><spring:message code="lbl.cc"/></th>
                                            <th><spring:message code="lbl.transfer.origin.lab"/></th>
                                            <th data-class="expand"><i class="fa fa-fw fa-list text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.sample.type"/></th>
                                            <th data-hide="phone"><i class="fa fa-fw fa-calendar text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.sampling.datetime"/></th>
                                            <th data-hide="phone"><i class="fa fa-fw fa-calendar text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.receipt.symptoms.start.date"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.silais"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.health.unit"/></th>
                                            <th data-hide="phone"><i class="fa fa-child fa-fw text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.pregnant.short"/></th>
                                            <th data-hide="phone"><i class="fa fa-hospital-o fa-fw text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.hosp"/></th>
                                            <th data-hide="phone"><i class="fa fa-exclamation-triangle fa-fw text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.urgent"/></th>
                                            <th data-class="expand"><i class="fa fa-fw fa-user text-muted hidden-md hidden-sm hidden-xs"></i><spring:message code="lbl.receipt.person.applicant.name"/></th>

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
				<div class="row">
                    <!-- NEW WIDGET START -->
                    <article class="col-xs-12 col-sm-6 col-md-6 col-lg-6">

                        <!-- Widget ID (each widget will need unique ID)-->
                        <div class="jarviswidget jarviswidget-color-darken" id="wid-id-7" data-widget-editbutton="false" data-widget-deletebutton="false">
                            <header>
                                <span class="widget-icon"> <i class="fa fa-bar-chart-o"></i> </span>
                                <h2><spring:message code="lbl.chart.SILAIS.title"/></h2>

                            </header>
                            <!-- widget div-->
                            <div>
                                <!-- widget edit box -->
                                <div class="jarviswidget-editbox">
                                    <!-- This area used as dropdown edit box -->
                                </div>
                                <!-- end widget edit box -->
                                <!-- widget content -->
                                <div class="widget-body no-padding">
                                    <div id="pie-chart" class="chart"></div>
                                </div>
                                <!-- end widget content -->
                            </div>
                            <!-- end widget div -->
                        </div>
                        <!-- end widget -->
                    </article>
                    <!-- WIDGET END -->
                    <!-- NEW WIDGET START -->
                    <article class="col-xs-12 col-sm-6 col-md-6 col-lg-6">

                        <!-- Widget ID (each widget will need unique ID)-->
                        <div class="jarviswidget jarviswidget-color-darken" id="wid-id-6" data-widget-editbutton="false" data-widget-deletebutton="false">
                            <header>
                                <span class="widget-icon"> <i class="fa fa-bar-chart-o"></i> </span>
                                <h2><spring:message code="lbl.chart.request.title"/></h2>
                            </header>
                            <!-- widget div-->
                            <div>
                                <!-- widget edit box -->
                                <div class="jarviswidget-editbox">
                                    <!-- This area used as dropdown edit box -->
                                </div>
                                <!-- end widget edit box -->
                                <!-- widget content -->
                                <div class="widget-body no-padding">
                                    <div id="pie-chart2" class="chart"></div>
                                </div>
                                <!-- end widget content -->
                            </div>
                            <!-- end widget div -->
                        </div>
                        <!-- end widget -->
                    </article>
                    <!-- WIDGET END -->
				</div>
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
	<jsp:include page="fragments/footer.jsp" />
	<!-- END FOOTER -->
	<!-- BEGIN JAVASCRIPTS(Load javascripts at bottom, this will reduce page load time) -->
	<jsp:include page="fragments/corePlugins.jsp" />
	<!-- BEGIN PAGE LEVEL PLUGINS -->
	<!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
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
    <!-- Flot Chart Plugin: Flot Engine, Flot Resizer, Flot Tooltip -->
    <spring:url value="/resources/js/plugin/flot/jquery.flot.cust.min.js" var="jqueryFlot" />
    <script src="${jqueryFlot}"></script>
    <spring:url value="/resources/js/plugin/flot/jquery.flot.resize.min.js" var="jqueryFlotResize" />
    <script src="${jqueryFlotResize}"></script>
    <spring:url value="/resources/js/plugin/flot/jquery.flot.time.min.js" var="jqueryFlotTime" />
    <script src="${jqueryFlotTime}"></script>
    <!-- EASY PIE CHARTS -->
    <spring:url value="/resources/js/plugin/flot/jquery.flot.pie.min.js" var="pieChart" />
    <script src="${pieChart}"></script>
    <spring:url value="/resources/js/plugin/flot/tooltip/jquery.flot.tooltip.min.js" var="jqueryFlotToolTip" />
    <script src="${jqueryFlotToolTip}"></script>

	<!-- END PAGE LEVEL SCRIPTS -->
    <c:url var="ordersUrl" value="/recepcionMx/searchOrders"/>
    <c:url var="sCreateReceiptUrl" value="/recepcionMx/create/"/>
    <c:url var="resumenSILAISUrl" value="/getResumenMuestrasSILAIS"/>
    <c:url var="resumenSolicitudUrl" value="/getResumenMuestrasSolicitud"/>
	<script>
	    $(function () {
	    	$("li.home").addClass("active");
	    });
	</script>
	<script type="text/javascript">
		$(document).ready(function() {
			pageSetUp();
            var responsiveHelper_dt_basic = undefined;
            var breakpointDefinition = {
                tablet : 1024,
                phone : 480
            };
            var table1 = $('#orders_result').dataTable({
                "sDom": "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>"+
                        "t"+
                        "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
                "autoWidth" : true, //"T<'clear'>"+
                "preDrawCallback" : function() {
                    // Initialize the responsive datatables helper once.
                    if (!responsiveHelper_dt_basic) {
                        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#orders_result'), breakpointDefinition);
                    }
                },
                "rowCallback" : function(nRow) {
                    responsiveHelper_dt_basic.createExpandIcon(nRow);
                },
                "drawCallback" : function(oSettings) {
                    responsiveHelper_dt_basic.respond();
                }
            });

            function getOrders() {
                var encuestaFiltros = {};
                    encuestaFiltros['nombreApellido'] = '';
                    encuestaFiltros['fechaInicioTomaMx'] = '';
                    encuestaFiltros['fechaFinTomaMx'] = '';
                    encuestaFiltros['codSilais'] = '';
                    encuestaFiltros['codUnidadSalud'] = '';
                    encuestaFiltros['codTipoMx'] = '';
                    encuestaFiltros['esLab'] =  'false';
                $.getJSON("${ordersUrl}", {
                    strFilter: JSON.stringify(encuestaFiltros),
                    ajax : 'true'
                }, function(dataToLoad) {
                    table1.fnClearTable();
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        for (var i = 0; i < len; i++) {
                            var idLoad;
                            if ($('#txtEsLaboratorio').val()=='true'){
                                idLoad =dataToLoad[i].idRecepcion;
                            }else{
                                idLoad = dataToLoad[i].idTomaMx;
                            }
                            var actionUrl = "${sCreateReceiptUrl}"+idLoad;
                            table1.fnAddData(
                                    [dataToLoad[i].codigoUnicoMx,dataToLoad[i].traslado, dataToLoad[i].origen,dataToLoad[i].tipoMuestra,dataToLoad[i].fechaTomaMx, dataToLoad[i].fechaInicioSintomas, dataToLoad[i].codSilais, dataToLoad[i].codUnidadSalud, dataToLoad[i].embarazada, dataToLoad[i].hospitalizado, dataToLoad[i].urgente, dataToLoad[i].persona, '<a target="_blank" title="Ver" href='+ actionUrl + ' class="btn btn-primary btn-xs"><i class="fa fa-mail-forward"></i></a>']);

                        }

                    }
                }).fail(function(jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });

            }

            getOrders();

            /* pie chart */
            var colores = ["#0066FF","#FF0000","#009900","#FF6600","#FF3399","#008B8B","#663399","#FFD700","#0000FF","#DC143C","#32CD32","#FF8C00","#C71585","#20B2AA","#6A5ACD","#9ACD32"];
            function getResumenSILAIS() {
                $.getJSON("${resumenSILAISUrl}", {
                    ajax : 'true'
                }, function(dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        var data_pie = [];
                        for (var i = 0; i < len; i++) {
                            data_pie[i] = {
                                label : dataToLoad[i].SILAIS,
                                data : dataToLoad[i].total,
                                color: colores[i]
                            }
                        }
                        cargarGrafico("#pie-chart",data_pie);
                    }else{
                        var noDatos = '<h4 class="alert alert-danger">'+$("#msg_no_data_found").val()+'</h4>';
                        $("#pie-chart").html(noDatos);
                    }
                }).fail(function(jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });
            }

            function getResumenSolicitudes() {
                $.getJSON("${resumenSolicitudUrl}", {
                    ajax : 'true'
                }, function(dataToLoad) {
                    var len = Object.keys(dataToLoad).length;
                    if (len > 0) {
                        var data_pie = [];
                        for (var i = 0; i < len; i++) {
                            data_pie[i] = {
                                label : dataToLoad[i].solicitud,
                                data : dataToLoad[i].total,
                                color: colores[i]
                            }
                        }
                        cargarGrafico("#pie-chart2",data_pie);
                    }else{
                        var noDatos = '<h4 class="alert alert-danger">'+$("#msg_no_data_found").val()+'</h4>';
                        $("#pie-chart2").html(noDatos);
                    }

                }).fail(function(jqXHR) {
                    setTimeout($.unblockUI, 10);
                    validateLogin(jqXHR);
                });

            }

            function cargarGrafico(contenedor,data) {
                $.plot(contenedor, data, {
                    series: {
                        pie: {
                            show: true,
                            radius: 1,
                            label: {
                                show: true,
                                radius: 2 / 3,
                                formatter: function (label, series) {
                                    return '<div style="font-size:11px;text-align:center;padding:4px;color:white;"><br/>' + series.data[0][1] + '</div>';
                                },
                                threshold: 0.01
                            }
                        }
                    },
                    legend: {
                        show: true,
                        noColumns: 1, // number of colums in legend table
                        labelFormatter: null, // fn: string -> string
                        labelBoxBorderColor: "#000", // border color for the little label boxes
                        container: null, // container (as jQuery object) to put legend in, null means default on top of graph
                        position: "ne", // position of default legend container within plot
                        margin: [5, 10], // distance from grid edge to default legend container within plot
                        backgroundColor: "#efefef", // null means auto-detect
                        backgroundOpacity: 1 // set to 0 to avoid background
                    },
                    grid: {
                        hoverable: true
                    },
                    tooltip: {
                        show: true,
                        content: "%p.0%, %s, n=%n", // show percentages, rounding to 2 decimal places
                        shifts: {
                            x: 20,
                            y: 0
                        },
                        defaultTheme: false
                    }
                });
            }

            getResumenSILAIS();
            getResumenSolicitudes();
            /* end pie chart */
		});
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>