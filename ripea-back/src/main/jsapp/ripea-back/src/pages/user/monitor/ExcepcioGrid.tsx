import {GridPage, MuiDialog, useResourceApiService} from "reactlib"
import {CardPage, DetailCard} from "../../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {formatDate} from "../../../util/dateUtils.ts";
import Load from "../../../components/Load.tsx";
import {useMemo, useState} from "react";
import {FieldData, MuiDetail} from "../../../components/MuiDetail.tsx";
import {Grid} from "@mui/material";
import {ErrorArea} from "../../../components/ErrorPage.tsx";

const ExcepcioDetail = ({entity, currentFields}:any) => {
    return <MuiDetail entity={entity} fields={currentFields}>
        <DetailCard>
            <FieldData field={'data'}>{formatDate(entity?.data)}</FieldData>
            <FieldData field={'tipus'}/>
            <FieldData field={'uri'}/>
            <FieldData field={'message'}/>
        </DetailCard>

        <Grid size={12}>
            <ErrorArea sx={{ maxHeight: '400px' }}>
                {entity?.stacktrace}
            </ErrorArea>
        </Grid>
    </MuiDetail>
}

const useExcepcioDetail = () => {
    const {t} = useTranslation()

    const {
        isReady: apiIsReady,
        currentFields
    } = useResourceApiService('excepcioLogResource');

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (_id:any, row:any) => {
        setEntity(row)
        setOpen(true);
    };

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
            icon: 'close',
            componentProps: { variant: 'outlined' }
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.exception.action.detail.title')}
            componentProps={{ fullWidth: true, maxWidth: 'lg' }}
            buttons={buttons}
            buttonCallback={() => {
                handleClose();
            }}
        >
            <Load value={entity && apiIsReady}>
                <ExcepcioDetail entity={entity} currentFields={currentFields}/>
            </Load>
        </MuiDialog>

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog
    }
}

const columns:any[] = [
    {
        field: 'data',
        flex: 0.5,
        valueFormatter: (value: any) => formatDate(value),
    },
    {
        field: 'tipus',
        flex: 0.75,
    },
    {
        field: 'uri',
        flex: 0.75,
    },
    {
        field: 'message',
        flex: 1,
    },
];
const perspectives:any[] = [];
const sortModel:any[] = [{field: 'data', sort: 'desc'}];
export const ExcepcioGrid = () => {
    const {t} = useTranslation()

    const {apiIsReady, handleOpen, dialog} = useExcepcioDetail();
    const actions = useMemo(() => [
        {
            label: t('common.detail'),
            icon: 'info',
            showInMenu: false,
            onClick: handleOpen,
        },
    ], [apiIsReady]);

    return <GridPage autoHeight>
        <CardPage title={t('navigate.exception')}>
            <StyledMuiGrid
                resourceName={"excepcioLogResource"}
                columns={columns}
                sortModel={sortModel}
                perspectives={perspectives}
                rowAdditionalActions={actions}
                toolbarShowQuickFilter
                readOnly
            />
            {dialog}
        </CardPage>
    </GridPage>
}