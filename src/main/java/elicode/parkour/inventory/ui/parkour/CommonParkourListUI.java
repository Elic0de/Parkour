package elicode.parkour.inventory.ui.parkour;

import java.util.Comparator;
import java.util.stream.Collectors;

import elicode.parkour.inventory.ui.InventoryLine;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.user.User;

public class CommonParkourListUI extends AbstractParkourListUI<Parkour> {

	public CommonParkourListUI(User user, ParkourCategory category,boolean duel) {
		super(
			user,
			category,
			() -> ParkourSet.getInstance().getEnabledParkours(category)
					.sorted(Comparator.comparingInt(parkour -> parkour.difficulty.getDifficulty(0)))
					.collect(Collectors.toList()),
			parkours -> InventoryLine.necessaryInventoryLine(54),
			layout -> {},
				duel

		);
	}

}
