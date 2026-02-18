import {useRef, useState} from "react";
import {Grid} from "@mui/material";
import {useBaseAppContext, MuiFormDialogApi } from "reactlib";
import {useTranslation} from "react-i18next";
import {CardData} from "../../../components/CardData.tsx";
import FormActionDialog from "../../../components/FormActionDialog.tsx";

const ActualitzarResultProcessor = (props:any) => {
    const {entity} = props;
    const { t } = useTranslation();

    return <Grid container sx={{display: "flex", flexDirection: "row", wordWrap: "break-word"}} columnSpacing={1} rowSpacing={1}>
        <CardData title={t('page.metaExpedient.action.actualize.result.title')} variant={"h6"}>
            <Grid item xs={12}>
                {t('page.metaExpedient.action.actualize.result.description', {
                    numOperacions: entity?.numOperacions,
                    numActualitzats: entity?.numElementsActualitzats,
                    numErrord: entity?.info?.filter((i:any)=>i?.hasError)?.length
                })}
            </Grid>
        </CardData>

        {entity?.info?.map((i:any) => <>
            <CardData
                title={`${t('page.metaExpedient.title')}: ${i?.codiSia} - ${i?.nomAntic}`}
                cardProps={{border: '1px solid #ffcdd2'}}
                headerProps={{ backgroundColor: '#ffcdd2', color: 'error.main' }}
                hidden={!i?.codiSia || !i?.hasError}
                variant={"h6"}
            >
                <Grid item xs={12}>{i?.errorText}</Grid>
            </CardData>
            <CardData
                title={`Procediment: ${i?.codiSia} - ${i?.nomAntic}`}
                cardProps={{border: '1px solid #e3f2fd'}}
                headerProps={{ backgroundColor: '#e3f2fd', color: 'info.main' }}
                hidden={!i?.codiSia || !i?.hasCanvis}
                variant={"h6"}
            >
                <Grid item xs={12}>{i?.descripcioNova}</Grid>
            </CardData>
            <CardData
                title={`Procediment: ${i?.codiSia} - ${i?.nomAntic}`}
                hidden={!i?.codiSia || i?.hasCanvis || i?.hasError}
                variant={"h6"}
            >
                <Grid item xs={12}>{t('page.metaExpedient.action.actualize.result.senseCanvi')}</Grid>
            </CardData>
        </>)}
    </Grid>
}

export const Actualitzar = (props: any) => {
    const { t } = useTranslation();

    return <FormActionDialog
        resourceName={"metaExpedientResource"}
        title={t('page.metaExpedient.action.actualize.title')}
        action={"UPDATE_ROLSAC"}
        formDialogButtons={[
            {text: t('common.cancel'), componentProps: { variant: 'outlined' }, value: false },
            {icon: 'save', text: t('common.actualize'), componentProps: { variant: 'contained', disabled: props?.disabled }, value: true },
        ]}
        {...props}
    >
        {t('page.metaExpedient.action.actualize.description')}
    </FormActionDialog>
}

export const useActualitzar = (refresh?: () => void) => {
    const { t } = useTranslation();
    const apiRef = useRef<MuiFormDialogApi>();
    const [disabled, setDisabled] = useState<boolean>(false);
    const {temporalMessageShow} = useBaseAppContext();

    const handleShow = (ids:any[]) :void => {
        setDisabled(false)
        apiRef.current?.show?.(undefined, {ids, massivo: true})
    }
    const onSuccess = () :void => {
        refresh?.()
        temporalMessageShow(null, t('page.metaExpedient.action.actualize.ok'), 'success');
    }

    return {
        handleShow,
        content: <Actualitzar
            apiRef={apiRef}
            onSuccess={onSuccess}
            disabled={disabled}
            formDialogResultProcessor={((response:any) => {
                setDisabled(true)
                return <ActualitzarResultProcessor entity={response}/>
            })}
        />
    }
}
export default useActualitzar;