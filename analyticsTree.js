var years = [];
var tree;
var quarters;
var months;
var weeks = [];
var russianDaysOfWeek = [];
var hours = [];
var plans = [];
var shops = [];
var brands = [];

window.onload = loadPlansAndForecast();

function updateTreeView()
{
	loadPlansAndForecast();
	redrawChart(); 
}

function initPlans() {
	return new Promise(function(resolve, reject) {
			var xhr = new XMLHttpRequest();
			xhr.open('POST', requestContextPath + '/planForecastServlet', false);
			xhr.setRequestHeader('Content-Type',
					'application/x-www-form-urlencoded');
			xhr.onreadystatechange = function() {
				if (this.readyState != 4)
					return;
				if (xhr.status != 200) {
					alert(xhr.status + ': ' + xhr.statusText);
					return null;
				}
				else
				{
					resolve(this.response);
				}
			}
			xhr.send('retailerID=' + retailerID);
	});
}

function updatePlanList(newRetailerID){
	retailerID = newRetailerID;
	loadPlansAndForecast();
}

function loadPlansAndForecast()
{
	years = [];
	plans = [];
	initPlans().then(response => {
		response = response.split("%");
		for (var i = 0; i < response.length; i++)
		{
			plans.push(JSON.parse(response[i]));
		}
		if (plans !== "noData")
		{ 
			years.push(plans[0][0].year);
			brands.push(plans[1][0].brands);
		}
		return years;
	})  
	.then(years => {initTree(years[0])});
}

function initVariables() {
	quarters = [ "1 квартал", "2 квартал", "3 квартал", "4 квартал" ];
	months = [ "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль",
			"Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь" ];
	russianDaysOfWeek = [ {
		monday : 'Понедельник'
	}, {
		tuesday : 'Вторник'
	}, {
		wednesday : 'Среда'
	}, {
		thursday : 'Четверг'
	}, {
		friday : 'Пятница'
	}, {
		saturday : 'Суббота'
	}, {
		sunday : 'Воскресенье'
	} ]
}

function getWeeksInMonth(month, year) {
	var weeks = [], firstDate = new Date(year, month, 1), lastDate = new Date(
			year, month + 1, 0), numDays = lastDate.getDate();

	var start = 1;
	var end = 8 - firstDate.getDay();
	if (firstDate.getDay() == 0)
		end = 1;
	while (start <= numDays) {
		weeks.push({
			start : start,
			end : end
		});
		start = end + 1;
		end = end + 7;
		if (end > numDays)
			end = numDays;
	}
	return weeks;
}

function getWeekCount(month, year) {
	var weeks = [], firstDate = new Date(year, month, 1), lastDate = new Date(
			year, month + 1, 0), numDays = lastDate.getDate();

	var start = 1;
	var end = 8 - firstDate.getDay();
	if (firstDate.getDay() == 0)
		end = 1;
	while (start <= numDays) {
		weeks.push({
			start : start,
			end : end
		});
		start = end + 1;
		end = end + 7;
		if (end > numDays)
			end = numDays;
	}
	return weeks.length;
}

function getDaysInMonth(month, year) {
	var date = new Date(year, month, 1);
	var days = [];
	console.log('month', month, 'date.getMonth()', date.getMonth())
	while (date.getMonth() === month) {
		days.push(new Date(date));
		date.setDate(date.getDate() + 1);
	}
	return days;
}

function getRussianDayOfWeek(date) {
	var result;
	if (date.toString().includes('Mon'))
		result = russianDaysOfWeek[0].monday;
	if (date.toString().includes('Tue'))
		result = russianDaysOfWeek[1].tuesday;
	if (date.toString().includes('Wed'))
		result = russianDaysOfWeek[2].wednesday;
	if (date.toString().includes('Thu'))
		result = russianDaysOfWeek[3].thursday;
	if (date.toString().includes('Fri'))
		result = russianDaysOfWeek[4].friday;
	if (date.toString().includes('Sat'))
		result = russianDaysOfWeek[5].saturday;
	if (date.toString().includes('Sun'))
		result = russianDaysOfWeek[6].sunday;
	return result;
}

