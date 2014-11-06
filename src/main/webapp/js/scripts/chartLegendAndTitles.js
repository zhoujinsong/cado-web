(function (wijmo) {
	'use strict';

	var chartLegendAndTitles = new wijmo.chart.FlexChart('#chartLegendAndTitles'),
		positionMenu = new wijmo.input.Menu('#positionMenu'),
		headerInput = $('#headerInput'),
		footerInput = $('#footerInput'),
		xTitleInput = $('#xTitleInput'),
		yTitleInput = $('#yTitleInput');

    // initialize FlexChart's properties
	chartLegendAndTitles.initialize({
	    itemsSource: appData,
	    bindingX: 'country',
	    header: 'Sample Chart',
	    footer: 'copyright (c) ComponentOne',
	    axisX: { title: 'country' },
	    axisY: { title: 'amount' },
	    series: [
            { name: 'Sales', binding: 'sales' },
            { name: 'Expenses', binding: 'expenses' },
            { name: 'Downloads', binding: 'downloads' }
	    ]
	});

    // sync the input's value with FlexChart's header
	headerInput.val(chartLegendAndTitles.header);

    // update the FlexChart's header
	headerInput.on('keyup', function () {
		chartLegendAndTitles.header = headerInput.val();
	});

    // sync the input's value with FlexChart's footer
	footerInput.val(chartLegendAndTitles.footer);

    // update the FlexChart's footer
	footerInput.on('keyup', function () {
		chartLegendAndTitles.footer = footerInput.val();
	});

    // sync the input's value with FlexChart's X-Axis title
	xTitleInput.val(chartLegendAndTitles.axisX.title);

    // update the FlexChart's X-Axis title
	xTitleInput.on('keyup', function () {
		chartLegendAndTitles.axisX.title = xTitleInput.val();
	});

    // sync the input's value with FlexChart's Y-Axis title
	yTitleInput.val(chartLegendAndTitles.axisY.title);

    // update the FlexChart's Y-Axis title
	yTitleInput.on('keyup', function () {
		chartLegendAndTitles.axisY.title = yTitleInput.val();
	});

    // update menu's header
	updatePositionMenuHeader();
	positionMenu.selectedIndexChanged.addHandler(function () {
		if (positionMenu.selectedValue) {
			// update the FlexChart legend's position
			chartLegendAndTitles.legend.position = parseInt(positionMenu.selectedValue);

			// update menu's header
			updatePositionMenuHeader();
		}
	});

	function updatePositionMenuHeader() {
	    positionMenu.header = '<b>Legend:</b> ' + positionMenu.text;
	}
})(wijmo);