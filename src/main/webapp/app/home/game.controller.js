(function() {
    'use strict';

    angular
        .module('simpleYahtzeeApp')
        .controller('GameController', GameController);

    GameController.$inject = ['$scope', '$stateParams', 'GameService', 'Principal'];

    function GameController ($scope, $stateParams, GameService, Principal) {
        var vm = this;
        var uuid = $stateParams.uuid;
        
        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        
        $scope.game = {};
        $scope.yourScore = 0;
        $scope.opponentScore = 0;
        $scope.yourDices = [0, 0, 0, 0, 0];
        $scope.opponentDices = [0, 0, 0, 0, 0];
        
        $scope.isYourMove = false;
        $scope.canRollDice = false;
        $scope.canSelectCategory = false;
        
        $scope.yourSelectedCategories = [];
        
        GameService.getGameByUUID(uuid).then(function(result) {
        	$scope.game = result.data;
        	refreshGameData($scope.game);
        });
        
        function refreshGameData(entity) {
        	if(entity.playerTurn.playerName == vm.account.username) {
        		$scope.isYourMove = true;
        	}
        	else {
        		$scope.isYourMove = false;
        	}
        	
        	if(entity.gameStatus == 'STARTED') {
        		$scope.canRollDice = true;
        	}
        	else {
        		$scope.canRollDice = false;
        	}
        	
        	if(entity.gameStatus == 'WAIT_FOR_CATEGORY_SELECTED') {
        		$scope.canSelectCategory = true;
        	}
        	else {
        		$scope.canSelectCategory = false;
        	}
        	
        	if(entity.playerTurn.playerName == vm.account.username) {
        		$scope.yourDices = entity.currentDices;
        		
        		//reset dices if it is your turn to roll
        		if($scope.canRollDice) {
        			$scope.yourDices = [0, 0, 0, 0, 0];
        		}
        	}
        	else {
        		$scope.opponentDices = entity.currentDices;
        	}
        	
        	for (var key in entity.playerScores) {
        		//your score
        		if(key == ('Player name: ' + vm.account.username)) {
        			var yourScore = 0;
        			for (var obj in entity.playerScores[key]) {
        				if(Object.values(entity.playerScores[key][obj])[0] > -1) {
        					yourScore += Object.values(entity.playerScores[key][obj])[0];
        				}
        			}
        			$scope.yourScore = yourScore;
        		}
        		//opponent score
        		else {
        			var opponentScore = 0;
        			for (var obj in entity.playerScores[key]) {
        				if(Object.values(entity.playerScores[key][obj])[0] > -1) {
        					opponentScore += Object.values(entity.playerScores[key][obj])[0];
        				}
        			}
        			$scope.opponentScore = opponentScore;
        		}
        	}
        	
        	for (var key in entity.selectedCategories) {
        		//your selected categories
        		if(key == ('Player name: ' + vm.account.username)) {
        			$scope.yourSelectedCategories = entity.selectedCategories[key];
        			break;
        		}
        	}
        }
        
        $scope.startGame = function() {
        	GameService.startGame(uuid).then(function(result) {
        		$scope.game = result.data;
            	refreshGameData($scope.game);
        	});
        }
        
        $scope.moveNext = function() {
        	GameService.moveNext(uuid).then(function(result) {
        		$scope.game = result.data;
            	refreshGameData($scope.game);
        	});
        }
        
        $scope.finishTurn = function(selectedCategory) {
        	GameService.finishTurn(uuid, selectedCategory).then(function(result) {
        		$scope.game = result.data;
            	refreshGameData($scope.game);
        	});
        }
    }
})();