function initDaysOfWeek(month, year, startDay, endDay) {
	var dayArray = [];
	var textDate;
	for (var i = startDay; i <= endDay; i++) {
		textDate = getDaysInMonth(month, year)[i - 1];
		dayArray.push({
			text : getRussianDayOfWeek(textDate),
			nodes : getHours()
		});
	}
	return dayArray;
}

function getHours() {
	var hours = [];
	for (var i = 1; i <= 24; i++) {
		hours.push({
			text : i < 10 ? '0' + i + ':00' : i + ':00'
		});
	}
	return hours;
}

function getWeeksNodes(month, year) {
	return getWeekCount(month, year) == 4 ? [
			{
				text : getWeeksInMonth(month, year)[0].start + ' - '
						+ getWeeksInMonth(month, year)[0].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[0].start, getWeeksInMonth(
								month, year)[0].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[1].start + ' - '
						+ getWeeksInMonth(month, year)[1].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[1].start, getWeeksInMonth(
								month, year)[1].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[2].start + ' - '
						+ getWeeksInMonth(month, year)[2].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[2].start, getWeeksInMonth(
								month, year)[2].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[3].start + ' - '
						+ getWeeksInMonth(month, year)[3].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[3].start, getWeeksInMonth(
								month, year)[3].end),
				state : {
					expanded : false
				}
			} ] : getWeekCount(month, year) == 5 ? [
			{
				text : getWeeksInMonth(month, year)[0].start + ' - '
						+ getWeeksInMonth(month, year)[0].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[0].start, getWeeksInMonth(
								month, year)[0].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[1].start + ' - '
						+ getWeeksInMonth(month, year)[1].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[1].start, getWeeksInMonth(
								month, year)[1].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[2].start + ' - '
						+ getWeeksInMonth(month, year)[2].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[2].start, getWeeksInMonth(
								month, year)[2].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[3].start + ' - '
						+ getWeeksInMonth(month, year)[3].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[3].start, getWeeksInMonth(
								month, year)[3].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[4].start + ' - '
						+ getWeeksInMonth(month, year)[4].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[4].start, getWeeksInMonth(
								month, year)[4].end),
				state : {
					expanded : false
				}
			} ] : [
			{
				text : getWeeksInMonth(month, year)[0].start + ' - '
						+ getWeeksInMonth(month, year)[0].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[0].start, getWeeksInMonth(
								month, year)[0].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[1].start + ' - '
						+ getWeeksInMonth(month, year)[1].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[1].start, getWeeksInMonth(
								month, year)[1].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[2].start + ' - '
						+ getWeeksInMonth(month, year)[2].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[2].start, getWeeksInMonth(
								month, year)[2].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[3].start + ' - '
						+ getWeeksInMonth(month, year)[3].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[3].start, getWeeksInMonth(
								month, year)[3].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[4].start + ' - '
						+ getWeeksInMonth(month, year)[4].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[4].start, getWeeksInMonth(
								month, year)[4].end),
				state : {
					expanded : false
				}
			},
			{
				text : getWeeksInMonth(month, year)[5].start + ' - '
						+ getWeeksInMonth(month, year)[5].end,
				nodes : initDaysOfWeek(month, year,
						getWeeksInMonth(month, year)[5].start, getWeeksInMonth(
								month, year)[5].end),
				state : {
					expanded : false
				}
			} ];
}

function initTree(years) {
	tree = [];
	initVariables();
	for (var i = 0; i < years.length; i++) {
		var treeElement = {
			text : years[i],
			nodes : [ {
				text : quarters[0],
				nodes : [ {
					text : months[0],
					nodes : getWeeksNodes(0, years[i]),
					state : {
						expanded : false
					}
				}, {
					text : months[1],
					nodes : getWeeksNodes(1, years[i]),
					state : {
						expanded : false
					}
				}, {
					text : months[2],
					nodes : getWeeksNodes(2, years[i]),
					state : {
						expanded : false
					}
				} ],
				state : {
					expanded : false
				}
			}, {
				text : quarters[1],
				nodes : [ {
					text : months[3],
					nodes : getWeeksNodes(3, years[i]),
					state : {
						expanded : false
					}
				}, {
					text : months[4],
					nodes : getWeeksNodes(4, years[i]),
					state : {
						expanded : false
					}
				}, {
					text : months[5],
					nodes : getWeeksNodes(5, years[i]),
					state : {
						expanded : false
					}
				} ],
				state : {
					expanded : false
				}
			}, {
				text : quarters[2],
				nodes : [ {
					text : months[6],
					nodes : getWeeksNodes(6, years[i]),
					state : {
						expanded : false
					}
				}, {
					text : months[7],
					nodes : getWeeksNodes(7, years[i]),
					state : {
						expanded : false
					}
				}, {
					text : months[8],
					nodes : getWeeksNodes(8, years[i]),
					state : {
						expanded : false
					}
				} ],
				state : {
					expanded : false
				}
			}, {
				text : quarters[3],
				nodes : [ {
					text : months[9],
					nodes : getWeeksNodes(9, years[i]),
					state : {
						expanded : false
					}
				}, {
					text : months[10],
					nodes : getWeeksNodes(10, years[i]),
					state : {
						expanded : false
					}
				}, {
					text : months[11],
					nodes : getWeeksNodes(11, years[i]),
					state : {
						expanded : false
					}
				} ],
				state : {
					expanded : false
				}
			} ],
			state : {
				expanded : false
			}
		}
		tree.push(treeElement);
	}
	drawTree();
}

