import {
    BasePage,
} from 'reactlib';
import {Typography} from "@mui/material";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";

const AnotacionsDetalle = (props:any) => {
    const { t } = useTranslation();
    const { entity } = props;

    const tabs = [
        {
            value: "resum",
            label: t('page.anotacio.tabs.resum'),
            content: <Typography>{t('page.anotacio.tabs.resum')}</Typography>,
        },
        {
            value: "estat",
            label: t('page.anotacio.tabs.estat'),
            content: <><Typography>{t('page.anotacio.tabs.estat')}</Typography></>,
        },
        {
            value: "registre",
            label: t('page.anotacio.tabs.registre'),
            content: <Typography>{t('page.anotacio.tabs.registre')}</Typography>,
        },
        {
            value: "interessats",
            label: t('page.anotacio.tabs.interessats'),
            content: <Typography>{t('page.anotacio.tabs.interessats')}</Typography>,
        },
        {
            value: "annexos",
            label: t('page.anotacio.tabs.annexos'),
            content: <Typography>{t('page.anotacio.tabs.annexos')}</Typography>,
        },
    ]
    return <BasePage>
        <TabComponent
            indicatorColor={"primary"}
            textColor={"primary"}
            aria-label="scrollable force tabs"
            tabs={tabs}
            variant="scrollable"
        />
    </BasePage>
}

export default AnotacionsDetalle;