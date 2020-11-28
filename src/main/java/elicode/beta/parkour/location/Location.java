package elicode.beta.parkour.location;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import elicode.beta.parkour.serialize.Deserializer;
import elicode.beta.parkour.serialize.Serializer;

public interface Location {

	public static Deserializer deserialize(String text){
		return Deserializer.stream(text)
				.map(Bukkit::getWorld, World.class, 0)
				.map(Double::parseDouble, double.class, 1, 2, 3)
				.map(Float::parseFloat, float.class, 4, 5);
	}

	World getWorld();

	default Chunk getChunk(){
		return getWorld().getChunkAt(getIntX(), getIntZ());
	}

	double getX();

	double getY();

	double getZ();

	float getYaw();

	float getPitch();

	default int getIntX(){
		return (int) getX();
	}

	default int getIntY(){
		return (int) getY();
	}

	default int getIntZ(){
		return (int) getZ();
	}

	default boolean isAt(int x, int y, int z){
		return x == getIntX() && y == getIntY() && z == getIntZ();
	}

	default boolean isAt(double x, double y, double z){
		return x == getX() && y == getY() && z == getZ();
	}

	<T extends Location> T add(double x, double y, double z, float yaw, float pitch);

	default <T extends Location> T add(int x, int y, int z, float yaw, float pitch){
		return add((double) x, (double) y, (double) z, yaw, pitch);
	}

	default <T extends Location> T add(double x, double y, double z){
		return add(x, y, z, 0f, 0f);
	}

	default <T extends Location> T add(int x, int y, int z){
		return add(x, y, z, 0f, 0f);
	}

	default <T extends Location> T add(Location location){
		return add(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	default <T extends Location> T sub(double x, double y, double z, float yaw, float pitch){
		return add(-x, -y, -z, -yaw, -pitch);
	}

	default <T extends Location> T sub(int x, int y, int z, float yaw, float pitch){
		return sub((double) x, (double) y, (double) z, yaw, pitch);
	}

	default <T extends Location> T sub(double x, double y, double z){
		return sub(x, y, z, 0f, 0f);
	}

	default <T extends Location> T sub(int x, int y, int z){
		return sub(x, y, z, 0f, 0f);
	}

	default <T extends Location> T sub(Location location){
		return sub(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	<T extends Location> T relative(double x, double y, double z, float yaw, float pitch);

	default <T extends Location> T relative(int x, int y, int z, float yaw, float pitch){
		return relative((double) x, (double) y, (double) z, yaw, pitch);
	}

	default <T extends Location> T relative(int x, int y, int z){
		return relative(x, y, z, 0f, 0f);
	}

	default <T extends Location> T relative(Location location){
		return relative(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	default org.bukkit.Location asBukkit(){
		return new org.bukkit.Location(getWorld(), getX(), getY(), getZ(), getYaw(), getPitch());
	}

	default String serialize(){
		return Serializer.serialize(getWorld().getName(), getX(), getY(), getZ(), getYaw(), getPitch());
	}

}
