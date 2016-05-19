

ALTER TABLE ip.ip
/*DROP COLUMN description*/
# ADD location VARCHAR(255);
ADD owner VARCHAR(255);

INSERT INTO ip.ip (id, fromIP, toIP, location, owner) VALUES (NULL,'0.0.0.0','255.255.255.255','','' );

SELECT count(*)
FROM ip.ip;

TRUNCATE TABLE ip.ip;

DESC  ip.ip;

DROP TABLE IF EXISTS ip.ip;
CREATE TABLE ip.ip(
  id int UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  fromIP VARCHAR(15) NOT NULL,
  toIP VARCHAR(15) NOT NULL,
  position VARCHAR(255),
  description VARCHAR(1024)
);