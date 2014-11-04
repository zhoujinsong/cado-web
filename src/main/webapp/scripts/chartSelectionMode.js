(function (wijmo) {
	'use strict';

	var chartSelectionMode = new wijmo.chart.FlexChart('#chartSelectionMode'),
		typeMenu = new wijmo.input.Menu('#chartTypeMenu'),
		selectionModeMenu = new wijmo.input.Menu('#seletionModeMenu'),
		seriesContainer = $('#seriesContainer'),
		detailContainer = $('#detailContainer');

    // initialize FlexChart's properties
	chartSelectionMode.initialize({
	    itemsSource: appData,
	    bindingX: 'country',
	    selectionMode: wijmo.chart.SelectionMode.Series,
	    series: [
            { name: 'Sales', binding: 'sales' },
            { name: 'Expenses', binding: 'expenses' },
            { name: 'Downloads', binding: 'downloads' }
	    ]
	});

    // update details when the FlexChart's selection changes
	chartSelectionMode.selectionChanged.addHandler(function () {
		var currentSelection = chartSelectionMode.selection,
			currentSelectItem;

		if (currentSelection) {
		    seriesContainer.show(); // show container

			$('#seriesName').text(currentSelection.name);
			currentSelectItem = currentSelection.collectionView.currentItem;

			if (currentSelectItem && selectionModeMenu.selectedValue === '2') {
				setSeriesDetail(currentSelectItem); // update details
			}
		}
	});

    // update Menu header
	updateMenuHeader(typeMenu, 'Chart Type');
	typeMenu.selectedIndexChanged.addHandler(function () {
		if (typeMenu.selectedValue) {
			// update FlexChart' chartType
			chartSelectionMode.chartType = parseInt(typeMenu.selectedValue);

			// update Menu header
			updateMenuHeader(typeMenu, 'Chart Type');
		}
	});

    // update Menu header
	updateMenuHeader(selectionModeMenu, 'Selection Mode');
	selectionModeMenu.selectedIndexChanged.addHandler(function () {
		if (selectionModeMenu.selectedValue) {
			// update FlexChart' selectionMode
			chartSelectionMode.selectionMode = parseInt(selectionModeMenu.selectedValue);

			// toggle the series panel's visiblity
			if (selectionModeMenu.selectedValue === '0' || !chartSelectionMode.selection) {
				seriesContainer.hide();
			}
			else {
				seriesContainer.show();
			}

			// toggle the series panel's visiblity
			if (selectionModeMenu.selectedValue !== '2' || !chartSelectionMode.selection || !chartSelectionMode.selection.collectionView.currentItem) {
				detailContainer.hide();
			}
			else {
				// update the details
				setSeriesDetail(chartSelectionMode.selection.collectionView.currentItem);
			}

			// update Menu header
			updateMenuHeader(selectionModeMenu, 'Selection Mode');
		}
	});

    // helper method to show details of the FlexChart's current selection
	function setSeriesDetail(currentSelectItem) {
		detailContainer.show();
		$('#seriesCountry').text(currentSelectItem.country);
		$('#seriesSales').text(wijmo.Globalize.format(currentSelectItem.sales, 'c2'));
		$('#seriesExpenses').text(wijmo.Globalize.format(currentSelectItem.expenses, 'c2'));
		$('#seriesDownloads').text(wijmo.Globalize.format(currentSelectItem.downloads, 'n0'));
	};

    // helper method for changing menu header
	function updateMenuHeader(menu, prefix) {
	    menu.header = '<b>' + prefix  + '</b>: ' + menu.text;
	}
})(wijmo);