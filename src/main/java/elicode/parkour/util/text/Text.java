package elicode.parkour.util.text;

import elicode.parkour.util.message.MessageStyle;
import org.bukkit.entity.Player;

public class Text implements TextStream {

	private static final String COLORS = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
	private static final String NULL = String.valueOf(Character.MIN_VALUE);

	private String text;

	public Text(String text){
		this.text = text;
	}

	public static Text stream(String text){
		return new Text(text);
	}

	@Override
	public TextStream color(char alternateColorCode) {
		//文字列を1文字ずつに分解する
		char[] characters = text.toCharArray();

		//各文字に対して処理をする
		for(int i = 0; i < characters.length - 1; i++){
			char color = characters[i + 1];

			//装飾コードでなければ戻る
			if(characters[i] != alternateColorCode || COLORS.indexOf(color) <= -1) continue;

			if(i > 0 && characters[i - 1] == '-') characters[i - 1] = Character.MIN_VALUE;

			characters[i] = '§';
			characters[i + 1] = Character.toLowerCase(color);

			if(i < characters.length - 2 && characters[i + 2] == '-'){
				characters[i + 2] = Character.MIN_VALUE;
				i += 2;
			}else{
				i++;
			}
		}

		text = new String(characters).replace(NULL, "");

		return this;
	}

	@Override
	public TextStream setAttribute(String name, Object value) {
		text = text.replace(name, value.toString());
		return this;
	}

	@Override
	public TextStream setAttribute(String name, MessageStyle messageStyle) {
		text = text.replace(name, messageStyle.toString());
		return this;
	}

	@Override
	public Text textBy(Player sender) {
		return this;
	}

	@Override
	public String toString(){
		return text;
	}

}
