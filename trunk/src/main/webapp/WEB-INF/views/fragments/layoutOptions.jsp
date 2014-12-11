<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:url value="/resources/img/" var="imgFolder" />
<div class="diseno">
	<span id="diseno-setting"><i class="fa fa-cog txt-color-blueDark"></i></span> 
	<form>
		<div class="no-padding margin-bottom-10"><spring:message code="layout.options" /></div>
		<section>
			<label><input name="subscription" id="smart-fixed-header" type="checkbox" class="checkbox style-0"><span><spring:message code="fixed.header" /></span></label>
			<label><input type="checkbox" name="terms" id="smart-fixed-navigation" class="checkbox style-0"><span><spring:message code="fixed.nav" /></span></label>
			<label><input type="checkbox" name="terms" id="smart-fixed-ribbon" class="checkbox style-0"><span><spring:message code="fixed.ribbon" /></span></label>
			<label><input type="checkbox" name="terms" id="smart-fixed-footer" class="checkbox style-0"><span><spring:message code="fixed.footer" /></span></label>
			<label><input type="checkbox" name="terms" id="smart-fixed-container" class="checkbox style-0"><span><spring:message code="inside.container" /></span></label>
			<label class="margin-top-10" style="display:block;"><input type="checkbox" id="smart-topmenu" class="checkbox style-0"><span><spring:message code="menu.on.top" /></span></label> 
			<div class="margin-top-10" id="smart-bgimages">
				<img id="img-bg-0" src='${imgFolder}pattern/graphy-xs.png' data-htmlbg-url='${imgFolder}pattern/graphy.png' width='22' height='22' class='margin-right-5 bordered cursor-pointer'>
				<img id="img-bg-1" src='${imgFolder}pattern/tileable_wood_texture-xs.png' width='22' height='22' data-htmlbg-url='${imgFolder}pattern/tileable_wood_texture.png' class='margin-right-5 bordered cursor-pointer'>
				<img id="img-bg-2" src='${imgFolder}pattern/sneaker_mesh_fabric-xs.png' width='22' height='22' data-htmlbg-url='${imgFolder}pattern/sneaker_mesh_fabric.png' class='margin-right-5 bordered cursor-pointer'>
				<img id="img-bg-3" src='${imgFolder}pattern/nistri-xs.png' data-htmlbg-url='${imgFolder}pattern/nistri.png' width='22' height='22' class='margin-right-5 bordered cursor-pointer'>
				<img id="img-bg-4" src='${imgFolder}pattern/paper-xs.png' data-htmlbg-url='${imgFolder}pattern/paper.png' width='22' height='22' class='bordered cursor-pointer'>
			</div>
		</section>
		<section>
		<h6 class="margin-top-10 semi-bold margin-bottom-5"><spring:message code="clear.storage" /></h6><a href="javascript:void(0);" class="btn btn-xs btn-block btn-primary" id="reset-smart-widget"><i class="fa fa-refresh"></i> <spring:message code="factory.reset" /></a></section> 
		<h6 class="margin-top-10 semi-bold margin-bottom-5"><spring:message code="smart.skin" /></h6><section id="smart-styles">
		<a href="javascript:void(0);" id="smart-style-0" data-skinlogo="${imgFolder}logo.png" class="btn btn-block btn-xs txt-color-white margin-right-5" style="background-color:#4E463F;"><i class="fa fa-check fa-fw" id="skin-checked"></i><spring:message code="smart.default" /></a>
		<a href="javascript:void(0);" id="smart-style-1" data-skinlogo="${imgFolder}logo-white.png" class="btn btn-block btn-xs txt-color-white" style="background:#3A4558;"><spring:message code="smart.dark" /></a>
		<a href="javascript:void(0);" id="smart-style-2" data-skinlogo="${imgFolder}logo-white.png" class="btn btn-xs btn-block txt-color-darken margin-top-5" style="background:#fff;"><spring:message code="smart.light" /></a>
		<a href="javascript:void(0);" id="smart-style-3" data-skinlogo="${imgFolder}logo-white.png" class="btn btn-xs btn-block txt-color-white margin-top-5" style="background:#f78c40"><spring:message code="smart.orange" /></a>
		</section>
	</form>
</div>