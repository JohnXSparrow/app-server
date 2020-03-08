package com.App.enumeration;

public enum ProfilePlanEnum {

	Basico     (0, "Basico",     1,   0,     0,  50,  50,   "0.00"), // id 0, name, winForKick, goldCoin, silverCoin, winPer, aTiePer, value
	Armador    (1, "Armador",    1, 100,  5000, 100,  50,  "29.99"), // id 1, name, winForKick, goldCoin, silverCoin, winPer, aTiePer, value
	Atacante   (2, "Atacante",   2, 100,  5000,  50,  50,  "49.99"), // id 2, name, winForKick, goldCoin, silverCoin, winPer, aTiePer, value
	Artilheiro (3, "Artilheiro", 2, 300,  5000, 100,  50,  "99.99"), // id 3, name, winForKick, goldCoin, silverCoin, winPer, aTiePer, value
	Campeao    (4, "Campeão",    3, 500, 10000, 100, 100, "199.99"), // id 4, name, winForKick, goldCoin, silverCoin, winPer, aTiePer, value
	
	MoedaOuro  (5, "100 Moedas Ouro",    1, 100,    0, 50, 50, "14,99"), // id 5, name, winForKick, goldCoin, silverCoin, winPer, aTiePer, value
	MoedaPrata (6, "5.000 Moedas Prata", 1,   0, 5000, 50, 50, "14,99"); // id 6, name, winForKick, goldCoin, silverCoin, winPer, aTiePer, value

	private final int id;
	private final String namePlan;
	private final int winForKick;
	private final long goldCoin;
	private final long silverCoin;
	private final String price;
	
	private final int winPer;
	private final int aTiePer;	
	
	private ProfilePlanEnum(int id, String namePlan, int winForKick, long goldCoin, long silverCoin, int winPer, int aTiePer, String price) {
		this.id = id;
		this.namePlan = namePlan;
		this.winForKick = winForKick;
		this.goldCoin = goldCoin;
		this.silverCoin = silverCoin;
		this.price = price;	
		this.winPer = winPer;
		this.aTiePer = aTiePer;
	}
	
	public static ProfilePlanEnum getProfilePlanById(int id) {
		if (id == 0) {
			return ProfilePlanEnum.Basico;
		} else if (id == 1) {
			return ProfilePlanEnum.Armador;
		} else if (id == 2) {
			return ProfilePlanEnum.Atacante;
		} else if (id == 3) {
			return ProfilePlanEnum.Artilheiro;
		} else if (id == 4) {
			return ProfilePlanEnum.Campeao;
		} else {
			return null;
		}
	}

	public int getId() {
		return id;
	}
	
	public String getNamePlan() {
		return namePlan;
	}

	public int getWinForKick() {
		return winForKick;
	}

	public long getGoldCoin() {
		return goldCoin;
	}

	public long getSilverCoin() {
		return silverCoin;
	}

	public String getPrice() {
		return price;
	}

	public int getWinPer() {
		return winPer;
	}

	public int getATiePer() {
		return aTiePer;
	}
	
	/*
	 * Campeão    = preço: 199,99 3 vitorias por chute; 100% vitoria; 100% empate; 500 ouro e 10000 prata - sobra caixa 104,99
	 * Artilheiro = preço: 99,99; 2 vitorias por chute; 100% vitoria;  50% empate; 300 ouro e  5000 prata - sobra caixa  49,99
	 * Atacante   = preço: 49,99; 2 vitorias por chute;  50% vitoria;  50% empate; 100 ouro e  5000 prata - sobra caixa  24,99
	 * Armador    = preço: 29,99; 1 vitorias por chute; 100% vitoria;  50% empate; 100 ouro e  5000 prata - sobra caixa  10,99
	 * Basico     = preço:  0,00; 1 vitorias por chute;  50% vitoria;  50% empate;   0 ouro e     0 prata - sobra caixa   0,00
	 * MoedaOuro  = preço: 14,99; 1 vitorias por chute;  50% vitoria;  50% empate; 100 ouro e     0 prata - sobra caixa   5,49
	 * MoedaPrata = preço: 14,99; 1 vitorias por chute;  50% vitoria;  50% empate;   0 ouro e  5000 prata - sobra caixa   5,49
	 * 
	 * - Os plano são referentes ao ano do campeonato brasileiro, as contas voltam para o plano Basic ao final de cada ano;
	 * - Podem ser comprados varios planos, os planos menores não sobrepoem aos planos maiores.
	 * 
	 */
	
}
