package com.example.meetverse.util;

import java.io.File;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DatabaseManager {
    private static final String DB_PATH = "src/main/resources/com/example/meetverse/Databases/meetverse.db";
    private static Connection connection;

    static {
        try {
            initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void initializeDatabase() throws SQLException {
        File dbFile = new File(DB_PATH);
        dbFile.getParentFile().mkdirs();

        connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
        createTables();
    }

    private static void createTables() throws SQLException {
        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;

        String createMeetingsTable = """
            CREATE TABLE IF NOT EXISTS meetings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                description TEXT NOT NULL,
                meeting_date TEXT NOT NULL,
                meeting_time TEXT NOT NULL,
                meeting_type TEXT NOT NULL,
                created_by INTEGER NOT NULL,
                status TEXT DEFAULT 'Pending',
                is_history INTEGER DEFAULT 0,
                meeting_link TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (created_by) REFERENCES users(id)
            )
        """;

        String createMeetingParticipantsTable = """
            CREATE TABLE IF NOT EXISTS meeting_participants (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                meeting_id INTEGER NOT NULL,
                user_id INTEGER NOT NULL,
                FOREIGN KEY (meeting_id) REFERENCES meetings(id),
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createMeetingsTable);
            stmt.execute(createMeetingParticipantsTable);
            
            try {
                stmt.execute("ALTER TABLE meetings ADD COLUMN is_history INTEGER DEFAULT 0");
            } catch (SQLException e) {
            }
            
            try {
                stmt.execute("ALTER TABLE meetings ADD COLUMN meeting_link TEXT");
            } catch (SQLException e) {
            }
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean registerUser(String name, String email, String password) {
        String role = getUserCount() == 0 ? "Admin" : "User";
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, hashPassword(password));
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getUserCount() {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static User loginUser(String email, String password, String role) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND role = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, hashPassword(password));
            pstmt.setString(3, role);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static java.util.List<User> getAllUsers() {
        java.util.List<User> users = new java.util.ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY name";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static int createMeetingRequest(String title, String description, String date, String time, 
                                            String type, int createdBy, java.util.List<Integer> participantIds) {
        String insertMeetingSql = "INSERT INTO meetings (title, description, meeting_date, meeting_time, meeting_type, created_by) VALUES (?, ?, ?, ?, ?, ?)";
        String insertParticipantSql = "INSERT INTO meeting_participants (meeting_id, user_id) VALUES (?, ?)";
        
        try {
            connection.setAutoCommit(false);
            
            int meetingId;
            try (PreparedStatement pstmt = connection.prepareStatement(insertMeetingSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, title);
                pstmt.setString(2, description);
                pstmt.setString(3, date);
                pstmt.setString(4, time);
                pstmt.setString(5, type);
                pstmt.setInt(6, createdBy);
                pstmt.executeUpdate();
                
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    meetingId = rs.getInt(1);
                } else {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return -1;
                }
            }
            
            try (PreparedStatement pstmt = connection.prepareStatement(insertParticipantSql)) {
                for (int participantId : participantIds) {
                    pstmt.setInt(1, meetingId);
                    pstmt.setInt(2, participantId);
                    pstmt.executeUpdate();
                }
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            return meetingId;
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return -1;
        }
    }

    public static java.util.List<Meeting> getAllMeetings() {
        return getMeetingsByHistory(false);
    }
    
    public static java.util.List<Meeting> getHistoricalMeetings() {
        return getMeetingsByHistory(true);
    }
    
    private static java.util.List<Meeting> getMeetingsByHistory(boolean isHistory) {
        java.util.List<Meeting> meetings = new java.util.ArrayList<>();
        int historyFlag = isHistory ? 1 : 0;
        String sql = "SELECT m.*, u.name as creator_name FROM meetings m JOIN users u ON m.created_by = u.id WHERE m.is_history = ? ORDER BY m.created_at DESC";
        
        System.out.println("DEBUG: Fetching meetings with isHistory=" + isHistory);
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, historyFlag);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                meetings.add(new Meeting(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("meeting_date"),
                    rs.getString("meeting_time"),
                    rs.getString("meeting_type"),
                    rs.getInt("created_by"),
                    rs.getString("creator_name"),
                    rs.getString("status"),
                    rs.getString("created_at"),
                    rs.getString("meeting_link")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }
    
    public static void moveToHistory(int meetingId) {
        String sql = "UPDATE meetings SET is_history = 1 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, meetingId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static boolean isUserParticipant(int userId, int meetingId) {
        String sql = "SELECT COUNT(*) FROM meeting_participants WHERE user_id = ? AND meeting_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, meetingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static void updateMeetingLink(int meetingId, String link) {
        String sql = "UPDATE meetings SET meeting_link = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, link);
            pstmt.setInt(2, meetingId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static java.util.List<Meeting> getApprovedMeetings() {
        java.util.List<Meeting> meetings = new java.util.ArrayList<>();
        String sql = "SELECT m.*, u.name as creator_name FROM meetings m JOIN users u ON m.created_by = u.id WHERE m.status = 'Approved' AND m.is_history = 0 ORDER BY m.meeting_date, m.meeting_time";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                meetings.add(new Meeting(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("meeting_date"),
                    rs.getString("meeting_time"),
                    rs.getString("meeting_type"),
                    rs.getInt("created_by"),
                    rs.getString("creator_name"),
                    rs.getString("status"),
                    rs.getString("created_at"),
                    rs.getString("meeting_link")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }

    public static java.util.List<User> getMeetingParticipants(int meetingId) {
        java.util.List<User> participants = new java.util.ArrayList<>();
        String sql = "SELECT u.* FROM users u JOIN meeting_participants mp ON u.id = mp.user_id WHERE mp.meeting_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, meetingId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                participants.add(new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("role")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return participants;
    }

    public static boolean updateMeetingStatus(int meetingId, String status) {
        String sql = "UPDATE meetings SET status = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, meetingId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static java.util.List<Meeting> getMeetingsByCreator(int userId) {
        java.util.List<Meeting> meetings = new java.util.ArrayList<>();
        String sql = "SELECT m.*, u.name as creator_name FROM meetings m JOIN users u ON m.created_by = u.id WHERE m.created_by = ? AND m.is_history = 0 ORDER BY m.created_at DESC";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                meetings.add(new Meeting(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("meeting_date"),
                    rs.getString("meeting_time"),
                    rs.getString("meeting_type"),
                    rs.getInt("created_by"),
                    rs.getString("creator_name"),
                    rs.getString("status"),
                    rs.getString("created_at"),
                    rs.getString("meeting_link")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return meetings;
    }
    
    public static boolean updateMeetingDetails(int meetingId, String title, String description, 
                                              String date, String time, String type, java.util.List<Integer> participantIds) {
        String updateMeetingSql = "UPDATE meetings SET title = ?, description = ?, meeting_date = ?, meeting_time = ?, meeting_type = ? WHERE id = ?";
        String deleteParticipantsSql = "DELETE FROM meeting_participants WHERE meeting_id = ?";
        String insertParticipantSql = "INSERT INTO meeting_participants (meeting_id, user_id) VALUES (?, ?)";
        
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement pstmt = connection.prepareStatement(updateMeetingSql)) {
                pstmt.setString(1, title);
                pstmt.setString(2, description);
                pstmt.setString(3, date);
                pstmt.setString(4, time);
                pstmt.setString(5, type);
                pstmt.setInt(6, meetingId);
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = connection.prepareStatement(deleteParticipantsSql)) {
                pstmt.setInt(1, meetingId);
                pstmt.executeUpdate();
            }
            
            try (PreparedStatement pstmt = connection.prepareStatement(insertParticipantSql)) {
                for (int participantId : participantIds) {
                    pstmt.setInt(1, meetingId);
                    pstmt.setInt(2, participantId);
                    pstmt.executeUpdate();
                }
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateUserRole(int userId, String newRole) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newRole);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean transferAdminRole(int currentAdminId, int newAdminId) {
        try {
            connection.setAutoCommit(false);
            
            String demoteSql = "UPDATE users SET role = 'User' WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(demoteSql)) {
                pstmt.setInt(1, currentAdminId);
                pstmt.executeUpdate();
            }
            
            String promoteSql = "UPDATE users SET role = 'Admin' WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(promoteSql)) {
                pstmt.setInt(1, newAdminId);
                pstmt.executeUpdate();
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteUser(int userId) {
        try {
            connection.setAutoCommit(false);
            
            String deleteParticipantsSql = "DELETE FROM meeting_participants WHERE user_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteParticipantsSql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }
            
            String deleteUserSql = "DELETE FROM users WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteUserSql)) {
                pstmt.setInt(1, userId);
                pstmt.executeUpdate();
            }
            
            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean addUser(String name, String email, String password, String role) {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, hashPassword(password));
            pstmt.setString(4, role);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class User {
        private final int id;
        private final String name;
        private final String email;
        private final String role;

        public User(int id, String name, String email, String role) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.role = role;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }

    public static class Meeting {
        private final int id;
        private final String title;
        private final String description;
        private final String meetingDate;
        private final String meetingTime;
        private final String meetingType;
        private final int createdBy;
        private final String creatorName;
        private final String status;
        private final String createdAt;
        private final String meetingLink;

        public Meeting(int id, String title, String description, String meetingDate, String meetingTime,
                      String meetingType, int createdBy, String creatorName, String status, String createdAt) {
            this(id, title, description, meetingDate, meetingTime, meetingType, createdBy, creatorName, status, createdAt, null);
        }
        
        public Meeting(int id, String title, String description, String meetingDate, String meetingTime,
                      String meetingType, int createdBy, String creatorName, String status, String createdAt, String meetingLink) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.meetingDate = meetingDate;
            this.meetingTime = meetingTime;
            this.meetingType = meetingType;
            this.createdBy = createdBy;
            this.creatorName = creatorName;
            this.status = status;
            this.createdAt = createdAt;
            this.meetingLink = meetingLink;
        }

        public int getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getMeetingDate() { return meetingDate; }
        public String getMeetingTime() { return meetingTime; }
        public String getMeetingType() { return meetingType; }
        public int getCreatedBy() { return createdBy; }
        public String getCreatorName() { return creatorName; }
        public String getStatus() { return status; }
        public String getCreatedAt() { return createdAt; }
        public String getMeetingLink() { return meetingLink; }
    }
}
