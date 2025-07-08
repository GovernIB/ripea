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
import {springFilterBuilder as expedientFilterBuilder} from "../ExpedientFilter.tsx";
import {Grid} from "@mui/material";

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
        renderCell: (params: any) => <StyledEstat entity={params?.row} icon={"folder"}>{params.formattedValue}</StyledEstat>
    },
    {
        field: 'createdDate',
        flex: 0.5,
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

    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={3} name="metaExpedient" filter={filterMetaExpedient}/>
        <GridFormField xs={3} name="numero"/>
        <GridFormField xs={3} name="nom"/>
        <GridFormField xs={3} name="estat" requestParams={{metaExpedientId: data?.metaExpedient?.id}}/>
    </Grid>
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
        return data?.relacionatsAmb?.map((a:any) => a.id)
    }, [])

    const [springFilter, setSpringFilter] = useState<string>();
    const [selectedRows, setSelectedRows] = useState<any[]>(selectionModel || []);

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
                builder.not(
                    builder.exists(
                        builder.eq('relacionatsAmb.id', apiRef?.current?.getId())
                    )
                ),
                springFilter
            )}
            sortModel={sortModel}
            perspectives={perspectives}

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
                error?.message && temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        handleShow,
        content: <Relacionar apiRef={formApiRef}/>
    }
}
export default useRelacionar;