function drawTree()
{
	var checkedNodesForUpdate = [];
	var options = {
		bootstrap2 : false,
		showTags : true,
		levels : 5,
		data : tree,
		uncheckedIcon : 'ui-state-default ui-icon ui-tree-checkbox-icon',
		showCheckbox : true,
		showBorder : true
	};
	$('#treeview').treeview('remove');				
	$('#treeview').treeview(options);
	$('#treeview').on('nodeChecked', function(e, node) {
		if (typeof node['nodes'] != "undefined") {
			var children = node['nodes'];
			for (var i = 0; i < children.length; i++) {
				$('#treeview').treeview('checkNode', [ children[i].nodeId, {
					silent : true
				} ]);
			}
		}
		if (node.parentId != undefined)
			$('#treeview').treeview('checkNode', [ node.parentId, {
				silent : true
			} ]);
		checkedNodesForUpdate = [];
		var nodeList = $('#treeview').treeview('getChecked', node.nodeId);
		for (var i =0; i < nodeList.length; i++)
		{
			checkedNodesForUpdate.push(nodeList[i].text.length == 1 ? nodeList[i].text[0] : nodeList[i].text);
		}
		parseRussianSymbolToDate(checkedNodesForUpdate);
		checkNodesEvent(checkedNodesForUpdate);
	});
	$('#treeview').on('nodeUnchecked', function(e, node) {
		if (typeof node['nodes'] != "undefined") {
			var children = node['nodes'];
			for (var i = 0; i < children.length; i++) {
				$('#treeview').treeview('uncheckNode', [ children[i].nodeId, {
					silent : true
				} ]);
			}
		}
		checkedNodesForUpdate = [];
		var nodeList = $('#treeview').treeview('getChecked', node.nodeId);
		for (var i =0; i < nodeList.length; i++)
		{
			checkedNodesForUpdate.push(nodeList[i].text.length == 1 ? nodeList[i].text[0] : nodeList[i].text);
		}
		parseRussianSymbolToDate(checkedNodesForUpdate);
		checkNodesEvent(checkedNodesForUpdate);
	});
}

