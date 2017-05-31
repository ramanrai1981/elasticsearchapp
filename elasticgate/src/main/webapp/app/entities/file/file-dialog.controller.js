(function() {
    'use strict';

    angular
        .module('elasticgateApp')
        .controller('FileDialogController', FileDialogController);

    FileDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'File'];

    function FileDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, File) {
        var vm = this;

        vm.file = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.file.id !== null) {
                File.update(vm.file, onSaveSuccess, onSaveError);
            } else {
                File.save(vm.file, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('elasticgateApp:fileUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.uploadDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
