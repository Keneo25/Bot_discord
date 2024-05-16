package org.commands;

import DiscordBot.Bot;
import com.github.kaktushose.jda.commands.annotations.interactions.*;
import com.github.kaktushose.jda.commands.dispatching.interactions.commands.CommandEvent;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.values.Values;

@Interaction
public class NormalCommands
{
	@SlashCommand(value = "punkty", desc = "test")
	public void onCommand(CommandEvent event) throws Exception
	{
		Member member = event.getMember();
		if (member == null)
			return;
		String memberId = member.getId();
		int points = Bot.DatabaseController.GetPoints(memberId);
		event.reply(String.format("Masz %d punktow!", points));
	}

	@SlashCommand(value = "przelej", desc = "przelewa punkty na konto innego uzytkownika")
	public void transferToAnotherUser(CommandEvent event, @Param("uzytkownik") Member memberToTransfer , @Param("ilosc punktow") int pointsToTransfer) throws Exception
	{
		if (event.getMember() == null || memberToTransfer == null)
			return;

		if (pointsToTransfer <= 0)
		{
			event.reply("Podaj prawidlowa ilosc punktow!");
			return;
		}

		int pointsOwner = Bot.DatabaseController.GetPoints(event.getMember().getId());
		if (pointsOwner < pointsToTransfer)
		{
			event.reply("Nie masz wystarczajaco punktow!");
			return;
		}

		Bot.DatabaseController.RemovePoints(event.getMember().getId(), pointsToTransfer);
		Bot.DatabaseController.AddPoints(memberToTransfer.getId(), pointsToTransfer);
		event.reply(String.format("Przelano %d punktow do %s", pointsToTransfer, memberToTransfer.getAsMention()));
	}

	@SlashCommand(value = "mute", desc = "wycisza uzytkownika na okreslona ilosc minut")
	public void muteSomeone(CommandEvent event, @Param("uzytkownik") Member memberToMute, @Param("ilosc minut") int minutes) throws Exception
	{

		if (event.getMember() == null || memberToMute == null)
			return;

		if (minutes <= 0)
		{
			event.reply("Podaj dobra wartosc minut!");
			return;
		}

		if (memberToMute.getVoiceState() == null)
		{
			event.reply("Podany uzytkownik nie znajduje sie na kanale glosowym");
			return;
		}

		if (memberToMute.getVoiceState().isGuildMuted())
		{
			// check
			event.reply("Uzytkownik jest juz zmutowany!");
			return;
		}

		// check if have points
		int pointsToDeduct = minutes * Values.Constants.POINTS_PER_MINUTE_TO_MUTE;
		int userPoints = Bot.DatabaseController.GetPoints(event.getMember().getId());
		if (userPoints < pointsToDeduct)
		{
			event.reply("Nie masz wystarczajacej ilosci punktow");
			return;
		}

		// deduct points
		Bot.DatabaseController.RemovePoints(event.getMember().getId(), pointsToDeduct);
		boolean isMuted = Bot.MuteController.AddUserToMutes(memberToMute.getId(), minutes);
		if (!isMuted){
			event.reply("Uzytkownik jest juz zmutowany");
			return;
		}
		memberToMute.mute(true).queue();
		event.reply(String.format("Pomyslnie zmutowano uzytkownika %s na %d minut", memberToMute.getAsMention(), minutes));
	}

	@SlashCommand(value = "ping", desc = "sprawdz ping")
	public void ping(CommandEvent event){
		event.reply(String.format("%d", event.getJDA().getGatewayPing()));
	}
}
