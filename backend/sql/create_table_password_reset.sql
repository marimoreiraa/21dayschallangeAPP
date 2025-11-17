CREATE TABLE IF NOT EXISTS `PASSWORD_RESET` (
  `id` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `used` tinyint(1) NOT NULL,
  `expiresIn` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `code` varchar(32) NOT NULL
)