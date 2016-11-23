(function() {
    'use strict';

    angular
        .module('simpleYahtzeeApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state', 'GameService'];

    function HomeController ($scope, Principal, LoginService, $state, GameService) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
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
        
        $scope.startNewGame = function() {
        	GameService.createNewGame().then(function(result) {
        		$state.go('game', { 'uuid': result.data.uuid });
        	});
        }
        
        $scope.joinExistingGame = function() {    	
        	//find random game which has status "WAITING_FOR_PLAYER"
        	GameService.findRandomNewGame().then(function(result) {
        		GameService.joinGame(result.data.uuid).then(function(res) {
            		$state.go('game', { 'uuid': res.data.uuid });
            	});
        	});
        }
    }
})();
