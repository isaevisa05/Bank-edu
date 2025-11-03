CREATE TABLE `bank_edu`.`accounts` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `balance` DECIMAL(16,2) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`));


CREATE TABLE `bank_edu`.`history` (
  `id` BIGINT NOT NULL,
  `payer` BIGINT NOT NULL,
  `recipient` BIGINT NULL,
  `type` ENUM('PAY', 'DEPOSIT', 'WITHDRAW') VARCHAR(24) NOT NULL,
  `amount` DECIMAL(16,2) NOT NULL,
  PRIMARY KEY (`id`));
