package org.database;

import DiscordBot.Bot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class DatabaseController
{
	public DatabaseController(){

	}

	public boolean IsUserExists(String discordUserId) throws SQLException
	{
		String sql = "SELECT COUNT(*) FROM Users WHERE discordId = ?";

		try (Connection conn = Bot.DatabaseManagerController.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql)) {

			pstmt.setString(1, discordUserId);

			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
			}
		}

		return false;
	}

	public void AddUser(String discordUserId, int points) throws SQLException
	{
		String sql = "INSERT INTO Users(discordId, points) VALUES(?, ?)";
		Connection conn = Bot.DatabaseManagerController.getConnection();
		try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

			Random random = new Random();
			// Set the corresponding param
			pstmt.setString(1, discordUserId);
			pstmt.setInt(2, points);

			// Execute the insert
			pstmt.executeUpdate();
		}
	}

	public void AddPoints(String discordUserId, int pointsToAdd) throws SQLException
	{
		if (!IsUserExists(discordUserId)){
			AddUser(discordUserId, pointsToAdd);
			return;
		}
		String sql = "UPDATE Users SET points = points + ? WHERE discordId = ?";
		try (Connection conn = Bot.DatabaseManagerController.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setInt(1, pointsToAdd);
			pstmt.setString(2, discordUserId);
			pstmt.executeUpdate();
		}
	}

	public int GetPoints(String discordUserId) throws SQLException
	{
		if (!IsUserExists(discordUserId))
		{
			AddUser(discordUserId, 0);
			return 0;
		}
		String sql = "SELECT points FROM Users WHERE discordId = ?";
		try (Connection conn = Bot.DatabaseManagerController.getConnection();
		     PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, discordUserId);
			ResultSet s = pstmt.executeQuery();
			return s.getInt(1);
		}
	}

	public void RemovePoints(String discordUserId, int pointToRemove) throws SQLException
	{
		String sql = "UPDATE Users SET points = points - ? WHERE discordId = ?";
		try (Connection conn = Bot.DatabaseManagerController.getConnection();
			 PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setInt(1, pointToRemove);
			pstmt.setString(2, discordUserId);
			pstmt.executeUpdate();
		}
	}
}
