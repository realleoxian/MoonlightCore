package de.leoxian.moonlightcore.common.server.permission;

import de.leoxian.moonlightcore.common.platform.XplatAbstraction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

@ApiStatus.NonExtendable
public interface PermissionsHelper {
	/// @return The permission helper
	static PermissionsHelper get() {
		return XplatAbstraction.INSTANCE.getPermissionHelper();
	}

	/// Registers a new permission
	/// @param id The identifier of the permission
	/// @param permissionResolver The permission resolver
	void registerPermission(Identifier id, Function<PermissionContext, Boolean> permissionResolver);

	/// Check whether the player has the requested permission
	/// @param player The player
	/// @param id The permission's identifier
	/// @return Whether the player has the requested permission
	boolean hasPermission(ServerPlayer player, Identifier id);

	/// Check whether the requested permission its present
	/// @param commandSourceStack The command source stack
	/// @param id The permission's identifier
	/// @return Whether the player has the requested permission
	boolean hasPermission(CommandSourceStack commandSourceStack, Identifier id);
}
