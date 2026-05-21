UPDATE IPA_CONFIG SET DESCRIPTION='Atribut member of del servidor LDAP CAIB' WHERE KEY LIKE '%.pluginsib.userinformation.ldap.attribute.memberof';
UPDATE IPA_CONFIG SET DESCRIPTION='Nom de l''endpoint del plugin d''usuaris' WHERE KEY LIKE '%.plugin.dades.usuari.endpointName';
DELETE FROM IPA_CONFIG WHERE KEY LIKE '%.documents.firma.biometrica';