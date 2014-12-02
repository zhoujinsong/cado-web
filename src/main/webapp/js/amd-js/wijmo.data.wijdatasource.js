/*
 *
 * Wijmo Library 3.20142.45
 * http://wijmo.com/
 *
 * Copyright(c) GrapeCity, Inc.  All rights reserved.
 * 
 * Licensed under the Wijmo Commercial License. Also available under the GNU GPL Version 3 license.
 * licensing@wijmo.com
 * http://wijmo.com/widgets/license/
 *
 */
var wijmo;
define(["./wijmo.data"], function () { 

/// <reference path="src/arrayDataView.ts"/>
var __extends = this.__extends || function (d, b) {
    for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
    function __() { this.constructor = d; }
    __.prototype = b.prototype;
    d.prototype = new __();
};
(function (wijmo) {
    /*globals jQuery, Globalize, wijmo */
    /*
    * Depends:
    *  wijmo.data.js
    *  globalize.js
    *  jquery.js
    *
    */
    (function (_data) {
        var $ = jQuery;

        var wijdatasourceReader = (function () {
            function wijdatasourceReader(originalReader) {
                this._originalReader = originalReader;
            }
            wijdatasourceReader.prototype.read = function (wijDataSource) {
                wijDataSource.items = null;

                if (this._originalReader && $.isFunction(this._originalReader.read)) {
                    this._originalReader.read(wijDataSource);
                }

                if (!$.isArray(wijDataSource.items)) {
                    if ($.isArray(wijDataSource.data)) {
                        wijDataSource.items = wijDataSource.data;
                    } else if (wijDataSource.data && $.isArray(wijDataSource.data.rows)) {
                        wijDataSource.items = wijDataSource.data.rows; // remoteDynamical
                    } else {
                        wijDataSource.items = [];
                    }
                }

                if (wijDataSource.dynamic) {
                    if (!wijDataSource.data || isNaN(wijDataSource.data.totalRows)) {
                        throw "totalRows value is missing";
                    }
                }
            };
            return wijdatasourceReader;
        })();

        var WijdatasourceView = (function (_super) {
            __extends(WijdatasourceView, _super);
            function WijdatasourceView(dataSource) {
                var _this = this;
                _super.call(this);
                this.dataSource = dataSource;
                this.isRemote = true;
                this.localPaging = true;

                this._origLoaded = dataSource.loaded;
                this._origReader = dataSource.reader;

                dataSource.loaded = function (e, data) {
                    _this._loaded();
                    if ($.isFunction(_this._origLoaded)) {
                        _this._origLoaded.apply(_this, arguments);
                    }
                };

                dataSource.reader = new wijdatasourceReader(dataSource.reader);

                var hasItems = dataSource.items && dataSource.items.length > 0;
                if ($.isArray(dataSource.data) && !hasItems) {
                    dataSource.read();
                }

                if (dataSource.items) {
                    this._loaded();
                }
            }
            WijdatasourceView.prototype.dispose = function () {
                this.dataSource.loaded = this._origLoaded;
                this.dataSource.reader = this._origReader;
                _super.prototype.dispose.call(this);
            };

            WijdatasourceView.prototype.getProperties = function () {
                return this.sourceArray && this.sourceArray.length ? _data.ArrayDataViewBase._getProps(this.sourceArray[0]) : [];
            };
            WijdatasourceView.prototype._loaded = function () {
                this.sourceArray = this.dataSource.items;
                if (this.dataSource.data && _data.util.isNumeric(this.dataSource.data.totalRows)) {
                    this._totalItemCount(this.dataSource.data.totalRows);
                }
                var def = _super.prototype._localRefresh.call(this, !this.dataSource.dynamic);
                if (this._currentDeferred) {
                    def.then(this._currentDeferred.resolve);
                }
            };

            WijdatasourceView.prototype._remoteRefresh = function () {
                if (this._currentDeferred && this._currentDeferred.state() === "pending") {
                    this._currentDeferred.fail();
                }
                this._currentDeferred = $.Deferred();

                var userData = {}, forceReload = false;

                if (this.dataSource.dynamic) {
                    forceReload = true;
                    userData.data = this._prepareRequest();
                    if (this.dataSource.proxy) {
                        if (!this._origDataOption) {
                            this._origDataOption = $.extend({}, this.dataSource.proxy.options.data);
                        }
                        this.dataSource.proxy.options.data = $.extend({}, this._origDataOption, userData.data);
                    }
                }

                this.dataSource.load(userData, forceReload);
                return this._currentDeferred;
            };

            WijdatasourceView.prototype._prepareRequest = function () {
                return {
                    filtering: this._prepareFilterRequest(),
                    paging: this._preparePageRequest(),
                    sorting: this._prepareSortRequest()
                };
            };

            WijdatasourceView.prototype._prepareFilterRequest = function () {
                var result = [];

                if (!this._shape._compiledFilter.isEmpty && this._shape._compiledFilter.normalized) {
                    $.each(this._shape._compiledFilter.normalized, function (prop, cond) {
                        result.push({
                            dataKey: prop,
                            filterOperator: cond.op.Name,
                            filterValue: cond.value
                        });
                    });
                }

                return result;
            };

            WijdatasourceView.prototype._preparePageRequest = function () {
                return {
                    pageIndex: this._shape.pageIndex(),
                    pageSize: this._shape.pageSize()
                };
            };

            WijdatasourceView.prototype._prepareSortRequest = function () {
                if (this._shape._compiledSort.isEmpty || !this._shape._compiledSort.normalized || this._shape._compiledSort.normalized.length == 0) {
                    return [];
                }

                return $.map(this._shape._compiledSort.normalized, function (sd) {
                    return {
                        dataKey: sd.property,
                        sortDirection: sd.asc ? "ascending" : "descending"
                    };
                });
            };
            return WijdatasourceView;
        })(_data.ArrayDataViewBase);

        _data.registerDataViewFactory(function (wds) {
            if (typeof wijdatasource !== "function" || !(wds instanceof wijdatasource))
                return;
            return new WijdatasourceView(wds);
        });
    })(wijmo.data || (wijmo.data = {}));
    var data = wijmo.data;
})(wijmo || (wijmo = {}));
});
