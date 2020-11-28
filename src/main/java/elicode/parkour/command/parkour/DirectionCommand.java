package elicode.parkour.command.parkour;

import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import elicode.parkour.util.text.BilingualText;
import elicode.parkour.util.text.Text;

public class DirectionCommand implements Command {

	@Override
	public void onCommand(Sender sender, Arguments args) {
		if(blockNonPlayer(sender))
			return;
		if(hasPermission(sender,this.getClass().getSimpleName())) return;

		//プレイヤーとして取得する
		Player player = sender.asPlayerCommandSender();

		//プレイヤーの今いる座標を取得する
		Location location = player.getLocation();

		if(args.hasNextFloat()){
			//第1引数をyawとして取得する
			float yaw = args.nextFloat();

			//第2引数が存在しない或いはfloat型の値ではない場合は警告しつつ戻る
			if(!args.hasNextFloat()){
				displayCommandUsage(player);
				return;
			}

			//第2引数をpitchとして取得する
			float pitch = args.nextFloat();

			adjustAndSetYaw(location, yaw);
			adjustAndSetPitch(location, pitch);

			BilingualText.stream("&b-ヨーを$yawに、ピッチを$pitchに設定しました", "&b-Set your yaw to $yaw and pitch $pitch")
			.setAttribute("$yaw", yaw)
			.setAttribute("$pitch", pitch)
			.color()
			.setReceiver(player)
			.sendActionBarMessage();
		}else{
			switch(args.next()){
			case "yaw":
				if(!args.hasNextFloat()){
					Text.stream("&c-/direction yaw [yaw]")
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
					return;
				}

				float yaw = args.nextFloat();

				adjustAndSetYaw(location, yaw);

				BilingualText.stream("&b-ヨーを$yawに設定しました", "&b-Set your yaw to $yaw")
				.setAttribute("$yaw", yaw)
				.color()
				.setReceiver(player)
				.sendActionBarMessage();
				break;
			case "pitch":
				if(!args.hasNextFloat()){
					Text.stream("&c-/direction pitch [pitch]")
					.color()
					.setReceiver(player)
					.sendActionBarMessage();
					return;
				}

				float pitch = args.nextFloat();

				adjustAndSetPitch(location, pitch);

				BilingualText.stream("&b-ピッチを$pitchに設定しました", "&b-Set your yaw to $pitch")
				.setAttribute("$pitch", pitch)
				.color()
				.setReceiver(player)
				.sendActionBarMessage();
				break;
			default:
				displayCommandUsage(player);
				return;
			}
		}

		//yawとpitchを適用する
		player.teleport(location, TeleportCause.COMMAND);
	}

	private void displayCommandUsage(Player player){
		Text.stream("&c-/direction [yaw] [pitch] &7-| &c-/direction yaw [yaw] &7-| &c-/direction pitch [pitch]")
		.color()
		.setReceiver(player)
		.sendActionBarMessage();
	}

	private void adjustAndSetYaw(Location location, float yaw){
		location.setYaw(Math.max(Math.min(yaw, 179.9f), -180.0f));
	}

	private void adjustAndSetPitch(Location location, float pitch){
		location.setPitch(Math.max(Math.min(pitch, 90.0f), -90.0f));
	}

}
