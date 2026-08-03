import {useMuiDataGridApiRef, GridPage, useFilterApiRef} from 'reactlib';
import {useTranslation} from "react-i18next";
import { formatDate } from "../../util/dateUtils.ts";
import {StyledPrioritat} from "../expedient/ExpedientGrid.tsx";
import {TascaComment} from "../CommentDialog.tsx";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import TasquesFilter from "./TasquesFilter.tsx";
import {useCallback, useEffect, useMemo, useState} from "react";
import Load from "../../components/Load.tsx";
import { CardPage } from "../../components/CardData.tsx";
import {useUserSession} from "../../components/Session.tsx";
import {FormControl, Grid, Icon, MenuItem, Select, Typography} from "@mui/material";
import useTascaActions from "./details/TascaActions.tsx";
import ContingutLink from "../../components/ContingutLink.tsx";
import {useNavigate} from "react-router-dom";
import useTascaDetail from "./details/TascaDetail.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";
import {TascaCalendar} from "@src/pages/tasca/TascaCalendar.tsx";
import {TascaKanban} from "@src/pages/tasca/TascaKanban.tsx";
import {useSession} from "@src/components/SessionStorageContext.tsx";

export const StyledDate = (props: any) => {
    const {entity, children} = props;
    const color = entity?.dataLimitExpirada
        ?'error'
        :entity?.shouldNotifyAboutDeadline
            ?'warning'
            :'default'
    return <Typography variant={"inherit"} color={color}>
        {children}
        {(entity?.dataLimitExpirada || entity?.shouldNotifyAboutDeadline) && <Icon>alarm</Icon>}
    </Typography>
}

export enum TascaView {
    table = 'TABLE',
    calendar = 'CALENDAR',
    kanban = 'KANBAN',
}
export const TascaViewSelector = (props: { value: any, onChange: (value: any) => void }) => {
    const { value, onChange } = props;
    const { t } = useTranslation();

    return <Grid size={3} sx={{ ml: 1 }}>
        <FormControl fullWidth size="small">
            <Select
                sx={{ maxHeight: '32px' }}
                title={t('page.document.view.title')}
                labelId="demo-simple-select-label"
                value={value}
                onChange={(event) => onChange(event.target.value)}
            >
                <MenuItem value={TascaView.table}>{t('page.tasca.view.table')}</MenuItem>
                <MenuItem value={TascaView.calendar}>{t('page.tasca.view.calendar')}</MenuItem>
                <MenuItem value={TascaView.kanban}>{t('page.tasca.view.kanban')}</MenuItem>
            </Select>
        </FormControl>
    </Grid>
}

// Persisteix el tipus de vista seleccionat (taula/calendari/kanban) a la sessió del
// navegador amb la clau indicada, de manera que es manté en tornar a la pantalla.
export const useTascaView = (sessionKey: string) => {
    const { value, save } = useSession(sessionKey);
    return {
        vista: (value as TascaView) ?? TascaView.table,
        setVista: (v: TascaView) => save(v),
    };
}


