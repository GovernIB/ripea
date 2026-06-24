const translationEn = {
    common: {
        close: "Close",
        cancel: "Cancel",
        create: "Create",
        copy: "Copy",
        update: "Update",
        actualize: "Actualiza",
        save: "Save",
        delete: "Delete",
        accepta: "Accept",
        rebutja: "Reject",
        action: "Actions",
        expand: "Expand",
        contract: "Collapse",
        download: "Download",
        send: "Send",
        detail: "Details",
        refresh: "Refresh",
        clear: "Clear",
        back: "Back",
        search: "Filter",
        options: "Options",
        select: {
            all: "Select all",
            clear: "Clear selection",
        },
        import: "Import",
        export: "Export",
        consult: "Consult",
        filter: "Filter",
		processing: "Processing...",
        auditoria: {
            create: "Created on {{createdDate}} by '{{createdBy}}'.",
            update: "Modified on {{lastModifiedDate}} by '{{lastModifiedBy}}'.",
        },
        nouPermis: "New permission",
        advancedSearch: "Advanced search",
        advancedSearchOpen: "Open advanced search",
        advancedSearchClose: "Close advanced search",
        error: {
            status: "Error code",
            title: "Title",
            message: "Message",
        },
        dragdrop: "Reorder content",
    },
    buttons: {
        answerRequired: {
            accept: 'Accept',
            cancel: 'Cancel',
        },
        confirm: {
            accept: 'Accept',
            cancel: 'Cancel',
        },
        form: {
            save: 'Save',
            cancel: 'Cancel',
        },
        action: {
            exec: 'Execute',
            cancel: 'Cancel',
        },
        report: {
            generate: 'Generate',
            cancel: 'Cancel',
        },
        misc: {
            close: 'Close',
            retry: 'Try again',
        },
    },
    enum: {
        rol: {
            IPA_SUPER: "Superuser",
            IPA_ADMIN: "Administrator",
            IPA_ADMIN_LECTURA: "Administrator (read)",
            IPA_DISSENY: "Designer",
            IPA_ORGAN_ADMIN: "Organ Administrator",
            IPA_REVISIO: "Reviewer",
            tothom: "User",
        },
        siNO: {
            true: "Yes",
            false: "No",
        },
        estat: {
            TANCAT: "Closed",
            OBERT: "Open",
            ENVIAT: "Sent",
            PAUSAT: "Paused",
            INICIAT: "Started",
            FIRMAT: "Signed",
            REBUTJAT: "Rejected",
            PARCIAL: "Partial",
        },
        estatNotificacio: {
            ENVIADA: "Sent",
            ENVIADA_AMB_ERRORS: "Sent with errors",
            FINALITZADA: "Completed",
            FINALITZADA_AMB_ERRORS: "Completed with errors",
            PENDENT: "Pending",
            PROCESSADA: "Processed",
            REGISTRADA: "Registered",
        },
        registreEstat: {
            OFICI_SIR: "SIR office",
            OFICI_ACCEPTAT: "Office accepted",
            REBUTJAT: "Rejected SIR",
        },
        origen: {
            O0: "Citizen",
            O1: "Administration",
        },
        estatElaboracio: {
            EE01: "Original",
            EE02: "Authentic electronic copy with format change",
            EE03: "Authentic electronic copy of paper document",
            EE04: "Authentic partial electronic copy",
            EE99: "Others",
        },
        tipusVia: {
            ALAMEDA: "Mall",
            AVENIDA: "Avenue",
            BARRIO: "Neighborhood",
            BULEVAR: "Boulevard",
            CALLE: "Street",
            CALLEJA: "Alley",
            CAMINO: "Path",
            CAMPO: "Field",
            CARRERA: "Road",
            CARRETERA: "Highway",
            CUESTA: "Slope",
            EDIFICIO: "Building",
            ENPARANTZA: "Square",
            ESTRADA: "Road",
            GLORIETA: "Roundabout",
            JARDINES: "Gardens",
            OTROS: "Others",
            PARQUE: "Park",
            PASAJE: "Passage",
            PASEO: "Promenade",
            PLAZA: "Square",
            PLAZUELA: "Small square",
            POBLADO: "Village",
            POLIGONO: "Industrial area",
            RAMBLA: "Boulevard",
            RONDA: "Ring road",
            RUA: "Street",
            SECTOR: "Sector",
            TRAVESIA: "Crossing",
            URBANIZACION: "Urbanization",
            VIA: "Way",
		},
    },
    navigate: {
        accessibilitat: "Accessibility",
        expedient: "Case search",
        expedientPeticio: "Registry entries search",
        usuariTasca: "Tasks",
        entitat: "Entity management",
        avis: "Notice management",
        exception: "Latest exceptions",
        integracio: "Integration monitoring",
        massiu: {
            portafirmes: "Mass action: send documents to the signature portal",
            firmasimpleweb: "Mass action: sign documents from the browser",
            canviEstat: "Mass action: Change case status",
            tancament: "Mass action: Close cases",
            seguimentArxiuPendents: "Mass action: Archive pending items",
            csv: "Mass action: copy CSV link",
            definitiu: "Mass action: mark documents as definitive",
            canviPrioritats: "Mass action: Change case priority",
            expedientPeticioCanviEstatDistribucio: "Mass action: Update the status of records in Distribution",
            procesarAnnexosPendents: "Mass action: Attach pending annexes from accepted entries",
        },
    },
    page: {
        comment: {
            label: "Comments",
            expedient: "Expedient Comments",
            tasca: "Task Comments",
            metaExpedient: "Procedure comments",
        },
        contingut: {
            grid: {
                nom: "Name",
                path: "Content path",
            },
            detalle: {
                title: "Detalles del contenido",
                dataProgramada: "Date on which the notification will be effectively sent to Notific@",
                duracio: "Calendar days\nThe notification will be available until 23:59:59 of the entered day, expiring at 00:00 of the following day. Only applies to Electronic Notifications. You can indicate either a number of calendar days or a specific date.",
                dataCaducitat: "Calendar days\nThe notification will be available until 23:59:59 of the entered day, expiring at 00:00 of the following day. Only applies to Electronic Notifications. You can indicate either a number of calendar days or a specific date.",
                retard: "Days the notification will remain at the headquarters before being sent via DEH or CIE",
            },
            tabs: {
                contingut: "Content",
                dades: "Data",
                interessats: "Interested Parties",
                remeses: "Shipments",
                publicacions: "Publications",
                anotacions: "Annotations",
                versions: "Versions",
                tasques: "Tasks",

                actions: "Actions",
                move: "Movements",
                auditoria: "Audit",
            },
            log: {
                causa: "Action cause",
                param: "Parameters",
                param1: "Parameter 1",
                param2: "Parameter 2",
                objecte: "Object",
            },
            moviment: {
                causa: "Movement cause",
                origen: "Origin",
                desti: "Destination",
            },
            action: {
                guardarArxiu: {
                    label: "Save to archive",
                    ok: "Item '{{contingut}}' saved to archive",
                },
                move: {
                    label: "Move...",
                    title: "Move content",
                    ok: "Document '{{document}}' moved successfully",
                },
                copy: {
                    label: "Copy...",
                    title: "Copy content",
                    ok: "Document '{{document}}' copied successfully",
                },
                vincular: {
                    label: "Link...",
                    title: "Link content",
                    ok: "Document '{{document}}' linked successfully",
                },
                create: {
                    label: "Create content",
                },
                history: {
                    label: "Action history",
                    title: "Element action history",
                    detail: "Action details",
                },
                infoArxiu: {
                    title: "Information obtained from the file",
                    label: "File information",
                },
                importarExpedient: {
                    label: "Import related case file...",
                    title: "Related case files",
                },
                seguimentPortafirmes: {
                    label: "Portafirmes tracking",
                    title: "Portafirmes tracking",
                },
                seguimentvf: {
                    label: "Viafirma tracking",
                    title: "Signature details",
                },
                custodiar: {
                    label: "Archive",
                },
                replay: {
                    label: "Recover",
                    ok: "The content has been recovered successfully",
                    massiveOk: "The contents have been recovered successfully",
                },
                delete: {
                    ok: "The content has been deleted successfully",
                    massiveOk: "The contents have been deleted successfully",
                },
            },
            history: {
                create: "Creation",
                update: "Modification",
                user: "User",
                date: "Date",
            },
            alert: {
                valid: "This content has validation errors",
                metaNode: "This document lacks a document type",
                guardarPendent: "Pending to save in archive",
                fileSize: "The maximum allowed file size is {{maxSize}}",
            },
        },
        anotacio: {
            filter: {
                title: "Log annotation search"
            },
            tabs: {
                resum: "Summary",
                estat: "Status",
                registre: "Registry info",
                interessats: "Stakeholders",
                annexos: "Annexes",
                justificant: "Proof",
            },
            detall: {
                title: "Log annotation details",
                estatView: "Status",
                dataAlta: "Registration date",
                observacions: "Reason",
                rejectedDate: "Rejection date",
                acceptedDate: "Acceptance date",
                usuariActualitzacio: "User",
            },
            action: {
                justificant: {
                    label: "Download proof",
                    ok: "The proof has been downloaded successfully",
                },
                acceptar: {
                    label: "Accept...",
                    button: "Accept",
                    title: "Accept case file",
                    ok: "The record has been successfully accepted",
                },
                rebutjar: {
                    label: "Reject...",
                    button: "Reject",
                    title: "Reject case file",
                    ok: "The record has been successfully rejected",
                },
                canviProcediment: {
                    label: "Change procedure...",
                    title: "Change procedure",
                    ok: "Annotation {{data.identificador}} has been successfully modified",
                },
                canviEstatDistribucio: {
                    label: "Change state to distribution",
                    ok: "Status has been successfully changed",
                    massiveOk: "Massive action scheduled to update the status of '{{data.num}}' records.",
                },
                descargarAnnex: {
                    label: "Download annex",
                    ok: "Annex downloaded successfully",
                },
                procesarAnnexosPendents: {
                    label: "Attach",
                    ok: "The annex has been processed successfully",
                    massiveOk: "Massive action scheduled to process '{{data.num}}' pending attachments.",
                    info: "If an error occurred when accepting a record from the Records screen, causing some of the record’s documents not to be attached to the case file, from this list you can try attaching the document to the case file again.",
                },
                firma: {
                    label: "Signatures",
                    title: "Signatures",
                },
                consultar: {
                    label: "Consult",
                    ok: "The entry has been consulted and saved successfully",
                    massiveOk: "{{data.num}} entries have been consulted and saved successfully",
                },
                reintentar: {
                    title: "Select document type for the pending annex/annexes",
                },
                subsanarAnnexos: {
                    label: "Fix annexes with error",
                    title: "Fix annexes with error",
                    ok: "The annexes have been fixed successfully",
                    info: "Select the document type for each annex that ended with an error when accepting the registry entry and retry attaching it to the file.",
                    tipusDocument: "Tipus de document",
                }
            }
        },
        tasca: {
            title: "Task",
            detall: {
                title: "Task details",
                metaExpedientTasca: "Task type",
                metaExpedientTascaDescription: "Task type description",
                createdBy: "Created by",
                responsablesStr: "Responsible users",
                responsableActual: "Current responsible",
                delegat: "Delegate",
                observadors: "Observers",
                dataInici: "Start date",
                duracio: "Duration",
                duracioFormat: {
                    expirada: "Deadline expired.",
                    avui: "Today is the deadline.",
                    falten: "{{count}} days remaining.",
                    mateixDia: "Same day.",
                    i: " and ",
                    setmana_1: "1 week",
                    setmana_n: "{{count}} weeks",
                    dia_1: "1 day",
                    dia_n: "{{count}} days",
                },
                dataLimit: "Deadline",
                estat: "Status",
                prioritat: "Priority",
            },
            action: {
                new: {
                    label: "New Task",
                    ok: "The task {{data.titol}} was created successfully",
                },
                tramitar: {
                    label: "Process",
                },
                iniciar: {
                    label: "Start",
                    ok: "The task has been started successfully",
                },
                rebutjar: {
                    label: "Reject...",
                    button: "Reject",
                    title: "Reject task",
                    ok: "The task has been rejected successfully",
                },
                cancel: {
                    label: "Cancel",
                    title: "Are you sure you want to cancel this task?",
                    ok: "The task has been canceled successfully",
                },
                finalitzar: {
                    label: "Finish",
                    ok: "The task has been finished successfully",
                },
                reassignar: {
                    label: "Reassign...",
                    button: "Reassign",
                    title: "Reassign task",
                    ok: "The task has been successfully reassigned",
                },
                delegar: {
                    label: "Delegate...",
                    button: "Delegate",
                    title: "Delegate task",
                    ok: "The task has been successfully delegated",
                },
                retomar: {
                    label: "Cancel delegation...",
                    button: "Cancel delegation",
                    title: "Cancel task delegation",
                    ok: "The task delegation has been successfully cancelled",
                },
                changeDataLimit: {
                    label: "Change deadline...",
                    button: "Change deadline",
                    title: "Change deadline",
                    ok: "The task has been successfully updated",
                },
                changePrioritat: {
                    label: "Change priority...",
                    button: "Change priority",
                    title: "Modify task priority",
                    ok: "The task has been successfully updated",
                },
                reobrir: {
                    label: "Reopen...",
                    button: "Reopen",
                    title: "Reopen task",
                    ok: "The task has been successfully reopened",
                },
                comment: {
                    ok: "Comment added to the task '{{data.expedientTasca.description}}'",
                },
            },
        },
        interessat: {
            title: "Interested Party",
            rep: "Representative",
            detall: {
				tipus: "Type",
                nif: "NIF/CIF/NIE",
                nom: "Name",
                raoSocial: "Company name",
                llinatges: "Surnames",
                telefon: "Phone",
                email: "Email",
                incapacitat: "Disability",
                direccio: "Address",
                direccioPostal: "Postal address",
                entregaDehObligat: "DEH required?",
            },
            action: {
                detail: {
                    title: "Interested Party Details",
                },
                new: {
                    label: "New Interested Party",
                    ok: "The interested party {{data.documentNum}} was created successfully",
                },
                update: {
                    ok: "The interested party {{data.documentNum}} was updated successfully",
                },
                delete: {
                    label: "Delete Interested Party",
                    check: "Are you sure you want to proceed with this action?",
                    description: "Once deleted, it cannot be recovered",
                    ok: "The interested party {{data.documentNum}} was deleted successfully",
                },
                createRep: {
                    label: "Add Representative",
                    ok: "The representative {{data.documentNum}} was created successfully",
                },
                updateRep: {
                    label: "Edit Representative",
                    ok: "The representative {{data.documentNum}} was updated successfully",
                },
                deleteRep: {
                    label: "Delete Representative",
                    check: "Are you sure you want to proceed with this action?",
                    description: "Once deleted, it cannot be recovered",
                    ok: "The representative {{data.documentNum}} was deleted successfully",
                },
                importar: {
                    label: "Import...",
                    title: "Import interested parties",
                    ok: "Interested parties imported successfully",
                },
                exportar: {
                    label: "Export...",
                    ok: "Interested parties exported successfully",
                    hint: "Select the interested parties you want to export.",
                },
                importSGD: {
                    label: "Import stakeholders from Registry...",
                    title: "Import stakeholders from Registry",
                    ok: "Stakeholders successfully imported",
                },
				gestGrups: {
				    label: "Manage groups...",
					title: "Manage groups",
				    ok: "Groups successfully modified",
				},
            },
            grid: {
                title: "Interested parties from file",
                representant: "Representative",
				tipus: {
					label: "Type",
					personaFisica: "Fisic person",
					personaJuridica: "Legal entity",
					administrador: "Administrator",
				},
            },
            alert: {
                incapacitat: "If the holder has a disability, it is mandatory to indicate a recipient.",
                jaExistentExpedient: "Already exists in the case file",
            },
			grup: {
				title: "Groups of interested parties",
				action: {
					new: {
						ok: "Group created successfully",
					},
					update: {
						ok: "Group updated successfully",
					},
					delete: {
					    label: "Delete Group Party",
					    check: "Are you sure you want to proceed with this action?",
					    description: "Once deleted, it cannot be recovered",
					    ok: "The group {{data.nom}} was deleted successfully",
					},
				},
			},
        },
        expedient: {
            title: "Case file",
            filter: {
                title: "Case file search"
            },
            detall: {
                title: "Case file information",
                agafatPer: "Taken by",
                avisos: "Warnings",
            },
            action: {
                new: {
                    label: "New file",
                    title: "Create new file",
                    ok: "The file '{{data.nom}}' has been created successfully.",
                },
                update: {
                    label: "Edit...",
                    title: "Edit case file",
                    ok: "The case file '{{data.nom}}' was updated successfully.",
                },
                detall: {
                    label: "Manage",
                },
                importar: {
                    label: "Import case file",
                    ok: "The case file was imported successfully",
                },
                agafar: {
                    label: "Lock",
                    ok: "The case file '{{expedient}}' has been taken by user '{{user}}'",
                },
                follow: {
                    label: "Follow",
                    ok: "User '{{user}}' started following case file '{{expedient}}'.",
                },
                unfollow: {
                    label: "Unfollow",
                    ok: "User '{{user}}' stopped following case file '{{expedient}}'.",
                },
                retornar: {
                    label: "Return",
                    ok: "The case file '{{expedient}}' has been returned to the original manager '{{user}}'",
                },
                lliberar: {
                    label: "Release",
                    ok: "The case file '{{expedient}}' has been released",
                },
                eliminar: {
                    label: "Delete",
                    ok: "The case file '{{data.nom}}' was deleted successfully",
                },
                close: {
                    label: "Close...",
                    button: "Close",
                    title: "Close case file",
                    titleMassive: "Closing {{num}} case files in bulk",
                    ok: "The case file '{{expedient}}' was closed successfully",
                },
                open: {
                    label: "Reopen",
                    description: "Do you want to reopen the case file?",
                    ok: "The case file '{{expedient}}' was reopened successfully",
                },
                download: {
                    label: "Download documents...",
                    button: "Download selected",
                    title: "Document selection",
                    ok: "Documents downloaded successfully",
                },
                exportFullCalcul: {
                    label: "Export spreadsheet",
                    ok: "Spreadsheet downloaded successfully",
                },
                exportZIP: {
                    label: "Export ZIP index",
                    button: "Export ZIP index",
                    title: "Export documents to ZIP",
                    ok: "ZIP document downloaded successfully",
                },
                exportPDF: {
                    label: "Export PDF index",
                    ok: "PDF document downloaded successfully",
                },
                exportCSV: {
                    label: "Export CSV index",
                    ok: "CSV index downloaded successfully",
                },
                exportEXCEL: {
                    label: "Export EXCEL index",
                    ok: "EXCEL index downloaded successfully",
                },
                exportPDF_ENI: {
                    label: "PDF index and ENI export",
                    ok: "Document downloaded successfully",
                },
                exportENI: {
                    label: "ENI export",
                    ok: "ENI document downloaded successfully",
                },
                exportINSIDE: {
                    label: "INSIDE export",
                    ok: "INSIDE document downloaded successfully",
                },
                exportDocs: {
                    label: "Export selected cases documents...",
                    ok: "The documents have been exported successfully",
                },
                export: {
                    label: "Export documents...",
                    button: "Export documents",
                    title: "Export documents",
                    ok: "Documents downloaded successfully",
                },
                sincronitzar: {
                    label: "Sync status with archive",
                    ok: "Archive status synchronized",
                },
                changePrioritat: {
                    label: "Change priority...",
                    button: "Change priority",
                    title: "Edit case file priority",
                    ok: "Priority of case file '{{expedient}}' was updated successfully.",
                    massiveOk: "The priority of '{{data.num}}' cases has been changed",
                },
                changeEstat: {
                    label: "Change status...",
                    button: "Change status",
                    title: "Edit case file status",
                    ok: "Status of case file '{{expedient}}' was updated successfully.",
                    massiveOk: "The status of '{{data.num}}' cases has been changed",
                },
                assignar: {
                    label: "Assign...",
                    button: "Assign",
                    title: "Assign case file to user",
                    ok: "Case file '{{expedient}}' was assigned successfully.",
                },
                relacio: {
                    label: "Relate...",
                    button: "Relate",
                    title: "Relate case file",
                    ok: "Relations of case file '{{expedient}}' were updated successfully.",
                },
                eliminarRelacio: {
                    label: "Delete relation",
                    ok: "The relation between the two case files was successfully deleted.",
                },
                excelInteressats: {
                    title: "Download Excel template to import interested parties",
                    ok: "Interested parties exported successfully",
                },
                impDocMass: {
                    label: "Import documents into the selected cases...",
                    title: "Document import",
                    mssg: "The documents you attach will be added to the {{num}} selected case files",
                    warning: "The case files must belong to the same procedure.",
                },
                exportMass: {
                    unic: "Export case...",
                    label: "Export selected cases...",
                    title: "Export selected cases",
                    titleUni: "Export cases",
                    info: "You can select various export formats. The export process will be executed in the background and you can track its progress in the massive actions list.",
                    info2: "You can select various export formats. The export can take a few moments to finish, once completed, the download will start automatically.",
                },                
                comment: {
                    ok: "Comment added to the case '{{data.expedient.description}}'",
                },
				moureTot: {
				    label: "Move all...",
                    button: "Move all",
				    title: "Move everything to the destination file",
				    ok: "The bulk action to move the file '{{expedient}}' has been created successfully.",
				},
            },
            alert: {
                owner: "You must reserve the case file in order to edit it",
                alert: "This case file has unread alerts",
                validation: "This case file has validation errors",
                esborranys: "There are draft documents (B) that must be finalized or removed in order to close the case file.\nThis action will finalize the documents and they can no longer be deleted.",
                borradors: "This case file contains drafts that will be deleted when closed. You can mark them to be signed with server signature before closing to avoid deletion. Invalid signatures will be removed and re-signed.",
                notificacio: "This case file contains expired, uncompleted notifications. An attempt will be made to update their status. Any new information will be saved in RIPEA, not in the Digital Archive.",
                documents: "This case file contains annex documents with errors. They will be reprocessed if possible. Otherwise, a copy will be saved in the Digital Archive without original signatures (both original and copy will remain accessible).",
                errorEnviament: "This case file has send errors",
                errorNotificacio: "This case file has notification errors",
                ambEnviamentsPendents: "This case file has pending sends to signature portal",
                ambNotificacionsPendents: "This case file has pending notifications",
                canviEstat: "It is necessary to select a procedure in order to perform the mass action",
				moureTot: {
					info: "The file is currently locked due to an ongoing background execution.\nUntil it completes, modifications will not be possible. Check pending massive actions to know their status.",
				  	title: "You are about to start a mass action that will move the following information to the destination file:",
				  	items: [
					    "Documents and folders",
					    "Interested parties",
					    "Followers",
					    "Related cases",
					    "Registry entries",
					    "Comments"
				  ],
				},
            },
            modal: {
                seguidors: {
                    label: "Followers",
                    title: "Case followers",
                },
            },
            results: {
                checkDelete: "Are you sure you want to delete this content? If it contained ongoing signatures, they will be canceled.",
				checkRelacio: "Are you sure you want to delete this relation?",
                actionOk: "Action executed successfully.",
                actionBackgroundOk: "Action has been scheduled for background execution. You can track it in the mass actions list.",
            }
        },
        arxiu: {
            detall: {
                arxiuUuid: "File identifier",
                fitxerNom: "File name",
                serie: "Documentary series",
                arxiuEstat: "File status",
                dades: "General data of the content",
                fitxerContentType: "MIME type",
                metadata: "ENI metadata",
                versions: "Version",
                identificador: "Identifier",
                organ: "Authority",
                dataCaptura: "Capture date",
                dataApertura: "Opening date",
                dataTancament: "Closing date",
                origen: "Origin",
                estadoElaboracion: "Drafting status",
                tipoDocumental: "NTI documentary type",
                format: "Format name",
                clasificacion: "Classification",
                estat: "Status",
                interessats: "Interested parties",
                firmes: "Signature type",
                documentOrigen: "Source document ID",
            },
            firma: {
                title: "Signature",
                perfil: "Signature profile",
                fitxerNom: "File name",
                tipusMime: "MIME type",
                contingut: "CSV",
                csvRegulacio: "CSV regulation",
                responsableNom: "Responsible person's name",
                responsableNif: "Responsible person's ID",
                data: "Signature date",
                emissorCertificat: "Certificate issuer"
            },
            tabs: {
                resum: "Information",
                fills: "Children",
                firmes: "Signatures",
                data: "Metadata",
            },
        },
        document: {
            title: "Document",
            view: {
                title: "View type",
                estat: "View by status",
                nullEstat: "No status",
                tipus: "View by document type",
                carpeta: "View by folder",
            },
            tabs: {
                resum: "Content",
                version: "Versions",
                file: "File",
                scaner: "Scan",
                firmes: "Signatures",
            },
            detall: {
                createdDate: "Creation date",
                createdBy: "Created by",
                dataCaptura: "Capture date",
                csv: "CSV",
                flux: "A predefined signature flow exists. Creating a new flow will overwrite the selected one.",
                summarize: "Generate title and description using artificial intelligence.\n(Requires a document to be attached first)",
                documentOrigenFormat: "Format: ES_<Organ>_<YYYY>_<Specific_ID>",
                dataBasic: "Basic data",
                dataInteressat: "Interested party data",
                dataEspecific: "Specific data",
                dadesRegistrals: "Registry data",
                fetRegistral: "Registry fact",
                naixement: "Birth",
                dadesAdicionals: "Additional data",
                dataOther: "Other data",
                senseTipus: "No type assigned",
                extensio: "Extension",
                ruta: "Path",
                mida: "Size",
                tipusDocumentDefault: "Document type to apply to all files",
            },
            action: {
                new: {
                    dropMessg: "Drag the file here or onto the table...",
                    ok: "The document {{data.nom}} has been created successfully"
                },
                update: {
                    ok: "The document {{data.nom}} has been updated successfully"
                },
                delete: {
                    label: "Delete",
                    check: "Are you sure you want to delete this content?",
                    description: "Once deleted it cannot be recovered. If it contained ongoing signatures, they will be canceled.",
                    ok: "The document {{data.nom}} has been deleted successfully"
                },
                pinbal: {
                    label: "PINBAL query...",
                    button: "PINBAL query",
                    title: "New PINBAL query",
                    ok: "Document created from PINBAL query '{{codiServeiPinbal}}'",
                },
				import: {
					close: {
					    check: "Are you sure you want to close this window?",
					    description: "The import will continue in the background and you can check the result in the file later.",
					},
					cancel: {
					    check: "Are you sure you want to cancel the import?",
					    description: "The documents imported up to this point will be kept in the file.",
					},
				},
				importSgd: {
				    label: "Import documents from SGD...",
				    title: "Import documents from SGD",
				    ok: "Documents and Interested imported successfully",
					interessats: "Select the interested parties you wish to associate with the file",
					interessat: {
					    tipus: {
					        1: "Administration",
					        2: "Natural person",
					        3: "Legal entity",
					    },
					},
					resultat: {
						title: "Result:",
						ok: "Import process completed",
						documents: "Documents processed correctly: ",
						interessats: "Interested processed correctly: ",
						carpetes: "Folders created successfully: ",
						errors: "Detailed errors: ",
					}
				},
                importZip: {
                    label: "Import documents from ZIP...",
                    title: "Import documentos from ZIP",
                    ok: "Documents imported successfully",
					resultat: {
						title: "Result:",
						ok: "Import process completed",
						documents: {
							ok: "Documents processed correctly: ",
							ko: "Documents with errors: ",
							firma: "Documents with signature errors: ",
						},
						carpetes: {
							ok: "Folders created successfully: ",
						},
						tamany: "Total size processed: ",
						errors: "Detailed errors: ",
					},
                },
                detall: {
                    label: "Details",
                    noUuid: "The document is not synchronized with the archive",
                },
                imprimible: {
                    label: "Printable authentic copy",
                    ok: "The printable authentic copy has been downloaded successfully"
                },
                original: {
                    label: "Download original",
                    ok: "Original document downloaded successfully",
                },
                download: {
                    firma: "Download signature",
                    ok: "Document downloaded successfully",
                },
                firma: {
                    label: "Sign from the browser...",
                    button: "Start signing process",
                    title: "Sign from the browser",
                    ok: "Document signed successfully",
                },
                view: {
                    label: "View",
                    title: "View",
                },
                csv: {
                    label: "Copy CSV link",
                    ok: "CSV link copied successfully",
                },
                portafirmes: {
                    label: "Send to Portafirmes...",
                    button: "Send to Portafirmes",
                    title: "Send document to Portafirmes",
                    ok: "Document '{{document}}' sent to Portafirmes",
                },
                toPDF: {
                    description: "Document format will be changed before sending to portafirmas",
                    title: "View PDF version",
                },
                firmar: {
                    label: "Browser signature...",
                    button: "Start signing process",
                },
                viaFirma: {
                    label: "Send viaFirma...",
                    button: "Send to viaFirma",
                    title: "Send document to viaFirma",
                    ok: "Document '{{document}}' sent to viaFirma",
                },
                mail: {
                    label: "Send via email...",
                    button: "Send via email",
                    title: "Send document by email",
                    ok: "Document '{{document}}' sent via email",
                },
                seguiment: {
                    label: "Portafirmes tracking",
                    title: "Signature details",
                },
                cancel: {
                    label: "Cancel sending",
                    ok: "The signature has been cancelled successfully",
                    check: "Are you sure you want to continue with this action?",
                    description: "Once deleted, it cannot be recovered",
                },
                notificar: {
                    label: "Notify or communicate...",
                    button: "Notify",
                    title: "Create document notification",
                    ok: "Notification created successfully",
                    alert: {
                        interessatsAmbAvis: {
                            title: "There are notifications with recipients without a NIF/NIE. These notifications cannot be sent to the citizen folder, as a NIF or NIE is required to access it.",
                            description: "The notifications without NIF/NIE are the following:",
                            marcat: {
                                title: "If postal delivery is selected:",
                                description: "The notification will be sent by postal mail, provided that the managing body has a defined CIE (Printing and Enveloping Center).",
                            },
                            noMarcat: {
                                title: "If postal delivery is NOT selected:",
                                description: "The electronic notification will not be carried out. Instead, an email will be sent to inform the recipient that they will soon receive a notification by postal mail.",
                                warning: "It is necessary to make the notification on paper."
                            },
                        },
                        administracioSir: {
                            title: "One of the selected recipients is a SIR administration.",
                            warning: "ALL deliveries will be sent as COMMUNICATION type.",
                        },
                    }
                },
                notificarMasiva: {
                    label: "Notify or communicate...",
                    button: "Notify",
                    title: "Generate document to notify",
                    ok: "A zip with the selected items has been generated",
                },
                comunicar: {
                    label: "Communicate...",
                },
                publicar: {
                    label: "Publish...",
                    button: "Publish",
                    title: "Create publication",
                    ok: "Publication created successfully",
                },
                descarregarOriginal: {
                    label: "Original document",
                    ok: "The original document has been downloaded successfully"
                },
                descarregarImprimible: {
                    label: "Download authentic copy",
                    ok: "The authentic copy has been downloaded successfully",
                },
                changeType: {
                    label: "Change type...",
                    button: "Change type",
                    title: "Change type",
                    ok: "Documents have been modified successfully",
                },
                definitive: {
                    label: "Convert to definitive",
                    description: "This action will make the documents part of the file definitively and they cannot be deleted.",
                    ok: "Document '{{document}}' changed to definitive",
                    massiveOk: " '{{data.num}}' documents have been marked as final",
                },
            },
            alert: {
                import: "Document imported",
                delete: "Draft document",
                firma: "Document signed",
                original: "This document contained invalid signatures and has been cloned and signed on the server to be saved in the Digital Archive. The original can be downloaded from the actions menu",
                custodiar: "Pending to custody signed portafirmas document",
                moure: "The annotation's document is pending to be moved to the procedural document series",
                definitiu: "Definitive document",
                firmaPendent: "Pending signature",
                firmaParcial: "Partially signed",
                errorPortafirmes: "Error sending to portafirmas",
                funcionariHabilitatDigitalib: "You must be an authorized official in DIGITALIB",
                folder: "If no folder is selected, documents will be imported directly into the file.",
                scaned: "The scanning process was successful.",
                view: "Only for PDF, ODT and DOCX",
                portafirmes: "Es necesario seleccionar un procedimiento y un tipo de documento para poder realizar la acción masiva",
                documentsZip: "At least one document must be selected to perform the import",
            },
            versio: {
                title: "Version",
                data: "Date",
                arxiuUuid: "File UUID",
            },
        },
        carpeta: {
            title: "Folder",
            detail: {
                tite: "Folder details",
            },
            action: {
                new: {
                    label: "Folder...",
                    ok: "Folder '{{data.nom}}' created successfully",
                },
                update: {
                    label: "Edit...",
                    title: "Edit folder",
                    ok: "Folder '{{data.nom}}' updated successfully",
                },
                delete: {
                    label: "Delete...",
                    check: "Are you sure you want to proceed with this action?",
                    description: "Once deleted, it cannot be recovered",
                    ok: "Folder '{{data.nom}}' deleted successfully",
                }
			},
			restriccions: {
				 title: "Select the users who will have access to the folder (from those who already have access to the procedure)",
			     notEmpty: {
				 		message: "At least one user must be selected to create the restriction"	
				 }
			}
        },
        dada: {
            title: "value for data '{{metaDada}}'",
            noRowsText: "There are no values for this data",
            grid: {
                valor: "Data value",
            },
            mensajeToolbar: {
                permis: "You do not have permission to manage the values of this data.",
                maxDades: "This type of data only allows a single value.",
            },
            action: {
                new: {
                    label: "Add value for data",
                    ok: "The data {{data.valor}} has been created successfully",
                },
                update: {
                    ok: "The data {{data.valor}} has been updated successfully",
                },
                delete: {
                    ok: "The data {{data.valor}} has been deleted successfully",
                },
            },
        },
        metaDada: {
            title: "Meta-data",
            plural: "Meta-datas",
            detail: {
                title: "Metadata details",
                value: "Values of the metadata '{{metaDada}}'",
            },
            action: {
                activar: {
                    label: "Activate",
                    ok: "Meta-data activated",
                },
                desactivar: {
                    label: "Deactivate",
                    ok: "Meta-data deactivated",
                },
                new: {
                    label: "New Meta-data",
                    ok: "Meta-data created successfully",
                },
                update: {
                    ok: "Meta-data updated successfully",
                },
                delete: {
                    ok: "Meta-data deleted successfully",
                },
            },
        },
        registre: {
            grid: {
                extracte: "Summary",
                nomAnnex: "Annex name",
                origenRegistreNumero: "Registry number",
                data: "Registration date",
                dataRecepcio: "Reception date",
                destiDescripcio: "Destination",
                interessats: "Interested parties",
                dataExpedient: "Expedient created on",
            },
            detall: {
                tipus: "Type",
                entrada: "Entrance",
                oficina: "Office",
                extracte: "Extract",
                observacions: "Observations",
                identificador: "Origin Number",
                data: "Origin Date",
                oficinaDescripcio: "Origin Office",
                docFisica: "Physical Documentation",
                desti: "Destination Body",
                refExterna: "External Reference",
                expedientNumero: "Case Number",
                procediment: "Procedure",
                llibre: "Book",
                assumpte: "Type of Matter",
                idioma: "Language",
                assumpteCodi: "Matter Code",
                transport: "Transport",
                transportNumero: "Transport Number",
                origenRegistreNumero: "Origin Number",
                origenData: "Origin Date",

                required: "Mandatory Data",
                optional: "Optional Data",
                infoResumida: "Summarized Registration Information",
                interessats: "Interested Parties",
                identifier: "Identification",
                registre: "Registration information",
                annexos: "Annexes",
            },
            justificant: {
                ntiFechaCaptura: "Capture date (ENI)",
                ntiOrigen: "Origin (ENI)",
                ntiTipoDocumental: "Document type (ENI)",
                uuid: "Identifier",
                titol: "File",
                firmaTipus: "Signature type",
                firmaPerfil: "Signature profile",
            }
        },
        notificacio: {
            title: "Notification",
            tabs: {
                dades: "Data",
                errors: "Errors",
            },
            detall: {
                title: "Notification details",
                notificacioDades: "Notification data",
                notificacioDocument: "Notification document",
                error: "Errors occurred while sending the notification",
                fitxerNom: "File name",
                notificacioEstat: "Status",
                createdDate: "Sent on",
                entregaPostal: "Postal delivery",
                serveiTipusEnum: "Service type",
                notificacioIdentificador: "Identifier",
            },
            action: {
                update: {
                    ok: "The dispatch {{data.assumpte}} has been updated successfully",
                },
                actualitzarEstat: {
                    label: "Update status",
                    ok: "Status has been updated successfully",
                    massiveOk: "The massive execution has been created. You can check its status in the massive actions list.",
                },
                notificacioInteressat: {
                    label: "Deliveries",
                    title: "Deliveries",
                    ok: "",
                },
                justificant: {
                    label: "Delivery receipt",
                    ok: "The receipt has been downloaded",
                },
                documentEnviat: {
                    label: "Sent document",
                    ok: "The sent document has been downloaded",
                },
            },
        },
        notificacioInteressat: {
            tabs: {
                dades: "Data",
                notif: "Notific@",
            },
            detall: {
                noEnviat: "Not send t Notific@",
                title: "Shipping details",
                datat: "Dated",
                certificacio: "Certification",
                enviament: "Shipping data",
                interessat: "Holder data",
                representant: "Recipient data",

                enviamentCertificacioData: "Date",
                enviamentCertificacioOrigen: "Origin",
                enviamentReferencia: "Reference",
                entregaNif: "DEH NIF",
                classificacio: "DEH procedure",
                enviamentDatatEstat: "Status",
                registreEstat: "Registry status",
            },
            action: {
                ampliarPlac: {
                    label: "Extend deadline...",
                    button: "Extend deadline",
                    title: "Extension of the deadline for the batch shipments",
                    ok: "The deadline has been extended",
                },
                certificat: {
                    label: "Certification",
                    ok: "The certificate has been downloaded successfully",
                },
            },
        },
        publicacio: {
            title: "Publication",
            detall: {
                title: "Publication detail",
                document: "Document",
                enviatData: "Sent date",
                estat: "Status",
                tipus: "Type",
                assumpte: "Subject",
                observacions: "Observations",
            },
            action: {
                update: {
                    ok: "The publication {{data.assumpte}} has been successfully updated",
                },
                delete: {
                    ok: "The publication {{data.assumpte}} has been successfully deleted",
                }
            },
        },
        documentVia: {
            tabs: {
                dades: "Data",
                errors: "Errors",
            },
            alert: {
                reintentar: "Retry sending",
                enviament: "Errors occurred while sending the document to viaFirma",
                processament: "Errors occurred while processing the document signature",
                cancelat: "The signature has been canceled",
            },
        },
        grup: {
            title: "Group",
            detail: {
                title: "Group details",
            },
            grid: {
                default: "Default",
            },
            action: {
                new: {
                    label: "New Group",
                    ok: "Group '{{data.codi}}' created successfully",
                },
                update: {
                    ok: "Group '{{data.codi}}' updated successfully",
                },
                delete: {
                    ok: "Group '{{data.codi}}' deleted successfully",
                },
                link: {
                    label: "Link group...",
                    button: "Link group",
                    title: "Link group",
                    ok: "Group linked",
                },
                unlink: {
                    label: "Unlink",
                    ok: "Group unlinked",
                },
                default: {
                    label: "Set as default",
                    ok: "Group set as default",
                },
                undefault: {
                    label: "Remove default",
                    ok: "Group unmarked as default",
                },
            },
        },
        organGestor: {
            title: "Managing Body",
            action: {
                update: {
                    ok: "Managing Body '{{data.codi}}' deleted successfully",
                },
                actualitzar: {
                    title: "Synchronization forecast",
                    label: "Update managing bodies from DIR3",
                    ok: "The bodies are up to date",
                    button: "Synchronize",
                    tabs: {
                        empty: "The bodies are up to date",
                        firstSync: "First synchronization",
                        split: "Splits",
                        merge: "Mergers",
                        subst: "Substitutions",
                        change: "Attribute changes",
                        new: "New",
                        del: "Decommissioned",
                    },
                },
                vista: "Change view",
                pdf: "Download PDF",
            },
        },
        tipusDocumental: {
            title: "Document type",
            action: {
                new: {
                    label: "New document type",
                    ok: "Document type '{{data.codi}}' created successfully",
                },
                update: {
                    ok: "Document type '{{data.codi}}' updated successfully",
                },
                delete: {
                    ok: "Document type '{{data.codi}}' deleted successfully",
                },
            },
        },
        metaExpedient: {
            title: "Procedure",
            columnes: {
                comu: "Common",
                directe: "Direct",
                grups: "Groups",
                actiu: "Active",
                estat: "Status",
            },
            detall: {
                elementsProc: "Procedure management: {{nom}}",
                elementsServ: "Service management: {{nom}}",
                expressioNumero: "If no expression is specified, the following default will be used: {{codi}}/{{seq}}/{{any}}",
                permisDirecte: "An entity administrator user can modify this value.",
                responsable: "You can change the signature responsible",
                portafirmesResponsables: "You can change the signature responsibles",
                regla: {
                    create: "Created",
                    data: "Creation date",
                    activa: "Active",
                    nom: "Name",
                },
            },
            tabs: {
                dades: "Data",
                estat: "Review status",

                metaDocument: "Document types",
                metaDada: "Metadata",
                expedientEstat: "Statuses",
                tasca: "Tasks",
                grup: "Groups",
                carpeta: "Folders",
            },
            action: {
                new: {
                    label: "New procedure",
                    ok: "Procedure created successfully",
                },
                update: {
                    ok: "Procedure updated successfully",
                },
                delete: {
                    ok: "Procedure deleted successfully",
                },
                consultar: {
                    title: "Procedure details",
                    label: "View",
                    revisat: "This procedure cannot be modified because it is in a reviewed state",
                },
                canviEstat: {
                    label: "Change review status...",
                    button: "Change status",
                    title: "Change review status",
                    ok: "Status changed successfully",
                },
                expedient: {
                    title: "Procedure records: {{nom}}",
                    label: "Records",
                },
                regla: {
                    title: "Distribution rule status",
                    label: "Distribution rule",
                    create: {
                        label: "Create distribution rule",
                        ok: "The rule with code '{{nom}}' has been created successfully.",
                    },
                    active: {
                        label: "Activate distribution rule",
                        ok: "The rule with code '{{nom}}' has been activated successfully.",
                    },
                    desactive: {
                        label: "Deactivate distribution rule",
                        ok: "The rule with code '{{nom}}' has been deactivated successfully",
                    },
                },
                activar: {
                    label: "Activate",
                    ok: "Procedure activated successfully",
                },
                desactivar: {
                    label: "Deactivate",
                    ok: "Procedure deactivated successfully",
                },
                comment: {
                    ok: "Comment added to procedure '{{data.metaExpedient.description}}'",
                },
                importRolsac: {
                    label: "Import from ROLSAC...",
                    title: "Import procedure from ROLSAC",
                },
                importFitxer: {
                    label: "Import from file...",
                    title: "Import procedure",
                    ok: "Procedure imported successfully",
                },
                export: {
                    ok: "Procedure exported successfully",
                },
                clonar: {
                    label: "Clone",
                    title: "Clone procedure",
                    ok: "New cloned procedure: {{codi}}",
                },
                canviPendent: {
                    label: "Mark as pending of review",
                    ok: "Procedure marked as pending of review",
                },                
                canviDisseny: {
                    label: "Mark as design process",
                    ok: "Procedure marked as a design process",
                },
                actualize: {
                    label: "Update from ROLSAC...",
                    button: "Update from ROLSAC",
                    title: "Procedure update",
                    description: "Do you want to update the procedures with information from ROLSAC?",
                    ok: "Procedures updated",
                    result: {
                        title: "Start of the procedure update process",
                        description: "'{{numOperacions}}' requests have been made, '{{numActualitzats}}' procedures have been updated, and '{{numErrord}}' resulted in errors",
                        senseCanvi: "No changes",
                    }
                }
            },
            alert: {
                pendentsRevisio: "There are {{num}} procedures or services pending review",
            },
        },
        metaDocument: {
            title: "Document type",
            detail: {
                title: "Document type details",
            },
            tabs: {
                dades: "Data",
                nti: "NTI data",
                portafirmes: "Signing with signature workflow",
                navegador: "Browser-based signing",
                viaFirma: "Signing via viaFirma",
                pinbal: "PINBAL",
            },
            action: {
                default: {
                    label: "Set as default",
                    ok: "Document type set as default",
                },
                undefault: {
                    label: "Unset as default",
                    ok: "Document type unset as default",
                },
                activar: {
                    label: "Activate",
                    ok: "Document type activated",
                },
                desactivar: {
                    label: "Deactivate",
                    ok: "Document type deactivated",
                },
                new: {
                    label: "New document type",
                    ok: "Document type created successfully",
                },
                update: {
                    ok: "Document type updated successfully",
                },
                delete: {
                    ok: "Document type deleted successfully",
                },
            },
        },
        expedientEstat: {
            title: "Procedure status",
            detail: {
                title: "Procedure status details",
            },
            action: {
                new: {
                    label: "New status",
                    ok: "Status created successfully",
                },
                update: {
                    ok: "Status updated successfully",
                },
                delete: {
                    ok: "Status deleted successfully",
                },
            },
        },
        metaExpedientTasca: {
            title: "Task",
            detall: {
                title: "Task details",
                duracio: "Task duration in calendar days.",
                validacio: "Task validations: {{nom}}",
            },
            action: {
                activar: {
                    label: "Activate",
                    ok: "Task activated",
                },
                desactivar: {
                    label: "Deactivate",
                    ok: "Task deactivated",
                },
                new: {
                    label: "New task",
                    ok: "Task created successfully",
                },
                update: {
                    ok: "Task updated successfully",
                },
                delete: {
                    ok: "Task deleted successfully",
                },
            },
        },
        metaExpedientTascaValidacio: {
            title: "Validation",
            detail: {
                title: "Validation details",
            },
            action: {
                activate: {
                    label: "Activate",
                    ok: "Validation activated",
                },
                deactivate: {
                    label: "Deactivate",
                    ok: "Validation deactivated",
                },
                new: {
                    label: "New validation",
                    ok: "Validation created successfully",
                },
                update: {
                    ok: "Validation updated successfully",
                },
                delete: {
                    ok: "Validation deleted successfully",
                },
            },
        },
        domini: {
            title: "Domain",
            action: {
                cleanCache: {
                    label: "Clear cache",
                    ok: "The cache has been cleared successfully",
                },
                new: {
                    label: "Add domain",
                    ok: "Domain created successfully",
                },
                update: {
                    ok: "Domain updated successfully",
                },
                delete: {
                    ok: "Domain deleted successfully",
                },
            },
        },
        entitat: {
            title: "Entity",
            form: {
                temaClar: "Light theme configuration",
                temaFosc: "Dark theme configuration",
            },
            action: {
                new: {
                    label: "New entity",
                    ok: "Entity successfully created",
                },
                update: {
                    ok: "Entity successfully updated",
                },
                delete: {
                    ok: "Entity successfully deleted",
                },
                config: {
                    label: "Configure",
                },
                activar: {
                    label: "Activate",
                    ok: "Entity activated",
                },
                desactivar: {
                    label: "Deactivate",
                    ok: "Entity deactivated",
                },
            },
        },
        avis: {
            title: "Notice",
            action: {
                new: {
                    label: "New notice",
                    ok: "Notice successfully created",
                },
                update: {
                    ok: "Notice successfully updated",
                },
                delete: {
                    ok: "Notice successfully deleted",
                },
                activar: {
                    label: "Activate",
                    ok: "Notice activated",
                },
                desactivar: {
                    label: "Deactivate",
                    ok: "Notice deactivated",
                },
            },
        },
        pinbalServei: {
            title: "Pinbal service",
            action: {
                update: {
                    ok: "Pinbal service successfully updated",
                },
            },
        },
        urlInstruccio: {
            title: "URL de instrucción",
            detall: {
                url: "Available formats:\n - http://URL.es/alegar/[ENI]",
            },
            action: {
                new: {
                    label: "Nueva URL de instrucción",
                    ok: "URL creada correctamente",
                },
                update: {
                    ok: "URL modificada correctamente",
                },
                delete: {
                    ok: "URL eliminada correctamente",
                },
            },
        },
        propietats: {
            title: "Property",
            empty: "No properties found",
            action: {
                sync: {
                    label: "Synchronize with JBoss",
                    ok: "The properties have been successfully synchronized",
                },
                new: {
                    label: "Add specific config",
                    ok: "The property has been successfully created",
                },
                update: {
                    ok: "The property has been successfully updated",
                },
                delete: {
                    ok: "The property has been successfully deleted",
                },
            }
        },
        exception: {
            action: {
                detail: {
                    title: "Exception details",
                }
            }
        },
        integracio: {
            action: {
                detail: {
                    title: "Integration communication details",
                },
                diagnostic: {
                    title: "Diagnostic",
                    label: "Repeat diagnostic",
                },
                diagnosticAll: {
                    title: "External systems diagnostics",
                    label: "Diagnose",
                },
                reiniciar: {
                    label: "Restart plugin",
                    ok: "The plugin with code '{{nom}}' has been successfully restarted",
                },
                reiniciarAll: {
                    label: "Restart all",
                    ok: "All plugins have been successfully restarted",
                },
            }
        },
        sistema: {
            detail: {
                sistemaOperatiu: "Operating system",
                arquitectura: "Architecture",
                processadors: "Processors",
                jbossVersion: "JBoss version",
                applicationServerInfo: "Application server information",
                tempsFuncionant: "Uptime",
                jvmMemory: "Java Virtual Machine",
                disksUsage: "Disk and CPU",
            },
            tabs: {
                sistema: "System",
                fils: "Execution threads",
                tasques: "Background tasks",
            },
            action: {
                restart: {
                    label: "Restart",
                    ok: "The task has been successfully restarted",
                },
                restartAll: {
                    label: "Restart selected",
                    ok: "All tasks have been successfully restarted",
                },
            }
        },
        permision: {
            title: "Permissions",
            grid: {
                organGestor: "Managing body",
                principal: "Type",
                sid: "Principal",
                create: "Create",
                read: "View",
                write: "Edit",
                delete: "Delete",
                estadistic: "Statistics",
            },
            tabs: {
                expedient: "Case management",
                admin: "Administration and design",
            },
            action: {
                new: {
                    label: "New permission",
                    title: "Create new permission",
                    ok: "Permission for '{{data.principal}} {{data.sid}}' has been created successfully",
                },
                update: {
                    title: "Edit permission",
                    ok: "Permission for '{{data.principal}} {{data.sid}}' has been updated successfully",
                },
                delete: {
                    check: "¿Está seguro de que desea continuar con esta acción?",
                    description: "Una vez eliminada, no se podrá recuperar",
                    ok: "Permission for '{{data.principal}} {{data.sid}}' has been deleted successfully",
                },
            },
        },
        user: {
            options: {
                perfil: "My profile",
                manual: "User manual",
                manualAdmin: "Administrator manual",
                logout: "Log out",
                noOrgans: "No managing body assigned",
            },
            menu: {
                title: "Menu",

                entitat: "Entities",
                expedient: "Cases",
                monitoritzar: "Monitor",
                integracions: "Integrations",
                excepcions: "Exceptions",
                monitor: "System monitor",

                config: "Configuration",
                props: "Configurable properties",
                pinbal: "PINBAL services",
                segonPla: "Restart background tasks...",
                plugins: "Restart plugins...",
                avisos: "Notifications",
                backVersio: "Classic interface",

                anotacions: "Annotations",
                procediments: "Procedures and services",
                procedimentsTitle: "Procedure and service management",
                procedimentsRevisorTitle: "Procedure and service review",
                procedimentPermis: "Procedure permissions: {{nom}}",
                grups: "Groups",
                grupPermis: "Group permissions",
                revisar: "Procedure and service review",
                tasca: "Tasks",
                flux: "Signature workflows",

                consultar: "Consult",
                continguts: "Contents",
                dadesEstadistiques: "Statistical data",
                portafib: "Documents sent to Portafib",
                notib: "Batches sent to Notib",
                pinbalEnviades: "Queries sent to PINBAL",
                assignacio: "Task assignment",
                pendents: "Cases pending distribution",
                comunicades: "Communicated annotations",

                documents: "Document types",
                documentDada: "Metadata of the document type: {{nom}}",
                nti: "NTI document types",
                dominis: "Domains",
                organs: "Managing bodies",
                organPermis: "Permissions of the managing body: {{nom}}",
                url: "Instruction URLs",
                permisos: "Entity permissions"
            },
            massive: {
                title: "Massive action",
                portafirmes: "Send documents to portafirmes",
                firmar: "Sign documents from browser",
                marcar: "Mark as final",
                estat: "Change case status",
                tancar: "Close cases",
                custodiar: "Safeguard pending items",
                csv: "Copy CSV link",
                anexos: "Attach pending annexes from accepted annotations",
                anotacio: "Update annotation status in Distribution",
                prioritat: "Change case priority",
                refresh: "Refresh data every 10 seconds"
            },
            action: {
                masives: {
                    label: "View massive actions",
                    title: "{{name}} mass executions",
                    detail: "Detail of the mass action: {{tipus}}",
                    ok: "The document has been downloaded successfully",
                    pending: "This item is currently being processed",
                },
            },
            perfil: {
                title: "My profile",
                ok: "The user's data '{{nom}}' has been successfully updated",
                dades: "User data",
                correu: "Email sending",
                generic: "General settings",
                column: "Column configuration of case list",
                vista: "Document view settings in cases",
                moure: "Destination view settings when moving documents",
                interficie: "Default interface",
                tema: "Theme customization",
                foscor: "Dark theme level",
                colorPrincipal: "Primary color",
                colorSecundari: "Secondary color",
                colorReset: "Default"
            }
        },
        alert: {
            title: "Case validation errors",
            action: {
                read: {
                    label: "Mark as read",
                    title: "Case alerts",
                    ok: "The alert has been marked as read",
                    massiveOk: "The alerts have been marked as read",
                },
            },
            errors: {
                metaDada: "The following data is missing:",
                metaDocument: "The following documents are missing:",
                metaNode: "There are documents without an assigned document type",
                noFinalitzades: "There are notifications with a non-final status",
                interessatObligatori: "An interested party is missing",
            },
        },
        accesibilitat: {
            title: "Accessibility Declaration",
            intro: {
                title: "Introduction",
                p1Part1: "The Government of the Balearic Islands has committed to making its website and mobile application accessible, in accordance with",
                p1LinkText: "Royal Decree 1112/2018",
                p1Part2: ", of 7 September, on the accessibility of websites and mobile applications in the public sector.",
                p2Part1: "This accessibility statement applies to the website",
                p2Part2: "and excludes pages that lead to external links.",
            },
            compliment: {
                title: "Compliance status",
                introPart1: "This website is partially compliant with",
                introLinkText: "Royal Decree 1112/2018",
                introPart2: "due to the exceptions and non-conformity of the aspects listed below.",
                criteri1: "Criterion A - 4.1.2 Name, Role, Value: Some icon buttons lacked an accessible text alternative. Solution applied: the \"title\" and \"aria-label\" attributes with descriptive text have been added to all buttons that did not have a visible label, ensuring screen readers can identify their function.",
                criteri2: "Criterion A - 1.1.1 Non-text Content: Some informational images did not have alternative text. Solution applied: an audit of all graphic resources has been performed. Non-decorative images now include a descriptive and contextual \"alt\" attribute. Purely decorative images use empty \"alt\" or \"role=presentation\" to be ignored by assistive technologies.",
                criteri3: "Criterion AA - 1.4.4 Resize Text: Text was truncated when scaling the interface to 200%. Solution applied: on reduced screen sizes, buttons display only the icon accompanied by a descriptive \"title\" attribute. The use of \"overflow: hidden\" in text containers has been removed, and it has been verified that all content remains accessible at 200% zoom.",
                criteri4: "Criterion AA - 2.5.8 Target Size (Minimum): Certain interactive elements did not meet the minimum size of 24x24 pixels or the required spacing. Solution applied: the positioning and layout of components has been modified to ensure an adequate click area and a minimum spacing of 8 pixels between interactive elements, facilitating their use on touch devices and for people with mobility difficulties.",
            },
            noAccesible: {
                title: "List of inaccessible content and explanation",
                item1: {
                    title: "Identification of the main language",
                    desc1: "The language code used to identify the main language is not a valid code.",
                    desc2: "In the generated source code, the lang attribute of the HTML node has the value \"ca\", which is valid as an IANA language code. The error may occur because there may be specific texts (those stored in the database and entered by the user) that do not match the indicated language, or because by architecture, language management is performed through the user session and the React application context, not through static HTML attributes. As a compensatory measure, the \"lang\" attribute is dynamically injected into the main container according to the language selected by the user.",
                },
                item2: {
                    title: "Forms and labels",
                    desc1: "Explicit association between controls and labels is not properly implemented.",
                    desc2: "In selector-type form fields, the input identifier does not match the \"for\" attribute of the label. The validator used strictly reports the error, although the Material UI (MUI) library claims to comply with WCAG 2.1 accessibility standards; in this case it only partially complies by compensating with the \"aria-labelledby\" attribute, which provides a correct accessible name for screen readers.",
                },
                item3: {
                    title: "Multiple navigation methods",
                    desc1: "Absence of a link to the site map and a site search engine.",
                    desc2: "Warning level for not providing any complementary navigation method such as a site map or a search option on the website.",
                },
            },
            preparacio: {
                title: "Preparation of this declaration",
                elaborat: "This declaration has been prepared through a self-assessment conducted by the development team using the automated tool: Web Crawler of the Web Accessibility Observatory.",
                dataPrep: "Preparation date: 01/05/2026",
                darreraRevisio: "Last review: 05/05/2026",
                properaRevisio: "Next scheduled review: 05/05/2027",
                norma: "Reference standard: UNE-EN 301549:2022, levels A and AA",
                resultatTitle: "Result",
                puntuacioLabel: "Average website score",
                puntuacioVal: "8.16",
                nivellLabel: "Estimated adequacy level",
                nivellVal: "A",
                situacioLabel: "Estimated compliance status",
                situacioVal: "Partially compliant",
                responsive: "The website is designed for responsive display, so that it displays optimally on tablet and mobile devices.",
            },
            contacte: {
                title: "Observations and contact details",
                p1: "The Government of the Balearic Islands intends to continue improving and offer citizens the best possible service. You may submit communications regarding accessibility requirements (article 10.2.a) of RD 1112/2018, such as:",
                li1: "Reporting any possible non-compliance by this website",
                li2: "Reporting other difficulties accessing content",
                li3: "Submitting any other query or suggestion for improvement regarding the accessibility of the website",
                p2Part1: "Through the following",
                p2LinkText: "contact form",
                p2Part2: "or by calling 971177140. You may submit:",
                li4: "A complaint regarding compliance with the requirements of RD 1112/2018, or",
                li5: "A request for accessible information regarding:",
                li5a: "Content excluded from the scope of RD 1112/2018 as established in article 3, paragraph 4, or",
                li5b: "Content exempt from accessibility requirements due to disproportionate burden.",
                p4Part1: "Through the following procedure:",
                p4LinkText: "Requests for accessible information and complaints regarding the accessibility of websites and mobile applications.",
            },
            procediment: {
                title: "Application procedure",
                p1: "The complaint procedure set out in article 13 of RD 1112/2018 entered into force on 20 September 2020.",
                p2: "If, after submitting a request for accessible information or a complaint, it has been dismissed, you disagree with the decision made, or the response does not meet the requirements set out in article 12.5, the interested party may initiate a complaint. A complaint may also be initiated if twenty working days have passed without receiving a response.",
                p3Part1: "The complaint can be submitted through the procedure",
                p3LinkText: "Complaints regarding the accessibility of websites and mobile applications",
            },
            opcional: {
                title: "Optional content",
                mesures: "Additional accessibility measures implemented: revised heading structure, labels added to fields that lacked them, correction of text blocks over 150 characters without text markup.",
                config: "Recommended technical configuration: updated browsers (Chrome, Firefox, Edge, Safari in their last two versions), minimum resolution of 1280x720 pixels, and zoom support up to 200% without loss of content or functionality.",
                recursos: "Resources of interest: W3C Web Accessibility Guide (https://www.w3.org/WAI/), automatic accessibility validators (https://achecker.ca/), and official documentation of Royal Decree 1112/2018.",
            },
        },
        notFound: "Not Found",
        forbidden: "You do not have the appropriate role or permission to access this resource.",
    }
};

export default translationEn;