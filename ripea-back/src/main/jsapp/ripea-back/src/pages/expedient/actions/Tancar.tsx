import {useEffect, useMemo, useState} from "react";
import {Grid, Alert, Box} from "@mui/material";
import {
    useMuiFormDialogApiRef,
    useBaseAppContext,
    useFormContext,
    useResourceApiService,
} from "reactlib";
import {useTranslation} from "react-i18next";
import FormActionDialog from "@src/components/FormActionDialog.tsx";
import GridFormField from "@src/components/GridFormField.tsx";
import StyledMuiGrid from "@src/components/StyledMuiGrid.tsx";
import Load from "@src/components/Load.tsx";
import {formatDate} from "@src/util/dateUtils.ts";
import * as builder from "@src/util/springFilterUtils.ts";
import ContingutIcon from "@src/pages/contingut/details/ContingutIcon.tsx";
import {useUserSession} from "@src/components/Session.tsx";
import {type ProcesSegonPla, ProcesSegonPlaAlert, useProcesSegonPlaDialog} from "@src/components/ProcesSegonPla.tsx";

const columns: any[] = [
    // {
    //     field: 'nom',
    //     flex: 0.5,
    // },
    {
        field: 'metaDocument',
        flex: 0.5,
    },
    {
        field: 'createdDate',
        flex: 0.4,
        align: 'left',
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'createdByFullName',
        flex: 0.5,
    },
]
type TancarFormProps = {
    // Avisa el diàleg que l'expedient té un procés en segon pla en curs (no es pot tancar)
    setBloquejat?: (bloquejat: boolean) => void,
    onShowProgress?: (operacioId: string) => void,
}
const TancarForm = (props: TancarFormProps) => {
    const { setBloquejat, onShowProgress } = props;
    const { t } = useTranslation();
    const {data, apiRef: formApiRef} = useFormContext();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);
    const [rowsCount, setRowsCount] = useState<any>();
    const [entities, setEntities] = useState<any>();

    const filter = useMemo(() => builder.and(
        builder.inside('expedient.id', data?.ids),
        builder.or(
            builder.eq('estat', "'REDACCIO'"),
            builder.eq('arxiuUuid', null),
        ),
        builder.eq('esborrat', 0),
    ), [data?.ids])

    const {
        isReady: apiIsReady,
        find: apiFind,
    } = useResourceApiService('expedientResource');

    useEffect(() => {
        if (apiIsReady && data?.ids) {
            apiFind({
                filter: builder.inside('id', data?.ids),
                unpaged: true,
                perspectives: [
                    'DOCUMENTS_OBLIGATORIS_TANCAR',// documentObligatorisAlTancar
                    'NOTIFICACIONS_CADUCADES',// conteNotificacionsCaducades
                    'DOCUMENTS_NO_MOGUTS',// conteDocumentsDeAnotacionesNoMogutsASerieFinal
                    'AVISOS',// valid + errors (evita el triangle groc fals a la fila de l'expedient)
                    'AUDITORIA',
                    'PROCES_SEGON_PLA',// procesSegonPla: tancament en curs (es demana ara, la fila pot no estar al dia)
                ]
            })
                .then((app) => setEntities(app?.rows))
                .catch(() => setEntities(undefined))
        }
    }, [apiIsReady]);

    // Tancament (o un altre procés en segon pla) ja en curs sobre l'expedient: només es mostra l'avís
    const procesEnCurs = !data?.massivo ? entities?.[0]?.procesSegonPla : undefined;
    useEffect(() => {
        setBloquejat?.(procesEnCurs != null);
    }, [procesEnCurs]);

    const temp = useMemo(()=>{
        return {
            documentObligatorisAlTancar: entities?.flatMap?.((e:any) => e?.documentObligatorisAlTancar),
            conteNotificacionsCaducades: entities?.some?.((e:any) => e?.conteNotificacionsCaducades),
            conteDocumentsDeAnotacionesNoMogutsASerieFinal: entities?.some?.((e:any) => e?.conteDocumentsDeAnotacionesNoMogutsASerieFinal),
            metaDocumentsFaltants: Array.from(new Set(entities
                ?.filter?.((e:any) => e?.potTancarAmbDocumentsFaltants)
                ?.flatMap?.((e:any) => e?.metaDocumentsFaltants ?? []) ?? [])),
        }
    },[entities])

    const selectedModel: any[] = useMemo(() =>
        temp?.documentObligatorisAlTancar?.map?.((row: any) => row?.id) ?? []
    , [temp?.documentObligatorisAlTancar])

    useEffect(() => {
        const selected = Array.from(new Set([...selectedRows, ...selectedModel || []]))
        formApiRef?.current?.setFieldValue('documentsPerFirmar', selected.filter((s) => !data.ids.includes(s)) );
    }, [selectedRows, selectedModel]);

    if (procesEnCurs != null) {
        return <ProcesSegonPlaAlert proces={procesEnCurs} onShowProgress={onShowProgress}/>;
    }

    return <Load value={entities && temp}>
        <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
            <Grid size={12} hidden={!rowsCount}>
                <Alert severity={"info"}>{t('page.expedient.alert.borradors')}</Alert>
            </Grid>
            <Grid size={12}>
                <Load value={selectedModel} noEffect>
                    <StyledMuiGrid
                        autoHeight
                        resourceName={"documentResource"}
                        persistentStateActive={false}
                        columns={columns}
                        filter={filter}
                        selectionActive
                        rowSelectionModel={selectedModel}
                        // isRowSelectable={(params) => !selectedModel.includes(params.row?.id) && params.row?.tipus=="DOCUMENT"}
                        onRowCountChange={setRowsCount}
                        onRowSelectionModelChange={(newSelection) => {
                            setSelectedRows([...newSelection]);
                        }}
                        paginationActive={false}
                        toolbarHide
                        readOnly
                        groupingColDef={{
                            headerName: t('page.contingut.grid.nom'),
                            flex: 1,
                            valueFormatter: (value: any, row: any) => {
                                if (row?.id) {
                                    return <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                        <ContingutIcon entity={row} />
                                    </Box>
                                }
                                return value;
                            },
                        }}
                        treeData
                        rowsTransformer={(_rows: any) => {
                            if (!_rows) return [];
                            const additionalRows: any[] = _rows;
                            if (entities!=null && data?.massivo){
                                for (const entity of entities) {
                                    if (!additionalRows.map((b) => b.id).includes(entity?.id)) {
                                        additionalRows.push(entity)
                                    }
                                }
                            }
                            return additionalRows;
                        }}
                        getTreeDataPath={(row: any): string[] => {
                            return (data?.massivo && row?.expedient) ?[`${row?.expedient?.id}`, `${row.id}`] :[`${row.id}`]
                        }}
                        isGroupExpandedByDefault={() => true}
                    />
                </Load>
            </Grid>

            <Grid size={12} hidden={!temp?.conteNotificacionsCaducades}>
                <Alert severity={"warning"}>{t('page.expedient.alert.notificacio')}</Alert>
            </Grid>
            <Grid size={12} hidden={!temp?.conteDocumentsDeAnotacionesNoMogutsASerieFinal}>
                <Alert severity={"warning"}>{t('page.expedient.alert.documents')}</Alert>
            </Grid>
            <Grid size={12} hidden={!temp?.metaDocumentsFaltants?.length}>
                <Alert severity={"warning"}>
                    {t('page.expedient.alert.documentsFaltants')}
                    <ul>
                        {temp?.metaDocumentsFaltants?.map?.((nom: any) => <li key={nom}>{nom}</li>)}
                    </ul>
                </Alert>
            </Grid>

            <GridFormField name="motiu" type={"textarea"} required/>
        </Grid>
    </Load>
}

