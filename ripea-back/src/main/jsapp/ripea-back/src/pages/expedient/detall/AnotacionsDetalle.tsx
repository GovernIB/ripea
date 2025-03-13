import {Dialog} from 'reactlib';
import {Typography} from "@mui/material";
import {useTranslation} from "react-i18next";
import TabComponent from "../../../components/TabComponent.tsx";
import {useState} from "react";

const useAnotacionsDetalle = () => {
    const { t } = useTranslation();

    const [open, setOpen] = useState(false);
    const [entity, setEntity] = useState<any>();

    const handleOpen = (row:any) => {
        setEntity(row);
        setOpen(true);
    }

    const handleClose = () => {
        setOpen(false);
    };

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

    const dialog =
        <Dialog
            open={open}
            closeCallback={handleClose}
            title={"Detalles de la anotación de registro"}
            componentProps={{ fullWidth: true, maxWidth: 'xl' }}
            buttons={[
                {
                    value: 'close',
                    text: 'Close'
                },
            ]}
            buttonCallback={(value :any) :void=>{
                console.log(value);
                if (value=='close') {
                    handleClose();
                }
            }}
        >
            <TabComponent
                indicatorColor={"primary"}
                textColor={"primary"}
                aria-label="scrollable force tabs"
                tabs={tabs}
                variant="scrollable"
            />
        </Dialog>

    return {
        handleOpen,
        handleClose,
        dialog
    }
}

export default useAnotacionsDetalle;