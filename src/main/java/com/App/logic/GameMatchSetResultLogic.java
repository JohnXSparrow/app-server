package com.App.logic;

import org.springframework.stereotype.Service;

import com.App.model.GameMatch;
import com.App.model.Result;

@Service
public class GameMatchSetResultLogic {
	
	public void teamWin(GameMatch gameMatch, Result result) {

		// Gols Feitos e Gols Sofridos Time que joga em Casa
		gameMatch.getTeamA().setGoalScored(result.getGoalsTeamA() + gameMatch.getTeamA().getGoalScored());
		gameMatch.getTeamA().setGoalSuffered(result.getGoalsTeamB() + gameMatch.getTeamA().getGoalSuffered());

		// Gols Feitos e Gols Sofridos Time que joga Fora
		gameMatch.getTeamB().setGoalScored(result.getGoalsTeamB() + gameMatch.getTeamB().getGoalScored());
		gameMatch.getTeamB().setGoalSuffered(result.getGoalsTeamA() + gameMatch.getTeamB().getGoalSuffered());

		// Adciona mais uma partida em Partidas Jogadas
		gameMatch.getTeamA().setMatchesPlayed(gameMatch.getTeamA().getMatchesPlayed() + 1);
		gameMatch.getTeamB().setMatchesPlayed(gameMatch.getTeamB().getMatchesPlayed() + 1);

		if (result.getGoalsTeamA() > result.getGoalsTeamB()) {

			gameMatch.getTeamA().setWinHome(gameMatch.getTeamA().getWinHome() + 1);
			gameMatch.getTeamB().setLostOut(gameMatch.getTeamB().getLostOut() + 1);

			result.setTeamWin(gameMatch.getTeamA());

		} else if (result.getGoalsTeamA() < result.getGoalsTeamB()) {

			gameMatch.getTeamB().setWinOut(gameMatch.getTeamB().getWinOut() + 1);
			gameMatch.getTeamA().setLostHome(gameMatch.getTeamA().getLostHome() + 1);

			result.setTeamWin(gameMatch.getTeamB());

		} else if (result.getGoalsTeamA() == result.getGoalsTeamB()) {
			gameMatch.getTeamA().setTieHome(gameMatch.getTeamA().getTieHome() + 1);
			gameMatch.getTeamB().setTieOut(gameMatch.getTeamB().getTieOut() + 1);
			result.setTeamWin(null);
		}
		
		gameMatch.setIsClosed(true);
		gameMatch.setIsKickDisabled(true);

	}

}
