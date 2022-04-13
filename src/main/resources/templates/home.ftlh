<head>
<title>Badminton</title>
<meta name="viewport" content="width=100, initial-scale=1">
</head>
<form href="/" method="POST">
<input type="date" name="date" value="${session.dateString}" onchange="dateselect.click()">
<input type="submit" name="action" value="Date" id="dateselect" hidden="true">
<p>
<table>
<tr><td>Players</td></tr>
<tr>
<td>
<textarea name="players" cols=25 rows=10>
<#list session.players as p>
${p}
</#list>
</textarea>
</td>
<td>
<input type="button" value="&lt;&lt;" style="width: 100%">
<p>
<input type="submit" name="action" value="Save"/>
</td>
<td style="height: 100%">
<select multiple="true" style="height: 100%">
<#list ["Pete Hicks", "Jeremy Hicks", "Mike Pilley"] as p>
<option value="${p}">${p}</option>
</#list>
</select>
</td>
</tr>
</table>
<p>
<#-->
<input type="submit" name="action" value="Prev"/>
<-->
<!--
${session.round + 1}
-->
<input type="submit" name="action" value="Next"/>
</form>
<b>
<#list session.pairingStrings as p>
${p}<br>
</#list>
</b>