const Tancar = (props: any) => {
    const {onShowProgress, ...otherProps} = props;
    const {t} = useTranslation();
    const [bloquejat, setBloquejat] = useState<boolean>(false);

    return <FormActionDialog
        resourceName={"expedientResource"}
        action={"TANCAR"}
        title={(data) => {
            return (data?.massivo)
                ? t('page.expedient.action.close.titleMassive', {num: data?.ids?.length})
                : t('page.expedient.action.close.title')
        }}
        formDialogButtons={[
            {icon: 'check', text: t('page.expedient.action.close.button'), componentProps: { variant: 'contained', disabled: bloquejat }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...otherProps}
    >
        <TancarForm setBloquejat={setBloquejat} onShowProgress={onShowProgress}/>
    </FormActionDialog>
}

/**
 * Tancament individual (#1903). L'acció valida i llança el tancament en segon pla; a partir d'aquí l'usuari en segueix
 * el progrés al diàleg de procés (ProcesSegonPlaDialog), que pot tancar quan vulgui: el tancament continua al servidor.
 *
 * Si l'expedient ja té un tancament en curs:
 *  - de l'usuari actual: s'obre directament el diàleg de progrés;
 *  - d'un altre usuari: la modal només mostra qui l'està tancant, i no deixa tancar.
 *
 * En tancar el diàleg de progrés s'ha de mostrar l'expedient tancat o, si encara és en curs, bloquejat:
 *  - onProcesClose: per a qui es pot actualitzar sense recarregar la pàgina (la fitxa de l'expedient, que a més rep
 *    els events SSE d'inici i fi del procés). Rep l'estat del procés en tancar el diàleg.
 *  - si no n'hi ha, refresh (p.ex. la graella d'expedients).
 */
export const useTancar = (refresh?: () => void, onProcesClose?: (proces?: ProcesSegonPla) => void) => {
    const apiRef = useMuiFormDialogApiRef();
    const { value: user } = useUserSession();

    const {show: showProgres, dialog: dialogProgres} = useProcesSegonPlaDialog((proces) => {
        if (onProcesClose) {
            onProcesClose(proces);
        } else {
            refresh?.();
        }
    });

    const handleShow = (id: any, row?: any): void => {
        const procesEnCurs = row?.procesSegonPla;
        if (procesEnCurs?.operacioId && procesEnCurs?.usuariCodi === user?.codi) {
            showProgres(procesEnCurs.operacioId);
            return;
        }
        apiRef.current?.show?.(undefined, {
            ids: [id],
            massivo: false,
        })
    }
    const onShowProgress = (operacioId: string) => {
        apiRef.current?.close?.();
        showProgres(operacioId);
    }
    // El resultat de l'acció és l'estat inicial del procés en segon pla
    const onSuccess = (result: any): void => {
        if (result?.operacioId) {
            showProgres(result.operacioId);
        } else {
            refresh?.();
        }
    }

    return {
        handleShow,
        content: <>
            <Tancar apiRef={apiRef} onSuccess={onSuccess} onShowProgress={onShowProgress}/>
            {dialogProgres}
        </>
    }
}
export const useTancarMassive = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useMuiFormDialogApiRef();
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids: any[]): void => {
        apiRef.current?.show?.(undefined, {
            ids,
            massivo: true,
        })
    }
    const onSuccess = (): void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.results.actionBackgroundOk'), 'info');
    }

    return {
        handleShow,
        content: <Tancar apiRef={apiRef} onSuccess={onSuccess}/>
    }
}
export default useTancar;