import * as React from 'react';
import DOMPurify from 'dompurify';
import {
    Badge,
    Grid,
    Button,
    Icon,
    IconButton,
    Typography
} from '@mui/material';
import {
    MuiForm,
    FormField,
    useResourceApiService,
    useBaseAppContext,
    useMuiContentDialog,
    FormApi
} from 'reactlib';
import { formatDate } from '../util/dateUtils';
import { useUserSession } from '../components/Session';
import {useTranslation} from "react-i18next";
import {useRef} from "react";

const Comments = (props: any) => {
    const { resourceName, id, resourceReference, readOnly, i18nKeys } = props;
    const { value: user } = useUserSession();
    const {
        isReady: apiIsReady,
        find: apiFind,
    } = useResourceApiService(resourceName);
    const [comments, setComments] = React.useState<any[]>();
    const {temporalMessageShow} = useBaseAppContext();
    const formApiRef = React.useRef<FormApi | any>({});
    const ref = useRef<HTMLDivElement | null>(null);
    const refresh = () => {
        apiFind({
            filter: `${resourceReference}.id:${id}`,
            includeLinksInRows: true,
            unpaged: true,
            sorts: ['createdDate,asc']
        }).
        then((result) => {
            setComments(result.rows);
            setTimeout(() => {
                const contentRef = ref.current?.parentElement;
                if (contentRef) {
                    contentRef.scrollTop = contentRef.scrollHeight;
                }
            }, 100);
        }).
        catch((error) => {
            if (error?.message)
                temporalMessageShow(null, error?.message, 'error');
        });
    }
    React.useEffect(() => {
        if (apiIsReady) {
            refresh();
        }
    }, [apiIsReady]);
    const handleButtonClick = () => {
        formApiRef.current?.save().then(() => {
            refresh();
            formApiRef.current?.reset();
        });
    }
    return <Grid
        container
        direction="column"
        rowGap={1}
        component={"div"}
        ref={ref}
        sx={{ justifyContent: 'center', alignItems: 'flex-start', pb: 1 }}>
        {comments?.map((a:any)=>
            <Grid key={a?.id} className={`comment ${a?.createdBy == user?.codi ? 'myComment' : 'otherComment'}`}>
                <Typography variant="subtitle2" color="textDisabled">{a?.createdBy}</Typography>
                <Typography variant="body2" dangerouslySetInnerHTML={{ __html: DOMPurify.sanitize(a?.text) }}/>
                <Typography variant="caption" color="textDisabled">{formatDate(a?.createdDate)}</Typography>
            </Grid>
        )}

        {!readOnly && user?.rolActual != "IPA_ADMIN_LECTURA" &&
            <Grid size={12}>
                <MuiForm
                    resourceName={resourceName}
                    hiddenToolbar
                    additionalData={{
                        [resourceReference]: { id },
                    }}
                    apiRef={formApiRef}
                    commonFieldComponentProps={{ size: 'small' }}
                    i18nKeys={i18nKeys}>
                    <Grid container columnSpacing={1} rowSpacing={1}>
                        <Grid size={12} sx={{ display: 'flex' }}>
                            <FormField name="text" />
                            <Button onClick={handleButtonClick} startIcon={<Icon>send</Icon>} variant="contained">Envia</Button>
                        </Grid>
                    </Grid>
                </MuiForm>
            </Grid>
        }
    </Grid>;
}

export const CommentDialog = (props: any) => {
    const { entity, title, resourceName, resourceReference, onClose, iconStyle, i18nKeys = {}, readOnly = false } = props;
    const [dialogShow, dialogComponent] = useMuiContentDialog();
    const { t } = useBaseAppContext();
    const closeButtons = [{
        value: false,
        text: t('common.close'),
        componentProps: { variant: 'outlined' }
    }];
    const handleOpen = (event:any) => {
        event.stopPropagation();
        dialogShow(
            title,
            <>
                <Comments
                    resourceName={resourceName}
                    id={entity?.id}
                    resourceReference={resourceReference}
                    readOnly={readOnly}
                    i18nKeys={i18nKeys}/>
            </>,
            closeButtons,
            { maxWidth: 'md', fullWidth: true }).
            catch(() => {
                onClose?.();
            });
    }
    return <>
        <IconButton title={t('page.comment.label')} aria-label="forum" color="inherit" onClick={handleOpen}>
            <Badge badgeContent={entity?.numComentaris} color="primary" showZero>
                <Icon sx={iconStyle}>forum</Icon>
            </Badge>
        </IconButton>
        {dialogComponent}
    </>;
}

export const ExpedientComment = (props: any) => {
    const { entity } = props;
    const { t } = useTranslation();
    return <CommentDialog
        {...props}
        title={`${t('page.comment.expedient')}: ${entity?.nom}`}
        resourceName={'expedientComentariResource'}
        resourceReference={'expedient'}
        i18nKeys={{
            createSuccess: 'page.expedient.action.comment.ok',
        }}
    />
}

export const TascaComment = (props: any) => {
    const { entity } = props;
    const { t } = useTranslation();
    return <CommentDialog
        {...props}
        title={`${t('page.comment.tasca')}: ${entity?.metaExpedientTascaDescription}`}
        resourceName={'expedientTascaComentariResource'}
        resourceReference={'expedientTasca'}
        i18nKeys={{
            createSuccess: 'page.tasca.action.comment.ok',
        }}
    />
}

export const MetaExpedientComment = (props: any) => {
    const { entity } = props;
    const { t } = useTranslation();
    return <CommentDialog
        {...props}
        title={`${t('page.comment.metaExpedient')}: ${entity?.nom}`}
        resourceName={'metaExpedientComentariResource'}
        resourceReference={'metaExpedient'}
        i18nKeys={{
            createSuccess: 'page.metaExpedient.action.comment.ok',
        }}
    />
}