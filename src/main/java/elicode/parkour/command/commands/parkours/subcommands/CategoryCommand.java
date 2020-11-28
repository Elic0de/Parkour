package elicode.parkour.command.commands.parkours.subcommands;

import elicode.parkour.Main;
import elicode.parkour.command.BaseCommand;
import elicode.parkour.parkour.Parkour;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.util.text.Text;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryCommand extends BaseCommand {

    private ParkourSet parkours = ParkourSet.getInstance();

    public CategoryCommand(final Main plugin) {
        super(plugin, "category", "category [parkour] [category]", "カテゴリーを設定する", 3, true);
    }

    @Override
    protected void execute(CommandSender sender, String label, String[] args) {
        final String name = ChatColor.translateAlternateColorCodes('&', args[1]);
        final Player player = (Player) sender;
        final Parkour parkour = ParkourSet.getInstance().getParkour(name);

        if (parkour == null) {
            sender.sendMessage("mitukarimasenn");
            return;
        }

        try{
            final ParkourCategory category = ParkourCategory.valueOf(args[2].toUpperCase());

            //同じカテゴリーであれば戻る
            if(category == parkour.category){
                //sender.warn("既に同じカテゴリーに設定されています。");
                return;
            }

            //正しいカテゴリーであれば書き換える
            parkour.category = category;

            //DatabaseUtils.updateCourse(parkour);
        }catch(Exception e){
            //sender.warn("指定されたカテゴリーは不正です。[Normal, Update, Extend, Segment, Biome]から選んで下さい。");
            return;
        }
    }

    @Override
    public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
        if (args.length == 2) {
            return handleTabCompletion(args[1], parkours.getParkours().stream().map(Parkour::getColorAndParkourName).collect(Collectors.toList()));
        }

        if(args.length == 3) {
            return Arrays.asList("Update", "Extend", "Segment", "Biome", "Normal");
        }

        return null;
    }
}
