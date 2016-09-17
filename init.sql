CREATE TABLE cities
(
  cid INT(11),
  name TEXT NOT NULL
);
CREATE TABLE countries
(
  cid INT(11) NOT NULL,
  name TEXT NOT NULL
);
CREATE TABLE friend_tag
(
  uid INT(11) NOT NULL,
  tid INT(11) NOT NULL,
  timestamp TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP',
  CONSTRAINT `PRIMARY` PRIMARY KEY (uid, tid)
);
CREATE TABLE friends
(
  uid INT(11) PRIMARY KEY NOT NULL,
  first_name MEDIUMTEXT NOT NULL,
  last_name MEDIUMTEXT NOT NULL,
  domain MEDIUMTEXT NOT NULL,
  photo MEDIUMTEXT NOT NULL,
  city INT(11) NOT NULL,
  sex INT(11) NOT NULL,
  group_id INT(11),
  is_deleted TINYINT(1) DEFAULT '0' NOT NULL,
  x DOUBLE,
  y DOUBLE,
  country INT(11),
  friends_col INT(11) DEFAULT '0' NOT NULL,
  comments LONGTEXT,
  added_to_db TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
  marked TIMESTAMP,
  is_potential INT(11) DEFAULT '0' NOT NULL,
  is_checked_by_google INT(11) DEFAULT '0' NOT NULL
);
CREATE TABLE friends_relation
(
  user_id INT(11) NOT NULL,
  friend_id INT(11) NOT NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (user_id, friend_id)
);
CREATE TABLE photos
(
  pid INT(11) NOT NULL,
  owner_id INT(11) NOT NULL,
  src VARCHAR(255) NOT NULL,
  created INT(11) NOT NULL,
  lat DOUBLE DEFAULT '0' NOT NULL,
  lng DOUBLE DEFAULT '0' NOT NULL,
  src_big VARCHAR(255),
  deleted INT(11) DEFAULT '0' NOT NULL,
  src_xbig VARCHAR(255),
  src_xxbig VARCHAR(255),
  src_xxxbig VARCHAR(255),
  likes INT(11),
  dateStr VARCHAR(255),
  CONSTRAINT `PRIMARY` PRIMARY KEY (pid, owner_id)
);
CREATE TABLE photos_old
(
  pid INT(11) PRIMARY KEY NOT NULL,
  owner_id INT(11) NOT NULL,
  src VARCHAR(255) NOT NULL,
  created INT(11) NOT NULL,
  lat FLOAT DEFAULT '0' NOT NULL,
  lng FLOAT DEFAULT '0' NOT NULL,
  src_big VARCHAR(255)
);
CREATE TABLE photos_old_2016
(
  pid INT(11) NOT NULL,
  owner_id INT(11) NOT NULL,
  src VARCHAR(255) NOT NULL,
  created INT(11) NOT NULL,
  lat DOUBLE DEFAULT '0' NOT NULL,
  lng DOUBLE DEFAULT '0' NOT NULL,
  src_big VARCHAR(255),
  deleted INT(11) DEFAULT '0' NOT NULL,
  src_xbig VARCHAR(255),
  src_xxbig VARCHAR(255),
  src_xxxbig VARCHAR(255),
  likes INT(11),
  CONSTRAINT `PRIMARY` PRIMARY KEY (pid, owner_id)
);
CREATE TABLE photos_polygon_map
(
  pid INT(11) NOT NULL,
  owner_id INT(11) NOT NULL,
  src VARCHAR(255) NOT NULL,
  created INT(11) NOT NULL,
  lat DOUBLE DEFAULT '0' NOT NULL,
  lng DOUBLE DEFAULT '0' NOT NULL,
  src_big VARCHAR(255),
  deleted INT(11) DEFAULT '0' NOT NULL,
  src_xbig VARCHAR(255),
  src_xxbig VARCHAR(255),
  src_xxxbig VARCHAR(255),
  dateStr VARCHAR(255),
  CONSTRAINT `PRIMARY` PRIMARY KEY (pid, owner_id)
);
CREATE TABLE photos_polygon_map_dates
(
  pid INT(11) DEFAULT '0' NOT NULL,
  owner_id INT(11) DEFAULT '0' NOT NULL,
  dateStr INT(11),
  CONSTRAINT `PRIMARY` PRIMARY KEY (pid, owner_id)
);
CREATE TABLE photos_polygon_map_deleted
(
  pid INT(11) NOT NULL,
  owner_id INT(11) NOT NULL,
  src VARCHAR(255) NOT NULL,
  created INT(11) NOT NULL,
  lat DOUBLE DEFAULT '0' NOT NULL,
  lng DOUBLE DEFAULT '0' NOT NULL,
  src_big VARCHAR(255),
  deleted INT(11) DEFAULT '0' NOT NULL,
  src_xbig VARCHAR(255),
  src_xxbig VARCHAR(255),
  src_xxxbig VARCHAR(255),
  CONSTRAINT `PRIMARY` PRIMARY KEY (pid, owner_id)
);
CREATE TABLE photos_polygon_map_old
(
  pid INT(11) NOT NULL,
  owner_id INT(11) NOT NULL,
  src VARCHAR(255) NOT NULL,
  created INT(11) NOT NULL,
  lat DOUBLE DEFAULT '0' NOT NULL,
  lng DOUBLE DEFAULT '0' NOT NULL,
  src_big VARCHAR(255),
  deleted INT(11) DEFAULT '0' NOT NULL,
  src_xbig VARCHAR(255),
  src_xxbig VARCHAR(255),
  src_xxxbig VARCHAR(255),
  CONSTRAINT `PRIMARY` PRIMARY KEY (pid, owner_id)
);
CREATE TABLE photos_tag
(
  pid INT(11) DEFAULT '0' NOT NULL,
  uid INT(11) DEFAULT '0' NOT NULL,
  tid INT(11) DEFAULT '0' NOT NULL,
  timestamp TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (pid, uid, tid)
);
CREATE TABLE potential_temp_table
(
  uid INT(11),
  cnt INT(11)
);
CREATE TABLE potential_with_photos_in_polygon
(
  uid INT(11) PRIMARY KEY NOT NULL
);
CREATE TABLE tags
(
  tid INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  name VARCHAR(255)
);
CREATE TABLE user_auth_ips
(
  id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  user_id INT(11),
  ip VARCHAR(255),
  timestamp TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL
);
CREATE TABLE user_failed_auth_ips
(
  id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  ip VARCHAR(255),
  timestamp TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
  username VARCHAR(255),
  pwd VARCHAR(255),
  token VARCHAR(255)
);
CREATE TABLE user_marked_log
(
  id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  link VARCHAR(255),
  user_id INT(11)
);
CREATE TABLE users
(
  id INT(11) PRIMARY KEY NOT NULL AUTO_INCREMENT,
  username VARCHAR(255),
  pwd VARCHAR(255),
  token VARCHAR(255),
  expired_at TIMESTAMP DEFAULT 'CURRENT_TIMESTAMP' NOT NULL,
  role INT(11) DEFAULT '0'
);
CREATE TABLE videos
(
  vid INT(11) DEFAULT '0' NOT NULL,
  owner_id INT(11) DEFAULT '0' NOT NULL,
  image TEXT,
  image_medium TEXT,
  player TEXT,
  date INT(11) NOT NULL,
  dateStr VARCHAR(255),
  deleted TINYINT(1) DEFAULT '0',
  title VARCHAR(255) DEFAULT '',
  CONSTRAINT `PRIMARY` PRIMARY KEY (vid, owner_id)
);
CREATE UNIQUE INDEX unique_id ON cities (cid);
CREATE UNIQUE INDEX unique_cid ON countries (cid);
CREATE INDEX friend_tag_timestamp_index ON friend_tag (timestamp);
CREATE INDEX city_index ON friends (city);
CREATE INDEX friends_col_index ON friends (friends_col);
CREATE INDEX friends_is_checked_by_google_index ON friends (is_checked_by_google);
CREATE INDEX friends_is_potential_index ON friends (is_potential);
CREATE INDEX group_id_index ON friends (group_id);
CREATE INDEX is_deleted_index ON friends (is_deleted);
CREATE INDEX sex_index ON friends (sex);
CREATE INDEX friend_id_index ON friends_relation (friend_id);
CREATE INDEX user_id_index ON friends_relation (user_id);
CREATE INDEX created_index ON photos (created);
CREATE INDEX lat_index ON photos (lat);
CREATE INDEX lng_index ON photos (lng);
CREATE INDEX owner_id_index ON photos (owner_id);
CREATE INDEX src_big_index ON photos (src_big);
CREATE INDEX src_dateStr ON photos (dateStr);
CREATE INDEX src_index ON photos (src);
CREATE INDEX lat_index ON photos_old (lat);
CREATE INDEX lng_index ON photos_old (lng);
CREATE INDEX owner_id_index ON photos_old (owner_id);
CREATE INDEX src ON photos_old (src);
CREATE INDEX src_big ON photos_old (src_big);
CREATE INDEX created_index ON photos_old_2016 (created);
CREATE INDEX lat_index ON photos_old_2016 (lat);
CREATE INDEX lng_index ON photos_old_2016 (lng);
CREATE INDEX owner_id_index ON photos_old_2016 (owner_id);
CREATE INDEX src_big_index ON photos_old_2016 (src_big);
CREATE INDEX src_index ON photos_old_2016 (src);
CREATE INDEX created_index ON photos_polygon_map (created);
CREATE INDEX deleted_index ON photos_polygon_map (deleted);
CREATE INDEX lat_index ON photos_polygon_map (lat);
CREATE INDEX lng_index ON photos_polygon_map (lng);
CREATE INDEX owner_id_index ON photos_polygon_map (owner_id);
CREATE INDEX photos_polygon_map_created_deleted_index ON photos_polygon_map (created, deleted);
CREATE INDEX photos_polygon_map_dateStr_index ON photos_polygon_map (dateStr);
CREATE INDEX photos_polygon_map_deleted_dateStr_index ON photos_polygon_map (deleted, dateStr);
CREATE INDEX src_big_index ON photos_polygon_map (src_big);
CREATE INDEX src_index ON photos_polygon_map (src);
CREATE INDEX photos_polygon_map_dates_dateStr_index ON photos_polygon_map_dates (dateStr);
CREATE INDEX created_index ON photos_polygon_map_deleted (created);
CREATE INDEX lat_index ON photos_polygon_map_deleted (lat);
CREATE INDEX lng_index ON photos_polygon_map_deleted (lng);
CREATE INDEX owner_id_index ON photos_polygon_map_deleted (owner_id);
CREATE INDEX src_big_index ON photos_polygon_map_deleted (src_big);
CREATE INDEX src_index ON photos_polygon_map_deleted (src);
CREATE INDEX created_index ON photos_polygon_map_old (created);
CREATE INDEX lat_index ON photos_polygon_map_old (lat);
CREATE INDEX lng_index ON photos_polygon_map_old (lng);
CREATE INDEX owner_id_index ON photos_polygon_map_old (owner_id);
CREATE INDEX src_big_index ON photos_polygon_map_old (src_big);
CREATE INDEX src_index ON photos_polygon_map_old (src);
CREATE INDEX photos_tag_tag_id_index ON photos_tag (tid);
CREATE INDEX photos_tag_uid_index ON photos_tag (uid);
CREATE UNIQUE INDEX unique_name ON tags (name);
CREATE INDEX user_auth_ips_ip_index ON user_auth_ips (ip);
CREATE INDEX user_auth_ips_timestamp_index ON user_auth_ips (timestamp);
CREATE INDEX user_auth_ips_user_id_index ON user_auth_ips (user_id);
CREATE INDEX user_failed_auth_ips_ip_index ON user_failed_auth_ips (ip);
CREATE INDEX user_failed_auth_ips_pwd_index ON user_failed_auth_ips (pwd);
CREATE INDEX user_failed_auth_ips_timestamp_index ON user_failed_auth_ips (timestamp);
CREATE INDEX user_failed_auth_ips_token_index ON user_failed_auth_ips (token);
CREATE INDEX user_failed_auth_ips_username_index ON user_failed_auth_ips (username);
CREATE INDEX users_expires_at_index ON users (expired_at);
CREATE INDEX users_token_index ON users (token);
CREATE INDEX users_username_pwd_index ON users (username, pwd);
CREATE UNIQUE INDEX users_username_uindex ON users (username);
CREATE INDEX videos_dateStr_index ON videos (dateStr);
CREATE INDEX videos_date_index ON videos (date);
CREATE INDEX videos_deleted_index ON videos (deleted);
