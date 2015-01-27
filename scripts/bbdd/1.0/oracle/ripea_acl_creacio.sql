--------------------------------------------------------
-- ACL_CLASS Table
--------------------------------------------------------
CREATE TABLE "IPA_ACL_CLASS" (
  "ID" NUMBER(19,0) NOT NULL,
  "CLASS" VARCHAR2(100) NOT NULL,
  PRIMARY KEY ("ID"),
  CONSTRAINT "ACL_CLASS_CLASS_UQ" UNIQUE ("CLASS")
);
 
--------------------------------------------------------
-- ACL_ENTRY Table
--------------------------------------------------------
CREATE TABLE "IPA_ACL_ENTRY" (
  "ID" NUMBER(19,0) NOT NULL,
  "ACL_OBJECT_IDENTITY" NUMBER(19,0) NOT NULL,
  "ACE_ORDER" NUMBER(19,0) NOT NULL,
  "SID" NUMBER(19,0) NOT NULL,
  "MASK" NUMBER(19,0) NOT NULL,
  "GRANTING" NUMBER(1,0) NOT NULL,
  "AUDIT_SUCCESS" NUMBER(1,0) NOT NULL,
  "AUDIT_FAILURE" NUMBER(1,0) NOT NULL,
  PRIMARY KEY ("ID"),
  CONSTRAINT "ACL_ENTRY_IDENT_ORDER_UQ" UNIQUE ("ACL_OBJECT_IDENTITY", "ACE_ORDER")
);
 
ALTER TABLE "IPA_ACL_ENTRY" ADD CONSTRAINT "ACL_ENTRY_GRANTING_CK"
  CHECK ("GRANTING" in (1,0));
ALTER TABLE "IPA_ACL_ENTRY" ADD CONSTRAINT "ACL_ENTRY_AUDIT_SUCCESS_CK"
  CHECK ("AUDIT_SUCCESS" in (1,0));
ALTER TABLE "IPA_ACL_ENTRY" ADD CONSTRAINT "ACL_ENTRY_AUDIT_FAILURE_CK"
  CHECK ("AUDIT_FAILURE" in (1,0));
 
--------------------------------------------------------
-- ACL_OBJECT_IDENTITY Table
--------------------------------------------------------
CREATE TABLE "IPA_ACL_OBJECT_IDENTITY" (
  "ID" NUMBER(19,0) NOT NULL,
  "OBJECT_ID_CLASS" NUMBER(19,0) NOT NULL,
  "OBJECT_ID_IDENTITY" NUMBER(19,0) NOT NULL,
  "PARENT_OBJECT" NUMBER(19,0),
  "OWNER_SID" NUMBER(19,0) NOT NULL,
  "ENTRIES_INHERITING" NUMBER(1,0) NOT NULL,
  PRIMARY KEY ("ID"),
  CONSTRAINT "ACL_OBJ_ID_CLASS_IDENT_UQ" UNIQUE ("OBJECT_ID_CLASS", "OBJECT_ID_IDENTITY")
);
 
ALTER TABLE "IPA_ACL_OBJECT_IDENTITY" ADD CONSTRAINT "ACL_OBJ_ID_ENTRIES_CK"
  CHECK ("ENTRIES_INHERITING" in (1,0));
 
--------------------------------------------------------
-- ACL_SID Table
--------------------------------------------------------
CREATE TABLE "IPA_ACL_SID" (
  "ID" NUMBER(19,0) NOT NULL,
  "PRINCIPAL" NUMBER(1,0) NOT NULL,
  "SID" VARCHAR2(100) NOT NULL,
  PRIMARY KEY ("ID"),
  CONSTRAINT "ACL_SID_PRINCIPAL_SID_UQ" UNIQUE ("SID", "PRINCIPAL")
);
 
ALTER TABLE "IPA_ACL_SID" ADD CONSTRAINT "ACL_SID_PRINCIPAL_CK"
  CHECK ("PRINCIPAL" in (1,0));
 
--------------------------------------------------------
-- Relationships
--------------------------------------------------------
 
ALTER TABLE "IPA_ACL_ENTRY" ADD CONSTRAINT "FK_ACL_ENTRY_ACL_OBJECT_ID"
  FOREIGN KEY ("ACL_OBJECT_IDENTITY")
  REFERENCES "IPA_ACL_OBJECT_IDENTITY" ("ID");
ALTER TABLE "IPA_ACL_ENTRY" ADD CONSTRAINT "FK_ACL_ENTRY_SID"
  FOREIGN KEY ("SID")
  REFERENCES "IPA_ACL_SID" ("ID");
 
ALTER TABLE "IPA_ACL_OBJECT_IDENTITY" ADD CONSTRAINT "FK_ACL_OBJ_ID_CLASS"
  FOREIGN KEY ("OBJECT_ID_CLASS")
  REFERENCES "IPA_ACL_CLASS" ("ID");
ALTER TABLE "IPA_ACL_OBJECT_IDENTITY" ADD CONSTRAINT "FK_ACL_OBJ_ID_PARENT"
  FOREIGN KEY ("PARENT_OBJECT")
  REFERENCES "IPA_ACL_OBJECT_IDENTITY" ("ID");
ALTER TABLE "IPA_ACL_OBJECT_IDENTITY" ADD CONSTRAINT "FK_ACL_OBJ_ID_SID"
  FOREIGN KEY ("OWNER_SID")
  REFERENCES "IPA_ACL_SID" ("ID");

--------------------------------------------------------
-- Create sequences
--------------------------------------------------------
CREATE SEQUENCE IPA_ACL_CLASS_SEQ;
CREATE SEQUENCE IPA_ACL_ENTRY_SEQ;
CREATE SEQUENCE IPA_ACL_OBJECT_IDENTITY_SEQ;
CREATE SEQUENCE IPA_ACL_SID_SEQ;

--------------------------------------------------------
-- Triggers
--------------------------------------------------------
CREATE OR REPLACE TRIGGER "IPA_ACL_CLASS_ID"
BEFORE INSERT ON IPA_ACL_CLASS
FOR EACH ROW
  BEGIN
    SELECT IPA_ACL_CLASS_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "IPA_ACL_ENTRY_ID"
BEFORE INSERT ON IPA_ACL_ENTRY
FOR EACH ROW
  BEGIN
    SELECT IPA_ACL_ENTRY_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "IPA_ACL_OBJECT_IDENTITY_ID"
BEFORE INSERT ON IPA_ACL_OBJECT_IDENTITY
FOR EACH ROW
  BEGIN
    SELECT IPA_ACL_OBJECT_IDENTITY_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/
 
CREATE OR REPLACE TRIGGER "IPA_ACL_SID_ID"
BEFORE INSERT ON IPA_ACL_SID
FOR EACH ROW
  BEGIN
    SELECT IPA_ACL_SID_SEQ.NEXTVAL INTO :new.id FROM dual;
  END;
/

