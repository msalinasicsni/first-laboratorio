<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<header id="header">
	<div id="logo-group">
		<!-- PLACE YOUR LOGO HERE -->
		<spring:url value="/resources/img/logo-white.png" var="logo" />
		<span id="logo"> <img src="${logo}" alt="<spring:message code="heading" />"> </span>
		<!-- END LOGO PLACEHOLDER -->
	</div>
	<!-- pulled right: nav area -->
	<div class="pull-right">
		
		<!-- collapse menu button -->
		<div id="hide-menu" class="btn-header pull-right">
			<span> <a href="javascript:void(0);" data-action="toggleMenu" title="<spring:message code="act.togglemenu" />"><i class="fa fa-reorder"></i></a> </span>
		</div>
		<!-- end collapse menu -->
		
		<!-- #MOBILE -->
		<!-- Top menu profile link : this shows only when top menu is active -->
		<spring:url value="/resources/img/user.png" var="user" />
		<ul id="mobile-profile-img" class="header-dropdown-list hidden-xs padding-5">
			<li class="">
				<a href="#" class="dropdown-toggle no-margin userdropdown" data-toggle="dropdown"> 
					<img src="${user}" alt="<spring:message code="lbl.user" />" class="online" />  
				</a>
				<ul class="dropdown-menu pull-right">
					<li>
						<a href="#" class="padding-10 padding-top-0 padding-bottom-0"> <i class="fa fa-user"></i> <spring:message code="lbl.profile" /></a>
					</li>
					<li class="divider"></li>
					<li>
						<a href="javascript:void(0);" class="padding-10 padding-top-0 padding-bottom-0" data-action="launchFullscreen"><i class="fa fa-arrows-alt"></i> <spring:message code="act.togglemenu" /></a>
					</li>
					<li class="divider"></li>
					<li>
						<a href="#" class="padding-10 padding-top-0 padding-bottom-0" data-action="userLogout"><i class="fa fa-sign-out fa-lg"></i> <spring:message code="act.logout" /></a>
					</li>
				</ul>
			</li>
		</ul>

		<!-- logout button -->
		<div id="logout" class="btn-header transparent pull-right">
			<span> <a href="#" title="<spring:message code="act.logout" />" data-action="userLogout" data-logout-msg="<spring:message code="msg.logout" />"><i class="fa fa-sign-out"></i></a> </span>
		</div>
		<!-- end logout button -->
		<!-- fullscreen button -->
		<div id="fullscreen" class="btn-header transparent pull-right">
			<span> <a href="javascript:void(0);" data-action="launchFullscreen" title="<spring:message code="act.launchFullscreen" />"><i class="fa fa-arrows-alt"></i></a> </span>
		</div>
		<!-- end fullscreen button -->
	</div>
	<!-- end pulled right: nav area -->
</header>