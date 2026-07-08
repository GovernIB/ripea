# Desplegament de RIPEA Back com a aplicació Spring Boot a OpenShift

Aquest document descriu com construir i desplegar el mòdul `ripea-back` com a
aplicació Spring Boot autònoma (sense JBoss/EAR) sobre OpenShift, gestionant la
configuració via **ConfigMap** i **Secrets**.

---

## 1. Arquitectura

```
┌─────────────────────────────────────────┐
│  Pod OpenShift                          │
│  ┌──────────────────────────────────┐   │
│  │  ripea-back (Spring Boot + Tomcat)│   │
│  │  :8080 /ripeaback                │   │
│  └──────────────────────────────────┘   │
│            │ munta                      │
│  ┌─────────┴──────────┐                 │
│  │ /deployments/config│                 │
│  │  application.      │  ← ConfigMap    │
│  │    properties      │                 │
│  └────────────────────┘                 │
│  Variables d'entorn sensibles ← Secret  │
└─────────────────────────────────────────┘
```

El perfil Maven **`ide`** substitueix l'EJB de JBoss per dependències directes de
`ripea-service`, afegint Tomcat embegut, i produeix un WAR executable per Spring Boot.

---

## 2. Construcció de la imatge

### 2.1 Build Maven (perfil `ide`)

```bash
# Construeix el WAR executable Spring Boot (sense frontend, per rapidesa)
./mvnw clean package -P ide -P-front -pl ripea-back -am -DskipTests

# Amb frontend React inclòs
./mvnw clean package -P ide,front -pl ripea-back -am
```

El resultat és `ripea-back/target/ripea-back-<versió>.war`.

### 2.2 Build de la imatge Docker

```bash
# Substitueix VERSION per la versió actual del projecte (ex. 1.0.6)
docker build \
  -f Dockerfile.springboot \
  --build-arg VERSION=1.0.6 \
  -t ghcr.io/governib/ripea-back:1.0.6 \
  .

# Pujar a la registry
docker push ghcr.io/governib/ripea-back:1.0.6
```

---

## 3. Configuració a OpenShift

### 3.1 ConfigMap — propietats no sensibles

Crea un ConfigMap amb el contingut de `application.properties`. Les propietats
sensibles (contrasenyes, claus) s'injectaran com a variables d'entorn des del Secret
(apartat 3.2) i sobreescriuran les del fitxer.

```yaml
# oc apply -f ripea-back-configmap.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: ripea-back-config
  namespace: <namespace>
data:
  application.properties: |
    # === Spring Boot / JPA ===
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.Oracle10gDialect
    spring.jpa.properties.hibernate.hbm2ddl.auto=none
    spring.jpa.properties.hibernate.show_sql=false
    spring.jpa.properties.hibernate.format_sql=true
    spring.jpa.properties.hibernate.use_sql_comments=true
    spring.jpa.properties.hibernate.hql.bulk_id_strategy=org.hibernate.hql.spi.id.inline.InlineIdsOrClauseBulkIdStrategy
    spring.jpa.open-in-view=false
    spring.datasource.initialization-mode=never
    spring.main.allow-circular-references=true
    spring.thymeleaf.templateResolverOrder=1
    spring.jackson.default-property-inclusion=non_null
    spring.data.rest.base-path=/data-rest
    spring.liquibase.enabled=false

    # === Servidor ===
    server.servlet.context-path=/ripeaback
    server.port=8080

    spring.mvc.view.prefix=/WEB-INF/jsp/
    spring.mvc.view.suffix=.jsp

    logging.level.root=INFO
    logging.level.org.reflections.Reflections=WARN

    # === Datasource (credencials injectades com a variables d'entorn) ===
    spring.datasource.url=${SPRING_DATASOURCE_URL}
    spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
    spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

    # === Keycloak ===
    keycloak.auth-server-url=${KEYCLOAK_AUTH_URL}
    keycloak.realm=${KEYCLOAK_REALM:GOIB}
    keycloak.resource=${KEYCLOAK_CLIENT_ID:goib-default}
    keycloak.credentials.secret=${KEYCLOAK_SECRET}

    # === Correu electrònic ===
    spring.mail.host=${MAIL_HOST}
    spring.mail.port=${MAIL_PORT:465}
    spring.mail.username=${MAIL_USERNAME}
    spring.mail.password=${MAIL_PASSWORD}

    # === Aplicació RIPEA ===
    es.caib.ripea.base.url=${RIPEA_BASE_URL}
    es.caib.ripea.email.remitent=${RIPEA_EMAIL_REMITENT}
    es.caib.ripea.front.api.url=${RIPEA_FRONT_API_URL}
    es.caib.ripea.app.data.dir=/deployments/data
    es.caib.ripea.plugin.gesdoc.filesystem.base.dir=/deployments/data/gesdoc
    es.caib.ripea.encription.key=${RIPEA_ENCRYPTION_KEY}

    # === Plugins (URLs públiques, credencials via env vars) ===
    es.caib.ripea.plugin.notificacio.url=${NOTIB_URL}
    es.caib.ripea.plugin.notificacio.username=${NOTIB_USERNAME}
    es.caib.ripea.plugin.notificacio.password=${NOTIB_PASSWORD}

    es.caib.ripea.plugin.arxiu.caib.base.url=${ARXIU_URL}
    es.caib.ripea.plugin.arxiu.caib.usuari=${ARXIU_USERNAME}
    es.caib.ripea.plugin.arxiu.caib.contrasenya=${ARXIU_PASSWORD}

    es.caib.ripea.distribucio.backofficeIntegracio.ws.url=${DISTRIBUCIO_URL}
    es.caib.ripea.distribucio.backofficeIntegracio.ws.username=${DISTRIBUCIO_USERNAME}
    es.caib.ripea.distribucio.backofficeIntegracio.ws.password=${DISTRIBUCIO_PASSWORD}

    es.caib.ripea.plugin.registre.baseurl=${REGWEB_URL}
    es.caib.ripea.plugin.registre.username=${REGWEB_USER}
    es.caib.ripea.plugin.registre.password=${REGWEB_PASSWORD}

    es.caib.ripea.plugin.firmaservidor.portafib.endpoint=${FIRMASERVIDOR_URL}
    es.caib.ripea.plugin.firmaservidor.portafib.auth.username=${FIRMASERVIDOR_USERNAME}
    es.caib.ripea.plugin.firmaservidor.portafib.auth.password=${FIRMASERVIDOR_PASSWORD}

    es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl=${KEYCLOAK_AUTH_URL}
    es.caib.ripea.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret=${KEYCLOAK_SECRET}
```

