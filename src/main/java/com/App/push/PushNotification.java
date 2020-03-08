package com.App.push;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.App.model.GameMatch;

@Service
public class PushNotification {
	
	private URL url;
	private HttpURLConnection con;
	private OutputStream outputStream;

	public void sendPushOponent(String username, String oponent, GameMatch gameMatch) {		
		String strJsonBody = "{\r\n" + 
				"\"app_id\": \"app id\",\r\n" + 
				"\"filters\": [{\"field\": \"tag\", \"key\": \"username\", \"relation\": \"=\", \"value\":" + "\"" + oponent + "\"" + "}],\r\n" + 
				"\"headings\": {\"en\":" +  "\"" + gameMatch.getTeamA().getNameTeam() + " x " + gameMatch.getTeamB().getNameTeam() + "\"" + "},\r\n" + 
				"\"contents\": {\"en\":" +  "\"" + "Oponente Encontrado: " + username + "\"" + "}\r\n" + 
				"}";

		sendPushConstructor(strJsonBody);
	}
	
	public void sendPushChallenge(String sendTo, String friend, GameMatch gameMatch) {		
		String strJsonBody = "{\r\n" + 
				"\"app_id\": \"app id\",\r\n" + 
				"\"filters\": [{\"field\": \"tag\", \"key\": \"username\", \"relation\": \"=\", \"value\":" + "\"" + sendTo + "\"" + "}],\r\n" + 
				"\"headings\": {\"en\":" +  "\"" + friend + " te desafiou!" + "\"" + "},\r\n" + 
				"\"contents\": {\"en\":" +  "\"" + "Você foi desafiado para o jogo entre: " + gameMatch.getTeamA().getNameTeam() + " X " + gameMatch.getTeamB().getNameTeam() + "\"" + "}\r\n" + 
				"}";

		sendPushConstructor(strJsonBody);
	}
	
	public void sendPushChallengeAccepted(String sendTo, String friend, GameMatch gameMatch) {		
		String strJsonBody = "{\r\n" + 
				"\"app_id\": \"app id\",\r\n" + 
				"\"filters\": [{\"field\": \"tag\", \"key\": \"username\", \"relation\": \"=\", \"value\":" + "\"" + sendTo + "\"" + "}],\r\n" + 
				"\"headings\": {\"en\":" +  "\"" + friend + " aceitou seu desafio!" + "\"" + "},\r\n" + 
				"\"contents\": {\"en\":" +  "\"" + "O desafio foi aceito para o jogo entre: " + gameMatch.getTeamA().getNameTeam() + " X " + gameMatch.getTeamB().getNameTeam() + "\"" + "}\r\n" + 
				"}";

		sendPushConstructor(strJsonBody);
	}
	
	public void sendPushChallengeRefused(String sendTo, String friend, GameMatch gameMatch) {		
		String strJsonBody = "{\r\n" + 
				"\"app_id\": \"app id\",\r\n" + 
				"\"filters\": [{\"field\": \"tag\", \"key\": \"username\", \"relation\": \"=\", \"value\":" + "\"" + sendTo + "\"" + "}],\r\n" + 
				"\"headings\": {\"en\":" +  "\"" + friend + " não aceitou seu desafio!" + "\"" + "},\r\n" + 
				"\"contents\": {\"en\":" +  "\"" + "O desafio foi recusado para o jogo entre: " + gameMatch.getTeamA().getNameTeam() + " X " + gameMatch.getTeamB().getNameTeam() + "\"" + "}\r\n" + 
				"}";

		sendPushConstructor(strJsonBody);
	}
	
	public void sendPushKickFinalized(String username, GameMatch gameMatch) {		
		String strJsonBody = "{\r\n" + 
				"\"app_id\": \"app id\",\r\n" + 
				"\"filters\": [{\"field\": \"tag\", \"key\": \"username\", \"relation\": \"=\", \"value\":" + "\"" + username + "\"" + "}],\r\n" + 
				"\"headings\": {\"en\":" +  "\"" + gameMatch.getTeamA().getNameTeam() + " x " + gameMatch.getTeamB().getNameTeam() + "\"" + "},\r\n" + 
				"\"contents\": {\"en\":" +  "\"" + "A partida já tem um resultado. Veja quem ganhou!" + "\"" + "}\r\n" + 
				"}";

		sendPushConstructor(strJsonBody);
	}
	
	public void sendPushGameTopAdd(String teamA, String teamB) {		
		String strJsonBody = "{\r\n" + 
				"\"app_id\": \"app id\",\r\n" + 
				"\"included_segments\": [\"Subscribed Users\"]," + 
				"\"headings\": {\"en\":" +  "\"" + teamA + " x " + teamB + "\"" + "},\r\n" + 
				"\"contents\": {\"en\":" +  "\"" + "Jogo Top adicionado. Jogue Agora!" + "\"" + "}\r\n" + 
				"}";

		sendPushConstructor(strJsonBody);
	}

	@Async
	public void sendPushConstructor(String strJsonBody) {
		try {
			url = new URL("https://onesignal.com/api/v1/notifications");
			con = (HttpURLConnection) url.openConnection();
			con.setUseCaches(false);
			con.setDoOutput(true);
			con.setDoInput(true);

			con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			con.setRequestProperty("Authorization", "Basic token");
			con.setRequestMethod("POST");

			byte[] sendBytes = strJsonBody.getBytes("UTF-8");
			con.setFixedLengthStreamingMode(sendBytes.length);
			outputStream = con.getOutputStream();
			outputStream.write(sendBytes);
			con.getResponseCode();

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
