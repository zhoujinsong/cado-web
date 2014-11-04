(function (wijmo) {
	'use strict';

    // create FlexChart
	var chartLegendToggle = new wijmo.chart.FlexChart('#chartLegendToggle');

    // initialize FlexChart's properties
	chartLegendToggle.initialize({
	    itemsSource: appData,
	    bindingX: 'country',
	    legendToggle: true,
	    series: [
            { name: 'Sales', binding: 'sales' },
            { name: 'Expenses', binding: 'expenses' },
            { name: 'Downloads', binding: 'downloads' }
	    ]
	});

	chartLegendToggle.seriesVisibilityChanged.addHandler(function () {
        // loop through chart series
	    $.each(chartLegendToggle.series, function (idx, series) {
	        var seriesName = series.name,
				checked = series.visibility === wijmo.chart.SeriesVisibility.Visible;

            // update custom checkbox panel
	        $('#cb' + seriesName).prop('checked', checked);
	    });
	});

    // loop through custom check boxes
	$.each(['cbSales', 'cbExpenses', 'cbDownloads'], function (idx, item) {
        // update checkbox and toggle FlexChart's series visibility when clicked
	    $('#' + item)
            .prop('checked', chartLegendToggle.series[idx].visibility === wijmo.chart.SeriesVisibility.Visible)
			.on('click', function () {
				if ($(this).is(':checked')) {
					chartLegendToggle.series[idx].visibility = wijmo.chart.SeriesVisibility.Visible;
				}
				else {
					chartLegendToggle.series[idx].visibility = wijmo.chart.SeriesVisibility.Legend;
				}
			});
	});
})(wijmo);