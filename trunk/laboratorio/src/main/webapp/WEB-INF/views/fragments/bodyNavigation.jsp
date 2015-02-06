<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- Left panel : Navigation area -->
<!-- Note: This width of the aside area can be adjusted through LESS variables -->
<%@ page import="ni.gob.minsa.laboratorio.service.SeguridadService" %>
<%
    SeguridadService seguridadService = new SeguridadService();
    boolean seguridadHabilitada = seguridadService.seguridadHabilitada();
%>
<aside id="left-panel">
    <!-- User info -->
    <div class="login-info">
	<span> <!-- User image size is adjusted inside CSS, it should stay as it -->
		<spring:url value="/resources/img/user.png" var="user" />
		<a href="javascript:void(0);" id="show-shortcut" data-action="toggleShortcut">
            <img src="${user}" alt="<spring:message code="lbl.user" />" class="online" />
			<span>
            <%if (seguridadHabilitada) {%>
				<%=seguridadService.obtenerNombreUsuario(request)%>
            <% } else { %>
                <spring:message code="lbl.user" />
            <% } %>
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
            <%if (seguridadHabilitada) {%>
            <%=seguridadService.obtenerMenu(request)%>
            <% } else { %>
            <li class="home">
                <a href="<spring:url value="/" htmlEscape="true "/>" title="<spring:message code="menu.home" />"><i class="fa fa-lg fa-fw fa-home"></i> <span class="menu-item-parent"><spring:message code="menu.home" /></span></a>
            </li>
            <li class="recepcion">
                <a href="#" title="<spring:message code="menu.receipt.orders" />"><i class="fa fa-lg fa-fw fa-tint"></i> <span class="menu-item-parent"><spring:message code="menu.receipt.orders" /></span></a>
                <ul>
                    <li class="receipt">
                        <a href="<spring:url value="/recepcionMx/init" htmlEscape="true "/>" title="<spring:message code="menu.receipt.orders.vig" />"><i class="fa fa-lg fa-fw fa-eyedropper"></i> <spring:message code="menu.receipt.orders.vig" /></a>
                    </li>
                    <li class="sendReceipt">
                        <a href="<spring:url value="/sendMxReceipt/init" htmlEscape="true "/>" title="<spring:message code="menu.send.receipt.orders" />"><i class="fa fa-lg fa-fw fa-shopping-cart "></i> <spring:message code="menu.send.receipt.orders" /></a>
                    </li>

                </ul>
            </li>

            <li class="laboratorio">
                <a href="#" title="<spring:message code="menu.lab" />"><i class="fa fa-lg fa-fw fa-flask"></i> <span class="menu-item-parent"><spring:message code="menu.lab" /></span></a>
                <ul>
                    <li class="receiptLab">
                        <a href="<spring:url value="/recepcionMx/initLab" htmlEscape="true "/>" title="<spring:message code="menu.receipt.orders.lab" />"><i class="fa fa-lg fa-fw fa-thumbs-up"></i> <spring:message code="menu.receipt.orders.lab" /></a>
                    </li>
                    <li class="generacionAlicuota">
                        <a href="<spring:url value="/generacionAlicuota/init" htmlEscape="true "/>" title="<spring:message code="menu.generate.aliquot" />"><i class="fa fa-lg fa-fw fa-ticket"></i> <spring:message code="menu.generate.aliquot" /></a>
                    </li>

                </ul>
            </li>
            <li class="resultado">
                <a href="#" title="<spring:message code="menu.result" />"><i class="fa fa-lg fa-fw fa-th-list"></i> <span class="menu-item-parent"><spring:message code="menu.result" /></span></a>
                <ul>
                    <li class="ingresoResultado">
                        <a href="<spring:url value="/resultados/init" htmlEscape="true "/>" title="<spring:message code="menu.result.income" />"><i class="fa fa-lg fa-fw fa-file-text"></i> <spring:message code="menu.result.income" /></a>
                    </li>
                </ul>
            </li>
            <li class="administracion">
                <a href="#" title="<spring:message code="menu.administration" />"><i class="fa fa-lg fa-fw fa-cogs"></i> <span class="menu-item-parent"><spring:message code="menu.administration" /></span></a>
                <ul>
                    <li class="catalogos">
                        <a href="<spring:url value="/administracion/catalogo/init" htmlEscape="true "/>" title="<spring:message code="menu.admin.catalog" />"><i class="fa fa-lg fa-fw fa-list-alt"></i> <spring:message code="menu.admin.catalog" /></a>
                    </li>
                    <li class="tipoDatos">
                        <a href="<spring:url value="/tipoDato/init" htmlEscape="true "/>" title="<spring:message code="menu.admin.datatypes" />"><i class="fa fa-lg fa-fw fa-list-ul"></i> <spring:message code="menu.admin.datatypes" /></a>
                    </li>
                    <li class="conceptos">
                        <a href="<spring:url value="/administracion/conceptos/init" htmlEscape="true "/>" title="<spring:message code="menu.admin.concept" />"><i class="fa fa-lg fa-fw fa-font"></i> <spring:message code="menu.admin.concept" /></a>
                    </li>
                </ul>
            </li>
            <li>
                <a href="<spring:url value="/logout" htmlEscape="true "/>"> <i class="fa fa-lg fa-fw fa-sign-out"></i> <span class="menu-item-parent"><spring:message code="menu.logout" /></span></a>
            </li>
            <% } %>
        </ul>

    </nav>

<span class="minifyme" data-action="minifyMenu">
	<i class="fa fa-arrow-circle-left hit"></i>
</span>
</aside>
<!-- END NAVIGATION -->