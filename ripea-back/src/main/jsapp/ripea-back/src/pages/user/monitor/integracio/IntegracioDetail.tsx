import {useTranslation} from "react-i18next";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useState} from "react";
import Load from "../../../../components/Load.tsx";
import {ContenidoData, DetailCard} from "../../../../components/CardData.tsx";
import {formatDate} from "../../../../util/dateUtils.ts";
import {FieldData, MuiDetail} from "../../../../components/MuiDetail.tsx";
import {StyledEstat} from "./IntegracioGrid.tsx";
import {Grid2 as Grid} from "@mui/material";
import {ErrorArea} from "../../../../components/ErrorPage.tsx";

const IntegracioDetail = ({entity, fields}:any) => {
    return <MuiDetail entity={entity} fields={fields}>
        <DetailCard>
            <FieldData field={'data'}>{formatDate(entity?.data)}</FieldData>
            <FieldData field={'descripcio'}/>
            <FieldData field={'tipus'}/>
            <FieldData field={'endpoint'}/>
            <FieldData field={'estat'} renderCell={(formattedValue:string) =>
                <StyledEstat entity={entity}>{formattedValue}</StyledEstat>}/>
            <FieldData field={'tempsResposta'} renderCell={(formattedValue:string) => `${formattedValue} ms`}/>
            <FieldData field={'parametres'} isObject>
                <Load value={entity?.parametres} noEffect>
                    {Object.entries(entity?.parametres ?? {})?.map?.(([key, value]) =>
                        <ContenidoData title={key}>{value}</ContenidoData>
                    )}
                </Load>
            </FieldData>
            <FieldData field={'errorDescripcio'} hidden={!entity?.errorDescripcio}/>
            <FieldData field={'excepcioMessage'} hidden={!entity?.excepcioMessage}/>
        </DetailCard>

        <Grid size={12} hidden={!entity?.excepcioStacktrace}>
            <ErrorArea>
                {entity?.excepcioStacktrace}
            </ErrorArea>
        </Grid>
    </MuiDetail>
}

// const perspectives:any[] = ['USUARIS']
export const useIntegracioDetail = () => {
    const { t } = useTranslation();

    const {
        isReady: apiIsReady,
        getOne: apiGetOne,
        currentFields
    } = useResourceApiService('integracioResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (id:any) => {
        if(apiIsReady){
            apiGetOne(id)
                .then((app) => setEntity(app))
                .catch((error) => {
                    handleClose()
                    temporalMessageShow(null, error?.message, 'error');
                });
            setOpen(true);
        }
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
            icon: 'close',
        },
    ]

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.integracio.action.detail.title')}
            componentProps={{ fullWidth: true, maxWidth: 'md' }}
            buttons={buttons}
            buttonCallback={() => {
                handleClose();
            }}
        >
            <Load value={entity}>
                <IntegracioDetail entity={entity} fields={currentFields}/>
            </Load>
        </MuiDialog>

    return {
        apiIsReady,
        handleOpen,
        handleClose,
        dialog
    }
}