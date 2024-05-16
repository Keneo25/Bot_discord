package org.workers;

import DiscordBot.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import org.models.MuteClientModel;
import org.values.Values;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MuteController
{
	private static final List<MuteClientModel> _mutedClients = new CopyOnWriteArrayList<>();

	public void Run()
	{
		Guild guild = Bot.JDAController.getGuildById(Values.Constants.GUILD_ID);
		if (guild == null)
			return;


		for (MuteClientModel model : _mutedClients)
		{
			Member member = guild.getMemberById(model.Id);
			if (member == null)
				continue;

			GuildVoiceState voiceState = member.getVoiceState();
			if (voiceState == null)
				continue;

			// check if is on channel
			if (voiceState.isGuildMuted()){
				model.RemainingSeconds -= 1;

				if (model.RemainingSeconds <= 0)
				{
					_mutedClients.remove(model);
					member.mute(false).queue();
				}
			}
		}
	}

	public boolean AddUserToMutes(String discordUserId, int minutes)
	{
		if (IsUserMuted(discordUserId))
			return false;
		MuteClientModel model = new MuteClientModel();
		model.Id = discordUserId;
		model.RemainingSeconds = minutes * 60;
		_mutedClients.add(model);
		return true;
	}

	public boolean IsUserMuted(String discordUserId)
	{
		MuteClientModel model = _mutedClients.stream()
				.filter(client -> client.Id.equals(discordUserId))
				.findFirst()
				.orElse(null);
		return model != null;
	}
}
