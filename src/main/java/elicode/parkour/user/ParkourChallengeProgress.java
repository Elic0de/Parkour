package elicode.parkour.user;

public class ParkourChallengeProgress {

	private int currentCheckAreanumber = 1;

	public ParkourChallengeProgress(){

	}

	public ParkourChallengeProgress(int currentCheckAreaNumber){
		this.currentCheckAreanumber = currentCheckAreaNumber;
	}

	public int currentCheckAreaNumber(){
		return currentCheckAreanumber;
	}

	public void incrementCurrentCheckAreaNumber(){
		currentCheckAreanumber++;
	}

}
