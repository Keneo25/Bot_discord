package org.workers;


import DiscordBot.Bot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import org.models.VoiceClientModel;
import org.values.Values;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PointsWorker
{
	private static final List<VoiceClientModel> _voiceClients = new ArrayList<>();

	public void Run()
	{
		Guild guild = Bot.JDAController.getGuildById(Values.Constants.GUILD_ID);
		if (guild == null)
			return;


		for (GuildChannel guildChannel : guild.getChannels())
		{
			if (guildChannel.getType() != ChannelType.VOICE)
				continue;
			VoiceChannel xd = (VoiceChannel) guildChannel;
			List<Member> members = xd.getMembers();
			for (Member member : members)
			{
				VoiceClientModel model = _voiceClients.stream()
						.filter(client -> client.getId().equals(member.getId()))
						.findFirst()
						.orElse(null);
				if (model == null)
				{
					VoiceClientModel md = new VoiceClientModel();
					md.setId(member.getId());
					md.setSeconds(0);
					_voiceClients.add(md);
				}
				else{
					model.Seconds += 1;
					if (model.getSeconds() % Values.Constants.POINTS_PER_INTERVAL == 0 && model.getSeconds() != 0){
						try{
							Bot.DatabaseController.AddPoints(model.Id, Values.Constants.POINTS_PER_MINUTE);
						}
						catch (SQLException ex){
							System.out.println(ex.toString());
						}
					}
				}
			}
		}
	}
}
