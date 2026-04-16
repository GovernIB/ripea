import {useTranslation} from "react-i18next";
import {GridPage, useBaseAppContext, useMuiDataGridApiRef, useResourceApiService} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import StyledMuiGrid, {ToolbarButton} from "../../../components/StyledMuiGrid.tsx";
import { Grid2 as Grid } from "@mui/material";
import GridFormField, {PasswordFormField} from "../../../components/GridFormField.tsx";

const useActions = (refresh?: () => void) => {
    const { t } = useTranslation();

    const {
        artifactAction: apiAction,
    } = useResourceApiService('dominiResource');
    const {temporalMessageShow} = useBaseAppContext();

    const cleanCache = () => {
        apiAction(undefined, {code: 'EMPTY_CACHE'})
            .then(() => {
                refresh?.()
                temporalMessageShow(null, t('page.domini.action.cleanCache.ok'), 'success');
            })
            .catch((error) => {
                temporalMessageShow(null, error?.message, 'error');
            });
    }

    return {
        cleanCache,
    }
}

// Grid
const DominiForm = () => {
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField name="codi"/>
        <GridFormField name="nom"/>
        <GridFormField name="descripcio" type={"textarea"}/>
        <GridFormField name="consulta" type={"textarea"}
                       componentProps={{ InputLabelProps: { shrink: true }, placeholder: "SELECT field_id AS ID, field_valor AS VALOR FROM tables" }}/>
        <GridFormField name="cadena" type={"textarea"}
                       componentProps={{ InputLabelProps: { shrink: true }, placeholder: "" +
                               "<local-tx-datasource>\n" +
                               "  <connection-url>jdbc:oracle:thin:@localhost:1521/orcl</connection-url>\n" +
                               "  <driver-class>oracle.jdbc.driver.OracleDriver</driver-class>\n" +
                               "  <user-name>usuari</user-name>\n" +
                               "</local-tx-datasource>"
                       }}/>
        <PasswordFormField name="contrasenya" />
    </Grid>
}

const sortModel: any[] = [{field: 'codi', sort: 'asc'}]
// const perspectives = [""];
const columns = [
    {
        field: 'codi',
        flex: 0.5,
    },
    {
        field: 'nom',
        flex: 1,
    },
    {
        field: 'descripcio',
        flex: 1,
    },
]

const DominiGrid = () => {
    const {t} = useTranslation();
    const apiRef = useMuiDataGridApiRef();

    const refresh = () => {
        apiRef?.current?.refresh?.();
    }

    const { cleanCache } = useActions(refresh)
    const actions = [
        {
            label: t('common.update'),
            icon: "edit",
            showInMenu: true,
            clickShowUpdateDialog: true,
        },
        {
            label: t('common.delete'),
            icon: "delete",
            showInMenu: true,
            clickTriggerDelete: true,
        },
    ]

    return <GridPage disableMargins>
        <CardPage title={t('page.user.menu.dominis')}>
            <StyledMuiGrid
                apiRef={apiRef}
                resourceName={"dominiResource"}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.domini.title')}
                popupEditFormContent={<DominiForm/>}
                columns={columns}
                toolbarShowQuickFilter
                sortModel={sortModel}
                // perspectives={perspectives}
                rowAdditionalActions={actions}

                toolbarElementsWithPositions={[
                    {
                        position: 2,
                        element: <ToolbarButton
                            icon={'cached'}
                            onClick={cleanCache}
                            variant={"contained"}
                            color={'warning'}>{t('page.domini.action.cleanCache.label')}</ToolbarButton>,
                    },
                ]}

                toolbarCreateTitle={t('page.domini.action.new.label')}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.domini.action.new.ok',
                    updateSuccess: 'page.domini.action.update.ok',
                    deleteSuccess: 'page.domini.action.delete.ok',
                }}
            />
        </CardPage>
    </GridPage>
}
export default DominiGrid;