const sortModel:any = [{field: 'dataInici', sort: 'desc'}];
const namedQueries:any = ['USUARI_RELACIONAT'];
const perspectives:any = ['CONTEXT_USUARI'];
const TasquesGrid = () => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();
    const navigate = useNavigate();
    const apiRef = useMuiDataGridApiRef();

    const [springFilter, setSpringFilter] = useState<string>();
    const [load, setLoad] = useState<boolean>(false);
    const { vista, setVista } = useTascaView('usuariTascaView');
    const [reload, setReload] = useState<boolean>(false);

    const refresh = useCallback(() => {
        apiRef?.current?.refresh?.();
        setReload((prev) => !prev)
    }, [apiRef])

    const columns = useMemo(() => [
        {
            field: 'expedient',
            flex: 0.5,
            renderCell: (params: any) => <ContingutLink
                id={params?.row?.expedient?.id}
                onClick={(e: any) => e.stopPropagation()}
            >{params?.formattedValue}</ContingutLink>,
        },
        {
            field: 'metaExpedientTasca',
            flex: 0.6,
        },
        {
            field: 'metaExpedientTascaDescription',
            flex: 0.6,
            sortProcessor: (_field: string, sort: GridSortDirection) => {
                return [
                    { field: "metaExpedientTasca.descripcio", sort },
                ]
            }
        },
        {
            field: 'titol',
            flex: 0.6,
        },
        {
            field: 'observacions',
            flex: 0.5,
        },
        {
            field: 'prioritat',
            flex: 0.4,
            renderCell: (params: any) => <StyledPrioritat entity={params?.row}>{params.formattedValue}</StyledPrioritat>
        },
        {
            field: 'dataInici',
            flex: 0.5,
            valueFormatter: (value: any) => formatDate(value)
        },
        {
            field: 'responsablesActualStr',
            headerName: t('page.tasca.detall.responsableActual'),
            flex: 0.5,
            sortable: false,
        },
        {
            field: 'dataLimit',
            flex: 0.4,
            valueFormatter: (value: any) => formatDate(value, "DD/MM/Y"),
            renderCell: (params: any) => <StyledDate entity={params?.row}>{params?.formattedValue}</StyledDate>
        },
        {
            field: 'estat',
            flex: 0.4,
        },
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            flex: 0.25,
            renderCell: (params: any) => <TascaComment
                entity={params?.row}
                readOnly={params?.row?.usuariActualOnlyObservador}
                onClose={refresh}
            />
        },
    ], [refresh, t]);

    const { actions, components, isTramitable } = useTascaActions({potModificar: true}, refresh)
    const { handleOpen, dialog } = useTascaDetail();

    const filterRef = useFilterApiRef();
    useEffect(() => {
        if (filterRef.current != null && vista == TascaView.kanban) {
            const fecha = new Date();
            fecha.setDate(fecha.getDate() - 30);

            filterRef.current.setFieldValue("dataInici", fecha)
            filterRef.current.setFieldValue("estats", undefined)
            filterRef.current.filter({
                dataInici: fecha,
                estats: undefined
            })
        }
    }, [vista]);

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.tasca')}>
            <TasquesFilter
                apiRef={filterRef}
                onSpringFilterChange={(value:any) => {
                    setSpringFilter(value)
                    setLoad(true)
                }}
            />

            <Load value={load} noEffect>
                {vista != TascaView.table && <>
                    <Grid container display={'flex'} justifyContent={'end'} mb={1}>
                        <TascaViewSelector value={vista} onChange={setVista}/>
                    </Grid>
                    {vista == TascaView.kanban && <TascaKanban actions={actions} filter={springFilter} namedQueries={namedQueries} perspectives={perspectives} reloadTrigger={reload} readOnly />}
                    {vista == TascaView.calendar && <TascaCalendar actions={actions} filter={springFilter} namedQueries={namedQueries} perspectives={perspectives} reloadTrigger={reload} />}
                </>}

                {vista == TascaView.table &&
                    <StyledMuiGrid
                        apiRef={apiRef}
                        resourceName="expedientTascaResource"
                        columns={columns}
                        filter={springFilter}
                        toolbarShowFilterCount
                        namedQueries={namedQueries}
                        sortModel={sortModel}
                        perspectives={perspectives}
                        rowAdditionalActions={actions}
                        toolbarElementsWithPositions={[
                            {
                                position: 1,
                                element: <TascaViewSelector value={vista} onChange={setVista}/>,
                            }
                        ]}
                        onRowClick={(params: any) => {
                            if (isTramitable(params?.row)) {
                                navigate(`/contingut/${params?.row?.expedient?.id}/tasca/${params?.id}`)
                            } else {
                                handleOpen(params?.row?.id)
                            }
                        }}
                        rowProps={(row: any) => {
                            let color;
                            if (row?.delegat?.id == user.codi) {
                                color = '#36cfe8';
                            }
                            const observadors: any[] = row?.observadors?.map((obs: any) => obs?.id);
                            if (observadors.includes(user.codi)) {
                                color = '#75ce73'
                            }
                            return color
                                ? {
                                    'box-shadow': `${color} -6px 0px 0px`,
                                    'border-left': `6px solid ${color}`,
                                }
                                : {
                                    'padding-left': '6px'
                                }
                        }}
                        // toolbarHide
                        readOnly
                        toolbarHideRefresh
                        filterCount={(num) => num - 1}
                    />
                }
                {components}
                {dialog}
            </Load>
        </CardPage>
    </GridPage>
}

export default TasquesGrid;
