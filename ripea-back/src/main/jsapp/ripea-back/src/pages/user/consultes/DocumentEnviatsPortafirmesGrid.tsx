import {useTranslation} from "react-i18next";
import {GridPage, MuiDialog} from "reactlib";
import {useState} from "react";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import StyledMuiFilter from "../../../components/StyledMuiFilter.tsx";
import * as builder from "../../../util/springFilterUtils.ts";
import GridFormField from "../../../components/GridFormField.tsx";
import {Link as RouterLink} from "react-router-dom";
import {Link} from "@mui/material";
import {SeguimentPortafirmes} from "../../contingut/actions/SeguimentPortafirmes.tsx";

// Detail
const useDetail = () => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id:any, row:any) => {
        console.log(id, row)
        setEntity(row)
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setEntity(undefined);
            setOpen(false);
        }
    };

    const buttons :any[] = [
        {
            value: 'close',
            text: t('common.close'),
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.document.action.seguiment.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={buttons}
            buttonCallback={() => {
                handleClose();
            }}
        >
            <SeguimentPortafirmes entity={entity}/>
        </MuiDialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

// Filter
const DocumentEnviatsPortafirmesFilterForm = () => {
    return <>
        <GridFormField xs={4} name="expedient"/>
        <GridFormField xs={4} name="document"/>
        <GridFormField xs={4} name="estat"/>
        <GridFormField xs={4} name="dataEnviamentInici" type={"date"}/>
        <GridFormField xs={4} name="dataEnviamentFi" type={"date"}/>
    </>
}

const springFilterBuilder = (data:any) => {
    return builder.and(
        builder.like('expedient.nom', data?.expedient),
        builder.like('document.nom', data?.document),
        builder.eq('estat', `'${data?.estat}'`),
        builder.betweenDates("enviatData", data?.dataEnviamentInici, data?.dataEnviamentFi),
    );
}

const DocumentEnviatsPortafirmesFilter = (props: any) => {
    const {onSpringFilterChange} = props;

    return <StyledMuiFilter
        resourceName={"documentPortafirmesResource"}
        code={"FILTER_ENVIATS_PORTAFIRMA"}
        springFilterBuilder={springFilterBuilder}
        onSpringFilterChange={onSpringFilterChange}
    >
        <DocumentEnviatsPortafirmesFilterForm/>
    </StyledMuiFilter>
}

// Grid
const sortModel: any = [{field: 'dataEnviamentInici', sort: 'desc'}]
const columns = [
    {
        field: 'expedient',
        flex: 1,
        renderCell: (params:any) => <Link component={RouterLink} to={`/contingut/${params?.row?.expedient?.id}`}>{params?.formattedValue}</Link>,
    },
    {
        field: 'document',
        flex: 1,
    },
    {
        field: 'assumpte',
        flex: 1.5,
    },
    {
        field: 'estat',
        flex: 0.5,
    },
    {
        field: 'enviatData',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value)
    },
    {
        field: 'processatData',
        flex: 0.75,
        valueFormatter: (value: any) => formatDate(value)
    },
]

const DocumentEnviatsPortafirmesGrid = () => {
    const {t} = useTranslation();
    const [springFilter, setSpringFilter] = useState<string>();

    const { handleOpen, dialog } = useDetail();

    const actions = [
        {
            label: t('common.detail'),
            icon: "info",
            showInMenu: false,
            onClick: handleOpen,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.portafib')}>
            <DocumentEnviatsPortafirmesFilter onSpringFilterChange={setSpringFilter}/>

            <StyledMuiGrid
                resourceName={"documentPortafirmesResource"}
                columns={columns}
                filter={springFilter}
                sortModel={sortModel}
                rowAdditionalActions={actions}
                readOnly
            />
            {dialog}
        </CardPage>
    </GridPage>
}
export default DocumentEnviatsPortafirmesGrid;