Aplicar:
```bash
oc apply -f ripea-back-configmap.yaml
```

---

### 3.2 Secret — credencials i dades sensibles

Les contrasenyes i claus secretes es gestionen com a variables d'entorn mapeades
des d'un Secret. Spring Boot les recull automàticament (relaxed binding):
`SPRING_DATASOURCE_PASSWORD` → `spring.datasource.password`.

```yaml
# oc apply -f ripea-back-secret.yaml
apiVersion: v1
kind: Secret
metadata:
  name: ripea-back-secrets
  namespace: <namespace>
type: Opaque
stringData:
  SPRING_DATASOURCE_URL: "jdbc:oracle:thin:@oracle-host:1521:xe"
  SPRING_DATASOURCE_USERNAME: "RIPEA10"
  SPRING_DATASOURCE_PASSWORD: "contrasenya_oracle"

  KEYCLOAK_AUTH_URL: "https://auth.caib.es/auth"
  KEYCLOAK_REALM: "GOIB"
  KEYCLOAK_CLIENT_ID: "goib-default"
  KEYCLOAK_SECRET: "secret-keycloak"

  MAIL_HOST: "correu.caib.es"
  MAIL_PORT: "465"
  MAIL_USERNAME: "ripea@caib.es"
  MAIL_PASSWORD: "contrasenya_mail"

  RIPEA_BASE_URL: "https://ripea.caib.es/ripeaback"
  RIPEA_EMAIL_REMITENT: "ripea@caib.es"
  RIPEA_FRONT_API_URL: "https://ripea.caib.es/ripeaback/api/"
  RIPEA_ENCRYPTION_KEY: "clau-encriptacio-secreta"

  NOTIB_URL: "https://notib.caib.es/notibapi"
  NOTIB_USERNAME: "ripea"
  NOTIB_PASSWORD: "contrasenya_notib"

  ARXIU_URL: "https://arxiu.caib.es/esb"
  ARXIU_USERNAME: "ripea"
  ARXIU_PASSWORD: "contrasenya_arxiu"

  DISTRIBUCIO_URL: "https://distribucio.caib.es/distribucioapi/interna"
  DISTRIBUCIO_USERNAME: "ripea"
  DISTRIBUCIO_PASSWORD: "contrasenya_distribucio"

  REGWEB_URL: "https://regweb.caib.es"
  REGWEB_USER: "ripea"
  REGWEB_PASSWORD: "contrasenya_regweb"

  FIRMASERVIDOR_URL: "https://portafib.caib.es/rest/v1/"
  FIRMASERVIDOR_USERNAME: "ripea"
  FIRMASERVIDOR_PASSWORD: "contrasenya_portafib"
```

> **Nota de seguretat**: en entorns productius, useu `oc create secret` o una eina
> de gestió de secrets com Vault o OpenShift Secrets Management en comptes d'un
> fitxer YAML amb `stringData`.

