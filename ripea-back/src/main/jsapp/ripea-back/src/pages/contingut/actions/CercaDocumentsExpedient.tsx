import {useState} from "react";
import {MuiDialog, useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {Box, CircularProgress, Icon, IconButton, InputAdornment, List, ListItemButton, TextField, Typography} from "@mui/material";
import ContingutIcon from "../details/ContingutIcon.tsx";

const CercaDocumentsExpedientContent = (props: any) => {
    const {expedientId, onSelect} = props;
    const {t} = useTranslation();
    const {artifactAction: apiAction} = useResourceApiService('expedientResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [text, setText] = useState('');
    const [loading, setLoading] = useState(false);
    const [searched, setSearched] = useState(false);
    const [results, setResults] = useState<any[]>([]);

    const handleSearch = () => {
        if (!text || expedientId == null) {
            return;
        }
        setLoading(true);
        apiAction(expedientId, {code: 'CERCA_DOCUMENTS', data: {text, page: 0, pageSize: 50}})
            .then((result: any) => setResults(result?.contingut ?? []))
            .catch((error: any) => {
                setResults([]);
                temporalMessageShow(null, error?.message, 'error');
            })
            .finally(() => {
                setSearched(true);
                setLoading(false);
            });
    };

    return <Box>
        <TextField
            fullWidth
            autoFocus
            variant={"outlined"}
            size={"small"}
            sx={{mt: 2}}
            label={t('page.contingut.action.cercaDocuments.text')}
            value={text}
            onChange={(e: any) => setText(e.target.value)}
            onKeyDown={(e: any) => {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    handleSearch();
                }
            }}
            slotProps={{
                input: {
                    endAdornment: <InputAdornment position={"end"}>
                        <IconButton title={t('page.contingut.action.cercaDocuments.search')} onClick={handleSearch} disabled={!text || loading}>
                            <Icon>search</Icon>
                        </IconButton>
                    </InputAdornment>,
                }
            }}
        />
        <Box sx={{mt: 2, minHeight: '4em'}}>
            {loading &&
                <Box sx={{display: 'flex', justifyContent: 'center', py: 2}}>
                    <CircularProgress size={28}/>
                </Box>
            }
            {!loading && searched && results.length === 0 &&
                <Typography color={"text.secondary"} sx={{px: 1}}>
                    {t('page.contingut.action.cercaDocuments.empty')}
                </Typography>
            }
            {!loading && results.length > 0 &&
                <List disablePadding sx={{maxHeight: '50vh', overflowY: 'auto'}}>
                    {results.map((document: any) => (
                        <ListItemButton
                            key={document.id}
                            divider
                            onClick={() => onSelect(document.id)}
                        >
                            <ContingutIcon entity={document}/>
                        </ListItemButton>
                    ))}
                </List>
            }
        </Box>
    </Box>
}

export const useCercaDocumentsExpedient = (entity: any, onOpenDocument: (id: any) => void) => {
    const {t} = useTranslation();
    const [open, setOpen] = useState(false);

    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);

    const dialog =
        <MuiDialog
            open={open}
            closeCallback={handleClose}
            title={t('page.contingut.action.cercaDocuments.title')}
            componentProps={{fullWidth: true, maxWidth: 'lg'}}
            buttons={[{text: t('common.close'), componentProps: {variant: 'outlined'}, value: false}]}
            buttonCallback={() => handleClose()}
        >
            <CercaDocumentsExpedientContent
                expedientId={entity?.id}
                onSelect={(id: any) => onOpenDocument(id)}
            />
        </MuiDialog>

    return {open, handleOpen, handleClose, dialog};
}

export default useCercaDocumentsExpedient;
