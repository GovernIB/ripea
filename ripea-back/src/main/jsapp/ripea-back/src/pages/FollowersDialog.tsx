import { useEffect, useState } from 'react';
import { Badge, Box, Icon, IconButton, Typography } from '@mui/material';
import { useResourceApiService, MuiDialog } from 'reactlib';
import { useTranslation } from 'react-i18next';

const followerStyle = {
    borderRadius: 2,
    px: 2,
    py: 1,
    bgcolor: (theme: any) => theme.palette.mode === 'dark' ? 'background.paper' : '#e9e9e9',
    color: 'text.primary',
};

export const FollowersDialog = (props: any) => {
    const { entity } = props;
    const { t } = useTranslation();

    const { isReady: appApiIsReady, getOne: apiGetOne } = useResourceApiService('expedientResource');

    const [followers, setFollowers] = useState<any[]>([]);
    const [open, setOpen] = useState(false);

    useEffect(() => {
        if (open && appApiIsReady) {
            apiGetOne(entity?.id, { perspectives: ['FOLLOWERS'] }).then((app) => {
                setFollowers(app?.seguidors);
            });
        }
    }, [open]);

    const handleOpen = (event: any) => {
        event.stopPropagation();
        setOpen(true);
    };

    const handleClose = (reason?: string) => {
        if (reason !== 'backdropClick') {
            setOpen(false);
        }
    };

    return (
        <>
            <IconButton title={t('page.expedient.modal.seguidors.label')} aria-label="forum" color={'inherit'} onClick={handleOpen}>
                <Badge badgeContent={entity?.numSeguidors} color="primary" showZero>
                    <Icon>people</Icon>
                </Badge>
            </IconButton>

            <MuiDialog
                open={open}
                closeCallback={handleClose}
                title={t('page.expedient.modal.seguidors.title') + ': ' + entity?.nom}
                key={entity?.id}
                componentProps={{ fullWidth: true, maxWidth: 'sm' }}
                buttons={[
                    {
                        value: 'close',
                        text: t('common.close'),
                        componentProps: { variant: 'outlined' },
                    },
                ]}
                buttonCallback={(value: any): void => {
                    if (value == 'close') {
                        handleClose();
                    }
                }}
            >
                {followers.length ? (
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1, mt: 2 }}>
                        {followers?.map((follower: any) => (
                            <Typography key={follower?.id} sx={followerStyle}>
                                {follower?.description}
                            </Typography>
                        ))}
                    </Box>
                ) : (
                    <Typography sx={{ pt: 2 }}>{t('page.expedient.modal.seguidors.noResults')}</Typography>
                )}
            </MuiDialog>
        </>
    );
};
