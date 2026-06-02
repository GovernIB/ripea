import {CardPage} from "../../components/CardData.tsx";
import StyledMuiGrid from "../../components/StyledMuiGrid.tsx";
import {GridPage, useMuiDataGridApiRef} from "reactlib";
import {useTranslation} from "react-i18next";
import {Grid, Icon} from "@mui/material";
import {formatDate} from "../../util/dateUtils.ts";
import {useAvisActions} from "./details/AvisActions.tsx";
import GridFormField from "../../components/GridFormField.tsx";

const AvisGridForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name={'assumpte'}/>
        <GridFormField name={'missatge'} type={'textarea'}/>
        <GridFormField name={'dataInici'} type={'date'}/>
        <GridFormField name={'dataFinal'} type={'date'}/>
        <GridFormField name={'avisNivell'} required/>
    </Grid>
}

const columns = [
    {
        field: 'assumpte',
        flex: 2,
    },
    {
        field: 'dataInici',
        flex: 0.75,
        valueFormatter: (value: string) => formatDate(value, 'DD/MM/Y'),
    },
    {
        field: 'dataFinal',
        flex: 0.75,
        valueFormatter: (value: string) => formatDate(value, 'DD/MM/Y'),
    },
    {
        field: 'actiu',
        flex: 0.5,
        renderCell: (params: any) => params?.row?.actiu && <Icon>check</Icon>,
    },
    {
        field: 'avisNivell',
        flex: 0.5,
    },
];
const perspectives:any[] = [];
const sortModel:any[] = [{field: 'dataInici', sort: 'asc'}];
export const AvisGrid = () => {
    const {t} = useTranslation()
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const {actions, massiveActions, components} = useAvisActions(refresh)

    return <GridPage>
        <CardPage title={t('navigate.avis')}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"avisResource"}
                popupEditFormDialogResourceTitle={t('page.avis.title')}
                columns={columns}
                sortModel={sortModel}
                perspectives={perspectives}
                popupEditCreateActive
                popupEditFormContent={<AvisGridForm/>}
                rowAdditionalActions={actions}
                toolbarMassiveActions={massiveActions}
                toolbarShowQuickFilter
                toolbarCreateTitle={t('page.avis.action.new.label')}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.avis.action.new.ok',
                    updateSuccess: 'page.avis.action.update.ok',
                    deleteSuccess: 'page.avis.action.delete.ok',
                }}
            />
            {components}
        </CardPage>
    </GridPage>
}