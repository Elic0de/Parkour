package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.region.selection.RegionSelectionSet;
import elicode.parkour.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class EditCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();
    private final RegionSelectionSet selections = RegionSelectionSet.getInstance();

    public EditCommand(final Main plugin) {
        super(plugin, "edit", "edit [name]", "範囲選択ツール", 2, true);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        final String parkourName = ChatColor.translateAlternateColorCodes('&', args[1]);
        final Player player = (Player) sender;
        final Parkour parkour = ParkourSet.getInstance().getParkour(parkourName);

        if (parkour == null) {
            sender.sendMessage("mitukarimasenn");
            return;
        }

        //送信者をプレイヤーとして取得する
        UUID uuid = player.getUniqueId();

        //finishと入力された場合は範囲選択を終了する
        if(parkourName.equals("finish")){
            //範囲選択中でなければ戻る
            if(!selections.hasSelection(uuid)){
                //sender.warn("範囲選択中ではありません。");
                return;
            }

            Inventory inventory = player.getInventory();

            //インベントリ内から範囲選択ツールを削除する
            for(ItemStack item : inventory.getContents()) if(selections.isSelectionTool(item))
                inventory.remove(item);

            //セレクションをクリアする
            selections.clearSelection(player);

            //sender.info("範囲選択を終了しました。");
            return;
        }

        //アスレが存在しなければ戻る
        if(!ParkourSet.getInstance().containsParkour(parkourName)){
            Text.stream("$parkour-&r-は存在しません。")
                    .setAttribute("$parkour", parkourName)
                    .color()
                    .setReceiver(player)
                    .sendChatMessage();
            return;
        }

        //新しいセレクションを作成する
        selections.setNewSelection(uuid, parkourName);

        //対応した範囲選択ツールを作成する
        ItemStack selectionTool = selections.makeNewSelectionTool(uuid);

        player.getInventory().addItem(selectionTool);

        Text.stream("$parkour-&r-用の範囲選択ツールを与えました。")
                .setAttribute("$parkour", parkourName)
                .color()
                .setReceiver(player)
                .sendChatMessage();
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 2) {
            return handleTabCompletion(args[1], parkours.getParkours().stream().map(Parkour::getColorAndParkourName).collect(Collectors.toList()));
        }

        return null;
    }
}
