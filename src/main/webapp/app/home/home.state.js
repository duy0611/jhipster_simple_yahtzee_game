(function() {
    'use strict';

    angular
        .module('simpleYahtzeeApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('home', {
            parent: 'app',
            url: '/',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeController',
                    controllerAs: 'vm'
                }
            }
        })
        .state('game', {
        	 parent: 'app',
             url: '/game/:uuid',
             data: {
                 authorities: ['ROLE_USER']
             },
             views: {
                 'content@': {
                     templateUrl: 'app/home/game.html',
                     controller: 'GameController',
                     controllerAs: 'vm'
                 }
             }
        });
    }
})();
