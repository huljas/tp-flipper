<h2>${_name}</h2>

<table>
    <thead>
    <tr><th>Name</th><th>Level</th><th>Min sale</th><th>Max offer</th><th>Flip %</th><th># Sale</th><th># Offer</th></tr>
    </thead>
<tbody>
#{list items:_list, as:'item'}
<tr>
    <td>${item.name}</td><td>${item.level}</td><td>${item.min_sale}</td><td>${item.max_offer}</td><td>${item.flipGain * 100} %</td><td>${item.sale_count}</td><td>${item.offer_count}</td>
    <td>
        #{if item.isTracked()}
            <a href="@{Application.untrack(item.data_id)}">Untrack</a>
        #{/if}
        #{else}
            <a href="@{Application.track(item.data_id)}">Track</a>
        #{/else}
    </td>
</tr>
#{/list}
</tbody>
</table>
