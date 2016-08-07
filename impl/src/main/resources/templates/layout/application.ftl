<!DOCTYPE html>
<#macro layout>
<html>
	<#include "/common/header.ftl">
    <body>
    	<div class="Top"><#include "/common/info.ftl"></div>
    	<div class="Container">
        	<div class="Left"><#include "/common/menu.ftl"></div>
        	<div class="Middle">
				<div>
					<#include "/common/breadcrumb.ftl">
				</div>
				<div class="Content">
					<#nested>
				</div>
			</div>
	    </div>
		<div class="Bottom">
			<#include "/common/copyright.ftl">
		</div>
    </body>
</html>
</#macro>