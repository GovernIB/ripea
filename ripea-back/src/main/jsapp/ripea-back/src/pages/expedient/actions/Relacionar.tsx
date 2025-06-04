import {useEffect, useMemo, useRef, useState} from "react";
import {
    useBaseAppContext, MuiFormDialog, useFormContext, MuiFormDialogApi
} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import {StyledEstat} from "../ExpedientGrid.tsx";
import Load from "../../../components/Load.tsx";

const sortModel:any = [{ field: 'createdDate', sort: 'desc' }];
const perspectives = ["ESTAT"];

const columns = [
    {
        field: 'metaExpedient',
        flex: 0.5,
    },
    {
        field: 'numero',
        flex: 0.5,
    },
    {
        field: 'nom',
        flex: 0.5,
    },
    {
        field: 'estat',
        flex: 0.5,
        renderCell: (params: any) => <StyledEstat entity={params?.row} icon={"folder"}/>
    },
    {
        field: 'createdDate',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const springFilterBuilder = (data: any) :string => {
    const filterStr = builder.and(
        builder.eq('metaExpedient.id', data?.metaExpedient?.id),
        builder.like('numero', data?.numero),
        builder.like('nom', data?.nom),
        data.estat && builder.equals("estat",`'TANCAT'`, (data.estat==='TANCAT')),
    );
    // console.log('>>> springFilterBuilder:', filterStr)
    return filterStr;
}

const ActionFilter = (props:any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName="expedientResource"
        code="EXPEDIENT_FILTER"
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <GridFormField xs={3} name="metaExpedient"/>
        <GridFormField xs={3} name="numero"/>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="estat"/>
    </StyledMuiFilter>
}

const RelacionarForm= () => {
    const {data, apiRef} = useFormContext();
    const [springFilter, setSpringFilter] = useState<string>();
    const [selectedRows, setSelectedRows] = useState<any[]>([]);

    const selectionModel = useMemo(()=>{
        return data?.relacionatsAmb?.map((a:any) => a.id)
    }, [])

    useEffect(() => {
        apiRef?.current?.setFieldValue("relacionatsAmb", selectedRows?.map(id => ({ id })))
    }, [selectedRows]);

    return <Load value={selectionModel} noEffect>
        <ActionFilter onSpringFilterChange={setSpringFilter}/>
        <StyledMuiGrid
            resourceName={'expedientResource'}
            columns={columns}
            filter={builder.and(
                builder.neq('id', apiRef?.current?.getId()),
                // builder.exists(
                //     builder.neq('relacionatsAmb.id', 1)
                // ),
                springFilter
            )}
            sortModel={sortModel}
            perspectives={perspectives}

            selectionActive
            rowSelectionModel={selectionModel}

            onRowSelectionModelChange={(newSelection) => {
                setSelectedRows([...newSelection]);
            }}

            // height={162 + 52 * 4}
            // paginationActive
            autoHeight
            readOnly
        />
    </Load>
}

const Relacionar = (props:any) => {
    const { t } = useTranslation();

    return <MuiFormDialog
        resourceName={'expedientResource'}
        title={t('page.expedient.action.relacio.title')}
        onClose={(reason?: string) => reason !== 'backdropClick'}
        {...props}
    >
        <RelacionarForm/>
    </MuiFormDialog>
}

const useRelacionar= (refresh?: () => void) => {
    const { t } = useTranslation();
    const formApiRef = useRef<MuiFormDialogApi>()
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        formApiRef.current?.show?.(id,{ relacionatsAmb: row?.relacionatsAmb })
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.expedient.action.relacio.ok', {expedient: row?.nom}), 'success');
            })
            .catch((error:any) :void => {
                if (error) {
                    temporalMessageShow(null, error.message, 'error');
                }
            });
    }

    return {
        handleShow,
        content: <Relacionar apiRef={formApiRef}/>
    }
}
export default useRelacionar;