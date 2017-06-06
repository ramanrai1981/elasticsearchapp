(function() {
    'use strict';

    angular
        .module('elasticgateApp')
        .controller('MyfileController', MyfileController);

    MyfileController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'File', 'FileSearch'];

    function MyfileController ($scope, Principal, LoginService, $state, File, FileSearch) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.files = [];
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function register () {
            $state.go('register');
        }


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
        }

    }
})();
