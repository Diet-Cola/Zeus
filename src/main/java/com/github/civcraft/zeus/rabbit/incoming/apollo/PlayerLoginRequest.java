package com.github.civcraft.zeus.rabbit.incoming.apollo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import org.json.JSONObject;

import com.github.civcraft.zeus.ZeusMain;
import com.github.civcraft.zeus.model.ZeusLocation;
import com.github.civcraft.zeus.plugin.event.events.PlayerInitialLoginEvent;
import com.github.civcraft.zeus.rabbit.incoming.InteractiveRabbitCommand;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.ConfirmInitialPlayerLogin;
import com.github.civcraft.zeus.rabbit.outgoing.artemis.RejectPlayerInitialLogin;
import com.github.civcraft.zeus.rabbit.sessions.ZeusPlayerLoginSession;
import com.github.civcraft.zeus.servers.ArtemisServer;
import com.github.civcraft.zeus.servers.ConnectedServer;

public class PlayerLoginRequest extends InteractiveRabbitCommand<ZeusPlayerLoginSession> {

	public static final String ID = "initial_login_request";

	@Override
	public boolean handleRequest(ZeusPlayerLoginSession connState, ConnectedServer sendingServer, JSONObject data) {
		ZeusLocation location = ZeusMain.getInstance().getDAO().getLocation(connState.getPlayer());
		ArtemisServer target = ZeusMain.getInstance().getServerPlacementManager().getTargetServer(location);
		PlayerInitialLoginEvent loginEvent = new PlayerInitialLoginEvent(connState.getPlayer(), connState.getIP(),
				target, location);
		ZeusMain.getInstance().getEventManager().broadcast(loginEvent);
		if (loginEvent.isCancelled()) {
			String msg;
			if (loginEvent.getDenyMessage() != null) {
				msg = loginEvent.getDenyMessage();
			}
			else {
				msg = "Login denied";
			}
			sendReply(connState.getServerID(),
					new RejectPlayerInitialLogin(connState.getTransactionID(), msg));
			return false;
		}
		if (target == null) {
			sendReply(connState.getServerID(),
					new RejectPlayerInitialLogin(connState.getTransactionID(), "No target found"));
			return false;
		}
		sendReply(connState.getServerID(), new ConfirmInitialPlayerLogin(connState.getTransactionID(), target.getID()));
		return false;
	}

	@Override
	public String getIdentifier() {
		return ID;
	}

	@Override
	public boolean createSession() {
		return true;
	}

	@Override
	protected ZeusPlayerLoginSession getFreshSession(ConnectedServer source, String transactionID, JSONObject data) {
		UUID player = UUID.fromString(data.getString("player"));
		InetAddress ip;
		try {
			ip = InetAddress.getByName(data.getString("ip"));
		} catch (UnknownHostException e) {
			ZeusMain.getInstance().getLogger().error("Could not parse ip", e);
			ip = null;
		}
		return new ZeusPlayerLoginSession(source, transactionID, player, ip);
	}

}
