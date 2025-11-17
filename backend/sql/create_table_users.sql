CREATE TABLE IF NOT EXISTS `USERS` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(64) NOT NULL,
  `password` varchar(64) NOT NULL,
  `username` varchar(64) NOT NULL,
  `userdata` text,
  `lastLogin` datetime DEFAULT NULL,
  `refreshToken` tinytext,
  PRIMARY KEY (`id`)
)