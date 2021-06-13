ALTER TABLE TRANSACTION DROP CONSTRAINT CONSTRAINT_FF;
DROP TABLE IF EXISTS WALLET;
DROP TABLE IF EXISTS TRANSACTION;

CREATE TABLE WALLET (
  WALLET_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  ACCOUNT_BALANCE VARCHAR(255)
);

CREATE TABLE TRANSACTION (
  TRANSACTION_ID BIGINT AUTO_INCREMENT PRIMARY KEY,
  TRANSACTION_TYPE VARCHAR(10) NOT NULL,
  TRANSACTION_AMOUNT DECIMAL NOT NULL,
  TRANSACTION_TIME VARCHAR(20) NOT NULL,
  WALLET_ID BIGINT DEFAULT NULL,

  CONSTRAINT CONSTRAINT_FF FOREIGN KEY (WALLET_ID) REFERENCES WALLET(WALLET_ID)
);
--ALTER TABLE TRANSACTION ADD FOREIGN KEY (WALLET_ID) REFERENCES WALLET(WALLET_ID);

INSERT INTO WALLET (ACCOUNT_BALANCE) VALUES
  ('1,2,3');