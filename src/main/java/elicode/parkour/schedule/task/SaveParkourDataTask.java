package elicode.parkour.schedule.task;

import elicode.parkour.parkour.ParkourSet;

public class SaveParkourDataTask extends AsyncTask {

	public SaveParkourDataTask() {
		//30分毎にセーブする
		super(18000);
	}

	@Override
	public void run() {
		System.out.println("Save to Parkour Data");
		ParkourSet.getInstance().saveAll();
	}

}
