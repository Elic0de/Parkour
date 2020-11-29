package elicode.parkour.region.selection;

import org.bukkit.World;

import elicode.location.ImmutableLocation;
import elicode.location.Location;
import elicode.location.MutableLocation;
import elicode.parkour.region.Region;
import elicode.parkour.util.text.Text;

public class RegionSelection {

	public final MutableLocation boundaryCorner1 = new MutableLocation(null, 0, 0, 0);
	public final MutableLocation boundaryCorner2 = new MutableLocation(null, 0, 0, 0);

	public void setWorld(World world){
		boundaryCorner1.world = boundaryCorner2.world = world;
	}

	public void setBoundaryCorner1(Location location){
		setBoundaryCorner1(location.getWorld(), location.getIntX(), location.getIntY(), location.getIntZ());
	}

	public void setBoundaryCorner1(org.bukkit.Location location){
		setBoundaryCorner1(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public void setBoundaryCorner1(World world, int x, int y, int z){
		boundaryCorner1.world = world;
		boundaryCorner1.x = x;
		boundaryCorner1.y = y;
		boundaryCorner1.z = z;
	}

	public void setBoundaryCorner2(Location location){
		setBoundaryCorner2(location.getWorld(), location.getIntX(), location.getIntY(), location.getIntZ());
	}

	public void setBoundaryCorner2(org.bukkit.Location location){
		setBoundaryCorner2(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	public void setBoundaryCorner2(World world, int x, int y, int z){
		boundaryCorner2.world = world;
		boundaryCorner2.x = x;
		boundaryCorner2.y = y;
		boundaryCorner2.z = z;
	}

	public World getWorld(){
		return boundaryCorner1.world;
	}

	public ImmutableLocation getLesserBoundaryCorner(){
		return new ImmutableLocation(
			boundaryCorner1.world,
			Math.min(boundaryCorner1.x, boundaryCorner2.x),
			Math.min(boundaryCorner1.y, boundaryCorner2.y),
			Math.min(boundaryCorner1.z, boundaryCorner2.z)
		);
	}

	public ImmutableLocation getGreaterBoundaryCorner(){
		return new ImmutableLocation(
			boundaryCorner1.world,
			Math.max(boundaryCorner1.x, boundaryCorner2.x),
			Math.max(boundaryCorner1.y, boundaryCorner2.y),
			Math.max(boundaryCorner1.z, boundaryCorner2.z)
		);
	}

	public Region makeRegion(){
		return new Region(getLesserBoundaryCorner(), getGreaterBoundaryCorner());
	}

	@Override
	public String toString(){
		World world = getWorld();

		ImmutableLocation lesserBoundaryCorner = getLesserBoundaryCorner();
		ImmutableLocation greaterBoundaryCorner = getGreaterBoundaryCorner();

		return Text.stream("$world,$lesser_x,$lesser_y,$lesser_z,$greater_x,$greater_y,$greater_z")
				.setAttribute("$world", world != null ? world.getName() : "null")
				.setAttribute("$lesser_x", lesserBoundaryCorner.getIntX())
				.setAttribute("$lesser_y", lesserBoundaryCorner.getIntY())
				.setAttribute("$lesser_z", lesserBoundaryCorner.getIntZ())
				.setAttribute("$greater_x", greaterBoundaryCorner.getIntX())
				.setAttribute("$greater_y", greaterBoundaryCorner.getIntY())
				.setAttribute("$greater_z", greaterBoundaryCorner.getIntZ())
				.toString();
	}

}
