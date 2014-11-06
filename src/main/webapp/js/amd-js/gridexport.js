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
define(["./wijmo.wijgrid", "./wijmo.wijgrid", "exportUtil"], function () { 

/// <reference path="../wijgrid/Grid.ts/Grid/interfaces.ts" />
/// <reference path="../wijgrid/Grid.ts/Grid/wijgrid.ts" />
/// <reference path="exportUtil.ts" />

(function (wijmo) {
    (function (exporter) {
        "use strict";

        var $ = jQuery;

        

        

        

        

        

        

        

        

        

        

        /** @ignore */
        (function (RowTemplateType) {
            RowTemplateType[RowTemplateType["None"] = 0] = "None";
            RowTemplateType[RowTemplateType["Template"] = 1] = "Template";
            RowTemplateType[RowTemplateType["AltTemplate"] = 2] = "AltTemplate";
        })(exporter.RowTemplateType || (exporter.RowTemplateType = {}));
        var RowTemplateType = exporter.RowTemplateType;

        /** @ignore */
        (function (ColType) {
            ColType[ColType["Text"] = 1] = "Text";
            ColType[ColType["Image"] = 2] = "Image";
            ColType[ColType["Link"] = 3] = "Link";
            ColType[ColType["Checkbox"] = 4] = "Checkbox";
            ColType[ColType["Html"] = 5] = "Html";
        })(exporter.ColType || (exporter.ColType = {}));
        var ColType = exporter.ColType;
        ;

        

        

        

        

        /** @ignore */
        var exportContext = (function () {
            function exportContext(grid, environmentInfo) {
                var self = this;
                self.environmentInfo = environmentInfo;
                self._init(grid);
            }
            exportContext.prototype._init = function (grid) {
                var self = this, dataView = grid.dataView(), $cols = $(grid.element).find(">colgroup>col");
                self.grid = grid;
                self.showRowHeader = grid.option("showRowHeader");
                self.dataView = dataView;
                self.dataCount = dataView.count();
                if (self.dataCount > 0) {
                    var first = dataView.item(0);
                    self.isArrayData = $.type(first) == "array";
                }

                self._initColInfos($cols);
            };

            exportContext.prototype._initColInfos = function ($cols) {
                var self = this;
                self.colLeaves = $.grep(self.grid.columns(), function (element) {
                    return element.option("isLeaf");
                });
                self.colWidths = [];
                self.colInfos = $cols.map(function (colIndex) {
                    if (colIndex < 0 || colIndex > self.colLeaves.length - 1)
                        return null;

                    var col = self.colLeaves[colIndex], type = 1 /* Text */, dataTypeQualifier = col.option("dataTypeQualifier");

                    if (self.isHtmlCol(col)) {
                        type = 5 /* Html */;
                    } else if (self.isLinkCol(col)) {
                        type = 3 /* Link */;
                    } else if (self.isImageCol(col)) {
                        type = 2 /* Image */;
                    } else if (self.isCheckBoxCol(col)) {
                        type = 4 /* Checkbox */;
                    }
                    self.colWidths.push(self.computedWidthToPt($(this).css("width")));
                    var colInfo = {
                        formatString: col.option("dataFormatString"),
                        dataType: col.option("dataType"),
                        type: type
                    };
                    if (dataTypeQualifier) {
                        colInfo.dataTypeQualifier = dataTypeQualifier;
                    }
                    return colInfo;
                }).get();
            };

            exportContext.prototype.computedWidthToPt = function (lengthStr) {
                return gridExporter.pxToPt(lengthStr, this.environmentInfo.dpiX);
            };

            exportContext.prototype.computedHeightToPt = function (lengthStr) {
                return gridExporter.pxToPt(lengthStr, this.environmentInfo.dpiY);
            };

            exportContext.prototype.getDataValue = function (rowIndex, colIndex) {
                var self = this;
                if (rowIndex >= self.dataCount || rowIndex < 0 || colIndex < 0)
                    return null;
                var data = self.grid.dataView().item(rowIndex);
                if (self.isArrayData) {
                    if (self.showRowHeader)
                        colIndex--;
                    if (colIndex < 0 || colIndex >= data.length)
                        return null;
                    return data[colIndex];
                }
                var cols = self.colLeaves;
                if (colIndex >= cols.length)
                    return null;
                var dataKey = cols[colIndex].option("dataKey");
                return data[dataKey];
            };

            exportContext.prototype.isCheckBoxCol = function (col) {
                return col instanceof wijmo.grid.c1booleanfield;
            };

            exportContext.prototype.isHtmlCol = function (col) {
                if (col.option("cellFormatter") != null) {
                    return true;
                }
                return false;
            };

            exportContext.prototype.isImageCol = function (col) {
                return false;
            };

            exportContext.prototype.isLinkCol = function (col) {
                if (col instanceof wijmo.grid.c1buttonfield && col.option("buttonType") == "link") {
                    return true;
                }

                return false;
            };
            return exportContext;
        })();
        exporter.exportContext = exportContext;

        /**
        Export the grid to pdf, xls or csv file.
        * @param {GridExportSetting} The export settings.
        */
        function exportGrid(setting) {
            exporter.exportFile(setting, JSON.stringify(gridExporter.exportSource(setting)));
        }
        exporter.exportGrid = exportGrid;

        /** @ignore */
        var gridExporter = (function () {
            function gridExporter() {
            }
            gridExporter.exportSource = function (setting) {
                var environmentInfo = wijmo.exporter.gridExporter._generateEnvironmentInfo();
                var exportSource = {
                    grid: wijmo.exporter.gridExporter._generateTemplate(new exportContext(setting.grid, environmentInfo)),
                    environmentInfo: environmentInfo,
                    exportFileType: setting.exportFileType
                };
                if (setting.fileName)
                    exportSource.fileName = setting.fileName;
                if (setting.pdf)
                    exportSource.pdf = setting.pdf;
                if (setting.excel)
                    exportSource.excel = setting.excel;
                return exportSource;
            };

            // the length string's pattern should be like 0px
            gridExporter.pxToPt = function (lengthStr, dpi) {
                if (lengthStr == null)
                    return 0;
                lengthStr = lengthStr.trim().toLowerCase();
                var suffix = "px";
                if (lengthStr.indexOf(suffix, lengthStr.length - suffix.length) == -1) {
                    return 0;
                }
                var numberPart = parseFloat(lengthStr);
                if (isNaN(numberPart))
                    return 0;
                return numberPart / dpi * wijmo.exporter.gridExporter.pointPerInch;
            };

            gridExporter._generateEnvironmentInfo = function () {
                var location = window.location, baseUrl = location.protocol + "//" + location.hostname + (location.port && ":" + location.port) + "/", dpiTestDiv = $("<div style='height:1in;left:-100%;position: absolute;top:-100%;width:1in;'></div>").appendTo($(document.body));
                return {
                    baseUrl: baseUrl,
                    dpiX: dpiTestDiv.outerWidth(),
                    dpiY: dpiTestDiv.outerHeight(),
                    culture: navigator.language ? navigator.language : navigator.browserLanguage
                };
            };

            gridExporter._generateTemplate = function (context) {
                //TODO: need combine the elements in the tables when scrolling feature is using.
                var $element = $(context.grid.element), style = wijmo.exporter.gridExporter._readStyle(context, $element), header = wijmo.exporter.gridExporter._readSection(context, $element.find(">thead")), footer = wijmo.exporter.gridExporter._readSection(context, $element.find(">tfoot")), body = wijmo.exporter.gridExporter._readSection(context, $element.find(">tbody"), true), $parent = $element.parent();
                if ($parent.hasClass("wijmo-wijgrid")) {
                    var combinedStyle = {};
                    $.extend(combinedStyle, wijmo.exporter.gridExporter._readStyle(context, $parent), style);
                    style = combinedStyle;
                }
                return {
                    colWidths: context.colWidths,
                    style: style,
                    header: header,
                    footer: footer,
                    body: body,
                    width: context.computedWidthToPt($element.css('width'))
                };
            };

            gridExporter._readSection = function (context, $section, isBody) {
                return {
                    colInfos: isBody ? context.colInfos : null,
                    style: wijmo.exporter.gridExporter._readStyle(context, $section),
                    rows: wijmo.exporter.gridExporter._readRows(context, $section, isBody),
                    height: context.computedHeightToPt($section.css('height'))
                };
            };

            gridExporter._readRows = function (context, $section, isBody) {
                if (!$section || !$section.length)
                    return null;
                return $section.find(">tr").map(function (index) {
                    var $row = $(this), rowTemplateType = (isBody && index == 1) ? 2 /* AltTemplate */ : ((!isBody || index == 0) ? 1 /* Template */ : 0 /* None */), row = {
                        height: context.computedHeightToPt($row.css("height")),
                        cells: wijmo.exporter.gridExporter._readCells(context, $row, index, isBody, rowTemplateType)
                    };
                    if (rowTemplateType != 0 /* None */) {
                        row.style = wijmo.exporter.gridExporter._readStyle(context, $row);
                        row.templateType = rowTemplateType;
                    }
                    return row;
                }).get();
            };

            gridExporter._readCells = function (context, $row, rowIndex, isBody, rowTemplateType) {
                var isBodyRowTemplate = isBody && rowTemplateType != 0 /* None */;
                return $row.find(">th,>td").map(function (index) {
                    var $cell = $(this), cell = {}, cspan = wijmo.exporter.gridExporter._getSpan($cell, "colspan"), rspan = wijmo.exporter.gridExporter._getSpan($cell, "rowspan"), colIndex = wijmo.exporter.gridExporter._cellPos($cell).left, colInfo = context.colInfos[colIndex], innerCell = $cell.find(">.wijmo-wijgrid-innercell");

                    if (innerCell.length == 0)
                        innerCell = $cell;

                    if (colIndex != index)
                        cell.colIndex = colIndex;

                    if (rspan != 1)
                        cell.rowSpan = rspan;

                    if (cspan != 1)
                        cell.colSpan = cspan;

                    if (isBody) {
                        switch (colInfo.type) {
                            case 2 /* Image */:
                                var $img = $cell.find("img");
                                if ($img.length != 0) {
                                    var imageInfo = {
                                        src: $img.attr("src"),
                                        height: context.computedHeightToPt($img.css("height")),
                                        width: context.computedWidthToPt($img.css("width"))
                                    };
                                    cell.value = imageInfo;
                                }
                                break;
                            case 3 /* Link */:
                                var $link = $cell.find("a");
                                if ($link.length != 0) {
                                    var linkInfo = {
                                        href: $link.attr("href"),
                                        text: $link.text()
                                    };
                                    cell.value = linkInfo;
                                }
                                break;
                            case 5 /* Html */:
                                cell.value = $cell.html();
                                break;
                            case 1 /* Text */:
                            case 4 /* Checkbox */:
                                cell.value = context.getDataValue(rowIndex, colIndex);
                                cell.text = $cell.text();
                                break;
                            default:
                                throw "Wrong column type!";
                        }
                    } else {
                        cell.text = $cell.text();
                    }

                    if (rowTemplateType != 0 /* None */) {
                        cell.style = wijmo.exporter.gridExporter._readStyle(context, $cell);
                        $.extend(cell.style, wijmo.exporter.gridExporter._readFontStyle(context, innerCell));
                    }

                    return cell;
                }).get();
            };

            gridExporter._readStyle = function (context, $element) {
                var style = {
                    backgroundColor: $element.css("backgroundColor"),
                    verticalAlign: $element.css("verticalAlign")
                };
                $.extend(style, wijmo.exporter.gridExporter._readBorderStyle(context, "Top", $element), wijmo.exporter.gridExporter._readBorderStyle(context, "Right", $element), wijmo.exporter.gridExporter._readBorderStyle(context, "Bottom", $element), wijmo.exporter.gridExporter._readBorderStyle(context, "Left", $element));
                return style;
            };

            gridExporter._readBorderStyle = function (context, borderType, $element) {
                var style = {}, colorProp = "border" + borderType + "Color", styleProp = "border" + borderType + "Style", widthProp = "border" + borderType + "Width", borderStyle = $element.css(styleProp);
                if (borderStyle != "none") {
                    var borderWidth = context.computedWidthToPt($element.css(widthProp));
                    if (borderWidth != 0) {
                        style[colorProp] = $element.css(colorProp);
                        style[styleProp] = borderStyle;
                        style[widthProp] = borderWidth;
                    }
                }
                return style;
            };

            gridExporter._readFontStyle = function (context, $element) {
                var style = {};
                style.textAlign = $element.css("textAlign");
                style.color = $element.css("color");

                style.fontFamily = $element.css("fontFamily");
                style.fontWeight = $element.css("fontWeight");
                style.fontStyle = $element.css("fontStyle");
                style.fontSize = context.computedHeightToPt($element.css("fontSize"));
                style.textDecoration = $element.css("textDecoration");
                return style;
            };

            gridExporter._cellPos = function ($cell, rescan) {
                var pos = $cell.data("cellPos");
                if (!pos || rescan) {
                    var $table = $cell.closest("table, thead, tbody, tfoot");
                    wijmo.exporter.gridExporter._scanTable($table);
                }
                pos = $cell.data("cellPos");
                return pos;
            };

            gridExporter._getSpan = function ($cell, spanName) {
                var span = $cell.attr(spanName);
                if (span) {
                    var spanNumber = parseInt(span);
                    if (!isNaN(spanNumber)) {
                        return spanNumber;
                    }
                }
                return 1;
            };

            gridExporter._scanTable = function ($table) {
                var m = [];
                $table.children("tr").each(function (y, row) {
                    $(row).children("td, th").each(function (x, cell) {
                        var $cell = $(cell), cspan = wijmo.exporter.gridExporter._getSpan($cell, "colspan"), rspan = wijmo.exporter.gridExporter._getSpan($cell, "rowspan"), tx, ty;
                        cspan = cspan ? cspan : 1;
                        rspan = rspan ? rspan : 1;
                        for (; m[y] && m[y][x]; ++x)
                            ;
                        for (tx = x; tx < x + cspan; ++tx) {
                            for (ty = y; ty < y + rspan; ++ty) {
                                if (!m[ty]) {
                                    m[ty] = [];
                                }
                                m[ty][tx] = true;
                            }
                        }
                        var pos = { top: y, left: x };
                        $cell.data("cellPos", pos);
                    });
                });
            };
            gridExporter.pointPerInch = 72;
            return gridExporter;
        })();
        exporter.gridExporter = gridExporter;

        var innerExportGrid = function (fileName, type, settings, serviceUrl) {
            var s, fName, t, setting = {}, sUrl;
            fName = fileName === undefined ? "export" : fileName;
            t = type === undefined ? "csv" : type;
            t = t.substr(0, 1).toUpperCase() + t.substr(1);
            if (typeof settings === "string" && arguments.length === 3) {
                sUrl = settings;
            } else {
                sUrl = serviceUrl === undefined ? "http://demos.componentone.com/ASPNET/ExportService/exportapi/grid" : serviceUrl;
                setting = settings;
            }
            s = {
                fileName: fName,
                serviceUrl: sUrl,
                grid: this,
                exportFileType: wijmo.exporter.ExportFileType[t]
            };
            if (type === "pdf") {
                s["pdf"] = setting;
            }
            if (type === "xls" || type === "xlsx") {
                s["excel"] = setting;
            }
            wijmo.exporter.exportGrid(s);
        };

        if (wijmo.grid && wijmo.grid.wijgrid) {
            wijmo.grid.wijgrid.prototype.exportGrid = innerExportGrid;
        }

        if ($.wijmo.wijgrid) {
            $.wijmo.wijgrid.prototype.exportGrid = innerExportGrid;
        }
    })(wijmo.exporter || (wijmo.exporter = {}));
    var exporter = wijmo.exporter;
})(wijmo || (wijmo = {}));
});
