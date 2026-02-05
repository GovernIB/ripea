import {GridPage} from "reactlib";
import {CardPage} from "../../../components/CardData.tsx";
import {useTranslation} from "react-i18next";
import StyledMuiGrid from "../../../components/StyledMuiGrid.tsx";
import {Grid} from "@mui/material";
import GridFormField from "../../../components/GridFormField.tsx";

const UrlInstruccioGridForm = () => {
    const {t} = useTranslation()
    return <Grid container direction={"row"} columnSpacing={1} rowSpacing={1}>
        <GridFormField xs={12} name="codi"/>
        <GridFormField xs={12} name="nom"/>
        <GridFormField xs={12} name="descripcio"/>
        <GridFormField xs={12} name="url" type={"textarea"}
                       componentProps={{
                           InputLabelProps: { shrink: true },
                           placeholder: t('page.urlInstruccio.detall.url')
                       }}/>
    </Grid>
}

const sortModel: any = [{field: 'nom', sort: 'asc'}]
const perspectives = [""];
const columns = [
    {
        field: 'codi',
        flex: 0.5,
    },
    {
        field: 'nom',
        flex: 0.5,
    },
    {
        field: 'descripcio',
        flex: 1,
    },
    {
        field: 'url',
        flex: 1,
    },
]

export const UrlInstruccioGrid = () => {
    const {t} = useTranslation();

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
        <CardPage title={t('page.user.menu.url')}>
            <StyledMuiGrid
                resourceName={"uRLInstruccioResource"}
                popupEditUpdateActive
                popupEditFormDialogResourceTitle={t('page.urlInstruccio.title')}
                popupEditFormContent={<UrlInstruccioGridForm/>}
                columns={columns}
                // filter={builder.eq("metaExpedient", null)}
                sortModel={sortModel}
                perspectives={perspectives}
                rowAdditionalActions={actions}

                // popupEditFormDialogComponentProps={{ fullWidth: true, maxWidth: 'lg' }}
                toolbarCreateTitle={t('page.urlInstruccio.action.new.label')}
                popupEditFormI18nKeys={{
                    createSuccess: 'page.urlInstruccio.action.new.ok',
                    updateSuccess: 'page.urlInstruccio.action.update.ok',
                    deleteSuccess: 'page.urlInstruccio.action.delete.ok',
                }}
            />
        </CardPage>
    </GridPage>
}