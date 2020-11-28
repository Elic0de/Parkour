package elicode.parkour.schedule.task;

import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;

public class SaveUserDataTask extends AsyncTask {

	/*
	 * 処理見直し必要あり
	 * User#changedを作る？
	 */

	private final UserSet users = UserSet.getInstnace();
	private int count;

	public SaveUserDataTask() {
		//12分毎にオンラインプレイヤー、1時間毎に全プレイヤーのデータをセーブする
		super(36000);
	}

	@Override
	public void run() {
		if(count++ >= 4){
			count = 0;
			System.out.println("Save to Online Player Data");
			users.saveAll();
		}else{
			System.out.println("Save to All player data");
			users.getOnlineUsers().forEach(User::save);
		}

	}

}
