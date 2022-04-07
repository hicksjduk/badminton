<form href="/" method="POST">
<input name="date" value="${dateString}">
<input type="submit" name="action" value="Select">
<p>
<#assign round=pairings?size-1/>
<table>
<tr><td>Players</td></tr>
<tr>
<td>
<textarea name="players" cols=25 rows=10>
<#list players as p>
${p}
</#list>
</textarea>
</td>
<td>
<input type="submit" name="action" value="Save"/>
</td>
</tr>
</table>
<p>
<input type="submit" name="action" value="Next"/>
</form>
<b>
<#list pairingStrings as p>
${p}<br>
</#list>
</b>