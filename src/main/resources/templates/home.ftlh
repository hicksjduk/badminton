<head>
<title>Badminton</title>
<meta name="viewport" content="width=100, initial-scale=1">
<script>
function selectPlayers() {
	const existing = enteredplayers.value.split("\n").filter(s => s.length)
	const selected = [...availableplayers.selectedOptions].map(o => o.value)
	const toAdd = []
	selected.forEach(s => {
	   for (let i = s.indexOf(" "); i <= s.length; i++) {
	      const str = s.substring(0, i).trim()
	      if (!existing.includes(str) && !toAdd.includes(str)) {
	         toAdd.push(str)
	         break;
	      }
	   }
	})
	if (toAdd.length)
	   enteredplayers.value = [...existing, ...toAdd].join("\n") 
}
</script>
</head>
<form href="/" method="POST">
<input type="date" name="date" value="${session.dateString}" onchange="dateselect.click()">
<input type="submit" name="action" value="Date" id="dateselect" hidden="true">
<p>
<table>
<tr><td>Players</td></tr>
<tr>
<td>
<textarea name="players" id="enteredplayers" cols=15 rows=10>
<#list session.players as p>
${p}
</#list>
</textarea>
</td>
<td>
<input type="button" value="&lt;&lt;" style="width: 100%" onclick="selectPlayers()">
<p>
<input type="submit" name="action" value="Save"/>
</td>
<td style="height: 100%">
<select id="availableplayers" multiple="true" style="height: 100%">
<#list players as p>
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