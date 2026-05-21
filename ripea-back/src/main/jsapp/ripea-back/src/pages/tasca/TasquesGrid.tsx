import {useMuiDataGridApiRef,GridPage} from 'reactlib';
import {useTranslation} from "react-i18next";
import { formatDate } from "../../util/dateUtils.ts";
import {StyledPrioritat} from "../expedient/ExpedientGrid.tsx";
import {TascaComment} from "../CommentDialog.tsx";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import TasquesFilter from "./TasquesFilter.tsx";
import {useCallback, useMemo, useState} from "react";
import Load from "../../components/Load.tsx";
import { CardPage } from "../../components/CardData.tsx";
import {useUserSession} from "../../components/Session.tsx";
import {Icon, Typography} from "@mui/material";
import useTascaActions from "./details/TascaActions.tsx";
import {useNavigate} from "react-router-dom";
import useTascaDetail from "./details/TascaDetail.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";

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

    const refresh = useCallback(() => {
        apiRef?.current?.refresh?.();
    }, [apiRef])

    const columns = useMemo(() => [
        {
            field: 'expedient',
            flex: 0.5,
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

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.tasca')}>
            <TasquesFilter
                onSpringFilterChange={(value:any) => {
                    setSpringFilter(value)
                    setLoad(true)
                }}
            />

            <Load value={load} noEffect>
                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName="expedientTascaResource"
                    columns={columns}
                    filter={springFilter}
                    namedQueries={namedQueries}
                    sortModel={sortModel}
                    perspectives={perspectives}
                    rowAdditionalActions={actions}
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
                        const observadors:any[] = row?.observadors?.map((obs:any)=>obs?.id);
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
                    toolbarHide
                    readOnly
                />
                {components}
                {dialog}
            </Load>
        </CardPage>
    </GridPage>
}

export default TasquesGrid;