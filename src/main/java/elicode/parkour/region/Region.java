package elicode.parkour.region;

import org.bukkit.Bukkit;
import org.bukkit.World;

import elicode.beta.parkour.location.ImmutableLocation;
import elicode.beta.parkour.location.Location;
import elicode.parkour.util.text.Text;

public class Region {

	public final World world;
	public final ImmutableLocation lesserBoundaryCorner, greaterBoundaryCorner;

	public static Region deserialize(String data){
		String[] coordinates = data.split(",");
		World world = Bukkit.getWorld(coordinates[0]);
		ImmutableLocation lesserBoundaryCorner = new ImmutableLocation(world, Double.parseDouble(coordinates[1]), Double.parseDouble(coordinates[2]), Double.parseDouble(coordinates[3]));
		ImmutableLocation greaterBoundaryCorner = new ImmutableLocation(world, Double.parseDouble(coordinates[4]), Double.parseDouble(coordinates[5]), Double.parseDouble(coordinates[6]));
		return new Region(lesserBoundaryCorner, greaterBoundaryCorner);
	}

	public Region(Location lesserBoundaryCorner, Location greaterBoundaryCorner){
		this(lesserBoundaryCorner.getWorld(), lesserBoundaryCorner.getIntX(), lesserBoundaryCorner.getIntY(), lesserBoundaryCorner.getIntZ(),
				greaterBoundaryCorner.getIntX(), greaterBoundaryCorner.getIntY(), greaterBoundaryCorner.getIntZ());
	}

	public Region(World world, double lesserBoundaryCornerX, double lesserBoundaryCornerY, double lesserBoundaryCornerZ,
					double greaterBoundaryCornerX, double greaterBoundaryCornerY, double greaterBoundaryCornerZ){

		this.world = world;

		lesserBoundaryCorner = new ImmutableLocation(world,
				Math.min(lesserBoundaryCornerX, greaterBoundaryCornerX),
				Math.min(lesserBoundaryCornerY, greaterBoundaryCornerY),
				Math.min(lesserBoundaryCornerZ, greaterBoundaryCornerZ)
		);

		greaterBoundaryCorner = new ImmutableLocation(world,
				Math.max(lesserBoundaryCornerX, greaterBoundaryCornerX),
				Math.max(lesserBoundaryCornerY, greaterBoundaryCornerY),
				Math.max(lesserBoundaryCornerZ, greaterBoundaryCornerZ)
		);
	}

	public boolean isIn(Location location){
		return isIn(location.getWorld(), location.getIntX(), location.getIntY(), location.getIntZ());
	}

	public boolean isIn(org.bukkit.Location location){
		return isIn(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public boolean isIn(World world, int x, int y, int z){
		return lesserBoundaryCorner.x <= x && x <= greaterBoundaryCorner.x
				&& lesserBoundaryCorner.y <= y && y <= greaterBoundaryCorner.y
				&& lesserBoundaryCorner.z <= z && z <= greaterBoundaryCorner.z
				&& world.equals(world);
	}

	public boolean isIn(World world, double x, double y, double z){
		return lesserBoundaryCorner.x <= x && x <= greaterBoundaryCorner.x
				&& lesserBoundaryCorner.y <= y && y <= greaterBoundaryCorner.y
				&& lesserBoundaryCorner.z <= z && z <= greaterBoundaryCorner.z
				&& world.equals(world);
	}

	public int getLength(){
		return greaterBoundaryCorner.getIntX() - lesserBoundaryCorner.getIntX();
	}

	public int getHeight(){
		return greaterBoundaryCorner.getIntY() - lesserBoundaryCorner.getIntY();
	}

	public int getWidth(){
		return greaterBoundaryCorner.getIntZ() - lesserBoundaryCorner.getIntZ();
	}

	public int getArea(){
		return getLength() * getWidth();
	}

	public int getVolume(){
		return getArea() * getHeight();
	}

	public Region extend(int x, int y, int z){
		return new Region(lesserBoundaryCorner.add(Math.min(x, 0), Math.min(y, 0), Math.min(z, 0)), greaterBoundaryCorner.add(Math.max(x, 0), Math.max(y, 0), Math.max(z, 0)));
	}

	public Region add(int x, int y, int z){
		return new Region(lesserBoundaryCorner.add(x, y, z), greaterBoundaryCorner.add(x, y, z));
	}

	public Region add(Location location){
		return add(location.getIntX(), location.getIntY(), location.getIntZ());
	}

	public Region sub(int x, int y, int z){
		return new Region(lesserBoundaryCorner.sub(x, y, z), greaterBoundaryCorner.sub(x, y, z));
	}

	public Region sub(Location location){
		return sub(location.getIntX(), location.getIntY(), location.getIntZ());
	}

	public Region relative(int x, int y, int z){
		return new Region(world, lesserBoundaryCorner.x - x, lesserBoundaryCorner.y - y, lesserBoundaryCorner.z - z, greaterBoundaryCorner.x - x, greaterBoundaryCorner.y - y, greaterBoundaryCorner.z - z);
	}

	public Region relative(Location location){
		return relative(location.getIntX(), location.getIntY(), location.getIntZ());
	}

	public String serialize(){
		return Text.stream("$world,$lesser_x,$lesser_y,$lesser_z,$greater_x,$greater_y,$greater_z")
				.setAttribute("$world", world.getName())
				.setAttribute("$lesser_x", lesserBoundaryCorner.x)
				.setAttribute("$lesser_y", lesserBoundaryCorner.y)
				.setAttribute("$lesser_z", lesserBoundaryCorner.z)
				.setAttribute("$greater_x", greaterBoundaryCorner.x)
				.setAttribute("$greater_y", greaterBoundaryCorner.y)
				.setAttribute("$greater_z", greaterBoundaryCorner.z)
				.toString();
	}

}
