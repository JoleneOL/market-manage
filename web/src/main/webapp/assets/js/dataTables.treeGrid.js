/**
 * @summary     TreeGrid
 * @description TreeGrid extension for DataTable
 * @version     1.0.0
 * @file dataTables.treeGrid.js
 * @author homfen(homfen@outlook.com)
 */
(function (factory) {
    if (typeof define === 'function' && define.amd) {
        // AMD
        define(['jquery', 'datatables.net'], function ($) {
            return factory($, window, document);
        });
    }
    else if (typeof exports === 'object') {
        // CommonJS
        module.exports = function (root, $) {
            if (!root) {
                root = window;
            }

            if (!$ || !$.fn.dataTable) {
                $ = require('datatables.net')(root, $).$;
            }

            return factory($, root, root.document);
        };
    }
    else {
        // Browser
        factory(jQuery, window, document);
    }
}(function ($, window, document) {
    'use strict';
    var DataTable = $.fn.dataTable;

    var TreeGrid = function (dt, init) {
        var that = this;

        if (!(this instanceof TreeGrid)) {
            alert('TreeGrid warning: TreeGrid must be initialised with the "new" keyword.');
            return;
        }

        if (init === undefined || init === true) {
            init = {};
        }

        var dtSettings = new $.fn.dataTable.Api(dt).settings()[0];

        this.s = {
            dt: dtSettings
        };

        if (dtSettings._oTreeGrid) {
            throw 'TreeGrid already initialised on this table';
        }

        dtSettings._oTreeGrid = this;

        if (!dtSettings._bInitComplete) {
            dtSettings.oApi._fnCallbackReg(dtSettings, 'aoInitComplete', function () {
                that.fnConstruct(init);
            }, 'TreeGrid');
        }
        else {
            this.fnConstruct(init);
        }
    };

    $.extend(TreeGrid.prototype, {
        fnConstruct: function (oInit) {
            this.s = $.extend(true, this.s, TreeGrid.defaults, oInit);

            var settings = this.s.dt;
            var select = settings._select;
            var dataTable = $(settings.nTable).dataTable().api();
            var sLeft = this.s.left;
            var treeGridRows = {};
            var expandIcon = $(this.s.expandIcon);
            var collapseIcon = $(this.s.collapseIcon);

            var resetTreeGridRows = function (index) {
                var indexes = [];
                if (index) {
                    indexes.push(index);
                }
                else {
                    for (var prop in treeGridRows) {
                        if (treeGridRows.hasOwnProperty(prop)) {
                            indexes.push(prop);
                        }
                    }
                }
                indexes.forEach(function (index) {
                    var subRows = treeGridRows[index];
                    if (subRows && subRows.length) {
                        subRows.forEach(function (node) {
                            dataTable.row($(node)).remove();
                            $(node).remove();
                        });
                        delete treeGridRows[index];
                        $(dataTable.row(index).node()).find('.treegrid-control-open').each(function (i, td) {
                            $(td).removeClass('treegrid-control-open').addClass('treegrid-control');
                            $(td).html('').append(expandIcon.clone());
                        });
                    }
                });
            };

            var resetEvenOddClass = function (dataTable) {
                var classes = ['odd', 'even'];
                $(dataTable.table().body()).find('tr').each(function (index, tr) {
                    $(tr).removeClass('odd even').addClass(classes[index % 2]);
                });
            };

            // Expand TreeGrid
            dataTable.on('click', 'td.treegrid-control', function (e) {
                if (!$(this).html()) {
                    return;
                }

                // record selected indexes
                var selectedIndexes = [];
                select && (selectedIndexes = dataTable.rows({selected: true}).indexes().toArray());

                var row = dataTable.row(this);
                var index = row.index();
                var data = row.data();

                var td = $(dataTable.cell(this).node());
                var paddingLeft = parseInt(td.css('padding-left'), 10);
                var layer = parseInt(td.find('span').css('margin-left') || 0, 10) / sLeft;
                var icon = collapseIcon.clone();
                icon.css('marginLeft', layer * sLeft + 'px');
                td.removeClass('treegrid-control').addClass('treegrid-control-open');
                td.html('').append(icon);

                if (data.children && data.children.length) {
                    var subRows = treeGridRows[index] = [];
                    var prevRow = row.node();
                    data.children.forEach(function (item) {
                        var newRow = dataTable.row.add(item);
                        var node = newRow.node();
                        var treegridTd = $(node).find('.treegrid-control');
                        var left = (layer + 1) * sLeft;
                        $(node).attr('parent-index', index);
                        treegridTd.find('span').css('marginLeft', left + 'px');
                        treegridTd.next().css('paddingLeft', paddingLeft + left + 'px');
                        $(node).insertAfter(prevRow);
                        prevRow = node;
                        subRows.push(node);
                    });

                    resetEvenOddClass(dataTable);
                    select && setTimeout(function () {
                        dataTable.rows(selectedIndexes).select();
                    }, 0);
                }
            });

            // Collapse TreeGrid
            dataTable.on('click', 'td.treegrid-control-open', function (e) {
                var selectedIndexes = [];
                select && (selectedIndexes = dataTable.rows({selected: true}).indexes().toArray());

                var td = $(dataTable.cell(this).node());
                var layer = parseInt(td.find('span').css('margin-left') || 0, 10) / sLeft;
                var icon = expandIcon.clone();
                icon.css('marginLeft', layer * sLeft + 'px');
                td.removeClass('treegrid-control-open').addClass('treegrid-control');
                td.html('').append(icon);

                var index = dataTable.row(this).index();
                resetTreeGridRows(index);
                resetEvenOddClass(dataTable);
                select && setTimeout(function () {
                    dataTable.rows(selectedIndexes).select();
                }, 0);
            });

            // resetTreeGridRows on pagination
            dataTable.on('page', function () {
                resetTreeGridRows();
            });

            // resetTreeGridRows on sorting
            dataTable.on('order', function () {
                resetTreeGridRows();
            });

            var inProgress = false;
            // Check parents and children on select
            select && select.style === 'multi' && dataTable.on('select', function (e, dt, type, indexes) {
                if (inProgress) {
                    return;
                }
                inProgress = true;
                indexes.forEach(function (index) {
                    // Check parents
                    selectParent(dataTable, index);

                    // Check children
                    selectChildren(dataTable, index);
                });
                inProgress = false;
            });

            // Check parents and children on deselect
            select && select.style === 'multi' && dataTable.on('deselect', function (e, dt, type, indexes) {
                if (inProgress) {
                    return;
                }
                inProgress = true;
                indexes.forEach(function (index) {
                    // Check parents
                    deselectParent(dataTable, index);

                    // Check children
                    deselectChildren(dataTable, index);
                });
                inProgress = false;
            });
        }
    });

    function selectParent(dataTable, index) {
        var row = dataTable.row(index);
        var parentIndex = $(row.node()).attr('parent-index');
        if (parentIndex != null) {
            parentIndex = +parentIndex;
            var selector = '[parent-index="' + parentIndex + '"]';
            var allChildRows = dataTable.rows(selector).nodes();
            var selectedChildRows = dataTable.rows(selector, {selected: true}).nodes();
            if (allChildRows.length === selectedChildRows.length) {
                var parentRow = dataTable.row(parentIndex, {selected: false});
                parentRow.select();
                if (parentRow.node()) {
                    selectParent(dataTable, parentIndex);
                }
            }
        }
    }

    function selectChildren(dataTable, index) {
        var rows = dataTable.rows('[parent-index="' + index + '"]', {selected: false});
        var childIndexes = rows.indexes().toArray();
        if (childIndexes.length) {
            rows.select();
            childIndexes.forEach(function (childIndex) {
                selectChildren(dataTable, childIndex);
            });
        }
    }

    function deselectParent(dataTable, index) {
        var row = dataTable.row(index);
        var parentIndex = $(row.node()).attr('parent-index');
        if (parentIndex != null) {
            parentIndex = +parentIndex;
            var parentRow = dataTable.row(parentIndex, {selected: true});
            parentRow.deselect();
            if (parentRow.node()) {
                deselectParent(dataTable, parentIndex);
            }
        }
    }

    function deselectChildren(dataTable, index) {
        var rows = dataTable.rows('[parent-index="' + index + '"]', {selected: true});
        var childIndexes = rows.indexes().toArray();
        if (childIndexes.length) {
            rows.deselect();
            childIndexes.forEach(function (childIndex) {
                deselectChildren(dataTable, childIndex);
            });
        }
    }

    TreeGrid.defaults = {
        left: 12,
        expandIcon: '<span>+</span>',
        collapseIcon: '<span>-</span>'
    };

    TreeGrid.version = '1.0.0';

    DataTable.Api.register('treeGrid()', function () {
        return this;
    });

    $(document).on('init.dt.treeGrid', function (e, settings) {
        if (e.namespace !== 'dt') {
            return;
        }

        var init = settings.oInit.treeGrid;
        var defaults = DataTable.defaults.treeGrid;

        if (init || defaults) {
            var opts = $.extend({}, init, defaults);

            if (init !== false) {
                new TreeGrid(settings, opts);
            }
        }
    });

    $.fn.dataTable.TreeGrid = TreeGrid;
    $.fn.DataTable.TreeGrid = TreeGrid;

    return TreeGrid;
}));
