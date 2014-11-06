(function(wijmo){
	'use strict';

    // create FlexChart
	var gettingStartChart = new wijmo.chart.FlexChart('#gettingStartChart');

    // initialize FlexChart's properties
	gettingStartChart.initialize({
	    itemsSource: appData,
	    bindingX: 'country',
	    series: [
            { name: 'Sales', binding: 'sales' },
            { name: 'Expenses', binding: 'expenses' },
            { name: 'Downloads', binding: 'downloads' }
	    ]
	});
})(wijmo);