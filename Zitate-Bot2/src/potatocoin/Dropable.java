package potatocoin;

import discord.UserInformation;

public abstract class Dropable {
	Rarity rarity;
	
	public abstract void drop(UserInformation ui, String UserId);
	
	public void setRarity(Rarity rarity) {
		this.rarity = rarity;
	}
	
	public Rarity getRarity() {
		return rarity;
	}
	public enum Rarity{
		gaulitastisch(1.0, -88), toxymoxy(0.95,-40), sonervigeinfach(0.80,-10), uuitoll(0.60,1), geringverdienend(0.25,10), wild(0.05,40), marximal(0.01,69);
		
		double percent, value;
		
		Rarity(double percent, double value){
			this.percent = percent;
			this.value = value;
		}
		
		public double getPercent() {
			return percent;
		}
	}
}
//jakob du hurensohn