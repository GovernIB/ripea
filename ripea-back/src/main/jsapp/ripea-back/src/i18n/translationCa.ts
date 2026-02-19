const translationCa = {
    common: {
        close: "Tanca",
        cancel: "Cancel·la",
        create: "Crea",
        copy: "Copiar",
        update: "Modifica",
        actualize: "Actualitza",
        save: "Guarda",
        delete: "Esborrar",
        accepta: "Accepta",
        rebutja: "Rebutja",
        action: "Accions",
        expand: "Expandir",
        contract: "Contreure",
        download: "Descarregar",
        send: "Envia",
        detail: "Detalls",
        refresh: "Refrescar",
        clear: "Netejar",
        back: "Tornar",
        search: "Filtrar",
        options: "Opcions",
        select: {
            all: "Seleccionar tots",
            clear: "Netejar selecció",
        },
        import: "Importa",
        export: "Exporta",
        consult: "Consulta",
        filter: "Filtrar",
        downloadSelected: "Descarrega contingut seleccionat",
        relateSelected: "Relaciona contingut seleccionat",
        processing: "Processant...",
        auditoria: {
            create: "Creat el {{createdDate}} per '{{createdBy}}'.",
            update: "Modificat el {{lastModifiedDate}} per '{{lastModifiedBy}}'.",
        },
        nouPermis: "Nou permís",
    },
    enum: {
        rol: {
            IPA_SUPER: "Superusuari",
            IPA_ADMIN: "Administrador d'entitat",
            IPA_ADMIN_LECTURA: "Administrador (lectura)",
            IPA_DISSENY: "Dissenyador d'òrgan gestor",
            IPA_ORGAN_ADMIN: "Administrador d'òrgan gestor",
            IPA_REVISIO: "Revisor de procediments",
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
            BAIXA: "Baixa",
            NORMAL: "Normal",
            ALTA: "Alta",
        },
        fluxTipus: {
            SIMPLE: "Simple",
            PORTAFIB: "Portafib",
        },
        estat: {
            TANCAT: "Tancat",
            OBERT: "Obert",
            ENVIAT: "Enviat",
            PAUSAT: "Pausat",
            INICIAT: "Iniciat",
            FIRMAT: "Firmat",
            REBUTJAT: "Rebutjat",
            PARCIAL: "Parcial",
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
        tipusDestinatari: {
            TABLET: "Tablet",
            EMAIL: "Email",
        },
        tipusVia: {
            ALAMEDA: "Alameda",
            AVENIDA: "Avinguda",
            BARRIO: "Barri",
            BULEVAR: "Bulevard",
            CALLE: "Carrer",
            CALLEJA: "Carreró",
            CAMINO: "Camí",
            CAMPO: "Camp",
            CARRERA: "Carrer",
            CARRETERA: "Carretera",
            CUESTA: "Pendent",
            EDIFICIO: "Edifici",
            ENPARANTZA: "Plaça",
            ESTRADA: "Estrada",
            GLORIETA: "Rotonda",
            JARDINES: "Jardins",
            OTROS: "Altres",
            PARQUE: "Parc",
            PASAJE: "Passatge",
            PASEO: "Passeig",
            PLAZA: "Plaça",
            PLAZUELA: "Placeta",
            POBLADO: "Poblat",
            POLIGONO: "Polígon",
            RAMBLA: "Rambla",
            RONDA: "Ronda",
            RUA: "Rua",
            SECTOR: "Sector",
            TRAVESIA: "Travessia",
            URBANIZACION: "Urbanització",
            VIA: "Via",
		},
        documentEnviamentEstat: {
            PENDENT: "Pendent",
            ENVIAT: "Enviat",
            PROCESSAT: "Processat",
            REBUTJAT: "Rebutjat",
            CANCELAT: "Cancelat",
        },
        tipusSequencia: {
            SERIE: "Serie",
            PARALEL: "Paralela",
        },
    },
    navigate: {
        expedient: "Cercador d'expedients",
        expedientPeticio: "Cercador d'anotacions de registre",
        usuariTasca: "Tasques",
        entitat: "Gestió d'entitats",
        avis: "Gestió d'avisos",
        massiu: {
            portafirmes: "Acció massiva: enviar documents al portafirmes",
            firmasimpleweb: "Acció massiva: firmar documents des del navegador",
            canviEstat: "Acció massiva: Canvi d'estat d'expedients",
            tancament: "Acció massiva: Tancament d'expedients",
            seguimentArxiuPendents: "Acció massiva: Custodiar elements pendents",
            csv: "Acció massiva: copiar enllaç CSV",
            definitiu: "Acció massiva: marcar documents com definitius",
            canviPrioritats: "Acció massiva: Canvi de prioritat d'expedients",
            expedientPeticioCanviEstatDistribucio: "Acció massiva: Actualitzar estat de les anotacions a Distribució",
            procesarAnnexosPendents: "Acció massiva: Adjuntar annexos pendents d'anotacions acceptades",
        },
    },
    page: {
        comment: {
            expedient: "Comentaris de l'expedient",
            tasca: "Comentaris de la tasca",
            metaExpedient: "Comentaris del procediment",
        },
        contingut: {
            grid: {
                nom: "Nom",
                path: "Ruta contingut",
            },
            detalle: {
                title: "Detalls del contingut",
                nom: "Nom",
                data: "Data",
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
                    ok: "Element '{{contingut}}' desat a l'arxiu",
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
                    label: "Informació arxiu",
                },
                importarExpedient: {
                    label: "Importar expedient relacionat...",
                    title: "Expedients relacionats",
                },
                seguimentPortafirmes: {
                    label: "Seguiment Portafirmes",
                    title: "Seguiment Portafirmes",
                },
                seguimentvf: {
                    label: "Seguiment Viafirma",
                    title: "Detalls de la firma",
                },
                custodiar: {
                    label: "Custodiar",
                },
                replay: {
                    label: "Recuperar",
                    ok: "El contingut s'ha recuperat correctament",
                    massiveOk: "Els continguts s'han recuperat correctament",
                },
                delete: {
                    ok: "El contingut s'ha eliminat correctament",
                    massiveOk: "Els continguts s'han eliminat correctament",
                },
            },
            history: {
                create: "Creació",
                update: "Darrera modificació",
                user: "Usuari",
                date: "Data",
            },
            alert: {
                valid: "Aquest contingut té errors de validació",
                metaNode: "Aquest document no té assignat un tipus de document",
                guardarPendent: "Pendent de guardar a l'arxiu",
                fileSize: "La mida màxima permesa per al fitxer és de {{maxSize}}",
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
                    label: "Modificar...",
                    title: "Canviar procediment",
                    ok: "L'anotació {{data.identificador}} s'ha modificat correctament",
                },
                canviEstatDistribucio: {
                    label: "Canviar estat a distribució",
                    ok: "L'estat ha canviat correctament",
                    massiveOk: "S'han actualitzat l'estat de '{{data.num}}' anotacions",
                },
                descargarAnnex: {
                    label: "Descarregar annex",
                    ok: "Annex descarregat correctament",
                },
                procesarAnnexosPendents: {
                    label: "Adjuntar",
                    ok: "El annex s'ha processat correctament",
                    massiveOk: "S'han processat '{{data.num}}' annexos",
                    info: "Si s'ha produït algun error al acceptar una anotació des de la pantalla Anotacions, de manera que algun dels documents de l'anotació no s'han adjuntat a l'expedient, des d'aquest llistat podrà tornar a intentar adjuntar el document a l'expedient.",
                },
                firma: {
                    label: "Signatures",
                    title: "Signatures",
                },
                consultar: {
                    label: "Consultar",
                    ok: "L'anotació s'ha consultat y guardat correctament",
                    massiveOk: "S'ha consultat y guardat correctament {{data.num}} annotations",
                },
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
                duracio: "Durada",
                dataLimit: "Data límit",
                estat: "Estat",
                prioritat: "Prioritat",
            },
            action: {
                new: {
                    label: "Nova tasca",
                    ok: "La tasca {{data.titol}} s'ha creat correctament",
                },
                tramitar: {
                    label: "Tramitar",
                },
                iniciar: {
                    label: "Iniciar",
                    ok: "La tasca s'ha iniciat correctament",
                },
                rebutjar: {
                    label: "Rebutjar...",
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
                    label: "Reassignar...",
                    title: "Reassignar tasca",
                    ok: "La tasca s'ha reassignat correctament",
                },
                delegar: {
                    label: "Delegar...",
                    title: "Delegar tasca",
                    ok: "La tasca s'ha delegat correctament",
                },
                retomar: {
                    label: "Cancel·lar delegació",
                    title: "Cancel·lar delegació de tasca",
                    ok: "La delegació de la tasca s'ha cancel·lat correctament",
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
                    label: "Reobrir...",
                    title: "Reobrir tasca",
                    ok: "La tasca s'ha reobert correctament",
                },
                comment: {
                    ok: "Comentari afegit a la tasca '{{data.expedientTasca.description}}'",
                },
            },
        },
        interessat: {
            title: "Interessat",
            rep: "Representant",
            detall: {
				tipus: "Tipus",
                nif: "NIF/CIF/NIE",
                nom: "Nom",
                raoSocial: "Raó social",
                llinatges: "Cognoms",
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
                    ok: "L'interessat {{data.documentNum}} s'ha creat correctament",
                },
                update: {
                    ok: "L'interessat {{data.documentNum}} s'ha modificat correctament",
                },
                delete: {
                    label: "Esborrar Interessat",
                    check: "Esteu segur que voleu continuar amb aquesta acció?",
                    description: "Un cop esborrat no es podrà recuperar",
                    ok: "L'interessat {{data.documentNum}} s'ha esborrat correctament",
                },
                createRep: {
                    label: "Afegir Representant",
                    ok: "El representant {{data.documentNum}} s'ha creat correctament",
                },
                updateRep: {
                    label: "Modificar Representant",
                    ok: "El representant {{data.documentNum}} s'ha modificat correctament",
                },
                deleteRep: {
                    label: "Esborrar Representant",
                    check: "Esteu segur que voleu continuar amb aquesta acció?",
                    description: "Un cop esborrat no es podrà recuperar",
                    ok: "El representant {{data.documentNum}} s'ha esborrat correctament",
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
                importSGD: {
                    label: "Importar interessats des de Registre...",
                    title: "Importar interessats des de Registre",
                    ok: "Interessats importats correctament",
                },
				gestGrups: {
				    label: "Gestionar grups",
					title: "Gestionar grups",
				    ok: "Grups modificats correctament",
				},
            },
            grid: {
                title: "Interessats del fitxer",
                representant: "Representant",
				tipus: {
					label: "Tipus",
					personaFisica: "Persona física",
					personaJuridica: "Persona jurídica",
					administrador: "Administrador",
				},
            },
            alert: {
                incapacitat: "En cas de titular amb discapacitat és obligatori indicar un destinatari.",
                jaExistentExpedient: "Ja existeix a l'expedient",
            },
			grup: {
				title: "Grups d'interessats",
				action: {
					new: {
						ok: "Grup creat correctament",
					},
					update: {
						ok: "Grup modificat correctament",
					},
					delete: {
					    label: "Esborrar Grup",
					    check: "Esteu segur que voleu continuar amb aquesta acció?",
					    description: "Un cop esborrat no es podrà recuperar",
					    ok: "El grup {{data.nom}} s'ha esborrat correctament",
					},                 
				},
			},
        },
        expedient: {
            title: "Expedient",
            filter: {
                title: "Cercador d’expedients"
            },
            detall: {
                title: "Informació de l’expedient",
                agafatPer: "Agafat per",
                avisos: "Avisos",
            },
            action: {
                new: {
                    label: "Nou expedient",
                    title: "Crear nou expedient",
                    ok: "L’expedient '{{data.nom}}' s’ha creat correctament.",
                },
                update: {
                    label: "Modificar...",
                    title: "Modificar expedient",
                    ok: "L’expedient '{{data.nom}}' s’ha modificat correctament.",
                },
                detall: {
                    label: "Gestionar",
                },
                importar: {
                    label: "Importar expedient",
                    ok: "L’expedient s’ha importat correctament",
                },
                agafar: {
                    label: "Agafar",
                    ok: "L’expedient '{{expedient}}' ha estat agafat per l’usuari '{{user}}'",
                },
                follow: {
                    label: "Seguir",
                    ok: "L’usuari '{{user}}' ha començat a seguir l’expedient '{{expedient}}'.",
                },
                unfollow: {
                    label: "Deixar de seguir",
                    ok: "L’usuari '{{user}}' ha deixat de seguir l’expedient '{{expedient}}'.",
                },
                retornar: {
                    label: "Tornar",
                    ok: "L’expedient '{{expedient}}' ha estat tornat a l’usuari '{{user}}'",
                },
                lliberar: {
                    label: "Alliberar",
                    ok: "L’expedient '{{expedient}}' ha estat alliberat",
                },
                eliminar: {
                    label: "Eliminar",
                    ok: "L’expedient '{{data.nom}}' ha estat eliminat correctament",
                },
                close: {
                    label: "Tancar...",
                    title: "Tancar expedient",
                    ok: "L’expedient '{{expedient}}' ha estat tancat correctament",
                },
                open: {
                    label: "Reobrir",
                    description: "Voleu reobrir l’expedient?",
                    ok: "L’expedient '{{expedient}}' ha estat reobert correctament",
                },
                download: {
                    label: "Descarregar documents...",
                    title: "Selecció de documents",
                    ok: "Els documents s’han descarregat correctament",
                },
                exportFullCalcul: {
                    label: "Exportar full de càlcul",
                    ok: "El full de càlcul s’ha descarregat correctament",
                },
                exportZIP: {
                    label: "Exportar índex ZIP",
                    title: "Exportar documents a ZIP",
                    ok: "El document ZIP s’ha descarregat correctament",
                },
                exportPDF: {
                    label: "Exportar índex PDF",
                    ok: "El document PDF s’ha descarregat correctament",
                },
                exportCSV: {
                    label: "Exportar CSV",
                    ok: "L’índex CSV s’ha descarregat correctament",
                },
                exportEXCEL: {
                    label: "Exportar índex EXCEL",
                    ok: "L’índex EXCEL s’ha descarregat correctament",
                },
                exportPDF_ENI: {
                    label: "Índex PDF i exportació ENI",
                    ok: "El document s’ha descarregat correctament",
                },
                exportENI: {
                    label: "Exportació ENI",
                    ok: "El document ENI s’ha descarregat correctament",
                },
                exportINSIDE: {
                    label: "Exportació INSIDE",
                    ok: "El document INSIDE s’ha descarregat correctament",
                },
                exportDocs: {
                    label: "Exportar els documents dels expedients seleccionats",
                    ok: "Els documents s’han exportat correctament",
                },
                export: {
                    label: "Exportar els documents...",
                    title: "Exportar documents",
                    ok: "Els documents s’han descarregat correctament",
                },
                sincronitzar: {
                    label: "Sincronitzar estat amb arxiu",
                    ok: "L’estat de l’arxiu s’ha sincronitzat",
                },
                changePrioritat: {
                    label: "Canviar prioritat...",
                    title: "Modificar prioritat de l’expedient",
                    ok: "La prioritat de l’expedient '{{expedient}}' s’ha modificat correctament.",
                    massiveOk: "S'han cambiat la prioritat de '{{data.num}}' expedients",
                },
                changeEstat: {
                    label: "Canviar estat...",
                    title: "Modificar estat de l’expedient",
                    ok: "L’estat de l’expedient '{{expedient}}' s’ha modificat correctament.",
                    massiveOk: "S'ha cambiat l'estat a '{{data.num}}' expedients"
                },
                assignar: {
                    label: "Assignar...",
                    title: "Assignar expedient a usuari",
                    ok: "L’expedient '{{expedient}}' s’ha assignat correctament.",
                },
                relacio: {
                    label: "Relacionar...",
                    title: "Relacionar expedient",
                    ok: "Les relacions de l’expedient '{{expedient}}' han canviat correctament.",
                    labelDialog: "Relacionar",
                },
                eliminarRelacio: {
                    label: "Eliminar relació",
                    ok: "La relació entre els 2 expedients s’ha eliminat correctament.",
                },
                excelInteressats: {
                    title: "Descarregar plantilla per importar interessats Excel",
                    ok: "Els interessats s’han exportat correctament",
                },
                impDocMass: {
                    label: "Importar documents als expedients seleccionats",
                    title: "Importar documents a expedients",
                    warning: "Els expedients han de pertànyer al mateix procediment.",
                },
                comment: {
                    ok: "Comentari afegit a l'expedient '{{data.expedient.description}}'",
                },
				moureTot: {
					label: "Moure tot...",
				    title: "Moure tot a l'expedient destí",
				    ok: "L'acció massiva per moure l'expedient '{{expedient}}' s'ha creat correctament.",
	            },
            },
            alert: {
                owner: "És necessari reservar l’expedient per poder-lo modificar",
                alert: "Aquest expedient té alertes pendents de llegir",
                validation: "Aquest expedient té errors de validació",
                esborranys: "Hi ha documents en estat esborrany (B) que s’han de passar a definitius o eliminar-se si es vol tancar l’expedient.\nAquesta acció farà que els documents formin part de l’expedient definitivament i no es podran eliminar.",
                borradors: "Aquest expedient conté esborranys que s’eliminaran en tancar-lo. Pot marcar-los per signar-los amb signatura de servidor i evitar-ne l’eliminació. Les firmes no vàlides seran eliminades i es tornaran a signar.",
                notificacio: "Aquest expedient conté notificacions caducades no finalitzades. Es provarà d’actualitzar-ne l’estat. Les noves dades es desaran a RIPEA, però no a l’arxiu digital.",
                documents: "Aquest expedient conté documents d’anotacions amb errors. Es provaran de reprocesar i, si no és possible, es guardarà una còpia sense signatures originals a l’arxiu digital.",
                errorEnviament: "Aquest expedient té enviaments amb errors",
                errorNotificacio: "Aquest expedient té notificacions amb errors",
                ambEnviamentsPendents: "Aquest expedient té enviaments pendents de Portasignatures",
                ambNotificacionsPendents: "Aquest expedient té notificacions pendents",
                canviEstat: "És necessari seleccionar un procediment per poder realitzar l'acció massiva",
				moureTot: {
					info: "L'expedient es troba actualment bloquejat a causa d'una execució en segon pla en curs.\nFins que aquesta finalitzi, no serà possible realitzar modificacions. Consulteu les accions massives pendents per conèixer el seu estat.",
				  	title: "Estàs a punt d'iniciar una acció massiva que mourà la informació següent cap a l'expedient destí:",
				  	items: [
				    	"Els documents i carpetes",
						"Els interessats",
				    	"Els seguidors",
				    	"Els expedients relacionats",
				    	"Les anotacions de registre",
				    	"Els comentaris"
				  ],
				},
            },
            modal: {
                seguidors: "Seguidors de l’expedient",
            },
            results: {
                checkDelete: "Estau segur que voleu eliminar aquest contingut? Si contenia firmes en curs, seràn cancelades.",
                checkRelacio: "Estau segur que voleu eliminar aquesta relació?",
                actionOk: "L’acció s’ha executat correctament.",
                actionBackgroundOk: "L’acció s’ha preparat per executar-se en segon pla. Podeu consultar-ne l’estat al llistat d’accions massives.",
            }
        },
        arxiu: {
            detall: {
                arxiuUuid: "Identificador a l'arxiu",
                fitxerNom: "Nom del document",
                serie: "Sèrie documental",
                arxiuEstat: "Estat a l'arxiu",
                document: "Contingut del document",
                fitxerContentType: "Tipus MIME",
                metadata: "Metadades ENI",
                versions: "Versió",
                identificador: "Identificador",
                organ: "Òrgan",
                dataCaptura: "Data de captura",
                dataApertura: "Data d'obertura",
                dataTancament: "Data de tancament",
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
                responsableNom: "Nom del responsable",
                responsableNif: "Nif del responsable",
                data: "Data de la signatura",
                emissorCertificat: "Emissor del certificat",
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
                firmes: "Signatures",
            },
            detall: {
                fitxerNom: "Nom del fitxer",
                fitxerContentType: "Tipus de contingut",
                metaDocument: "Tipus de document",
                createdDate: "Data de creació",
                createdBy: "Creat per",
                estat: "Estat",
                dataCaptura: "Data de captura",
                origen: "Origen",
                tipoDocumental: "Tipus documental NTI",
                estadoElaboracion: "Estat d'elaboració",
                csv: "CSV",
                csvRegulacion: "Regulació del CSV",
                tipoFirma: "Tipus de signatura",
                flux: "Existeix un flux de signatura predefinit. La creació d'un nou flux implica sobreescriure el seleccionat.",
                summarize: "Generar títol i descripció amb intel·ligència artificial.\n(Requereix haver adjuntat un document prèviament)",
                documentOrigenFormat: "Format: ES_<Òrgan>_<AAAA>_<ID_específic>",
                dataBasic: "Dades bàsiques",
                dataInteressat: "Dades interessat",
                dataEspecific: "Dades específiques",
                dadesRegistrals: "Dades registrals",
                fetRegistral: "Fet registral",
                naixement: "Naixement",
                dadesAdicionals: "Dades addicionals",
                dataOther: "Altres dades",
                senseTipus: 'Sense tipus assignat',
                extensio: "Extensio",
                ruta: "Ruta",
                mida: "Mida",
            },
            action: {
                new: {
                    ok: "El document {{data.nom}} s'ha creat correctament"
                },
                update: {
                    ok: "El document {{data.nom}} s'ha modificat correctament"
                },
                delete: {
                    label: "Esborrar",
                    check: "Estau segur que voleu eliminar aquest contingut? Si contenia firma en curs, serà cancelada.",
                    description: "Un cop esborrat no es podrà recuperar",
                    ok: "El document {{data.nom}} s'ha eliminat correctament"
                },
                pinbal: {
                    label: "Consulta PINBAL...",
                    title: "Nova consulta PINBAL",
                    ok: "S'ha creat el document a partir de la consulta pinbal '{{codiServeiPinbal}}'",
                },
				import: {
					close: {
						check: "Estau segur que voleu tancar aquesta finestra?",
						description: "L'importació continuarà en segon pla i podreu consultar el resultat a l'expedient més tard.",
					},
					cancel: {
						check: "Esteu segur que voleu cancel·lar la importació?",
						description: "Els documents importats fins a aquest moment es conservaran a l’expedient.",
					},
				},
                importSgd: {
                    label: "Importar documents SGD...",
                    title: "Importació de documents des del SGD",
                    ok: "Documents importats correctament",
					interessats: "Selecciona els interessats que desitgi associar a l'expedient",
					interessat: {
						tipus: {
							1: "Administrador",
							2: "Persona física",
							3: "Persona jurídica",
						},
					},
					resultat: {
						title: "Resultat:",
						ok: "Procés importació completat",
						documents: "Documents processats correctament: ",
						interessats: "Interessats processats correctament: ",
						carpetes: "Carpetes creades correctament: ",
						errors: "Errors detallats: ",
					}
                },
                importZip: {
                    label: "Importar des de ZIP...",
                    title: "Importació de documents des d'un ZIP",
                    ok: "Documents importats correctament",
					resultat: {
						title: "Resultat:",
						ok: "Procés importació completat",
						documents: {
							ok: "Documents processats correctament: ",
							ko: "Documents amb error: ",
							firma: "Documents amb error de firma: ",
						},
						carpetes: {
							ok: "Carpetes creades correctament: ",
						},
						tamany: "Tamany total processat: ",
						errors: "Errors detallats: ",
					}
                },
                detall: {
                    label: "Detalls",
                },
                imprimible: {
                    label: "Còpia autèntica imprimible",
                    ok: "La còpia autèntica imprimible s'ha descarregat correctament",
                },
                original: {
                    label: "Descarregar original",
                    ok: "El document original s'ha descarregat correctament",
                },
                download: {
                    ok: "Document descarregat correctament",
                },
                firma: {
                    button: "Iniciar procés de firma",
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
                    button: "Envia a portafirmes",
                    label: "Enviar a portafirmes...",
                    title: "Enviar document a portafirmes",
                    ok: "Document '{{document}}' enviat a portafirmes",
                },
                toPDF: {
                    description: "Es canviarà el format del document abans d'enviar-lo al portafirmes",
                    title: "Visualitzar versió PDF",
                },
                firmar: {
                    button: "Inicia procés de firma",
                    label: "Signatura des del navegador...",
                },
                viaFirma: {
                    button: "Envia a ViaFirma",
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
                    cancel: "Cancel·la enviament",
                    title: "Detalls de la firma",
                    ok: "La signatura ha estat cancel·lada correctament",
                    check: "Confirmau l'acció",
                    description: "Segur que voleu cancel·lar la firma actualment en procés?",
                },
                cancel: {
                    label: "Cancel·la enviament",
                },
                notificar: {
                    button: "Notifica",
                    label: "Notificar o comunicar...",
                    title: "Crear notificació document",
                    ok: "Notificació creada correctament",
                    alert: {
                        interessatsAmbAvis: {
                            title: "Hi ha notificacions amb destinatari sense NIF/NIE. Aquestes notficacions no es poden enviar a la carpeta ciutadana, degut a que és necessari un NIF o NIE per a accedir-hi.",
                            description: "Els notificacions sense NIF/NIE són els següents:",
                            marcat: {
                                title: "Si ha marcat entrega postal:",
                                description: "La notificació s'enviarà per correu postal, sempre que l'òrgan gestor tengui un CIE (Centre de Impressió i Ensobrat) definit.",
                            },
                            noMarcat: {
                                title: "Si NO ha seleccionat entrega postal:",
                                description: "La notificació telemàtica no es realitzarà. En el seu lloc s'enviarà un correu electrònic d'avís informant al titular que en breu rebrà una notificació per correu postal.",
                                warning: "És necessari que feu la notificació en Paper."
                            },
                        },
                        administracioSir: {
                            title: "Uno de los interesados seleccionados es una administración SIR.",
                            warning: "TODOS los envíos se realizarán de tipo COMUNICACIÓN",
                        },
                    }
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
                descarregarOriginal: {
                    label: "Document original",
                    ok: "El document original s'ha descarregat correctament",
                },
                descarregarImprimible: {
                    label: "Descarregar còpia auténtica imprimible",
                    ok: "La còpia auténtica imprimible s'ha descarregat correctament",
                },
                changeType: {
                    label: "Canviar tipus...",
                    title: "Canviar tipus",
                    ok: "Els documents s'han modificat correctament",
                },
                definitive: {
                    label: "Convertir a definitiu",
                    description: "Aquesta acció farà que els documents passin a formar part de l'expedient de forma definitiva i no es podran eliminar.",
                    ok: "Document '{{document}}' canviat a definitiu",
                    massiveOk: "S'han marcat com definitius '{{data.num}}' documents",
                },
            },
            alert: {
                import: "Document importat",
                delete: "Document esborrany",
                firma: "Document signat",
                original: "Aquest document contenia signatures invàlides i s'ha clonat i signat en servidor per poder guardar-lo a l'Arxiu Digital. Es pot descarregar l'original des del menú d'accions",
                custodiar: "Pendent de custodiar document signat de portafirmes",
                moure: "El document de l'anotació està pendent de moure a la sèrie documental del procediment",
                definitiu: "Document definitiu",
                firmaPendent: "Pendent de signar",
                firmaParcial: "Signat parcialment",
                errorPortafirmes: "Error en enviar al portafirmes",
                funcionariHabilitatDigitalib: "És necessari ser un funcionari habilitat a DIGITALIB",
                folder: "En cas de no seleccionar una carpeta s'importaran els documents directament a l'expedient.",
                scaned: "El procés d'escaneig s'ha realitzat amb èxit.",
                view: "Nomes per PDF, ODT i DOCX",
                portafirmes: "És necessari seleccionar un procediment i un tipus de document per poder realitzar l'acció massiva",
                documentsZip: "S'ha de seleccionar com a mínim un document per fer l'importació",
            },
            versio: {
                title: "Versió",
                data: "Data",
                arxiuUuid: "Arxiu UUID",
            },
        },
        carpeta: {
            title: "Carpeta",
            detail: {
                tite: "Detall de la carpeta",
            },
            action: {
                new: {
                    label: "Carpeta...",
                    ok: "Carpeta '{{data.nom}}' creada correctament",
                },
                update: {
                    label: "Modificar...",
                    title: "Modificar carpeta",
                    ok: "Carpeta '{{data.nom}}' modificada correctament",
                },
                delete: {
                    label: "Esborrar...",
                    check: "Està segur que vol continuar amb aquesta acció?",
                    description: "Un cop esborrada no es podrà recuperar",
                    ok: "Carpeta '{{data.nom}}' eliminada correctament",
                }
            },
			restriccions: {
				 title: "Selecciona els usuaris que tindran accés a la carpeta (d’entre els que ja tenen accés al procediment)",
			     notEmpty: {
				 		message: "S'ha de seleccionar com a mínim un usuari per crear la restricció"	
				 }
			}
        },
        dada: {
            title: "valor per a la dada '{{metaDada}}'",
            noRowsText: "No hi ha valors per a aquesta dada",
            grid: {
                valor: "Valor de la dada",
            },
            mensajeToolbar: {
                permis: "No teniu permisos per gestionar els valors d'aquesta dada.",
                maxDades: "Aquest tipus de dada només permet indicar un únic valor.",
            },
            action: {
                new: {
                    label: "Afegir valor per la dada",
                    ok: "La dada {{data.valor}} s'ha creat correctament",
                },
                update: {
                    ok: "La dada {{data.valor}} s'ha modificat correctament",
                },
                delete: {
                    ok: "La dada {{data.valor}} s'ha eliminat correctament",
                },
            },
        },
        metaDada: {
            title: "Meta-dada",
            plural: "Meta-dades",
            detail: {
                title: "Detall de la meta-dada",
                value: "Valors de la meta-dada '{{metaDada}}'",
            },
            action: {
                activar: {
                    label: "Activar",
                    ok: "Meta-dada activada",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Meta-dada desactivada",
                },
                new: {
                    label: "Nova metadada",
                    ok: "Meta-dada creada correctament",
                },
                update: {
                    ok: "Meta-dada modificada correctament",
                },
                delete: {
                    ok: "Meta-dada eliminada correctament",
                },
            },
        },
        registre: {
            grid: {
                extracte: "Extracte",
                nomAnnex: "Nom de l'annex",
                origenRegistreNumero: "Número de registre",
                data: "Data de registre",
                dataRecepcio: "Data de recepció",
                destiDescripcio: "Destinació",
                interessats: "Interessats",
                dataExpedient: "Expedient creat el",
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
                entregaPostal: "Lliurament postal",
                fitxerNom: "Nom de l'arxiu",
                serveiTipusEnum: "Tipus de servei",
                notificacioIdentificador: "Identificador",
                estatError: "Error processant la notificació dins de Notib",
            },
            action: {
                update: {
                    ok: "La remesa {{data.assumpte}} s'ha modificat correctament",
                },
                actualitzarEstat: {
                    label: "Actualitzar estat",
                    ok: "L'estat s'ha actualitzat correctament",
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
                    label: "Ampliar plaç",
                    title: "Ampliació del plaç dels enviaments de la remesa",
                    ok: "El plaç del enviament ha estat ampliat",
                },
                certificat: {
                    label: "Certificació",
                    ok: "El certificat s'ha descarregat correctament",
                },
            },
        },
        publicacio: {
            title: "Publicació",
            detall: {
                title: "Detall de la publicació",
                document: "Document",
                enviatData: "Data d'enviament",
                estat: "Estat",
                tipus: "Tipus",
                assumpte: "Assumpte",
                observacions: "Observacions",
            },
            action: {
                update: {
                    ok: "La publicació {{data.assumpte}} s'ha modificat correctament",
                },
                delete: {
                    ok: "La publicació {{data.assumpte}} s'ha eliminat correctament",
                }
            },
        },
        documentPortafirmes: {
            detall: {
                assumpte: "Assumpte",
                enviatData: "Data d'enviament",
                estat: "Estat",
                prioritat: "Prioritat",
                documentTipusNom: "Tipus de document",
                fluxTipus: "Tipus de flux",
                responsables: "Responsables",
                sequenciaTipus: "Tipus sequencia firma",
                portafirmesId: "ID Portafirmes",
            },
        },
        documentVia: {
            detall: {
                document: "Document",
                titol: "Títol notificació",
                descripcio: "Descripció notificació",
                enviatData: "Data d'enviament",
                estat: "Estat",
                tipusDestinatari: "Tipus de destinatari",
                codiUsuari: "Usuari viaFirma",
                signantEmail: "Email destinatari",
                messageCode: "Codi missatge viaFirma",
                intentData: "Data darrer intent",
                intentNum: "Número reintents",
            },
            tabs: {
                dades: "Dades",
                errors: "Errors",
            },
            alert: {
                reintentar: "Reintentar enviament",
                enviament: "S'han produit errors al enviar el document a viaFirma",
                processament: "S'han produit errors processant la firma del document",
                cancelat: "S'ha cancelat la firma",
            },
        },
        grup: {
            title: "Grup",
            detail: {
                title: "Detall del grup",
            },
            grid: {
                default: "Per defecte",
            },
            action: {
                new: {
                    label: "Nou Grup",
                    ok: "Grup '{{data.codi}}' creat correctament",
                },
                update: {
                    ok: "Grup '{{data.codi}}' modificat correctament",
                },
                delete: {
                    ok: "Grup '{{data.codi}}' esborrat correctament",
                },
                link: {
                    label: "Víncular grup",
                    title: "Víncular grup",
                    ok: "Grup vinculat",
                },
                unlink: {
                    label: "Desvincular",
                    ok: "Grup desvinculat",
                },
                default: {
                    label: "Marcar per defecte",
                    ok: "Grup marcat com defecte",
                },
                undefault: {
                    label: "Llevar per defecte",
                    ok: "Grup desmarcat com defecte",
                },
            },
        },
        organGestor: {
            title: "Òrgan Gestor",
            action: {
                update: {
                    ok: "Òrgan Gestor '{{data.codi}}' esborrat correctament",
                },
                actualitzar: {
                    title: "Predicció de sincronització",
                    label: "Actualitzar òrgans gestors de DIR3",
                    ok: "Els òrgans estan actualitzats",
                    button: "Sincronitza",
                    tabs: {
                        empty: "Els òrgans estan actualitzats",
                        firstSync: "Primera sincronització",
                        split: "Divisions",
                        merge: "Fusions",
                        subst: "Substitucions",
                        change: "Canvis en atributs",
                        new: "Nous",
                        del: "Extingides",
                    },
                },
                vista: "Canvi vista",
                pdf: "Descarrega PDF",
            },
        },
        tipusDocumental: {
            title: "Tipus documental",
            action: {
                new: {
                    label: "Afegir tipus documental",
                    ok: "Tipus documental '{{data.codi}}' creat correctament",
                },
                update: {
                    ok: "Tipus documental '{{data.codi}}' modificat correctament",
                },
                delete: {
                    ok: "Tipus documental '{{data.codi}}' esborrat correctament",
                },
            },
        },
        metaExpedient: {
            title: "Procediment",
            detall: {
                elementsProc: "Gestió del procediment: {{nom}}",
                elementsServ: "Gestió del servei: {{nom}}",
                expressioNumero: "Si no s'especifica cap expressió s'utilitzarà aquesta per defecte: {{codi}}/{{seq}}/{{any}}",
                permisDirecte: "Un usuari administrador de l'entitat pot modificar aquest valor.",
                responsable: "Podeu canviar el responsable de firma",
                portafirmesResponsables: "Podeu canviar els responsables de firma",
                regla: {
                    create: "Creada",
                    data: "Data creació",
                    activa: "Activa",
                    nom: "Nom",
                },
            },
            tabs: {
                dades: "Dades",
                estat: "Estat revisió",

                metaDocument: "Tipus de doc.",
                metaDada: "Meta-Dades",
                expedientEstat: "Estats",
                tasca: "Tasques",
                grup: "Grups",
                carpeta: "Carpetes",
            },
            action: {
                new: {
                    label: "Nou procediment",
                    ok: "Procediment creat correctament",
                },
                update: {
                    ok: "Procediment modificat correctament",
                },
                delete: {
                    ok: "Procediment eliminat correctament",
                },
                consultar: {
                    title: "Detall del procediment",
                    label: "Consultar",
                    revisat: "Aquest procediment no es pot modificar ja que es troba en estat revisat",
                },
                canviEstat: {
                    title: "Canviar estat de revisó",
                    label: "Canviar l'estat de revisó",
                    ok: "",
                },
                expedient: {
                    title: "Expedients del procediment: {{nom}}",
                    label: "Expedients",
                },
                regla: {
                    title: "Estat de la regla en Distribució",
                    label: "Regla distribució",
                    create: {
                        label: "Crear regla en Distribució",
                        ok: "La regla amb codi '{{nom}}' s'ha creat correctament.",
                    },
                    active: {
                        label: "Activar regla en Distribució",
                        ok: "La regla amb codi '{{nom}}' s'ha activat correctament.",
                    },
                    desactive: {
                        label: "Desactivar regla en Distribució",
                        ok: "La regla amb codi '{{nom}}' s'ha desactivat correctament",
                    },
                },
                activar: {
                    label: "Activar",
                    ok: "Procediment activat correctament",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Procediment desactivat correctament",
                },
                comment: {
                    ok: "Comentari afegit al procediment '{{data.metaExpedient.description}}'",
                },
                importRolsac: {
                    title: "Importar procediment des de ROLSAC",
                    label: "Importar des de ROLSAC",
                },
                importFitxer: {
                    title: "Importar procediment",
                    label: "Importar des de fitxer",
                    ok: "Procediment importat correctament",
                },
                export: {
                    ok: "Procediment exportat correctament",
                },
                canviPendent: {
                    label: "Marcar com a pendent de revisió",
                    ok: "Procediment marcat com a pendent de revisió",
                },
                canviDisseny: {
                    label: "Marcar com a procés de disseny",
                    ok: "Procediment marcat com a procés de disseny",
                },                
                actualize: {
                    title: "Actualització de procediments",
                    label: "Actualitzar desde ROLSAC",
                    description: "Vols actualitzar els procediments amb la informació de ROLSAC?",
                    ok: "Procediments actualitzats",
                    result: {
                        title: "Inici de procés d'actualització dels procediments",
                        description: "S'han realitzat '{{numOperacions}}' peticions, s'han modificat '{{numActualitzats}}' procediments, i '{{numErrord}}' han donat error",
                        senseCanvi: "Sense canvis",
                    }
                }
            },
            alert: {
                pendentsRevisio: "Hi ha {{num}} procedimientos o servicios pendents de revisar",
            },
        },
        metaDocument: {
            title: "Tipus de document",
            detail: {
                title: "Detall de tipus de document",
            },
            tabs: {
                dades: "Dades",
                nti: "Dades NTI",
                portafirmes: "Firma amb portafirmes",
                navegador: "Firma amb navegador",
                viaFirma: "Firma amb viaFirma",
                pinbal: "PINBAL",
            },
            action: {
                default: {
                    label: "Marcar per defecte",
                    ok: "Tipus de document marcat com defecte",
                },
                undefault: {
                    label: "Borrar per defecte",
                    ok: "Tipus de document desmarcat com defecte",
                },
                activar: {
                    label: "Activar",
                    ok: "Tipus de document activat",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Tipus de document desactivat",
                },
                new: {
                    label: "Nou tipus de document",
                    ok: "Tipus de document creat correctament",
                },
                update: {
                    ok: "Tipus de document modificat correctament",
                },
                delete: {
                    ok: "Tipus de document eliminat correctament",
                },
            },
        },
        expedientEstat: {
            title: "Estat del procediment",
            detail: {
                title: "Detall de l'estat del procediment"
            },
            action: {
                new: {
                    label: "Nou estat",
                    ok: "Estat creat correctament",
                },
                update: {
                    ok: "Estat modificat correctament",
                },
                delete: {
                    ok: "Estat eliminat correctament",
                },
            },
        },
        metaExpedientTasca: {
            title: "Tasca",
            detall: {
                title: "Detall de la tasca",
                duracio: "Duració de la tasca en dies naturals.",
                validacio: "Validacions de la tasca: {{nom}}",
            },
            action: {
                activar: {
                    label: "Activar",
                    ok: "Tasca activat",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Tasca desactivat",
                },
                new: {
                    label: "Nova tasca",
                    ok: "Tasca creat correctament",
                },
                update: {
                    ok: "Tasca modificat correctament",
                },
                delete: {
                    ok: "Tasca eliminat correctament",
                },
            },
        },
        metaExpedientTascaValidacio: {
            title: "Validació",
            detail: {
                title: "Detall de la validació",
            },
            action: {
                activar: {
                    label: "Activar",
                    ok: "Validació activat",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Validació desactivat",
                },
                new: {
                    label: "Nova validació",
                    ok: "Validació creat correctament",
                },
                update: {
                    ok: "Validació modificat correctament",
                },
                delete: {
                    ok: "Validació eliminat correctament",
                },
            },
        },
        domini: {
            title: "Domini",
            action: {
                cleanCache: {
                    label: "Buidar cache",
                    ok: "La cache s'ha buidat correctament",
                },
                new: {
                    label: "Afegir domini",
                    ok: "Domini creat correctament",
                },
                update: {
                    ok: "Domini modificat correctament",
                },
                delete: {
                    ok: "Domini eliminat correctament",
                },
            },
        },
        entitat: {
            title: "Entitat",
            form: {
                temaClar: "Configuració per el tema clar",
                temaFosc: "Configuració per el tema fosc",
            },
            action: {
                new: {
                    label: "Nova entitat",
                    ok: "Entitat creada correctament",
                },
                update: {
                    ok: "Entitat modificada correctament",
                },
                delete: {
                    ok: "Entitat eliminada correctament",
                },
                config: {
                    label: "Configurar",
                    // ok: "Entitat eliminada correctament",
                },
                activar: {
                    label: "Activar",
                    ok: "Entitat activada",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Entitat desactivada",
                },
            },
        },
        avis: {
            title: "Avis",
            action: {
                new: {
                    label: "Nov avís",
                    ok: "Avís creat correctament",
                },
                update: {
                    ok: "Avís modificat correctament",
                },
                delete: {
                    ok: "Avís eliminat correctament",
                },
                activar: {
                    label: "Activar",
                    ok: "Avís activat",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Avís desactivat",
                },
            },
        },
        pinbalServei: {
            title: "Servei pinbal",
            action: {
                update: {
                    ok: "Servei pinbal modificat correctament",
                },
            },
        },
        urlInstruccio: {
            title: "URL Instrucció",
            detall: {
                url: "Formats disponibles:\n - http://URL.es/alegar/[ENI]",
            },
            action: {
                new: {
                    label: "Nova url instrucció",
                    ok: "Url creat correctament",
                },
                update: {
                    ok: "Url modificat correctament",
                },
                delete: {
                    ok: "Url eliminat correctament",
                },
            },
        },
        permision: {
            title: "Permisos",
            grid: {
                organGestor: "Organ gestor",
                principal: "Tipus",
                sid: "Principal",
                create: "Consulta",
                read: "Creació",
                write: "Modificació",
                delete: "Eliminació",
                estadistic: "Estadístiques",
            },
            tabs: {
                expedient: "Gestió d'expedients",
                admin: "Administració i disseny",
            },
            action: {
                new: {
                    title: "Crear nou permís",
                    label: "Nou permís",
                    ok: "El permís per '{{data.principal}} {{data.sid}}' s'ha creat correctament",
                },
                update: {
                    title: "Modificar permís",
                    ok: "El permís per '{{data.principal}} {{data.sid}}' s'ha modificat correctament",
                },
                delete: {
                    check: "Està segur que vol continuar amb aquesta acció?",
                    description: "Un cop esborrada no es podrà recuperar",
                    ok: "El permís per '{{data.principal}} {{data.sid}}' s'ha esborrat correctament",
                },
            },
        },
        user: {
            options: {
                perfil: "El meu perfil",
                manual: "Manual d'usuari",
                manualAdmin: "Manual dels administradors",
                logout: "Desconnectar",
                noOrgans: "Cap organ gestor assignat"
            },
            menu: {
                entitat: "Entitats",
                expedient: "Expedients",
                monitoritzar: "Monitoritzar",
                integracions: "Integracions",
                excepcions: "Excepcions",
                monitor: "Monitor de sistema",

                config: "Configurar",
                props: "Propietats configurables",
                pinbal: "Serveis PINBAL",
                segonPla: "Reiniciar tasques en segon pla ...",
                plugins: "Reiniciar plugins ...",
                avisos: "Avisos",
                backVersio: "Interfície clàssica",

                anotacions: "Anotacions",
                procediments: "Procediments i serveis",
                procedimentsTitle: "Gestió de procediments i serveis",
                procedimentsRevisorTitle: "Revisió de procediments i serveis",
                procedimentPermis: "Permisos del procediment: {{nom}}",
                grups: "Grups",
                grupPermis: "Permisos del grup",
                revisar: "Revisió de procediments i serveis",
                tasca: "Tasques",
                flux: "Fluxos de firma",

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
                documentDada: "Meta-dades del tipus de document: {{nom}}",
                nti: "Tipus documentals NTI",
                dominis: "Dominis",
                organs: "Òrgans gestors",
                organPermis: "Permisos de l'òrgan gestor: {{nom}}",
                url: "URLs d'instrucció",
                permisos: "Permisos de l'entitat"
            },
            massive: {
                title: "Acció massiva",
                portafirmes: "Enviar documents al portafirmes",
                firmar: "Firmar documents des del navegador",
                marcar: "Marcar com a definitius",
                estat: "Canvi d'estat d'expedients",
                tancar: "Tancament d'expedients",
                custodiar: "Custodiar elements pendents",
                csv: "Copiar enllaç CSV",
                anexos: "Adjuntar annexos pendents d'anotacions acceptades",
                anotacio: "Actualitzar estat de les anotacions a Distribució",
                prioritat: "Canviar prioritat d'expedients",
                refresh: "Refrescar dada 10 segons"
            },
            action: {
                massives: {
                    label: "Consultar accions massives",
                    title: "Execucions massives de {{name}}",
                    detail: "Detall de l'acció massiva",
                    ok: "El document s'ha baixat correctament",
                },
            },
            perfil: {
                title: "El meu perfil",
                ok: "Les dades de l'usuari '{{nom}}' s'han modificat correctament",
                dades: "Dades d'usuari",
                correu: "Enviament de correus",
                generic: "Configuració genèrica",
                column: "Configuració de columnes del llistat d'expedients",
                vista: "Configuració vista de documents dels expedients",
                moure: "Configuració vista destí al moure documents"
            }
        },
        alert: {
            title: "Errors de validació del expedient",
            action: {
                read: {
                    label: "Marcar com a llegida",
                    ok: "L'alerta s'ha marcat com a llegida",
                    massiveOk: "Les alertes s'han marcat com a llegides",
                },
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