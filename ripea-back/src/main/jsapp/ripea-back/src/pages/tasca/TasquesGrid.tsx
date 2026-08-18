import {useMuiDataGridApiRef, GridPage, useFilterApiRef} from 'reactlib';
import {useTranslation} from "react-i18next";
import { formatDate } from "../../util/dateUtils.ts";
import {StyledPrioritat} from "../expedient/ExpedientGrid.tsx";
import {TascaComment} from "../CommentDialog.tsx";
import StyledMuiGrid, {FilterCountChip} from '../../components/StyledMuiGrid.tsx';
import ViewSelector from '../../components/ViewSelector.tsx';
import TasquesFilter, {TASCA_FILTER_SESSION_KEY} from "./TasquesFilter.tsx";
import {useCallback, useEffect, useMemo, useRef, useState} from "react";
import Load from "../../components/Load.tsx";
import { CardPage } from "../../components/CardData.tsx";
import {useUserSession} from "../../components/Session.tsx";
import {Grid, Icon, Typography} from "@mui/material";
import useTascaActions, {tascaTramitacioRoute} from "./details/TascaActions.tsx";
import ContingutLink from "../../components/ContingutLink.tsx";
import {openRouteInNewTab} from "../../util/navigationUtils.ts";
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
// Vistes que ofereix el selector, amb la icona de cada botó i el text que se'n mostra
// com a tooltip (i com a etiqueta accessible, ja que el botó no porta text visible).
const tascaViewOptions: { view: TascaView, icon: string, i18nKey: string }[] = [
    { view: TascaView.table, icon: 'view_headline', i18nKey: 'page.tasca.view.table' },
    { view: TascaView.calendar, icon: 'calendar_month', i18nKey: 'page.tasca.view.calendar' },
    { view: TascaView.kanban, icon: 'view_week', i18nKey: 'page.tasca.view.kanban' },
];

