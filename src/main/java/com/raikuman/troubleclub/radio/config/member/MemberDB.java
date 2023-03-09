package com.raikuman.troubleclub.radio.config.member;

import com.raikuman.botutilities.database.DatabaseManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles IO for the member database
 *
 * @version 1.0 2022-03-08
 * @since 1.2
 */
public class MemberDB {

	private static final Logger logger = LoggerFactory.getLogger(MemberDB.class);

	/**
	 * Populates the member table with members from the current list of guilds. Also checks if users have
	 * joined or left while the bot was disconnected
	 * @param guilds The guild to add members from
	 */
	public static void populateMemberTable(List<Guild> guilds) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement retrieveStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"SELECT member_long FROM members");

			PreparedStatement removeStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"DELETE FROM members WHERE member_long = ?");

			PreparedStatement insertStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"INSERT OR IGNORE INTO members(member_long) VALUES(?)")
			) {

			// Retrieve
			List<String> memberDatabaseList = new ArrayList<>();
			try (ResultSet resultSet = retrieveStatement.executeQuery()) {
				while (resultSet.next())
					memberDatabaseList.add(resultSet.getString("member_long"));
			}

			List<String> memberGuildList = new ArrayList<>();
			for (Guild guild : guilds) {
				for (Member member : guild.getMembers()) {
					if (member.getUser().isBot())
						continue;

					memberGuildList.add(member.getId());
				}
			}

			List<String> memberRemoveList = new ArrayList<>();
			for (String memberId : memberDatabaseList) {
				if (!memberGuildList.contains(memberId))
					memberRemoveList.add(memberId);
			}

			List<String> memberAddList = new ArrayList<>();
			for (String memberId : memberGuildList) {
				if (!memberDatabaseList.contains(memberId))
					memberAddList.add(memberId);
			}

			// Remove
			for (String memberId : memberRemoveList) {
				removeStatement.setString(1, memberId);
				removeStatement.addBatch();
				removeStatement.clearParameters();
			}
			removeStatement.executeBatch();

			// Insert
			for (String memberId : memberAddList) {
				insertStatement.setString(1, memberId);
				insertStatement.addBatch();
				insertStatement.clearParameters();
			}
			insertStatement.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not populate the members table");
		}
	}

	/**
	 * Add a member to the member table
	 * @param member The member to add to the table
	 */
	public static void addMember(Member member) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"INSERT OR IGNORE INTO members(member_long) VALUES(?)")
			) {

			preparedStatement.setString(1, member.getId());
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not add member to the members table");
		}
	}

	/**
	 * Removes a member from the member table
	 * @param member The member to remove from the table
	 */
	public static void removeMember(Member member) {
		try (
			Connection connection = DatabaseManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(
				// language=SQLITE-SQL
				"DELETE FROM members " +
				"WHERE member_long = ?")
			) {

			preparedStatement.setString(1, member.getId());
			preparedStatement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Could not remove member from the members table");
		}

		// Remove all rows that rely on member
	}
}
