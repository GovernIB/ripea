import {
    GridPage,
    MuiFormDialog,
    MuiFormDialogApi,
    MuiGrid,
    useBaseAppContext,
    useConfirmDialogButtons,
    useMuiDataGridApiRef,
    useResourceApiService,
} from 'reactlib';
import {Grid} from "@mui/material";
import React from "react";
import GridFormField from "../../../components/GridFormField.tsx";

const InteressatsGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="tipus" required/>
        <GridFormField xs={12} name="documentTipus" required/>
        <GridFormField xs={12} name="documentNum"/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={6} name="llinatge1"/>
        <GridFormField xs={6} name="llinatge2"/>
        <GridFormField xs={6} name="pais"/>
        <GridFormField xs={6} name="provincia"/>
        <GridFormField xs={6} name="municipi"/>
        <GridFormField xs={6} name="codiPostal"/>
        <GridFormField xs={12} name="adresa" type={"textarea"}/>
        <GridFormField xs={6} name="email"/>
        <GridFormField xs={6} name="telefon"/>
        <GridFormField xs={12} name="observacions" type={"textarea"}/>
        <GridFormField xs={12} name="preferenciaIdioma" required/>
    </Grid>
}

interface DetailGridProps {
    id: any,
    onRowCountChange?: (number: number) => void,
}

const InteressatsGrid: React.FC<DetailGridProps> = (props: DetailGridProps) => {
    const {id, onRowCountChange} = props
    const formApiRef = React.useRef<MuiFormDialogApi>();
    const {messageDialogShow, temporalMessageShow} = useBaseAppContext();
    const confirmDialogButtons = useConfirmDialogButtons();
    const confirmDialogComponentProps = {maxWidth: 'sm', fullWidth: true};

    const {
        delette: apiDelete,
        patch: apiPatch,
        getOne
    } = useResourceApiService('interessatResource');
    const apiRef = useMuiDataGridApiRef()

    const createRepresentent = (rowId: any) => {
        formApiRef.current?.show(undefined, {
            expedient: {
                id: id
            },
            representat: {
                id: rowId
            },
            esRepresentant: true,
        })
            .then(() => {
                apiRef?.current?.refresh?.();
                temporalMessageShow(null, 'Elemento creado', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }
    const updateRepresentent = (rowId: any, row: any) => {
        formApiRef.current?.show(row?.representant?.id)
            .then(() => {
                apiRef?.current?.refresh?.();
                temporalMessageShow(null, 'Elemento modificado', 'success');
            })
            .catch((error) => {
                temporalMessageShow('Error', error.message, 'error');
            });
    }
    const deleteRepresentent = (rowId: any, row: any) => {
        getOne(row?.representant?.id)
            .then((representant) => {
                if (representant?.esRepresentant) {
                    messageDialogShow(
                        'Title',
                        'Message',
                        confirmDialogButtons,
                        confirmDialogComponentProps)
                        .then((value: any) => {
                            if (value) {
                                apiDelete(representant?.id)
                                    .then(() => {
                                        apiRef?.current?.refresh?.();
                                        temporalMessageShow(null, 'Elemento borrado', 'success');
                                    })
                                    .catch((error) => {
                                        temporalMessageShow('Error', error.message, 'error');
                                    });
                            }
                        });
                } else {
                    apiPatch(rowId, {
                        data: {
                            representant: null,
                        }
                    })
                        .then(() => {
                            apiRef?.current?.refresh?.();
                            temporalMessageShow(null, 'Elemento borrado', 'success');
                        })
                }
            })
    }
    const deleteInteressat = (rowId: any, row: any) => {
        if (row?.hasRepresentats) {
            apiPatch(rowId, {
                data: {
                    esRepresentant: true,
                }
            })
                .then(() => {
                    apiRef?.current?.refresh?.();
                    temporalMessageShow(null, 'Elemento borrado', 'success');
                })
        } else {
            messageDialogShow(
                'Title',
                'Message',
                confirmDialogButtons,
                confirmDialogComponentProps)
                .then((value: any) => {
                    if (value) {
                        apiDelete(rowId)
                            .then(() => {
                                apiRef?.current?.refresh?.();
                                temporalMessageShow(null, 'Elemento borrado', 'success');
                            })
                            .catch((error) => {
                                temporalMessageShow('Error', error.message, 'error');
                            });
                    }
                });
        }
    }

    const columns = [
        {
            field: 'tipus',
            flex: 1,
        },
        {
            field: 'documentNum',
            flex: 0.5,
        },
        {
            field: 'nomComplet',
            flex: 0.5,
        },
        {
            field: 'representant',
            flex: 0.5,
            valueFormatter: (value: any) => {
                return value?.description;
            }
        },
    ];
    const actions = [
        {
            title: "Borrar Interesado",
            icon: "delete",
            showInMenu: true,
            onClick: deleteInteressat,
        },
        {
            title: "Añadir Representante",
            icon: "add",
            showInMenu: true,
            onClick: createRepresentent,
            disabled: (row: any) => row?.representant,
        },
        {
            title: "Modificar Representante",
            icon: "edit",
            showInMenu: true,
            onClick: updateRepresentent,
            disabled: (row: any) => !row?.representant,
        },
        {
            title: "Borrar Representante",
            icon: "delete",
            showInMenu: true,
            onClick: deleteRepresentent,
            disabled: (row: any) => !row?.representant,
        },
    ];

    return <GridPage>
        <MuiGrid
            resourceName="interessatResource"
            columns={columns}
            paginationActive
            apiRef={apiRef}
            filter={`expedient.id:${id} AND esRepresentant:false`}
            staticSortModel={[{field: 'id', sort: 'asc'}]}
            disableColumnSorting
            disableColumnMenu
            titleDisabled
            popupEditCreateActive
            popupEditFormContent={<InteressatsGridForm/>}
            formAdditionalData={{
                expedient: {
                    id: id
                },
            }}
            rowAdditionalActions={actions}
            onRowsChange={(rows) => onRowCountChange && onRowCountChange(rows.length)}
            rowHideDeleteButton
        />

        <MuiFormDialog
            resourceName={"interessatResource"}
            title={`Representante`}
            apiRef={formApiRef}
        >
            <InteressatsGridForm/>
        </MuiFormDialog>
    </GridPage>
}

export default InteressatsGrid;