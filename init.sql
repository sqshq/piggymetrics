SET NAMES utf8;

CREATE DATABASE piggymetrics;
USE piggymetrics;

CREATE TABLE users (
  id int(11) NOT NULL AUTO_INCREMENT,
  username varchar(64) COLLATE utf8_bin NOT NULL DEFAULT '',
  password varchar(80) COLLATE utf8_bin NOT NULL DEFAULT '',
  enabled tinyint(1) NOT NULL DEFAULT '1',
  IP varchar(50) COLLATE utf8_bin DEFAULT NULL,
  data text COLLATE utf8_bin,
  note text COLLATE utf8_bin,
  checked_currency varchar(4) COLLATE utf8_bin DEFAULT 'rub',
  last_currency varchar(4) COLLATE utf8_bin DEFAULT 'rub',
  money int(10) DEFAULT '0',
  deposit tinyint(1) DEFAULT '1',
  interest smallint(2) DEFAULT '9',
  capitalization tinyint(1) DEFAULT '0',
  slider_value decimal(3,2) NOT NULL DEFAULT '0.80',
  userpic varchar(50) COLLATE utf8_bin DEFAULT NULL,
  email varchar(64) COLLATE utf8_bin DEFAULT NULL,
  email_hash varchar(40) COLLATE utf8_bin DEFAULT NULL,
  mailing tinyint(2) DEFAULT '30',
  last_mail datetime DEFAULT NULL,
  last_visit datetime DEFAULT NULL,
  role varchar(45) COLLATE utf8_bin DEFAULT 'ROLE_USER',
  backup tinyint(1) NOT NULL DEFAULT '0',
  language varchar(10) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY username (username) USING BTREE,
  UNIQUE KEY email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE settings (
  usd decimal(6,4) DEFAULT NULL,
  eur decimal(6,4) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE persistent_logins (
  username varchar(64) COLLATE utf8_bin NOT NULL,
  series varchar(64) COLLATE utf8_bin NOT NULL,
  token varchar(64) COLLATE utf8_bin NOT NULL,
  last_used timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (series)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO settings (usd, eur) VALUES (50, 60);

INSERT INTO users (id, username, PASSWORD, enabled, data, note, checked_currency, last_currency, money, deposit, interest, capitalization, slider_value, userpic, role, language)
  VALUES(1, 'en_demo', '5624ab3598ae3f48e0fc09254615195c8c293d0c8da99d2432cc37c48d953d62e6ae77de9fe83afd', 1, '{"incomes":{"1":{"income_id":1,"title":"salary","icon":"graphs","currency":"doll","period":"year","value":"34000","converted":"2833.333"},"2":{"income_id":2,"title":"scholarship","icon":"edu","currency":"doll","period":"month","value":"500","converted":"500.000"}},"expenses":{"1":{"expense_id":1,"title":"Rent","icon":"home","currency":"doll","period":"month","value":"1300","converted":"1300.000"},"2":{"expense_id":2,"title":"utilities","icon":"utilities","currency":"doll","period":"month","value":"120","converted":"120.000"},"3":{"expense_id":3,"title":"meal","icon":"meal","currency":"doll","period":"day","value":"16","converted":"486.666"},"4":{"expense_id":4,"title":"gas","icon":"gas","currency":"doll","period":"month","value":"240","converted":"240.000"},"5":{"expense_id":5,"title":"entertainment","icon":"earth","currency":"doll","period":"month","value":"200","converted":"200.000"},"6":{"expense_id":6,"title":"gym","icon":"sport","currency":"doll","period":"year","value":"700","converted":"58.333"},"7":{"expense_id":7,"title":"phone","icon":"phone","currency":"doll","period":"month","value":"30","converted":"30.000"}}}', 'Piggy metrics demo note', 'usd', 'usd', 5900, 1, 9, 1, 0.80, 'default.jpg', 'ROLE_USER', 'en');
insert into users (id, username, PASSWORD, enabled, data, note, checked_currency, last_currency, money, deposit, interest, capitalization, slider_value, userpic, role, language)
  VALUES(2, 'ru_demo', '5624ab3598ae3f48e0fc09254615195c8c293d0c8da99d2432cc37c48d953d62e6ae77de9fe83afd', 1, '{"incomes":{"1":{"income_id":"1","title":"Зарплата","icon":"case","currency":"rub","period":"month","value":"64000","converted":"64000"},"2":{"income_id":"2","title":"Стипендия","icon":"edu","currency":"rub","period":"month","value":"1500","converted":"1500"}},"expenses":{"1":{"expense_id":"1","title":"Аренда","icon":"home","currency":"rub","period":"month","value":"19000","converted":"19000"},"2":{"expense_id":"2","title":"Отпуск","icon":"island","currency":"rub","period":"year","value":"70000","converted":"5833.333"},"3":{"expense_id":"3","title":"Питание","icon":"meal","currency":"rub","period":"day","value":"300","converted":"9125.000"},"4":{"expense_id":"4","title":"Коммуналка","icon":"utilities","currency":"rub","period":"month","value":"3600","converted":"3600"},"5":{"expense_id":"5","title":"спортзал","icon":"sport","currency":"rub","period":"year","value":"17000","converted":"1416.667"},"6":{"expense_id":"6","title":"бензин","icon":"gas","currency":"rub","period":"month","value":"4200","converted":"4200"},"7":{"expense_id":"7","title":"Развлечения","icon":"utilities","currency":"rub","period":"month","value":"5000","converted":"5000"},"8":{"expense_id":"8","title":"телефон","icon":"phone","currency":"rub","period":"month","value":"400","converted":"400"}}}', 'Тестовая заметка', 'rub', 'rub', 59000, 1, 9, 1, 0.80, 'default.jpg', 'ROLE_USER', 'ru');