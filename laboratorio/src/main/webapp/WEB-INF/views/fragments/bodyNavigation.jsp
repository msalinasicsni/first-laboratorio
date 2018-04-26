<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!-- Left panel : Navigation area -->
<!-- Note: This width of the aside area can be adjusted through LESS variables -->
<%@ page import="ni.gob.minsa.laboratorio.service.SeguridadService" %>
<%
    //SeguridadService seguridadService = new SeguridadService();
    //boolean seguridadHabilitada = seguridadService.seguridadHabilitada();
%>
<aside id="left-panel">
    <!-- User info -->
    <div class="login-info">
	<span> <!-- User image size is adjusted inside CSS, it should stay as it -->
		<spring:url value="/resources/img/user.png" var="user" />
		<a href="javascript:void(0);" id="show-shortcut" data-action="toggleShortcut">
            <img src="${user}" alt="<spring:message code="lbl.user" />" class="online" />
			<span>
            <%//if (seguridadHabilitada) {%>
				<%//=seguridadService.obtenerNombreUsuario(request)%>
            <% //} else { %>
                <!--<spring:message code="lbl.user" />-->
                <sec:authentication property="principal.username" />
            <!--<%// } %>-->
			</span>
            <i class="fa fa-angle-down"></i>
        </a>

	</span>
    </div>
    <!-- end user info -->
    <!-- NAVIGATION : This navigation is also responsive
    To make this navigation dynamic please make sure to link the node
    (the reference to the nav > ul) after page load. Or the navigation
    will not initialize.
    -->
    <nav>
        <!-- NOTE: Notice the gaps after each icon usage <i></i>..
        Please note that these links work a bit different than
        traditional href="" links. See documentation for details.
        -->

        <ul>
            <%//if (seguridadHabilitada) {%>
            <%//=seguridadService.obtenerMenu(request)%>
            <% //} else { %>
            <li class="home">
                <a href="<spring:url value="/" htmlEscape="true "/>" title="<spring:message code="menu.home" />"><i class="fa fa-lg fa-fw fa-home"></i> <span class="menu-item-parent"><spring:message code="menu.home" /></span></a>
            </li>
            <li class="mantenimiento">
                <a href="#" title="<spring:message code="menu.maint" />"><i class="fa fa-lg fa-fw fa-cogs"></i> <span class="menu-item-parent"><spring:message code="menu.maint" /></span></a>
                <ul>
                    <li class="personas">
                        <a href="<spring:url value="/personas/search" htmlEscape="true "/>" title="<spring:message code="menu.persons" />"><i class="fa fa-lg fa-fw fa-group"></i> <spring:message code="menu.persons" /></a>
                    </li>
                    <li class="solicitantes">
                        <a href="<spring:url value="/solicitante/search" htmlEscape="true "/>" title="<spring:message code="menu.applicants" />"><i class="fa fa-lg fa-fw fa-group"></i> <spring:message code="menu.applicants" /></a>
                    </li>
                </ul>
            </li>
            <li class="recepcion">
                <a href="#" title="<spring:message code="menu.receipt.orders" />"><i class="fa fa-lg fa-fw fa-tint"></i> <span class="menu-item-parent"><spring:message code="menu.receipt.orders" /></span></a>
                <ul>
                   <li class="check-in">
                        <a href="#" title="<spring:message code="lbl.check-in" />"><i class="fa fa-lg fa-fw fa-list-ul"></i> <span class="menu-item-parent"><spring:message code="lbl.check-in" /></span></a>
                        <ul>
                            <li class="patient">
                                <a href="<spring:url value="/tomaMx/search" htmlEscape="true "/>" title="<spring:message code="menu.receipt.patient" />"><i class="fa fa-lg fa-fw fa-eyedropper"></i> <spring:message code="menu.receipt.patient" /></a>
                            </li>
                            <li class="receipt">
                                <a href="<spring:url value="/recepcionMx/init" htmlEscape="true "/>" title="<spring:message code="lbl.check-in.samples.from.alerta" />"><i class="fa fa-lg fa-fw fa-eyedropper"></i> <spring:message code="lbl.check-in.samples.from.alerta" /></a>
                            </li>
                            <li class="receiptCC">
                                <a href="<spring:url value="/recepcionMx/initCC" htmlEscape="true "/>" title="<spring:message code="lbl.check-in.samples.from.cc" />"><i class="fa fa-lg fa-fw fa-cc"></i> <spring:message code="lbl.check-in.samples.from.cc" /></a>
                            </li>
                            <li class="otherSamples">
                                <a href="<spring:url value="/tomaMx/searchOMx" htmlEscape="true "/>" title="<spring:message code="menu.receipt.other.samples" />"><i class="fa fa-lg fa-fw fa-eyedropper"></i> <spring:message code="menu.receipt.other.samples" /></a>
                            </li>
                        </ul>
                    </li>
                    <li class="sendReceipt">
                        <a href="<spring:url value="/sendMxReceipt/init" htmlEscape="true "/>" title="<spring:message code="menu.send.receipt.orders" />"><i class="fa fa-lg fa-fw fa-shopping-cart "></i> <spring:message code="menu.send.receipt.orders" /></a>
                    </li>
                    <li class="searchMx">
                        <a href="<spring:url value="/searchMx/init" htmlEscape="true "/>" title="<spring:message code="menu.search.mx" />"><i class="fa fa-lg fa-fw fa-search"></i> <spring:message code="menu.search.mx" /></a>
                    </li>
                    <li class="reprintLabCode">
                        <a href="<spring:url value="/reprint/init" htmlEscape="true "/>" title="<spring:message code="lbl.reprint.codelab" />"><i class="fa fa-lg fa-fw fa-print"></i> <spring:message code="lbl.reprint.codelab" /></a>
                    </li>
                </ul>
            </li>
            <li class="laboratorio">
                <a href="#" title="<spring:message code="menu.lab" />"><i class="fa fa-lg fa-fw fa-flask"></i> <span class="menu-item-parent"><spring:message code="menu.lab" /></span></a>
                <ul>
                    <li class="receiptLab">
                        <a href="<spring:url value="/recepcionMx/initLab" htmlEscape="true "/>" title="<spring:message code="menu.receipt.orders.lab" />"><i class="fa fa-lg fa-fw fa-thumbs-up"></i> <spring:message code="menu.receipt.orders.lab" /></a>
                    </li>
                    <li class="separacionMx">
                        <a href="<spring:url value="/separacionMx/init" htmlEscape="true "/>" title="<spring:message code="menu.generate.aliquot" />"><i class="fa fa-lg fa-fw fa-ticket"></i> <spring:message code="menu.generate.aliquot" /></a>
                    </li>
                    <li class="traslado">
                        <a href="#" title="<spring:message code="menu.transfer" />"><i class="fa fa-lg fa-fw fa-list-ul"></i> <span class="menu-item-parent"><spring:message code="menu.transfer" /></span></a>
                        <ul>
                            <li class="trasladoMx">
                                <a href="<spring:url value="/trasladoMx/init" htmlEscape="true "/>" title="<spring:message code="menu.transfer.internal.mx" />"><i class="fa fa-lg fa-fw fa-send"></i> <spring:message code="menu.transfer.internal.mx" /></a>
                            </li>
                            <li class="trasladoMxCC">
                                <a href="<spring:url value="/trasladoMx/initCC" htmlEscape="true "/>" title="<spring:message code="menu.transfer.qualitycontrol.mx" />"><i class="fa fa-lg fa-fw fa-send"></i> <spring:message code="menu.transfer.qualitycontrol.mx" /></a>
                            </li>
                            <li class="trasladoMxEx">
                                <a href="<spring:url value="/trasladoMx/initExternal" htmlEscape="true "/>" title="<spring:message code="menu.transfer.external.mx" />"><i class="fa fa-lg fa-fw fa-send"></i> <spring:message code="menu.transfer.external.mx" /></a>
                            </li>
                        </ul>
                    </li>
                    <li class="editarMx">
                        <a href="<spring:url value="/editarMx/init" htmlEscape="true "/>" title="<spring:message code="menu.edit.request.mx" />"><i class="fa fa-lg fa-fw fa-pencil"></i> <spring:message code="menu.edit.request.mx" /></a>
                    </li>
                    <li class="viewNoti">
                        <a href="<spring:url value="/viewNoti/init" htmlEscape="true "/>" title="<spring:message code="lbl.view.noti" />"><i class="fa fa-lg fa-fw fa-file-pdf-o"></i> <spring:message code="lbl.view.noti" /></a>
                    </li>

                </ul>
            </li>
            <li class="resultado">
                <a href="#" title="<spring:message code="menu.result" />"><i class="fa fa-lg fa-fw fa-th-list"></i> <span class="menu-item-parent"><spring:message code="menu.result" /></span></a>
                <ul>
                    <li class="ingresoResultado">
                        <a href="<spring:url value="/resultados/init" htmlEscape="true "/>" title="<spring:message code="menu.exam.result" />"><i class="fa fa-lg fa-fw fa-file-text"></i> <spring:message code="menu.exam.result" /></a>
                    </li>
                    <li class="enterFinalResult">
                        <a href="<spring:url value="/resultadoFinal/init" htmlEscape="true "/>" title="<spring:message code="menu.enter.final.result" />"><i class="fa fa-lg fa-fw fa-file-text-o"></i> <spring:message code="menu.enter.final.result" /></a>
                    </li>
                    <li class="approveResult">
                        <a href="<spring:url value="/aprobacion/init" htmlEscape="true "/>" title="<spring:message code="menu.approval.results" />"><i class="fa fa-lg fa-fw fa-check-circle"></i> <spring:message code="menu.approval.results" /></a>
                    </li>
                    <li class="rejectResult">
                        <a href="<spring:url value="/aprobacion/rejected" htmlEscape="true "/>" title="<spring:message code="menu.rejected.results" />"><i class="fa fa-lg fa-fw fa-times-circle"></i> <spring:message code="menu.rejected.results" /></a>
                    </li>
                    <li class="approvedResults">
                        <a href="<spring:url value="/aprobacion/approved" htmlEscape="true "/>" title="<spring:message code="menu.approved.results" />"><i class="fa fa-lg fa-fw fa-check-square-o"></i> <spring:message code="menu.approved.results" /></a>
                    </li>
                </ul>
            </li>
            <li class="gestion">
                <a href="#" title="<spring:message code="menu.management" />"><i class="fa fa-lg fa-fw fa-gear"></i> <span class="menu-item-parent"><spring:message code="menu.management" /></span></a>
                <ul>
                    <li class="gestion_sample">
                        <a href="<spring:url value="/gestion/initsample" htmlEscape="true "/>" title="<spring:message code="menu.management.samples" />"><i class="fa fa-lg fa-fw fa-tint"></i> <spring:message code="menu.management.samples" /></a>
                    </li>
                    <li class="gestion_noti">
                        <a href="<spring:url value="/gestion/initnoti" htmlEscape="true "/>" title="<spring:message code="menu.management.notifications" />"><i class="fa fa-lg fa-fw fa-book"></i> <spring:message code="menu.management.notifications" /></a>
                    </li>
                </ul>
            </li>
            <li class="administracion">
                <a href="#" title="<spring:message code="menu.administration" />"><i class="fa fa-lg fa-fw fa-cogs"></i> <span class="menu-item-parent"><spring:message code="menu.administration" /></span></a>
                <ul>
                    <li class="catalogos">
                        <a href="#" title="<spring:message code="menu.catalogs" />"><i class="fa fa-lg fa-fw fa-list-ul"></i> <span class="menu-item-parent"><spring:message code="menu.catalogs" /></span></a>
                        <ul>
                            <li class="admlaboratorio">
                                <a href="<spring:url value="/administracion/laboratorio/list" htmlEscape="true "/>" title="<spring:message code="menu.catalog.lab" />"><i class="fa fa-lg fa-fw fa-sitemap"></i> <spring:message code="menu.catalog.lab" /></a>
                            </li>
                            <li class="direccion">
                                <a href="<spring:url value="/administracion/direccion/list" htmlEscape="true "/>" title="<spring:message code="menu.catalog.management" />"><i class="fa fa-lg fa-fw fa-sitemap"></i> <spring:message code="menu.catalog.management" /></a>
                            </li>
                            <li class="departamento">
                                <a href="<spring:url value="/administracion/departamento/list" htmlEscape="true "/>" title="<spring:message code="menu.department" />"><i class="fa fa-lg fa-fw fa-sitemap"></i> <spring:message code="menu.department" /></a>
                            </li>
                            <li class="area">
                                <a href="<spring:url value="/administracion/area/list" htmlEscape="true "/>" title="<spring:message code="menu.area" />"><i class="fa fa-lg fa-fw fa-sitemap"></i> <spring:message code="menu.area" /></a>
                            </li>
                            <li class="request">
                                <a href="<spring:url value="/administracion/request/init" htmlEscape="true "/>" title="<spring:message code="lbl.dx.large" />"><i class="fa fa-lg fa-fw fa-file-text-o"></i> <spring:message code="lbl.dx.large" /></a>
                            </li>
                            <li class="examen">
                                <a href="<spring:url value="/administracion/examenes/list" htmlEscape="true "/>" title="<spring:message code="menu.test" />"><i class="fa fa-lg fa-fw fa-medkit"></i> <spring:message code="menu.test" /></a>
                            </li>
                            <li class="sampleTypes">
                                <a href="<spring:url value="/administracion/sampleTypes/init" htmlEscape="true "/>" title="<spring:message code="lbl.sample.types" />"><i class="fa fa-lg fa-fw fa-list-ul"></i> <spring:message code="lbl.sample.types" /></a>
                            </li>
                        </ul>
                    </li>
                    <li class="concepto">
                        <a href="<spring:url value="/administracion/conceptos/init" htmlEscape="true "/>" title="<spring:message code="menu.admin.concept" />"><i class="fa fa-lg fa-fw fa-list-ul"></i> <spring:message code="menu.admin.concept" /></a>
                    </li>
                    <li class="respuesta">
                        <a href="<spring:url value="/administracion/respuestas/init" htmlEscape="true "/>" title="<spring:message code="menu.admin.respuestas" />"><i class="fa fa-lg fa-fw fa-font"></i> <spring:message code="menu.admin.respuestas" /></a>
                    </li>
                    <li class="respuestaSolicitud">
                        <a href="<spring:url value="/administracion/respuestasSolicitud/init" htmlEscape="true "/>" title="<spring:message code="menu.admin.request.concepts" />"><i class="fa fa-lg fa-fw fa-list-alt "></i> <spring:message code="menu.admin.request.concepts" /></a>
                    </li>
                    <li class="assocSamplesRequest">
                        <a href="<spring:url value="/administracion/associationSR/init" htmlEscape="true "/>" title="<spring:message code="lbl.association.samples.req" />"><i class="fa fa-lg fa-fw fa-link"></i> <spring:message code="lbl.association.samples.req" /></a>
                    </li>
                    <li class="testsRequest">
                        <a href="<spring:url value="/administracion/testsRequest/init" htmlEscape="true "/>" title="<spring:message code="lbl.association.samples.req" />"><i class="fa fa-lg fa-fw fa-link"></i> <spring:message code="lbl.tests.Request" /></a>
                    </li>
                    <li class="users">
                        <a href="<spring:url value="/usuarios/list" htmlEscape="true "/>" title="<spring:message code="lbl.admin.users" />"><i class="fa fa-lg fa-fw fa-users"></i> <spring:message code="menu.admin.users" /></a>
                    </li>
                    <li class="organizationChart">
                        <a href="<spring:url value="/administracion/organizationChart/init" htmlEscape="true "/>" title="<spring:message code="lbl.organizationchart" />"><i class="fa fa-lg fa-fw fa-sitemap"></i> <spring:message code="lbl.organizationchart" /></a>
                    </li>
                    <li class="reportImages">
                        <a href="<spring:url value="/administracion/file/init" htmlEscape="true "/>" title="<spring:message code="menu.admin.images" />"><i class="fa fa-lg fa-fw fa-picture-o"></i> <spring:message code="menu.admin.images" /></a>
                    </li>
                </ul>
            </li>

            <li class="reportes">
                <a href="#" title="<spring:message code="menu.reports" />"><i class="fa fa-lg fa-fw fa-file-pdf-o"></i> <span class="menu-item-parent"><spring:message code="menu.reports" /></span></a>
                <ul>
                    <li class="printresults">
                        <a href="<spring:url value="/recepcionMx/printResults" htmlEscape="true "/>" title="<spring:message code="menu.print.result" />"><i class="fa fa-lg fa-fw fa-print"></i> <spring:message code="menu.print.result" /></a>
                    </li>
                    <li class="receptionReport">
                        <a href="<spring:url value="/reports/reception/init" htmlEscape="true "/>" title="<spring:message code="menu.reception.report" />"><i class="fa fa-lg fa-fw fa-list"></i> <spring:message code="menu.reception.report" /></a>
                    </li>
                    <li class="consolReceptionReport">
                        <a href="<spring:url value="/reports/consolidated/init" htmlEscape="true "/>" title="<spring:message code="menu.consolidated.reception.report" />"><i class="fa fa-lg fa-fw fa-list"></i> <spring:message code="menu.consolidated.reception.report" /></a>
                    </li>
                    <li class="generalReport">
                        <a href="<spring:url value="/reports/general/init" htmlEscape="true "/>" title="<spring:message code="lbl.general.report.results" />"><i class="fa fa-lg fa-fw fa-list"></i> <spring:message code="lbl.general.report.results" /></a>
                    </li>
                    <li class="positiveResultsReport">
                        <a href="<spring:url value="/reports/positiveResults/init" htmlEscape="true "/>" title="<spring:message code="menu.positiveResultsReport" />"><i class="fa fa-lg fa-fw fa-list"></i> <spring:message code="menu.positiveResultsReport" /></a>
                    </li>
                    <li class="posNegResultsReport">
                        <a href="<spring:url value="/reports/posNegResults/init" htmlEscape="true "/>" title="<spring:message code="lbl.posNegReport" />"><i class="fa fa-lg fa-fw fa-list"></i> <spring:message code="lbl.posNegReport" /></a>
                    </li>
                    <li class="workSheet">
                        <a href="<spring:url value="/workSheet/init" htmlEscape="true "/>" title="<spring:message code="menu.search.workSheet" />"><i class="fa fa-lg fa-fw fa-list-alt"></i> <spring:message code="menu.search.workSheet" /></a>
                    </li>
                    <li class="qualityControlReport">
                        <a href="<spring:url value="/reports/qualityControl/init" htmlEscape="true "/>" title="<spring:message code="menu.qualityControlReport" />"><i class="fa fa-lg fa-fw fa-list"></i> <spring:message code="menu.qualityControlReport" /></a>
                    </li>
                    <li class="resultDx">
                        <a href="<spring:url value="/reports/reportResultDx/init" htmlEscape="true "/>" title="<spring:message code="menu.report.result.dx" />"><i class="fa fa-lg fa-fw fa-list"></i> <spring:message code="menu.report.result.dx" /></a>
                    </li>
                    <li class="resultDxVig">
                        <a href="<spring:url value="/reports/reportResultDxVig/init" htmlEscape="true "/>" title="<spring:message code="menu.report.result.dx.vig" />"><i class="fa fa-lg fa-fw fa-list-alt"></i> <spring:message code="menu.report.result.dx.vig" /></a>
                    </li>
                    <li class="consolidatedexams">
                        <a href="<spring:url value="/reports/consolidatedexams/init" htmlEscape="true "/>" title="<spring:message code="menu.report.consol.exams" />"><i class="fa fa-lg fa-fw fa-list-alt"></i> <spring:message code="menu.report.consol.exams" /></a>
                    </li>
                </ul>
            </li>


            <li>
                <a href="<spring:url value="/logout" htmlEscape="true "/>"> <i class="fa fa-lg fa-fw fa-sign-out"></i> <span class="menu-item-parent"><spring:message code="menu.logout" /></span></a>
            </li>
            <%// } %>
        </ul>

    </nav>

<span class="minifyme" data-action="minifyMenu">
	<i class="fa fa-arrow-circle-left hit"></i>
</span>
</aside>
<!-- END NAVIGATION -->