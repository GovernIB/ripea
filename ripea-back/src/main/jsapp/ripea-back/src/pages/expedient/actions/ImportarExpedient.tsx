import {useState} from "react";
import {MuiDialog, useFormContext} from "reactlib";
import {useTranslation} from "react-i18next";
import Load from "../../../components/Load.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import GridFormField from "../../../components/GridFormField.tsx";
import {StyledEstat} from "../ExpedientGrid.tsx";
import {useActions} from "../details/CommonActions.tsx";
import {Grid} from "@mui/material";
import {springFilterBuilder as expedientFilterBuilder} from "../ExpedientFilter.tsx";
import {formatDate} from "../../../util/dateUtils.ts";

const sortModel:any = [{ field: 'createdDate', sort: 'desc' }];
const perspectives = ['ESTAT']

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

const ImportarExpedient = (props:any) => {
    const { entity, refresh } = props
    const { t } = useTranslation()
    const [springFilter, setSpringFilter] = useState<string>();
    const [load, setLoad] = useState<boolean>(false);

    const {importarExpedient} = useActions(refresh)

    const actions = [
        {
            title: t('page.expedient.action.importar.label'),
            icon: "download",
            showInMenu: false,
            onClick: (id:any) => importarExpedient(entity?.id, id)
        }
    ]

    return <>
        <ActionFilter onSpringFilterChange={(value:any)=>{
            setSpringFilter(value)
            setLoad(true)
        }}/>
        <Load value={load} noEffect>
            <StyledMuiGrid
                resourceName={'expedientResource'}
                columns={columns}
                filter={builder.and(
                    builder.exists(
                        builder.eq('relacionatsPer.id', entity?.id),
                    ),
                    springFilter
                )}
                sortModel={sortModel}
                perspectives={perspectives}
                rowAdditionalActions={actions}

                // height={162 + 52 * 4}
                // paginationActive
                autoHeight
                readOnly
            />
        </Load>
    </>
}

const useImportarExpedient = (entity:any, refresh?: () => void) => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);

    const handleOpen = () => {
        setOpen(true);
    }

    const handleClose = () => {
        setOpen(false);
    };

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={entity?.nom}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={[
                {
                    value: 'close',
                    text: t('common.close')
                },
            ]}
            buttonCallback={(value :any) :void=>{
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <Load value={entity}>
                <ImportarExpedient entity={entity} refresh={refresh}/>
            </Load>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}
export default useImportarExpedient;