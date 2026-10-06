import {useEffect, useRef, useState} from "react";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {Icon, IconButton, InputAdornment, TextField} from "@mui/material";

const MIN_CARACTERS = 3;
const ESPERA_MS = 500;
const MAX_RESULTATS = 100;

export const CercaDocumentsExpedient = (props: {
    expedientId: any,
    onChange: (ids: Set<string> | null, text?: string) => void,
    onLoading: (loading: boolean) => void,
}) => {
    const {expedientId, onChange, onLoading} = props;
    const {t} = useTranslation();
    const {artifactAction: apiAction} = useResourceApiService('expedientResource');
    const {temporalMessageShow} = useBaseAppContext();

    const [text, setText] = useState('');
    const peticioActual = useRef(0);
    const cercant = useRef(false);
    const setCercant = (value: boolean) => {
        if (cercant.current !== value) {
            cercant.current = value;
            onLoading(value);
        }
    };

    useEffect(() => {
        const textCerca = text.trim();
        const peticio = ++peticioActual.current;
        if (textCerca.length < MIN_CARACTERS || expedientId == null) {
            setCercant(false);
            onChange(null);
            return;
        }
        const timeout = setTimeout(() => {
            setCercant(true);
            apiAction(expedientId, {code: 'CERCA_DOCUMENTS', data: {text: textCerca, page: 0, pageSize: MAX_RESULTATS}})
                .then((result: any) => {
                    if (peticio !== peticioActual.current) return;
                    const documents: any[] = result?.contingut ?? [];
                    setCercant(false);
                    onChange(new Set(documents.map((document: any) => String(document?.id))), textCerca);
                })
                .catch((error: any) => {
                    if (peticio !== peticioActual.current) return;
                    setCercant(false);
                    onChange(null);
                    temporalMessageShow(null, error?.message, 'error');
                });
        }, ESPERA_MS);
        return () => clearTimeout(timeout);
    }, [text, expedientId]);

    return <TextField
        size={"small"}
        variant={"outlined"}
        sx={{width: 280}}
        placeholder={t('page.contingut.action.cercaDocuments.text')}
        value={text}
        onChange={(e: any) => setText(e.target.value)}
        onKeyDown={(e: any) => {
            if (e.key === 'Escape') {
                setText('');
            }
        }}
        slotProps={{
            htmlInput: {'aria-label': t('page.contingut.action.cercaDocuments.label')},
            input: {
                startAdornment: <InputAdornment position={"start"}>
                    <Icon fontSize={"small"}>search</Icon>
                </InputAdornment>,
                endAdornment: text
                    ? <InputAdornment position={"end"}>
                        <IconButton size={"small"} title={t('page.contingut.action.cercaDocuments.clear')} onClick={() => setText('')}>
                            <Icon fontSize={"small"}>close</Icon>
                        </IconButton>
                    </InputAdornment>
                    : undefined,
            }
        }}
    />
}

export default CercaDocumentsExpedient;