function parseRussianSymbolToDate(checkedNodesForUpdate)
{
	for (var i = 0; i < checkedNodesForUpdate.length; i++)
	{
		for (var j = 0; j < years[0].length; j++)
			if (checkedNodesForUpdate[i] == years[0][j])
			{
				var year = checkedNodesForUpdate[i];
				checkedNodesForUpdate[i] = {"year" : year};
				i++;
			}
		switch (true) {
		case checkedNodesForUpdate[i] == quarters[0]:
			checkedNodesForUpdate[i] = {"quarter" : "01.01"};
			break;
		case checkedNodesForUpdate[i] == quarters[1]:
			checkedNodesForUpdate[i] = {"quarter" : "04.01"};
			break;
		case checkedNodesForUpdate[i] == quarters[2]:
			checkedNodesForUpdate[i] = {"quarter" : "07.01"};
			break;
		case checkedNodesForUpdate[i] == quarters[3]:
			checkedNodesForUpdate[i] = {"quarter" : "10.01"};
			break;
		case checkedNodesForUpdate[i] == months[0]:
			checkedNodesForUpdate[i] = {"month" : "01"};
			break;
		case checkedNodesForUpdate[i] == months[1]:
			checkedNodesForUpdate[i] = {"month" : "02"};
			break;
		case checkedNodesForUpdate[i] == months[2]:
			checkedNodesForUpdate[i] = {"month" : "03"};
			break;
		case checkedNodesForUpdate[i] == months[3]:
			checkedNodesForUpdate[i] = {"month" : "04"};
			break;
		case checkedNodesForUpdate[i] == months[4]:
			checkedNodesForUpdate[i] = {"month" : "05"};
			break;
		case checkedNodesForUpdate[i] == months[5]:
			checkedNodesForUpdate[i] = {"month" : "06"};
			break;
		case checkedNodesForUpdate[i] == months[6]:
			checkedNodesForUpdate[i] = {"month" : "07"};
			break;
		case checkedNodesForUpdate[i] == months[7]:
			checkedNodesForUpdate[i] = {"month" : "08"};
			break;
		case checkedNodesForUpdate[i] == months[8]:
			checkedNodesForUpdate[i] = {"month" : "09"};
			break;
		case checkedNodesForUpdate[i] == months[9]:
			checkedNodesForUpdate[i] = {"month" : "10"};
			break;
		case checkedNodesForUpdate[i] == months[10]:
			checkedNodesForUpdate[i] = {"month" : "11"};
			break;
		case checkedNodesForUpdate[i] == months[11]:
			checkedNodesForUpdate[i] = {"month" : "12"};
			break;
		case /([* - *])/.test(checkedNodesForUpdate[i]):
			checkedNodesForUpdate[i] = {"week" : checkedNodesForUpdate[i]};
			break;
		case checkedNodesForUpdate[i] == russianDaysOfWeek[0].monday:
			checkedNodesForUpdate[i] = {"dayOfWeek" : "0"};
			break;
		case checkedNodesForUpdate[i] == russianDaysOfWeek[1].tuesday:
			checkedNodesForUpdate[i] = {"dayOfWeek" : "1"};
			break;
		case checkedNodesForUpdate[i] == russianDaysOfWeek[2].wednesday:
			checkedNodesForUpdate[i] = {"dayOfWeek" : "2"};
			break;
		case checkedNodesForUpdate[i] == russianDaysOfWeek[3].thursday:
			checkedNodesForUpdate[i] = {"dayOfWeek" : "3"};
			break;
		case checkedNodesForUpdate[i] == russianDaysOfWeek[4].friday:
			checkedNodesForUpdate[i] = {"dayOfWeek" : "4"};
			break;
		case checkedNodesForUpdate[i] == russianDaysOfWeek[5].saturday:
			checkedNodesForUpdate[i] = {"dayOfWeek" : "5"};
			break;
		case checkedNodesForUpdate[i] == russianDaysOfWeek[6].sunday:
			checkedNodesForUpdate[i] = {"dayOfWeek" : "6"};
			break;
		case /([*:*])/.test(checkedNodesForUpdate[i]):
			checkedNodesForUpdate[i] = {"hour" : checkedNodesForUpdate[i]};
			break;	
		}
	}
}

function checkNodesEvent(checkedNodesForUpdate)
{
	var xhr = new XMLHttpRequest();
	xhr.open('POST', requestContextPath + '/planForecastServlet', false);
	xhr.setRequestHeader('Content-Type',
			"application/x-www-form-urlencoded");
	xhr.onreadystatechange = function() {
		if (this.readyState != 4)
			return;
		if (xhr.status != 200) {
			alert(xhr.status + ': ' + xhr.statusText);
			return null;
		}
	}
	if (checkedNodesForUpdate.length > 0)
		xhr.send('checkNodesArray=' + JSON.stringify(checkedNodesForUpdate));
	else
		xhr.send('retailerID=' + retailerID);
	updateTableCommand();
}

function paginatorBeforeAction() {
    if (PF('planValuesWV').paginator.getCurrentPage() !== PF('planValuesWV').paginator.cfg.pageCount - 1) {
        (function () {
            PF('planValuesWV').paginator.setPage(PF('planValuesWV').paginator.cfg.pageCount - 1);
        })();
    }
}

function redrawChart()
{
	PF('planValuesChartWV').plot.replot({
		resetAxes : true
	});
}

$(function() {drawTree()});