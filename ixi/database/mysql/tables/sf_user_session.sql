CREATE TABLE `sf_user_session` (
  `UUID` int(11) NOT NULL AUTO_INCREMENT,
  `user_jid` varchar(256) NOT NULL,
  `resource_id` varchar(512) NOT NULL,
  `login_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `logout_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
