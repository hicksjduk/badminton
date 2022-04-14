<head>
<title>Badminton</title>
<meta name="viewport" content="width=100, initial-scale=1">
<style type= "text/css" media="screen and (max-width: 481px)">
#availableplayers {
	visibility: hidden
}
</style>
<style type= "text/css" media="screen and (min-width: 481px)">
#mobilemessage {
	visibility: hidden
}
</style>
<script>
function selectPlayers() {
	const players = enteredplayers.value.split("\n").filter(s => s.length)
	const selections = [...availableplayers.selectedOptions].map(o => o.value)
	players.push(...selections.map(s => addPlayer(s, players)))
	enteredplayers.value = [...players].join("\n") 
}

function addPlayer(player, existing) {
	for (let i = player.indexOf(" "); i <= player.length; i++) {
	   const str = player.substring(0, i).trim()
	   if (!existing.includes(str))
	      return str
	}
}
</script>
</head>
<div id="mobilemessage">Hello mobile!</div>
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
<input type="submit" name="action" value="Save"/>
</td>
<td style="height: 100%">
<select id="availableplayers" multiple style="height: 100%" onchange="selectPlayers()">
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