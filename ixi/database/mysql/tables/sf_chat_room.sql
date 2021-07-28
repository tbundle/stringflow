CREATE TABLE `sf_chat_room` (
  `jabber_id` varchar(128) NOT NULL,
  `name` varchar(256) NOT NULL,
  `subject` varchar(256) DEFAULT NULL,
  `access_mode` varchar(152) NOT NULL DEFAULT 'public',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`jabber_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;