```bash
# Alternativa per crear el secret sense fitxer YAML
oc create secret generic ripea-back-secrets \
  --from-literal=SPRING_DATASOURCE_PASSWORD=contrasenya \
  --from-literal=RIPEA_ENCRYPTION_KEY=clau-secreta \
  ...
```

---

### 3.3 Deployment

```yaml
# oc apply -f ripea-back-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ripea-back
  namespace: <namespace>
  labels:
    app: ripea-back
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ripea-back
  template:
    metadata:
      labels:
        app: ripea-back
    spec:
      containers:
        - name: ripea-back
          image: ghcr.io/governib/ripea-back:1.0.6
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
              protocol: TCP
          # Totes les credencials s'injecten com a variables d'entorn des del Secret
          envFrom:
            - secretRef:
                name: ripea-back-secrets
          # El ConfigMap es munta com a fitxer application.properties
          volumeMounts:
            - name: config-volume
              mountPath: /deployments/config
              readOnly: true
            - name: data-volume
              mountPath: /deployments/data
          resources:
            requests:
              memory: "512Mi"
              cpu: "250m"
            limits:
              memory: "1536Mi"
              cpu: "1000m"
          readinessProbe:
            httpGet:
              path: /ripeaback/actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 10
            failureThreshold: 6
          livenessProbe:
            httpGet:
              path: /ripeaback/actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 15
            failureThreshold: 4
      volumes:
        - name: config-volume
          configMap:
            name: ripea-back-config
            items:
              - key: application.properties
                path: application.properties
        - name: data-volume
          persistentVolumeClaim:
            claimName: ripea-back-data
      securityContext:
        runAsNonRoot: true
        runAsUser: 1001
        fsGroup: 0
```

---

### 3.4 PersistentVolumeClaim per a fitxers

```yaml
apiVersion: v1
kind: PersistentVolumeClaim
metadata:
  name: ripea-back-data
  namespace: <namespace>
spec:
  accessModes:
    - ReadWriteOnce
  resources:
    requests:
      storage: 10Gi
  storageClassName: <storage-class>
```

---

### 3.5 Service i Route

```yaml
apiVersion: v1
kind: Service
metadata:
  name: ripea-back
  namespace: <namespace>
spec:
  selector:
    app: ripea-back
  ports:
    - port: 8080
      targetPort: 8080
      protocol: TCP
  type: ClusterIP
---
apiVersion: route.openshift.io/v1
kind: Route
metadata:
  name: ripea-back
  namespace: <namespace>
spec:
  host: ripea.apps.<cluster-domain>
  to:
    kind: Service
    name: ripea-back
  port:
    targetPort: 8080
  tls:
    termination: edge
    insecureEdgeTerminationPolicy: Redirect
```

---

## 4. Verificació del desplegament

```bash
# Estat dels pods
oc get pods -l app=ripea-back

# Logs de l'aplicació
oc logs -f deployment/ripea-back

# Verificar que l'aplicació carrega la configuració
oc exec deployment/ripea-back -- \
  ls /deployments/config/

# Comprovar la salut de l'aplicació
curl https://ripea.apps.<cluster-domain>/ripeaback/actuator/health
```

---

## 5. Actualització de la configuració

Quan es modifica el ConfigMap, cal redeployar el pod perquè els canvis s'apliquin:

```bash
# Actualitzar el ConfigMap
oc apply -f ripea-back-configmap.yaml

# Reiniciar els pods per aplicar els canvis
oc rollout restart deployment/ripea-back

# Seguir el desplegament
oc rollout status deployment/ripea-back
```

Per actualitzar un Secret:
```bash
oc patch secret ripea-back-secrets \
  --type='json' \
  -p='[{"op": "replace", "path": "/data/SPRING_DATASOURCE_PASSWORD", "value": "'$(echo -n 'nova-contrasenya' | base64)'"}]'

oc rollout restart deployment/ripea-back
```

---

## 6. Diferències respecte al desplegament JBoss EAP

| Aspecte | JBoss EAP (`Dockerfile`) | Spring Boot (`Dockerfile.springboot`) |
|---|---|---|
| Imatge base | `goib/jboss-eap72-openshift-base` | `eclipse-temurin:11-jre-alpine` |
| Mida imatge | ~700 MB | ~180 MB |
| Perfil Maven | `jboss` (per defecte) | `ide` |
| Artefacte | `ripea.ear` (tots els mòduls) | `ripea-back-*.war` (un WAR) |
| Config app | `ripea.properties` + `ripea_system.properties` | `application.properties` (Spring Boot) |
| Variables entorn | `JBOSS_*` | `SPRING_*` + propietats pròpies |
| Port | 8080 | 8080 |
| Context path | `/ripeaback` | `/ripeaback` |
