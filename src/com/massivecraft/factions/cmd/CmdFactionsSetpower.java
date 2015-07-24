package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsPowerChange;
import com.massivecraft.factions.event.EventFactionsPowerChange.PowerChangeReason;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.cmd.arg.ARDouble;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsSetpower extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetpower()
	{
		// Aliases
		this.addAliases("sp", "setpower");
		
		// Args
		this.addArg(ARMPlayer.get(), "player");
		this.addArg(ARDouble.get(), "power");
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.SETPOWER.node));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		MPlayer mplayer = this.readArg();
		double power = this.readArg();
		
		// Power
		double oldPower = mplayer.getPower();
		double newPower = mplayer.getLimitedPower(power);
		
		// Detect "no change"
		double difference = Math.abs(newPower - oldPower);
		double maxDifference = 0.1d;
		if (difference < maxDifference)
		{
			msg("%s's <i>power is already <h>%.2f<i>.", mplayer.getDisplayName(msender), newPower);
			return;
		}

		// Event
		EventFactionsPowerChange event = new EventFactionsPowerChange(sender, mplayer, PowerChangeReason.COMMAND, newPower);
		event.run();
		if (event.isCancelled()) return;
		
		// Inform
		msg("<i>You changed %s's <i>power from <h>%.2f <i>to <h>%.2f<i>.", mplayer.getDisplayName(msender),  oldPower, newPower);
		if (msender != mplayer)
		{
			mplayer.msg("%s <i>changed your power from <h>%.2f <i>to <h>%.2f<i>.", msender.getDisplayName(mplayer), oldPower, newPower);
		}
		
		// Apply
		mplayer.setPower(newPower);
	}

}
