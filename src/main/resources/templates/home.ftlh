<form href="/" method="POST">
Players
<p>
<#assign round=pairings?size-1/>
<input hidden="true" name="date" value="${dateString}">
<textarea name="players" cols=25 rows=10>
<#list players as p>
${p}
</#list>
</textarea>
<p>
<input type="submit" name="action" value="Save"/>
<input type="submit" name="action" value="Next"/>
</form>
<b>
<#list pairingStrings as p>
${p}<br>
</#list>
</b>