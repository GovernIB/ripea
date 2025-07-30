import {GridPage,} from 'reactlib';
import {useTranslation} from "react-i18next";
import { formatDate } from "../../util/dateUtils.ts";
import {StyledPrioritat} from "../expedient/ExpedientGrid.tsx";
import {CommentDialog} from "../CommentDialog.tsx";
import StyledMuiGrid from '../../components/StyledMuiGrid.tsx';
import TasquesGridFilter from "./TasquesGridFilter.tsx";
import {useMemo, useState} from "react";
import Load from "../../components/Load.tsx";
import { CardPage } from "../../components/CardData.tsx";

const columns = [
    {
        field: 'expedient',
        flex: 0.5,
    },
    {
        field: 'metaExpedientTasca',
        flex: 0.5,
    },
    {
        field: 'metaExpedientTascaDescription',
        flex: 0.5,
    },
    {
        field: 'titol',
        flex: 0.5,
    },
    {
        field: 'observacions',
        flex: 0.5,
    },
    {
        field: 'prioritat',
        flex: 0.5,
        renderCell: (params: any) => <StyledPrioritat entity={params?.row}>{params.formattedValue}</StyledPrioritat>
    },
    {
        field: 'dataInici',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'responsableActual',
        flex: 0.5,
        sortable: false,
    },
    {
        field: 'dataLimit',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value, "DD/MM/Y")
    },
    {
        field: 'estat',
        flex: 0.5,
    },
];

const sortModel:any = [{field: 'dataInici', sort: 'desc'}];
const TasquesGrid = () => {
    const { t } = useTranslation();
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
            />
        },
    ], [columns])

    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    return <GridPage>
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
                filter={springFilter}
                // perspectives={perspectives}
                sortModel={sortModel}
                // rowAdditionalActions={actions}
                paginationActive
                readOnly
            />
            </Load>
        </CardPage>
    </GridPage>
}

export default TasquesGrid;