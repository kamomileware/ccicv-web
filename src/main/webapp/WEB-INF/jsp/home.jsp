<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>
<style type="text/css">
    input.error { background-color: yellow; }
</style>
<s:layout-render name="/WEB-INF/jsp/layout.jsp" title="CCI Converter">
  <s:layout-component name="body">
    <H1>CCI Converter</H1>
    
    <s:form beanclass="org.karmaware.ccicv.web.action.ConvertActionBean" onsubmit="/*javascript:document.getElementById('conver-input').disabled=true;*/">
    	<s:errors/>
    	<s:label for="fileUp-input">Upload a file:</s:label><br/>
    	<s:file id="fileUp-input" style="width:40em;" name="xlsAttachment"/><br/>
    	<s:checkbox id="compress-input" name="compresed"></s:checkbox>
    	<s:label for="compress-input">Compress result</s:label><br/>
		<s:checkbox id="debug-input" name="debug" ></s:checkbox>
    	<s:label for="debug-input">Debug mode (Marks fields and replace spaces with asterisk)</s:label><br/>
    	<div class="buttons">
	        <s:submit id="conver-input" name="convert" value="Convert"/>
	    </div>
	</s:form>
    
  </s:layout-component>
</s:layout-render>
