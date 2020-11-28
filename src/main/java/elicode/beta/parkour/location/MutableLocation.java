package elicode.beta.parkour.location;

import org.bukkit.World;

public class MutableLocation implements Location {

	public static ImmutableLocation deserialize(String text){
		return Location.deserialize(text).deserializeTo(MutableLocation.class);
	}

	public World world;
	public double x, y, z;
	public float yaw, pitch;

	public MutableLocation(World world, double x, double y, double z, float yaw, float pitch){
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	public MutableLocation(World world, double x, double y, double z){
		this(world, x, y, z, 0f, 0f);
	}

	public MutableLocation(org.bukkit.Location location){
		this(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getZ() {
		return z;
	}

	@Override
	public float getYaw() {
		return yaw;
	}

	@Override
	public float getPitch() {
		return pitch;
	}

	@Override
	@SuppressWarnings("unchecked")
	public MutableLocation add(double x, double y, double z, float yaw, float pitch) {
		return new MutableLocation(world, x + this.x, y + this.y, z + this.z, yaw + this.yaw, pitch + this.pitch);
	}

	@Override
	@SuppressWarnings("unchecked")
	public MutableLocation relative(double x, double y, double z, float yaw, float pitch) {
		return new MutableLocation(world, x - this.x, y - this.y, z - this.z, yaw - this.yaw, pitch - this.pitch);
	}

	public ImmutableLocation asImmutable(){
		return new ImmutableLocation(world, x, y, z, yaw, pitch);
	}

}
