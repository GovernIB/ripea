const translationEs = {
    common: {
        close: "Cerrar",
        copy: "Copiar",
        create: "Crear",
        update: "Modificar",
        delete: "Borrar",
        action: "Acción",
        expand: "Expandir",
        contract: "Contraer",
        download: "Descargar",
        detail: "Detalle",
        refresh: "Refrescar",
        clear: "Limpiar",
        search: "Buscar",
        options: "Opciones",
        import: "Importar",
        export: "Exportar",
        consult: "Consultar",
    },
    enum: {
        rol: {
            IPA_SUPER: "Superusuario",
            IPA_ADMIN: "Administrador",
            IPA_DISSENY: "Disseñador",
            IPA_ORGAN_ADMIN: "Administrador de organos",
            IPA_REVISIO: "Revisor",
            tothom: "Usuario",
        },
        siNO: {
            true: "Si",
            false: "No",
        },
        prioritat: {
            D_MOLT_ALTA: "Muy alta",
            C_ALTA: "Alta",
            B_NORMAL: "Normal",
            A_BAIXA: "Baja",
        },
        estat: {
            TANCAT: "Cerrado",
            OBERT: "Abierto",
        },
        origen: {
            O0: "Ciudadano",
            O1: "Administración",
        },
        estatElaboracio: {
            EE01: "Original",
            EE02: "Copia electrónica auténtica con cambio de formato",
            EE03: "Copia electrónica auténtica de documento en papel",
            EE04: "Copia electrónica parcial auténtica",
            EE99: "Otros",
        },
        tipoFirma: {
            TF01: "CSV",
            TF02: "Firma XAdES internamente separada",
            TF03: "Firma XAdES envuelta",
            TF04: "Firma CAdES separada/explícita",
            TF05: "Firma CAdES adjunta/implícita",
            TF06: "PAdES",
            TF07: "SMIME",
            TF08: "ODT",
            TF09: "OOXML",
        },
    },
    page: {
        comment: {
            expedient: "Comentarios del expediente",
            tasca: "Comentarios de la tarea",
        },
        contingut: {
            grid: {
                nom: "Nombre",
            },
            detalle: {
                numero: "Numero",
                titol: "Titulo",
                metaExpedient: "Tipo",
                organGestor: "Organo gestor",
                fechaApertura: "Fecha apertura",
                estat: "Estado",
                prioritat: "Prioridad",
                clasificacio: "Clasificación",
                dataProgramada: "Fecha en que se hará efectivo el envio de la notificación a Notific@",
                duracio: "Días naturales\nLa notificación estará disponible hasta las 23:59:59 del día introducido, expirando a las 00:00 del día siguiente. Sólo se aplica a Notificaciones Electrónicas. Puede indicarse tanto en número de días naturales, como con una fecha concreta.",
                dataCaducitat: "Días naturales\nLa notificación estará disponible hasta las 23:59:59 del día introducido, expirando a las 00:00 del día siguiente. Sólo se aplica a Notificaciones Electrónicas. Puede indicarse tanto en número de días naturales, como con una fecha concreta.",
                retard: "Dias que la notificación permanecerá en la sede antes de ser enviada via DEH o CIE",
            },
            tabs: {
                contingut: "Contenido",
                dades: "Datos",
                interessats: "Interesados",
                remeses: "Remesas",
                publicacions: "Publicaciones",
                anotacions: "Anotaciones",
                versions: "Versiones",
                tasques: "Tareas",

                actions: "Acciones",
                move: "Movimientos",
                auditoria: "Auditoria",
            },
            log: {
                causa: "Accion causa",
                param: "Parametros",
                param1: "Parametro 1",
                param2: "Parametro 2",
                objecte: "Objeto",
            },
            moviment: {
                causa: "Movimiento causa",
                origen: "Origen",
                desti: "Destino",
            },
            action: {
                create: {
                    label: "Crear contenido",
                },
                history: {
                    label: "Histórico de acciones",
                    title: "Histórico de acciones del elemento",
                    detail: "Detalle de la acción",
                },
                infoArxiu: {
                    title: "Información obtenida del archivo",
                    label: "Información archivo",
                },
            },
            history: {
                create: "Creación",
                update: "Modificación",
                user: "Usuario",
                date: "Fecha",
            },
            alert: {
                valid: "Este contenido tiene errores de validación",
                metaNode: "Este documento carece de un tipo de documento",
                guardarPendent: "Pendiente de guardar en archivo",
            },
        },
        anotacio: {
            tabs: {
                resum: "Resumen",
                estat: "Estado",
                registre: "Información registro",
                interessats: "Interesados",
                annexos: "Annexos",
                justificant: "Justificante",
            },
            detall: {
                title: "Detalles de la anotación de registro",
                estatView: "Estado",
                dataAlta: "Fecha alta",
                observacions: "Motivo",
                rejectedDate: "Fecha rechazo",
                acceptedDate: "Fecha aceptación",
                usuariActualitzacio: "Usuario",
            },
            action: {
                justificant: {
                    label: "Descargar justificante",
                    ok: "El justificante se ha descargado correctamente",
                }
            }
        },
        tasca: {
            title: "Tarea",
            detall: {
                title: "Detalles de la tarea",
                metaExpedientTasca: "Tipo de tarea",
                metaExpedientTascaDescription: "Descripción tipo de tarea",
                createdBy: "Creada por",
                responsablesStr: "Responsables",
                responsableActual: "Responsable actual",
                delegat: "Delegado",
                observadors: "Observadores",
                dataInici: "Fecha inicio",
                duracio: "Duración",
                dataLimit: "Fecha limite",
                estat: "Estado",
                prioritat: "Prioridad",
            },
            action: {
                new: {
                    label: "Nueva Tarea",
                },
                tramitar: {
                    label: "Tramitar",
                },
                iniciar: {
                    label: "Iniciar",
                    ok: "La tarea se ha iniciado correctamente",
                },
                rebutjar: {
                    label: "Rechazar",
                    title: "Rechazar tarea",
                    ok: "La tarea se ha rechazado correctamente",
                },
                cancel: {
                    label: "Cancelar",
                    title: "¿Seguro de que desea cancelar esta tarea?",
                    ok: "La tarea se ha cancelado correctamente",
                },
                finalitzar: {
                    label: "Finalizar",
                    ok: "La tarea se ha finalizado correctamente",
                },
                reassignar: {
                    label: "Reasignar",
                    title: "Reasignar tarea",
                    ok: "La tarea se ha reasignado correctamente",
                },
                delegar: {
                    label: "Delegar",
                    title: "Delegar tarea",
                    ok: "La tarea se ha delegado correctamente",
                },
                retomar: {
                    label: "Retomar",
                    title: "Retomar tarea",
                    ok: "La tarea se ha retomado correctamente",
                },
                changeDataLimit: {
                    label: "Modificar fecha limite...",
                    title: "Cambiar fecha limite",
                    ok: "La tarea se ha modificado correctamente",
                },
                changePrioritat: {
                    label: "Cambiar prioridad...",
                    title: "Modificar prioridad de la tarea",
                    ok: "La tarea se ha modificado correctamente",
                },
                reobrir: {
                    label: "Reabrir",
                    title: "Reabrir tarea",
                    ok: "La tarea se ha reabierto correctamente",
                },
            },
        },
        interessat: {
            title: "Interesado",
            rep: "Representante",
            detall: {
                nif: "NIF/CIF/NIE",
                nom: "Nombre",
                raoSocial: "Razón social",
                llinatges: "Apellidos",
                telefon: "Teléfono",
                email: "Correo electrónico",
                incapacitat: "Incapacidad",
                direccio: "Dirección",
                direccioPostal: "Dirección postal",
                entregaDehObligat: "DEH obligada?",
            },
            action: {
                detail: {
                    title: "Detalle del interesado",
                },
                new: {
                    label: "Nuevo Interesado",
                    ok: "Elemento creado",
                },
                delete: {
                    label: "Borrar Interesado",
                    check: "Esta seguro de que quiere seguir con esta acción?",
                    description: "Una vez borrado no se prodra recuperar",
                    ok: "Elemento borrado",
                },
                createRep: {
                    label: "Añadir Representante",
                    ok: "Elemento creado",
                },
                updateRep: {
                    label: "Modificar Representante",
                    ok: "Elemento modificado",
                },
                deleteRep: {
                    label: "Borrar Representante",
                    check: "Esta seguro de que quiere seguir con esta acción?",
                    description: "Una vez borrado no se prodra recuperar",
                    ok: "Elemento borrado",
                },
                importar: {
                    label: "Importar...",
                    title: "Importar interesados",
                    ok: "Interesados importados correctamente",
                },
                exportar: {
                    label: "Exportar...",
                    ok: "Interesados exportados correctamente",
                },
            },
            grid: {
                title: "Interesados del fichero",
                representant: "Representante",
            },
            alert: {
                incapacitat: "En caso de titular con incapacidad es obligatorio indicar un destinatario.",
            },
        },
        expedient: {
            title: "Expediente",
            filter: {
                title: "Buscador de expedientes"
            },
            detall: {
                title: "Información del expediente",
                agafatPer: "Cogido por",
                avisos: "Avisos",
            },
            action: {
                new: {
                    label: "New expedient",
                    ok: "El expediente '{{expedient}}' se ha creado correctamente.",
                },
                update: {
                    label: "Modificar...",
                    title: "Modificar Expediente",
                    ok: "El expediente '{{expedient}}' se ha modificado correctamente.",
                },
                detall: {
                    label: "Gestionar",
                },
                agafar: {
                    label: "Coger",
                    ok: "El expediente '{{expedient}}' ha sido cogido por el usuario '{{user}}'",
                },
                follow: {
                    label: "Seguir",
                    ok: "EL usuario '{{user}}' ha empezado a seguir al expediente '{{expedient}}'.",
                },
                unfollow: {
                    label: "Dejar de seguir",
                    ok: "EL usuario '{{user}}' ha dejado de seguir al expediente '{{expedient}}'.",
                },
                retornar: {
                    label: "Devolver",
                    ok: "El expediente '{{expedient}}' ha sido devuelto al usuario '{{user}}'",
                },
                lliberar: {
                    label: "Liberar",
                    ok: "El expediente '{{expedient}}' ha sido liberado",
                },
                eliminar: {
                    label: "Eliminar",
                    ok: "El expediente '{{expedient}}' ha sido borrado correctamente",
                },
                close: {
                    label: "Cerrar...",
                    title: "Cerrar expediente",
                    ok: "El expediente '{{expedient}}' ha sido cerrado correctamente",
                },
                open: {
                    label: "Reabrir",
                    description: "¿Desea reabrir el epediente?",
                    ok: "El expediente '{{expedient}}' ha sido reabierto correctamente",
                },
                download: {
                    label: "Descargar documentos...",
                    title: "Selección documentos",
                    ok: "Los documentos se han descargado correctamente",
                },
                exportFullCalcul: {
                    label: "Exportar hoja de cálculo",
                    ok: "La hoja de cálculo se ha descargado correctamente",
                },
                exportZIP: {
                    label: "Exportar indice ZIP",
                    title: "Exportar documentos a ZIP",
                    ok: "El documento ZIP se ha descargado correctamente",
                },
                exportPDF: {
                    label: "Exportar indice PDF",
                    ok: "El documento PDF se ha descargado correctamente",
                },
                exportCSV: {
                    label: "Exportar indice CSV",
                    ok: "El indice CSV se ha descargado correctamente",
                },
                exportEXCEL: {
                    label: "Exportar índice EXCEL",
                    ok: "El índice EXCEL se ha descargado correctamente",
                },
                exportPDF_ENI: {
                    label: "Indice PDF y exportación ENI",
                    ok: "El documento se ha descargado correctamente",
                },
                exportENI: {
                    label: "Exportación ENI",
                    ok: "El documento ENI se ha descargado correctamente",
                },
                exportINSIDE: {
                    label: "Exportación INSIDE",
                    ok: "El documento INSIDE se ha descargado correctamente",
                },
                export: {
                    label: "Exportar los documentos...",
                    title: "Exportar documentos",
                    ok: "Los documentos se han descargado correctamente",
                },
                sincronitzar: {
                    label: "Sincronizar estado con archivo",
                    ok: "El estado del archivo ha sido sincronizado",
                },

                changePrioritat: {
                    label: "Cambiar prioridad...",
                    title: "Modificar prioridad del expediente",
                    ok: "El expediente '{{expedient}}' se ha modificado correctamente.",
                },
                changeEstat: {
                    label: "Cambiar estado...",
                    title: "Modificar estado del expediente",
                    ok: "El expediente '{{expedient}}' se ha modificado correctamente.",
                },
                assignar: {
                    label: "Assignar",
                    title: "Assignar expediente a usuario",
                    ok: "El expediente '{{expedient}}' se ha assignado correctamente.",
                },
                relacio: {
                    label: "Relacionar...",
                    title: "Relacionar expediente",
                    ok: "Las relaciones del expediente '{{expedient}}' han cambiado correctamente.",
                },
                excelInteressats: {
                    title: "Exportar interesados a EXCEL",
                    ok: "Los interesados se han exportado correctamente",
                }
            },
            alert: {
                owner: "Es necesario reservar el expediente para poder modificarlo",
                alert: "Este expediente tiene alertas pendientes de leer",
                validation: "Este expediente tiene errores de validación",
                borradors: "Este expediente contiene borradores que serán eliminados al cerrarlo. A continuación tiene la possibilidad de marcar los borradores para que sean firmados con firma de servidor antes del cierre del expediente y así se evitará su eliminación. Si los documentos contienen alguna firma inválida, éstas serán eliminadas, y se volverá a firmar el documento en servidor.",
                notificacio: "Este expediente contiene notificaciones caducadas no finalizadas. Se intentará actualitzar su estado. Si llega nueva información de las notificaciones pendientes, se guardará el certificado en Helium, pero no en el Archivo digital.",
                documents: "Este expediente contiene documentos de anexos de anotaciones con errores. Se intentarán reprocesar al cerrar, y en caso de que no sea posible moverlos, se guardará una copia de éstos en el Archivo digital sin las firmas originales (tanto el documento original como la copia, se podrán seguir consultando desde la pestaña de contenido del expediente).",
                errorEnviament: "Este expediente tiene envíos con errores",
                errorNotificacio: "Este expediente tiene notificaciones con errores",
                ambEnviamentsPendents: "Este expediente tiene envíos pendientes de Portafirmas",
                ambNotificacionsPendents: "Este expediente tiene notificaciones pendientes",
            },
            modal: {
                seguidors: "Seguidores del expediente",
            },
            results: {
                checkDelete: "¿Está seguro de que quiere borrar este elemento?",
                checkRelacio: "¿Está seguro de que quiere borrar esta relación?",
                actionOk: "La acción se ha ejecutado correctamente.",
                actionBackgroundOk: "La acción se ha preparado para su ejecución en segundo plano. Puede consultar el estado del proceso desde el listado de acciones masivas.",
            }
        },
        arxiu: {
            detall: {
                arxiuUuid: "Identificador archivo",
                fitxerNom: "Nombre del archivo",
                serie: "Serie documental",
                arxiuEstat: "Estado del archivo",
                document: "Contenido documento",
                fitxerContentType: "Tipo MIME",
                metadata: "Metadatos ENI",
                versions: "Versión",
                identificador: "Identificador",
                organ: "Órgano",
                dataCaptura: "Fecha captura",
                dataApertura: "Fecha apertura",
                origen: "Origen",
                estadoElaboracion: "Estado elaboración",
                tipoDocumental: "Tipo documental NTI",
                format: "Formato nombre",
                clasificacion: "Clasificación",
                estat: "Estado",
                interessats: "Interesados",
                firmes: "Tipo de firma",
                documentOrigen: "Documento origen id",
            },
            firma: {
                title: "Firma",
                perfil: "Perfil de firma",
                fitxerNom: "Nombre del fichero",
                tipusMime: "Tipo MIME",
                contingut: "CSV",
                csvRegulacio: "Regulación CSV",
            },
            tabs: {
                resum: "Información",
                fills: "Hijos",
                firmes: "Firmas",
                data: "Metadatos",
            },
        },
        document: {
            title: "Documento",
            view: {
                title: "Tipo de vista",
                estat: "Vista por estado",
                tipus: "Vista por tipo documento",
                carpeta: "Vista por carpeta",
            },
            tabs: {
                resum: "Contenido",
                version: "Versiones",
                file: "Fichero",
                scaner: "Escaneo",
            },
            detall: {
                fitxerNom: "Nombre del fichero",
                fitxerContentType: "Tipo de contenido",
                metaDocument: "Tipo de documento",
                createdDate: "Fecha de creación",
                estat: "Estado",
                dataCaptura: "Fecha de captura",
                origen: "Origen",
                tipoDocumental: "Tipo documental NTI",
                estadoElaboracion: "Estado de elaboración",
                csv: "CSV",
                csvRegulacion: "Regulación del CSV",
                tipoFirma: "Tipo de firma",
                flux: "Existe un flujo de firma predefinido. La creación de un nuevo flujo de firma implica sobrescribir el seleccionado.",
                summarize: "Generar título y descripción con inteligencia artificial.\n(Requiere haber adjuntado un documento previamente)",
                documentOrigenFormat: "Formato: ES_<Órgano>_<AAAA>_<ID_específico>",
                dataBasic: "Datos básicos",
                dataInteressat: "Datos interesado",
                dataOther: "Otros datos",
            },
            action: {
                crearCarpets: {
                    label: "Carpeta...",
                    title: "Crear nueva carpeta",
                    ok: "Carpeta '{{carpeta}}' creada correctamente",
                },
                pinbal: {
                    label: "Consulta PINBAL...",
                    title: "Nueva consulta PINBAL",
                    ok: "Se ha creado el documento a partir de la consulta pinbal '{{codiServeiPinbal}}'",
                },
                import: {
                    label: "Importar documentos...",
                    title: "Importación de documentos desde el SGD",
                    ok: "Documentos importados correctamente",
                },
                detall: {
                    label: "Detalles",
                },
                move: {
                    label: "Mover...",
                    title: "Mover contenido",
                    ok: "Documento '{{document}}' movido correctamente",
                },
                copy: {
                    label: "Copiar...",
                    title: "Copiar contenido",
                    ok: "Documento '{{document}}' copiado correctamente",
                },
                vincular: {
                    label: "Vincular...",
                    title: "Vincular contenido",
                    ok: "Documento '{{document}}' vinculado correctamente",
                },
                imprimible: {
                    label: "Versión imprimible",
                    ok: "La versión imprimible se ha descargado correctamente",
                },
                original: {
                    label: "Descargar original",
                    ok: "El documenmto original se ha descargado correctamente",
                },
                firma: {
                    label: "Descargar firma",
                    title: "Firmar desde el navegador",
                    ok: "Documento firmado correctamente",
                },
                view: {
                    label: "Visualizar",
                    title: "",
                },
                csv: {
                    label: "Copiar enlace CSV",
                    ok: "Enlace CSV copiado correctamente",
                },
                portafirmes: {
                    label: "Enviar a portafirmas...",
                    title: "Enviar documento a portafirmas",
                    ok: "Documento '{{document}}' enviado a portafirmas",
                },
                firmar: {
                    label: "Firma desde el navegador...",
                },
                viaFirma: {
                    label: "Enviar viaFirma...",
                    title: "Enviar documento a ViaFirma",
                    ok: "Documento '{{document}}' enviado a viaFirma",
                },
                mail: {
                    label: "Enviar via email...",
                    title: "Enviar documento por email",
                    ok: "Documento '{{document}}' enviado via email",
                },
                seguiment: {
                    label: "Seguimiento portafirmas",
                    title: "Detalles de la firma",
                    ok: "La firma ha sido cancelada correctamente",
                },
                notificar: {
                    label: "Notificar o comunicar...",
                    title: "Crear notificación documento",
                    ok: "Notificación creada correctamente",
                },
                notificarMasiva: {
                    label: "Notificar o comunicar...",
                    title: "Generar documento para notificar",
                    ok: "Se ha generado un zip de los elementos seleccionados",
                },
                comunicar: {
                    label: "Comunicar...",
                },
                publicar: {
                    label: "Publicar...",
                    title: "Crear publicación",
                    ok: "Publicación creada correctamente",
                },
                descarregarImprimible: {
                    label: "Descarregar versión imprimible",
                    ok: "La versión imprimible se ha descargado correctamente",
                },
                changeType: {
                    label: "Cambiar tipo...",
                    title: "Cambiar tipo",
                    ok: "Los documentod se han modificado correctamente",
                },
                definitive: {
                    label: "Convertir a definitivo",
                    description: "Esta acción hará que los documentos pasen a formar parte del expediente de forma definitiva y no se podrán eliminar.",
                    ok: "Documento '{{document}}' cambiado a definitivo",
                },
            },
            alert: {
                import: "Documento importado",
                delete: "Documento borrador",
                firma: "Documento firmado",
                original: "Este documento contenía firmas inválidas y se ha clonado y firmado en servidor para poder guardarlo en el Archivo Digital. Se puede descargar el original desde el menú de acciones",
                custodiar: "Pendiente de custodiar documento firmado de portafrimes",
                moure: "El documento de la anotación está pendiente de mover a la serie documental del procedimiento",
                definitiu: "Documento definitivo",
                firmaPendent: "Pendiente de firmar",
                firmaParcial: "Firmado parcialmente",
                errorPortafirmes: "Error al enviar al portafirmas",
                funcionariHabilitatDigitalib: "Es necesario ser un funcionario habilitado en DIGITALIB",
                folder: "En caso de no seleccionar una carpeta se importarán los documentos directamente en el expediente.",
                scaned: "El proceso de escaneo se ha realizado con éxito.",
            },
            versio: {
                title: "Versión",
                data: "Fecha",
                arxiuUuid: "Archivo UUID",
            },
        },
        dada: {
            title: "Dato"
        },
        metaDada: {
            title: "Tipo de dato",
            detail: "Editar valores de la metadata",
        },
        registre: {
            grid: {
                extracte: "Extracto",
                origenRegistreNumero: "Numero registro",
                data: "Fecha registro",
                destiDescripcio: "Destino",
            },
            detall: {
                tipus: "Tipo",
                entrada: "Entrada",
                oficina: "Oficina",
                extracte: "Extracto",
                observacions: "Observaciones",
                identificador: "Num. origen",
                data: "Fecha origen",
                oficinaDescripcio: "Oficina origen",
                docFisica: "Documentación física",
                desti: "Órgano destino",
                refExterna: "Ref. externa",
                expedientNumero: "Num. expediente",
                procediment: "Procedimiento",
                llibre: "Libro",
                assumpte: "Tipo de asunto",
                idioma: "Idioma",
                assumpteCodi: "Código asunto",
                transport: "Transporte",
                transportNumero: "Num. transporte",
                origenRegistreNumero: "Num. origen",
                origenData: "Fecha origen",

                required: "Datos obligatorios",
                optional: "Datos opcionales",
                infoResumida: "Información de registro resumida",
                interessats: "Interesados",
                annexos: "Anexos",
            },
        },
        notificacio: {
            title: "Notificación",
            tabs: {
                dades: "Datos",
                errors: "Errores",
            },
            detall: {
                title: "Detalles de la notificación",
                notificacioDades: "Datos de la notificación",
                notificacioDocument: "Documento de la notificación",
                error: "Se han producido errores enviando la notificación",

                emisor: "Emisor",
                assumpte: "Concepto",
                observacions: "Descripción",
                notificacioEstat: "Estado",
                createdDate: "Enviada el",
                processatData: "Finalitzada el",
                tipus: "Tipos",
                entregaPostal: "Entrega postal",
                fitxerNom: "Nombre de archivo",
                serveiTipusEnum: "Tipo de servicio",
                notificacioIdentificador: "Identificador",
            },
            action: {
                actualitzarEstat: {
                    label: "Actualizar estado",
                    ok: "El estado ha sido actualizado",
                },
                notificacioInteressat: {
                    label: "Envios",
                    title: "Envios",
                    ok: "",
                },
                justificant: {
                    label: "Justificante de envio",
                    ok: "",
                },
                documentEnviat: {
                    label: "Documento enviado",
                    ok: "Se ha descargado el documento enviado",
                },
            },
        },
        notificacioInteressat: {
            tabs: {
                dades: "Datos",
                notif: "Notific@",
            },
            detall: {
                noEnviat: "No enviado a Notific@",
                title: "Detalle de envio",
                datat: "Datado",
                certificacio: "Certificación",
                enviament: "Datos del envio",
                interessat: "Datos del titular",
                representant: "Datos del destinatario",

                enviamentCertificacioData: "Fecha",
                enviamentCertificacioOrigen: "Origen",
                enviamentReferencia: "Referencia",
                entregaNif: "DEH NIF",
                classificacio: "DEH procedimiento",
                enviamentDatatEstat: "Estado",
            },
            action: {
                ampliarPlac: {
                    label: "Ampliar plazo",
                    title: "Ampliación del plazo de los envíos de la remesa",
                    ok: "El plazo ha sido ampliado",
                },
                certificat: {
                    label: "Certificación",
                    ok: "El certificado se ha descargado correctamente",
                },
            },
        },
        publicacio: {
            detall: {
                title: "Detalle de la publicación",
                document: "Documento",
                enviatData: "Fecha de envio",
                estat: "Estado",
                tipus: "Tipo",
                assumpte: "Asunto",
                observacions: "Observaciones",
            },
            action: {
                update: "Modificar publicación",
                delete: {
                    title: "Borrar publicación",
                    message: "Una vez borrada la publicación no se podra recuperar",
                }
            },
        },
        user: {
            options: {
                perfil: "Perfil",
                manual: "Manual de usuario",
                manualAdmin: "Manual de administrador",
                logout: "Desconectar"
            },
            menu: {
                expedient: "Expedientes",
                monitoritzar: "Monitorizar",
                integracions: "Integraciones",
                excepcions: "Excepciones",
                monitor: "Monitor de sistema",

                config: "Configuración",
                props: "Propiedades configurables",
                pinbal: "Servicios PINBAL",
                segonPla: "Reiniciar tareas en segundo plano",
                plugins: "Reiniciar plugins",
                avisos: "Avisos",

                anotacions: "Anotaciones",
                procediments: "Procedimientos",
                procedimentsTitle: "La entidad tiene procedimientos con órganos gestores no actualizados",
                grups: "Grupos",
                revisar: "Revisión de procedimientos",
                tasca: "Tareas",
                flux: "Flujos de firma",

                consultar: "Consultar",
                continguts: "Contenidos",
                dadesEstadistiques: "Datos estadisticos",
                portafib: "Documentos enviados a Portafib",
                notib: "Remesas enviadas a Notib",
                pinbalEnviades: "Consultas enviadas a PINBAL",
                assignacio: "Asignación de tareas",
                pendents: "Expedientes pendientes de distribución",
                comunicades: "Anotaciones comunicadas",

                documents: "Tipos de documentos",
                nti: "Tipos documentales NTI",
                dominis: "Dominios",
                organs: "Órganos gestores",
                url: "URLs instrucción",
                permisos: "Permisos de la entidad",
            },
            massive: {
                title: "Acción masiva",
                portafirmes: "Enviar documentos al portafirmas",
                firmar: "Firmar documentos desde el navegador",
                marcar: "Marcar como definitivos",
                estat: "Cambio de estado de expedientes",
                tancar: "Cierre de expedientes",
                custodiar: "Custodiar elementos pendientes",
                csv: "Copiar enlace CSV",
                anexos: "Adjuntar anexos pendientes de anotaciones aceptadas",
                anotacio: "Actualizar estado de las anotaciones en Distribución",
                prioritat: "Cambiar prioridad de expedientes",
            },
            action: {
                massives: {
                    label: "Consultar acciones masivas",
                    title: "Ejecuciones masivas de {{name}}",
                    detail: "Detalle de la acción masiva",
                },
            },
            perfil: {
                title: "Mi perfil",
                dades: "Datos de usuario",
                correu: "Envio de correos",
                generic: "Configuración genérica",
                column: "Configuración de columnas del listado de expedientes",
                vista: "Configuración vista de documentos de expedientes",
                moure: "Configuración vista destino al mover documentos",
            }
        },
        alert: {
            title: "Alertas de expediente",
            acciones: {
                read: "Marcar como leido",
            },
            errors: {
                metaDada: "Faltan los siguientes datos:",
                metaDocument: "Faltan los siguientes documentos:",
                metaNode: "Existen documentos sin un tipo de documento asigando",
                noFinalitzades: "Existen notificaciones con un estado que no es final",
                interessatObligatori: "Falta informar un interesado",
            },
        },
        notFound: "No encontrado",
    }
};

export default translationEs;