export const TascaViewSelector = (props: { value: any, onChange: (value: any) => void }) => {
    const { value, onChange } = props;
    const { t } = useTranslation();

    const options = useMemo(() => tascaViewOptions.map(({ view, icon, i18nKey }) => ({
        value: view,
        icon,
        label: t(i18nKey),
    })), [t]);

    return <ViewSelector
        value={value}
        onChange={onChange}
        options={options}
        groupLabel={t('page.tasca.view.title')}
    />
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


// El filtre spring sempre inclou la condició fixa "expedient.esborrat = 0" (veure
// TasquesFilter), que no és un filtre triat per l'usuari i no s'ha de comptar.
const filterCount = (num: number) => num - 1;

/** Període (dies enrere) al qual la vista kanban limita la data d'inici. */
const KANBAN_DIES_DATA_INICI = 30;
// Estats amb què arrenca el filtre. Han de coincidir amb el valor per defecte del filtre al
// backend (ExpedientTascaResource.TascaFilterForm.estats), que és qui els aplica en entrar.
const FILTRE_ESTATS_PER_DEFECTE = ['PENDENT', 'INICIADA', 'AGAFADA'];

const sortModel:any = [{field: 'dataInici', sort: 'desc'}];
const namedQueries:any = ['USUARI_RELACIONAT'];
const perspectives:any = ['CONTEXT_USUARI'];
const TasquesGrid = () => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();
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

    // Acció principal de la tasca, compartida per la vista de taula (clic a la fila) i la de
    // calendari (clic amb el botó esquerre), perquè totes dues es comportin igual. La tramitació
    // s'obre en una pestanya nova per no perdre el llistat ni els filtres aplicats.
    const handleTascaClick = (tasca: any) => {
        if (isTramitable(tasca)) {
            openRouteInNewTab(tascaTramitacioRoute(tasca?.expedient?.id, tasca?.id));
        } else {
            handleOpen(tasca?.id);
        }
    };

    const filterRef = useFilterApiRef();
    const { save: saveFilterData } = useSession(TASCA_FILTER_SESSION_KEY);
    // Es guarda per referència perquè useSession en retorna una de nova a cada render: com a
    // dependència faria que l'efecte de la vista es tornés a executar (i a cercar) sense parar.
    const saveFilterDataRef = useRef(saveFilterData);
    saveFilterDataRef.current = saveFilterData;
    // Aplica valors als camps indicats del filtre i torna a cercar. La cerca es fa sobre les
    // dades actuals del filtre amb els valors nous a sobre: filter(data) no fa merge, i passar
    // només els camps canviats descartaria la resta de filtres de l'usuari (procediment,
    // títol...) tot i seguir-se veient al formulari.
    const applyFilterValues = useCallback((values: any) => {
        if (filterRef.current == null) {
            return;
        }
        Object.entries(values).forEach(([name, value]) => filterRef.current?.setFieldValue(name, value));
        const data = { ...filterRef.current.getData?.(), ...values };
        filterRef.current.filter(data);
        // El filtre desa a la sessió el que retorna getData() en el moment de cercar, que encara
        // no reflecteix els setFieldValue d'aquesta mateixa passada. Es re-desa aquí perquè hi
        // quedin els valors realment aplicats i no els anteriors (si no, en tornar a la pantalla
        // es recuperaria un filtre que no és el que es veu).
        saveFilterDataRef.current(data);
    }, [filterRef]);

    // El filtre carrega els seus camps del backend: fins que no ho ha fet, setFieldValue no té
    // efecte i el formulari s'acabaria inicialitzant amb les dades de la sessió. Per això la
    // vista no s'aplica fins que el filtre notifica dades (cas d'entrar a la pantalla amb la
    // vista kanban ja desada).
    const [filterReady, setFilterReady] = useState<boolean>(false);

    // La vista kanban mostra les tasques per estat, i per això amplia el filtre: treu els estats
    // i limita la cerca a l'últim mes. En sortir-ne es tornen aquests dos camps al seu valor per
    // defecte, perquè són canvis de la vista i no del que havia demanat l'usuari.
    const vistaAnteriorRef = useRef<TascaView | undefined>(undefined);
    useEffect(() => {
        if (!filterReady) {
            return;
        }
        const vistaAnterior = vistaAnteriorRef.current;
        vistaAnteriorRef.current = vista;

        if (vista == TascaView.kanban) {
            const dataInici = new Date();
            dataInici.setDate(dataInici.getDate() - KANBAN_DIES_DATA_INICI);
            applyFilterValues({ dataInici, estats: undefined });
        } else if (vistaAnterior == TascaView.kanban) {
            applyFilterValues({ dataInici: null, estats: [...FILTRE_ESTATS_PER_DEFECTE] });
        }
    }, [vista, filterReady, applyFilterValues]);

    return <GridPage autoHeight>
        <CardPage title={t('page.user.menu.tasca')}>
            <TasquesFilter
                apiRef={filterRef}
                onDataChange={() => setFilterReady(true)}
                onSpringFilterChange={(value:any) => {
                    setSpringFilter(value)
                    setLoad(true)
                }}
            />

            <Load value={load} noEffect>
                {vista != TascaView.table && <>
                    <Grid container display={'flex'} justifyContent={'end'} alignItems={'center'} mb={1}>
                        <FilterCountChip filter={springFilter} filterCount={filterCount} sx={{mr: 'auto'}}/>
                        <TascaViewSelector value={vista} onChange={setVista}/>
                    </Grid>
                    {vista == TascaView.kanban && <TascaKanban actions={actions} filter={springFilter} namedQueries={namedQueries} perspectives={perspectives} reloadTrigger={reload} readOnly />}
                    {vista == TascaView.calendar && <TascaCalendar actions={actions} filter={springFilter} namedQueries={namedQueries} perspectives={perspectives} reloadTrigger={reload} onEventClick={handleTascaClick} />}
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
                        onRowClick={(params: any) => handleTascaClick(params?.row)}
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
                        filterCount={filterCount}
                    />
                }
                {components}
                {dialog}
            </Load>
        </CardPage>
    </GridPage>
}

export default TasquesGrid;
