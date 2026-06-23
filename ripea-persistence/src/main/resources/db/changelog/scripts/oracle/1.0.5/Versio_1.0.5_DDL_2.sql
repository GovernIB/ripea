--1830 creat un camp a la taula de historics per gordar el tipus de contingut
ALTER TABLE IPA_CONT_LOG ADD CONTINGUT_TIPUS VARCHAR2(16 CHAR) DEFAULT 'CONTINGUT' NOT NULL;