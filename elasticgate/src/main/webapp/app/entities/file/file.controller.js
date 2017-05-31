(function() {
    'use strict';

    angular
        .module('elasticgateApp')
        .controller('FileController', FileController);

    FileController.$inject = ['File', 'FileSearch'];

    function FileController(File, FileSearch) {

        var vm = this;

        vm.files = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            File.query(function(result) {
                vm.files = result;
                vm.searchQuery = null;
            });
        }

        function search() {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            FileSearch.query({query: vm.searchQuery}, function(result) {
                vm.files = result;
                vm.currentSearch = vm.searchQuery;
            });
        }

        function clear() {
            vm.searchQuery = null;
            loadAll();
        }    }
})();
