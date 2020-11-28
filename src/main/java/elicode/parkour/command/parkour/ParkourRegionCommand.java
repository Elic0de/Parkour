package elicode.parkour.command.parkour;

import java.util.UUID;

import elicode.beta.parkour.location.ImmutableLocation;
import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourRegion;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.region.Region;
import elicode.parkour.region.selection.RegionSelection;
import elicode.parkour.region.selection.RegionSelectionSet;
import elicode.parkour.util.text.Text;

public class ParkourRegionCommand implements Command {

	private final ParkourSet parkours = ParkourSet.getInstance();
	private final RegionSelectionSet selections = RegionSelectionSet.getInstance();

	@Override
	public void onCommand(Sender sender, Arguments args) {
		//送信者がプレイヤーでなければ戻る
		if(blockNonPlayer(sender)) return;

		if(hasPermission(sender,this.getClass().getSimpleName())) return;

		//第1引数が無ければ戻る
		if(!args.hasNext()){
			displayCommandUsage(sender);
			return;
		}

		//送信者のUUIDを取得する
		UUID uuid = sender.asPlayerCommandSender().getUniqueId();

		if(!selections.hasSelection(uuid)){
			sender.warn("範囲選択をして下さい。");
			return;
		}

		//対象となるアスレの名前を取得する
		String parkourName = selections.getSelectedParkourName(uuid);

		//アスレが存在しなければ戻る
		if(!parkours.containsParkour(parkourName)){
			sender.warn("指定されたアスレは存在しません。");
			return;
		}

		Parkour parkour = parkours.getParkour(parkourName);

		//セレクションを取得する
		RegionSelection selection = selections.getSelection(uuid);
		ImmutableLocation lesserBoundaryCorner = selection.getLesserBoundaryCorner();
		ImmutableLocation greaterBoundaryCorner = selection.getGreaterBoundaryCorner();

		String regionName = null;

		switch(args.next()){
		case "region":{
			parkour.update(it -> it.region = new Region(lesserBoundaryCorner, greaterBoundaryCorner));
			parkour.update(it -> it.arena = new ParkourRegion(parkour, lesserBoundaryCorner, greaterBoundaryCorner));
			regionName = "領域";
			break;
		}case "startline":{
			parkour.update(it -> it.startLine = new ParkourRegion(parkour, lesserBoundaryCorner, greaterBoundaryCorner));
			regionName = "スタートライン";
			break;
		}case "finishline":{
			parkour.update(it -> it.finishLine = new ParkourRegion(parkour, lesserBoundaryCorner, greaterBoundaryCorner));
			regionName = "フィニッシュライン";
			break;
		}case "portal":{
				parkour.update(it -> it.portal = new ParkourRegion(parkour, lesserBoundaryCorner, greaterBoundaryCorner));
				regionName = "ポータル";
				break;
			}default:
			displayCommandUsage(sender);
			return;
		}

		Text.stream("$parkour-&r-の$regionを$selectionに設定しました。")
		.setAttribute("$parkour", parkourName)
		.setAttribute("$region", regionName)
		.setAttribute("$selection", selection)
		.color()
		.setReceiver(sender.asPlayerCommandSender())
		.sendChatMessage();
	}

	private void displayCommandUsage(Sender sender){
		sender.warn("/parkourregion region @ アスレの領域を設定する");
		sender.warn("/parkourregion startline @ アスレのスタートラインを設定する");
		sender.warn("/parkourregion finishline @ アスレのフィニッシュラインを設定する");
		sender.warn("/parkourregion portal @ アスレのポータルを設定する");
	}

}
