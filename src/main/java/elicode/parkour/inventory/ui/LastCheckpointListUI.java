package elicode.parkour.inventory.ui;

import elicode.parkour.user.User;

public class LastCheckpointListUI extends AbstractCheckpointListUI {

	public LastCheckpointListUI(User user) {
		super(
			user,
			new LocaleFunction("最終", "Last"),
			(parkour, checkpoints) -> user.parkourChallengeProgress()
					.setPresentFunction(it -> checkpoints.getLastCheckpoint(parkour, it.currentCheckAreaNumber()))
					.setEmptyFunction(() -> checkpoints.getLastCheckpoint(parkour))
					.apply()
		);
	}

}
