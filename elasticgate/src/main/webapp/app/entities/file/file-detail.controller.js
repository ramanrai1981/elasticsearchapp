(function() {
    'use strict';

    angular
        .module('elasticgateApp')
        .controller('FileDetailController', FileDetailController);

    FileDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'File'];

    function FileDetailController($scope, $rootScope, $stateParams, previousState, entity, File) {
        var vm = this;

        vm.file = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('elasticgateApp:fileUpdate', function(event, result) {
            vm.file = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
