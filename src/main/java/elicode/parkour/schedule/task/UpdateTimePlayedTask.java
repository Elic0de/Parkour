package elicode.parkour.schedule.task;

import elicode.parkour.user.UserSet;

public class UpdateTimePlayedTask extends AsyncTask {

	public UpdateTimePlayedTask() {
		super(900);
	}

	@Override
	public void run() {
		UserSet.getInstnace().getOnlineUsers().forEach(user -> user.statusBoard.updateTimePlayed());
	}

}
