package elicode.parkour.parkour;

import elicode.beta.parkour.location.ImmutableLocation;
import elicode.parkour.region.LocationOnBorderCollector;
import elicode.parkour.region.Region;
import elicode.parkour.region.selection.RegionSelection;
import elicode.parkour.schedule.Async;
import elicode.parkour.util.Color;
import elicode.parkour.util.joor.Reflect;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;


import java.util.List;
import java.util.stream.Collectors;

public class ParkourRegion extends Region {

	//この領域のあるアスレ(飽く迄プレイヤーのコネクションを取得する為の存在)
	public final Parkour parkour;

	//この領域の中央の座標
	public final ImmutableLocation center;

	//各地点のパーティクル
	private List<Object> packets;

	//パーティクルの表示位置
	private int position;

	//パーティクルパケットを送信する非同期のループタスク
	private BukkitTask task;

	public ParkourRegion(Parkour parkour, RegionSelection selection){
		this(parkour, selection.getLesserBoundaryCorner(), selection.getGreaterBoundaryCorner());
	}

	public ParkourRegion(Parkour parkour, Region region){
		this(parkour, region.lesserBoundaryCorner, region.greaterBoundaryCorner);
	}

	public ParkourRegion(Parkour parkour, ImmutableLocation lesserBoundaryCorner, ImmutableLocation greaterBoundaryCorner){
		super(lesserBoundaryCorner, greaterBoundaryCorner);
		this.parkour = parkour;
		this.center = new ImmutableLocation(lesserBoundaryCorner.world, (lesserBoundaryCorner.x + greaterBoundaryCorner.x) / 2, lesserBoundaryCorner.y, (lesserBoundaryCorner.z + greaterBoundaryCorner.z) / 2);

		recolorParticles2();
	}
	public void recolorParticles(){
		boolean running = task != null;

		if(running) undisplayBorders();

		List<ImmutableLocation> locations = LocationOnBorderCollector.collect(this, 4);

		Color color = parkour.borderColor;

		//各座標に対応したパーティクルパケットを作成する
		packets = locations.stream()
				.map(location -> {
					float red = color.adjustRed(30) / 255f;
					float green = color.adjustGreen(30) / 255f;
					float blue = color.adjustBlue(30) / 255f;

					Object particle = Reflect.onClass("ParticleParamRedstone").create(red, green, blue, 1).get();
					return Reflect.onClass("PacketPlayOutWorldParticles").create(particle, true,
							(float) location.x, (float) location.y + 0.15f, (float) location.z,
							red, green, blue, 1, 0).get();
				})
				.collect(Collectors.toList());

		position = 0;

		if(running) displayBorders();
	}

	public void recolorParticles2(){
		boolean running = task != null;

		if(running) undisplayBorders();

		List<ImmutableLocation> locations = LocationOnBorderCollector.collect(this, 4);

		Color color = parkour.borderColor;

		//各座標に対応したパーティクルパケットを作成する
		packets = locations.stream()
				.map(location -> {
					float red = color.adjustRed(30) / 255f;
					float green = color.adjustGreen(30) / 255f;
					float blue = color.adjustBlue(30) / 255f;

					return new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true,
							(float) location.x, (float) location.y + 0.15f, (float) location.z,
							red, green, blue, 1, 0);
				})
				.collect(Collectors.toList());

		position = 0;

		if(running) displayBorders();
	}

	//境界線を表示する
	public void displayBorders(){
		//既にタスクが存在していれば戻る
		if(task != null) return;

		//コネクションリストが空であれば戻る
		if(parkour.connections.isEmpty())
			return;

		final int size = packets.size();
		final int halfSize = size / 2;
		final int lastIndex = size - 1;

		//非同期で実行する
		task = Async.define(() -> {
			if(position >= size) position = 0;

			//各ポジションに対応したパケットを取得する
			Object packet1 = packets.get(position);
			Object packet2 = packets.get(position < halfSize ? position + halfSize : position + halfSize - lastIndex);

			position++;

			for(Reflect connection : parkour.connections.getConnections()){
				EntityPlayer player = connection.get("player");

				//if(player.clientViewDistanc == null) continue;
				//プレイヤーの描画距離を取得する
				//int viewChunks = 0;
				//player.clientViewDistance.intValue() - 2
				//プレイヤーとエリア中央のチャンク距離
				double xDistance = (int) Math.abs(center.x - player.locX) >> 4;
				double zDistance = (int) Math.abs(center.z - player.locZ) >> 4;
				//描画範囲外であれば処理しない
				//if(xDistance > viewChunks || zDistance > viewChunks) continue;
				connection.call("sendPacket", packet1);
				connection.call("sendPacket", packet2);
			}
		}).executeTimer(0, 1);
	}

	//境界線を非表示にする
	public void undisplayBorders(){
		if(task == null) return;

		task.cancel();
		task = null;
	}

}
