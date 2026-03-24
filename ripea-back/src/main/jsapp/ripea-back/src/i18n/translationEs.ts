const translationEs = {
    common: {
        close: "Cerrar",
        cancel: "Cancela",
        copy: "Copiar",
        create: "Crear",
        update: "Modificar",
        actualize: "Actualiza",
        save: "Guarda",
        delete: "Borrar",
        accepta: "Acepta",
        rebutja: "Rechaza",
        action: "Acciones",
        expand: "Expandir",
        contract: "Contraer",
        download: "Descargar",
        send: "Envia",
        detail: "Detalles",
        refresh: "Refrescar",
        clear: "Limpiar",
        back: "Volver",
        search: "Buscar",
        options: "Opciones",
        select: {
            all: "Seleccionar todos",
            clear: "Limpiar selección",
        },
        import: "Importar",
        export: "Exportar",
        consult: "Consultar",
        filter: "Filtrar",
        processing: "Procesando...",
        auditoria: {
            create: "Creado el {{createdDate}} por '{{createdBy}}'.",
            update: "Modificado el {{lastModifiedDate}} por '{{lastModifiedBy}}'.",
        },
        nouPermis: "Nuevo permiso",
    },
    enum: {
        rol: {
            IPA_SUPER: "Superusuario",
            IPA_ADMIN: "Administrador de entidad",
            IPA_ADMIN_LECTURA: "Administrador (lectura)",
            IPA_DISSENY: "Diseñador de Órgano gestor",
            IPA_ORGAN_ADMIN: "Administrador de Órgano gestor",
            IPA_REVISIO: "Revisor de procedimientos",
            tothom: "Usuario",
        },
        siNO: {
            true: "Si",
            false: "No",
        },
        estat: {
            TANCAT: "Cerrado",
            OBERT: "Abierto",
            ENVIAT: "Enviado",
            PAUSAT: "Pausado",
            INICIAT: "Iniciado",
            FIRMAT: "Firmado",
            REBUTJAT: "Rechazado",
            PARCIAL: "Parcial",
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
        tipusVia: {
            ALAMEDA: "Alameda",
            AVENIDA: "Avenida",
            BARRIO: "Barrio",
            BULEVAR: "Bulevar",
            CALLE: "Calle",
            CALLEJA: "Callejón",
            CAMINO: "Camino",
            CAMPO: "Campo",
            CARRERA: "Carrera",
            CARRETERA: "Carretera",
            CUESTA: "Cuesta",
            EDIFICIO: "Edificio",
            ENPARANTZA: "Plaza",
            ESTRADA: "Estrada",
            GLORIETA: "Glorieta",
            JARDINES: "Jardines",
            OTROS: "Otros",
            PARQUE: "Parque",
            PASAJE: "Pasaje",
            PASEO: "Paseo",
            PLAZA: "Plaza",
            PLAZUELA: "Plazuela",
            POBLADO: "Poblado",
            POLIGONO: "Polígono",
            RAMBLA: "Rambla",
            RONDA: "Ronda",
            RUA: "Rúa",
            SECTOR: "Sector",
            TRAVESIA: "Travesía",
            URBANIZACION: "Urbanización",
            VIA: "Vía",
		},
    },
    navigate: {
        expedient: "Buscador de expedientes",
        expedientPeticio: "Buscador de anotaciones de registro",
        usuariTasca: "Tareas",
        entitat: "Gestión de entidades",
        avis: "Gestión de avisos",
        exception: "Últimas excepciones producidas",
        integracio: "Seguimiento de integraciones",
        massiu: {
            portafirmes: "Acción masiva: enviar documentos al portafirmas",
            firmasimpleweb: "Acción masiva: firmar documentos desde el navegador",
            canviEstat: "Acción masiva: Cambio de estado de expedientes",
            tancament: "Acción masiva: Cierre de expedientes",
            seguimentArxiuPendents: "Acción masiva: Custodiar elementos pendientes",
            csv: "Acción masiva: copiar enlace CSV",
            definitiu: "Acción masiva: marcar documentos como definitivos",
            canviPrioritats: "Acción masiva: Cambio de prioridad de expedientes",
            expedientPeticioCanviEstatDistribucio: "Acción masiva: Actualizar estado de las anotaciones en Distribución",
            procesarAnnexosPendents: "Acción masiva: Adjuntar anexos pendientes de anotaciones aceptadas",
        },
    },
    page: {
        comment: {
            expedient: "Comentarios del expediente",
            tasca: "Comentarios de la tarea",
            metaExpedient: "Comentarios del procedimiento",
        },
        contingut: {
            grid: {
                nom: "Nombre",
                path: "Ruta del contenido",
            },
            detalle: {
                title: "Content details",
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
                guardarArxiu: {
                    label: "Guardar en archivo",
                    ok: "Elemento '{{contingut}}' guardado en archivo",
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
                importarExpedient: {
                    label: "Importar expediente relacionado...",
                    title: "Expedientes relacionados",
                },
                seguimentPortafirmes: {
                    label: "Seguimiento Portafirmas",
                    title: "Seguimiento Portafirmas",
                },
                seguimentvf: {
                    label: "Seguiment Viafirma",
                    title: "Detalles de la firma",
                },
                custodiar: {
                    label: "Custodiar",
                },
                replay: {
                    label: "Recuperar",
                    ok: "El contenido se ha recuperado correctamente",
                    massiveOk: "Los contenidos se han recuperado correctamente",
                },
                delete: {
                    ok: "El contenido se ha eliminado correctamente",
                    massiveOk: "Los contenidos se han eliminado correctamente",
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
                fileSize: "El tamaño máximo permitido para el fichero es de {{maxSize}}",
            },
        },
        anotacio: {
            filter: {
                title: "Buscador de anotaciones de registro"
            },
            tabs: {
                resum: "Resumen",
                estat: "Estado",
                registre: "Información registro",
                interessats: "Interesados",
                annexos: "Anexos",
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
                },
                acceptar: {
                    label: "Aceptar...",
                    button: "Acepta",
                    title: "Aceptar expediente",
                    ok: "La anotación se ha aceptado correctamente",
                },
                rebutjar: {
                    label: "Rechazar...",
                    button: "Rechaza",
                    title: "Rechazar expediente",
                    ok: "La anotación se ha rechazado correctamente",
                },
                canviProcediment: {
                    label: "Modificar...",
                    title: "Cambiar procedimiento",
                    ok: "La anotación {{data.identificador}} se ha modificado correctamente",
                },
                canviEstatDistribucio: {
                    label: "Cambiar estado a distribución",
                    ok: "El estado ha cambiado correctamente",
                    massiveOk: "Se ha actualizado el estado de '{{data.num}}' anotaciones",
                },
                descargarAnnex: {
                    label: "Descargar Anexo",
                    ok: "Anexo descargado correctamente",
                },
                procesarAnnexosPendents: {
                    label: "Adjuntar",
                    ok: "El anexo se ha procesado correctamente",
                    massiveOk: "Se han procesado '{{data.num}}' anexos",
                    info: "Si se ha producido algún error al aceptar una anotación desde la pantalla de Anotaciones, de manera que alguno de los documentos de la anotación no se haya adjuntado al expediente, desde este listado podrá volver a intentar adjuntar el documento al expediente.",
                },
                firma: {
                    label: "Firmas",
                    title: "Firmas",
                },
                consultar: {
                    label: "Consultar",
                    ok: "La anotación se ha consultado y guardado correctamente",
                    massiveOk: "Se han consultado y guardado correctamente {{data.num}} anotaciones",
                },
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
                    ok: "La tarea {{data.titol}} se ha creado correctamente",
                },
                tramitar: {
                    label: "Tramitar",
                },
                iniciar: {
                    label: "Iniciar",
                    ok: "La tarea se ha iniciado correctamente",
                },
                rebutjar: {
                    label: "Rechazar...",
                    button: "Rechaza",
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
                    label: "Reasignar...",
                    button: "Reasigna",
                    title: "Reasignar tarea",
                    ok: "La tarea se ha reasignado correctamente",
                },
                delegar: {
                    label: "Delegar...",
                    button: "Delega",
                    title: "Delegar tarea",
                    ok: "La tarea se ha delegado correctamente",
                },
                retomar: {
                    label: "Cancelar delegación...",
                    button: "Cancela delegación",
                    title: "Cancelar delegación de tarea",
                    ok: "La delegación de la tarea se ha cancelado correctamente",
                },
                changeDataLimit: {
                    label: "Modificar fecha límite...",
                    button: "Modifica fecha límite",
                    title: "Cambiar fecha límite",
                    ok: "La tarea se ha modificado correctamente",
                },
                changePrioritat: {
                    label: "Cambiar prioridad...",
                    button: "Cambia prioridad",
                    title: "Modificar prioridad de la tarea",
                    ok: "La tarea se ha modificado correctamente",
                },
                reobrir: {
                    label: "Reabrir...",
                    button: "Reabre",
                    title: "Reabrir tarea",
                    ok: "La tarea se ha reabierto correctamente",
                },
                comment: {
                    ok: "Comentario añadido a la tarea '{{data.expedientTasca.description}}'",
                },
            },
        },
        interessat: {
            title: "Interesado",
            rep: "Representante",
            detall: {
				tipus: "Tipo",
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
                    ok: "El interesado {{data.documentNum}} se ha creado correctamente",
                },
                update: {
                    ok: "El interesado {{data.documentNum}} se ha modificado correctamente",
                },
                delete: {
                    label: "Borrar Interesado",
                    check: "Esta seguro de que quiere seguir con esta acción?",
                    description: "Una vez borrado no se prodra recuperar",
                    ok: "El interesado {{data.documentNum}} se ha borrado correctamente",
                },
                createRep: {
                    label: "Añadir Representante",
                    ok: "El representante {{data.documentNum}} se ha creado correctamente",
                },
                updateRep: {
                    label: "Modificar Representante",
                    ok: "El representante {{data.documentNum}} se ha modificar correctamente",
                },
                deleteRep: {
                    label: "Borrar Representante",
                    check: "Esta seguro de que quiere seguir con esta acción?",
                    description: "Una vez borrado no se prodra recuperar",
                    ok: "El representante {{data.documentNum}} se ha borrado correctamente",
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
                importSGD: {
                    label: "Importar interesados desde Registro...",
                    title: "Importar interesados desde Registro",
                    ok: "Interesados importados correctamente",
                },
				gestGrups: {
				    label: "Gestionar grupos...",
					title: "Gestionar grupos",
				    ok: "Grupos modificados correctamente",
				},
            },
            grid: {
                title: "Interesados del fichero",
                representant: "Representante",
				tipus: {
					label: "Tipo",
					personaFisica: "Persona física",
					personaJuridica: "Persona jurídica",
					administrador: "Administrador",
				},
            },
            alert: {
                incapacitat: "En caso de titular con incapacidad es obligatorio indicar un destinatario.",
                jaExistentExpedient: "Ya existe en el expediente",
            },
			grup: {
				title: "Grupos de interesados",
				action: {
					new: {
						ok: "Grupo creado correctamente",
					},
					update: {
						ok: "Grupo modificado correctamente",
					},
					delete: {
					    label: "Borrar Grupo",
					    check: "Esta seguro de que quiere seguir con esta acción?",
					    description: "Una vez borrado no se prodra recuperar",
					    ok: "El grupo {{data.nom}} se ha borrado correctamente",
					},
				},
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
                    label: "Nuevo expediente",
                    title: "Crear nuevo expediente",
                    ok: "El expediente '{{data.nom}}' se ha creado correctamente.",
                },
                update: {
                    label: "Modificar...",
                    title: "Modificar expediente",
                    ok: "El expediente '{{data.nom}}' se ha modificado correctamente.",
                },
                detall: {
                    label: "Gestionar",
                },
                importar: {
                    label: "Importar expediente",
                    ok: "El expediente se ha importado correctamente",
                },
                agafar: {
                    label: "Bloquear",
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
                    ok: "El expediente '{{data.nom}}' ha sido borrado correctamente",
                },
                close: {
                    label: "Cerrar...",
                    button: "Cierrar",
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
                    button: "Descarga documentos",
                    title: "Selección documentos",
                    ok: "Los documentos se han descargado correctamente",
                },
                exportFullCalcul: {
                    label: "Exportar hoja de cálculo",
                    ok: "La hoja de cálculo se ha descargado correctamente",
                },
                exportZIP: {
                    label: "Exportar índice ZIP...",
                    button: "Exporta ZIP",
                    title: "Exportar documentos a ZIP",
                    ok: "El documento ZIP se ha descargado correctamente",
                },
                exportPDF: {
                    label: "Exportar índice PDF",
                    ok: "El documento PDF se ha descargado correctamente",
                },
                exportCSV: {
                    label: "Exportar CSV",
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
                exportDocs: {
                    label: "Exportar los documentos de los expedientes seleccionados",
                    ok: "Los documentos se han exportado correctamente",
                },
                export: {
                    label: "Exportar los documentos...",
                    button: "Exporta los documentos",
                    title: "Exportar documentos",
                    ok: "Los documentos se han descargado correctamente",
                },
                sincronitzar: {
                    label: "Sincronizar estado con archivo",
                    ok: "El estado del archivo ha sido sincronizado",
                },
                changePrioritat: {
                    label: "Cambiar prioridad...",
                    button: "Cambia prioridad",
                    title: "Modificar prioridad del expediente",
                    ok: "La prioridad del expediente '{{expedient}}' se ha modificado correctamente.",
                    massiveOk: "Se ha cambiado la prioridad de '{{data.num}}' expedientes",
                },
                changeEstat: {
                    label: "Cambiar estado...",
                    button: "Cambia estado",
                    title: "Modificar estado del expediente",
                    ok: "El estado del expediente '{{expedient}}' se ha modificado correctamente.",
                    massiveOk: "Se ha cambiado el estado de '{{data.num}}' expedientes",
                },
                assignar: {
                    label: "Asignar...",
                    button: "Asigna",
                    title: "Asignar expediente a usuario",
                    ok: "El expediente '{{expedient}}' se ha assignado correctamente.",
                },
                relacio: {
                    label: "Relacionar...",
                    button: "Relaciona",
                    title: "Relacionar expediente",
                    ok: "Las relaciones del expediente '{{expedient}}' han cambiado correctamente.",
                },
                eliminarRelacio: {
                    label: "Eliminar relación",
                    ok: "La relación entre los 2 expedientes se ha eliminado correctamente.",
                },
                excelInteressats: {
                    title: "Descargar plantilla para importar interesados Excel",
                    ok: "Los interesados se han exportado correctamente",
                },
                impDocMass: {
                    label: "Importar documentos a los expedientes seleccionados",
                    title: "Importación de documentos",
                    mssg: "Los documentos que adjunte se incorporarán a los {{num}} expedientes seleccionados",
                    warning: "Los expedientes deben pertenecer al mismo procedimiento.",
                },
                comment: {
                    ok: "Comentario añadido al expediente '{{data.expedient.description}}'",
                },
				moureTot: {
					label: "Mover todo...",
                    button: "Mover todo",
				    title: "Mover todo al expediente destino",
				    ok: "La acción masiva para mover el expediente '{{expedient}}' se ha creado correctamente.",
				},
            },
            alert: {
                owner: "Es necesario reservar el expediente para poder modificarlo",
                alert: "Este expediente tiene alertas pendientes de leer",
                validation: "Este expediente tiene errores de validación",
                esborranys: "Existen documentos en estado borrador (B) que deben pasarse a definitivos o eliminarse del expediente si se quiere cerrar el expediente.\nEsta acción hará que los documentos pasen a formar parte del expediente definitivamente y no se podrán eliminar.",
                borradors: "Este expediente contiene borradores que serán eliminados al cerrarlo. A continuación tiene la possibilidad de marcar los borradores para que sean firmados con firma de servidor antes del cierre del expediente y así se evitará su eliminación. Si los documentos contienen alguna firma inválida, éstas serán eliminadas, y se volverá a firmar el documento en servidor.",
                notificacio: "Este expediente contiene notificaciones caducadas no finalizadas. Se intentará actualitzar su estado. Si llega nueva información de las notificaciones pendientes, se guardará el certificado en RIPEA, pero no en el Archivo digital.",
                documents: "Este expediente contiene documentos de anexos de anotaciones con errores. Se intentarán reprocesar al cerrar, y en caso de que no sea posible moverlos, se guardará una copia de éstos en el Archivo digital sin las firmas originales (tanto el documento original como la copia, se podrán seguir consultando desde la pestaña de contenido del expediente).",
                errorEnviament: "Este expediente tiene envíos con errores",
                errorNotificacio: "Este expediente tiene notificaciones con errores",
                ambEnviamentsPendents: "Este expediente tiene envíos pendientes de Portafirmas",
                ambNotificacionsPendents: "Este expediente tiene notificaciones pendientes",
                canviEstat: "Es necesario seleccionar un procedimiento para poder realizar la acción masiva",
				moureTot: {
					info: "El expediente se encuentra actualmente bloqueado debido a una ejecución en segundo plano en curso.\nHasta que esta finalice, no será posible realizar modificaciones. Consulte las acciones masivas pendientes para conocer su estado.",
					title: "Estás a punto de iniciar una acción masiva que moverá la siguiente información hacía el expediente destino:",
					items: [
						"Los documentos y carpetas",
					  	"Los interesados",
					  	"Los seguidores",
					  	"Los expedientes relacionados",
					  	"Las anotaciones de registro",
					  	"Los comentarios"
					],
				},
            },
            modal: {
                seguidors: "Seguidores del expediente",
            },
            results: {
                checkDelete: "¿Está seguro de que quiere borrar este contenido? Si contenia firmas en curso, seran canceladas.",
                checkRelacio: "¿Está seguro de que quiere borrar esta relación?",
                actionOk: "La acción se ha ejecutado correctamente.",
                actionBackgroundOk: "La acción se ha preparado para su ejecución en segundo plano. Puede consultar el estado del proceso desde el listado de acciones masivas.",
            }
        },
        arxiu: {
            detall: {
                arxiuUuid: "Identificador arxiu",
                fitxerNom: "Nombre del documento",
                serie: "Serie documental",
                arxiuEstat: "Estado en arxiu",
                document: "Contenido documento",
                fitxerContentType: "Tipo MIME",
                metadata: "Metadatos ENI",
                versions: "Versión",
                identificador: "Identificador",
                organ: "Órgano",
                dataCaptura: "Fecha captura",
                dataApertura: "Fecha apertura",
                dataTancament: "Fecha de cierre",
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
                responsableNom: "Nombre del responsable",
                responsableNif: "NIF del responsable",
                data: "Fecha de la firma",
                emissorCertificat: "Emisor del certificado"
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
                nullEstat: "Sin estado",
                tipus: "Vista por tipo documento",
                carpeta: "Vista por carpeta",
            },
            tabs: {
                resum: "Contenido",
                version: "Versiones",
                file: "Fichero",
                scaner: "Escaneo",
                firmes: "Firmas",
            },
            detall: {
                createdDate: "Fecha de creación",
                createdBy: "Creado por",
                dataCaptura: "Fecha de captura",
                csv: "CSV",
                flux: "Existe un flujo de firma predefinido. La creación de un nuevo flujo de firma implica sobrescribir el seleccionado.",
                summarize: "Generar título y descripción con inteligencia artificial.\n(Requiere haber adjuntado un documento previamente)",
                documentOrigenFormat: "Formato: ES_<Órgano>_<AAAA>_<ID_específico>",
                dataBasic: "Datos básicos",
                dataInteressat: "Datos interesado",
                dataEspecific: "Datos específicos",
                dadesRegistrals: "Datos registrales",
                fetRegistral: "Hecho registral",
                naixement: "Nacimiento",
                dadesAdicionals: "Datos adicionales",
                dataOther: "Otros datos",
                senseTipus: "Sin tipo asignado",
                extensio: "Extensión",
                ruta: "Ruta",
                mida: "Tamaño",
            },
            action: {
                new: {
                    dropMessg: "Arrastra el fichero aquí o sobre la tabla...",
                    ok: "El documento {{data.nom}} se ha creado correctamente"
                },
                update: {
                    ok: "El documento {{data.nom}} se ha modificado correctamente"
                },
                delete: {
                    label: "Borrar",
                    check: "¿Está seguro de que quiere borrar este contenido?",
                    description: "Una vez borrado no se podrá recuperar. Si contenia firmas en curso, seran canceladas.",
                    ok: "El documento {{data.nom}} se ha eliminado correctamente"
                },
                pinbal: {
                    label: "Consultar PINBAL...",
                    button: "Consulta",
                    title: "Nueva consulta PINBAL",
                    ok: "Se ha creado el documento a partir de la consulta pinbal '{{codiServeiPinbal}}'",
                },
				import: {
					close: {
					    check: "¿Está seguro de que desea cerrar esta ventana?",
					    description: "La importación continuará en segundo plano y podrá consultar el resultado en el expediente más tarde.",
					},
					cancel: {
					    check: "¿Está seguro de que desea cancelar la importación?",
					    description: "Los documentos importados hasta este momento se conservarán en el expediente.",
					},
				},
				importSgd: {
				    label: "Importar documentos SGD...",
				    title: "Importación de documentos desde el SGD",
				    ok: "Documentos importados correctamente",
					interessats: "Selecciona los interesados que desea asociar al expediente",
					interessat: {
						tipus: {
							1: "Administración",
							2: "Persona física",
							3: "Persona jurídica",
						},
					},
					resultat: {
						title: "Resultado:",
						ok: "Proceso importación completado",
						documents: "Documentos procesados correctamente: ",
						interessats: "Interesados procesados correctamente: ",
						carpetes: "Carpetas creadas correctamente: ",
						errors: "Errores detallados: ",
					}
				},
                importZip: {
                    label: "Importar desde ZIP...",
                    title: "Importación de documentos desde un ZIP",
                    ok: "Documentos importados correctamente",
					resultat: {
						title: "Resultado:",
						ok: "Proceso importación completado",
						documents: {
							ok: "Documentos procesados correctamente: ",
							ko: "Documentos con error: ",
							firma: "Documentos con error de firma: ",
						},
						carpetes: {
							ok: "Carpetas creadas correctamente: ",
						},
						tamany: "Tamaño total procesado: ",
						errors: "Errores detallados: ",
					},
                },
                detall: {
                    label: "Detalles",
                },
                imprimible: {
                    label: "Copia auténtica imprimible",
                    ok: "La copia auténtica imprimible se ha descargado correctamente"
                },
                original: {
                    label: "Descargar original",
                    ok: "El documenmto original se ha descargado correctamente",
                },
                download: {
                    ok: "El documento se ha descargado correctamente",
                },
                firma: {
                    label: "Descargar firma",
                    button: "Iniciar proceso de firma",
                    title: "Firmar desde el navegador",
                    ok: "Documento firmado correctamente",
                },
                view: {
                    label: "Visualizar",
                    title: "Visualizar",
                },
                csv: {
                    label: "Copiar enlace CSV",
                    ok: "Enlace CSV copiado correctamente",
                },
                portafirmes: {
                    label: "Enviar a portafirmas...",
                    button: "Envia a portafirmas",
                    title: "Enviar documento a portafirmas",
                    ok: "Documento '{{document}}' enviado a portafirmas",
                },
                toPDF: {
                    description: "Se cambiará el formato del documento antes de enviarlo al portafirmas",
                    title: "Visualizar versión PDF",
                },
                firmar: {
                    label: "Firma desde el navegador...",
                    button: "Inicia proceso de firma",
                },
                viaFirma: {
                    label: "Enviar viaFirma...",
                    button: "Enviar a ViaFirma",
                    title: "Enviar documento a ViaFirma",
                    ok: "Documento '{{document}}' enviado a viaFirma",
                },
                mail: {
                    label: "Enviar via email...",
                    button: "Envia via email",
                    title: "Enviar documento por email",
                    ok: "Documento '{{document}}' enviado via email",
                },
                seguiment: {
                    label: "Seguimiento portafirmes",
                    title: "Detalles de la firma",
                },
                cancel: {
                    label: "Cancelar envío",
                    ok: "La firma ha sido cancelada correctamente",
                    check: "Confirme la acción",
                    description: "¿Seguro que desea cancelar la firma actualmente en proceso?",
                },
                notificar: {
                    label: "Notificar o comunicar...",
                    button: "Notifica",
                    title: "Crear notificación documento",
                    ok: "Notificación creada correctamente",
                    alert: {
                        interessatsAmbAvis: {
                            title: "Hay notificaciones con destinatario sin NIF/NIE. Estas notificaciones no se pueden enviar a la carpeta ciudadana, ya que es necesario un NIF o NIE para acceder a ella.",
                            description: "Las notificaciones sin NIF/NIE son las siguientes:",
                            marcat: {
                                title: "Si ha marcado entrega postal:",
                                description: "La notificación se enviará por correo postal, siempre que el órgano gestor tenga definido un CIE (Centro de Impresión y Ensobrado).",
                            },
                            noMarcat: {
                                title: "Si NO ha seleccionado entrega postal:",
                                description: "La notificación telemática no se realizará. En su lugar se enviará un correo electrónico de aviso informando al titular de que en breve recibirá una notificación por correo postal.",
                                warning: "Es necesario que realice la notificación en papel."
                            },
                        },
                        administracioSir: {
                            title: "Uno de los interesados seleccionados es una administración SIR.",
                            warning: "TODOS los envíos se realizarán como tipo COMUNICACIÓN",
                        },
                    }
                },
                notificarMasiva: {
                    label: "Notificar o comunicar...",
                    button: "Notifica",
                    title: "Generar documento para notificar",
                    ok: "Se ha generado un zip de los elementos seleccionados",
                },
                comunicar: {
                    label: "Comunicar...",
                },
                publicar: {
                    label: "Publicar...",
                    button: "Publica",
                    title: "Crear publicación",
                    ok: "Publicación creada correctamente",
                },
                descarregarOriginal: {
                    label: "Documento original",
                    ok: "El documento original se ha descargado correctamente"
                },
                descarregarImprimible: {
                    label: "Descargar copia auténtica",
                    ok: "La copia auténtica se ha descargado correctamente",
                },
                changeType: {
                    label: "Cambiar tipo...",
                    button: "Cambia tipo",
                    title: "Cambiar tipo",
                    ok: "Los documentos se han modificado correctamente",
                },
                definitive: {
                    label: "Convertir a definitivo",
                    description: "Esta acción hará que los documentos pasen a formar parte del expediente de forma definitiva y no se podrán eliminar.",
                    ok: "Documento '{{document}}' cambiado a definitivo",
                    massiveOk: "Se han marcado como definitivos '{{data.num}}' documentos",
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
                view: "Solo para PDF, ODT y DOCX",
                portafirmes: "Es necesario seleccionar un procedimiento y un tipo de documento para poder realizar la acción masiva",
                documentsZip: "Se debe seleccionar al menos un documento para realizar la importación",
            },
            versio: {
                title: "Versión",
                data: "Fecha",
                arxiuUuid: "Archivo UUID",
            },
        },
        carpeta: {
            title: 'Carpeta',
            detail: {
                tite: "Detalle de la carpeta",
            },
            action: {
                new: {
                    label: "Carpeta...",
                    ok: "Carpeta '{{data.nom}}' creada correctamente",
                },
                update: {
                    label: "Modificar...",
                    title: "Modificar carpeta",
                    ok: "Carpeta '{{data.nom}}' modificada correctamente",
                },
                delete: {
                    label: "Borrar...",
                    check: "Esta seguro de que quiere seguir con esta acción?",
                    description: "Una vez borrado no se prodra recuperar",
                    ok: "Carpeta '{{data.nom}}' eliminada correctamente",
                }
			},
			restriccions: {
				 title: "Selecciona los usuarios que tendrán acceso a la carpeta (de entre los que ya tienen acceso al procedimiento)",
			     notEmpty: {
				 		message: "Se debe seleccionar al menos un usuario para crear la restricción"	
				 }
			}
        },
        dada: {
            title: "valor para el dato '{{metaDada}}'",
            noRowsText: "No hay valores para este dato",
            grid: {
                valor: "Valor del dato",
            },
            mensajeToolbar: {
                permis: "No tienes permiso para gestionar los valores de este dato.",
                maxDades: "Este tipo de dato solo permite indicar un único valor.",
            },
            action: {
                new: {
                    label: "Añadir valor para el dato",
                    ok: "El dato {{data.valor}} se ha creado correctamente",
                },
                update: {
                    ok: "El dato {{data.valor}} se ha modificado correctamente",
                },
                delete: {
                    ok: "El dato {{data.valor}} se ha eliminado correctamente",
                },
            },
        },
        metaDada: {
            title: "Meta-dato",
            plural: "Meta-datos",
            detail: {
                title: "Detalle del metadato",
                value: "Valores del metadato '{{metaDada}}'",
            },
            action: {
                activar: {
                    label: "Activar",
                    ok: "Meta-dato activado",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Meta-dato desactivado",
                },
                new: {
                    label: "Nuevo Meta-dato",
                    ok: "Meta-dato creado correctamente",
                },
                update: {
                    ok: "Meta-dato modificado correctamente",
                },
                delete: {
                    ok: "Meta-dato eliminado correctamente",
                },
            },
        },
        registre: {
            grid: {
                extracte: "Extracto",
                nomAnnex: "Nombre del anexo",
                origenRegistreNumero: "Número de registro",
                data: "Fecha de registro",
                dataRecepcio: "Fecha de recepción",
                destiDescripcio: "Destino",
                interessats: "Interesados",
                dataExpedient: "Expediente creado el",
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
                identifier: "Identificación",
                registre: "Información de registro",
                annexos: "Anexos",
            },
            justificant: {
                ntiFechaCaptura: "Fecha de captura (ENI)",
                ntiOrigen: "Origen (ENI)",
                ntiTipoDocumental: "Tipo documental (ENI)",
                uuid: "Identificador",
                titol: "Fichero",
                firmaTipus: "Tipo firma",
                firmaPerfil: "Perfil firma",
            }
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

                notificacioEstat: "Estado",
                createdDate: "Enviada el",
                entregaPostal: "Entrega postal",
                serveiTipusEnum: "Tipo de servicio",
                notificacioIdentificador: "Identificador",
            },
            action: {
                update: {
                    ok: "La remesa {{data.assumpte}} se ha modificado correctamente",
                },
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
                    ok: "Se ha descargado el justificant",
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
                    label: "Ampliar plazo...",
                    button: "Amplia plazo",
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
            title: "Publicación",
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
                update: {
                    ok: "La publicación {{data.assumpte}} se ha modificado correctamente",
                },
                delete: {
                    ok: "La publicación {{data.assumpte}} se ha eliminado correctamente",
                }
            },
        },
        documentVia: {
            tabs: {
                dades: "Datos",
                errors: "Errores",
            },
            alert: {
                reintentar: "Reintentar envío",
                enviament: "Se han producido errores al enviar el documento a viaFirma",
                processament: "Se han producido errores procesando la firma del documento",
                cancelat: "Se ha cancelado la firma",
            },
        },
        grup: {
            title: "Grupo",
            detail: {
                title: "Detalle del grupo",
            },
            grid: {
                default: "Por defecto",
            },
            action: {
                new: {
                    label: "Nuevo Grupo",
                    ok: "Grupo '{{data.codi}}' creado correctamente",
                },
                update: {
                    ok: "Grupo '{{data.codi}}' modificado correctamente",
                },
                delete: {
                    ok: "Grupo '{{data.codi}}' eliminado correctamente",
                },
                link: {
                    label: "Vincular grupo...",
                    button: "Vincula grupo",
                    title: "Vincular grupo",
                    ok: "Grupo vinculado",
                },
                unlink: {
                    label: "Desvincular",
                    ok: "Grupo desvinculado",
                },
                default: {
                    label: "Marcar por defecto",
                    ok: "Grupo marcado como defecto",
                },
                undefault: {
                    label: "Quitar por defecto",
                    ok: "Grupo desmarcado como predeterminado",
                },
            },
        },
        organGestor: {
            title: "Órgano Gestor",
            action: {
                update: {
                    ok: "Órgano Gestor '{{data.codi}}' eliminado correctamente",
                },
                actualitzar: {
                    title: "Predicción de sincronización",
                    label: "Actualizar órganos gestores de DIR3",
                    ok: "Los órganos están actualizados",
                    button: "Sincronizar",
                    tabs: {
                        empty: "Los órganos están actualizados",
                        firstSync: "Primera sincronización",
                        split: "Divisiones",
                        merge: "Fusiones",
                        subst: "Sustituciones",
                        change: "Cambios en atributos",
                        new: "Nuevos",
                        del: "Extintas",
                    },
                },
                vista: "Cambiar vista",
                pdf: "Descarga PDF",
            },
        },
        tipusDocumental: {
            title: "Tipo documental",
            action: {
                new: {
                    label: "Añadir tipo documental",
                    ok: "Tipo documental '{{data.codi}}' creado correctamente",
                },
                update: {
                    ok: "Tipo documental '{{data.codi}}' modificado correctamente",
                },
                delete: {
                    ok: "Tipo documental '{{data.codi}}' eliminado correctamente",
                },
            },
        },
        metaExpedient: {
            title: "Procedimiento",
            detall: {
                elementsProc: "Gestión del procedimiento: {{nom}}",
                elementsServ: "Gestión del servicio: {{nom}}",
                expressioNumero: "Si no se especifica ninguna expresión se utilizará la siguiente por defecto: {{codi}}/{{seq}}/{{any}}",
                permisDirecte: "Un usuario administrador de la entidad puede modificar este valor.",
                responsable: "Puede cambiar el responsable de la firma",
                portafirmesResponsables: "Puede cambiar los responsables de la firma",
                regla: {
                    create: "Creada",
                    data: "Fecha de creación",
                    activa: "Activa",
                    nom: "Nombre",
                },
            },
            tabs: {
                dades: "Datos",
                estat: "Estado de revisión",

                metaDocument: "Tipos de documento",
                metaDada: "Metadatos",
                expedientEstat: "Estados",
                tasca: "Tareas",
                grup: "Grupos",
                carpeta: "Carpetas",
            },
            action: {
                new: {
                    label: "Nuevo procedimiento",
                    ok: "Procedimiento creado correctamente",
                },
                update: {
                    ok: "Procedimiento modificado correctamente",
                },
                delete: {
                    ok: "Procedimiento eliminado correctamente",
                },
                consultar: {
                    title: "Detalle del procedimiento",
                    label: "Consultar",
                    revisat: "Este procedimiento no se puede modificar ya que se encuentra en estado revisado",
                },
                canviEstat: {
                    label: "Cambiar el estado de revisión...",
                    button: "Cambia el estado",
                    title: "Cambiar estado de revisión",
                    ok: "Estado canbiado correctamente",
                },
                expedient: {
                    title: "Expedientes del procedimiento: {{nom}}",
                    label: "Expedientes",
                },
                regla: {
                    title: "Estado de la regla en Distribución",
                    label: "Regla de distribución",
                    create: {
                        label: "Crear regla en Distribución",
                        ok: "La regla con código '{{nom}}' se ha creado correctamente.",
                    },
                    active: {
                        label: "Activar regla en Distribución",
                        ok: "La regla con código '{{nom}}' se ha activado correctamente.",
                    },
                    desactive: {
                        label: "Desactivar regla en Distribución",
                        ok: "La regla con código '{{nom}}' se ha desactivado correctamente",
                    },
                },
                activar: {
                    label: "Activar",
                    ok: "Procedimiento activado correctamente",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Procedimiento desactivado correctamente",
                },
                comment: {
                    ok: "Comentario añadido al procedimiento '{{data.metaExpedient.description}}'",
                },
                importRolsac: {
                    label: "Importar desde ROLSAC...",
                    title: "Importar procedimiento desde ROLSAC",
                },
                importFitxer: {
                    label: "Importar desde fichero...",
                    title: "Importar procedimiento",
                    ok: "Procedimiento importado correctamente",
                },
                export: {
                    ok: "Procedimiento exportado correctamente",
                },
                canviPendent: {
                    label: "Marcar como pendiente de revisión",
                    ok: "Procedimiento marcado como pendiente de revisión",
                },                
                canviDisseny: {
                    label: "Marcar como proceso de diseño",
                    ok: "Procedimiento marcado como proceso de diseño",
                },
                actualize: {
                    label: "Actualizar desde ROLSAC...",
                    button: "Actualiza",
                    title: "Actualización de procedimientos",
                    description: "¿Desea actualizar los procedimientos con la información de ROLSAC?",
                    ok: "Procedimientos actualizados",
                    result: {
                        title: "Inicio del proceso de actualización de los procedimientos",
                        description: "Se han realizado '{{numOperacions}}' peticiones, se han modificado '{{numActualitzats}}' procedimientos y '{{numErrord}}' han dado error",
                        senseCanvi: "Sin cambios",
                    }
                }
            },
            alert: {
                pendentsRevisio: "Hay {{num}} procedimientos o servicios pendientes de revisar",
            },
        },
        metaDocument: {
            title: "Tipo de documento",
            detail: {
                title: "Detalle del tipo de documento",
            },
            tabs: {
                dades: "Datos",
                nti: "Datos NTI",
                portafirmes: "Firma con portafirmas",
                navegador: "Firma con navegador",
                viaFirma: "Firma con viaFirma",
                pinbal: "PINBAL",
            },
            action: {
                default: {
                    label: "Marcar por defecto",
                    ok: "Tipo de documento marcado como defecto",
                },
                undefault: {
                    label: "Quitar por defecto",
                    ok: "Tipo de documento desmarcado como predeterminado",
                },
                activar: {
                    label: "Activar",
                    ok: "Tipo de documento activado",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Tipo de documento desactivado",
                },
                new: {
                    label: "Nuevo tipo de documento",
                    ok: "Tipo de documento creado correctamente",
                },
                update: {
                    ok: "Tipo de documento modificado correctamente",
                },
                delete: {
                    ok: "Tipo de documento eliminado correctamente",
                },
            },
        },
        expedientEstat: {
            title: "Estado del procedimiento",
            detail: {
                title: "Detalle del estado del procedimiento",
            },
            action: {
                new: {
                    label: "Nuevo estado",
                    ok: "Estado creado correctamente",
                },
                update: {
                    ok: "Estado modificado correctamente",
                },
                delete: {
                    ok: "Estado eliminado correctamente",
                },
            },
        },
        metaExpedientTasca: {
            title: "Tarea",
            detall: {
                title: "Detalle de la tarea",
                duracio: "Duración de la tarea en días naturales.",
                validacio: "Validaciones de la tarea: {{nom}}",
            },
            action: {
                activar: {
                    label: "Activar",
                    ok: "Tarea activada",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Tarea desactivada",
                },
                new: {
                    label: "Nueva tarea",
                    ok: "Tarea creada correctamente",
                },
                update: {
                    ok: "Tarea modificada correctamente",
                },
                delete: {
                    ok: "Tarea eliminada correctamente",
                },
            },
        },
        metaExpedientTascaValidacio: {
            title: "Validación",
            detail: {
                title: "Detalle de la validación",
            },
            action: {
                activar: {
                    label: "Activar",
                    ok: "Validación activada",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Validación desactivada",
                },
                new: {
                    label: "Nueva validación",
                    ok: "Validación creada correctamente",
                },
                update: {
                    ok: "Validación modificada correctamente",
                },
                delete: {
                    ok: "Validación eliminada correctamente",
                },
            },
        },
        domini: {
            title: "Dominio",
            action: {
                cleanCache: {
                    label: "Vaciar caché",
                    ok: "La caché se ha vaciado correctamente",
                },
                new: {
                    label: "Añadir dominio",
                    ok: "Dominio creado correctamente",
                },
                update: {
                    ok: "Dominio modificado correctamente",
                },
                delete: {
                    ok: "Dominio eliminado correctamente",
                },
            },
        },
        entitat: {
            title: "Entidad",
            form: {
                temaClar: "Configuración para el tema claro",
                temaFosc: "Configuración para el tema oscuro",
            },
            action: {
                new: {
                    label: "Nueva entidad",
                    ok: "Entidad creada correctamente",
                },
                update: {
                    ok: "Entidad modificada correctamente",
                },
                delete: {
                    ok: "Entidad eliminada correctamente",
                },
                config: {
                    label: "Configurar",
                },
                activar: {
                    label: "Activar",
                    ok: "Entidad activada",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Entidad desactivada",
                },
            },
        },
        avis: {
            title: "Aviso",
            action: {
                new: {
                    label: "Nuevo aviso",
                    ok: "Aviso creado correctamente",
                },
                update: {
                    ok: "Aviso modificado correctamente",
                },
                delete: {
                    ok: "Aviso eliminado correctamente",
                },
                activar: {
                    label: "Activar",
                    ok: "Aviso activado",
                },
                desactivar: {
                    label: "Desactivar",
                    ok: "Aviso desactivado",
                },
            },
        },
        pinbalServei: {
            title: "Servicio Pinbal",
            action: {
                update: {
                    ok: "Servicio Pinbal modificado correctamente",
                },
            },
        },
        urlInstruccio: {
            title: "URL de instrucción",
            detall: {
                url: "Formatos disponibles:\n - http://URL.es/alegar/[ENI]",
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
            title: "Propiedad",
            empty: "No se han encontrado propiedades",
            action: {
                sync: {
                    label: "Sincronizar con JBoss",
                    ok: "Las propiedades se han sincronizado correctamente",
                },
                new: {
                    label: "Añadir conf. específica",
                    ok: "La propiedad se ha creado correctamente",
                },
                update: {
                    ok: "La propiedad se ha modificado correctamente",
                },
                delete: {
                    ok: "La propiedad se ha eliminado correctamente",
                },
            }
        },
        exception: {
            action: {
                detail: {
                    title: "Detalles de la excepción",
                }
            }
        },
        integracio: {
            action: {
                detail: {
                    title: "Detalles de la comunicación con la integración",
                },
                diagnostic: {
                    title: "Diagnóstico",
                    label: "Repetir diagnóstico",
                },
                diagnosticAll: {
                    title: "Diagnóstico de los sistemas externos",
                    label: "Diagnóstico",
                },
                reiniciar: {
                    label: "Reiniciar plugin",
                    ok: "El plugin con código '{{nom}}' se ha reiniciado correctamente",
                },
                reiniciarAll: {
                    label: "Reiniciar todos",
                    ok: "Los plugins se han reiniciado correctamente",
                },
            }
        },
        sistema: {
            detail: {
                sistemaOperatiu: "Sistema operativo",
                arquitectura: "Arquitectura",
                processadors: "Procesadores",
                jbossVersion: "Versión de JBoss",
                applicationServerInfo: "Información del servidor de aplicaciones",
                tempsFuncionant: "Tiempo en funcionamiento",
                jvmMemory: "Máquina virtual de Java",
                disksUsage: "Disco y CPU",
            },
            tabs: {
                sistema: "Sistema",
                fils: "Hilos de ejecución",
                tasques: "Tareas en segundo plano",
            },
            action: {
                restart: {
                    label: "Reiniciar",
                    ok: "La tarea se ha reiniciado correctamente",
                },
                restartAll: {
                    label: "Reiniciar seleccionadas",
                    ok: "Las tareas se han reiniciado correctamente",
                },
            }
        },
        permision: {
            title: "Permisos",
            grid: {
                organGestor: "Órgano gestor",
                principal: "Tipo",
                sid: "Principal",
                create: "Creación",
                read: "Consulta",
                write: "Modificación",
                delete: "Eliminación",
                estadistic: "Estadísticas",
            },
            tabs: {
                expedient: "Gestión de expedientes",
                admin: "Administración y diseño",
            },
            action: {
                new: {
                    label: "Nuevo permiso",
                    title: "Crear nuevo permiso",
                    ok: "El permiso para '{{data.principal}} {{data.sid}}' se ha creado correctamente",
                },
                update: {
                    title: "Modificar permiso",
                    ok: "El permiso para '{{data.principal}} {{data.sid}}' se ha modificado correctamente",
                },
                delete: {
                    check: "¿Está seguro de que desea continuar con esta acción?",
                    description: "Una vez eliminada, no se podrá recuperar",
                    ok: "El permiso para '{{data.principal}} {{data.sid}}' se ha eliminado correctamente",
                },
            },
        },
        user: {
            options: {
                perfil: "Mi perfil",
                manual: "Manual de Usuario",
                manualAdmin: "Manual de los Administradores",
                logout: "Desconectar",
                noOrgans: "Ningún órgano gestor asignado",
            },
            menu: {
                entitat: "Entidades",
                expedient: "Expedientes",
                monitoritzar: "Monitorizar",
                integracions: "Integraciones",
                excepcions: "Excepciones",
                monitor: "Monitor del sistema",

                config: "Configuración",
                props: "Propiedades configurables",
                pinbal: "Servicios PINBAL",
                segonPla: "Reiniciar tareas en segundo plano...",
                plugins: "Reiniciar plugins...",
                avisos: "Avisos",
                backVersio: "Interfaz clásica",

                anotacions: "Anotaciones",
                procediments: "Procedimientos y servicios",
                procedimentsTitle: "Gestión de procedimientos y servicios",
                procedimentsRevisorTitle: "Revisión de procedimientos y servicios",
                procedimentPermis: "Permisos del procedimiento: {{nom}}",
                grups: "Grupos",
                grupPermis: "Permisos del grupo",
                revisar: "Revisión de procedimientos y servicios",
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
                documentDada: "Metadatos del tipo de documento: {{nom}}",
                nti: "Tipos documentales NTI",
                dominis: "Dominios",
                organs: "Órganos gestores",
                organPermis: "Permisos del órgano gestor: {{nom}}",
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
                refresh: "Refrescar dada 10 segundos"
            },
            action: {
                massives: {
                    label: "Consultar acciones masivas",
                    title: "Ejecuciones masivas de {{name}}",
                    detail: "Detalle de la acción masiva",
                    ok: "El documento se ha descargado correctamente",
                },
            },
            perfil: {
                title: "Mi perfil",
                ok: "Los datos del usuario '{{nom}}' se han modificado correctamente",
                dades: "Datos de usuario",
                correu: "Envio de correos",
                generic: "Configuración genérica",
                column: "Configuración de columnas del listado de expedientes",
                vista: "Configuración vista de documentos de expedientes",
                moure: "Configuración vista destino al mover documentos",
            }
        },
        alert: {
            title: "Errores de validación del expediente",
            action: {
                read: {
                    label: "Marcar como leído",
                    ok: "La alerta se ha marcado como leida",
                    massiveOk: "Las alertas se han marcado como leídas",
                },
            },
            errors: {
                metaDada: "Faltan los siguientes datos:",
                metaDocument: "Faltan los siguientes documentos:",
                metaNode: "Existen documentos sin un tipo de documento asignado",
                noFinalitzades: "Existen notificaciones con un estado que no es final",
                interessatObligatori: "Falta informar un interesado",
            },
        },
        notFound: "No encontrado",
    }
};

export default translationEs;