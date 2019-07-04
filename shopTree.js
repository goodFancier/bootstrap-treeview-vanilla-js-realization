window.onload = initShopTree();

var shopTree;

function initVariables() {
	shopTree = [];
}

function initShops(shopsArray) {
	var shops = [];
	for (var i = 0; i < shopsArray.length; i++) {
		var treeElement = {
			text : shopsArray[i],
			state : {
				expanded : false
			}
		}
		shops.push(treeElement);
	}
	return shops;
}

function initShopTree() {
	initVariables();
	var brandsArray = Object.entries(brands[0]);
	for (var i = 0; i < brandsArray.length; i++) {
		var treeElement = {
			text : brandsArray[i][0],
			nodes : initShops(brandsArray[i][1]),
			state : {
				expanded : false
			}
		}
		shopTree.push(treeElement);
	}
}

$(function() {
	var options = {
		bootstrap2 : false,
		levels : 5,
		data : shopTree,
		selectedBackColor : 'var(--turquoise)',
		uncheckedIcon : 'ui-state-default ui-icon ui-tree-checkbox-icon',
		multiselect : true,
		showCheckbox : true,
		showBorder : true
	};
	$('#shopTreeview').treeview(options);
	$('#shopTreeview').on(
			'nodeChecked',
			function(e, node) {
				if (typeof node['nodes'] != "undefined") {
					var children = node['nodes'];
					for (var i = 0; i < children.length; i++) {
						$('#shopTreeview').treeview('checkNode',
								[ children[i].nodeId, {
									silent : true
								} ]);
					}
				}
			});
	$('#shopTreeview').on(
			'nodeUnchecked',
			function(e, node) {
				if (typeof node['nodes'] != "undefined") {
					var children = node['nodes'];
					for (var i = 0; i < children.length; i++) {
						$('#shopTreeview').treeview('uncheckNode',
								[ children[i].nodeId, {
									silent : true
								} ]);
					}
				}
			});
});