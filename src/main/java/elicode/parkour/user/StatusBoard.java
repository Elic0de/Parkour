package elicode.parkour.user;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import elicode.parkour.inventory.ui.LocaleFunction;
import elicode.parkour.parkour.RankColor;
import elicode.parkour.util.format.CoinFormat;
import elicode.parkour.util.format.CountFormat;
import elicode.parkour.util.joor.Reflect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;




import elicode.parkour.scoreboard.Scoreboard;
import elicode.parkour.util.text.Text;
import elicode.parkour.util.tuplet.Quadruple;

public class StatusBoard {

	private static class Line extends Quadruple<Function<StatusBoardSetting, Boolean>, Integer, LocaleFunction, Function<User, Object>> {

		public Line(Function<StatusBoardSetting, Boolean> displaySetting, Integer score, String japanise, String english, Function<User, Object> value) {
			super(displaySetting, score, new LocaleFunction(japanise, english), value);
		}

	}

	private static final List<Line> LINES;
	private static final Pattern DOUBLE_BYTE_CHARACTER_CHECKER = Pattern.compile("^[^!-~｡-ﾟ]+$");

	static{
		LINES = ImmutableList.of(
				new Line(s -> true, 10, "  ", "  ", u -> ""),
				new Line(s -> s.displayTraceur, 9, "&f-トレイサー: &f-$value", "&f-Traceur: &f-$value", u -> u.asBukkitPlayer().getName()),
				new Line(s -> s.displayUpdateRank, 8, "&f-Update: &a-$value", "&f-Update: &a-$value", u -> u.updateRank()),
				new Line(s -> s.displayExtendRank, 7, "&f-Extend: &a-$value", "&f-Extend: &a-$value", u -> u.extendRank()),
				new Line(s -> s.displayJumps, 6, "&f-ジャンプ数: &b-$value", "&f-Jumps: &b-$value", u -> CountFormat.format(u.asBukkitPlayer().getStatistic(Statistic.JUMP))),
				new Line(s -> s.displayTimePlayed, 5, "&f-プレイ時間: &a-$valueh", "&f-Time Played: &a-$valueh", u -> u.asBukkitPlayer().getStatistic(Statistic.PLAY_ONE_TICK) / 72000),
				new Line(s -> s.displayOnlinePlayers, 4, "&f-プレイヤー数&7-: &a-$value", "&f-Players: &a-$value", u -> Bukkit.getOnlinePlayers().size()),
				new Line(s -> s.displayPing, 3, "&f-遅延: &f-$valuems", "&f-Ping: &f-$valuems", u -> Reflect.on(u.asBukkitPlayer()).call("getHandle").get("ping")),
				new Line(s -> s.displayCoins, 2, "&f-コイン: &6-$value", "&f-Coins: &6-$value", u -> CoinFormat.format(u.coins())),
				new Line(s -> true, 1, "", "", u -> ""),
				new Line(s -> s.displayServerAddress, 0, "$value", "$value", u -> {
				Scoreboard board = u.statusBoard.board;

				int maxLength = 0;

				for(int score = 2; score <= 9; score++) if(board.hasScore(score)){
					String text = ChatColor.stripColor(board.getScore(score));

					double length = 0;

					//全角文字であれば2.5、そうでなければ1加算する
					for(char character : text.toCharArray()) length += DOUBLE_BYTE_CHARACTER_CHECKER.matcher(String.valueOf(character)).matches() ? 2.5 : 1;

					maxLength = Math.max((int) length, maxLength);
				}

				//azisaba.netの文字数分だけ引く
				maxLength = Math.max(maxLength - 11, 0);

				int halfMaxLength = maxLength / 2;

/*//				String spaces = "";
//				for(int i = 0; i < halfMaxLength; i++) spaces += " ";*/

				return Text.stream("&e-hatosaba.f5.si").color().toString();
			})
		);
	}

	private final User user;
	private Scoreboard board;

	public StatusBoard(User user){
		this.user = user;
	}

	public void loadScoreboard(){
		StatusBoardSetting setting = user.setting;

		//スコアボードを表示しない設定であれば戻る
		if(!setting.displayScoreboard){
			//スコアボードが表示されていれば非表示にする
			if(board != null && board.isDisplay()) board.setDisplay(false);

			return;
		}

		//スコアボードを新しく作成する
		board = new Scoreboard(user.asBukkitPlayer(), Text.stream("&b-Rise &f-Network").color().toString());

		for(Line line : LINES){
			//表示しない設定であれば処理しない
			if(!line.first.apply(setting)) continue;

			//表示するテキストを作成する
			String text = Text.stream(line.third.apply(user.asBukkitPlayer()))
					.setAttribute("$value", line.fourth.apply(user))
					.color()
					.toString();

			//対応したスコアにテキストをセットする
			board.setScore(line.second, text);
		}

		board.setDisplay(true);
	}

	public void clearScoreboard(){
		if(board == null) return;

		board.setDisplay(false);

		board = null;
	}

	public void updateAll(){
		for(int score = 0; score < LINES.size() - 1; score++) updateValue(score, false);
	}

	public void updateUpdateRank(){
		updateValue(2);
	}

	public void updateExtendRank(){
		updateValue(3);
	}

	public void updateJumps(){
		updateValue(4);
	}

	public void updateCoins(){
		updateValue(8);
	}

	public void updateTimePlayed(){
		updateValue(5);
	}

	public void updateOnlinePlayers(){
		updateValue(6);
	}

	public void updatePing(){
		updateValue(7);
	}

	private void updateValue(int score){
		updateValue(score, false);
	}

	private void updateValue(int score, boolean whetherToUpdate){
		if(board == null) return;

		Line line = LINES.get(score);

		StatusBoardSetting setting = user.setting;

		//表示しない設定であれば戻る
		if(!line.first.apply(setting)) return;

		//現在表示されている文字列を取得する
		String before = board.getScore(score);

		Player player = user.asBukkitPlayer();

		//表示する文字列を作成する
		String after = Text.stream(line.third.apply(player))
				.setAttribute("$value", line.fourth.apply(user))
				.color()
				.toString();

		//指定されたスコアをアップデートする
		board.updateScore(line.second, after);

		//サーバーアドレスの表示を更新しないのであれば戻る
		if(!whetherToUpdate) return;

		//サーバーアドレス行のコンポーネントを取得する
		Line serverAddress = LINES.get(11);

		//サーバーアドレスを表示しない又は文字列長に差が無い場合は戻る
		if(!serverAddress.first.apply(setting) || (before != null && before.length() == after.length())) return;

		String serverAddressForDisplay = Text.stream(serverAddress.third.apply(player))
				.setAttribute("$value", serverAddress.fourth.apply(user))
				.color()
				.toString();

		board.updateScore(serverAddress.second, serverAddressForDisplay);
	}

}
