const translationCa = {
    common: {
        close: "Tanca",
        cancel: "Cancel·la",
        create: "Crea",
        copy: "Copia",
        update: "Modifica",
        actualize: "Actualitza",
        save: "Guarda",
        delete: "Esborra",
        accepta: "Accepta",
        rebutja: "Rebutja",
        action: "Accions",
        expand: "Expandeix",
        contract: "Contreu",
        download: "Descarrega",
        send: "Envia",
        detail: "Detalls",
        refresh: "Refresca",
        clear: "Neteja",
        back: "Torna",
        search: "Filtra",
        options: "Opcions",
        select: {
            all: "Selecciona-ho tot",
            clear: "Neteja la selecció",
        },
        import: "Importa",
        export: "Exporta",
        consult: "Consulta",
        filter: "Filtra",
        processing: "Processant...",
        auditoria: {
            create: "Creat el {{createdDate}} per '{{createdBy}}'.",
            update: "Modificat el {{lastModifiedDate}} per '{{lastModifiedBy}}'.",
        },
        nouPermis: "Nou permís",
        advancedSearch: 'Cerca avançada',
        advancedSearchOpen: 'Obre la cerca avançada',
        advancedSearchClose: 'Tanca la cerca avançada',
        error: {
            status: "Codi d'error",
            title: "Títol",
            message: "Missatge",
        },
        dragdrop: "Reordena el contingut",
    },
    buttons: {
        answerRequired: {
            accept: 'Accepta',
            cancel: 'Cancel·la',
        },
        confirm: {
            accept: 'Accepta',
            cancel: 'Cancel·la',
        },
        form: {
            save: 'Desa',
            cancel: 'Cancel·la',
        },
        action: {
            exec: 'Executa',
            cancel: 'Cancel·la',
        },
        report: {
            generate: 'Genera',
            cancel: 'Cancel·la',
        },
        misc: {
            close: 'Tanca',
            retry: 'Torna a provar',
        },
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
        estatNotificacio: {
            ENVIADA: "Enviada",
            ENVIADA_AMB_ERRORS: "Enviada amb errors",
            FINALITZADA: "Finalitzada",
            FINALITZADA_AMB_ERRORS: "Finalitzada amb errors",
            PENDENT: "Pendent",
            PROCESSADA: "Processada",
            REGISTRADA: "Registrada",
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
    },
    navigate: {
        accessibilitat: "Accessibilitat",
        expedient: "Expedients",
        expedientPeticio: "Anotacions de registre",
        usuariTasca: "Tasques",
        entitat: "Gestió d'entitats",
        avis: "Gestió d'avisos",
        exception: 'Darreres excepcions produïdes',
        integracio: "Seguiment d'integracions",
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
            label: "Comentaris",
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
                    label: "Desa l'arxiu",
                    ok: "Element '{{contingut}}' desat a l'arxiu",
                },
                move: {
                    label: "Mou...",
                    title: "Moure contingut",
                    ok: "Document '{{document}}' mogut correctament",
                },
                copy: {
                    label: "Copia...",
                    title: "Copiar contingut",
                    ok: "Document '{{document}}' copiat correctament",
                },
                vincular: {
                    label: "Vincula...",
                    title: "Vincular contingut",
                    ok: "Document '{{document}}' vinculat correctament",
                },
                create: {
                    label: "Crea contingut",
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
                    label: "Importa expedient relacionat...",
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
                    label: "Custodia",
                },
                replay: {
                    label: "Recupera",
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
                title: "Anotacions de registre"
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
                    label: "Descarrega justificant",
                    ok: "El justificant s'ha descarregat correctament",
                },
                acceptar: {
                    label: "Accepta...",
                    button: "Accepta",
                    title: "Acceptar expedient",
                    ok: "L'anotació s'ha acceptat correctament",
                },
                rebutjar: {
                    label: "Rebutja...",
                    button: "Rebutja",
                    title: "Rebutjar expedient",
                    ok: "L'anotació s'ha rebutjat correctament",
                },
                canviProcediment: {
                    label: "Modifica...",
                    title: "Canviar procediment",
                    ok: "L'anotació {{data.identificador}} s'ha modificat correctament",
                },
                canviEstatDistribucio: {
                    label: "Canvia estat a distribució",
                    ok: "L'estat ha canviat correctament",
                    massiveOk: "S'ha programat l'acció massiva per actualitzar l'estat de '{{data.num}}' anotacions.",
                },
                descargarAnnex: {
                    label: "Descarrega annex",
                    ok: "Annex descarregat correctament",
                },
                procesarAnnexosPendents: {
                    label: "Adjunta",
                    ok: "El annex s'ha processat correctament",
                    massiveOk: "S'ha programat l'acció massiva per processar '{{data.num}}' annexos pendents.",
                    info: "Si s'ha produït algun error al acceptar una anotació des de la pantalla Anotacions, de manera que algun dels documents de l'anotació no s'han adjuntat a l'expedient, des d'aquest llistat podrà tornar a intentar adjuntar el document a l'expedient.",
                },
                firma: {
                    label: "Signatures",
                    title: "Signatures",
                },
                consultar: {
                    label: "Consulta",
                    ok: "L'anotació s'ha consultat y guardat correctament",
                    massiveOk: "S'ha consultat y guardat correctament {{data.num}} annotations",
                },
                reintentar: {
                    title: "Selecciona tipus de document per a l'annex/annexos pendents",
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
                duracio: "Durada",
                duracioFormat: {
                    expirada: "Data límit expirada.",
                    avui: "La data límit és avui.",
                    falten: "Falten {{count}} dies.",
                    mateixDia: "El mateix dia.",
                    i: " i ",
                    setmana_1: "1 setmana",
                    setmana_n: "{{count}} setmanes",
                    dia_1: "1 dia",
                    dia_n: "{{count}} dies",
                },
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
                    label: "Tramita",
                },
                iniciar: {
                    label: "Inicia",
                    ok: "La tasca s'ha iniciat correctament",
                },
                rebutjar: {
                    label: "Rebutja...",
                    button: "Rebutja",
                    title: "Rebutjar tasca",
                    ok: "La tasca s'ha rebutjat correctament",
                },
                cancel: {
                    label: "Cancel·la",
                    title: "Segur que voleu cancel·lar aquesta tasca?",
                    ok: "La tasca s'ha cancel·lat correctament",
                },
                finalitzar: {
                    label: "Finalitza",
                    ok: "La tasca s'ha finalitzat correctament",
                },
                reassignar: {
                    label: "Reassigna...",
                    button: "Reassigna",
                    title: "Reassignar tasca",
                    ok: "La tasca s'ha reassignat correctament",
                },
                delegar: {
                    label: "Delega...",
                    button: "Delega",
                    title: "Delegar tasca",
                    ok: "La tasca s'ha delegat correctament",
                },
                retomar: {
                    label: "Cancel·la delegació...",
                    button: "Cancel·la delegació",
                    title: "Cancel·lar delegació de tasca",
                    ok: "La delegació de la tasca s'ha cancel·lat correctament",
                },
                changeDataLimit: {
                    label: "Modifica data límit...",
                    button: "Modifica data límit",
                    title: "Canviar data límit",
                    ok: "La tasca s'ha modificat correctament",
                },
                changePrioritat: {
                    label: "Canvia prioritat...",
                    button: "Canvia prioritat",
                    title: "Modificar prioritat de la tasca",
                    ok: "La tasca s'ha modificat correctament",
                },
                reobrir: {
                    label: "Reobre...",
                    button: "Reobre",
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
                    label: "Esborra Interessat",
                    check: "Esteu segur que voleu continuar amb aquesta acció?",
                    description: "Un cop esborrat no es podrà recuperar",
                    ok: "L'interessat {{data.documentNum}} s'ha esborrat correctament",
                },
                createRep: {
                    label: "Afegeix Representant",
                    ok: "El representant {{data.documentNum}} s'ha creat correctament",
                },
                updateRep: {
                    label: "Modifica Representant",
                    ok: "El representant {{data.documentNum}} s'ha modificat correctament",
                },
                deleteRep: {
                    label: "Esborra Representant",
                    check: "Esteu segur que voleu continuar amb aquesta acció?",
                    description: "Un cop esborrat no es podrà recuperar",
                    ok: "El representant {{data.documentNum}} s'ha esborrat correctament",
                },
                importar: {
                    label: "Importa...",
                    title: "Importar interessats",
                    ok: "Interessats importats correctament",
                },
                exportar: {
                    label: "Exporta...",
                    ok: "Interessats exportats correctament",
                    hint: "Seleccioni els interessats que vol exportar.",
                },
                importSGD: {
                    label: "Importa interessats des de Registre...",
                    title: "Importar interessats des de Registre",
                    ok: "Interessats importats correctament",
                },
				gestGrups: {
				    label: "Gestiona grups...",
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
					    label: "Esborra Grup",
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
                title: "Expedients"
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
                    label: "Modifica...",
                    title: "Modificar expedient",
                    ok: "L’expedient '{{data.nom}}' s’ha modificat correctament.",
                },
                detall: {
                    label: "Gestiona",
                },
                importar: {
                    label: "Importa expedient",
                    ok: "L’expedient s’ha importat correctament",
                },
                agafar: {
                    label: "Agafa",
                    ok: "L’expedient '{{expedient}}' ha estat agafat per l’usuari '{{user}}'",
                },
                follow: {
                    label: "Segueix",
                    ok: "L’usuari '{{user}}' ha començat a seguir l’expedient '{{expedient}}'.",
                },
                unfollow: {
                    label: "Deixa de seguir",
                    ok: "L’usuari '{{user}}' ha deixat de seguir l’expedient '{{expedient}}'.",
                },
                retornar: {
                    label: "Retorna",
                    ok: "L’expedient '{{expedient}}' ha estat retornat al gestor original '{{user}}'",
                },
                lliberar: {
                    label: "Allibera",
                    ok: "L’expedient '{{expedient}}' ha estat alliberat",
                },
                eliminar: {
                    label: "Elimina",
                    ok: "L’expedient '{{data.nom}}' ha estat eliminat correctament",
                },
                close: {
                    label: "Tanca...",
                    button: "Tanca",
                    title: "Tancar expedient",
                    titleMassive: "Tancant massivament {{num}} expedients",
                    ok: "L’expedient '{{expedient}}' ha estat tancat correctament",
                },
                open: {
                    label: "Reobre",
                    description: "Voleu reobrir l’expedient?",
                    ok: "L’expedient '{{expedient}}' ha estat reobert correctament",
                },
                download: {
                    label: "Descarrega documents...",
                    button: "Descarrega seleccionats",
                    title: "Selecció de documents",
                    ok: "Els documents s’han descarregat correctament",
                },
                exportFullCalcul: {
                    label: "Exporta full de càlcul",
                    ok: "El full de càlcul s’ha descarregat correctament",
                },
                exportZIP: {
                    label: "Exporta índex ZIP",
                    button: "Exporta ZIP",
                    title: "Exportar documents a ZIP",
                    ok: "El document ZIP s’ha descarregat correctament",
                },
                exportPDF: {
                    label: "Exporta índex PDF",
                    ok: "El document PDF s’ha descarregat correctament",
                },
                exportCSV: {
                    label: "Exporta CSV",
                    ok: "L’índex CSV s’ha descarregat correctament",
                },
                exportEXCEL: {
                    label: "Exporta índex EXCEL",
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
                    label: "Exporta documents dels exp. seleccionats...",
                    ok: "Els documents s’han exportat correctament",
                },
                export: {
                    label: "Exporta els documents...",
                    button: "Exporta els documents",
                    title: "Exportar documents",
                    ok: "Els documents s’han descarregat correctament",
                },
                sincronitzar: {
                    label: "Sincronitza estat amb arxiu",
                    ok: "L’estat de l’arxiu s’ha sincronitzat",
                },
                changePrioritat: {
                    label: "Canvia prioritat...",
                    button: "Canvia prioritat",
                    title: "Modificar prioritat de l’expedient",
                    ok: "La prioritat de l’expedient '{{expedient}}' s’ha modificat correctament.",
                    massiveOk: "S'ha cambiat la prioritat de '{{data.num}}' expedients.",
                },
                changeEstat: {
                    label: "Canvia estat...",
                    button: "Canvia estat",
                    title: "Modificar estat de l’expedient",
                    ok: "L’estat de l’expedient '{{expedient}}' s’ha modificat correctament.",
                    massiveOk: "S'ha cambiat l'estat a '{{data.num}}' expedients."
                },
                assignar: {
                    label: "Assigna...",
                    button: "Assigna",
                    title: "Assignar expedient a usuari",
                    ok: "L’expedient '{{expedient}}' s’ha assignat correctament.",
                },
                relacio: {
                    label: "Relaciona...",
                    button: "Relaciona",
                    title: "Relacionar expedient",
                    ok: "Les relacions de l’expedient '{{expedient}}' han canviat correctament.",
                },
                eliminarRelacio: {
                    label: "Elimina relació",
                    ok: "La relació entre els 2 expedients s’ha eliminat correctament.",
                },
                excelInteressats: {
                    title: "Descarrega plantilla per importar interessats Excel",
                    ok: "Els interessats s’han exportat correctament",
                },
                impDocMass: {
                    label: "Importa documents als exp. seleccionats...",
                    title: "Importació de documents",
                    mssg: "Els documents que adjunteu s'incorporaran als {{num}} expedients seleccionats",
                    warning: "Els expedients han de pertànyer al mateix procediment.",
                },
                exportMass: {
                    unic: "Exporta l'expedient...",
                    label: "Exporta els expedients seleccionats...",
                    title: "Exportar expedients seleccionats",
                    titleUni: "Exportar expedient",
                    info: "Podeu seleccionar diversos formats d'exportació. L'exportació es realitzarà en segon pla, i un cop finalitzada, podreu descarregar el document generat des del llistat d'accions massives.",
                    info2: "Podeu seleccionar diversos formats d'exportació. L'exportació pot tardar uns instants en completar-se, un cop finalitzada, s'iniciarà la descarrega automàticament.",
                },
                comment: {
                    ok: "Comentari afegit a l'expedient '{{data.expedient.description}}'",
                },
				moureTot: {
					label: "Mou-ho tot...",
                    button: "Mou-ho tot",
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
                seguidors: {
                    label: "Seguidors",
                    title: "Seguidors de l’expedient",
                },
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
                dades: "Dades generals",
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
                nullEstat: "Sense estat",
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
                createdDate: "Data de creació",
                createdBy: "Creat per",
                dataCaptura: "Data de captura",
                csv: "CSV",
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
                tipusDocumentDefault: "Tipus de document a aplicar a tots els fitxers",
            },
            action: {
                new: {
                    dropMessg: "Arrossega el fitxer aquí o sobre la taula...",
                    ok: "El document {{data.nom}} s'ha creat correctament"
                },
                update: {
                    ok: "El document {{data.nom}} s'ha modificat correctament"
                },
                delete: {
                    label: "Esborra",
                    check: "Estau segur que voleu eliminar aquest contingut?",
                    description: "Un cop esborrat no es podrà recuperar. Si contenia firma en curs, serà cancelada.",
                    ok: "El document {{data.nom}} s'ha eliminat correctament"
                },
                pinbal: {
                    label: "Consulta PINBAL...",
                    button: "Consulta",
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
                    label: "Importa documents SGD...",
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
                    label: "Importa des de ZIP...",
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
                    noUuid: "El document no està sincronitzat amb l'arxiu",
                },
                imprimible: {
                    label: "Còpia autèntica imprimible",
                    ok: "La còpia autèntica imprimible s'ha descarregat correctament",
                },
                original: {
                    label: "Descarrega original",
                    ok: "El document original s'ha descarregat correctament",
                },
                download: {
                    firma: "Descarrega signatura",
                    ok: "Document descarregat correctament",
                },
                firma: {
                    label: "Signa des del navegador...",
                    button: "Inicia procés de firma",
                    title: "Signar des del navegador",
                    ok: "Document signat correctament",
                },
                view: {
                    label: "Visualitza",
                    title: "Visualitzar",
                },
                csv: {
                    label: "Copia enllaç CSV",
                    ok: "Enllaç CSV copiat correctament",
                },
                portafirmes: {
                    label: "Envia a portafirmes...",
                    button: "Envia a portafirmes",
                    title: "Enviar document a portafirmes",
                    ok: "Document '{{document}}' enviat a portafirmes",
                },
                toPDF: {
                    description: "Es canviarà el format del document abans d'enviar-lo al portafirmes",
                    title: "Visualitzar versió PDF",
                },
                firmar: {
                    label: "Signatura des del navegador...",
                    button: "Inicia procés de firma",
                },
                viaFirma: {
                    label: "Envia viaFirma...",
                    button: "Envia a ViaFirma",
                    title: "Enviar document a ViaFirma",
                    ok: "Document '{{document}}' enviat a viaFirma",
                },
                mail: {
                    label: "Envia via email...",
                    button: "Envia via email",
                    title: "Enviar document per email",
                    ok: "Document '{{document}}' enviat via email",
                },
                seguiment: {
                    label: "Seguiment portafirmes",
                    title: "Detalls de la firma",
                },
                cancel: {
                    label: "Cancel·la enviament",
                    ok: "La signatura ha estat cancel·lada correctament",
                    check: "Confirmau l'acció",
                    description: "Segur que voleu cancel·lar la firma actualment en procés?",
                },
                notificar: {
                    label: "Notifica o comunica...",
                    button: "Notifica",
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
                        format: {
                            document: "El format del document actual és: {{extension}}",
                            noSir: "Canal No-SIR: Els formats admesos són pdf y zip.",
                            sir: "Canal SIR: Els formats admesos són jpg, jpeg, odt, odp, ods, odg, docx, xlsx, pptx, pdf, png, rtf, svg, tiff, txt, xml y xsig.",
                        },
                    },
                },
                notificarMasiva: {
                    label: "Notifica o comunica...",
                    button: "Notifica",
                    title: "Generar document per notificar",
                    ok: "S'ha generat un zip dels elements seleccionats",
                },
                comunicar: {
                    label: "Comunica...",
                },
                publicar: {
                    label: "Publica...",
                    button: "Publica",
                    title: "Crear publicació",
                    ok: "Publicació creada correctament",
                },
                descarregarOriginal: {
                    label: "Document original",
                    ok: "El document original s'ha descarregat correctament",
                },
                descarregarImprimible: {
                    label: "Descarrega còpia auténtica imprimible",
                    ok: "La còpia auténtica imprimible s'ha descarregat correctament",
                },
                changeType: {
                    label: "Canvia tipus...",
                    button: "Canvia tipus",
                    title: "Canviar tipus",
                    ok: "Els documents s'han modificat correctament",
                },
                definitive: {
                    label: "Converteix a definitiu",
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
                    label: "Modifica...",
                    title: "Modificar carpeta",
                    ok: "Carpeta '{{data.nom}}' modificada correctament",
                },
                delete: {
                    label: "Esborra...",
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
                    label: "Afegeix valor per la dada",
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
                    label: "Activa",
                    ok: "Meta-dada activada",
                },
                desactivar: {
                    label: "Desactiva",
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
                identifier: "Identificació",
                registre: "Informació de registre",
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
                fitxerNom: "Nom de l'arxiu",
                notificacioEstat: "Estat",
                createdDate: "Enviada el",
                entregaPostal: "Lliurament postal",
                serveiTipusEnum: "Tipus de servei",
                notificacioIdentificador: "Identificador",
            },
            action: {
                update: {
                    ok: "La remesa {{data.assumpte}} s'ha modificat correctament",
                },
                actualitzarEstat: {
                    label: "Actualitza estat",
                    ok: "L'estat s'ha actualitzat correctament",
                    massiveOk: "S'ha creat l'execució massiva. Podeu consultar-ne l'estat al llistat d'accions massives.",
                },
                notificacioInteressat: {
                    label: "Enviaments",
                    title: "Enviaments",
                    ok: "",
                },
                justificant: {
                    label: "Justificant d'enviament",
                    ok: "S'ha descarregat el justificant",
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
                    label: "Amplia termini...",
                    button: "Amplia termini",
                    title: "Ampliació del termini dels enviaments de la remesa",
                    ok: "El termini del enviament ha estat ampliat",
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
        documentVia: {
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
                    label: "Vincula grup...",
                    button: "Vincula grup",
                    title: "Vincular grup",
                    ok: "Grup vinculat",
                },
                unlink: {
                    label: "Desvincula",
                    ok: "Grup desvinculat",
                },
                default: {
                    label: "Marca per defecte",
                    ok: "Grup marcat com defecte",
                },
                undefault: {
                    label: "Treu per defecte",
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
                    label: "Actualitza òrgans gestors de DIR3",
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
                    label: "Afageix tipus documental",
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
            columnes: {
                comu: "Comú",
                directe: "Directe",
                grups: "Grups",
                actiu: "Actiu",
                estat: "Estat",
            },
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
                    label: "Consulta",
                    revisat: "Aquest procediment no es pot modificar ja que es troba en estat revisat",
                },
                canviEstat: {
                    label: "Canvia l'estat de revisió...",
                    button: "Canvia estat",
                    title: "Canviar estat de revisió",
                    ok: "Estat canviat correctament",
                },
                expedient: {
                    title: "Expedients del procediment: {{nom}}",
                    label: "Expedients",
                },
                regla: {
                    title: "Estat de la regla en Distribució",
                    label: "Regla distribució",
                    create: {
                        label: "Crea regla en Distribució",
                        ok: "La regla amb codi '{{nom}}' s'ha creat correctament.",
                    },
                    active: {
                        label: "Activa regla en Distribució",
                        ok: "La regla amb codi '{{nom}}' s'ha activat correctament.",
                    },
                    desactive: {
                        label: "Desactiva regla en Distribució",
                        ok: "La regla amb codi '{{nom}}' s'ha desactivat correctament",
                    },
                },
                activar: {
                    label: "Activa",
                    ok: "Procediment activat correctament",
                },
                desactivar: {
                    label: "Desactiva",
                    ok: "Procediment desactivat correctament",
                },
                comment: {
                    ok: "Comentari afegit al procediment '{{data.metaExpedient.description}}'",
                },
                importRolsac: {
                    label: "Importa des de ROLSAC...",
                    title: "Importar procediment des de ROLSAC",
                },
                importFitxer: {
                    label: "Importa des de fitxer...",
                    title: "Importar procediment",
                    ok: "Procediment importat correctament",
                },
                export: {
                    ok: "Procediment exportat correctament",
                },
                clonar: {
                    label: "Clona",
                    title: "Clonar procediment",
                    ok: "Nou procediment clonat: {{codi}}",
                },
                canviPendent: {
                    label: "Marca com a pendent de revisió",
                    ok: "Procediment marcat com a pendent de revisió",
                },
                canviDisseny: {
                    label: "Marca com a procés de disseny",
                    ok: "Procediment marcat com a procés de disseny",
                },                
                actualize: {
                    label: "Actualitza des de ROLSAC...",
                    button: "Actualitza",
                    title: "Actualització de procediments",
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
                pendentsRevisio: "Hi ha {{num}} procedimients o serveis pendents de revisar",
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
                    label: "Marca per defecte",
                    ok: "Tipus de document marcat com defecte",
                },
                undefault: {
                    label: "Esborra per defecte",
                    ok: "Tipus de document desmarcat com defecte",
                },
                activar: {
                    label: "Activa",
                    ok: "Tipus de document activat",
                },
                desactivar: {
                    label: "Desactiva",
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
                    label: "Activa",
                    ok: "Tasca activat",
                },
                desactivar: {
                    label: "Desactiva",
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
                    label: "Activa",
                    ok: "Validació activat",
                },
                desactivar: {
                    label: "Desactiva",
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
                    label: "Buida la cache",
                    ok: "La cache s'ha buidat correctament",
                },
                new: {
                    label: "Afegeix domini",
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
                    label: "Configura",
                },
                activar: {
                    label: "Activa",
                    ok: "Entitat activada",
                },
                desactivar: {
                    label: "Desactiva",
                    ok: "Entitat desactivada",
                },
            },
        },
        avis: {
            title: "Avis",
            action: {
                new: {
                    label: "Nou avís",
                    ok: "Avís creat correctament",
                },
                update: {
                    ok: "Avís modificat correctament",
                },
                delete: {
                    ok: "Avís eliminat correctament",
                },
                activar: {
                    label: "Activa",
                    ok: "Avís activat",
                },
                desactivar: {
                    label: "Desactiva",
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
        propietats: {
            title: "Propietat",
            empty: "No s'han trobat propietats",
            action: {
                sync: {
                    label: "Sincronitza amb JBoss",
                    ok: "Les propietats s'han sincronitzat correctament",
                },
                new: {
                    label: "Afegeix conf. específica",
                    ok: "La propietat s'ha creat correctament",
                },
                update: {
                    ok: "La propietat s'ha modificat correctament",
                },
                delete: {
                    ok: "La propietat s'ha esborrat correctament",
                },
            }
        },
        exception: {
            action: {
                detail: {
                    title: "Detalls de l'excepció",
                }
            }
        },
        integracio: {
            action: {
                detail: {
                    title: "Detalls de la comunicació amb la integració",
                },
                diagnostic: {
                    title: "Diagnòstic",
                    label: "Repetir diagnòstic",
                },
                diagnosticAll: {
                    title: "Diagnòstic dels sistemes externs",
                    label: "Diagnòstic",
                },
                reiniciar: {
                    label: "Reinicia plugin",
                    ok: "El plugin amb codi '{{nom}}' s'ha reiniciat correctament",
                },
                reiniciarAll: {
                    label: "Reinicia-ho tot",
                    ok: "Els plugins s'han reiniciat correctament",
                },
            }
        },
        sistema: {
            detail: {
                sistemaOperatiu: "Sistema operatiu",
                arquitectura: "Arquitectura",
                processadors: "Processadors",
                jbossVersion: "Versió de JBoss",
                applicationServerInfo: "Informació del servidor d'aplicacions",
                tempsFuncionant: "Temps funcionant",
                jvmMemory: "Màquina virtual de Java",
                disksUsage: "Disc i CPU",
            },
            tabs: {
                sistema: "Sistema",
                fils: "Fils d'execució",
                tasques: "Tasques en segon pla",
            },
            action: {
                restart: {
                    label: "Reinicia",
                    ok: "La tasca s'ha reiniciat correctament",
                },
                restartAll: {
                    label: "Reinicia seleccionades",
                    ok: "Les tasques s'han reiniciat correctament",
                },
            }
        },
        permision: {
            title: "Permisos",
            grid: {
                organGestor: "Organ gestor",
                principal: "Tipus",
                sid: "Principal",
                create: "Creació",
                read: "Consulta",
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
                    label: "Nou permís",
                    title: "Crear nou permís",
                    ok: "El permís per '{{data.principal}} {{data.sid}}' s'ha creat correctament",
                },
                update: {
                    title: "Modifica permís",
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
                title: "Menú",

                entitat: "Entitats",
                expedient: "Expedients",
                monitoritzar: "Monitoritzar",
                integracions: "Integracions",
                excepcions: "Excepcions",
                monitor: "Monitor de sistema",

                config: "Configurar",
                props: "Propietats configurables",
                pinbal: "Serveis PINBAL",
                segonPla: "Reiniciar tasques en segon pla...",
                plugins: "Reiniciar plugins...",
                avisos: "Avisos",
                backVersio: "Interfície clàssica",

                anotacions: "Anotacions",
                procediments: "Procediments i serveis",
                procedimentsTitle: "Procediments i serveis",
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
                    label: "Consulta accions massives",
                    title: "Execucions massives de {{name}}",
                    detail: "Detall de l'acció massiva: {{tipus}}",
                    ok: "El document s'ha baixat correctament",
                    pending: "Aquest element s'està processant actualment",
                },
            },
            perfil: {
                title: "El meu perfil",
                ok: "Les dades de l'usuari '{{nom}}' s'han modificat correctament",
                dades: "Dades d'usuari",
                correu: "Enviament de correus",
                activarCorreu: "Activar tots els correus",
                generic: "Configuració genèrica",
                column: "Configuració de columnes del llistat d'expedients",
                vista: "Configuració vista de documents dels expedients",
                moure: "Configuració vista destí al moure documents",
                interficie: "Interfície per defecte"
            }
        },
        alert: {
            titleExpedient: "Errors de validació del expedient",
            titleDocument: "Errors de validació del document",
            action: {
                read: {
                    label: "Marca com a llegida",
                    title: "Alertes de l'expedient",
                    ok: "L'alerta s'ha marcat com a llegida",
                    massiveOk: "Les alertes s'han marcat com a llegides",
                },
            },
            errors: {
                altresErrors: ", i altres {{restants}} errors de validació.",
                unAltreError: ", i un altre error de validació.",
                metaDada: "Falten les dades següents:",
                metaDocument: "Falten els documents següents:",
                metaNode: "Hi ha documents sense un tipus de document assignat",
                noFinalitzades: "Hi ha notificacions amb un estat que no és final",
                interessatObligatori: "Falta informar un interessat",
            },
        },
        accesibilitat: {
            title: "Declaració d'Accessibilitat",
            intro: {
                title: "Introducció",
                p1Part1: "El Govern de les Illes Balears s'ha compromès a fer accessible el seu lloc web i la seva aplicació per a dispositius mòbils, de conformitat amb",
                p1LinkText: "el Reial decret 1112/2018",
                p1Part2: ", de 7 de setembre, d'accessibilitat dels llocs web i aplicacions mòbils del sector públic.",
                p2Part1: "La present declaració d'accessibilitat s'aplica al lloc web",
                p2Part2: "i exclou les pàgines que condueixen a enllaços externs.",
            },
            compliment: {
                title: "Situació de compliment",
                introPart1: "Aquest lloc web és parcialment conforme amb",
                introLinkText: "el RD 1112/2018",
                introPart2: "a causa de les excepcions i de la manca de conformitat dels aspectes que s'indiquen a continuació.",
                criteri1: "Criteri A - 4.1.2 Name, Role, Value: Alguns botons iconogràfics mancaven d'alternativa textual accessible. Solució aplicada: s'ha afegit l'atribut \"title\" i l'atribut \"aria-label\" amb text descriptiu a tots els botons que no disposaven d'etiqueta visible, garantint que els lectors de pantalla puguin identificar la seva funció.",
                criteri2: "Criteri A - 1.1.1 Non-text Content: Algunes imatges informatives no disposaven de text alternatiu. Solució aplicada: s'ha realitzat una auditoria de tots els recursos gràfics. Les imatges no decoratives inclouen ara un atribut \"alt\" descriptiu i contextual. Les imatges purament decoratives utilitzen \"alt\" buit o \"role=presentation\" per ser ignorades per les tecnologies de suport.",
                criteri3: "Criteri AA - 1.4.4 Resize Text: El text es truncava en escalar la interfície al 200%. Solució aplicada: en mides de pantalla reduïdes, els botons mostren únicament la icona acompanyada d'un atribut \"title\" descriptiu. S'ha eliminat l'ús de \"overflow: hidden\" en contenidors de text i s'ha verificat que tot el contingut romangui accessible amb zoom del 200%.",
                criteri4: "Criteri AA - 2.5.8 Target Size (Minimum): Determinats elements interactius no complien amb la mida mínima de 24x24 píxels o l'espaiat requerit. Solució aplicada: s'ha modificat el posicionament i maquetació dels components per garantir una àrea de polsació adequada i un espaiat mínim de 8 píxels entre elements interactius, facilitant-ne l'ús en dispositius tàctils i per a persones amb dificultats de mobilitat.",
            },
            noAccesible: {
                title: "Llista de contingut no accessible i explicació del motiu",
                item1: {
                    title: "Identificació de l'idioma principal",
                    desc1: "El codi d'idioma usat per identificar l'idioma principal no és un codi correcte.",
                    desc2: "En el codi font generat s'ha comprovat que l'atribut lang del node HTML té valor \"ca\", que és vàlid com a codi d'idioma de IANA. És possible que l'error vingui donat perquè pugui haver-hi textos puntuals (els que es guarden a la BBDD introduïts per l'usuari) que no s'adeqüen a l'idioma indicat, o perquè per arquitectura, la gestió de l'idioma es realitza a través de la sessió d'usuari i el context de l'aplicació React, no mitjançant atributs estàtics a l'HTML. Com a mesura compensatòria, l'atribut \"lang\" s'injecta dinàmicament al contenidor principal segons l'idioma seleccionat per l'usuari.",
                },
                item2: {
                    title: "Formularis i etiquetes",
                    desc1: "No es realitza l'associació explícita adequadament entre controls i etiquetes.",
                    desc2: "Als camps de formulari de tipus selector, l'identificador del input no coincideix amb l'atribut \"for\" de l'etiqueta. El validador utilitzat reporta estrictament l'error, tot i que la llibreria Material UI (MUI) declara complir amb la normativa d'accessibilitat WCAG 2.1; en aquest cas només ho compleix parcialment compensant-ho amb l'atribut \"aria-labelledby\", que proporciona un nom accessible correcte per als lectors de pantalla.",
                },
                item3: {
                    title: "Múltiples vies de navegació",
                    desc1: "Absència d'un enllaç al mapa web i d'un cercador al lloc.",
                    desc2: "Nivell d'advertència en no proporcionar cap mètode complementari de navegació com un mapa web o una opció de cerca al lloc web.",
                },
            },
            preparacio: {
                title: "Preparació de la present declaració",
                elaborat: "Aquesta declaració s'ha elaborat mitjançant autoavaluació realitzada per l'equip de desenvolupament utilitzant l'eina automatitzada: Rastrejador Web de l'Observatori d'Accessibilitat Web.",
                dataPrep: "Data de preparació: 01/05/2026",
                darreraRevisio: "Darrera revisió: 05/05/2026",
                properaRevisio: "Propera revisió programada: 05/05/2027",
                norma: "Norma de referència: UNE-EN 301549:2022, nivells A i AA",
                resultatTitle: "Resultat",
                puntuacioLabel: "Puntuació mitjana del lloc web",
                puntuacioVal: "8.16",
                nivellLabel: "Nivell d'adequació estimat",
                nivellVal: "A",
                situacioLabel: "Situació de compliment estimada",
                situacioVal: "Parcialment conforme",
                responsive: "El lloc web està dissenyat per a la seva visualització responsive, de manera que es visualitza de forma òptima en dispositius tauleta i mòbils.",
            },
            contacte: {
                title: "Observacions i dades de contacte",
                p1: "El Govern de les Illes Balears pretén continuar millorant i oferir als ciutadans el millor servei possible. Podeu realitzar comunicacions sobre requisits d'accessibilitat (article 10.2.a) del RD 1112/2018, com per exemple:",
                li1: "Informar sobre qualsevol possible incompliment per part d'aquest lloc web",
                li2: "Transmetre altres dificultats d'accés al contingut",
                li3: "Formular qualsevol altra consulta o suggeriment de millora relativa a l'accessibilitat del lloc web",
                p2Part1: "A través del següent formulari de",
                p2LinkText: "contacte",
                p2Part2: "o trucant al telèfon 971177140. Podeu presentar:",
                li4: "Una queixa relativa al compliment dels requisits del RD 1112/2018, o",
                li5: "Una sol·licitud d'informació accessible relativa a:",
                li5a: "Continguts que estan exclosos de l'àmbit d'aplicació del RD 1112/2018 segons el que estableix l'article 3, apartat 4, o",
                li5b: "Continguts que estan exempts del compliment dels requisits d'accessibilitat per imposar una càrrega desproporcionada.",
                p4Part1: "A través del següent procediment:",
                p4LinkText: "Peticions d'informació accessible i queixes relatives a l'accessibilitat de llocs web i aplicacions mòbils.",
            },
            procediment: {
                title: "Procediment d'aplicació",
                p1: "El procediment de reclamació recollit a l'article 13 del RD 1112/2018 va entrar en vigor el 20 de setembre de 2020.",
                p2: "Si un cop realitzada una sol·licitud d'informació accessible o una queixa, aquesta ha estat desestimada, no s'està d'acord amb la decisió adoptada, o la resposta no compleix els requisits contemplats a l'article 12.5, la persona interessada podrà iniciar una reclamació. Igualment, es podrà iniciar una reclamació en el cas que hagi transcorregut el termini de vint dies hàbils sense haver obtingut resposta.",
                p3Part1: "La reclamació pot ser presentada a través del procediment",
                p3LinkText: "Reclamacions relatives a l'accessibilitat de llocs web i aplicacions mòbils",
            },
            opcional: {
                title: "Contingut opcional",
                mesures: "Mesures d'accessibilitat addicionals implementades: estructura d'encapçalaments revisada, afegides etiquetes dels camps que no en tenien, correcció de blocs de text de més de 150 caràcters sense marcatge de text.",
                config: "Configuració tècnica recomanada: navegadors actualitzats (Chrome, Firefox, Edge, Safari en les seves dues darreres versions), resolució mínima de 1280x720 píxels, i suport de zoom fins al 200% sense pèrdua de contingut o funcionalitat.",
                recursos: "Recursos d'interès: Guia d'accessibilitat web del W3C (https://www.w3.org/WAI/), validadors automàtics d'accessibilitat (https://achecker.ca/), i documentació oficial del Reial Decret 1112/2018.",
            },
        },
        notFound: "No trobat",
        forbidden: "No teniu el rol o permís adequat per accedir a aquest recurs.",
    }
};

export default translationCa;