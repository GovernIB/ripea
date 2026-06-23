# ![Logo](https://github.com/GovernIB/ripea/blob/ripea-wip/ripea-war/src/main/webapp/img/logo.png)
RIPEA és l'acrònim de Repositori per a la Interoperabilitat dels Procediments Electrònics Administratius.
RIPEA és el repositori corporatiu d’expedients electrònics que permetrà als usuaris administradors la creació d’expedients a partir de metadocuments i metadades; i als usuaris gestors, la tramitació d’expedients electrònics a nivell de documents.
Actualment encara es troba en procés però es pot dir que les principals característiques que definiran aquesta eina seran les següents:
* Adaptada a les normes tècniques de l’ENI
* Integrada amb els sistemes de Custòdia, Portafirmes, Registre d’Entrada i Sortida, SISTRA.
* Permetrà la integració amb altres sistemes gestors d’expedients externs (Helium)
## <a name="caracteristiques"></a> Característiques
* Registered: 2015-01-21
* Topic category: Storage-Archiving 
* License: European Union Public License  & GNU General Public License version 3.0 (GPLv3) 
* Database Environment: JDBC 
* Development Status: Production/Stable 
* Intended Audience: Government 
* Operating System: OS Independent (Written in an interpreted language) 
* Programming Language: Java & JSP 
* Translations : Catalan & Spanish 
* User Interface: Web-based
## <a name="docs"></a> Accessibilitat
Aquest lloc web és parcialment conforme amb el RD 1112/2018:
* Puntuació mitja del lloc web: 8.16
* Nivell d'adequació estimat: A
* Darrera revisió: 06/05/2026
## <a name="docs"></a> Documentació
* [Manual d'usuari](https://github.com/GovernIB/ripea/raw/ripea-1.0/doc/pdf/01_ripea_manual_usuari.pdf)
* [Manual d'administrador](https://github.com/GovernIB/ripea/raw/ripea-1.0/doc/pdf/02_ripea_manual_administradors.pdf)
## <a name="v_estable"></a> Versió estable
### [v1.0.1](https://github.com/GovernIB/ripea/releases/tag/RIPEA_1.0.1_RC13_HOTFIX5)
## <a name="b_activa"></a> Branques actives
### [ripea-1.0](https://github.com/GovernIB/ripea/tree/ripea-1.0]) Versió estable
### [ripea-1.0-dev](https://github.com/GovernIB/ripea/tree/ripea-1.0-dev) Seguent versió candidata
### [ripea-1.0-wip](https://github.com/GovernIB/ripea/tree/ripea-1.0-wip) Branca de treball

## <a name="configuracio"></a> Configuració del Build

Aquest projecte utilitza Docker per construir la imatge de l'aplicació. Si s'utilitzen imatges base privades, cal configurar les següents variables d'entorn per a l'autenticació:

*   `DOCKER_USER`: El teu nom d'usuari (per a GitHub, utilitza `git`).
*   `DOCKER_TOKEN`: El teu Personal Access Token (PAT).

### Com configurar les variables d'entorn

#### A la terminal (Linux / macOS)
Pots exportar-les directament abans d'executar el build de Maven:
```bash
export DOCKER_USER="el_teu_usuari"
export DOCKER_TOKEN="el_teu_token"
./mvnw clean install
```
Per fer-ho permanent, afegeix aquestes línies al teu fitxer `~/.bashrc` o `~/.zshrc`.

#### A la terminal (Windows - PowerShell)
```powershell
$env:DOCKER_USER="el_teu_usuari"
$env:DOCKER_TOKEN="el_teu_token"
.\mvnw.cmd clean install
```

#### A GitHub Actions
Si utilitzes GitHub Actions, afegeix-les com a `secrets` del repositori i configura-les en el teu fitxer de workflow:
```yaml
- name: Build with Maven
  run: ./mvnw clean install
  env:
    DOCKER_USER: ${{ secrets.DOCKER_USER }}
    DOCKER_TOKEN: ${{ secrets.DOCKER_TOKEN }}
```
