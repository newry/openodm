<a href="/">Home</a>
<#if breadcrumbs??>
	<#list breadcrumbs as item>
	     -> <a href="${item.url}">${item.label}</a>
		<#--<#if item?has_next>-> </#if> -->
	</#list>
</#if>
