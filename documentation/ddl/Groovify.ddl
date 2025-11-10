-- Converted from Oracle SQL Developer DDL to MySQL 8
-- All primary and foreign keys are BIGINT
-- Genre_ID in Song is now NOT NULL
-- Safe to rerun (DROP TABLE IF EXISTS included)
-- Date: 2025-11-08

-- ========================================
-- Drop existing tables in dependency order
-- ========================================
DROP TABLE IF EXISTS Playlist_Song;
DROP TABLE IF EXISTS Song;
DROP TABLE IF EXISTS Playlist;
DROP TABLE IF EXISTS Client_Genre;
DROP TABLE IF EXISTS Genre;
DROP TABLE IF EXISTS Client;

-- ========================================
-- Table: Client
-- ========================================
CREATE TABLE Client (
    ID BIGINT NOT NULL AUTO_INCREMENT,
    Name VARCHAR(20) NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Image_File_Name VARCHAR(50),
    Description VARCHAR(255),
    Password_Salt VARCHAR(16),
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ========================================
-- Table: Genre
-- ========================================
CREATE TABLE Genre (
    ID BIGINT NOT NULL AUTO_INCREMENT,
    Name VARCHAR(50) NOT NULL,
    PRIMARY KEY (ID)
) ENGINE=InnoDB;

-- ========================================
-- Table: Client_Genre
-- ========================================
CREATE TABLE Client_Genre (
    Genre_ID BIGINT NOT NULL,
    Client_ID BIGINT NOT NULL,
    PRIMARY KEY (Genre_ID, Client_ID),
    FOREIGN KEY (Genre_ID) REFERENCES Genre(ID) ON DELETE CASCADE,
    FOREIGN KEY (Client_ID) REFERENCES Client(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- Table: Playlist
-- ========================================
CREATE TABLE Playlist (
    ID BIGINT NOT NULL AUTO_INCREMENT,
    Name VARCHAR(20) NOT NULL,
    Description VARCHAR(100),
    Client_ID BIGINT NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (Client_ID) REFERENCES Client(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- Table: Song
-- ========================================
CREATE TABLE Song (
    ID BIGINT NOT NULL AUTO_INCREMENT,
    Filename VARCHAR(100) NOT NULL,
    Title VARCHAR(100) NOT NULL,
    Artist VARCHAR(100) NOT NULL,
    Genre_ID BIGINT NOT NULL,
    PRIMARY KEY (ID),
    FOREIGN KEY (Genre_ID) REFERENCES Genre(ID) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ========================================
-- Table: Playlist_Song
-- ========================================
CREATE TABLE Playlist_Song (
    Playlist_ID BIGINT NOT NULL,
    Song_ID BIGINT NOT NULL,
    PRIMARY KEY (Playlist_ID, Song_ID),
    FOREIGN KEY (Playlist_ID) REFERENCES Playlist(ID) ON DELETE CASCADE,
    FOREIGN KEY (Song_ID) REFERENCES Song(ID) ON DELETE CASCADE
) ENGINE=InnoDB;
