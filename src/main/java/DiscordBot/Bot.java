package DiscordBot;

import com.github.kaktushose.jda.commands.JDACommands;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.commands.NormalCommands;
import org.database.DatabaseController;
import org.database.DatabaseManager;
import org.workers.MuteController;
import org.workers.PointsWorker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Bot extends ListenerAdapter
{
	private static String token = "";

	public static JDA JDAController;

	public static DatabaseManager DatabaseManagerController = new DatabaseManager();
	public static DatabaseController DatabaseController = new DatabaseController();
	public static MuteController MuteController = new MuteController();

	public static void main(String[] args) throws SQLException
	{
		Path filePath = Paths.get("token.txt");
		try{
			String content = Files.readString(filePath);
			token = content;
			System.out.println(content);
		}
		catch (IOException ex){

		}
		JDABuilder builder = JDABuilder.createDefault(token);
		JDA bl = builder.build();

		DatabaseManagerController.connect();

		JDAController = bl;
		JDACommands jdaCommands = JDACommands.start(bl, NormalCommands.class, "org.commands");
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		PointsWorker pointsWorker = new PointsWorker();
		scheduler.scheduleAtFixedRate(pointsWorker::Run, 0, 1, TimeUnit.SECONDS);
		scheduler.scheduleAtFixedRate(MuteController::Run, 0, 1, TimeUnit.SECONDS);
	}
}
