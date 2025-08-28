import {useMuiDataGridApiRef,GridPage} from 'reactlib';
import {useTranslation} from "react-i18next";
import { formatDate } from "../../util/dateUtils.ts";
import {StyledPrioritat} from "../expedient/ExpedientGrid.tsx";
import {CommentDialog} from "../CommentDialog.tsx";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import TasquesGridFilter from "./TasquesGridFilter.tsx";
import {useMemo, useState} from "react";
import Load from "../../components/Load.tsx";
import { CardPage } from "../../components/CardData.tsx";
import * as builder from "../../util/springFilterUtils.ts";
import {useUserSession} from "../../components/Session.tsx";
import {Icon, Typography} from "@mui/material";
import useTascaActions from "./details/TascaActions.tsx";
import {useNavigate} from "react-router-dom";

const columns = [
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
        headerName: 'Responsable actual',
        flex: 0.5,
        sortable: false,
    },
    {
        field: 'dataLimit',
        flex: 0.4,
        valueFormatter: (value: any) => formatDate(value, "DD/MM/Y"),
        renderCell: (params: any) => {
            const color = params?.row?.dataLimitExpirada
                ?'error'
                :params?.row?.shouldNotifyAboutDeadline
                    ?'warning'
                    :'default'
            return <Typography variant={"inherit"} color={color}>{params?.formattedValue}<Icon>alarm</Icon></Typography>
        }
    },
    {
        field: 'estat',
        flex: 0.4,
    },
];

const sortModel:any = [{field: 'dataInici', sort: 'desc'}];
const TasquesGrid = () => {
    const { t } = useTranslation();
    const { value: user } = useUserSession();
    const navigate = useNavigate();

    const [springFilter, setSpringFilter] = useState<string>();
    const [load, setLoad] = useState<boolean>(false);

    const additionalColumns = useMemo(()=>[
        ...columns,
        {
            field: 'numComentaris',
            headerName: '',
            sortable: false,
            flex: 0.25,
            renderCell: (params: any) => <CommentDialog
                entity={params?.row}
                title={`${t('page.comment.tasca')}: ${params?.row?.metaExpedientTascaDescription}`}
                resourceName={'expedientTascaComentariResource'}
                resourceReference={'expedientTasca'}
                readOnly={params?.row?.usuariActualOnlyObservador}
                onClose={refresh}
            />
        },
    ], [columns])

    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        setLoad(true);
        apiRef?.current?.refresh?.();
    }

    const { actions, components } = useTascaActions({potModificar: true}, refresh)

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.tasca')}>
            <TasquesGridFilter
                onSpringFilterChange={(value:any)=>{
                    setSpringFilter(value)
                    setLoad(true)
                }}
            />

            <Load value={load} noEffect>
                <StyledMuiGrid
                    apiRef={apiRef}
                    resourceName="expedientTascaResource"
                    columns={additionalColumns}
                    filter={builder.and(
                        builder.eq("expedient.esborrat", 0),
                        springFilter,
                    )}
                    namedQueries={['USUARI_RELACIONAT']}
                    sortModel={sortModel}
                    rowAdditionalActions={actions}
                    onRowDoubleClick={(params: any) => navigate(`/contingut/${params?.row?.expedient?.id}/tasca/${params?.id}`)}
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
                    paginationActive
                    readOnly
                />
                {components}
            </Load>
        </CardPage>
    </GridPage>
}

export default TasquesGrid;