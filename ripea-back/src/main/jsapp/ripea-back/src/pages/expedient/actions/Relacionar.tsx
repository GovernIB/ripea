import {useEffect, useMemo, useState} from "react";
import {
    useBaseAppContext, useFormContext, useMuiFormDialogApiRef
} from "reactlib";
import {useTranslation} from "react-i18next";
import GridFormField from "../../../components/GridFormField.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import * as builder from "../../../util/springFilterUtils.ts";
import {StyledEstat} from "../ExpedientGrid.tsx";
import Load from "../../../components/Load.tsx";
import {springFilterBuilder as expedientFilterBuilder} from "../ExpedientFilter.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";
import {GridSortDirection} from "@mui/x-data-grid-pro";

const sortModel:any = [{ field: 'createdDate', sort: 'desc' }];
const perspectives = ["ESTAT"];

const columns = [
    {
        field: 'metaExpedient',
        flex: 1.6,
    },
    {
        field: 'numero',
        flex: 0.9,
    },
    {
        field: 'nom',
        flex: 1.5,
    },
    {
        field: 'estat',
        flex: 0.7,
        renderCell: (params: any) => <StyledEstat entity={params?.row}>{params.formattedValue}</StyledEstat>,
        sortProcessor: (field: string, sort: GridSortDirection) => {
            return [
                { field: "estatAdditional", sort },
                { field: field, sort },
                { field: "id", sort }
            ]
        }
    },
    {
        field: 'createdDate',
        flex: 0.6,
        valueFormatter: (value: any) => formatDate(value),
    },
]

const springFilterBuilder = (data: any) :string => {
    const processedData = {
        metaExpedient: data?.metaExpedient,
        numero: data?.numero,
        nom: data?.nom,
        estat: data?.estat,
    }
    return expedientFilterBuilder(processedData);
}

const ActionFilterFrom = () => {
    const {data} = useFormContext()

    const filterMetaExpedient = builder.and(
        builder.eq('actiu', true),
        builder.eq('revisioEstat', "'REVISAT'"),
    );

    return <>
        <GridFormField size={{xs: 12, sm: 6, md: 3}} name="metaExpedient" filter={filterMetaExpedient}/>
        <GridFormField size={{xs: 12, sm: 6, md: 2}} name="numero"/>
        <GridFormField size={{xs: 12, sm: 6, md: 2}} name="nom"/>
        <GridFormField size={{xs: 12, sm: 6, md: 2}} name="estat" requestParams={{metaExpedientId: data?.metaExpedient?.id}}/>
    </>
}

const ActionFilter = (props:any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName="expedientResource"
        code="EXPEDIENT_FILTER"
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <ActionFilterFrom/>
    </StyledMuiFilter>
}

const RelacionarForm= () => {
    const {data, apiRef} = useFormContext();
    const selectionModel = useMemo(()=>{
        return data?.ids
    }, [data])

    const [springFilter, setSpringFilter] = useState<string>();
    const [selectedRows, setSelectedRows] = useState<any[]>(selectionModel || []);
    const [load, setLoad] = useState<boolean>(false);

    useEffect(() => {
        apiRef?.current?.setFieldValue("ids", selectedRows)
    }, [selectedRows]);

    return <Load value={selectionModel} noEffect>
        <ActionFilter onSpringFilterChange={(value:any)=>{
            setSpringFilter(value)
            setLoad(true)
        }}/>
        <Load value={load} noEffect>
        <StyledMuiGrid
            resourceName={'expedientResource'}
            columns={columns}
            filter={builder.and(
                builder.neq('id', apiRef?.current?.getId()),
                builder.not(
                    builder.exists(
                        builder.eq('relacionatsAmb.id', apiRef?.current?.getId())
                    )
                ),
                springFilter
            )}
            sortModel={sortModel}
            perspectives={perspectives}
            toolbarHide
            selectionActive
            rowSelectionModel={selectionModel}

            onRowSelectionModelChange={(newSelection) => {
                setSelectedRows([...newSelection]);
            }}

            rowProps={(row: any) => {
                const color = row?.estatAdditionalInfo?.color;
                return color
                    ? {
                        'box-shadow': `${color} -6px 0px 0px`,
                        'border-left': `6px solid ${color}`,
                    }
                    : {
                        'padding-left': '6px'
                    }
            }}

            autoHeight
            paginationModel={{page: 0, pageSize: 10}}
            readOnly
        />
        </Load>
    </Load>
}

const Relacionar = (props:any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={'expedientResource'}
        action={'RELACIONAR'}
        title={t('page.expedient.action.relacio.title')}
        formDialogComponentProps={{fullWidth: true, maxWidth: 'xl'}}
        formDialogButtons={[
            {icon: 'link', text: t('page.expedient.action.relacio.button'), componentProps: { variant: 'contained' }, value: true },
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
        ]}
        {...props}
    >
        <RelacionarForm/>
    </FormActionDialog>
}

const useRelacionar= (refresh?: () => void) => {
    const { t } = useTranslation();
    const formApiRef = useMuiFormDialogApiRef()
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (id:any, row:any) :void => {
        formApiRef.current?.show?.(id,{ ids: row?.relacionatsAmb?.map((a:any) => a.id), action: 'ADD' })
    }
    const onSuccess = (result: any): void => {
        refresh?.()
        temporalMessageShow(null, t('page.expedient.action.relacio.ok', {expedient: result?.nom}), 'success');
    }

    return {
        handleShow,
        content: <Relacionar apiRef={formApiRef} onSuccess={onSuccess}/>
    }
}
export default useRelacionar;