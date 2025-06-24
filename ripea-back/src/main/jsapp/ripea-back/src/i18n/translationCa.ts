const translationCa = {
    common: {
        close: "Tancar",
        create: "Crear",
        copy: "Copiar",
        update: "Modificar",
        delete: "Esborrar",
        action: "Acció",
        expand: "Expandir",
        contract: "Contraure",
        download: "Descarregar",
        detail: "Detall",
        refresh: "Refrescar",
        clear: "Netejar",
        search: "Cercar",
        options: "Opcions",
        import: "Importar",
        export: "Exportar",
        consult: "Consultar",
    },
    enum: {
        rol: {
            IPA_SUPER: "Superusuari",
            IPA_ADMIN: "Administrador",
            IPA_DISSENY: "Dissenyador",
            IPA_ORGAN_ADMIN: "Administrador d'òrgans",
            IPA_REVISIO: "Revisor",
            tothom: "Usuari",
        },
        siNO: {
            true: "Sí",
            false: "No",
        },
        prioritat: {
            D_MOLT_ALTA: "Molt alta",
            C_ALTA: "Alta",
            B_NORMAL: "Normal",
            A_BAIXA: "Baixa",
        },
        estat: {
            TANCAT: "Tancat",
            OBERT: "Obert",
        },
        origen: {
            O0: "Ciutadà",
            O1: "Administració",
        },
        estatElaboracio: {
            EE01: "Original",
            EE02: "Còpia electrònica autèntica amb canvi de format",
            EE03: "Còpia electrònica autèntica de document en paper",
            EE04: "Còpia electrònica parcial autèntica",
            EE99: "Altres",
        },
        tipoFirma: {
            TF01: "CSV",
            TF02: "Signatura XAdES internament separada",
            TF03: "Signatura XAdES envolupada",
            TF04: "Signatura CAdES separada/explícita",
            TF05: "Signatura CAdES adjunta/implícita",
            TF06: "PAdES",
            TF07: "SMIME",
            TF08: "ODT",
            TF09: "OOXML",
        },
    },
    page: {
        comment: {
            expedient: "Comentaris de l'expedient",
            tasca: "Comentaris de la tasca",
        },
        contingut: {
            grid: {
                nom: "Nom",
            },
            detalle: {
                numero: "Número",
                titol: "Títol",
                metaExpedient: "Tipus",
                organGestor: "Òrgan gestor",
                fechaApertura: "Data d'obertura",
                estat: "Estat",
                prioritat: "Prioritat",
                clasificacio: "Classificació",
                dataProgramada: "Data en què es farà efectiu l’enviament de la notificació a Notific@",
                duracio: "Dies naturals\nLa notificació estarà disponible fins a les 23:59:59 del dia introduït, i caducarà a les 00:00 del dia següent. Només s’aplica a les Notificacions Electròniques. Es pot indicar tant un nombre de dies naturals com una data concreta.",
                dataCaducitat: "Dies naturals\nLa notificació estarà disponible fins a les 23:59:59 del dia introduït, i caducarà a les 00:00 del dia següent. Només s’aplica a les Notificacions Electròniques. Es pot indicar tant un nombre de dies naturals com una data concreta.",
                retard: "Dies que la notificació romandrà a la seu abans de ser enviada via DEH o CIE",
            },
            tabs: {
                contingut: "Contingut",
                dades: "Dades",
                interessats: "Interessats",
                remeses: "Remeses",
                publicacions: "Publicacions",
                anotacions: "Anotacions",
                versions: "Versions",
                tasques: "Tasques",

                actions: "Accions",
                move: "Moviments",
                auditoria: "Auditoria",
            },
            log: {
                causa: "Acció causa",
                param: "Paràmetres",
                param1: "Paràmetre 1",
                param2: "Paràmetre 2",
                objecte: "Objecte",
            },
            moviment: {
                causa: "Causa del moviment",
                origen: "Origen",
                desti: "Destí",
            },
            action: {
                guardarArxiu: {
                    label: "Desar a l'arxiu",
                    ok: "Element desat a l'arxiu",
                },
                move: {
                    label: "Moure...",
                    title: "Moure contingut",
                    ok: "Document '{{document}}' mogut correctament",
                },
                copy: {
                    label: "Copiar...",
                    title: "Copiar contingut",
                    ok: "Document '{{document}}' copiat correctament",
                },
                vincular: {
                    label: "Vincular...",
                    title: "Vincular contingut",
                    ok: "Document '{{document}}' vinculat correctament",
                },
                create: {
                    label: "Crear contingut",
                },
                history: {
                    label: "Històric d'accions",
                    title: "Històric d'accions de l'element",
                    detail: "Detall de l'acció",
                },
                infoArxiu: {
                    title: "Informació obtinguda de l'arxiu",
                    label: "Informació de l'arxiu",
                },
                importarExpedient: {
                    label: "Importar expedient relacionat...",
                    title: "Expedients relacionats",
                },
            },
            history: {
                create: "Creació",
                update: "Modificació",
                user: "Usuari",
                date: "Data",
            },
            alert: {
                valid: "Aquest contingut té errors de validació",
                metaNode: "Aquest document no té assignat un tipus de document",
                guardarPendent: "Pendent de gordar en arxivo",
            },
        },
        anotacio: {
            filter: {
                title: "Cercador d'anotacions de registre"
            },
            tabs: {
                resum: "Resum",
                estat: "Estat",
                registre: "Informació registre",
                interessats: "Interessats",
                annexos: "Annexos",
                justificant: "Justificant",
            },
            detall: {
                title: "Detalls de l'anotació de registre",
                estatView: "Estat",
                dataAlta: "Data d'alta",
                observacions: "Motiu",
                rejectedDate: "Data de rebuig",
                acceptedDate: "Data d'acceptació",
                usuariActualitzacio: "Usuari",
            },
            action: {
                justificant: {
                    label: "Descarregar justificant",
                    ok: "El justificant s'ha descarregat correctament",
                },
                acceptar: {
                    label: "Acceptar...",
                    title: "Acceptar expedient",
                    ok: "L'anotació s'ha acceptat correctament",
                },
                rebutjar: {
                    label: "Rebutjar...",
                    title: "Rebutjar expedient",
                    ok: "L'anotació s'ha rebutjat correctament",
                },
                canviProcediment: {
                    label: "Canviar procediment",
                    title: "Canviar procediment",
                },
                canviEstatDistribucio: {
                    label: "Canviar estat a distribució",
                    ok: "L'estat ha canviat correctament",
                },
                descargarAnnex: {
                    label: "Descarregar annex",
                    ok: "Annex descarregat correctament",
                },
                firma: {
                    label: "Signatures",
                    title: "Signatures",
                }
            }
        },
        tasca: {
            title: "Tasca",
            detall: {
                title: "Detalls de la tasca",
                metaExpedientTasca: "Tipus de tasca",
                metaExpedientTascaDescription: "Descripció del tipus de tasca",
                createdBy: "Creada per",
                responsablesStr: "Responsables",
                responsableActual: "Responsable actual",
                delegat: "Delegat",
                observadors: "Observadors",
                dataInici: "Data d'inici",
                duracio: "Duració",
                dataLimit: "Data límit",
                estat: "Estat",
                prioritat: "Prioritat",
            },
            action: {
                new: {
                    label: "Nova tasca",
                },
                tramitar: {
                    label: "Tramitar",
                },
                iniciar: {
                    label: "Iniciar",
                    ok: "La tasca s'ha iniciat correctament",
                },
                rebutjar: {
                    label: "Rebutjar",
                    title: "Rebutjar tasca",
                    ok: "La tasca s'ha rebutjat correctament",
                },
                cancel: {
                    label: "Cancel·lar",
                    title: "Segur que voleu cancel·lar aquesta tasca?",
                    ok: "La tasca s'ha cancel·lat correctament",
                },
                finalitzar: {
                    label: "Finalitzar",
                    ok: "La tasca s'ha finalitzat correctament",
                },
                reassignar: {
                    label: "Reassignar",
                    title: "Reassignar tasca",
                    ok: "La tasca s'ha reassignat correctament",
                },
                delegar: {
                    label: "Delegar",
                    title: "Delegar tasca",
                    ok: "La tasca s'ha delegat correctament",
                },
                retomar: {
                    label: "Reprendre",
                    title: "Reprendre tasca",
                    ok: "La tasca s'ha reprès correctament",
                },
                changeDataLimit: {
                    label: "Modificar data límit...",
                    title: "Canviar data límit",
                    ok: "La tasca s'ha modificat correctament",
                },
                changePrioritat: {
                    label: "Canviar prioritat...",
                    title: "Modificar prioritat de la tasca",
                    ok: "La tasca s'ha modificat correctament",
                },
                reobrir: {
                    label: "Reobrir",
                    title: "Reobrir tasca",
                    ok: "La tasca s'ha reobert correctament",
                },
            }
        },
        interessat: {
            title: "Interessat",
            rep: "Representant",
            detall: {
                nif: "NIF/CIF/NIE",
                nom: "Nom",
                raoSocial: "Raó social",
                llinatges: "Llinatges",
                telefon: "Telèfon",
                email: "Correu electrònic",
                incapacitat: "Incapacitat",
                direccio: "Adreça",
                direccioPostal: "Adreça postal",
                entregaDehObligat: "DEH obligat?",
            },
            action: {
                detail: {
                    title: "Detall de l'interessat",
                },
                new: {
                    label: "Nou Interessat",
                    ok: "Element creat",
                },
                delete: {
                    label: "Esborrar Interessat",
                    check: "Estàs segur que vols continuar amb aquesta acció?",
                    description: "Un cop esborrat no es podrà recuperar",
                    ok: "Element esborrat",
                },
                createRep: {
                    label: "Afegir Representant",
                    ok: "Element creat",
                },
                updateRep: {
                    label: "Modificar Representant",
                    ok: "Element modificat",
                },
                deleteRep: {
                    label: "Esborrar Representant",
                    check: "Estàs segur que vols continuar amb aquesta acció?",
                    description: "Un cop esborrat no es podrà recuperar",
                    ok: "Element esborrat",
                },
                importar: {
                    label: "Importar...",
                    title: "Importar interessats",
                    ok: "Interessats importats correctament",
                },
                exportar: {
                    label: "Exportar...",
                    ok: "Interessats exportats correctament",
                },
            },
            grid: {
                title: "Interessatd del fitxer",
                representant: "Representant",
            },
            alert: {
                incapacitat: "En cas de titular amb discapacitat es obligatori indicar un destinatari.",
            },
        },
        expedient: {
            title: "Expedient",
            filter: {
                title: "Cercador d'expedients"
            },
            detall: {
                title: "Informació de l'expedient",
                agafatPer: "Agafat per",
                avisos: "Avisos",
            },
            action: {
                new: {
                    label: "Nou expedient",
                    ok: "L'expedient '{{expedient}}' s'ha creat correctament.",
                },
                update: {
                    label: "Modificar...",
                    title: "Modificar Expedient",
                    ok: "L'expedient '{{expedient}}' s'ha modificat correctament.",
                },
                detall: {
                    label: "Gestionar",
                },
                agafar: {
                    label: "Agafar",
                    ok: "L'expedient '{{expedient}}' ha estat agafat per l'usuari '{{user}}'",
                },
                importar: {
                    label: "Importar expedient",
                    ok: "L'expedient s'ha importat correctament",
                },
                follow: {
                    label: "Seguir",
                    ok: "L'usuari '{{user}}' ha començat a seguir l'expedient '{{expedient}}'.",
                },
                unfollow: {
                    label: "Deixar de seguir",
                    ok: "L'usuari '{{user}}' ha deixat de seguir l'expedient '{{expedient}}'.",
                },
                retornar: {
                    label: "Retornar",
                    ok: "L'expedient '{{expedient}}' ha estat retornat a l'usuari '{{user}}'",
                },
                lliberar: {
                    label: "Alliberar",
                    ok: "L'expedient '{{expedient}}' ha estat alliberat",
                },
                eliminar: {
                    label: "Eliminar",
                    ok: "L'expedient '{{expedient}}' ha estat esborrat correctament",
                },
                close: {
                    label: "Tancar...",
                    title: "Tancar expedient",
                    ok: "L'expedient '{{expedient}}' ha estat tancat correctament",
                },
                open: {
                    label: "Reobrir",
                    description: "Voleu reobrir l'expedient?",
                    ok: "L'expedient '{{expedient}}' ha estat reobert correctament",
                },
                download: {
                    label: "Descarregar documents...",
                    title: "Selecció de documents",
                    ok: "Els documents s'han descarregat correctament",
                },
                exportFullCalcul: {
                    label: "Exportar full de càlcul",
                    ok: "El full de càlcul s'ha descarregat correctament",
                },
                exportZIP: {
                    label: "Exportar índex ZIP",
                    title: "Exportar documents a ZIP",
                    ok: "El document ZIP s'ha descarregat correctament",
                },
                exportPDF: {
                    label: "Exportar índex PDF",
                    ok: "El document PDF s'ha descarregat correctament",
                },
                exportCSV: {
                    label: "Exportar índex CSV",
                    ok: "L'índex CSV s'ha descarregat correctament",
                },
                exportEXCEL: {
                    label: "Exportar índex EXCEL",
                    ok: "L'índex EXCEL s'ha descarregat correctament",
                },
                exportPDF_ENI: {
                    label: "Índex PDF i exportació ENI",
                    ok: "El document s'ha descarregat correctament",
                },
                exportENI: {
                    label: "Exportació ENI",
                    ok: "El document ENI s'ha descarregat correctament",
                },
                exportINSIDE: {
                    label: "Exportació INSIDE",
                    ok: "El document INSIDE s'ha descarregat correctament",
                },
                export: {
                    label: "Exportar els documents...",
                    title: "Exportar documents",
                    ok: "Els documents s'han descarregat correctament",
                },
                sincronitzar: {
                    label: "Sincronitzar estat amb arxiu",
                    ok: "L'estat de l'arxiu s'ha sincronitzat",
                },

                changePrioritat: {
                    label: "Canviar prioritat...",
                    title: "Modificar prioritat de l'expedient",
                    ok: "L'expedient '{{expedient}}' s'ha modificat correctament.",
                },
                changeEstat: {
                    label: "Canviar estat...",
                    title: "Modificar estat de l'expedient",
                    ok: "L'expedient '{{expedient}}' s'ha modificat correctament.",
                },
                assignar: {
                    label: "Assignar",
                    title: "Assignar expedient a usuari",
                    ok: "L'expedient '{{expedient}}' s'ha assignat correctament.",
                },
                relacio: {
                    label: "Relacionar...",
                    title: "Relacionar expedient",
                    ok: "Les relacions de l'expedient '{{expedient}}' han canviat correctament.",
                },
                excelInteressats: {
                    title: "Exportar interessats a EXCEL",
                    ok: "Els interessats s'han exportat correctament"
                }
            },
            alert: {
                owner: "Cal reservar l'expedient per poder modificar-lo",
                alert: "Aquest expedient té alertes pendents de llegir",
                validation: "Aquest expedient té errors de validació",
                esborranys: "Existen documentos en estado borrador (B) que deben pasarse a definitivos o eliminarse del expediente si se quiere cerrar el expediente.\nEsta acción hará que los documentos pasen a formar parte del expediente definitivamente y no se podrán eliminar.",
                borradors: "Aquest expedient conté esborranys que s'eliminaran en tancar-lo. A continuació teniu l'opció de marcar els esborranys perquè siguin signats amb signatura de servidor abans del tancament de l'expedient i així evitar-ne l'eliminació. Si els documents contenen alguna signatura invàlida, aquestes s'eliminaran i es tornarà a signar el document al servidor.",
                notificacio: "Aquest expedient conté notificacions caducades no finalitzades. S'intentarà actualitzar-ne l'estat. Si arriba nova informació de les notificacions pendents, es desarà el certificat a l'Helium, però no a l'Arxiu digital.",
                documents: "Aquest expedient conté documents d'annexos d'anotacions amb errors. S'intentaran reprocessar en tancar-lo i, en cas que no sigui possible moure'ls, se'n desarà una còpia a l'Arxiu digital sense les signatures originals (tant el document original com la còpia es podran continuar consultant des de la pestanya de contingut de l'expedient).",
                errorEnviament: "Aquest expedient té enviaments amb errors",
                errorNotificacio: "Aquest expedient té notificacions amb errors",
                ambEnviamentsPendents: "Aquest expedient té enviaments pendents de Portafirmes",
                ambNotificacionsPendents: "Aquest expedient té notificacions pendents",
            },
            modal: {
                seguidors: "Seguidors del expedient",
            },
			results: {
                checkDelete: "Esteu segur que voleu esborrar aquest element?",
                checkRelacio: "Esteu segur que voleu esborrar aquesta relació?",
                actionOk: "L'acció s'ha executat correctament.",
				actionBackgroundOk: "L'acción s'ha preparat per la seva execució en segon plà. Pot consultar l'estat del procés des del llistat de execucions massives.",
			}
        },
        arxiu: {
            detall: {
                arxiuUuid: "Identificador de l'arxiu",
                fitxerNom: "Nom del fitxer",
                serie: "Sèrie documental",
                arxiuEstat: "Estat de l'arxiu",
                document: "Contingut del document",
                fitxerContentType: "Tipus MIME",
                metadata: "Metadades ENI",
                versions: "Versió",
                identificador: "Identificador",
                organ: "Òrgan",
                dataCaptura: "Data de captura",
                dataApertura: "Data d'obertura",
                origen: "Origen",
                estadoElaboracion: "Estat d'elaboració",
                tipoDocumental: "Tipus documental NTI",
                format: "Nom del format",
                clasificacion: "Classificació",
                estat: "Estat",
                interessats: "Interessats",
                firmes: "Tipus de firma",
                documentOrigen: "ID del document origen",
            },
            firma: {
                title: "Firma",
                perfil: "Perfil de firma",
                fitxerNom: "Nom del fitxer",
                tipusMime: "Tipus MIME",
                contingut: "CSV",
                csvRegulacio: "Regulació del CSV",
            },
            tabs: {
                resum: "Informació",
                fills: "Fills",
                firmes: "Firmes",
                data: "Metadades",
            },
        },
        document: {
            title: "Document",
            view: {
                title: "Tipus de vista",
                estat: "Vista per estat",
                tipus: "Vista per tipus de document",
                carpeta: "Vista per carpeta",
            },
            tabs: {
                resum: "Contingut",
                version: "Versions",
                file: "Fitxer",
                scaner: "Escaneig",
            },
            detall: {
                fitxerNom: "Nom del fitxer",
                fitxerContentType: "Tipus de contingut",
                metaDocument: "Tipus de document",
                createdDate: "Data de creació",
                estat: "Estat",
                dataCaptura: "Data de captura",
                origen: "Origen",
                tipoDocumental: "Tipus documental NTI",
                estadoElaboracion: "Estat d'elaboració",
                csv: "CSV",
                csvRegulacion: "Regulació del CSV",
                tipoFirma: "Tipus de firma",
                flux: "Existeix un flux de firma predefinit. La creació d’un nou flux de firma implica sobreescriure el seleccionat.",
                summarize: "Generar titol i descripció amb inteligència artificial.\n(Requereix haver adjuntat un document prèviament)",
                documentOrigenFormat: "Format: ES_<Òrgan>_<AAAA>_<ID_específic>",
                dataBasic: "Dades bàsiques",
                dataInteressat: "Dades de l'interessat",
                dataOther: "Altres dades",
            },
            action: {
                pinbal: {
                    label: "Consulta PINBAL...",
                    title: "Nova consulta PINBAL",
                    ok: "S'ha creat el document a partir de la consulta pinbal '{{codiServeiPinbal}}'",
                },
                import: {
                    label: "Importar documents...",
                    title: "Importació de documents des del SGD",
                    ok: "Documents importats correctament",
                },
                detall: {
                    label: "Detalls",
                },
                imprimible: {
                    label: "Versió imprimible",
                    ok: "La versió imprimible s'ha descarregat correctament",
                },
                original: {
                    label: "Descarregar original",
                    ok: "El document original s'ha descarregat correctament",
                },
                firma: {
                    label: "Descarregar signatura",
                    title: "Signar des del navegador",
                    ok: "Document signat correctament",
                },
                view: {
                    label: "Visualitzar",
                    title: "Visualitzar",
                },
                csv: {
                    label: "Copiar enllaç CSV",
                    ok: "Enllaç CSV copiat correctament",
                },
                portafirmes: {
                    label: "Enviar a portafirmes...",
                    title: "Enviar document a portafirmes",
                    ok: "Document '{{document}}' enviat a portafirmes",
                },
                toPDF: {
                    description: "Es canviarà el format del document abans d'enviar-lo al portafirmes",
                    title: "Visualitzar versió PDF",
                },
                firmar: {
                    label: "Signatura des del navegador...",
                },
                viaFirma: {
                    label: "Enviar viaFirma...",
                    title: "Enviar document a ViaFirma",
                    ok: "Document '{{document}}' enviat a viaFirma",
                },
                mail: {
                    label: "Enviar via email...",
                    title: "Enviar document per email",
                    ok: "Document '{{document}}' enviat via email",
                },
                seguiment: {
                    label: "Seguiment portafirmes",
                    title: "Detalls de la signatura",
                    ok: "La signatura ha estat cancel·lada correctament",
                },
                notificar: {
                    label: "Notificar o comunicar...",
                    title: "Crear notificació document",
                    ok: "Notificació creada correctament",
                },
                notificarMasiva: {
                    label: "Notificar o comunicar...",
                    title: "Generar document per notificar",
                    ok: "S'ha generat un zip dels elements seleccionats",
                },
                comunicar: {
                    label: "Comunicar...",
                },
                publicar: {
                    label: "Publicar...",
                    title: "Crear publicació",
                    ok: "Publicació creada correctament",
                },
                descarregarImprimible: {
                    label: "Descarregar versió imprimible",
                    ok: "La versió imprimible s'ha descarregat correctament",
                },
                changeType: {
                    label: "Canviar tipus...",
                    title: "Canviar tipus",
                    ok: "Document '{{document}}' modificat correctament",
                },
                definitive: {
                    label: "Convertir a definitiu",
                    description: "Aquesta acció farà que els documents passin a formar part de l'expedient de forma definitiva i no es podran eliminar.",
                    ok: "Document '{{document}}' canviat a definitiu",
                },
            },
            alert: {
                import: "Document importat",
                delete: "Document esborrat",
                firma: "Document firmat",
                original: "Aquest document contenia signatures invàlides i s'ha clonat i signat al servidor per poder desar-lo a l'Arxiu Digital. Es pot descarregar l'original des del menú d'accions",
                custodiar: "Document signat del portafirmes pendent de custodiar",
                moure: "El document de l'anotació està pendent de moure a la sèrie documental del procediment",
                definitiu: "Document definitiu",
                firmaPendent: "Pendent de firmar",
                firmaParcial: "Firmat parcialment",
                errorPortafirmes: "Error a l'enviar al portafirmes",
                funcionariHabilitatDigitalib: "Cal ser un funcionari habilitat a DIGITALIB",
                folder: "En cas de no seleccionar una carpeta, s'importaran els documents directament a l'expedient.",
                scaned: "El procés d'escaneig s'ha realitzat amb èxit.",
            },
            versio: {
                title: "Versió",
                data: "Data",
                arxiuUuid: "Arxiu UUID",
            },
        },
        carpeta: {
            title: 'Carpeta',
            action: {
                new: {
                    label: "Carpeta...",
                    ok: "Carpeta '{{carpeta}}' creada correctament",
                },
                update: {
                    label: "Modificar...",
                    ok: "Carpeta '{{carpeta}}' modificada correctament",
                },
                delete: {
                    label: "Esborrar...",
                    check: "Esteu segur que voleu continuar amb aquesta acció?",
                    description: "Un cop esborrat no es podrà recuperar",
                    ok: "Element esborrat",
                }
            }
        },
        dada: {
            title: "valor per al dada '{{metaDada}}'",
            grid: {
                valor: "Valor de la dada",
            },
        },
        metaDada: {
            title: "Tipus de dada",
            detail: "Valors de la dada '{{metaDada}}'",
        },
        registre: {
            grid: {
                extracte: "Extracte",
                origenRegistreNumero: "Número de registre",
                data: "Data de registre",
                dataRecepcio: "Data de recepció",
                destiDescripcio: "Destinació",
                interessats: "Interessats",
            },
            detall: {
                tipus: "Tipus",
                entrada: "Entrada",
                oficina: "Oficina",
                extracte: "Extracte",
                observacions: "Observacions",
                identificador: "Núm. origen",
                data: "Data d'origen",
                oficinaDescripcio: "Oficina d'origen",
                docFisica: "Documentació física",
                desti: "Òrgan de destinació",
                refExterna: "Ref. externa",
                expedientNumero: "Núm. expedient",
                procediment: "Procediment",
                llibre: "Llibre",
                assumpte: "Tipus d'assumpte",
                idioma: "Idioma",
                assumpteCodi: "Codi d'assumpte",
                transport: "Transport",
                transportNumero: "Núm. de transport",
                origenRegistreNumero: "Núm. d'origen",
                origenData: "Data d'origen",

                required: "Dades obligatòries",
                optional: "Dades opcionals",
                infoResumida: "Informació de registre resumida",
                interessats: "Interessats",
                annexos: "Annexos",
            },
            justificant: {
                ntiFechaCaptura: "Data de captura (ENI)",
                ntiOrigen: "Origen (ENI)",
                ntiTipoDocumental: "Tipus documental (ENI)",
                uuid: "Identificador",
                titol: "Fitxer",
                firmaTipus: "Tipus de signatura",
                firmaPerfil: "Perfil de signatura",
            }
        },
        notificacio: {
            title: "Notificació",
            tabs: {
                dades: "Dades",
                errors: "Errors",
            },
            detall: {
                title: "Detalls de la notificació",
                notificacioDades: "Dades de la notificació",
                notificacioDocument: "Document de la notificació",
                error: "S'han produït errors en enviar la notificació",

                emisor: "Emissor",
                assumpte: "Concepte",
                observacions: "Descripció",
                notificacioEstat: "Estat",
                createdDate: "Enviada el",
                processatData: "Finalitzada el",
                tipus: "Tipus",
                entregaPostal: "Entrega postal",
                fitxerNom: "Nom del fitxer",
                serveiTipusEnum: "Tipus de servei",
                notificacioIdentificador: "Identificador",
            },
            action: {
                actualitzarEstat: {
                    label: "Actualitzar estat",
                    ok: "L'estat ha estat actualitzat",
                },
                notificacioInteressat: {
                    label: "Enviaments",
                    title: "Enviaments",
                    ok: "",
                },
                justificant: {
                    label: "Justificant d'enviament",
                    ok: "",
                },
                documentEnviat: {
                    label: "Document enviat",
                    ok: "S'ha descarregat el document enviat",
                },
            },
        },
        notificacioInteressat: {
            tabs: {
                dades: "Dades",
                notif: "Notific@",
            },
            detall: {
                noEnviat: "No enviat a Notific@",
                title: "Detall de l’enviament",
                datat: "Datat",
                certificacio: "Certificació",
                enviament: "Dades de l'enviament",
                interessat: "Dades del titular",
                representant: "Dades del destinatari",

                enviamentCertificacioData: "Data",
                enviamentCertificacioOrigen: "Origen",
                enviamentReferencia: "Referència",
                entregaNif: "DEH NIF",
                classificacio: "DEH procediment",
                enviamentDatatEstat: "Estat",
            },
            action: {
                ampliarPlac: {
                    label: "Ampliar termini",
                    title: "Ampliació del termini dels enviaments de la remesa",
                    ok: "El termini ha estat ampliat",
                },
                certificat: {
                    label: "Certificació",
                    ok: "El certificat s'ha descarregat correctament",
                },
            },
        },
        publicacio: {
            detall: {
                title: "Detall de la publicació",
                document: "Document",
                enviatData: "'Data d'enviament'",
                estat: "Estat",
                tipus: "Tipus",
                assumpte: "Assumpte",
                observacions: "Observacions",
            },
            action: {
                update: "Modificar publicació",
                delete: {
                    title: "Esborrar publicació",
                    message: "Un cop esborrada la publicació no es podrà recuperar",
                }
            },
        },
        user: {
            options: {
                perfil: "El meu perfil",
                manual: "Manual d'usuari",
                manualAdmin: "Manual d'administrador",
                logout: "Desconnectar"
            },
            menu: {
                entitat: "Entitats",
                expedient: "Expedients",
                monitoritzar: "Monitoritzar",
                integracions: "Integracions",
                excepcions: "Excepcions",
                monitor: "Monitor del sistema",

                config: "Configuració",
                props: "Propietats configurables",
                pinbal: "Serveis PINBAL",
                segonPla: "Reiniciar tasques en segon pla",
                plugins: "Reiniciar connectors",
                avisos: "Avisos",

                anotacions: "Anotacions",
                procediments: "Procediments",
                procedimentsTitle: "L'entitat té procediments amb òrgans gestors no actualitzats",
                grups: "Grups",
                revisar: "Revisió de procediments",
                tasca: "Tasques",
                flux: "Fluxos de signatura",

                consultar: "Consultar",
                continguts: "Continguts",
                dadesEstadistiques: "Dades estadístiques",
                portafib: "Documents enviats a Portafib",
                notib: "Remeses enviades a Notib",
                pinbalEnviades: "Consultes enviades a PINBAL",
                assignacio: "Assignació de tasques",
                pendents: "Expedients pendents de distribució",
                comunicades: "Anotacions comunicades",

                documents: "Tipus de documents",
                nti: "Tipus documentals NTI",
                dominis: "Dominis",
                organs: "Òrgans gestors",
                url: "URLs d'instrucció",
                permisos: "Permisos de l'entitat"
            },
            massive: {
                title: "Acció massiva",
                portafirmes: "Enviar documents al portafirmes",
                firmar: "Signar documents des del navegador",
                marcar: "Marcar com a definitius",
                estat: "Canvi d'estat dels expedients",
                tancar: "Tancament d'expedients",
                custodiar: "Custodiar elements pendents",
                csv: "Copiar enllaç CSV",
                anexos: "Adjuntar annexos pendents d'anotacions acceptades",
                anotacio: "Actualitzar estat de les anotacions a Distribució",
                prioritat: "Canviar prioritat dels expedients",
            },
            action: {
                massives: {
                    label: "Consultar accions massives",
                    title: "Execucions massives de {{name}}",
                    detail: "Detall de l'acció massiva",
                },
            },
            perfil: {
                title: "El meu perfil",
                dades: "Dades d'usuari",
                correu: "Enviament de correus",
                generic: "Configuració genèrica",
                column: "Configuració de columnes del llistat d'expedients",
                vista: "Configuració vista de documents dels expedients",
                moure: "Configuració vista destí al moure documents"
            }
        },
        alert: {
            title: "Alertes d'expedient",
            acciones: {
                read: "Marcar com a llegida",
            },
            errors: {
                metaDada: "Falten les dades següents:",
                metaDocument: "Falten els documents següents:",
                metaNode: "Hi ha documents sense un tipus de document assignat",
                noFinalitzades: "Hi ha notificacions amb un estat que no és final",
                interessatObligatori: "Falta informar un interessat",
            },
        },
        notFound: "No trobat",
    }
};

export default translationCa;