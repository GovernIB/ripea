const translationEn = {
    common: {
        close: "Close",
        copy: "Copy",
        create: "Create",
        update: "Update",
        delete: "Delete",
        action: "Action",
        expand: "Expand",
        contract: "Contract",
        download: "Download",
        detail: "Detail",
        refresh: "Refresh",
        clear: "Clear",
        search: "Search",
        options: "Options",
        import: "Import",
        export: "Export",
        consult: "Consult",
    },
    enum: {
        rol: {
            IPA_SUPER: "Superuser",
            IPA_ADMIN: "Administrator",
            IPA_DISSENY: "Designer",
            IPA_ORGAN_ADMIN: "Organ Administrator",
            IPA_REVISIO: "Reviewer",
            tothom: "User",
        },
        siNO: {
            true: "Yes",
            false: "No",
        },
        prioritat: {
            D_MOLT_ALTA: "Very High",
            C_ALTA: "High",
            B_NORMAL: "Normal",
            A_BAIXA: "Low",
        },
        estat: {
            TANCAT: "Closed",
            OBERT: "Open",
        },
        origen: {
            O0: "Citizen",
            O1: "Administration",
        },
        estatElaboracio: {
            EE01: "Original",
            EE02: "Authentic electronic copy with format change",
            EE03: "Authentic electronic copy of paper document",
            EE04: "Partial authentic electronic copy",
            EE99: "Others",
        },
        tipoFirma: {
            TF01: "CSV",
            TF02: "Internally detached XAdES signature",
            TF03: "Enveloped XAdES signature",
            TF04: "Detached/explicit CAdES signature",
            TF05: "Attached/implicit CAdES signature",
            TF06: "PAdES",
            TF07: "SMIME",
            TF08: "ODT",
            TF09: "OOXML",
        },
    },
    page: {
        comment: {
            expedient: "Expedient Comments",
            tasca: "Task Comments",
        },
        contingut: {
            grid: {
                nom: "Name",
            },
            detalle: {
                numero: "Number",
                titol: "Title",
                metaExpedient: "Type",
                organGestor: "Managing Body",
                fechaApertura: "Opening Date",
                estat: "Status",
                prioritat: "Priority",
                clasificacio: "Classification",
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
                create: {
                    label: "Create content",
                },
                history: {
                    label: "Action history",
                    title: "Element action history",
                },
                infoArxiu: {
                    title: "Information obtained from the file",
                    label: "File information",
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
            },
        },
        anotacio: {
            tabs: {
                resum: "Summary",
                estat: "Status",
                registre: "Registry Information",
                interessats: "Interested Parties",
                annexos: "Annexes",
                justificant: "Receipt",
            },
            detall: {
                title: "Registration Annotation Details",
                estatView: "Status",
                dataAlta: "Creation Date",
                observacions: "Reason",
                rejectedDate: "Rejection Date",
                acceptedDate: "Acceptance Date",
                usuariActualitzacio: "User",
            },
            action: {
                justificant: {
                    label: "Download receipt",
                    ok: "The receipt has been downloaded successfully",
                }
            }
        },
        tasca: {
            title: "Task",
            detall: {
                title: "Task Details",
                metaExpedientTasca: "Task Type",
                metaExpedientTascaDescription: "Task Type Description",
                createdBy: "Created by",
                responsablesStr: "Responsible Parties",
                responsableActual: "Current Responsible",
                delegat: "Delegate",
                observadors: "Observers",
                dataInici: "Start Date",
                duracio: "Duration",
                dataLimit: "Deadline",
                estat: "Status",
                prioritat: "Priority",
            },
            action: {
                new: {
                    label: "New Task",
                },
                tramitar: {
                    label: "Process",
                },
                iniciar: {
                    label: "Start",
                    ok: "The task has been started successfully",
                },
                rebutjar: {
                    label: "Reject",
                    title: "Reject task",
                    ok: "The task has been rejected successfully",
                },
                cancel: {
                    label: "Cancel",
                    title: "Are you sure you want to cancel this task?",
                    ok: "The task has been cancelled successfully",
                },
                finalitzar: {
                    label: "Finish",
                    ok: "The task has been completed successfully",
                },
                reassignar: {
                    label: "Reassign",
                    title: "Reassign task",
                    ok: "The task has been reassigned successfully",
                },
                delegar: {
                    label: "Delegate",
                    title: "Delegate task",
                    ok: "The task has been delegated successfully",
                },
                retomar: {
                    label: "Resume",
                    title: "Resume task",
                    ok: "The task has been resumed successfully",
                },
                changeDataLimit: {
                    label: "Change deadline...",
                    title: "Change deadline",
                    ok: "The task has been updated successfully",
                },
                changePrioritat: {
                    label: "Change priority...",
                    title: "Change task priority",
                    ok: "The task has been updated successfully",
                },
                reobrir: {
                    label: "Reopen",
                    title: "Reopen task",
                    ok: "The task has been reopened successfully",
                },
            }
        },
        interessat: {
            title: "Interested Party",
            rep: "Representative",
            detall: {
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
                new: {
                    label: "New Stakeholder",
                    ok: "Item created",
                },
                delete: {
                    label: "Delete Stakeholder",
                    check: "Are you sure you want to proceed with this action?",
                    description: "Once deleted, it cannot be recovered",
                    ok: "Item deleted",
                },
                createRep: {
                    label: "Add Representative",
                    ok: "Item created",
                },
                updateRep: {
                    label: "Edit Representative",
                    ok: "Item updated",
                },
                deleteRep: {
                    label: "Delete Representative",
                    check: "Are you sure you want to proceed with this action?",
                    description: "Once deleted, it cannot be recovered",
                    ok: "Item deleted",
                },
                importar: {
                    label: "Import...",
                    title: "Import stakeholders",
                    ok: "Stakeholders imported successfully",
                },
                exportar: {
                    label: "Export...",
                    ok: "Stakeholders exported successfully",
                },
            },
            grid: {
                title: "Stakeholders from the file",
                representant: "Representative",
            },
            alert: {
                incapacitat: "If the holder has a disability, a recipient must be specified.",
            },
        },
        expedient: {
            title: "Expedient",
            filter: {
                title: "Expedient Search"
            },
            detall: {
                title: "Case Information",
                agafatPer: "Taken by",
                avisos: "Warnings",
            },
            action: {
                new: {
                    label: "New expedient",
                    ok: "The expedient '{{expedient}}' has been created successfully.",
                },
                update: {
                    label: "Modify...",
                    title: "Modify Expedient",
                    ok: "The expedient '{{expedient}}' has been modified successfully.",
                },
                detall: {
                    label: "Manage",
                },
                agafar: {
                    label: "Take",
                    ok: "The expedient '{{expedient}}' has been taken by the user '{{user}}'.",
                },
                follow: {
                    label: "Follow",
                    ok: "The user '{{user}}' has started following the expedient '{{expedient}}'.",
                },
                unfollow: {
                    label: "Unfollow",
                    ok: "The user '{{user}}' has stopped following the expedient '{{expedient}}'.",
                },
                retornar: {
                    label: "Return",
                    ok: "The expedient '{{expedient}}' has been returned to the user '{{user}}'.",
                },
                lliberar: {
                    label: "Release",
                    ok: "The expedient '{{expedient}}' has been released.",
                },
                eliminar: {
                    label: "Delete",
                    ok: "The expedient '{{expedient}}' has been deleted successfully.",
                },
                close: {
                    label: "Close...",
                    title: "Close expedient",
                    ok: "The expedient '{{expedient}}' has been closed successfully.",
                },
                open: {
                    label: "Reopen",
                    ok: "The expedient '{{expedient}}' has been reopened successfully.",
                },
                download: {
                    label: "Download documents...",
                    title: "Document selection",
                    ok: "The documents have been downloaded successfully",
                },
                exportFullCalcul: {
                    label: "Export spreadsheet",
                    ok: "The spreadsheet has been downloaded successfully",
                },
                exportZIP: {
                    label: "Export ZIP index",
                    title: "Export documents to ZIP",
                    ok: "The ZIP document has been downloaded successfully",
                },
                exportPDF: {
                    label: "Export PDF index",
                    ok: "The PDF document has been downloaded successfully",
                },
                exportCSV: {
                    label: "Export CSV index",
                    ok: "The CSV index has been downloaded successfully",
                },
                exportEXCEL: {
                    label: "Export EXCEL index",
                    ok: "The EXCEL index has been downloaded successfully",
                },
                exportPDF_ENI: {
                    label: "PDF index and ENI export",
                    ok: "The document has been downloaded successfully",
                },
                exportENI: {
                    label: "ENI export",
                    ok: "The ENI document has been downloaded successfully",
                },
                exportINSIDE: {
                    label: "INSIDE export",
                    ok: "The INSIDE document has been downloaded successfully",
                },
                export: {
                    label: "Export documents...",
                    title: "Export documents",
                    ok: "The documents have been downloaded successfully",
                },
                sincronitzar: {
                    label: "Synchronize status with file",
                    ok: "The archive status has been synchronized",
                },

                changePrioritat: {
                    label: "Change priority...",
                    title: "Modify expedient priority",
                    ok: "The expedient '{{expedient}}' has been modified successfully.",
                },
                changeEstat: {
                    label: "Change status...",
                    title: "Modify expedient status",
                    ok: "The expedient '{{expedient}}' has been modified successfully.",
                },
                assignar: {
                    label: "Assign",
                    title: "Assign expedient to user",
                    ok: "The expedient '{{expedient}}' has been assigned successfully.",
                },
                relacio: {
                    label: "Relate...",
                    title: "Relate expedient",
                    ok: "The relations of the expedient '{{expedient}}' have changed successfully.",
                },
            },
            alert: {
                owner: "It is necessary to reserve the file to be able to modify it",
                alert: "This file has unread pending alerts",
                validation: "This file has validation errors",
                borradors: "This file contains drafts that will be deleted when closing it. You have the option to mark the drafts to be signed with server signature before closing the file to avoid their deletion. If the documents contain any invalid signatures, they will be removed and the document will be re-signed on the server.",
                notificacio: "This file contains expired notifications that are not finalized. An attempt will be made to update their status. If new information about pending notifications arrives, the certificate will be saved in Helium but not in the Digital Archive.",
                documents: "This file contains annotation annex documents with errors. An attempt will be made to reprocess them upon closing, and if it is not possible to move them, a copy will be saved in the Digital Archive without the original signatures (both the original document and the copy will still be accessible from the file’s content tab).",
                errorEnviament: "This case file has deliveries with errors",
                errorNotificacio: "This case file has notifications with errors",
                ambEnviamentsPendents: "This case file has pending deliveries to Portafirmas",
                ambNotificacionsPendents: "This case file has pending notifications",
            },
            modal: {
                seguidors: "Expedient followers",
            },
			results: {
                checkDelete: "Are you sure you want to delete this item?",
                checkRelacio: "Are you sure you want to delete this relationship?",
                actionOk: "Action executed.",
				actionBackgroundOk: "Action is executing on background.",
			}
        },
        arxiu: {
            detall: {
                arxiuUuid: "File identifier",
                fitxerNom: "File name",
                serie: "Documentary series",
                arxiuEstat: "File status",
                document: "Document content",
                fitxerContentType: "MIME type",
                metadata: "ENI metadata",
                versions: "Version",
                identificador: "Identifier",
                organ: "Authority",
                dataCaptura: "Capture date",
                dataApertura: "Opening date",
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
                title: "View Type",
                estat: "View by Status",
                tipus: "View by Document Type",
                carpeta: "View by Folder",
            },
            tabs: {
                resum: "Content",
                version: "Versions",
                file: "File",
                scaner: "Scan",
            },
            detall: {
                fitxerNom: "File Name",
                fitxerContentType: "Content Type",
                metaDocument: "Document Type",
                createdDate: "Creation Date",
                estat: "Status",
                dataCaptura: "Capture Date",
                origen: "Origin",
                tipoDocumental: "NTI Documentary Type",
                estadoElaboracion: "Elaboration Status",
                csv: "CSV",
                csvRegulacion: "CSV Regulation",
                tipoFirma: "Signature Type",
                flux: "There is a predefined signature flow. Creating a new signature flow will overwrite the selected one.",
                summarize: "Generate title and description using artificial intelligence.\n(Requires a document to be attached beforehand)",
                documentOrigenFormat: "Format: ES_<Body>_<AAAA>_<Specific_ID>",
                dataBasic: "Basic data",
                dataInteressat: "Interested party data",
                dataOther: "Other data",
            },
            action: {
                crearCarpets: {
                    label: "Folder...",
                    title: "Create new folder",
                    ok: "Folder '{{carpeta}}' created successfully",
                },
                pinbal: {
                    label: "PINBAL query...",
                    title: "New PINBAL query",
                    ok: "The document has been created from the pinbal query '{{codiServeiPinbal}}'"
                },
                import: {
                    label: "Import documents...",
                    title: "Import documents from SGD",
                    ok: "Documents imported successfully",
                },
                detall: {
                    label: "Details",
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
                imprimible: {
                    label: "Printable version",
                    ok: "The printable version has been downloaded successfully",
                },
                original: {
                    label: "Download original",
                    ok: "The original document has been downloaded successfully",
                },
                firma: {
                    label: "Download signature",
                    title: "Sign from browser",
                    ok: "Document signed successfully",
                },
                view: {
                    label: "View",
                    title: "",
                },
                csv: {
                    label: "Copy CSV link",
                    ok: "CSV link copied successfully",
                },
                portafirmes: {
                    label: "Send to signature inbox...",
                    title: "Send document to signature inbox",
                    ok: "Document '{{document}}' sent to signature inbox",
                },
                firmar: {
                    label: "Sign from browser...",
                },
                viaFirma: {
                    label: "Send viaFirma...",
                    title: "Send document to ViaFirma",
                    ok: "Document '{{document}}' sent via viaFirma",
                },
                mail: {
                    label: "Send via email...",
                    title: "Send document via email",
                    ok: "Document '{{document}}' sent via email",
                },
                seguiment: {
                    label: "Signature inbox tracking",
                    title: "Signature details",
                    ok: "The signature has been cancelled successfully",
                },
                notificar: {
                    label: "Notify or communicate...",
                    title: "Create document notification",
                    ok: "Notification created successfully",
                },
                notificarMasiva: {
                    label: "Notify or communicate...",
                    title: "Generate document to notify",
                    ok: "A zip of the selected items has been generated",
                },
                comunicar: {
                    label: "Communicate...",
                },
                publicar: {
                    label: "Publish...",
                    title: "Create publication",
                    ok: "Publication created successfully",
                },
                descarregarImprimible: {
                    label: "Download printable version",
                    ok: "The printable version has been downloaded successfully",
                },
                changeType: {
                    label: "Change type...",
                    title: "Change type",
                    ok: "Document '{{document}}' modified successfully",
                },
                definitive: {
                    label: "Convert to definitive",
                    description: "This action will make the documents permanently part of the file and they cannot be deleted.",
                    ok: "Document '{{document}}' changed to definitive",
                },
            },
            alert: {
                import: "Document imported",
                delete: "Draft document",
                firma: "Signed document",
                original: "This document contained invalid signatures and has been cloned and signed on the server to be saved in the Digital Archive. The original can be downloaded from the actions menu",
                custodiar: "Pending custody of signed document from portafirmas",
                moure: "The annotation’s document is pending to be moved to the procedure’s documentary series",
                definitiu: "Final document",
                firmaPendent: "Pending signature",
                firmaParcial: "Partially signed",
                errorPortafirmes: "Error sending to portafirmas",
                funcionariHabilitatDigitalib: "You must be an authorized official in DIGITALIB",
                folder: "If no folder is selected, the documents will be imported directly into the case file.",
                scaned: "The scanning process was completed successfully.",
            },
            versio: {
                title: "Version",
                data: "Date",
                arxiuUuid: "File UUID",
            },
        },
        dada: {
            title: "Data"
        },
        metaDada: {
            title: "Data Type",
        },
        registre: {
            grid: {
                extracte: "Extract",
                origenRegistreNumero: "Registration Number",
                data: "Registration Date",
                destiDescripcio: "Destination",
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
                annexos: "Annexes",
            },
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

                emisor: "Sender",
                assumpte: "Subject",
                observacions: "Description",
                notificacioEstat: "Status",
                createdDate: "Sent on",
                processatData: "Completed on",
                tipus: "Types",
                entregaPostal: "Postal delivery",
                fitxerNom: "File name",
                serveiTipusEnum: "Service type",
                notificacioIdentificador: "Identifier",
            },
            action: {
                actualitzarEstat: {
                    label: "Update status",
                    ok: "The status has been updated",
                },
                notificacioInteressat: {
                    label: "Shipments",
                    title: "Shipments",
                    ok: "",
                },
                justificant: {
                    label: "Proof of shipment",
                    ok: "",
                },
                documentEnviat: {
                    label: "Document sent",
                    title: "",
                    ok: "",
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
            },
            action: {
                ampliarPlac: {
                    label: "Extend deadline",
                    title: "Extension of the deadline for the batch shipments",
                    ok: "The deadline has been extended",
                },
                downloadDoc: {
                    label: "Download document",
                    ok: "The document has been downloaded successfully",
                },
                certificat: {
                    label: "Certification",
                    ok: "The certificate has been downloaded successfully",
                },
            },
        },
        publicacio: {
            detall: {
                title: "Publication Details",
                document: "Document",
                enviatData: "Sending Date",
                estat: "Status",
                tipus: "Type",
                assumpte: "Subject",
                observacions: "Observations",
            },
            action: {
                update: "Edit Publication",
                delete: {
                    title: "Delete Publication",
                    message: "Once deleted, the publication cannot be recovered",
                }
            },
        },
        user: {
            options: {
                perfil: "Profile",
                manual: "User manual",
                manualAdmin: "Administrator manual",
                logout: "Log out"
            },
            menu: {
                expedient: "Cases",
                monitoritzar: "Monitor",
                integracions: "Integrations",
                excepcions: "Exceptions",
                monitor: "System monitor",

                config: "Configuration",
                props: "Configurable properties",
                pinbal: "PINBAL services",
                segonPla: "Restart background tasks",
                plugins: "Restart plugins",
                avisos: "Notifications",

                anotacions: "Annotations",
                procediments: "Procedures",
                procedimentsTitle: "The entity has procedures with outdated managing bodies",
                grups: "Groups",
                revisar: "Procedure review",
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
                nti: "NTI document types",
                dominis: "Domains",
                organs: "Managing bodies",
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
                masives: "View massive actions"
            },
            action: {
                masives: "{{name}} mass executions",
            },
            perfil: {
                title: "My profile",
                dades: "User data",
                correu: "Email sending",
                generic: "General settings",
                column: "Column configuration of case list",
                vista: "Document view settings in cases",
                moure: "Destination view settings when moving documents"
            }
        },
        alert: {
            title: "Case Alerts",
            acciones: {
                read: "Mark as read",
            },
            errors: {
                metaDada: "The following data is missing:",
                metaDocument: "The following documents are missing:",
                metaNode: "There are documents without an assigned document type",
                noFinalitzades: "There are notifications with a non-final status",
                interessatObligatori: "An interested party is missing",
            },
        },
        notFound: "Not Found",
    }
};

export default translationEn;