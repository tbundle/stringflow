CREATE TABLE `sf_user_device` (
  `uuid` int(11) NOT NULL AUTO_INCREMENT,
  `user_jid` varchar(256) NOT NULL,
  `device_id` varchar(256) NOT NULL,
  `device_token` varchar(512) NOT NULL,
  `notification_service` varchar(45) NOT NULL,
  `device_type` varchar(256) DEFAULT NULL,
  `last_update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
