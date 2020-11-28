package elicode.parkour.command.game;

import elicode.parkour.Main;
import elicode.parkour.command.Arguments;
import elicode.parkour.command.Command;
import elicode.parkour.command.Sender;
import elicode.parkour.game.queue.Queue;
import elicode.parkour.game.queue.QueueManager;
import elicode.parkour.parkour.ParkourCategory;
import elicode.parkour.parkour.ParkourSet;
import elicode.parkour.user.User;
import elicode.parkour.user.UserSet;
import elicode.parkour.util.text.Text;
import org.bukkit.entity.Player;

public class PlayCommand implements Command {

    private final ParkourSet parkours = ParkourSet.getInstance();
    private final UserSet users = UserSet.getInstnace();
    private static final ParkourCategory[] COMMON_CATEGORIES = new ParkourCategory[]{ParkourCategory.NORMAL, ParkourCategory.SEGMENT, ParkourCategory.BIOME};
    private QueueManager queueManager = Main.getPlugin().getQueueManager();

    @Override
    public void onCommand(Sender sender, Arguments args) {
        //送信者がプレイヤーでなければ戻る
        if (blockNonPlayer(sender)) return;
        //第1引数が無ければ戻る
        if (!args.hasNext()) {

            displayCommandUsage(sender);

            return;
        }
        Player player = sender.asPlayerCommandSender();
        User user = users.getUser(player);

        String arg = args.next();
        if (arg.equals("ranked")) {
            if(queueManager.getQueue(player) == null) {
                Queue queue = new Queue(player,true);
                queue.addToQueue();
            }else {
                Queue soloQueue = queueManager.getQueue(player);
                soloQueue.removeFromQueue();
                queueManager.getPlayerQueue().remove(player);
                Queue queue = new Queue(player,true);
                queue.addToQueue();
            }

            return;

        }
        if (arg.equals("solo")) {
            if(queueManager.getQueue(player) == null) {
                Queue queue = new Queue(player,false);
                queue.addToQueue();
            }else {
                Queue soloQueue = queueManager.getQueue(player);
                soloQueue.removeFromQueue();
                queueManager.getPlayerQueue().remove(player);
                Queue queue = new Queue(player,false);
                queue.addToQueue();
            }

            return;

        }

    }

    private void displayCommandUsage(Sender sender) {

        Text.stream("&aRiht click with the compass in a lobby to select a game!").color().setReceiver(sender.asPlayerCommandSender()).sendChatMessage();
    }
}
