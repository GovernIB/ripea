import {useEffect, useRef, useState} from "react";
import {useBaseAppContext, useResourceApiService} from "reactlib";
import {useTranslation} from "react-i18next";
import {Icon, IconButton, InputAdornment, TextField, Tooltip} from "@mui/material";

const MIN_CARACTERS = 3;
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

    const textCerca = text.trim();
    const textValid = textCerca.replace(/\*/g, '').length >= MIN_CARACTERS;

    const netejar = () => {
        peticioActual.current++;
        setText('');
        setCercant(false);
        onChange(null);
    };

    const cercar = () => {
        if (!textValid || expedientId == null) {
            return;
        }
        const peticio = ++peticioActual.current;
        setCercant(true);
        apiAction(expedientId, {code: 'CERCA_DOCUMENTS', data: {text: textCerca, page: 0, pageSize: MAX_RESULTATS}})
            .then((result: any) => {
                if (peticio !== peticioActual.current) return;
                const documents: any[] = result?.contingut ?? [];
                setCercant(false);
                onChange(new Set(documents.map((document: any) => String(document?.id))), textCerca.replace(/\*/g, ''));
            })
            .catch((error: any) => {
                if (peticio !== peticioActual.current) return;
                setCercant(false);
                onChange(null);
                temporalMessageShow(null, error?.message, 'error');
            });
    };

    useEffect(() => {
        netejar();
    }, [expedientId]);

    return <TextField
        size={"small"}
        variant={"outlined"}
        sx={{width: 320}}
        placeholder={t('page.contingut.action.cercaDocuments.text')}
        value={text}
        onChange={(e: any) => {
            setText(e.target.value);
            if (!e.target.value) {
                netejar();
            }
        }}
        onKeyDown={(e: any) => {
            if (e.key === 'Enter') {
                e.preventDefault();
                cercar();
            } else if (e.key === 'Escape') {
                netejar();
            }
        }}
        slotProps={{
            htmlInput: {'aria-label': t('page.contingut.action.cercaDocuments.label')},
            input: {
                startAdornment: <InputAdornment position={"start"}>
                    <Tooltip title={<span style={{whiteSpace: 'pre-line'}}>{t('page.contingut.action.cercaDocuments.ajuda')}</span>}>
                        <Icon fontSize={"small"} color={"action"} sx={{cursor: 'help'}}>help_outline</Icon>
                    </Tooltip>
                </InputAdornment>,
                endAdornment: <InputAdornment position={"end"}>
                    {text &&
                        <IconButton size={"small"} title={t('page.contingut.action.cercaDocuments.clear')} onClick={netejar}>
                            <Icon fontSize={"small"}>close</Icon>
                        </IconButton>}
                    <IconButton size={"small"} title={t('page.contingut.action.cercaDocuments.search')} onClick={cercar} disabled={!textValid}>
                        <Icon fontSize={"small"}>search</Icon>
                    </IconButton>
                </InputAdornment>,
            }
        }}
    />
}

export default CercaDocumentsExpedient;
