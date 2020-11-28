package elicode.parkour.schedule.task;

import elicode.parkour.user.UserSet;

public class UpdatePingTask extends AsyncTask {

	public UpdatePingTask() {
		super(0);
	}

	@Override
	public void run() {
		UserSet.getInstnace().getOnlineUsers().forEach(user -> user.statusBoard.updatePing());
	}

}
