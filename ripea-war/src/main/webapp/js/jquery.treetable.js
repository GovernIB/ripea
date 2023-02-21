+ function($) {
    'use strict';

    /**
     * Store reference to plugin with same name.
     */
    var old = $.fn.treeTable;


    /**
     * Public API constructor.
     * Usage: $( selector ).treeTable({ ... })
     */
    function Plugin(options) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data('treetable');

            if (!data) {
                $this.data(
                    'treetable',
                    new TreeTable(
                        this,
                        $.extend(
                            true,
                            $.fn.treeTable.defaults,
                            typeof options == 'object' ? options : {})
                    ));
            }
        });
    }

    /**
     * API Constructor. Takes in an element selector and an options
     * object and converts the table to be rendered as a tree.
     */
    var TreeTable = function(element, options) {
        // Reference to each nodes depth, starts with 0
        this.depths = {};
        // Reference to count of children nodes for each node
        this.children = {};
        // Extended options
        this.options = options;
        this.$table = $(element);
        this.build(this.$table.find('tr[data-node^="treetable"]'));
    }

    /**
     * Turns the table into a tree, with expand/collapse buttons.
     * This runs in the following steps:
     *   1) Attach event handlers to the toggle buttons
     *   2) Add depth class to each row
     *   3) Insert expand/collapse buttons for rows with children
     *      amd mark initial state (expanded or collapsed)
     */
    TreeTable.prototype.build = function(nodes) {

        this.addDepth(nodes);
        this.addExpanders(nodes);
        this.attachEvents();
    };

    /**
     * Iterates over the nodes and adds a CSS class and data attribute
     * for the depth of the node in the tree.
     */
    TreeTable.prototype.addDepth = function(nodes) {
        var self = this;

        nodes.each(function(idx, node) {
            var $node = $(node);
            var nodeId = $node.data('node');
            var pnodeId = $node.data('pnode');
            var depth = (pnodeId && pnodeId in self.depths) ?
                self.depths[pnodeId] + 1 :
                0;

            // Add a counter to the children if this has a parent
            if (pnodeId) {
                self.children[pnodeId]++;
            }

            self.children[nodeId] = 0;
            $node.data('depth', depth);
            self.depths[nodeId] = depth;
            $node.addClass('treetable-depth-' + depth);
        });
    };

    /**
     * Renders expander buttons to each row with children.
     */
    TreeTable.prototype.addExpanders = function(nodes) {
        var self = this;

        nodes.each(function(idx, node) {
            var $node = $(node);
            var nodeId = $node.data('node');

            if (self.children[nodeId] > 0) {
                $('<span class="treetable-expander"></span>')
                    .prependTo($node.find('td').get(1))
                    .addClass((self.options.startCollapsed) ?
                        self.options.collapsedClass :
                        self.options.expandedClass);
                $node.addClass((self.options.startCollapsed) ?
                    'treetable-collapsed' :
                    'treetable-expanded');

                // If the node is to start collapsed, collapse all
                // of this node's children.
                if (self.options.startCollapsed) {
                    self.$table.find('tr[data-pnode="' + nodeId + '"]').hide();
                }
            }
        });
    };

    /**
     * Attaches an event handler to the table for catching all clicks
     * to the expander buttons.
     */
    TreeTable.prototype.attachEvents = function() {
        var self = this;
        
        this.$table.find('tbody > :is(tr.treetable-collapsed, tr.treetable-expanded) > td:not(:last-child)').css('cursor', 'pointer').click(function() {
            var $this = $(this);
            self.toggle($this.closest('tr'));
        });
    };




    TreeTable.prototype.hide = function(nodeId) {

        var self = this;

        let children = this.$table.find('tr[data-pnode="' + nodeId + '"]');

        if (children.length) {
            children.each(function(i, e) {
                self.hide($(e).data("node"));
            });

            this.$table.find('tr[data-pnode="' + nodeId + '"]')
                .addClass('treetable-collapsed')
                .removeClass('treetable-expanded')
                .hide();
            this.$table.find('tr[data-pnode="' + nodeId + '"] .treetable-expander')
                .removeClass(this.options.expandedClass)
                .addClass(this.options.collapsedClass);

        }
    };



    TreeTable.prototype.toggle = function($node) {


        let $expander = $node.find('.treetable-expander');
        var nodeId = $node.data('node');

        $expander.toggleClass(this.options.expandedClass);
        $expander.toggleClass(this.options.collapsedClass);
        $node.toggleClass('treetable-collapsed').toggleClass('treetable-expanded');

        if ($node.hasClass('treetable-collapsed')) { //if collapsing
            // Hide all descendant nodes and toggle the state of
            // any expander in the descendants.

            this.hide(nodeId);

        } else { //if expanding
            // Just show the immediate children
            this.$table.find('tr[data-pnode="' + nodeId + '"]').show();
        }
    };



    $.fn.expandAll = function() {

        $(this).find('tr[data-node]')
            .addClass('treetable-expanded')
            .removeClass('treetable-collapsed')
            .show();
        $(this).find('tr[data-node] .treetable-expander')
            .removeClass($(this).data('treetable').options.collapsedClass)
            .addClass($(this).data('treetable').options.expandedClass);


    }

    $.fn.collapseAll = function() {

        $(this).find('tr[data-node]:not(.treetable-depth-0)').hide();
        $(this).find('tr[data-node]')
            .addClass('treetable-collapsed')
            .removeClass('treetable-expanded');
        $(this).find('tr[data-node] .treetable-expander')
            .removeClass($(this).data('treetable').options.expandedClass)
            .addClass($(this).data('treetable').options.collapsedClass);
    }


    $.fn.treeTable = Plugin;
    $.fn.treeTable.defaults = {
        treeColumn: 0,
        startCollapsed: false,
        expandedClass: 'fa fa-angle-down',
        collapsedClass: 'fa fa-angle-right'
    };

    $.fn.treeTable.noConflict = function() {
        $.fn.treeTable = old;
        return this;
    }
}(jQuery);