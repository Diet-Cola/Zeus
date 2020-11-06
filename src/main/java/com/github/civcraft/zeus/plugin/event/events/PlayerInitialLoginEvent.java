package com.github.civcraft.zeus.plugin.event.events;

import java.net.InetAddress;
import java.util.UUID;

import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.plugin.event.CancellableEvent;
import com.github.civcraft.zeus.servers.ArtemisServer;

/**
 * Called when a player is first connecting to a Bungee server from outside
 *
 */
public class PlayerInitialLoginEvent extends CancellableEvent {

	private UUID player;
	private InetAddress ip;
	private String denyMessage;
	private ZeusLocation location;
	private ArtemisServer intendedTargetServer;

	public PlayerInitialLoginEvent(UUID player, InetAddress ip, ArtemisServer server, ZeusLocation location) {
		this.player = player;
		this.ip = ip;
		this.intendedTargetServer = server;
		this.location = location;
	}

	/**
	 * @return Location of the player
	 */
	public ZeusLocation getPlayerLocation() {
		return location;
	}

	/**
	 * @return Minecraft server the player will be sent to. May be null if no target
	 *         could be determined, if that is still the case after the event, the
	 *         player will be rejected
	 */
	public ArtemisServer getTargetServer() {
		return intendedTargetServer;
	}
	
	/**
	 * Sets the location the player will be sent to
	 * @param target Server to send the player to
	 * @param location Location to send the player to
	 */
	public void setTarget(ArtemisServer target, ZeusLocation location) {
		this.location = location;
		this.intendedTargetServer = target;
	}

	/**
	 * @return Player attempting to login
	 */
	public UUID getPlayer() {
		return player;
	}

	/**
	 * @return IP the player is connecting from, can be either v4 or v6
	 */
	public InetAddress getPlayerIP() {
		return ip;
	}

	/**
	 * @return Message to show the player if his login is denied
	 */
	public String getDenyMessage() {
		return denyMessage;
	}

	/**
	 * Sets the message shown to the player if his login is denied and sets the
	 * login attempt to be cancelled
	 * 
	 * @param denyMessage Message to show
	 */
	public void setDenyMessage(String denyMessage) {
		this.denyMessage = denyMessage;
		setCancelled(true);
	}

}
