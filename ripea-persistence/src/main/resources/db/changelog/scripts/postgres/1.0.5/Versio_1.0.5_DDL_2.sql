--1830 creat un camp a la taula de historics per gordar el tipus de contingut
ALTER TABLE IPA_CONT_LOG ADD COLUMN CONTINGUT_TIPUS VARCHAR(16) DEFAULT 'CONTINGUT' NOT NULL;