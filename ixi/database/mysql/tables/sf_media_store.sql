CREATE TABLE `sf_media_store` (
  `uuid` int(11) NOT NULL AUTO_INCREMENT,
  `media_name` text NOT NULL,
  `sender_jid` varchar(256) NOT NULL,
  `receiver_jid` varchar(256) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
