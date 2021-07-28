CREATE TABLE `sf_undelivered_stanzas` (
  `uuid` int(11) NOT NULL AUTO_INCREMENT,
  `stanza` blob NOT NULL,
  `receiver_jid` varchar(126) NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
