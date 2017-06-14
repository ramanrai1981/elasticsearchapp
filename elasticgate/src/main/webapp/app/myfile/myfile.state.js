(function() {
    'use strict';

    angular
        .module('elasticgateApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('myfile', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/myfile/myfile.html',
                    controller: 'MyfileController',
                    controllerAs: 'vm'
                }
            },

            file: function () {
                return {
                    fileNo: null,
                    title: null,
                    tag: null,
                    uploadDate: null,
                    status: null,
                    priority: false,
                    id: null
                };
            },

            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('myfile');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
