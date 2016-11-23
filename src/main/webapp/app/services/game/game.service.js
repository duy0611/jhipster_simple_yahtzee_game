(function() {
    'use strict';

    angular
        .module('simpleYahtzeeApp')
        .factory('GameService', ProfileService);

    ProfileService.$inject = ['$http', 'AlertService'];

    function ProfileService($http, AlertService) {
    	
        var service = {
            getGameByUUID : getGameByUUID,
            findRandomNewGame: findRandomNewGame,
            createNewGame: createNewGame,
            joinGame: joinGame,
            startGame: startGame,
            moveNext: moveNext,
            finishTurn: finishTurn
        };

        return service;

        function getGameByUUID(uuid) {
        	return $http.get('api/games/' + uuid).then(function(result) {
        		return result;
        	}, function(error) {
        		AlertService.error(error.statusText);
        	});
        }
        
        function findRandomNewGame() {
        	return $http.get('api/games/getRandomNewGame').then(function(result) {
        		return result;
        	}, function(error) {
        		AlertService.error(error.statusText);
        	});
        }
        
        function createNewGame() {
        	return $http.post('api/games').then(function(result) {
        		return result;
        	}, function(error) {
        		AlertService.error(error.statusText);
        	});
        }
        
        function joinGame(uuid) {
        	return $http.post('api/games/' + uuid + '/join').then(function(result) {
        		return result;
        	}, function(error) {
        		AlertService.error(error.statusText);
        	});
        }
        
        function startGame(uuid) {
        	return $http.post('api/games/' + uuid + '/start').then(function(result) {
        		return result;
        	}, function(error) {
        		AlertService.error(error.statusText);
        	});
        }
        
        function moveNext(uuid) {
        	return $http.post('api/games/' + uuid + '/moveNext').then(function(result) {
        		return result;
        	}, function(error) {
        		AlertService.error(error.statusText);
        	});
        }
        
        function finishTurn(uuid, selectedCategory) {
        	return $http.post('api/games/' + uuid + '/finishTurn?selectedCategory=' + selectedCategory).then(function(result) {
        		return result;
        	}, function(error) {
        		AlertService.error(error.statusText);
        	});
        }
    }
})();
