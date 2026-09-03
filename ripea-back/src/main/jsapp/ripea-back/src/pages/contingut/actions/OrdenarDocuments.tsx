import {useState} from "react";
import {MuiDialog} from "reactlib";
import {useTranslation} from "react-i18next";
import {Box, Icon, IconButton, List, ListItem, Paper, Typography} from "@mui/material";
import {DndContext} from "@dnd-kit/core";
import {DraggableGridRowHandler, DraggableItem} from "../../../components/DraggableContext.tsx";
import {dndScreenReaderInstructions} from "../../../util/dndAccessibility.tsx";

/**
 * Diàleg per triar l'ordre en què es combinaran els documents seleccionats dins el PDF final.
 *
 * Es pot reordenar arrossegant (com a la resta de llistes ordenables de l'aplicació) i amb els
 * botons de pujar/baixar, que són els que fan la llista accessible per teclat.
 */
const moure = (documents: any[], desDe: number, finsA: number): any[] => {
    if (desDe === finsA || finsA < 0 || finsA >= documents.length) {
        return documents;
    }
    const ordenats = [...documents];
    const [doc] = ordenats.splice(desDe, 1);
    ordenats.splice(finsA, 0, doc);
    return ordenats;
}

const useOrdenarDocuments = (onConfirm: (documents: any[]) => void) => {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [documents, setDocuments] = useState<any[]>([]);

    const handleOpen = (documentsAOrdenar: any[]) => {
        setDocuments(documentsAOrdenar);
        setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    }

    const handleDragEnd = (event: any) => {
        const origen = event?.active?.data?.current;
        const desti = event?.over?.data?.current;
        if (origen?.id == null || desti?.id == null || origen.id === desti.id) {
            return;
        }
        setDocuments((actuals) => moure(
            actuals,
            actuals.findIndex((d) => d?.id === origen.id),
            actuals.findIndex((d) => d?.id === desti.id)));
    }

    const desplaca = (index: number, increment: number) => {
        setDocuments((actuals) => moure(actuals, index, index + increment));
    }

    const dialog = <MuiDialog
        open={open}
        closeCallback={handleClose}
        title={t('page.document.action.notificarMasiva.ordre.title')}
        componentProps={{ fullWidth: true, maxWidth: 'sm' }}
        buttons={[
            {
                value: 'concatenar',
                text: t('page.document.action.notificarMasiva.ordre.button'),
                icon: 'picture_as_pdf',
                componentProps: { variant: 'contained' },
            },
            {
                value: 'cancel',
                text: t('common.cancel'),
                componentProps: { variant: 'outlined' },
            },
        ]}
        buttonCallback={(value: any): void => {
            handleClose();
            if (value === 'concatenar') {
                onConfirm(documents);
            }
        }}
    >
        <Typography variant={'body2'} sx={{ mb: 1 }}>
            {t('page.document.action.notificarMasiva.ordre.description')}
        </Typography>
        <DndContext onDragEnd={handleDragEnd} accessibility={{ screenReaderInstructions: dndScreenReaderInstructions }}>
            <List component={Paper}>
                {documents.map((doc: any, index: number) => (
                    <ListItem key={doc?.id} divider={index < documents.length - 1}>
                        <DraggableItem id={doc?.id} data={doc} style={{ width: '100%' }}>
                            <Box sx={{ display: 'flex', alignItems: 'center', width: '100%' }}>
                                <Typography sx={{ minWidth: '2rem' }}>{index + 1}.</Typography>
                                <Typography sx={{ flexGrow: 1, wordBreak: 'break-all' }}>{doc?.nom}</Typography>
                                <IconButton
                                    size="small"
                                    title={t('page.document.action.notificarMasiva.ordre.pujar')}
                                    aria-label={t('page.document.action.notificarMasiva.ordre.pujar')}
                                    disabled={index === 0}
                                    onClick={() => desplaca(index, -1)}
                                >
                                    <Icon sx={{ m: 0 }}>arrow_upward</Icon>
                                </IconButton>
                                <IconButton
                                    size="small"
                                    title={t('page.document.action.notificarMasiva.ordre.baixar')}
                                    aria-label={t('page.document.action.notificarMasiva.ordre.baixar')}
                                    disabled={index === documents.length - 1}
                                    onClick={() => desplaca(index, 1)}
                                >
                                    <Icon sx={{ m: 0 }}>arrow_downward</Icon>
                                </IconButton>
                                <DraggableGridRowHandler/>
                            </Box>
                        </DraggableItem>
                    </ListItem>
                ))}
            </List>
        </DndContext>
    </MuiDialog>

    return {
        handleOpen,
        dialog,
    }
}
export default useOrdenarDocuments;
