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
				<li><a href="<spring:url value="/" htmlEscape="true "/>"><spring:message code="menu.home" /></a> <i class="fa fa-angle-right"></i> <a href="<spring:url value="/recepcionMx/searchOrdersLab" htmlEscape="true "/>"><spring:message code="menu.receipt.orders.lab" /></a></li>
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
							<spring:message code="lbl.result.title" />
						<span> <i class="fa fa-angle-right"></i>  
							<spring:message code="lbl.result.income.subtitle" />
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
                                <h2><spring:message code="lbl.receipt.widgettitle" /> </h2>
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
                                    <input id="msg_receipt_added" type="hidden" value="<spring:message code="msg.receipt.successfully.added"/>"/>
                                    <input id="msg_receipt_cancel" type="hidden" value="<spring:message code="msg.receipt.cancel"/>"/>
                                    <input id="txtEsLaboratorio" type="hidden" value="true"/>
                                    <form id="receiptOrdersLab-form" class="smart-form" autocomplete="off">
                                        <fieldset>
                                            <div class="row">
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.name1"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="primerNombre" name="primerNombre" value="${alicuota.codUnicoMx.idNotificacion.persona.primerNombre}" placeholder=" <spring:message code="person.name1" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.nombre1"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.name2"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled name="segundoNombre" id="segundoNombre" value="${alicuota.codUnicoMx.idNotificacion.persona.segundoNombre}" placeholder=" <spring:message code="person.name2" />" />
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.nombre2"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.lastname1"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled name="primerApellido" id="primerApellido" value="${alicuota.codUnicoMx.idNotificacion.persona.primerApellido}" placeholder=" <spring:message code="person.lastname1" />" />
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.apellido1"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-6 col-md-3 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="person.lastname2"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled name="segundoApellido" id="segundoApellido" value="${alicuota.codUnicoMx.idNotificacion.persona.segundoApellido}" placeholder=" <spring:message code="person.lastname2" />"/>
                                                            <b class="tooltip tooltip-bottom-right"> <i
                                                                    class="fa fa-warning txt-color-pink"></i> <spring:message code="tooltip.apellido2"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-12 col-md-6 col-lg-2">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.receipt.symptoms.start.date.full"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="fechaIniSintomas" name="fechaIniSintomas" value="<fmt:formatDate value="${fechaInicioSintomas}" pattern="dd/MM/yyyy" />"
                                                                   placeholder=" <spring:message code="lbl.receipt.symptoms.start.date.full" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.receipt.symptoms.start.date.full"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.sample.type" /> </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="codTipoMx" name="codTipoMx" value="${alicuota.codUnicoMx.codTipoMx.nombre}" placeholder=" <spring:message code="lbl.sample.type" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.sample.type"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-6 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.sampling.datetime"/>
                                                    </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="fechaHoraTomaMx" name="fechaHoraTomaMx" value="<fmt:formatDate value="${alicuota.codUnicoMx.fechaHTomaMx}" pattern="dd/MM/yyyy hh:mm:ss a" />"
                                                                   placeholder=" <spring:message code="lbl.sampling.datetime" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.sampling.datetime"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                            <div class="row">
                                                <section class="col col-sm-12 col-md-4 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.silais" /> </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="codSilais" name="codSilais" value="${alicuota.codUnicoMx.idNotificacion.codSilaisAtencion.nombre}" placeholder=" <spring:message code="lbl.silais" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.silais"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-8 col-lg-6">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.health.unit" /> </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="codUnidadSalud" name="codUnidadSalud" value="${alicuota.codUnicoMx.idNotificacion.codUnidadAtencion.nombre}" placeholder=" <spring:message code="lbl.health.unit" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.health.unit"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                                <section class="col col-sm-12 col-md-4 col-lg-3">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <spring:message code="lbl.receipt.pcr.area" /> </label>
                                                    <div class="">
                                                        <label class="input">
                                                            <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                            <input class="form-control" type="text" disabled id="codAreaPrc" name="codAreaPrc" value="${alicuota.idOrden.codExamen.area.nombre}" placeholder=" <spring:message code="lbl.receipt.pcr.area" />">
                                                            <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> <spring:message code="lbl.receipt.pcr.area"/>
                                                            </b>
                                                        </label>
                                                    </div>
                                                </section>
                                            </div>
                                        </fieldset>
                                        <fieldset>
                                            <header>
                                                <spring:message code="lbl.header.result.orders.form" />
                                            </header>
                                            <br>
                                            <c:forEach items="${conceptosList}" var="concepto">
                                                <c:choose>
                                                    <c:when test="${concepto.tipoDato.tipo.codigo=='TPDATO|LIST'}">
                                                        <div class="row">
                                                            <section class="col col-sm-12 col-md-6 col-lg-6">
                                                                <label class="text-left txt-color-blue font-md">
                                                                    <c:if test="${concepto.requerido}">
                                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>
                                                                    </c:if>
                                                                    ${concepto.nombre} </label>
                                                                <div class="input-group">
                                                                    <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                                                    <c:choose>
                                                                        <c:when test="${concepto.requerido}">
                                                                            <select id="${concepto.idConcepto}" name="${concepto.idConcepto}" class="select2 requiredConcept">
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                    <select id="${concepto.idConcepto}" name="${concepto.idConcepto}" class="select2">
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                        <option value=""><spring:message code="lbl.select" />...</option>
                                                                        <c:forEach items="${valoresListas}" var="valor">
                                                                            <c:choose>
                                                                                <c:when test="${fn:contains(concepto.tipoDato.idTipoDato, valor.idTipoDato.idTipoDato)}">
                                                                                    <option value="${valor.idCatalogoLista}">${valor.valor}</option>
                                                                                </c:when>
                                                                                <c:otherwise>

                                                                                </c:otherwise>
                                                                            </c:choose>
                                                                        </c:forEach>
                                                                    </select>
                                                                </div>
                                                            </section>
                                                        </div>
                                                    </c:when>
                                                    <c:when test="${concepto.tipoDato.tipo.codigo=='TPDATO|NMRO'}">
                                                        <div class="row">
                                                            <section class="col col-sm-6 col-md-6 col-lg-6">
                                                                <c:choose>
                                                                    <c:when test="${concepto.requerido}">
                                                                        <label class="text-left txt-color-blue font-md">
                                                                            <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>
                                                                                ${concepto.nombre}
                                                                        </label>
                                                                        <div class="">
                                                                            <label class="input">
                                                                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>
                                                                                <input class="form-control decimal requiredConcept" type="text" name="${concepto.idConcepto}" id="${concepto.idConcepto}" placeholder="${concepto.nombre}"/>
                                                                                <b class="tooltip tooltip-bottom-right"> <i
                                                                                        class="fa fa-warning txt-color-pink"></i> ${concepto.nombre}
                                                                                </b>
                                                                            </label>
                                                                        </div>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <label class="text-left txt-color-blue font-md">
                                                                                ${concepto.nombre}
                                                                        </label>
                                                                        <div class="">
                                                                            <label class="input">
                                                                                <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-numeric-asc fa-fw"></i>
                                                                                <input class="form-control decimal" type="text" name="${concepto.idConcepto}" id="${concepto.idConcepto}" placeholder="${concepto.nombre}"/>
                                                                                <b class="tooltip tooltip-bottom-right"> <i
                                                                                        class="fa fa-warning txt-color-pink"></i> ${concepto.nombre}
                                                                                </b>
                                                                            </label>
                                                                        </div>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </section>
                                                        </div>
                                                    </c:when>
                                                    <c:when test="${concepto.tipoDato.tipo.codigo=='TPDATO|LOG'}">
                                                        <div class="row">
                                                            <section class="col col-sm-4 col-md-3 col-lg-3">
                                                                <label class="text-left txt-color-blue font-md">
                                                                    <c:if test="${concepto.requerido}">
                                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>
                                                                    </c:if>
                                                                    ${concepto.nombre}
                                                                </label>
                                                                <label class="checkbox">
                                                                    <c:choose>
                                                                        <c:when test="${concepto.requerido}">
                                                                            <input type="checkbox" class="requiredConcept" name="${concepto.idConcepto}" id="${concepto.idConcepto}">
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <input type="checkbox" name="${concepto.idConcepto}" id="${concepto.idConcepto}">
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                    <i></i>
                                                                </label>
                                                            </section>
                                                        </div>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <div class="row">
                                                            <section class="col col-sm-12 col-md-6 col-lg-6">
                                                                <label class="text-left txt-color-blue font-md">
                                                                    <c:if test="${concepto.requerido}">
                                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i>
                                                                    </c:if>
                                                                    ${concepto.nombre}
                                                                </label>
                                                                <div class="">
                                                                    <label class="input">
                                                                        <i class="icon-prepend fa fa-pencil fa-fw"></i><i class="icon-append fa fa-sort-alpha-asc fa-fw"></i>
                                                                        <c:choose>
                                                                            <c:when test="${concepto.requerido}">
                                                                                <input class="form-control requiredConcept" type="text" id="${concepto.idConcepto}" name="${concepto.idConcepto}" value="" placeholder="${concepto.nombre}">
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <input class="form-control" type="text" id="${concepto.idConcepto}" name="${concepto.idConcepto}" value="" placeholder="${concepto.nombre}">
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                        <b class="tooltip tooltip-bottom-right"> <i class="fa fa-warning txt-color-pink"></i> ${concepto.nombre}
                                                                        </b>
                                                                    </label>
                                                                </div>
                                                            </section>
                                                        </div>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:forEach>
                                            <!--<div class="row">
                                                <section class="col col-sm-12 col-md-6 col-lg-6">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i> <spring:message code="lbl.result" /> </label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                                        <select id="codResultado" name="codResultado"
                                                                class="select2">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <option value="1">Positivo</option>
                                                            <option value="2">Negativo</option>
                                                            <option value="3">Descartado</option>
                                                        </select>
                                                    </div>
                                                </section>
                                                <div id="divSerotipo" class="col col-sm-12 col-md-6 col-lg-6">
                                                    <label class="text-left txt-color-blue font-md">
                                                        <i class="fa fa-fw fa-asterisk txt-color-red font-sm"></i> Serotipo</label>
                                                    <div class="input-group">
                                                        <span class="input-group-addon"><i class="fa fa-location-arrow fa-fw"></i></span>
                                                        <select id="codSerotipo" name="codSerotipo"
                                                                class="select2">
                                                            <option value=""><spring:message code="lbl.select" />...</option>
                                                            <option value="1">1</option>
                                                            <option value="2">2</option>
                                                            <option value="3">3</option>
                                                            <option value="4">4</option>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>-->
                                        </fieldset>
                                        <footer>
                                            <input id="val_yes" type="hidden" value="<spring:message code="lbl.yes"/>"/>
                                            <input id="val_no" type="hidden" value="<spring:message code="lbl.no"/>"/>
                                            <input id="idExamen" type="hidden" value="${alicuota.idOrden.codExamen.idExamen}"/>
                                            <button type="submit" id="receipt-orders-lab" class="btn btn-success btn-lg pull-right header-btn"><i class="fa fa-save"></i> <spring:message code="act.save" /></button>
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
                    <article class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
                        <!-- Widget ID (each widget will need unique ID)-->
                        <div class="jarviswidget jarviswidget-color-darken" id="wid-id-1">
                            <header>
                                <span class="widget-icon"> <i class="fa fa-font"></i> </span>
                                <h2><spring:message code="lbl.concepts.header" /> </h2>
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
                                            <th data-class="expand"><spring:message code="lbl.concepts.name"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.concepts.datatype"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.concepts.order"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.concepts.required"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.concepts.pasive"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.concepts.minvalue"/></th>
                                            <th data-hide="phone"><spring:message code="lbl.concepts.maxvalue"/></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </article>
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
    <!-- JQUERY INPUT MASK -->
    <spring:url value="/resources/js/plugin/jquery-inputmask/jquery.inputmask.bundle.min.js" var="jqueryInputMask" />
    <script src="${jqueryInputMask}"></script>
    <!-- END PAGE LEVEL PLUGINS -->
	<!-- BEGIN PAGE LEVEL SCRIPTS -->
	<spring:url value="/resources/scripts/resultados/income-Result.js" var="incomeResult" />
	<script src="${incomeResult}"></script>
    <spring:url value="/resources/scripts/utilidades/handleInputMask.js" var="handleInputMask" />
    <script src="${handleInputMask}"></script>
    <!-- END PAGE LEVEL SCRIPTS -->
	<spring:url value="/personas/search" var="sPersonUrl"/>
    <c:set var="blockMess"><spring:message code="blockUI.message" /></c:set>
    <c:url var="ordersUrl" value="/resultados/searchOrders"/>

    <c:url var="unidadesURL" value="/api/v1/unidadesPrimariasHospSilais"/>
    <c:url var="sConceptosUrl" value="/administracion/conceptos/getConceptosExamen"/>
    <c:url var="sSearchReceiptUrl" value="/resultados/ver"/>
    <script type="text/javascript">
		$(document).ready(function() {
			pageSetUp();
			var parametros = {sPersonUrl: "${sPersonUrl}",
                sOrdersUrl : "${ordersUrl}",
                sUnidadesUrl : "${unidadesURL}",
                blockMess: "${blockMess}",
                sConceptosUrl: "${sConceptosUrl}",
                sSearchReceiptUrl : "${sSearchReceiptUrl}"
            };
			IncomeResult.init(parametros);
            handleInputMasks();
            $("#divSerotipo").hide();
	    	$("li.samples").addClass("open");
	    	$("li.recepcion").addClass("active");
	    	if("top"!=localStorage.getItem("sm-setmenu")){
	    		$("li.recepcion").parents("ul").slideDown(200);
	    	}
            $('#codCalidadMx').change();
        });
	</script>
	<!-- END JAVASCRIPTS -->
</body>
<!-- END BODY -->
</html>